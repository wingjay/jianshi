package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.data.Diary;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.DatePickDialogFragment;
import com.wingjay.jianshi.ui.widget.DayChooser;
import com.wingjay.jianshi.ui.widget.DayPickDialogFragment;
import com.wingjay.jianshi.ui.widget.RedPointView;
import com.wingjay.jianshi.ui.widget.VerticalTextView;
import com.wingjay.jianshi.util.ConstantUtil;
import com.wingjay.jianshi.util.DateUtil;
import com.wingjay.jianshi.util.FullDateManager;
import com.wingjay.jianshi.util.UpgradeUtil;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import dagger.internal.Preconditions;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

  private final static String YEAR = "year";
  private final static String MONTH = "month";
  private final static String DAY = "day";

  @InjectView(R.id.year)
  VerticalTextView yearTextView;

  @InjectView(R.id.month)
  VerticalTextView monthTextView;

  @InjectView(R.id.day)
  VerticalTextView dayTextView;

  @InjectView(R.id.writer)
  RedPointView writerView;

  @InjectView(R.id.reader)
  RedPointView readerView;

  @InjectView(R.id.day_chooser)
  DayChooser dayChooser;

  private volatile int year, month, day;

  @Inject
  UserService userService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    JianShiApplication.getInstance().getAppComponent().inject(MainActivity.this);

    userService.getJsonTest()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<JsonDataResponse>() {
          @Override
          public void onCompleted() {
            Log.d("jaydebug", "onCompleted");
          }

          @Override
          public void onError(Throwable e) {
            Log.d("jaydebug", "onError");
          }

          @Override
          public void onNext(JsonDataResponse jsonDataResponse) {
            Log.d("jaydebug", "json data:" + jsonDataResponse.getData());
          }
        });

    Diary diary = DbUtil.getDiary(1);
    Preconditions.checkNotNull(diary);
//    Log.d("jaydebug", "diary 1: " + diary.getTitle() + ", " + diary.getContent() + ", id " + diary.getId() +
//     ", device_id " + diary.getDeviceId() + ", createTime: " + diary.getCreatedTime() + ", modified " + diary.getModifiedTime()
//     + ", DELETE " + diary.getDeleted());
    userService.createDiary(diary.getTitle(), diary.getContent(), diary.getCreatedTime(), diary.getDeviceId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<JsonDataResponse<Diary>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {
            Log.d("jaydebug", "create diary Error");
          }

          @Override
          public void onNext(JsonDataResponse<Diary> diaryJsonDataResponse) {
            Log.d("jaydebug", "create diary data: " + diaryJsonDataResponse.getData().getTitle() + ", "
              + diaryJsonDataResponse.getData().getContent());
          }
        });

    setContentView(R.layout.activity_main);

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
            Intent i = EditActivity.createIntent(MainActivity.this, dateSeconds);
            startActivity(i);
        }
    });

    readerView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, DiaryListActivity.class));
        }
    });

    dayChooser.setOnDayChooserClickListener(new DayChooser.OnDayChooserClickListener() {
        @Override
        public void onDayChoose(int chooseDay) {
            DayPickDialogFragment dayPickDialogFragment = new DayPickDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(DayPickDialogFragment.CHOOSE_DAY, chooseDay);
            bundle.putInt(DayPickDialogFragment.CHOOSE_MONTH, month);
            bundle.putInt(DayPickDialogFragment.CHOOSE_YEAR, year);
            dayPickDialogFragment.setArguments(bundle);
            dayPickDialogFragment.setOnDayChoosedListener(new DayPickDialogFragment.OnDayChoosedListener() {
                @Override
                public void onDayChoosed(DateTime chooseDate) {
                    setDate(chooseDate);
                    updateFullDate();
                }
            });
            dayPickDialogFragment.show(getSupportFragmentManager(), null);
        }
    });
  }

  @OnClick(R.id.setting)
  void toSettingsPage(View v) {
    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
    startActivityForResult(intent, ConstantUtil.REQUEST_CODE_BG_COLOR_CHANGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ConstantUtil.REQUEST_CODE_BG_COLOR_CHANGE) {
        if (resultCode == RESULT_OK) {
            setContainerBgColorFromPrefs();
        }
    }
  }

  @OnClick(R.id.day)
  void chooseDay(View v) {
    showDatePickDialog(DatePickDialogFragment.PICK_TYPE_DAY);
  }

  @OnClick(R.id.month)
  void chooseMonth() {
    showDatePickDialog(DatePickDialogFragment.PICK_TYPE_MONTH);
  }

  private void showDatePickDialog(int pickType) {
    DatePickDialogFragment datePickDialogFragment = new DatePickDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(DatePickDialogFragment.CURRENT_DAY, day);
    bundle.putInt(DatePickDialogFragment.CURRENT_MONTH, month);
    bundle.putInt(DatePickDialogFragment.CURRENT_YEAR, year);
    bundle.putInt(DatePickDialogFragment.PICK_TYPE, pickType);
    datePickDialogFragment.setArguments(bundle);
    datePickDialogFragment.setOnDateChoosedListener(new DatePickDialogFragment.OnDateChoosedListener() {
        @Override
        public void onDayChoosed(int mDay) {
            day = mDay;
            updateFullDate();
        }

        @Override
        public void onMonthChoosed(int mMonth) {
            month = mMonth;
            if (!DateUtil.checkDayAndMonth(day, mMonth, year)) {
                day = DateUtil.getLastDay(mMonth, year);
            }
            updateFullDate();
        }
    });
    datePickDialogFragment.show(getSupportFragmentManager(), null);
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
}
