package com.wingjay.jianshi.global;

import android.app.Application;
import android.content.ContentResolver;
import android.provider.Settings.Secure;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.wingjay.jianshi.BuildConfig;
import com.wingjay.jianshi.di.AppComponent;
import com.wingjay.jianshi.di.AppModule;
import com.wingjay.jianshi.di.DaggerAppComponent;
import com.wingjay.jianshi.ui.widget.font.FontFamilyFactory;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public class JianShiApplication extends Application {

  private static JianShiApplication instance;

  public static JianShiApplication getInstance() {
    return instance;
  }

  public String getDeviceId() {
    ContentResolver contentResolver = getApplicationContext().getContentResolver();
    return Secure.getString(contentResolver, Secure.ANDROID_ID);
  }


  private static AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();

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
    FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());

    initLog();
  }

  private void initLog() {
    Timber.uprootAll();
    Timber.tag("Jianshi");
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
          if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
          }

          Crashlytics.log(priority, tag, message);

          if (t != null) {
            if (priority == Log.ERROR) {
              Crashlytics.logException(t);
            } else if (priority == Log.WARN) {
              Crashlytics.log(t.getMessage());
            }
          }
        }
      });
    }
  }

  public static AppComponent getAppComponent() {
    return appComponent;
  }

}
