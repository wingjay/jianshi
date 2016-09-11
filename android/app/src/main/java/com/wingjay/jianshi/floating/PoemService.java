package com.wingjay.jianshi.floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.floating.adapter.PoemViewPagerAdapter;
import com.wingjay.jianshi.floating.helper.FloatWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/9/1.
 */
public class PoemService extends Service {
  private FloatWindow mFloatWindow;
  private IWXAPI mWechatApi;
  private WindowManager windowManager = null;
  private static final String APP_ID = "wx565518a6df3fcacb";

  public class PoemFloatWindowBinder extends Binder {
    public PoemService getService(){
      return PoemService.this;
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return new PoemFloatWindowBinder();
  }

  @Override
  public void onCreate(){
    super.onCreate();
    initFloatWindow();
  }

  public void show(){
    if(null != mFloatWindow){
      mFloatWindow.show();
    }
  }

  private void initFloatWindow(){
    regToWx();
    if(windowManager == null){
      windowManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    }

    LayoutInflater lf = LayoutInflater.from(getApplicationContext());
    View floatView = lf.inflate(R.layout.view_floatwindow_float,null);
    View expandView = lf.inflate(R.layout.view_floatwindow_expand,null);

    mFloatWindow = new FloatWindow(this);
    mFloatWindow.setFloatView(floatView);
    mFloatWindow.setPlayerView(expandView);

    ViewPager viewPager = (ViewPager)expandView.findViewById(R.id.expand_content_viewpager);
    View view2 = lf.inflate(R.layout.item_floatwindow_2,null);

    List<View> viewList = new ArrayList<>();
    viewList.add(view2);
    PoemViewPagerAdapter viewPagerAdapter = new PoemViewPagerAdapter(viewList);
    viewPager.setAdapter(viewPagerAdapter);
  }

  private void regToWx(){
    mWechatApi = WXAPIFactory.createWXAPI(this,APP_ID,true);
    mWechatApi.registerApp(APP_ID);
  }
}
