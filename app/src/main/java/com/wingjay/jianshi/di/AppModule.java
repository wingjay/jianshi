package com.wingjay.jianshi.di;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.wingjay.jianshi.BuildConfig;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.network.UserService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jay on 8/10/16.
 */
@Module
public class AppModule {

  private final JianShiApplication jianShiApplication;

  public AppModule(JianShiApplication jianShiApplication) {
    this.jianShiApplication = jianShiApplication;
  }

  @Provides
  @Singleton
  OkHttpClient provideOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectionPool(new ConnectionPool(5, 59, TimeUnit.SECONDS))
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false);
    if (BuildConfig.DEBUG) {
      builder.addNetworkInterceptor(new StethoInterceptor());
    }

    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(
        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    builder.addInterceptor(httpLoggingInterceptor);

    return builder.build();
  }

  @Provides
  @Singleton
  Retrofit provideRetrofit(OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();
  }

  @Provides
  UserService provideUserService(Retrofit retrofit) {
    return retrofit.create(UserService.class);
  }

}
