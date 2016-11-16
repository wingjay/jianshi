/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.prefs.UserPrefs;

import javax.annotation.Nullable;
import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by panl on 2016/10/24.
 * contact panlei106@gmail.com
 */

public class SyncService extends IntentService {

  private static SyncManager.SyncResultListener mSyncResultListener;
  @Inject
  SyncManager syncManager;
  @Inject
  UserPrefs userPrefs;

  public SyncService() {
    super("SyncService");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    JianShiApplication.getAppComponent().inject(this);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Timber.d("SyncService name: %s", Thread.currentThread().getName());
    if (!TextUtils.isEmpty(userPrefs.getAuthToken()) && userPrefs.getUser() != null) {
      syncManager.sync(mSyncResultListener);
      syncManager.syncLog();
    }
  }

  public static void syncImmediately(Context context) {
    syncImmediately(context, null);
  }

  public static void syncImmediately(Context context, @Nullable SyncManager.SyncResultListener syncResultListener) {
    mSyncResultListener = syncResultListener;
    context.startService(new Intent(context, SyncService.class));
  }
}
