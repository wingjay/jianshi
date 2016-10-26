package com.wingjay.jianshi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.util.RxUtil;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Signup Activity.
 */
public class SignupActivity extends BaseActivity {

  @InjectView(R.id.user_name)
  EditText userNameEditText;

  @InjectView(R.id.user_password)
  EditText userPasswordEditText;

  @InjectView(R.id.signup_button)
  Button signupButton;

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
    if (TextUtils.isEmpty(userNameEditText.getText())
        || TextUtils.isEmpty(userPasswordEditText.getText())) {
      makeToast("Name & password shouldn't be null");
      return;
    }
    userService.signup(userNameEditText.getText().toString(), userPasswordEditText.getText().toString())
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .subscribe(new Subscriber<JsonDataResponse<User>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {
            makeToast("Please check your network status");
          }

          @Override
          public void onNext(JsonDataResponse<User> userJsonDataResponse) {
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
              makeToast("Welcome to JianShi, ENJOY!");
            } else {
              makeToast(userJsonDataResponse.getMsg());
            }
          }
        });
  }

  @OnClick(R.id.login_button)
  void login() {
    userService.login(userNameEditText.getText().toString(), userPasswordEditText.getText().toString())
        .compose(RxUtil.<JsonDataResponse<User>>normalSchedulers())
        .subscribe(new Subscriber<JsonDataResponse<User>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {
            makeToast("Please check your network status");
          }

          @Override
          public void onNext(JsonDataResponse<User> userJsonDataResponse) {
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
        });
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
