package com.wingjay.jianshi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.font.CustomizeEditText;
import com.wingjay.jianshi.util.RxUtil;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Signup Activity.
 */
public class SignupActivity extends BaseActivity {

  @InjectView(R.id.email)
  CustomizeEditText userEmail;

  @InjectView(R.id.password)
  CustomizeEditText userPasswordEditText;

  @Inject
  UserService userService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    JianShiApplication.getAppComponent().inject(this);
  }

  @OnClick(R.id.signup_button)
  void signUp() {
    if (!checkEmailPwdNonNull()) {
      return;
    }

    userService.signup(userEmail.getText().toString(), userPasswordEditText.getText().toString())
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .subscribe(new Action1<JsonDataResponse<User>>() {
          @Override
          public void call(JsonDataResponse<User> userJsonDataResponse) {
            if (userJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
              User user = userJsonDataResponse.getData();
              if (user == null || user.getId() <= 0) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              } else if (TextUtils.isEmpty(user.getEncryptedToken())) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              }

              UserPrefs userPrefs = new UserPrefs(SignupActivity.this);
              userPrefs.setAuthToken(user.getEncryptedToken());
              userPrefs.setUser(user);
            } else {
              makeToast(userJsonDataResponse.getMsg());
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            Timber.e(e, "signup failure");
            makeToast(getString(R.string.signup_failure));
          }
        });
  }

  @OnClick(R.id.login_button)
  void login() {
    if (!checkEmailPwdNonNull()) {
      return;
    }

    userService.login(userEmail.getText().toString(), userPasswordEditText.getText().toString())
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .subscribe(new Action1<JsonDataResponse<User>>() {
          @Override
          public void call(JsonDataResponse<User> userJsonDataResponse) {
            if (userJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
              User user = userJsonDataResponse.getData();
              if (user == null || user.getId() <= 0) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              } else if (TextUtils.isEmpty(user.getEncryptedToken())) {
                throw new RuntimeException(userJsonDataResponse.getMsg());
              }

              UserPrefs userPrefs = new UserPrefs(SignupActivity.this);
              userPrefs.setAuthToken(user.getEncryptedToken());
              userPrefs.setUser(user);

              startActivity(MainActivity.createIntent(SignupActivity.this));
            } else {
              makeToast(userJsonDataResponse.getMsg());
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            Timber.e(e, "login failure");
            makeToast(getString(R.string.login_failure));
          }
        });
  }

  private boolean checkEmailPwdNonNull() {
    if (TextUtils.isEmpty(userEmail.getText())) {
      userEmail.setError(getString(R.string.email_should_not_be_null));
      return false;
    }
    if (TextUtils.isEmpty(userPasswordEditText.getText())) {
      userPasswordEditText.setError(getString(R.string.password_should_not_be_null));
      return false;
    }

    return true;
  }

  @OnClick(R.id.skip_button)
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
