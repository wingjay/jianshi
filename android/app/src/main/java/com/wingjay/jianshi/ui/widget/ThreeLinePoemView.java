/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.util.StringByTime;
import com.wingjay.jianshi.util.StringUtil;

import javax.annotation.Nullable;

/**
 * Display a three-line poem.
 */
public class ThreeLinePoemView extends FrameLayout {

  private VerticalTextView[] verticalTextViews = new VerticalTextView[3];
  private String[] threeLinePoem;

  public ThreeLinePoemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.view_three_line_poem, this);

    verticalTextViews[0] = (VerticalTextView) findViewById(R.id.first_line);
    verticalTextViews[1] = (VerticalTextView) findViewById(R.id.second_line);
    verticalTextViews[2] = (VerticalTextView) findViewById(R.id.last_line);

    threeLinePoem = StringByTime.getThreeLinePoemArrayByNow();
    showThreeLinePoem(threeLinePoem);
  }

  public void setThreeLinePoem(@Nullable String rawThreeLinePoem) {
    if (TextUtils.isEmpty(rawThreeLinePoem)) {
      threeLinePoem = null;
    } else {
      threeLinePoem = StringUtil.split(rawThreeLinePoem,
          getResources().getString(R.string.three_line_string_split));
    }

    showThreeLinePoem(threeLinePoem);
  }

  private void showThreeLinePoem(@Nullable String[] threeLinePoem) {
    if (threeLinePoem == null || threeLinePoem.length != 3) {
      threeLinePoem = StringUtil.split(
          getResources().getString(R.string.three_line_poem_default),
          getResources().getString(R.string.three_line_string_split));
    }
    for (int i=0; i<3; i++) {
      verticalTextViews[i].setText(threeLinePoem[i]);
    }
  }
}
