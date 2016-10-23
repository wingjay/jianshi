package com.wingjay.jianshi.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.structure.ModelAdapter;


public class GsonUtil {
  private static ExclusionStrategy gsonExclusionStrategy = new ExclusionStrategy() {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
      return clazz.equals(ModelAdapter.class);
    }
  };

  public static Gson getGsonWithExclusionStrategy() {
    return new GsonBuilder().setExclusionStrategies(gsonExclusionStrategy).create();
  }
}
