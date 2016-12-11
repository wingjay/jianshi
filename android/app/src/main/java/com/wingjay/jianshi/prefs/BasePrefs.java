/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

/**
 * Created by wingjay on 10/4/15.
 */
public class BasePrefs extends Observable {

  protected Context context;
  private final static String PREFS_NAME = "PREFS_NAME";
  private static final String KEY_TIME_MODIFIED = "timeModified";

  protected final SharedPreferences preferences;

  public BasePrefs(Context context, final String prefName) {
    this.context = context;
    this.preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
  }

  protected String getString(String key, String defaultValue) {
    return preferences.getString(key, defaultValue);
  }

  protected void setString(String key, String value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.putLong(KEY_TIME_MODIFIED, System.currentTimeMillis());
    editor.apply();
  }

  protected Integer getInt(String key, int defaultValue) {
    return preferences.getInt(key, defaultValue);
  }

  protected void setInt(String key, int value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(key, value);
    editor.putLong(KEY_TIME_MODIFIED, System.currentTimeMillis());
    editor.apply();
  }

  protected void setBoolean(String key, boolean value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(key, value);
    editor.putLong(KEY_TIME_MODIFIED, System.currentTimeMillis());
    editor.apply();
  }

  protected boolean getBoolean(String key, boolean defaultValue) {
    return preferences.getBoolean(key, defaultValue);
  }

  protected void setLong(String key, long value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(key, value);
    editor.putLong(KEY_TIME_MODIFIED, System.currentTimeMillis());
    editor.apply();
  }

  protected long getLong(String key, long defaultValue) {
    return preferences.getLong(key, defaultValue);
  }

  protected Set<String> getStringSet(String key, Set<String> defaultValue) {
    return preferences.getStringSet(key, defaultValue);
  }

  protected void setStringSet(String key, Set<String> value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putStringSet(key, value);
    editor.putLong("timeModified", System.currentTimeMillis());
    editor.apply();
  }

  protected void appendToStringSet(String key, String value) {
    Object set = this.getStringSet(key, (Set)null);
    if(set == null) {
      set = new HashSet();
    }

    HashSet newSet = new HashSet((Collection)set);
    newSet.add(value);
    this.setStringSet(key, newSet);
  }

  protected boolean hasValueInStringSet(String key, String value) {
    Set set = this.getStringSet(key, (Set)null);
    return set != null && set.contains(value);
  }

  public void clear() {
    SharedPreferences.Editor editor = preferences.edit();
    editor.clear();
    editor.apply();
  }
}
