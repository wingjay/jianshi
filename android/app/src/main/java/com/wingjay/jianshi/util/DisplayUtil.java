/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * Transformation of pixel, sp, dp.
 */
public class DisplayUtil {

  private DisplayUtil() {

  }


  private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;



  public static int dp2px(int dp) {
    return Math.round(dp * DENSITY);
  }

  public static int getDisplayWidth() {
    return Resources.getSystem().getDisplayMetrics().widthPixels;
  }


  public static int getDisplayHeight() {
    return Resources.getSystem().getDisplayMetrics().heightPixels;
  }
  /**
   * Transfer sp to px in order keep font size the same
   * @param context
   * @param spValue
   * @return
   */
  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /**
   * Transfer px to sp in order keep font size the same
   * @param context
   * @param pxValue
   * @return
   */
  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }

  /**
   * transfer px to dip/dp to keep size the same
   *
   */
  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * transfer dip/dp to px to keep size the same
   *
   */
  public static int dip2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }


}
