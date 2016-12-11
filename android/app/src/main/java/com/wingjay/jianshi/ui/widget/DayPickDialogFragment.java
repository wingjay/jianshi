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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wingjay.jianshi.manager.FullDateManager;
import com.wingjay.jianshi.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wingjay on 9/30/15.
 */
public class DayPickDialogFragment extends DialogFragment {

  public final static String CHOOSE_YEAR = "chooseYear";
  public final static String CHOOSE_MONTH = "chooseMonth";
  public final static String CHOOSE_DAY = "chooseDay";

  private ListView dayListView;

  private List<String> dayList;
  private Map<String, DateTime> dayMapToDateTime;
  private int year = 0;
  private int month = 0;
  private int day = 0;

  private OnDayChoosedListener onDayChoosedListener;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      year = bundle.getInt(CHOOSE_YEAR);
      month = bundle.getInt(CHOOSE_MONTH);
      day = bundle.getInt(CHOOSE_DAY);
    }
    if (day == 0 || month == 0 || year == 0) {
      onDetach();
    }
    constructDayList();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_day_pick, container, false);
    getDialog().setTitle(getResources().getString(R.string.please_choose_day));
    dayListView = (ListView) root.findViewById(R.id.day_list_view);
    dayListView.setAdapter(new DayPickAdapter(getActivity(), dayList, dayMapToDateTime));

    return root;
  }

  private void constructDayList() {
    dayList = new ArrayList<>();
    dayMapToDateTime = new HashMap<>();
    DateTime dateTime = new DateTime(year, month, day, 0, 0);
    for (int i=6; i>=-6; i--) {
      DateTime newDateTime = dateTime.minusDays(i);
      if (newDateTime.getMonthOfYear() != month || newDateTime.getYear() != year) {
        continue;
      }
      int day = newDateTime.getDayOfMonth();
      FullDateManager fullDateManager = new FullDateManager();
      dayList.add(fullDateManager.getDay(day));
      dayMapToDateTime.put(fullDateManager.getDay(day), newDateTime);
    }
  }

  private class DayPickAdapter extends BaseAdapter {

    private class ViewHolder {
      TextView textView;
    }

    private List<String> dayList;
    private Context context;
    private Map<String, DateTime> dayMapToDateTime;

    public DayPickAdapter(Context context,
                          List<String> dayList,
                          Map<String, DateTime> dayMapToDateTime) {
      this.dayList = dayList;
      this.context = context;
      this.dayMapToDateTime = dayMapToDateTime;
    }

    @Override
    public int getCount() {
      return dayList.size();
    }

    @Override
    public Object getItem(int position) {
      return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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

      final String day = dayList.get(position);
      viewHolder.textView.setText(day);
      viewHolder.textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (onDayChoosedListener != null) {
            onDayChoosedListener.onDayChoosed(dayMapToDateTime.get(day));
          }
          dismiss();
        }
      });
      return convertView;
    }
  }

  public void setOnDayChoosedListener(OnDayChoosedListener onDayChoosedListener) {
    this.onDayChoosedListener = onDayChoosedListener;
  }

  public interface OnDayChoosedListener {
    void onDayChoosed(DateTime chooseDate);
  }
}
