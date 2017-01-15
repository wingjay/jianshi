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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.ImagePoem;
import com.wingjay.jianshi.bean.PayDeveloperDialogData;
import com.wingjay.jianshi.bean.VersionUpgrade;
import com.wingjay.jianshi.events.InvalidUserTokenEvent;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.log.Blaster;
import com.wingjay.jianshi.log.LoggingData;
import com.wingjay.jianshi.manager.FullDateManager;
import com.wingjay.jianshi.manager.PayDeveloperManager;
import com.wingjay.jianshi.manager.UpgradeManager;
import com.wingjay.jianshi.manager.UserManager;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.sync.SyncManager;
import com.wingjay.jianshi.sync.SyncService;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.DayChooser;
import com.wingjay.jianshi.ui.widget.TextPointView;
import com.wingjay.jianshi.ui.widget.ThreeLinePoemView;
import com.wingjay.jianshi.ui.widget.VerticalTextView;
import com.wingjay.jianshi.util.RxUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

  private final static String YEAR = "year";
  private final static String MONTH = "month";
  private final static String DAY = "day";

  @InjectView(R.id.background_image)
  ImageView backgroundImage;

  @InjectView(R.id.year)
  VerticalTextView yearTextView;

  @InjectView(R.id.month)
  VerticalTextView monthTextView;

  @InjectView(R.id.day)
  VerticalTextView dayTextView;

  @InjectView(R.id.writer)
  TextPointView writerView;

  @InjectView(R.id.reader)
  TextPointView readerView;

  @InjectView(R.id.day_chooser)
  DayChooser dayChooser;

  @InjectView(R.id.three_line_poem)
  ThreeLinePoemView threeLinePoemView;

  @Inject
  SyncManager syncManager;

  @Inject
  UserService userService;

  @Inject
  UserManager userManager;

  @Inject
  UpgradeManager upgradeManager;

  @Inject
  UserPrefs userPrefs;

  @Inject
  PayDeveloperManager payDeveloperManager;

  private volatile int year, month, day;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    JianShiApplication.getAppComponent().inject(MainActivity.this);

    setContentView(R.layout.activity_main);
    setNeedRegister();

    if (savedInstanceState != null) {
      year = savedInstanceState.getInt(YEAR);
      month = savedInstanceState.getInt(MONTH);
      day = savedInstanceState.getInt(DAY);
    } else {
      setTodayAsFullDate();
      tryNotifyUpgrade();

      showPayDialog();
    }
    updateFullDate();

    writerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Blaster.log(LoggingData.BTN_CLK_HOME_WRITE);
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);
      }
    });

    readerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Blaster.log(LoggingData.BTN_CLK_HOME_VIEW);
        startActivity(new Intent(MainActivity.this, DiaryListActivity.class));
      }
    });

    Blaster.log(LoggingData.PAGE_IMP_HOME);
    SyncService.syncImmediately(this);
  }

  private void showPayDialog() {
    payDeveloperManager.getPayDeveloperDialogData()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<PayDeveloperDialogData>() {
          @Override
          public void call(final PayDeveloperDialogData payDeveloperDialogData) {
            if (payDeveloperDialogData != null
                && userPrefs.ableToShowPayDeveloperDialog(payDeveloperDialogData.getTimeGapSeconds())) {
              payDeveloperManager.displayPayDeveloperDialog(MainActivity.this, payDeveloperDialogData);
              userPrefs.savePayDeveloperDialogData(payDeveloperDialogData);
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {

          }
        });
  }

  private void tryNotifyUpgrade() {
    upgradeManager.checkUpgradeObservable()
        .compose(RxUtil.<VersionUpgrade>normalSchedulers())
        .subscribe(new Action1<VersionUpgrade>() {
      @Override
      public void call(final VersionUpgrade versionUpgrade) {
        if (!isUISafe()) {
          return;
        }
        if (versionUpgrade != null && !userPrefs.isNewVersionNotified(versionUpgrade)) {
          String upgradeInfo = getString(R.string.upgrade_info,
              versionUpgrade.getVersionName(),
              versionUpgrade.getDescription());
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setTitle(R.string.upgrade_title)
              .setMessage(upgradeInfo)
              .setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                      Uri.parse(versionUpgrade.getDownloadLink()));
                  startActivity(browserIntent);
                  userPrefs.addNotifiedNewVersionName(versionUpgrade);
                }
              })
              .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  makeToast(getString(R.string.go_to_setting_for_upgrading));
                  dialogInterface.dismiss();
                  userPrefs.addNotifiedNewVersionName(versionUpgrade);
                }
              });
          builder.create().show();
        }
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        Timber.e(throwable, "check upgrade failure");
      }
    });

  }

  @Override
  protected void onStart() {
    super.onStart();
    setupImagePoemBackground();
  }

  private void setupImagePoemBackground() {
    if (!userPrefs.getHomeImagePoemSetting()) {
      setContainerBgColorFromPrefs();
      return;
    }

    if (!userPrefs.canFetchNextHomeImagePoem() && userPrefs.getLastHomeImagePoem() != null) {
      // use last imagePoem data
      setImagePoem(userPrefs.getLastHomeImagePoem());
      return;
    }

    if (backgroundImage.getWidth() == 0) {
      backgroundImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          backgroundImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          loadImagePoem();
        }
      });
    } else {
      loadImagePoem();
    }
  }

  private void loadImagePoem() {
    userService.getImagePoem(backgroundImage.getWidth(), backgroundImage.getHeight())
        .compose(RxUtil.<JsonDataResponse<ImagePoem>>normalSchedulers())
        .filter(new Func1<JsonDataResponse<ImagePoem>, Boolean>() {
          @Override
          public Boolean call(JsonDataResponse<ImagePoem> response) {
            return (response.getRc() == Constants.ServerResultCode.RESULT_OK)
                && (response.getData() != null);
          }
        })
        .subscribe(new Action1<JsonDataResponse<ImagePoem>>() {
          @Override
          public void call(JsonDataResponse<ImagePoem> response) {
            setImagePoem(response.getData());
            userPrefs.setLastHomeImagePoem(response.getData());
            userPrefs.setNextFetchHomeImagePoemTime(response.getData().getNextFetchTimeSec());
            Blaster.log(LoggingData.LOAD_IMAGE_EVENT);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Timber.e(throwable, "getImagePoem() failure");
          }
        });
  }

  private void setImagePoem(ImagePoem imagePoem) {
    setContainerBgColor(R.color.transparent);
    if (imagePoem == null) {
      Picasso.with(this)
          .load(R.mipmap.default_home_image)
          .fit()
          .centerCrop()
          .into(backgroundImage);
    } else {
      Picasso.with(MainActivity.this)
          .load(imagePoem.getImageUrl())
          .placeholder(R.mipmap.default_home_image)
          .fit()
          .centerCrop()
          .into(backgroundImage);
      threeLinePoemView.setThreeLinePoem(imagePoem.getPoem());
    }
  }

  @OnClick(R.id.setting)
  void toSettingsPage(View v) {
    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
    startActivityForResult(intent, Constants.RequestCode.REQUEST_CODE_BG_COLOR_CHANGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.RequestCode.REQUEST_CODE_BG_COLOR_CHANGE) {
      if (resultCode == RESULT_OK) {
        setContainerBgColorFromPrefs();
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(InvalidUserTokenEvent event) {
    makeToast(getString(R.string.invalid_token_force_logout));
    userManager.logoutByInvalidToken(this);
  }

  private void setDate(DateTime date) {
    year = date.getYear();
    month = date.getMonthOfYear();
    day = date.getDayOfMonth();
  }

  private void setTodayAsFullDate() {
    DateTime currentDateTime = new DateTime();
    setDate(currentDateTime);
  }

  private void updateFullDate() {
    FullDateManager fullDateManager = new FullDateManager();
    yearTextView.setText(fullDateManager.getYear(year));
    monthTextView.setText(fullDateManager.getMonth(month));
    dayTextView.setText(fullDateManager.getDay(day));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(YEAR, year);
    outState.putInt(MONTH, month);
    outState.putInt(DAY, day);
    super.onSaveInstanceState(outState);
  }

  public static Intent createIntent(Context context) {
    Intent intent = new Intent(context, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_NO_ANIMATION);
    return intent;
  }
}
