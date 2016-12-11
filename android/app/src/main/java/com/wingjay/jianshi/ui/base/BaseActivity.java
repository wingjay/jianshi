/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.prefs.UserPrefs;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

  //todo(wingjay) Add RxLifecycle function
  protected boolean isVisible = false;

  protected View containerView;
  protected String TAG = getClass().getSimpleName() + ": %s";

  private boolean isNeedRegister = false;

  protected void setNeedRegister() {
    this.isNeedRegister = true;
  }

  @Inject
  UserPrefs userPrefs;

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    JianShiApplication.getAppComponent().inject(this);
    Timber.d(TAG, "onCreate");
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.inject(this);

    containerView = findViewById(R.id.layout_container);
  }

  protected void setContainerBgColorFromPrefs() {
    if (containerView != null) {
      containerView.setBackgroundResource(userPrefs.getBackgroundColor());
    }
  }

  protected void setContainerBgColor(int colorRes) {
    if (containerView != null) {
      containerView.setBackgroundResource(colorRes);
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Timber.d(TAG, "onRestart");
  }

  @Override
  protected void onStart() {
    super.onStart();
    Timber.d(TAG, "onStart");
    setContainerBgColorFromPrefs();

    if (isNeedRegister) {
      EventBus.getDefault().register(this);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    isVisible = true;
    Timber.d(TAG, "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    isVisible = false;
    Timber.d(TAG, "onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Timber.d(TAG, "onStop");
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Timber.d(TAG, "onDestroy");
  }

  public boolean isUISafe() {
    return isVisible;
  }

  protected void makeToast(@NonNull String content) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
  }
}
