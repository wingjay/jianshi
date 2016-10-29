package com.wingjay.jianshi.util;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class CaptureViewUtil {

  private CaptureViewUtil() {
  }

  public static Observable<Boolean> captureView(View view, final String path) {
    final WeakReference<View> viewWeakReference = new WeakReference<>(view);
    return Observable.just(null)
        .subscribeOn(Schedulers.io())
        .flatMap(new Func1<Object, Observable<Bitmap>>() {
          @Override
          public Observable<Bitmap> call(Object o) {
            Timber.i("CaptureView %s", Thread.currentThread().getName());
            View view = viewWeakReference.get();
            if (view != null) {
              int width = view.getWidth();
              int height = view.getHeight();
              Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
              return Observable.just(bitmap);
            }
            return Observable.just(null);
          }
        }).observeOn(AndroidSchedulers.mainThread())
        .filter(new Func1<Bitmap, Boolean>() {
          @Override
          public Boolean call(Bitmap bitmap) {
            return bitmap != null && viewWeakReference.get() != null;
          }
        }).flatMap(new Func1<Bitmap, Observable<Bitmap>>() {
          @Override
          public Observable<Bitmap> call(Bitmap bitmap) {
            View view = viewWeakReference.get();
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return Observable.just(bitmap);
          }
        }).flatMap(new Func1<Bitmap, Observable<Boolean>>() {
          @Override
          public Observable<Boolean> call(Bitmap bitmap) {
            File imageFile = new File(path);
            boolean result = false;
            try {
              FileOutputStream outputStream = new FileOutputStream(imageFile);
              bitmap.compress(Bitmap.CompressFormat.JPEG, 90, new BufferedOutputStream(outputStream));
              outputStream.flush();
              outputStream.close();
              result = true;
            } catch (Throwable e) {
              Timber.e(e, e.getMessage());
            } finally {
              bitmap.recycle();
            }
            return Observable.just(result);
          }
        });
  }
}
