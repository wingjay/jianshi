package com.wingjay.jianshi.global;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.Secure;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.wingjay.jianshi.db.DbOpenHepler;
import com.wingjay.jianshi.di.AppComponent;
import com.wingjay.jianshi.di.AppModule;
import com.wingjay.jianshi.di.DaggerAppComponent;
import com.wingjay.jianshi.ui.widget.font.FontFamilyFactory;

import io.fabric.sdk.android.Fabric;

/**
* Created by wingjay on 9/30/15.
*/
public class JianShiApplication extends Application {

  private static JianShiApplication instance;
  private static Context newsContext;

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
        new DbOpenHepler(getApplicationContext(), DbOpenHepler.DB_NAME, DbOpenHepler.DB_VAERION);
    }
    return dbOpenHepler;
  }

  private static AppComponent appComponent;

  @Override
  public void onCreate() {
//        FIR.init(this);
    super.onCreate();

    newsContext = getApplicationContext();

    appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(JianShiApplication.this))
        .build();

    final Fabric fabric = new Fabric.Builder(this)
            .kits(new Crashlytics())
            .debuggable(true)
            .build();
    Fabric.with(fabric);
    Stetho.initializeWithDefaults(this);
    instance = this;
    FontFamilyFactory.init(this).subscribe();
  }

  public static AppComponent getAppComponent() {
    return appComponent;
  }

  public static Context getPoemContext(){
    return newsContext;
  }
}
