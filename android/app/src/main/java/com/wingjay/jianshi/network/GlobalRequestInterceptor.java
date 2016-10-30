package com.wingjay.jianshi.network;

import android.content.Context;
import android.text.TextUtils;

import com.wingjay.jianshi.di.ForApplication;
import com.wingjay.jianshi.events.InvalidUserTokenEvent;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.util.DeviceUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jay on 8/27/16.
 */
public class GlobalRequestInterceptor implements Interceptor {

  Context applicationContext;
  UserPrefs userPrefs;

  @Inject
  GlobalRequestInterceptor(@ForApplication Context applicationContext, UserPrefs userPrefs) {
    this.applicationContext = applicationContext;
    this.userPrefs = userPrefs;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    HttpUrl.Builder urlBuilder = request.url().newBuilder();
    urlBuilder.addQueryParameter("device_id", DeviceUtil.getAndroidId(applicationContext));

    Request.Builder newRequestBuilder = request.newBuilder();
    if (!TextUtils.isEmpty(userPrefs.getAuthToken())) {
      newRequestBuilder.addHeader("Authorization", userPrefs.getAuthToken());
    }
    Response response = chain.proceed(newRequestBuilder.url(urlBuilder.build()).build());

    if (response.code() == 401 || response.code() == 404) {
      EventBus.getDefault().post(new InvalidUserTokenEvent());
    }
    return response;
  }
}
