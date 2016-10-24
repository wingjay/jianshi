package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.widget.font.FontFamilyFactory;
import com.wingjay.jianshi.util.DisplayUtil;

/**
 * Created by wingjay on 10/4/15.
 */
public class MultipleRowTextView extends View {

  public static final int LAYOUT_CHANGED = 1;
  private Paint paint;
  private int mTextPosx = 0;// x坐标
  private int mTextPosy = 0;// y坐标
  private int mTextWidth = 0;// 绘制宽度
  private int mTextHeight = 0;// 绘制高度
  private int mFontHeight = 0;// 绘制字体高度
  private float mFontSize = 24;// 字体大小
  private int mRealLine = 0;// 字符串真实的行数
  private int mLineWidth = 0;//列宽度
  private int TextLength = 0 ;//字符串长度
  private int oldwidth = 0 ;//存储久的width
  private String text="";//待显示的文字
  private Handler mHandler=null;
  private Matrix matrix;
  BitmapDrawable drawable = (BitmapDrawable) getBackground();

  public MultipleRowTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public MultipleRowTextView(Context context,AttributeSet attrs) {
    super(context, attrs);
    matrix = new Matrix();
    paint = new Paint();//新建画笔
    paint.setTextAlign(Align.CENTER);//文字居中
    paint.setAntiAlias(true);//平滑处理
    paint.setColor(Color.BLACK);//默认文字颜色
    TypedArray typedArray = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.MultipleRowTextView, 0, 0);
    try {
      float textSizePixel = typedArray.getDimension(
          R.styleable.MultipleRowTextView_multiRowTextSize,
          getResources().getDimension(R.dimen.normal_text_size));
      int textSizeSp = DisplayUtil.px2sp(context, textSizePixel);
      mFontSize = DisplayUtil.sp2px(context, textSizeSp);

      boolean bold = typedArray.getBoolean(R.styleable.MultipleRowTextView_multiRowTextBold, false);
      if (bold) {
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
      }
    } finally {
      typedArray.recycle();
    }
    Typeface typeface = Typeface.createFromAsset(context.getAssets(),
        "fonts/" + FontFamilyFactory.getDefaultFontFamily());
    setTypeface(typeface);
  }

  //设置文字
  public final void setText(String text) {
    this.text=text;
    this.TextLength = text.length();
    if(mTextHeight>0) {
      GetTextInfo();
    }
  }
  //设置字体大小
  public final void setTextSize(float size) {
    if (size != paint.getTextSize()) {
      mFontSize = size;
      if(mTextHeight>0) {
        GetTextInfo();
      }
    }
  }
  //设置字体颜色
  public final void setTextColor(int color) {
    paint.setColor(color);
  }
  //设置字体颜色
  public final void setTextARGB(int a,int r,int g,int b) {
    paint.setARGB(a, r, g, b);
  }
  //设置字体
  public void setTypeface(Typeface tf) {
    if (this.paint.getTypeface() != tf) {
      this.paint.setTypeface(tf);
    }
  }
  //设置行宽
  public void setLineWidth(int LineWidth) {
    mLineWidth = LineWidth;
  }
  //获取实际宽度
  public int getTextWidth() {
    return mTextWidth;
  }
  //设置Handler，用以发送事件
  public void setHandler(Handler handler) {
    mHandler=handler;
  }
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.v("TextViewVertical","onDraw");
    if(drawable!=null){
      //画背景
      Bitmap b = Bitmap.createBitmap(drawable.getBitmap(),0,0,mTextWidth,mTextHeight);
      canvas.drawBitmap(b, matrix, paint);
    }
    //画字
    draw(canvas, this.text);
  }
  private void draw(Canvas canvas, String thetext) {
    char ch;
    mTextPosy = 0;//初始化y坐标
    mTextPosx = mTextWidth - mLineWidth;//初始化x坐标
    for (int i = 0; i < this.TextLength; i++) {
      ch = thetext.charAt(i);
      if (ch == '\n') {
        mTextPosx -= mLineWidth;// 换列
        mTextPosy = 0;
      } else {
        mTextPosy += mFontHeight;
        if (mTextPosy > this.mTextHeight) {
          mTextPosx -= mLineWidth;// 换列
          i--;
          mTextPosy = 0;
        }else{
          canvas.drawText(String.valueOf(ch), mTextPosx, mTextPosy, paint);
        }
      }
    }

    //调用接口方法
    //activity.getHandler().sendEmptyMessage(TestFontActivity.UPDATE);
  }
  //计算文字行数和总宽
  private void GetTextInfo() {
    Log.v("TextViewVertical","GetTextInfo");
    char ch;
    int h = 0;
    paint.setTextSize(mFontSize);
    //获得字宽
    if(mLineWidth==0){
      float[] widths = new float[1];
      paint.getTextWidths("正", widths);//获取单个汉字的宽度
      float[] space = new float[1];
      paint.getTextWidths(" ", space);
      mLineWidth = (int) Math.ceil( (widths[0] + space[0]) * 1.1 + 2 );
    }

    FontMetrics fm = paint.getFontMetrics();
    mFontHeight = (int) (Math.ceil(fm.descent - fm.top) * 0.9);// 获得字体高度
    //计算文字行数
    mRealLine=0;
    for (int i = 0; i < this.TextLength; i++) {
      ch = this.text.charAt(i);
      if (ch == '\n') {
        mRealLine++;// 真实的行数加一
        h = 0;
      } else {
        h += mFontHeight;
        if (h > this.mTextHeight) {
          mRealLine++;// 真实的行数加一
          i--;
          h = 0;
        } else {
          if (i == this.TextLength - 1) {
            mRealLine++;// 真实的行数加一
          }
        }
      }
    }
    mRealLine++;//额外增加一行
    mTextWidth = mLineWidth * mRealLine;//计算文字总宽度
    measure(mTextWidth, getHeight());//重新调整大小
    layout(getLeft(), getTop(), getLeft()+mTextWidth, getBottom());//重新绘制容器
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int measuredHeight = measureHeight(heightMeasureSpec);
    //int measuredWidth = measureWidth(widthMeasureSpec);
    if(mTextWidth==0) {
      GetTextInfo();
    }
    setMeasuredDimension(mTextWidth, measuredHeight);
    if(oldwidth!=getWidth()) {//
      oldwidth=getWidth();
      if(mHandler!=null) {
        mHandler.sendEmptyMessage(LAYOUT_CHANGED);
      }
    }
  }

  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    int result = 500;
    if (specMode == MeasureSpec.AT_MOST){
      result = specSize;
    }else if (specMode == MeasureSpec.EXACTLY){
      result = specSize;
    }
    mTextHeight = result;//设置文本高度
    return result;
  }
    	/*
	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = 500;
		if (specMode == MeasureSpec.AT_MOST){
			result = specSize;
		}else if (specMode == MeasureSpec.EXACTLY){
			result = specSize;
		}
		return result;
	}  */

}
