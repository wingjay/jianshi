package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    private final static int JUMP_TO_MAIN = 1;

    private MyHandler handler = new MyHandler(SplashActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.sendEmptyMessageDelayed(JUMP_TO_MAIN, 1000);

    }

    private static class MyHandler extends Handler {
        private static BaseActivity context;

        public MyHandler(BaseActivity activity) {
            context = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case JUMP_TO_MAIN:
                    context.startActivity(new Intent(context, DateChooseActivity.class));
                    context.finish();
            }
            super.handleMessage(msg);
        }
    }

}
