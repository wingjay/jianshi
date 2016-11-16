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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.db.service.DiaryService;
import com.wingjay.jianshi.global.JianShiApplication;
import com.wingjay.jianshi.log.Blaster;
import com.wingjay.jianshi.log.LoggingData;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.util.DateUtil;
import com.wingjay.jianshi.util.StringByTime;

import java.util.UUID;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EditActivity extends BaseActivity {

  public final static String DIARY_UUID = "diary_uuid";

  @InjectView(R.id.edit_title)
  EditText title;

  @InjectView(R.id.edit_content)
  EditText content;

  @InjectView(R.id.edit_save)
  View save;

  @InjectView(R.id.edit_scroll_view)
  ScrollView scrollView;

  private String diaryUUID;

  private boolean unchanged = true;
  private Diary diary;
  private String originContent, originTitle;
  @Inject
  DiaryService diaryService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit);
    JianShiApplication.getAppComponent().inject(this);
    final Intent intent = getIntent();
    diaryUUID = intent.getStringExtra(DIARY_UUID);

    if (diaryUUID != null) {
      loadDiary();
    }
    //manually save
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Blaster.log(LoggingData.BTN_CLK_SAVE_DIARY);
        saveDiary();
      }
    });

    SimpleTextWatcher textWatcher = new SimpleTextWatcher() {
      @Override
      public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        unchanged = false;
      }
    };

    title.addTextChangedListener(textWatcher);
    content.addTextChangedListener(textWatcher);

    scrollView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (content.requestFocus()) {
          InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.showSoftInput(content, InputMethodManager.SHOW_IMPLICIT);
        }
      }
    });

    title.setHint(StringByTime.getEditTitleHintByNow());
    content.setHint(StringByTime.getEditContentHintByNow());
    Blaster.log(LoggingData.PAGE_IMP_WRITE);
  }

  private void saveDiary() {
    if (!checkNotNull()) {
      Toast.makeText(EditActivity.this, R.string.edit_content_not_null,
          Toast.LENGTH_SHORT).show();
      return;
    }

    String titleString = (TextUtils.isEmpty(title.getText().toString()))
        ? title.getHint().toString() : title.getText().toString();
    String contentString = (TextUtils.isEmpty(content.getText().toString()))
        ? content.getHint().toString() : content.getText().toString();

    if (diary == null) {
      diary = new Diary();
      diary.setTitle(titleString);
      diary.setContent(contentString);
      diary.setUuid(UUID.randomUUID().toString().toUpperCase());
      diary.setTime_created(DateUtil.getCurrentTimeStamp());
    }else {
      diary.setTitle(titleString);
      diary.setContent(contentString);
      diary.setTime_modified(DateUtil.getCurrentTimeStamp());
    }
    diaryService.saveDiary(diary)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Void>() {
          @Override
          public void call(Void aVoid) {
            Intent i = ViewActivity.createIntent(EditActivity.this, diary.getUuid());
            startActivity(i);
            finish();
          }
        });
  }

  private boolean checkNotNull() {
    return !TextUtils.isEmpty(title.getText()) || !TextUtils.isEmpty(content.getText());
  }

  private void loadDiary() {
    diaryService.getDiaryByUuid(diaryUUID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Diary>() {
          @Override
          public void call(Diary diary) {
            if (diary != null) {
              EditActivity.this.diary = diary;
              originContent = diary.getContent();
              originTitle = diary.getTitle();
              title.setText(diary.getTitle());
              content.setText(diary.getContent());
            }
          }
        });
  }

  @Override
  public void onBackPressed() {
    boolean unchangedFlag = (TextUtils.equals(originContent, content.getText().toString().trim())
        && TextUtils.equals(originTitle, title.getText().toString().trim()))
        || unchanged;
    if (unchangedFlag) {
      super.onBackPressed();
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
      builder.setTitle(R.string.want_to_save)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              saveDiary();
            }
          })
          .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
          })
          .create()
          .show();

    }

  }

  public class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }

  public static Intent createIntentWithId(Context context, String diaryId) {
    Intent i = new Intent(context, EditActivity.class);
    i.putExtra(DIARY_UUID, diaryId);
    return i;
  }
}
