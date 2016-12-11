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
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.util.DisplayUtil;

public class VerticalTextView extends TextView {

  public VerticalTextView(Context context) {
    this(context, null);
  }

  public VerticalTextView(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
  }

  public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray typedArray = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.VerticalTextView, 0, 0);
    try {
      float textSizePixel = typedArray.getDimension(R.styleable.VerticalTextView_verticalTextSize,
          getResources().getDimension(R.dimen.normal_text_size));
      int textSizeSp = DisplayUtil.px2sp(context, textSizePixel);
      setTextSize(textSizeSp);
    } finally {
      typedArray.recycle();
    }
  }

  @Override
  public void setText(CharSequence text, BufferType type) {
    if (TextUtils.isEmpty(text)) {
      super.setText(text, type);
      return;
    }
    StringBuffer stringBuffer = new StringBuffer();
    int length = text.length();
    for (int i=0; i<length; i++) {
      CharSequence sequence = text.toString().subSequence(i, i+1);
      stringBuffer.append(sequence);
      if (i < length - 1) {
        stringBuffer.append("\n");
      }
    }
    super.setText(stringBuffer, type);
  }
}
