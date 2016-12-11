/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseActivity {

  private final static int JUMP_TO_NEXT = 1;

  private MyHandler handler = new MyHandler(SplashActivity.this);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    handler.sendEmptyMessageDelayed(JUMP_TO_NEXT, 500);
  }

  private static class MyHandler extends Handler {
    private WeakReference<BaseActivity> weakReference;

    MyHandler(BaseActivity activity) {
      this.weakReference = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case JUMP_TO_NEXT:
          if (weakReference.get() == null) {
            return;
          }
          BaseActivity activity = weakReference.get();
          UserPrefs userPrefs = new UserPrefs(activity);
          if (!TextUtils.isEmpty(userPrefs.getAuthToken())
              && (userPrefs.getUser() != null)) {
            activity.startActivity(MainActivity.createIntent(activity));
          } else {
            activity.startActivity(new Intent(activity, SignupActivity.class));
          }
          activity.finish();
      }
      super.handleMessage(msg);
    }
  }
}
