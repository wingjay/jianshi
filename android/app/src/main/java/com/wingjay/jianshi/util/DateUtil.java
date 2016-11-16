/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/16/16 3:31 PM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.util;

import org.joda.time.DateTime;

public class DateUtil {
  private static final long SECOND_IN_MILLIS = 1000;

  public static boolean checkDayAndMonth(int day, int month, int year) {
    if (month <= 0 || month > 12 || day <= 0) {
      return false;
    }
    DateTime dateTime = new DateTime(year, month, 1, 0, 0);
    return day <= dateTime.dayOfMonth().getMaximumValue();
  }

  public static int getLastDay(int month, int year) {
    if (!checkMonth(month)) {
      return -1;
    }
    DateTime dateTime = new DateTime(year, month, 1, 0, 0);
    return dateTime.dayOfMonth().getMaximumValue();
  }

  private static boolean checkMonth(int month) {
    return month > 0 && month <= 12;
  }

  public static long getCurrentTimeStamp() {
    return System.currentTimeMillis() / SECOND_IN_MILLIS;
  }

}
