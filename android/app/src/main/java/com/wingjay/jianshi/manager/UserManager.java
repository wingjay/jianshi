package com.wingjay.jianshi.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.MainActivity;
import com.wingjay.jianshi.util.RxUtil;

import javax.inject.Inject;

import dagger.Lazy;
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
  UserManager() {}

  public void login(final Context context, @NonNull String email, @NonNull String password) {
    userService.login(email, password)
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .subscribe(new Action1<JsonDataResponse<User>>() {
          @Override
          public void call(JsonDataResponse<User> userJsonDataResponse) {
            if (userJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
              User user = userJsonDataResponse.getData();
              if (user == null || user.getId() <= 0) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              } else if (TextUtils.isEmpty(user.getEncryptedToken())) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              }
              userPrefsLazy.get().setAuthToken(user.getEncryptedToken());
              userPrefsLazy.get().setUser(user);

              context.startActivity(MainActivity.createIntent(context));
            } else {
              Toast.makeText(context, userJsonDataResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            Timber.e(e, "login failure");
            Toast.makeText(context, context.getString(R.string.login_failure),
                Toast.LENGTH_SHORT).show();
          }
        });
  }

  public void signup(final Context context, @NonNull String email, @NonNull String password) {
    userService.signup(email, password)
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .subscribe(new Action1<JsonDataResponse<User>>() {
          @Override
          public void call(JsonDataResponse<User> userJsonDataResponse) {
            if (userJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
              User user = userJsonDataResponse.getData();
              if (user == null || user.getId() <= 0) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              } else if (TextUtils.isEmpty(user.getEncryptedToken())) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              }

              userPrefsLazy.get().setAuthToken(user.getEncryptedToken());
              userPrefsLazy.get().setUser(user);
            } else {
              Toast.makeText(context, userJsonDataResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            Timber.e(e, "signup failure");
            Toast.makeText(context, context.getString(R.string.signup_failure),
                Toast.LENGTH_SHORT).show();
          }
        });
  }

}
