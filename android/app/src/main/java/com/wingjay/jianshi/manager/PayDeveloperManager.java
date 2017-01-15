/*
 * Created by wingjay on 1/15/17 10:47 PM
 * Copyright (c) 2017.  All rights reserved.
 *
 * Last modified 1/15/17 10:47 PM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.manager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.PayDeveloperDialogData;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Jay on 1/15/17.
 */

public class PayDeveloperManager {

  @Inject
  UserService userService;

  @Inject
  UserPrefs userPrefs;

  @Inject
  PayDeveloperManager() {}

  public Observable<PayDeveloperDialogData> getPayDeveloperDialogData() {
    return Observable.defer(new Func0<Observable<PayDeveloperDialogData>>() {
      @Override
      public Observable<PayDeveloperDialogData> call() {
        return userService.payDeveloper()
            .flatMap(new Func1<JsonDataResponse<PayDeveloperDialogData>, Observable<PayDeveloperDialogData>>() {
              @Override
              public Observable<PayDeveloperDialogData> call(JsonDataResponse<PayDeveloperDialogData> response) {
                if (response.getRc() == Constants.ServerResultCode.RESULT_OK) {
                  return Observable.just(response.getData());
                } else {
                  return Observable.just(null);
                }
              }
            })
            .subscribeOn(Schedulers.io());
      }
    });
  }

  public void updateLocalPayDeveloperDialogInfo() {
    getPayDeveloperDialogData().subscribe(new Action1<PayDeveloperDialogData>() {
      @Override
      public void call(PayDeveloperDialogData payDeveloperDialogData) {
        if (payDeveloperDialogData != null) {
          userPrefs.savePayDeveloperDialogData(payDeveloperDialogData);
        }
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {

      }
    });
  }

  public void displayPayDeveloperDialog(final Context context, @NonNull final PayDeveloperDialogData payDeveloperDialogData) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    builder.setTitle(payDeveloperDialogData.getTitle())
        .setMessage(payDeveloperDialogData.getMessage())
        .setPositiveButton(R.string.copy_alipay_account, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            ClipData clip = ClipData.newPlainText("aliPay", payDeveloperDialogData.getAliPayAccount());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, context.getString(R.string.copy_successfully), Toast.LENGTH_LONG).show();
          }
        })
        .setNegativeButton(R.string.copy_wechat_account, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            ClipData clip = ClipData.newPlainText("wechatPay", payDeveloperDialogData.getWechatPayAccount());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, context.getString(R.string.copy_successfully), Toast.LENGTH_LONG).show();
          }
        })
        .setNeutralButton(R.string.refuse_pay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {}
        });
    builder.create().show();
    userPrefs.setPayDeveloperDialogLastShowTimeSeconds();
  }

}
