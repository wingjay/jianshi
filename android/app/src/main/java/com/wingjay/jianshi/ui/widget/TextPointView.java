package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorRes;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.widget.font.FontFamilyFactory;
import com.wingjay.jianshi.util.DisplayUtil;

/**
 * One Text on the circle background.
 */
public class TextPointView extends RelativeLayout {

  private Context context;
  private static final int DEFAULT_SIZE_DP = 30;
  private static final int DEFAULT_TEXT_SIZE = 16;
  private @Px int size;
  private String singleText;
  private @Px int textSize;
  private @ColorRes int circleColorRes;

  public TextPointView(Context context) {
    this(context, null);
  }

  public TextPointView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TextPointView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    TypedArray typedArray = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.TextPointView, 0, 0);
    singleText = typedArray.getString(R.styleable.TextPointView_text);
    circleColorRes = typedArray.getInt(R.styleable.TextPointView_redPointViewBgColor, R.color.bright_red);
    textSize = typedArray.getDimensionPixelSize(R.styleable.TextPointView_textSize, DEFAULT_TEXT_SIZE);
    typedArray.recycle();

    View circleView = new View(context);
    circleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    ShapeDrawable circleShapeDrawable = new ShapeDrawable();
    circleShapeDrawable.setShape(new OvalShape());
    circleShapeDrawable.getPaint().setColor(ContextCompat.getColor(context, circleColorRes));
    circleView.setBackgroundDrawable(circleShapeDrawable);

    TextView textView = new TextView(context);
    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT);
    textView.setLayoutParams(params);
    textView.setTypeface(FontFamilyFactory.getTypeface());
    textView.setTextSize(textSize);
    textView.setTextColor(ContextCompat.getColor(context, R.color.white));
    textView.setText(singleText);

    addView(circleView);
    addView(textView);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
        || MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
      size = DisplayUtil.dip2px(context, DEFAULT_SIZE_DP);
    } else {
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);
      size = Math.min(width, height);
    }
    super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
  }

  public void setCircleBackgroundColor(@ColorRes int circleColorRes) {
    this.circleColorRes = circleColorRes;
    invalidate();
  }

  public void setSingleText(String text) {
    if (!TextUtils.isEmpty(text)) {
      this.singleText = text.substring(0, 1);
    } else {
      this.singleText = "";
    }
    invalidate();
  }

}
