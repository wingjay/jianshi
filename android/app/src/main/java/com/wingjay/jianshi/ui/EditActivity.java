package com.wingjay.jianshi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import com.wingjay.jianshi.data.Diary;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.util.LanguageUtil;
import com.wingjay.jianshi.util.StringByTime;

import butterknife.InjectView;

public class EditActivity extends BaseActivity {

    public final static String DATE_TIME = "datetime";
    public final static String DIARY_ID = "diaryId";

    @InjectView(R.id.edit_title)
    EditText title;

    @InjectView(R.id.edit_content)
    EditText content;

    @InjectView(R.id.edit_save)
    View save;

    @InjectView(R.id.edit_scroll_view)
    ScrollView scrollView;

    private long dateSeconds;
    private long diaryId;

    private boolean unchanged = true;
    private String originTitle, originContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final Intent intent = getIntent();
        dateSeconds = intent.getLongExtra(DATE_TIME, 0);
        diaryId = intent.getLongExtra(DIARY_ID, 0);
        if (dateSeconds == 0 && (diaryId == 0)) {
            finish();
        }

        if (diaryId > 0) {
            loadDiary();
        }
        //manually save
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    private void saveDiary() {
        if (!checkNotNull()) {
            Toast.makeText(EditActivity.this, R.string.edit_content_not_null,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        long saveId = 0;

        String titleString = (TextUtils.isEmpty(title.getText().toString()))
                ? title.getHint().toString() : title.getText().toString();
        String contentString = (TextUtils.isEmpty(content.getText().toString()))
                ? content.getHint().toString() : content.getText().toString();

        if (dateSeconds == 0 && diaryId > 0) {
            saveId = DbUtil.updateDiary(titleString, contentString, diaryId);
        } else if (dateSeconds > 0 && diaryId == 0) {
            contentString += LanguageUtil.getDiaryDateEnder(EditActivity.this, dateSeconds);
            saveId = DbUtil.saveDiary(titleString, contentString, dateSeconds);
        }
        Toast.makeText(EditActivity.this,
                (saveId > 0) ? R.string.save_success : R.string.save_failure,
                Toast.LENGTH_SHORT).show();
        if (saveId > 0) {
            Intent i = ViewActivity.createIntent(EditActivity.this, saveId);
            startActivity(i);
            finish();
        }
    }

    private boolean checkNotNull() {
        return !TextUtils.isEmpty(title.getText()) || !TextUtils.isEmpty(content.getText());
    }

    private void loadDiary() {
        Cursor cursor = DbUtil.getDiaryCursor(diaryId);
        if (cursor.getCount() != 1) {
            finish();
        }
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(Diary.TITLE);
            originTitle = cursor.getString(index);
            title.setText(originTitle);
            index = cursor.getColumnIndex(Diary.CONTENT);
            originContent = cursor.getString(index);
            content.setText(originContent);
        }
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        boolean unchangedFlag = (TextUtils.equals(originContent, content.getText().toString().trim())
                && TextUtils.equals(originTitle, title.getText().toString().trim()))
                || unchanged;
        if(unchangedFlag) {
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

    public static Intent createIntentWithId(Context context, long diaryId) {
        Intent i = new Intent(context, EditActivity.class);
        i.putExtra(DIARY_ID, diaryId);
        return i;
    }

    public static Intent createIntent(Context context, long dateMillis) {
        Intent i = new Intent(context, EditActivity.class);
        i.putExtra(DATE_TIME, dateMillis);
        return i;
    }
}
