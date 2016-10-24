package com.wingjay.jianshi.ui.theme;

import android.content.Context;

import com.wingjay.jianshi.prefs.UserPrefs;

/**
 * Provide different color customized by user.
 */
public class BackgroundColorHelper {

  public static int getBackgroundColorResFromPrefs(Context context) {
    UserPrefs userPrefs = new UserPrefs(context);
    return userPrefs.getBackgroundColor();
  }

}
