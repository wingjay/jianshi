package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;

public class SplashActivity extends BaseActivity {

  private final static int JUMP_TO_NEXT = 1;

  private MyHandler handler = new MyHandler(SplashActivity.this);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    handler.sendEmptyMessageDelayed(JUMP_TO_NEXT, 2000);
  }

  private static class MyHandler extends Handler {
    private static BaseActivity context;

    public MyHandler(BaseActivity activity) {
      context = activity;
    }

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case JUMP_TO_NEXT:
          UserPrefs userPrefs = new UserPrefs(context);
          if (!TextUtils.isEmpty(userPrefs.getAuthToken())
              && (userPrefs.getUser() != null)) {
            context.startActivity(MainActivity.createIntent(context));
          } else {
            context.startActivity(new Intent(context, SignupActivity.class));
          }
          context.finish();
      }
      super.handleMessage(msg);
    }
  }
}
