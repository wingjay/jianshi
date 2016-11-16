/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.di;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.wingjay.jianshi.BuildConfig;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.network.GlobalRequestInterceptor;
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
  @ForApplication
  Context provideApplicationContext() {
    return jianShiApplication;
  }

  @Provides
  @Singleton
  OkHttpClient provideOkHttpClient(GlobalRequestInterceptor globalRequestInterceptor) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectionPool(new ConnectionPool(5, 59, TimeUnit.SECONDS))
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(globalRequestInterceptor)
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

  @Provides
  ExclusionStrategy provideExclusionStrategy() {
    return new ExclusionStrategy() {
      @Override
      public boolean shouldSkipField(FieldAttributes f) {
        return false;
      }

      @Override
      public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.equals(ModelAdapter.class);
      }
    };
  }

  @Provides
  Gson provideGson(ExclusionStrategy exclusionStrategy) {
    return new GsonBuilder().setExclusionStrategies(exclusionStrategy).create();
  }
}
