package com.wingjay.jianshi.di;

import com.wingjay.jianshi.global.JianShiApplication;

import dagger.Module;

/**
 * Created by Jay on 8/10/16.
 */
@Module
public class AppModule {

  private final JianShiApplication jianShiApplication;

  public AppModule(JianShiApplication jianShiApplication) {
    this.jianShiApplication = jianShiApplication;
  }
}
