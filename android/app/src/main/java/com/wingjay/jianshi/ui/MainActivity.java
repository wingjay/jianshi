package com.wingjay.jianshi.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.ImagePoem;
import com.wingjay.jianshi.events.InvalidUserTokenEvent;
import com.wingjay.jianshi.global.JianShiApplication;
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
import com.wingjay.jianshi.util.FullDateManager;
import com.wingjay.jianshi.util.RxUtil;
import com.wingjay.jianshi.util.UpgradeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
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
  UserPrefs userPrefs;

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
      UpgradeUtil.checkUpgrade(MainActivity.this);
    }
    updateFullDate();

    writerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DateTime current = new DateTime(year, month, day, 0, 0);
        long dateSeconds = FullDateManager.getDateSeconds(current);
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);
      }
    });

    readerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, DiaryListActivity.class));
      }
    });

    SyncService.syncImmediately(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    setupImagePoemBackground();
  }

  private void setupImagePoemBackground() {
    if (!userPrefs.getHomeImagePoem()) {
      setContainerBgColorFromPrefs();
      return;
    }

    Picasso.with(this)
        .load(R.mipmap.default_home_image)
        .fit()
        .centerCrop()
        .into(backgroundImage);
    setContainerBgColor(R.color.transparent);
    userService.getImagePoem()
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
            ImagePoem imagePoem = response.getData();
            Picasso.with(MainActivity.this)
                .load(imagePoem.getImageUrl())
                .fit()
                .centerCrop()
                .into(backgroundImage);
            threeLinePoemView.setThreeLinePoem(imagePoem.getPoem());
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Timber.e(throwable, "getImagePoem() failure");
          }
        });
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
