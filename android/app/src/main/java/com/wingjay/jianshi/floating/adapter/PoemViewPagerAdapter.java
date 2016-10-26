package com.wingjay.jianshi.floating.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ASUS on 2016/9/2.
 */
public class PoemViewPagerAdapter extends PagerAdapter {
  private List<View> mContentViews;

  public PoemViewPagerAdapter(List<View>contentViews){
    this.mContentViews = contentViews;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object)   {
    container.removeView(mContentViews.get(position));
  }


  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    container.addView(mContentViews.get(position), 0);
    return mContentViews.get(position);
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public int getCount() {
    return mContentViews == null ? 0 : mContentViews.size();
  }
}
