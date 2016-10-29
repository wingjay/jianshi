package com.wingjay.jianshi.ui.widget.font;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;


public class CustomizeEditText extends EditText {

  public CustomizeEditText(Context context) {
    super(context);
    Log.d("jaydebug", "1");
    initTypeFace();
  }

  //can not call this(context, attrs, 0), 0 should be com.android.internal.R.attr.editTextStyle
  public CustomizeEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    Log.d("jaydebug", "2");
    initTypeFace();
  }

  public CustomizeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    Log.d("jaydebug", "3");
    initTypeFace();
  }

  private void initTypeFace() {
    if (FontFamilyFactory.getTypeface() != null) {
      setTypeface(FontFamilyFactory.getTypeface());
    }
  }

}
