package com.wingjay.jianshi.floating.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by cpb on 2016/7/1.
 */

public class FloatWindow {

  private Context mContext;
  private WindowManager.LayoutParams mLayoutParams;
  private WindowManager mWindowManager;
  private DisplayMetrics mDisplayMetrics;
  private View mPlayerView,mFloatView;
  private View mContentView;

  private boolean mIsOpen = false;//是否开启菜单页
  private boolean mIsShowing = false;//contentView是否显示
  private float mOldX;
  private float mOldY;
  private int mOffsetX;
  private int mOffsetY;
  private float mDownX;
  private float mDownY;
  private float mDownXInView;
  private float mDownYInView;
  private long mDownTimeMillis;
  private long mLastTouchTimeMillis;

  private float mParamsX;
  private float mParamsY;

  private final int WHAT_HIDE = 1;
  private final float DISTANCE = 15.0f;//在这个范围内的点击事件都可以归为对控件的点击

  public FloatWindow(Context context){
    this(context,null,null);
  }

  public FloatWindow(Context context, View playerView,View floatView){
    mContext = context;
    setPlayerView(playerView);
    setFloatView(floatView);
    initWindowManager();
    initLayoutParams();
  }

  /**
   * 将playerView设置到backgroundLayout中，从而获得backgroundLayout中的关于返回键等MotionEvent的处理情况
   * 换句话说，backgroundLayout专门处理playerView的触摸点击事件
   * 而WindowTouchListener专门处理floatView的触摸点击事件
   * @param playerView
   */
  public void setPlayerView(View playerView){
    if(null != playerView){
      //BackgroundLayout中处理返回键事务，以及捕捉event中的UP和OUTSIDE，这些都会关掉playerView
      BackgroundLayout backgroundLayout = new BackgroundLayout(getContext());
      RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
          (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
      lp.addRule(RelativeLayout.CENTER_IN_PARENT);

      //设置监听事件，TouchIntercept只是在原来OnTouchListener的基础上在event.MotionEvent.UP事件中记录下lastTouchTimeMillis
      playerView.setOnTouchListener(new TouchIntercept());
      playerView.setLayoutParams(lp);

      //将playerView菜单项寄托到backgroundLayout中，因此就具有了backgroundLayout的点击事件响应
      backgroundLayout.addView(playerView);

            /*
            注意，这里设置的是backgroundLayout而不是playerView，如果设置playerView，
            则在设置contentView将playerView添加到contentView中时，会报错，因为
            contentView已经有一个parent（BackgroundLayout），不能再添加到其他布局，
            除非parent添加到其他布局
             */
      this.mPlayerView = backgroundLayout;
    }
  }

  /**
   * 设置floatView，并且展示出来
   * @param floatView
   */
  public void setFloatView(View floatView){
    if(null != floatView){
      this.mFloatView = floatView;
      setContentView(floatView);
    }
  }

  public WindowManager.LayoutParams getWLayoutParams(){
    if(mLayoutParams == null){
      mLayoutParams = new WindowManager.LayoutParams();
      initLayoutParams();
    }
    return mLayoutParams;
  }

  public void showNewsPlayer(){
    if(mIsOpen)
      return;

    getWLayoutParams().flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    getWLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
    getWLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
    mOldX = getWLayoutParams().x;
    mOldY = getWLayoutParams().y;
    setContentView(mPlayerView);
    mHandler.removeMessages(WHAT_HIDE);
    mIsOpen = true;
  }

  public void show(){
    if(getContentView() != null && !isShowing()){
      getWindowManager().addView(getContentView(),getWLayoutParams());
      mIsShowing = true;
      if(!mIsOpen){
        mHandler.sendEmptyMessage(WHAT_HIDE);
      }
    }else if(getContentView() == null){
      createContentView(mFloatView);
      getWindowManager().addView(getContentView(),getWLayoutParams());
      mIsShowing = true;
      if(!mIsOpen){
        mHandler.sendEmptyMessage(WHAT_HIDE);
      }
    }
  }

  public void dimiss(){
    if(getContentView()!=null && isShowing()){
      getWindowManager().removeView(getContentView());
      mIsShowing = false;
    }
  }

  /******************************************内部实现*********************************************************/

  /**
   * 只是用于更新alpha
   * 不管如何到最后都是使floatView接近透明，只是在点击的时候变亮一段时间
   * 延迟时间是3.5s
   */
  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg){
      switch(msg.what){
        case WHAT_HIDE:
          if(System.currentTimeMillis() - mDownTimeMillis > 3500){
            if(!mIsOpen && getContentView() != null){
              getWLayoutParams().alpha = 0.4f;//不透明度是0.4f
              getWindowManager().updateViewLayout(getContentView(),getWLayoutParams());
            }
          }else{
            if(!mIsOpen){
              mLastTouchTimeMillis = System.currentTimeMillis() + 3500;
            }
            mHandler.sendEmptyMessageDelayed(WHAT_HIDE,200);
          }
      }
    }
  };

  private Context getContext(){
    return mContext;
  }

  private void initWindowManager(){
    mWindowManager = (WindowManager)getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    mDisplayMetrics = new DisplayMetrics();
    mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
  }

  private WindowManager getWindowManager(){
    if(mWindowManager == null){
      mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
    }
    return mWindowManager;
  }

  private DisplayMetrics getDisplayMetrics(){
    if(mDisplayMetrics == null){
      mDisplayMetrics = getContext().getResources().getDisplayMetrics();
    }
    return mDisplayMetrics;
  }

  private void initLayoutParams() {
    getWLayoutParams().flags = getWLayoutParams().flags
        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH//在设置FLAG_NOT_TOUCH_MODAL的前提下，该窗口以MotionEvent.ACTION_OUTSIDE的形式接受事件，不过事件仍然会传递到其他窗口中
        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//任何该窗口之外的event事件都会发送到其余窗口

    mLayoutParams.dimAmount = 0.2f;//用于设置后面的窗口变暗程度 1.0为完全不透明，0.0表示完全透明
    mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
    mLayoutParams.gravity = Gravity.START|Gravity.TOP;
    mLayoutParams.format = PixelFormat.RGBA_8888;
    mLayoutParams.alpha = 1.0f;//整个窗口的透明程度

    mOffsetX = 0;
    mOffsetY = getStatusBarHeight(getContext());
    mLayoutParams.x = (getDisplayMetrics().widthPixels - mOffsetX);
    mLayoutParams.y = (getDisplayMetrics().heightPixels / 4 - mOffsetY);
  }

  private int getStatusBarHeight(Context context){
    int height = 0;
    int resId = context.getResources().getIdentifier("status_bar_height","dimen","android");
    if(resId > 0){
      height = context.getResources().getDimensionPixelSize(resId);
    }
    return height;
  }

  /**
   * 用于监听playerView，在监听事件时顺便在MotionEvent.ACTION_UP的触碰事件发生时记录最后触摸的时间
   */

  class TouchIntercept implements View.OnTouchListener{
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch(event.getAction()){
        case MotionEvent.ACTION_UP:
          mLastTouchTimeMillis = System.currentTimeMillis();
          break;
        default:
          break;
      }
      return true;
    }
  }

  /**
   * 处理在按下返回键时采取的行为（只针对playerView）
   */
  class BackgroundLayout extends RelativeLayout{

    public BackgroundLayout(Context context){
      super(context);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){//dispatchKeyEvent()函数在onTouchEvent()前调用
      if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
        if(getKeyDispatcherState() == null){
          return super.dispatchKeyEvent(event);
        }

        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
          final KeyEvent.DispatcherState state = getKeyDispatcherState();
          if(state != null){
            state.startTracking(event,this);//跟踪这个事件
          }
          return true;
        }else if(event.getAction() == KeyEvent.ACTION_UP){
          final KeyEvent.DispatcherState state = getKeyDispatcherState();
          if(state != null && state.isTracking(event) && !event.isCanceled()){
            turnoffMenu();//当发现跟踪的事件最后有ACTION_UP的行为产生，则关掉菜单界面
          }
          return true;
        }
      }else{
        return super.dispatchKeyEvent(event);
      }

      return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){//UP和OUTSIDE会关掉菜单界面
      switch(event.getAction()){
        case MotionEvent.ACTION_UP:
          turnoffMenu();
          return true;
        case MotionEvent.ACTION_OUTSIDE:
          turnoffMenu();
          return true;
        default:
          return super.onTouchEvent(event);
      }
    }
  }

  private void setContentView(View contentView){
    if(contentView == null)
      return;

    if(isShowing()){
      getWindowManager().removeView(mContentView);
      createContentView(contentView);
      getWindowManager().addView(mContentView,getWLayoutParams());
      updateLocation(getDisplayMetrics().widthPixels/2,getDisplayMetrics().heightPixels/2,true);
    }else{
      createContentView(contentView);
    }
  }

  private boolean isShowing(){
    return mIsShowing;
  }

  private void createContentView(View view){
    mContentView = view;
    mContentView.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
    mOffsetY = mContentView.getMeasuredHeight() / 2 + getStatusBarHeight(getContext());
    mOffsetX = mContentView.getMeasuredWidth() / 2;
    mContentView.setOnTouchListener(new WindowTouchListener());
  }

  private View getContentView(){
    return mContentView;
  }

  protected void turnoffMenu(){
    if(!mIsOpen) {
      return;
    }

    mIsOpen = false;
    getWLayoutParams().flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    getWLayoutParams().flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//取消后重新设置的原因是防止重复设置同一属性。
    getWLayoutParams().height = WindowManager.LayoutParams.WRAP_CONTENT;
    getWLayoutParams().width = WindowManager.LayoutParams.WRAP_CONTENT;
    setContentView(mFloatView);
    getWLayoutParams().alpha = 1.0f;
    updateLocation(mOldX,mOldY,false);
    mLastTouchTimeMillis = System.currentTimeMillis();
    mHandler.sendEmptyMessage(WHAT_HIDE);
  }

  /**
   * 在按下的时候使得floatView的颜色变深，不会像普通状态下的半透明
   * @param event
   */
  private void down(MotionEvent event){
    mDownX = event.getRawX();
    mDownY = event.getRawY();

    //mDownXInView = event.getX();
    //mDownYInView = event.getY();

    mDownTimeMillis = System.currentTimeMillis();
    mLastTouchTimeMillis = System.currentTimeMillis();
    getWLayoutParams().alpha = 1.0f;
    getWindowManager().updateViewLayout(getContentView(),getWLayoutParams());
  }

  private void move(MotionEvent event){
    mLastTouchTimeMillis = System.currentTimeMillis();
    updateLocation(event.getRawX(),event.getRawY(),true);
  }

  private void up(MotionEvent event) {
    float x = event.getRawX();
    float y = event.getRawY();
    float xInView = event.getX();
    float yInView = event.getY();

    if(x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
        && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE) {
      if (System.currentTimeMillis() - mDownTimeMillis > 1200) {
        Toast.makeText(getContext(),"长按",Toast.LENGTH_SHORT).show();
        launchTheFloatingWindow(x,y,xInView,yInView);
      } else {
        showNewsPlayer();
      }
    }
    else{
      ValueAnimator animator = alignAnimator(x,y);
      animator.start();
    }
  }

  private void launchTheFloatingWindow(float rawX,float rawY,float xInView,float yInView){
    mParamsX = rawX - xInView;
    mParamsY = rawY - yInView;

    new LaunchTask().execute();
  }

  class LaunchTask extends AsyncTask<Void,Void,Void>{
    @Override
    protected Void doInBackground(Void... params) {
      while(mParamsY > 0){
        mParamsY -= 20;
        //AsyncTask中要在doInBackground中加入publishProcess()
        // 才会调用onProcessUpdate()
        publishProgress();
        try{
          Thread.sleep(8);
        }catch(Exception e){
          e.printStackTrace();
        }
      }
      return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
      updateLocation(mParamsX,mParamsY,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      mWindowManager.removeView(mFloatView);
      mContentView = null;
    }
  }

  private void updateLocation(float x,float y,boolean isOffset){
    if(mContentView == null){
      return;
    }

    if(isOffset){
      getWLayoutParams().x = (int)x - mOffsetX;
      getWLayoutParams().y = (int)y - mOffsetY;
    }else{
      getWLayoutParams().x = (int)x;
      getWLayoutParams().y = (int)y;
    }
    getWindowManager().updateViewLayout(getContentView(),getWLayoutParams());
  }

  private ValueAnimator alignAnimator(float x,float y){
    ValueAnimator animator = null;
    if(x < getDisplayMetrics().widthPixels/2){
      animator = ValueAnimator.ofObject(new PointEvaluator(),new Point((int)x,(int)y),new Point(0,(int)y));
    }else{
      animator = ValueAnimator.ofObject(new PointEvaluator(),new Point((int)x,(int)y),new Point(getDisplayMetrics().widthPixels,(int)y));
    }
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        Point point = (Point)animation.getAnimatedValue();
        updateLocation(point.x,point.y,true);
      }
    });
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animator){
        super.onAnimationEnd(animator);
        mLastTouchTimeMillis = System.currentTimeMillis();
        mHandler.sendEmptyMessage(WHAT_HIDE);
      }
    });

    animator.setDuration(60);
    return animator;
  }

  /**
   * 动画类，使得playerView转化到floatView时坐标的变化变得更加平滑
   */
  class PointEvaluator implements TypeEvaluator{
    @Override
    public Point evaluate(float fraction, Object from, Object to) {
      Point start = (Point)from;
      Point end = (Point)to;

      int x = (int)(start.x + fraction * ( end.x - start.x ));
      int y = (int)(start.y + fraction * ( end.y - start.y ));

      return new Point(x,y);
    }
  }


  /**
   * 用于专门处理floatView悬浮窗状态下的MotionEvent事件,
   * 但是因为WindowTouchListener是设置到contentView中，
   * 因此在事件判断中都要加入if(!mIsOpen)来确定只对floatView状态的点击事件进行响应
   */
  class WindowTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch(event.getAction()){
        case MotionEvent.ACTION_DOWN://在float悬浮窗状态下记录当前按下坐标和时间
          if(!mIsOpen){
            down(event);
          }
          break;
        case MotionEvent.ACTION_MOVE://在float悬浮窗状态下移动悬浮窗
          if(!mIsOpen){
            move(event);
          }
          break;
        case MotionEvent.ACTION_UP://在float悬浮窗状态下如果点击图标则弹出menu，否则则靠着边沿。
          if(!mIsOpen){
            up(event);
          }
          break;
        case MotionEvent.ACTION_OUTSIDE://点击任意外面的地方都会使得menu变为float悬浮窗状态。
          if(mIsOpen){
            turnoffMenu();
          }
          break;
        default:
          break;
      }
      return false;
    }
  }
}
