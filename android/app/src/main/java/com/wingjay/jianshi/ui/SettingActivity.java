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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wingjay.jianshi.BuildConfig;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.VersionUpgrade;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.log.Blaster;
import com.wingjay.jianshi.log.LoggingData;
import com.wingjay.jianshi.manager.PayDeveloperManager;
import com.wingjay.jianshi.manager.UpgradeManager;
import com.wingjay.jianshi.manager.UserManager;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.BgColorPickDialogFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

public class SettingActivity extends BaseActivity {

  @InjectView(R.id.vertical_write)
  SwitchCompat verticalWrite;

  @InjectView(R.id.home_image_poem_switch)
  SwitchCompat homeImagePoemSwitch;

  @InjectView(R.id.send_feedback)
  View sendFeedBack;

  @InjectView(R.id.customize_bg_color)
  View customizeBgColor;

  @InjectView(R.id.version_upgrade_title)
  TextView versionUgradeTitle;

  @InjectView(R.id.version_upgrade_warning)
  View versionUpgradeWarning;

  @Inject
  UserPrefs userPrefs;

  @Inject
  UserManager userManager;

  @Inject
  UpgradeManager upgradeManager;

  @Inject
  PayDeveloperManager payDeveloperManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    JianShiApplication.getAppComponent().inject(this);
    setContentView(R.layout.activity_setting);
    verticalWrite.setChecked(userPrefs.getVerticalWrite());
    homeImagePoemSwitch.setChecked(userPrefs.getHomeImagePoemSetting());
    Blaster.log(LoggingData.PAGE_IMP_SETTING);

    SpannableStringBuilder builder = new SpannableStringBuilder();
    builder.append(getString(R.string.version_upgrade));
    builder.append(". ");
    int length = builder.length();
    builder.append(getString(R.string.current_version_code));
    builder.append(BuildConfig.VERSION_NAME);
    builder.setSpan(new RelativeSizeSpan(10f), length, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    versionUgradeTitle.setText(builder.toString());
    setupUpgradeWarning();
  }

  public void setupUpgradeWarning() {
    VersionUpgrade versionUpgrade = userPrefs.getVersionUpgrade();
    if (versionUpgrade == null) {
      versionUpgradeWarning.setVisibility(View.GONE);
    } else {
      versionUpgradeWarning.setVisibility(View.VISIBLE);
    }
  }

  @OnCheckedChanged(R.id.vertical_write)
  void chooseVerticalWrite() {
    userPrefs.setVerticalWrite(verticalWrite.isChecked());
    if (verticalWrite.isChecked()) {
      Blaster.log(LoggingData.BTN_CLK_TURN_ON_VERTICAL_TEXT);
    } else {
      Blaster.log(LoggingData.BTN_CLK_TURN_OFF_VERTICAL_TEXT);
    }
  }

  @OnCheckedChanged(R.id.home_image_poem_switch)
  void checkHomeImagePoem() {
    userPrefs.setHomeImagePoem(homeImagePoemSwitch.isChecked());
    if (homeImagePoemSwitch.isChecked()) {
      Blaster.log(LoggingData.BTN_CLK_SHOW_HOME_IMAGE);
    } else {
      Blaster.log(LoggingData.BTN_CLK_TURN_OFF_HOME_IMAGE);
    }
  }

  @OnClick(R.id.version_upgrade)
  void checkVersionUpgrade() {
    final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.checking_upgrade), "");
    upgradeManager.checkUpgradeObservable()
        .doOnTerminate(new Action0() {
          @Override
          public void call() {
            progressDialog.dismiss();
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<VersionUpgrade>() {
          @Override
          public void call(final VersionUpgrade versionUpgrade) {
            if (!isUISafe()) {
              return;
            }

            if (versionUpgrade == null) {
              makeToast(getString(R.string.current_newest_verion));
              return;
            }

            String upgradeInfo = getString(R.string.upgrade_info,
                versionUpgrade.getVersionName(),
                versionUpgrade.getDescription());
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

            builder.setTitle(R.string.upgrade_title)
                .setMessage(upgradeInfo)
                .setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(versionUpgrade.getDownloadLink()));
                    startActivity(browserIntent);
                  }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                });
            builder.create().show();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Timber.e(throwable, "upgrade failure");
          }
        });
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
            userPrefs.setBackgroundColor(newColorRes);
            SettingActivity.this.setContainerBgColor(newColorRes);
            setResult(RESULT_OK);
          }
        });
    bgColorPickDialogFragment.show(getSupportFragmentManager(), null);
  }

  @OnClick(R.id.pay_developer_dialog)
  void payDeveloperDialog() {
    if (userPrefs.getLocalPayDeveloperDialogData() != null) {
      payDeveloperManager.displayPayDeveloperDialog(SettingActivity.this, userPrefs.getLocalPayDeveloperDialogData());
    } else {
      payDeveloperManager.updateLocalPayDeveloperDialogInfo();
    }
  }

  @OnClick(R.id.logout)
  void logout() {
    Blaster.log(LoggingData.BTN_CLK_LOGOUT);
    userManager.logout(this);
  }
}
