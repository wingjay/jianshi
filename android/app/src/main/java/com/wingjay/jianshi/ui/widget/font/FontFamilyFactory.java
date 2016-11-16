/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.widget.font;

import android.content.Context;
import android.graphics.Typeface;

import rx.Observable;
import rx.functions.Func0;
import rx.schedulers.Schedulers;


public class FontFamilyFactory {

  private final static String DEFAULT_FONT_FAMILY_2 = "jianshi_default.otf";
  private static Typeface typeface = null;

  public static String getDefaultFontFamily() {
    return DEFAULT_FONT_FAMILY_2;
  }

  public static Observable<Void> init(final Context context) {
    return Observable.defer(new Func0<Observable<Void>>() {
      @Override
      public Observable<Void> call() {
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + getDefaultFontFamily());
        return Observable.just(null);
      }
    }).subscribeOn(Schedulers.io());
  }

  public static Typeface getTypeface() {
    return typeface;
  }
}
