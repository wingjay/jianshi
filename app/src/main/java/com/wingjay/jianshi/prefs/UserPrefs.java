package com.wingjay.jianshi.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.util.UpgradeUtil;

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

    private final static String KEY_GLOBAL_BACKGROUND_COLOR_RES = "global_background_color_res";

    public void setBackgroundColor(int colorRes) {
        setInt(KEY_GLOBAL_BACKGROUND_COLOR_RES, colorRes);
    }

    public int getBackgroundColor() {
        return getInt(KEY_GLOBAL_BACKGROUND_COLOR_RES, R.color.normal_bg);
    }

}
