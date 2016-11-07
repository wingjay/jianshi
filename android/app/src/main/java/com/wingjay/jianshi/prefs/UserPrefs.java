package com.wingjay.jianshi.prefs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wingjay.jianshi.BuildConfig;
import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.ImagePoem;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.bean.VersionUpgrade;
import com.wingjay.jianshi.di.ForApplication;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by wingjay on 10/4/15.
 */
public class UserPrefs extends BasePrefs {

  public final static String PREFS_NAME = "userPrefs";

  @Inject
  Gson gson;

  @Inject
  public UserPrefs(@ForApplication Context context) {
    super(context, PREFS_NAME);
  }

  private final static String KEY_VERTICAL_WRITE = "vertical_write";

  public void setVerticalWrite(boolean verticalWrite) {
    setBoolean(KEY_VERTICAL_WRITE, verticalWrite);
  }

  public boolean getVerticalWrite() {
    return getBoolean(KEY_VERTICAL_WRITE, true);
  }

  private final static String KEY_HOME_IMAGE_POEM = "key_home_image_poem";

  public void setHomeImagePoem(boolean homeImagePoem) {
    setBoolean(KEY_HOME_IMAGE_POEM, homeImagePoem);
  }

  public boolean getHomeImagePoemSetting() {
    return getBoolean(KEY_HOME_IMAGE_POEM, false);
  }

  private final static String KEY_LAST_FETCH_HOME_IMAGE_POEM_TIME = "key_last_fetch_home_image_poem_time";

  public void setLastFetchHomeImagePoemTime() {
    setLong(KEY_LAST_FETCH_HOME_IMAGE_POEM_TIME, System.currentTimeMillis() / 1000);
  }

  public boolean canFetchNextHomeImagePoem() {
    long timeGap = (BuildConfig.DEBUG ? Constants.DEBUG_FETCH_HOME_IMAGE_POEM_TIME_GAP
        : Constants.FETCH_HOME_IMAGE_POEM_TIME_GAP);

    return System.currentTimeMillis() / 1000 > timeGap + getLong(KEY_LAST_FETCH_HOME_IMAGE_POEM_TIME, 0);
  }

  private final static String KEY_LAST_HOME_IMAGE_POEM = "key_last_home_image_poem";

  public void setLastHomeImagePoem(@NonNull ImagePoem imagePoem) {
    String imagePoemString = gson.toJson(imagePoem);
    setString(KEY_LAST_HOME_IMAGE_POEM, imagePoemString);
  }

  @Nullable
  public ImagePoem getLastHomeImagePoem() {
    String imagePoemString = getString(KEY_LAST_HOME_IMAGE_POEM, null);
    if (TextUtils.isEmpty(imagePoemString)) {
      return null;
    }
    return gson.fromJson(imagePoemString, ImagePoem.class);
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

  public void clearUser() {
    setString(KEY_USER, null);
  }

  private static final String KEY_SYNC_TOKEN = "sync_token";

  public void setSyncToken(@NonNull String syncToken) {
    setString(KEY_SYNC_TOKEN, syncToken);
  }

  public String getSyncToken() {
    return getString(KEY_SYNC_TOKEN, "");
  }

  private static final String KEY_UPGRADE_VERSION = "key_upgrade_version";

  public void setVersionUpgrade(@Nullable VersionUpgrade versionUpgrade) {
    String versionUpgradeString = (versionUpgrade == null) ? "" : gson.toJson(versionUpgrade);
    setString(KEY_UPGRADE_VERSION, versionUpgradeString);
  }

  public @Nullable VersionUpgrade getVersionUpgrade() {
    String versionUpgradeString = getString(KEY_UPGRADE_VERSION, "");
    if (TextUtils.isEmpty(versionUpgradeString)) {
      return null;
    }
    return gson.fromJson(versionUpgradeString, VersionUpgrade.class);
  }

}
