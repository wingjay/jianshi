package com.wingjay.jianshi.global;

import android.app.Application;
import android.content.ContentResolver;
import android.provider.Settings.Secure;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.wingjay.jianshi.db.DbOpenHepler;

import im.fir.sdk.FIR;
import io.fabric.sdk.android.Fabric;

/**
 * Created by wingjay on 9/30/15.
 */
public class JianShiApplication extends Application {

    private static JianShiApplication instance;

    public static JianShiApplication getInstance() {
        return instance;
    }

    public String getDeviceId() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        return Secure.getString(contentResolver, Secure.ANDROID_ID);
    }

    private DbOpenHepler dbOpenHepler;
    public DbOpenHepler getDbOpenHepler() {
        if (dbOpenHepler == null ||
                dbOpenHepler.getReadableDatabase().getVersion() < DbOpenHepler.DB_VAERION) {
            dbOpenHepler =
                    new DbOpenHepler(getApplicationContext(), DbOpenHepler.DN_NAME, DbOpenHepler.DB_VAERION);
        }
        return dbOpenHepler;
    }

    @Override
    public void onCreate() {
        FIR.init(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        instance = this;
    }

}
