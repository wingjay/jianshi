/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wingjay.jianshi.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wingjay on 10/6/15.
 */
public class DatePickDialogFragment extends DialogFragment {

  public final static String CURRENT_YEAR = "currentYear";
  public final static String CURRENT_MONTH = "currentMonth";
  public final static String CURRENT_DAY = "currentDay";

  public final static String PICK_TYPE = "pickType";
  public final static int PICK_TYPE_MONTH = 1;
  public final static int PICK_TYPE_DAY = 2;

  private int pickType;
  private int year, month, day;

  private ListView dateListView;
  private List<Integer> dateList;
  private OnDateChoosedListener onDateChoosedListener;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle == null) {
      onDestroy();
      return;
    }
    pickType = bundle.getInt(PICK_TYPE, -1);
    year = bundle.getInt(CURRENT_YEAR, -1);
    month = bundle.getInt(CURRENT_MONTH, -1);
    day = bundle.getInt(CURRENT_DAY, -1);
    if ((pickType != PICK_TYPE_DAY && pickType != PICK_TYPE_MONTH)
        || year == -1
        || month == -1
        || day == -1) {
      onDestroy();
      return;
    }
    initDateSet();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_date_pick, container, false);
    dateListView = (ListView) root.findViewById(R.id.date_list_view);
    dateListView.setAdapter(new DatePickAdapter(getActivity(), dateList));

    String title = "";
    if (pickType == PICK_TYPE_DAY) {
      title = getResources().getString(R.string.please_choose_day);
    } else if (pickType == PICK_TYPE_MONTH) {
      title = getResources().getString(R.string.please_choose_month);
    }
    getDialog().setTitle(title);

    return root;
  }

  // only month and day
  private void initDateSet() {
    dateList = new ArrayList<>();
    if (pickType == PICK_TYPE_MONTH) {
      for (int i=1; i<=12; i++) {
        dateList.add(i);
      }
    } else if (pickType == PICK_TYPE_DAY) {
      DateTime dateTime = new DateTime(year, month, day, 0, 0);
      int daysNumInMonth = dateTime.dayOfMonth().getMaximumValue();
      for (int i=1; i<=daysNumInMonth; i++) {
        dateList.add(i);
      }
    }
  }

  private class DatePickAdapter extends BaseAdapter{

    private class ViewHolder {
      TextView textView;
    }

    private Context context;
    private List<Integer> mDateList;

    public DatePickAdapter(Context context, List<Integer> dateList) {
      this.context = context;
      mDateList = dateList;
    }

    @Override
    public int getCount() {
      return mDateList.size();
    }

    @Override
    public Object getItem(int position) {
      return mDateList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder;
      if (convertView == null) {
        convertView = LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_1, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.textView = (TextView) convertView.findViewById(android.R.id.text1);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      final int date = mDateList.get(position);
      Log.i("test", String.valueOf(date));
      viewHolder.textView.setText(String.valueOf(date));
      viewHolder.textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (onDateChoosedListener != null) {
            if (pickType == PICK_TYPE_DAY) {
              onDateChoosedListener.onDayChoosed(date);
            } else if (pickType == PICK_TYPE_MONTH) {
              onDateChoosedListener.onMonthChoosed(date);
            }
          }
          dismiss();
        }
      });
      return convertView;
    }
  }

  public void setOnDateChoosedListener(OnDateChoosedListener onDateChoosedListener) {
    this.onDateChoosedListener = onDateChoosedListener;
  }

  public interface OnDateChoosedListener {
    void onDayChoosed(int mDay);
    void onMonthChoosed(int mMonth);
  }


}
