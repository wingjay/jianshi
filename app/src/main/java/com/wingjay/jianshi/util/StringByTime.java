package com.wingjay.jianshi.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.wingjay.jianshi.R;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * provide string based on current time.
 */
public class StringByTime {

    private enum TimeRange {
        MORNING(6, 10),
        BEFORENOON(10, 12),
        NOON(12, 14),
        AFTERNOON(14, 18),
        NIGHT(18, 21),
        EVENING(21, 24),
        MIDNIGHT(0, 6),
        DEFAULT(-1, -1);
        private int startHour, endHour;

        TimeRange(int startHour, int endHour) {
            this.startHour = startHour;
            this.endHour = endHour;
        }

        public boolean contains(int currentHour) {
            return startHour < currentHour && currentHour <= endHour;
        }

        public static TimeRange getType(int currentHour) {
            if (MORNING.contains(currentHour)) {
                return MORNING;
            } else if (BEFORENOON.contains(currentHour)) {
                return BEFORENOON;
            } else if (NOON.contains(currentHour)) {
                return NOON;
            } else if (AFTERNOON.contains(currentHour)) {
                return AFTERNOON;
            } else if (NIGHT.contains(currentHour)) {
                return NIGHT;
            } else if (EVENING.contains(currentHour)) {
                return EVENING;
            } else if (MIDNIGHT.contains(currentHour)) {
                return MIDNIGHT;
            }
            return DEFAULT;
        }
    }

    private static Map<TimeRange, Integer> editContentHintdataSet;
    private static Map<TimeRange, Integer> editTitleHintdataSet;
    static {
        editContentHintdataSet = new HashMap<>();
        editContentHintdataSet.put(TimeRange.MORNING, R.string.edit_content_hint_morning);
        editContentHintdataSet.put(TimeRange.BEFORENOON, R.string.edit_content_hint_before_noon);
        editContentHintdataSet.put(TimeRange.NOON, R.string.edit_content_hint_noon);
        editContentHintdataSet.put(TimeRange.AFTERNOON, R.string.edit_content_hint_after_noon);
        editContentHintdataSet.put(TimeRange.NIGHT, R.string.edit_content_hint_night);
        editContentHintdataSet.put(TimeRange.EVENING, R.string.edit_content_hint_evening);
        editContentHintdataSet.put(TimeRange.MIDNIGHT, R.string.edit_content_hint_midnight);
        editContentHintdataSet.put(TimeRange.DEFAULT, R.string.edit_content_hint);

        editTitleHintdataSet = new HashMap<>();
        editTitleHintdataSet.put(TimeRange.MORNING, R.string.edit_title_hint_morning);
        editTitleHintdataSet.put(TimeRange.BEFORENOON, R.string.edit_title_hint_before_noon);
        editTitleHintdataSet.put(TimeRange.NOON, R.string.edit_title_hint_noon);
        editTitleHintdataSet.put(TimeRange.AFTERNOON, R.string.edit_title_hint_after_noon);
        editTitleHintdataSet.put(TimeRange.NIGHT, R.string.edit_title_hint_night);
        editTitleHintdataSet.put(TimeRange.EVENING, R.string.edit_title_hint_evening);
        editTitleHintdataSet.put(TimeRange.MIDNIGHT, R.string.edit_title_hint_midnight);
        editTitleHintdataSet.put(TimeRange.DEFAULT, R.string.edit_title_hint);
    }

    private static String getStringFromDataset(Map<TimeRange, Integer> dataSet,
                                               Resources resources) {
        DateTime now = new DateTime();
        int currentHour = now.getHourOfDay();
        return resources.getString(dataSet.get(TimeRange.getType(currentHour)));
    }

    public static String getEditContentHintByNow(Context context) {
        Resources resources = context.getResources();
        return getStringFromDataset(editContentHintdataSet, resources);
    }

    public static String getEditTitleHintByNow(Context context) {
        Resources resources = context.getResources();
        return getStringFromDataset(editTitleHintdataSet, resources);
    }


}
