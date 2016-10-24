package com.wingjay.jianshi.util;

import android.content.Context;
import android.content.res.Resources;

import com.wingjay.jianshi.R;


public class LanguageUtil {

  public static String getDiaryDateEnder(Context context, long dateSeconds) {
    FullDateManager fullDateManager = new FullDateManager(dateSeconds);
    Resources resources = context.getResources();
    return resources.getString(R.string.space_of_date_record)
        + fullDateManager.getFullDate()
        + resources.getString(R.string.record)
        + resources.getString(R.string.space_of_date_record);
  }

}
