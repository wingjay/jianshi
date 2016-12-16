/*
 * Created by wingjay on 11/16/16 3:32 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.db.model.EventLog;
import com.wingjay.jianshi.db.model.PushData;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.sync.SyncManager;
import com.wingjay.jianshi.sync.SyncService;
import com.wingjay.jianshi.ui.MainActivity;
import com.wingjay.jianshi.ui.SignupActivity;
import com.wingjay.jianshi.util.RxUtil;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * User Management.
 */

public class UserManager {

  @Inject
  Lazy<UserPrefs> userPrefsLazy;

  @Inject
  UserService userService;

  @Inject
  UserPrefs userPrefs;

  @Inject
  UserManager() {}

  public void login(final Context context, @NonNull String email, @NonNull String password) {
    final ProgressDialog dialog = ProgressDialog.show(context, context.getString(R.string.logining), "");
    userService.login(email, password)
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .doOnTerminate(new Action0() {
          @Override
          public void call() {
            dialog.dismiss();
          }
        })
        .subscribe(new Action1<JsonDataResponse<User>>() {
          @Override
          public void call(JsonDataResponse<User> userJsonDataResponse) {
            if (userJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
              User user = userJsonDataResponse.getData();
              if (user == null || user.getId() <= 0 || TextUtils.isEmpty(user.getEncryptedToken())) {
                Toast.makeText(context, context.getString(R.string.login_failure),
                    Toast.LENGTH_SHORT).show();
                return;
              }
              userPrefsLazy.get().setAuthToken(user.getEncryptedToken());
              userPrefsLazy.get().setUser(user);

              context.startActivity(MainActivity.createIntent(context));
            } else {
              Timber.e("login failure msg: %s", userJsonDataResponse.getMsg());
              Toast.makeText(context, userJsonDataResponse.getMsg(),
                  Toast.LENGTH_SHORT).show();
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            Timber.e(e, "login failure");
            Toast.makeText(context, context.getString(R.string.network_error),
                Toast.LENGTH_SHORT).show();
          }
        });
  }

  public void signup(final Context context, @NonNull String email, @NonNull String password) {
    final ProgressDialog dialog = ProgressDialog.show(context, context.getString(R.string.signuping), "");
    userService.signup(email, password)
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .doOnTerminate(new Action0() {
          @Override
          public void call() {
            dialog.dismiss();
          }
        })
        .subscribe(new Action1<JsonDataResponse<User>>() {
          @Override
          public void call(JsonDataResponse<User> userJsonDataResponse) {
            if (userJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
              User user = userJsonDataResponse.getData();
              if (user == null || user.getId() <= 0 || TextUtils.isEmpty(user.getEncryptedToken())) {
                Toast.makeText(context, context.getString(R.string.signup_failure),
                    Toast.LENGTH_SHORT).show();
                return;
              }

              userPrefsLazy.get().setAuthToken(user.getEncryptedToken());
              userPrefsLazy.get().setUser(user);
              context.startActivity(MainActivity.createIntent(context));
            } else {
              Timber.e("signup failure msg: %s", userJsonDataResponse.getMsg());
              Toast.makeText(context, userJsonDataResponse.getMsg(),
                  Toast.LENGTH_SHORT).show();
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            Timber.e(e, "signup failure");
            Toast.makeText(context, context.getString(R.string.network_error),
                Toast.LENGTH_SHORT).show();
          }
        });
  }

  public void logoutByInvalidToken(final @NonNull Context context) {
    doLogout(context);
  }

  public void logout(final @NonNull Context context) {
    final ProgressDialog dialog = ProgressDialog.show(context,
        context.getString(R.string.logout_ing), "");
    if (SQLite.select().from(PushData.class).queryList().size() > 0) {
      SyncService.syncImmediately(context, new SyncManager.SyncResultListener() {
        @Override
        public void onSuccess() {
          dialog.dismiss();
          doLogout(context);
        }

        @Override
        public void onFailure() {
          dialog.dismiss();
          AlertDialog.Builder builder = new AlertDialog.Builder(context)
              .setTitle(R.string.logout_warning_with_data_not_sync)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  doLogout(context);
                }
              })
              .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
                }
              });
          builder.create().show();
        }
      });
    } else {
      doLogout(context);
    }
  }

  private void doLogout(final @NonNull Context context) {
    userPrefs.clear();
    SQLite.delete().from(PushData.class).execute();
    SQLite.delete().from(EventLog.class).execute();
    SQLite.delete().from(Diary.class).execute();
    context.startActivity(SignupActivity.createIntent(context));
  }
}
