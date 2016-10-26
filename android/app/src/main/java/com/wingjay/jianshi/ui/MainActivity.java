package com.wingjay.jianshi.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.floating.PoemService;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.screenshot.ScreenshotHelper;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.DatePickDialogFragment;
import com.wingjay.jianshi.ui.widget.DayChooser;
import com.wingjay.jianshi.ui.widget.DayPickDialogFragment;
import com.wingjay.jianshi.ui.widget.RedPointView;
import com.wingjay.jianshi.ui.widget.VerticalTextView;
import com.wingjay.jianshi.util.ConstantUtil;
import com.wingjay.jianshi.util.DateUtil;
import com.wingjay.jianshi.util.FullDateManager;
import com.wingjay.jianshi.util.UpgradeUtil;

import org.joda.time.DateTime;

import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements IWXAPIEventHandler {

  private final static String YEAR = "year";
  private final static String MONTH = "month";
  private final static String DAY = "day";

  @InjectView(R.id.year)
  VerticalTextView yearTextView;

  @InjectView(R.id.month)
  VerticalTextView monthTextView;

  @InjectView(R.id.day)
  VerticalTextView dayTextView;

  @InjectView(R.id.writer)
  RedPointView writerView;

  @InjectView(R.id.reader)
  RedPointView readerView;

  @InjectView(R.id.floating)
  RedPointView floatView;

  @InjectView(R.id.screenshot)
  RedPointView screenshotView;

  @InjectView(R.id.day_chooser)
  DayChooser dayChooser;

  @InjectView(R.id.screenshot_layout)
  RelativeLayout screenshotLayout;

  private volatile int year, month, day;

  private PoemService mService;
  private ServiceConnection mConnection;
  private Intent mServiceIntent;

  private static final String APP_ID = "wx565518a6df3fcacb";
  private IWXAPI mWechatApi;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    JianShiApplication.getAppComponent().inject(MainActivity.this);
    bindService();
    regToWx();

    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      year = savedInstanceState.getInt(YEAR);
      month = savedInstanceState.getInt(MONTH);
      day = savedInstanceState.getInt(DAY);
    } else {
      setTodayAsFullDate();
      UpgradeUtil.checkUpgrade(MainActivity.this);
    }
    updateFullDate();

    writerView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DateTime current = new DateTime(year, month, day, 0, 0);
            long dateSeconds = FullDateManager.getDateSeconds(current);
            Intent i = EditActivity.createIntent(MainActivity.this, dateSeconds);
            startActivity(i);
        }
    });

    readerView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, DiaryListActivity.class));
        }
    });

    dayChooser.setOnDayChooserClickListener(new DayChooser.OnDayChooserClickListener() {
        @Override
        public void onDayChoose(int chooseDay) {
            DayPickDialogFragment dayPickDialogFragment = new DayPickDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(DayPickDialogFragment.CHOOSE_DAY, chooseDay);
            bundle.putInt(DayPickDialogFragment.CHOOSE_MONTH, month);
            bundle.putInt(DayPickDialogFragment.CHOOSE_YEAR, year);
            dayPickDialogFragment.setArguments(bundle);
            dayPickDialogFragment.setOnDayChoosedListener(new DayPickDialogFragment.OnDayChoosedListener() {
                @Override
                public void onDayChoosed(DateTime chooseDate) {
                    setDate(chooseDate);
                    updateFullDate();
                }
            });
            dayPickDialogFragment.show(getSupportFragmentManager(), null);
        }
    });

    floatView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MainActivity.this.startService(mServiceIntent);
        if(mService != null){
          mService.show();
        }
      }
    });

    screenshotView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try{
          android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
          builder.setTitle("简诗");
          builder.setMessage("这首优美的小诗怎么不分享给其他人呢？");
          builder.setNegativeButton("朋友圈", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              try{
                ScreenshotHelper.screenshotShare(MainActivity.this,screenshotLayout,mWechatApi,true);
              }catch(Exception e){
                e.printStackTrace();
              }
            }
          });
          builder.setPositiveButton("好友", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              try{
                ScreenshotHelper.screenshotShare(MainActivity.this,screenshotLayout,mWechatApi,false);
              }catch(Exception e){
                e.printStackTrace();
              }
            }
          });
          builder.show();
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });
  }

  private void regToWx(){
    mWechatApi = WXAPIFactory.createWXAPI(this,APP_ID,true);
    mWechatApi.registerApp(APP_ID);
  }

  @OnClick(R.id.setting)
  void toSettingsPage(View v) {
    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
    startActivityForResult(intent, ConstantUtil.REQUEST_CODE_BG_COLOR_CHANGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ConstantUtil.REQUEST_CODE_BG_COLOR_CHANGE) {
        if (resultCode == RESULT_OK) {
            setContainerBgColorFromPrefs();
        }
    }
  }

  @OnClick(R.id.day)
  void chooseDay(View v) {
    showDatePickDialog(DatePickDialogFragment.PICK_TYPE_DAY);
  }

  @OnClick(R.id.month)
  void chooseMonth() {
    showDatePickDialog(DatePickDialogFragment.PICK_TYPE_MONTH);
  }

  private void showDatePickDialog(int pickType) {
    DatePickDialogFragment datePickDialogFragment = new DatePickDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(DatePickDialogFragment.CURRENT_DAY, day);
    bundle.putInt(DatePickDialogFragment.CURRENT_MONTH, month);
    bundle.putInt(DatePickDialogFragment.CURRENT_YEAR, year);
    bundle.putInt(DatePickDialogFragment.PICK_TYPE, pickType);
    datePickDialogFragment.setArguments(bundle);
    datePickDialogFragment.setOnDateChoosedListener(new DatePickDialogFragment.OnDateChoosedListener() {
        @Override
        public void onDayChoosed(int mDay) {
            day = mDay;
            updateFullDate();
        }

        @Override
        public void onMonthChoosed(int mMonth) {
            month = mMonth;
            if (!DateUtil.checkDayAndMonth(day, mMonth, year)) {
                day = DateUtil.getLastDay(mMonth, year);
            }
            updateFullDate();
        }
    });
    datePickDialogFragment.show(getSupportFragmentManager(), null);
  }

  private void setDate(DateTime date) {
    year = date.getYear();
    month = date.getMonthOfYear();
    day = date.getDayOfMonth();
  }

  private void setTodayAsFullDate() {
    DateTime currentDateTime = new DateTime();
    setDate(currentDateTime);
  }

  private void updateFullDate() {
    FullDateManager fullDateManager = new FullDateManager();
    yearTextView.setText(fullDateManager.getYear(year));
    monthTextView.setText(fullDateManager.getMonth(month));
    dayTextView.setText(fullDateManager.getDay(day));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(YEAR, year);
    outState.putInt(MONTH, month);
    outState.putInt(DAY, day);
    super.onSaveInstanceState(outState);
  }

  public static Intent createIntent(Context context) {
    Intent intent = new Intent(context, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_NO_ANIMATION);
    return intent;
  }

  private void bindService(){
    mServiceIntent = new Intent(this,PoemService.class);
    if(mConnection == null){
      mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          mService = ((PoemService.PoemFloatWindowBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
      };

      this.bindService(mServiceIntent,mConnection,this.BIND_AUTO_CREATE);
    }
  }

  private void unbindService(){
    if(null != mConnection){
      this.unbindService(mConnection);
      mConnection = null;
    }
  }

  @Override
  public void onDestroy(){
    unbindService();
    super.onDestroy();
  }

  @Override
  public void onPause(){
    unbindService();
    super.onPause();
  }

  @Override
  public void onStop(){
    unbindService();
    super.onStop();
  }

  @Override
  public void onResume(){
    bindService();
    super.onResume();
  }

  @Override
  public void onReq(BaseReq baseReq) {

  }

  @Override
  public void onResp(BaseResp resp) {
    int result = 0;

    switch (resp.errCode) {
      case BaseResp.ErrCode.ERR_OK:
        result = R.string.errcode_success;
        break;
      case BaseResp.ErrCode.ERR_USER_CANCEL:
        result = R.string.errcode_cancel;
        break;
      case BaseResp.ErrCode.ERR_AUTH_DENIED:
        result = R.string.errcode_deny;
        break;
      default:
        result = R.string.errcode_unknown;
        break;
    }

    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
  }
}
