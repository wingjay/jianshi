package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.util.StringByTime;
import com.wingjay.jianshi.util.StringUtil;

/**
 * Display a three-line poem.
 */
public class ThreeLinePoemView extends FrameLayout {

  private VerticalTextView[] verticalTextViews;
  private String[] threeLinePoem;

  public ThreeLinePoemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.view_three_line_poem, this);

    verticalTextViews = new VerticalTextView[3];
    verticalTextViews[0] = (VerticalTextView) findViewById(R.id.first_line);
    verticalTextViews[1] = (VerticalTextView) findViewById(R.id.second_line);
    verticalTextViews[2] = (VerticalTextView) findViewById(R.id.last_line);

    threeLinePoem = StringByTime.getThreeLinePoemArrayByNow();

    showThreeLinePoem();
  }

  private void showThreeLinePoem() {
    if (threeLinePoem == null || threeLinePoem.length != 3) {
      setDefaultPoem();
    }
    for (int i=0; i<3; i++) {
      verticalTextViews[i].setText(threeLinePoem[i]);
    }
  }

  private void setDefaultPoem() {
    threeLinePoem = StringUtil.split(
        getResources().getString(R.string.three_line_poem_default),
        getResources().getString(R.string.three_line_string_split));

  }

}
