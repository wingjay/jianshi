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
