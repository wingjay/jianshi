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

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wingjay on 10/6/15.
 */
public class StringUtil {

  public static String[] split(String s, String splitFlag) {
    if (TextUtils.isEmpty(s)) {
      return new String[0];
    }
    if (TextUtils.isEmpty(splitFlag)) {
      return new String[]{s};
    }
    while (TextUtils.equals(s.substring(0, 1), splitFlag)) {
      s = s.substring(1);
    }
    if (TextUtils.isEmpty(s)) {
      return new String[0];
    }
    int length = s.length();
    List<String> list = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<length; i++) {
      if (!TextUtils.equals(s.substring(i, i+1), splitFlag)) {
        sb.append(s.substring(i, i+1));
      } else {
        list.add(sb.toString());
        sb = new StringBuilder();
      }
    }
    if (sb.length() > 0) {
      list.add(sb.toString());
    }
    return list.toArray(new String[list.size()]);
  }

}
