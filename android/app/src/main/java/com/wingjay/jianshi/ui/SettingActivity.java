package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.manager.UserManager;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.BgColorPickDialogFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

  @InjectView(R.id.vertical_write)
  SwitchCompat verticalWrite;

  @InjectView(R.id.home_image_poem_switch)
  SwitchCompat homeImagePoemSwitch;

  @InjectView(R.id.send_feedback)
  View sendFeedBack;

  @InjectView(R.id.customize_bg_color)
  View customizeBgColor;

  @Inject
  UserPrefs userPrefs;

  @Inject
  UserManager userManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    JianShiApplication.getAppComponent().inject(this);
    setContentView(R.layout.activity_setting);
    verticalWrite.setChecked(userPrefs.getVerticalWrite());
    homeImagePoemSwitch.setChecked(userPrefs.getHomeImagePoem());
  }

  @OnCheckedChanged(R.id.vertical_write)
  void chooseVerticalWrite() {
    userPrefs.setVerticalWrite(verticalWrite.isChecked());
  }

  @OnCheckedChanged(R.id.home_image_poem_switch)
  void checkHomeImagePoem() {
    userPrefs.setHomeImagePoem(homeImagePoemSwitch.isChecked());
  }

  @OnClick(R.id.send_feedback)
  void sendFeedback() {
    Intent i = new Intent(Intent.ACTION_SENDTO);
    i.setType("*/*");
    i.setData(Uri.parse("mailto:"));
    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"yinjiesh@126.com"});
    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_email_subject));
    i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.feedback_email_prefix));
    try {
      if (i.resolveActivity(getPackageManager()) != null) {
        startActivity(Intent.createChooser(i, getResources().getString(R.string.send_email)));
      }
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, getResources().getString(R.string.email_client_no_found),
          Toast.LENGTH_SHORT).show();
    }
  }

  @OnClick(R.id.customize_bg_color)
  void chooseBgColor() {
    BgColorPickDialogFragment bgColorPickDialogFragment = new BgColorPickDialogFragment();
    bgColorPickDialogFragment.setOnBackgroundColorChangedListener(
        new BgColorPickDialogFragment.OnBackgroundColorChangedListener() {
          @Override
          public void onBackgroundColorChanged(int newColorRes) {
            SettingActivity.this.setContainerBgColor(newColorRes);
            userPrefs.setBackgroundColor(newColorRes);
            setResult(RESULT_OK);
          }
        });
    bgColorPickDialogFragment.show(getSupportFragmentManager(), null);
  }

  @OnClick(R.id.logout)
  void logout() {
    userManager.logout(this);
  }
}
