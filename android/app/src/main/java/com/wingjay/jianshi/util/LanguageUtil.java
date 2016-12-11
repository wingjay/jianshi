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

import android.content.Context;
import android.content.res.Resources;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.manager.FullDateManager;


public class LanguageUtil {

  public static String getDiaryDateEnder(Context context, long dateSeconds) {
    FullDateManager fullDateManager = new FullDateManager(dateSeconds);
    Resources resources = context.getResources();
    return fullDateManager.getFullCNDate() + resources.getString(R.string.record);
  }

}
