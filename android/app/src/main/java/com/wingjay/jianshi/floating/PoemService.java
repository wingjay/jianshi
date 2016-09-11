package com.wingjay.jianshi.floating;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.floating.adapter.PoemViewPagerAdapter;
import com.wingjay.jianshi.floating.helper.FloatWindow;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.ui.widget.RedPointView;
import com.wingjay.jianshi.util.FullDateManager;
import com.wingjay.jianshi.util.LanguageUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/9/1.
 */
public class PoemService extends Service {
  private FloatWindow mFloatWindow;
  private View mFloatView;
  private View mExpandView;
  private ViewPager mViewPager;
  private EditText mTitleET;
  private EditText mContentET;
  private RedPointView mSaveRPV;

  private PoemViewPagerAdapter mViewPagerAdapter;
  private volatile int year, month, day;
  private long dateSeconds;
  private final String PATH = "sdcard/share.png";

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
    setTime();
    initFloatWindow();
  }

  public void show(){
    if(null != mFloatWindow){
      mFloatWindow.show();
    }
  }

  private void initFloatWindow(){
    LayoutInflater lf = LayoutInflater.from(getApplicationContext());
    mFloatView = lf.inflate(R.layout.view_floatwindow_float,null);
    mExpandView = lf.inflate(R.layout.view_floatwindow_expand,null);

    mFloatWindow = new FloatWindow(this);
    mFloatWindow.setFloatView(mFloatView);
    mFloatWindow.setPlayerView(mExpandView);

    mViewPager = (ViewPager)mExpandView.findViewById(R.id.expand_content_viewpager);
    View view2 = lf.inflate(R.layout.item_floatwindow_2,null);

    List<View> viewList = new ArrayList<>();
    viewList.add(view2);
    mViewPagerAdapter = new PoemViewPagerAdapter(viewList);
    mViewPager.setAdapter(mViewPagerAdapter);
    registerEvents(null,view2);
  }

  private void registerEvents(View view1,final View view2){
    if(view2 == null)
      return;

    ImageButton shareIB = (ImageButton)view2.findViewById(R.id.share_poem);
    shareIB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try{

        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });
  }

  private void saveDiary() {
    if (!checkNotNull()) {
      Toast.makeText(JianShiApplication.getPoemContext(), R.string.edit_content_not_null,
          Toast.LENGTH_SHORT).show();
      return;
    }
    long saveId = 0;

    String titleString = (TextUtils.isEmpty(mTitleET.getText().toString()))
        ? mTitleET.getHint().toString() : mTitleET.getText().toString();
    String contentString = (TextUtils.isEmpty(mContentET.getText().toString()))
        ? mContentET.getHint().toString() : mContentET.getText().toString();

   if (dateSeconds > 0) {
      contentString += LanguageUtil.getDiaryDateEnder(JianShiApplication.getPoemContext(), dateSeconds);
      saveId = DbUtil.saveDiary(titleString, contentString, dateSeconds);
    }
    Toast.makeText(JianShiApplication.getPoemContext(),
        (saveId > 0) ? R.string.save_success : R.string.save_failure,
        Toast.LENGTH_SHORT).show();
  }

  private boolean checkNotNull() {
    return !TextUtils.isEmpty(mTitleET.getText()) || !TextUtils.isEmpty(mContentET.getText());
  }


  private void setTime(){
    DateTime currentDateTime = new DateTime();
    year = currentDateTime.getYear();
    month = currentDateTime.getMonthOfYear();
    day = currentDateTime.getDayOfMonth();
    DateTime current = new DateTime(year, month, day, 0, 0);
    dateSeconds = FullDateManager.getDateSeconds(current);
  }
}
