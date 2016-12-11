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

import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.bean.VersionUpgrade;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Version upgrade.
 */
public class UpgradeManager {

  @Inject
  UserService userService;

  @Inject
  UserPrefs userPrefs;

  @Inject
  UpgradeManager() {}

  public Observable<VersionUpgrade> checkUpgradeObservable() {
    return Observable.defer(new Func0<Observable<VersionUpgrade>>() {
      @Override
      public Observable<VersionUpgrade> call() {
        return userService.checkUpgrade()
            .doOnError(new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {
                Timber.e(throwable, "check upgrade failure");
              }
            })
            .flatMap(new Func1<JsonDataResponse<VersionUpgrade>, Observable<VersionUpgrade>>() {
              @Override
              public Observable<VersionUpgrade> call(JsonDataResponse<VersionUpgrade> response) {
                if (response.getRc() == Constants.ServerResultCode.RESULT_OK) {
                  userPrefs.setVersionUpgrade(response.getData());
                  return Observable.just(response.getData());
                }
                return Observable.just(null);
              }
            });
      }
    }).subscribeOn(Schedulers.io());
  }
}
