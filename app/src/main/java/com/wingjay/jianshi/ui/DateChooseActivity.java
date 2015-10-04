package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wingjay.jianshi.FullDateManager;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.view.DayChooser;
import com.wingjay.jianshi.ui.view.DayPickDialogFragment;
import com.wingjay.jianshi.ui.view.RedPointView;
import com.wingjay.jianshi.ui.view.VerticalTextView;

import org.joda.time.DateTime;

import butterknife.InjectView;
import butterknife.OnClick;

public class DateChooseActivity extends BaseActivity {

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

    @InjectView(R.id.setting)
    RedPointView settingView;
    
    @InjectView(R.id.day_chooser)
    DayChooser dayChooser;

    private int year, month, day;
    private DateTime currentDateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_choose);

        setTodayAsFullDate();

        writerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long dateSeconds = FullDateManager.getDateSeconds(currentDateTime);
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
                        currentDateTime = chooseDate;
                        updateFullDate();
                    }
                });
                dayPickDialogFragment.show(getSupportFragmentManager(), null);
            }
        });
    }

    @OnClick(R.id.setting)
    void toSettingsPage(View v) {
        startActivity(new Intent(DateChooseActivity.this, SettingActivity.class));
    }

    private void setTodayAsFullDate() {
        currentDateTime = new DateTime();
        updateFullDate();
    }

    private void updateFullDate() {
        DateTime dateTime = currentDateTime;
        year = dateTime.getYear();
        month = dateTime.getMonthOfYear();
        day = dateTime.getDayOfMonth();
        FullDateManager fullDateManager = new FullDateManager();
        yearTextView.setText(fullDateManager.getYear(year));
        monthTextView.setText(fullDateManager.getMonth(month));
        dayTextView.setText(fullDateManager.getDay(day));
    }

}
