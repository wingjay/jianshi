package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wingjay.jianshi.FullDateManager;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.DatePickDialogFragment;
import com.wingjay.jianshi.ui.widget.DayChooser;
import com.wingjay.jianshi.ui.widget.DayPickDialogFragment;
import com.wingjay.jianshi.ui.widget.RedPointView;
import com.wingjay.jianshi.ui.widget.VerticalTextView;
import com.wingjay.jianshi.util.ConstantUtil;
import com.wingjay.jianshi.util.DateUtil;
import com.wingjay.jianshi.util.UpgradeUtil;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.OnClick;

public class DateChooseActivity extends BaseActivity {

    private final static String YEAR = "year";
    private final static String MONTH = "month";
    private final static String DAY = "day";

    @BindView(R.id.year) VerticalTextView yearTextView;
    @BindView(R.id.month) VerticalTextView monthTextView;
    @BindView(R.id.day) VerticalTextView dayTextView;
    @BindView(R.id.writer) RedPointView writerView;
    @BindView(R.id.reader) RedPointView readerView;
    @BindView(R.id.day_chooser) DayChooser dayChooser;

    private volatile int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_choose);

        if (savedInstanceState != null) {
            year = savedInstanceState.getInt(YEAR);
            month = savedInstanceState.getInt(MONTH);
            day = savedInstanceState.getInt(DAY);
        } else {
            setTodayAsFullDate();
            UpgradeUtil.checkUpgrade(DateChooseActivity.this);
        }
        updateFullDate();

        writerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime current = new DateTime(year, month, day, 0, 0);
                long dateSeconds = FullDateManager.getDateSeconds(current);
                Intent i = EditActivity.createIntent(DateChooseActivity.this, dateSeconds);
                startActivity(i);
            }
        });

        readerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DateChooseActivity.this, DiaryListActivity.class));
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
        Intent intent = new Intent(DateChooseActivity.this, SettingActivity.class);
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
