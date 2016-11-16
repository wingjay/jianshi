/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.theme;

import com.wingjay.jianshi.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wingjay on 10/11/15.
 */
public enum TraditionalColorNamer {
  DEFAULT(R.color.normal_bg, "默认"),
  ZHU_QING(R.color.zhu_qing, "竹青"),
  CHI_JIN(R.color.chi_jin, "赤金"),
  QIAN_BAI(R.color.qian_bai, "铅白"),
  YING_BAI(R.color.ying_bai, "莹白"),
  SU(R.color.su, "素"),
  YUE_BAI(R.color.yue_bai, "月白"),
  SHUI_HONG(R.color.shui_hong, "水红"),
  CANG_HUANG(R.color.cang_huang, "苍黄"),
  DING_XIANG_SE(R.color.ding_xiang_se, "丁香色"),
  AI_LV(R.color.ai_lv, "艾绿"),
  YU_SE(R.color.yu_se, "玉色"),
  HUANG_LU(R.color.huang_lu, "黄栌"),
  JIANG_HUANG(R.color.jiang_huang, "姜黄");

  private int colorRes;
  private String colorName;
  private static List<TraditionalColorNamer> colorNamerList;

  TraditionalColorNamer(int res, String name) {
    this.colorRes = res;
    this.colorName = name;
  }

  static {
    TraditionalColorNamer[] list = {
        DEFAULT, ZHU_QING, CHI_JIN, QIAN_BAI, YING_BAI, SU, YUE_BAI, SHUI_HONG,
        CANG_HUANG, DING_XIANG_SE, AI_LV, YU_SE, HUANG_LU, JIANG_HUANG
    };
    colorNamerList = Arrays.asList(list);
  }

  public String getColorName() {
    return this.colorName;
  }

  public int getColorRes() {
    return this.colorRes;
  }

  public static List<TraditionalColorNamer> getAllColorNamer() {
    return colorNamerList;
  }
}
