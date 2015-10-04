package com.wingjay.jianshi.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wingjay on 10/4/15.
 */
public class UserPrefs extends BasePrefs {

    public final static String PREFS_NAME = "userPrefs";

    public UserPrefs(Context context) {
        super(context);
    }

    private final static String KEY_VERTICAL_WRITE = "vertical_write";

    public void setVerticalWrite(boolean verticalWrite) {
        setBoolean(KEY_VERTICAL_WRITE, verticalWrite);
    }

    public boolean getVerticalWrite() {
        return getBoolean(KEY_VERTICAL_WRITE, false);
    }

}
