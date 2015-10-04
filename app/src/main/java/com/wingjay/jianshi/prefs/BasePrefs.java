package com.wingjay.jianshi.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Observable;

/**
 * Created by wingjay on 10/4/15.
 */
public class BasePrefs extends Observable {

    private final static String PREFS_NAME = "PREFS_NAME";
    private static final String KEY_TIME_MODIFIED = "timeModified";

    protected final SharedPreferences preferences;

    public BasePrefs(Context context) {
        // get child prefs name by reflection
        BasePrefs me = BasePrefs.this;
        Class c = me.getClass();

        try {
            Field f = c.getField("PREFS_NAME");
            String name = (String)f.get(null);
            preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        } catch (NoSuchFieldException e) {
            throw new NoSuchElementException("PREFS_NAME is not specified");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("PREFS_NAME is not specified");
        }
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

}
