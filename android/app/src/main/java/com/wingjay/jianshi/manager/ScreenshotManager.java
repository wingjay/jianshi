/*
 * Created by wingjay on 12/11/16 10:53 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/16/16 3:32 PM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.manager;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.wingjay.jianshi.di.ForApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class ScreenshotManager {

  private Context context;

  @Inject
  ScreenshotManager(@ForApplication Context context) {
    this.context = context;
  }

  // Return file path
  public Observable<String> shotScreen(final View view, final String name) {
    return Observable.defer(new Func0<Observable<String>>() {
      @Override
      public Observable<String> call() {
        final WeakReference<View> viewWeakReference = new WeakReference<>(view);
        final String path = context.getExternalCacheDir() + "/" + name;
        return Observable.just(null)
            .subscribeOn(Schedulers.io())
            .flatMap(new Func1<Object, Observable<Bitmap>>() {
              @Override
              public Observable<Bitmap> call(Object o) {
                Timber.i("ScreenshotManager 1 %s", Thread.currentThread().getName());
                View view = viewWeakReference.get();
                if (view != null) {
                  int width = view.getWidth();
                  int height = view.getHeight();
                  Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                  return Observable.just(bitmap);
                }
                return Observable.just(null);
              }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .filter(new Func1<Bitmap, Boolean>() {
              @Override
              public Boolean call(Bitmap bitmap) {
                Timber.i("ScreenshotManager 2 %s", Thread.currentThread().getName());
                return bitmap != null && viewWeakReference.get() != null;
              }
            })
            .flatMap(new Func1<Bitmap, Observable<Bitmap>>() {
              @Override
              public Observable<Bitmap> call(Bitmap bitmap) {
                Timber.i("ScreenshotManager 3 %s", Thread.currentThread().getName());
                View view = viewWeakReference.get();
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                return Observable.just(bitmap);
              }
            })
            .observeOn(Schedulers.io())
            .flatMap(new Func1<Bitmap, Observable<String>>() {
              @Override
              public Observable<String> call(Bitmap bitmap) {
                Timber.i("ScreenshotManager 4 %s", Thread.currentThread().getName());
                File imageFile = new File(path);
                try {
                  FileOutputStream outputStream = new FileOutputStream(imageFile);
                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, new BufferedOutputStream(outputStream));
                  outputStream.flush();
                  outputStream.close();
                  return Observable.just(path);
                } catch (Throwable e) {
                  Timber.e(e, e.getMessage());
                } finally {
                  bitmap.recycle();
                }
                return Observable.just("");
              }
            });
      }
    });
  }
}
