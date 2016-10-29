package com.wingjay.jianshi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.PatternsCompat;
import android.text.TextUtils;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.manager.UserManager;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.TextPointView;
import com.wingjay.jianshi.ui.widget.font.CustomizeEditText;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Signup Activity.
 */
public class SignupActivity extends BaseActivity {

  @InjectView(R.id.email)
  CustomizeEditText userEmail;

  @InjectView(R.id.password)
  CustomizeEditText userPassword;

  @Inject
  UserService userService;

  @Inject
  UserManager userManager;

  @InjectView(R.id.text_point)
  TextPointView textPointView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    JianShiApplication.getAppComponent().inject(this);
  }

  @OnClick(R.id.signup)
  void signUp() {
    if (!checkEmailPwdNonNull()) {
      return;
    }
    userManager.signup(SignupActivity.this,
        getEmailText(),
        getPassword());
  }

  @OnClick(R.id.login)
  void login() {
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
    return userEmail.getText().toString();
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
