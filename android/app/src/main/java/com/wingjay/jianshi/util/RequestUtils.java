/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.util;

import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by Jay on 11/9/16.
 */

public class RequestUtils {

  private static String getRandomHexString(int length) {
    Random r = new Random();
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(r.nextInt()));
    }

    return sb.toString().substring(0, length);
  }

  @NonNull
  public static String generateRequestId() {
    return String.format(
        "%s-%s", getRandomHexString(16), Long.toString(System.currentTimeMillis()));
  }

}
