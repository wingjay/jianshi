package com.wingjay.jianshi.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.theme.BackgroundColorHelper;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

  //todo(wingjay) Add RxLifecycle function
  protected boolean isVisible = false;

  protected View containerView;
  protected String TAG = getClass().getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "onCreate");
  }

  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.inject(this);

    containerView = findViewById(R.id.layout_container);
    setContainerBgColorFromPrefs();
  }

  protected void setContainerBgColorFromPrefs() {
    if (containerView != null) {
      containerView.setBackgroundResource(BackgroundColorHelper.getBackgroundColorResFromPrefs(this));
    }
  }
  protected void setContainerBgColor(int colorRes) {
    if (containerView != null) {
      containerView.setBackgroundResource(colorRes);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart");
  }

  @Override
  protected void onResume() {
    super.onResume();
    isVisible = true;
    Log.i(TAG, "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    isVisible = false;
    Log.i(TAG, "onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i(TAG, "onStop");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestory");
  }

  public boolean isUISafe() {
    return isVisible;
  }

  protected void makeToask(@NonNull String content) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
  }
}
