package com.wingjay.jianshi.prefs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.di.ForApplication;

import javax.inject.Inject;

/**
 * Created by wingjay on 10/4/15.
 */
public class UserPrefs extends BasePrefs {

  //// TODO: 8/27/16 (wingjay) Make it Injectable 
  public final static String PREFS_NAME = "userPrefs";

  @Inject
  public UserPrefs(@ForApplication Context context) {
    super(context);
  }

  private final static String KEY_VERTICAL_WRITE = "vertical_write";

  public void setVerticalWrite(boolean verticalWrite) {
    setBoolean(KEY_VERTICAL_WRITE, verticalWrite);
  }

  public boolean getVerticalWrite() {
    return getBoolean(KEY_VERTICAL_WRITE, false);
  }

  private final static String KEY_GLOBAL_BACKGROUND_COLOR_RES = "global_background_color_res";

  public void setBackgroundColor(int colorRes) {
    setInt(KEY_GLOBAL_BACKGROUND_COLOR_RES, colorRes);
  }

  public int getBackgroundColor() {
    return getInt(KEY_GLOBAL_BACKGROUND_COLOR_RES, R.color.normal_bg);
  }

  public final static String KEY_USER_AUTH_TOKEN = "user_auth_token";

  public String getAuthToken() {
    return getString(KEY_USER_AUTH_TOKEN, null);
  }

  public void setAuthToken(@NonNull String authToken) {
    setString(KEY_USER_AUTH_TOKEN, authToken);
  }

  public void clearAuthToken() {
    setString(KEY_USER_AUTH_TOKEN, null);
  }

  public static final String KEY_USER = "user";

  public User getUser() {
    String jsonString = getString(KEY_USER, null);
    if (TextUtils.isEmpty(jsonString)) {
      return null;
    }
    Gson gson = new Gson();
    return gson.fromJson(jsonString, User.class);
  }

  public void setUser(@NonNull User user) {
    Gson gson = new Gson();
    setString(KEY_USER, gson.toJson(user));
  }

  private static final String KEY_SYNC_TOKEN = "sync_token";

  public void setSyncToken(@NonNull String syncToken) {
    setString(KEY_SYNC_TOKEN, syncToken);
  }

  public String getSyncToken() {
    return getString(KEY_SYNC_TOKEN, "");
  }
}
