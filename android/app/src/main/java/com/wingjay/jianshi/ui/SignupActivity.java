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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.PatternsCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wingjay.jianshi.BuildConfig;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.log.Blaster;
import com.wingjay.jianshi.log.LoggingData;
import com.wingjay.jianshi.manager.UserManager;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Signup Activity.
 */
public class SignupActivity extends BaseActivity {

  @InjectView(R.id.email)
  EditText userEmail;

  @InjectView(R.id.password)
  EditText userPassword;

  @InjectView(R.id.skip)
  TextView skip;

  @Inject
  UserService userService;

  @Inject
  UserManager userManager;

  @Inject
  UserPrefs userPrefs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    JianShiApplication.getAppComponent().inject(this);

    if (userPrefs.getAuthToken() != null) {
      startActivity(MainActivity.createIntent(this));
      finish();
      return;
    }

    if (BuildConfig.DEBUG) {
      skip.setVisibility(View.VISIBLE);
    } else {
      skip.setVisibility(View.GONE);
    }
    Blaster.log(LoggingData.PAGE_IMP_SIGN_UP);
  }

  @OnClick(R.id.signup)
  void signUp() {
    Blaster.log(LoggingData.BTN_CLK_SIGN_UP);
    if (!checkEmailPwdNonNull()) {
      return;
    }

    if (getPassword().length() < 6) {
      userPassword.setError(getString(R.string.password_length_must_bigger_than_6));
      return;
    }

    userManager.signup(SignupActivity.this,
        getEmailText(),
        getPassword());
  }

  @OnClick(R.id.login)
  void login() {
    Blaster.log(LoggingData.BTN_CLK_LOGIN);
    if (!checkEmailPwdNonNull()) {
      return;
    }
    userManager.login(SignupActivity.this,
        getEmailText(),
        getPassword());
  }

  private boolean checkEmailPwdNonNull() {
    if (TextUtils.isEmpty(getEmailText())) {
      userEmail.setError(getString(R.string.email_should_not_be_null));
      return false;
    }
    if (!PatternsCompat.EMAIL_ADDRESS.matcher(getEmailText()).matches()) {
      userEmail.setError(getString(R.string.wrong_email_format));
      return false;
    }
    if (TextUtils.isEmpty(getPassword())) {
      userPassword.setError(getString(R.string.password_should_not_be_null));
      return false;
    }

    return true;
  }

  private String getEmailText() {
    return userEmail.getText().toString().trim();
  }

  private String getPassword() {
    return userPassword.getText().toString();
  }

  @OnClick(R.id.skip)
  void skip() {
    startActivity(MainActivity.createIntent(SignupActivity.this));
  }

  public static Intent createIntent(Context context) {
    Intent intent = new Intent(context, SignupActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_NO_ANIMATION);
    return intent;
  }
}
