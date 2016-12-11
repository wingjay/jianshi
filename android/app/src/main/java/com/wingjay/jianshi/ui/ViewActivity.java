/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.ShareContent;
import com.wingjay.jianshi.db.service.DiaryService;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.log.Blaster;
import com.wingjay.jianshi.log.LoggingData;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.MultipleRowTextView;
import com.wingjay.jianshi.ui.widget.TextPointView;
import com.wingjay.jianshi.util.DisplayUtil;
import com.wingjay.jianshi.util.IntentUtil;
import com.wingjay.jianshi.util.LanguageUtil;
import com.wingjay.jianshi.manager.ScreenshotManager;

import java.io.File;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ViewActivity extends BaseActivity {

  @InjectView(R.id.view_edit)
  TextPointView edit;

  @InjectView(R.id.hori_container)
  ScrollView verticalScrollView;

  @InjectView(R.id.view_title)
  TextView horizTitle;

  @InjectView(R.id.view_content)
  TextView horizContent;

  @InjectView(R.id.vertical_container)
  HorizontalScrollView horizontalScrollView;

  @InjectView(R.id.vertical_view_title)
  MultipleRowTextView verticalTitle;

  @InjectView(R.id.vertical_view_content)
  MultipleRowTextView verticalContent;

  @InjectView(R.id.container)
  View container;

  @InjectView(R.id.normal_container)
  View normalContainer;

  @InjectView(R.id.bottom_container)
  View bottomContainer;

  @InjectView(R.id.vertical_view_date)
  MultipleRowTextView verticalDate;

  private String diaryUuid;
  private boolean verticalStyle = false;

  @Inject
  DiaryService diaryService;

  @Inject
  UserPrefs userPrefs;

  @Inject
  UserService userService;

  @Inject
  ScreenshotManager screenshotManager;

  @OnClick(R.id.view_share)
  void share() {
    Blaster.log(LoggingData.BTN_CLK_SHARE_DIARY_IMAGE);

    final View target = verticalStyle ? container : normalContainer;
    final ProgressDialog dialog = ProgressDialog.show(this, "", "加载中...");
    screenshotManager.shotScreen(target, "temp.jpg")
        .observeOn(Schedulers.io())
        .filter(new Func1<String, Boolean>() {
          @Override
          public Boolean call(String s) {
            return !TextUtils.isEmpty(s);
          }
        })
        .flatMap(new Func1<String, Observable<Pair<String, ShareContent>>>() {
          @Override
          public Observable<Pair<String, ShareContent>> call(String path) {
            Timber.i("ViewActivity ScreenshotManager 1 %s", Thread.currentThread().getName());
            ShareContent shareContent = new ShareContent();
            try {
              JsonDataResponse<ShareContent> response = userService.getShareContent().toBlocking().first();
              if (response.getRc() == Constants.ServerResultCode.RESULT_OK && response.getData() != null) {
                shareContent = response.getData();
              }
            } catch (Exception e) {
              Timber.e(e, "getShareContent() error");
              return Observable.just(Pair.create(path, shareContent));
            }
            return Observable.just(Pair.create(path, shareContent));
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate(new Action0() {
          @Override
          public void call() {
            dialog.dismiss();
          }
        })
        .subscribe(new Action1<Pair<String, ShareContent>>() {
          @Override
          public void call(Pair<String, ShareContent> stringShareContentPair) {
            Timber.i("ViewActivity ScreenshotManager 2 %s", Thread.currentThread().getName());
            if (!isUISafe()) {
              return;
            }
            IntentUtil.shareLinkWithImage(ViewActivity.this,
                stringShareContentPair.second,
                Uri.fromFile(new File(stringShareContentPair.first)));
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            makeToast(getString(R.string.share_failure));
            Timber.e(throwable, "screenshot share failure");
          }
        });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view);
    JianShiApplication.getAppComponent().inject(this);
    UserPrefs userPrefs = new UserPrefs(ViewActivity.this);
    verticalStyle = userPrefs.getVerticalWrite();
    setVisibilityByVerticalStyle();

    diaryUuid = getIntent().getStringExtra(EditActivity.DIARY_UUID);
    if (diaryUuid == null) {
      finish();
    }

    loadDiary();

    edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(RESULT_OK);
        Blaster.log(LoggingData.BTN_CLK_UPDATE_DIARY);
        Intent i = EditActivity.createIntentWithId(ViewActivity.this, diaryUuid);
        startActivity(i);
        finish();
      }
    });
    Timber.i("contentWidth : %s", container.getWidth());
    Blaster.log(LoggingData.PAGE_IMP_VIEW);
  }

  private void loadDiary() {
    diaryService.getDiaryByUuid(diaryUuid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<com.wingjay.jianshi.db.model.Diary>() {
          @Override
          public void call(com.wingjay.jianshi.db.model.Diary diary) {
            if (diary != null) {
              showDiary(
                  diary.getTitle(),
                  diary.getContent(),
                  LanguageUtil.getDiaryDateEnder(
                      getApplicationContext(),
                      diary.getTime_created()));
            }
          }
        });
  }

  private void showDiary(String titleString, String contentString, String contentDate) {
    setVisibilityByVerticalStyle();

    if (verticalStyle) {
      verticalTitle.setText(titleString);
      verticalContent.setText(contentString);
      verticalDate.setText(contentDate);
      container.setBackgroundResource(userPrefs.getBackgroundColor());
      container.post(new Runnable() {
        @Override
        public void run() {
          int scrollOffsetX = container.getWidth() - DisplayUtil.getDisplayWidth();
          Timber.i("contentWidthAfter : %s", container.getMeasuredHeight());
          if (scrollOffsetX > 0) {
            horizontalScrollView.scrollBy(scrollOffsetX, 0);
          }
        }
      });
    } else {
      normalContainer.setBackgroundResource(userPrefs.getBackgroundColor());
      horizTitle.setText(titleString);
      horizContent.setText(contentString + getString(R.string.space_of_date_record_end) + contentDate);
    }
  }

  private void setVisibilityByVerticalStyle() {
    verticalScrollView.setVisibility(verticalStyle ? View.GONE : View.VISIBLE);
    horizontalScrollView.setVisibility(verticalStyle ? View.VISIBLE : View.GONE);
  }

  public static Intent createIntent(Context context, String diaryId) {
    Intent i = new Intent(context, ViewActivity.class);
    i.putExtra(EditActivity.DIARY_UUID, diaryId);
    return i;
  }

}
