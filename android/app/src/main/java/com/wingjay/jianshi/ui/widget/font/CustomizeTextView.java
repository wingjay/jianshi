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
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomizeTextView extends TextView {

  public CustomizeTextView(Context context) {
    this(context, null);
  }

  public CustomizeTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initTypeFace();
  }

  private void initTypeFace() {
    if (FontFamilyFactory.getTypeface() != null) {
      setTypeface(FontFamilyFactory.getTypeface());
    }
  }

}
