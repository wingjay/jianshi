package com.wingjay.jianshi.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.data.Diary;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.MultipleRowTextView;
import com.wingjay.jianshi.ui.widget.RedPointView;

import butterknife.InjectView;

public class ViewActivity extends BaseActivity {

    @InjectView(R.id.view_edit)
    RedPointView edit;

    @InjectView(R.id.hori_container)
    ScrollView verticalScrollView;

    @InjectView(R.id.view_title)
    TextView horizTitle;

    @InjectView(R.id.view_content)
    TextView horizContent;

    @InjectView(R.id.vertical_container)
    HorizontalScrollView horizontalScrollView;

    @InjectView(R.id.vertical_view_title)
    TextView vertialTitle;

    @InjectView(R.id.vertical_view_content)
    MultipleRowTextView verticalContent;

    private long diaryId;
    private boolean verticalStyle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        UserPrefs userPrefs = new UserPrefs(ViewActivity.this);
        verticalStyle = userPrefs.getVerticalWrite();
        setVisibilityByVerticalStyle();

        diaryId = getIntent().getLongExtra(EditActivity.DIARY_ID, 0);
        if (diaryId <= 0) {
            finish();
        }

        loadDiary();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = EditActivity.createIntentWithId(ViewActivity.this, diaryId);
                startActivity(i);
                finish();
            }
        });

        Handler handler = new HorizHandler(horizontalScrollView, verticalContent);
        verticalContent.setHandler(handler);
    }

    private void loadDiary() {
        Cursor cursor = DbUtil.getDiary(diaryId);
        if (cursor.getCount() != 1) {
            finish();
        }
        if (cursor.moveToFirst()) {
            do {
                String titleString, contentString;
                int index = cursor.getColumnIndex(Diary.TITLE);
                titleString = cursor.getString(index);
                index = cursor.getColumnIndex(Diary.CONTENT);
                contentString = cursor.getString(index);

                showDiary(titleString, contentString);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private static class HorizHandler extends Handler {

        private static HorizontalScrollView MhorizontalScrollView;
        private static MultipleRowTextView mMultipleRowTextView;

        public HorizHandler(HorizontalScrollView horizontalScrollView,
                            MultipleRowTextView multipleRowTextView) {
            MhorizontalScrollView = horizontalScrollView;
            mMultipleRowTextView = multipleRowTextView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MultipleRowTextView.LAYOUT_CHANGED:
                    MhorizontalScrollView.scrollBy(mMultipleRowTextView.getTextWidth(), 0);
                    break;
            }
        }
    }

    private void showDiary(String titleString, String contentString) {
        setVisibilityByVerticalStyle();

        if (verticalStyle) {
            vertialTitle.setText(titleString);
            verticalContent.setText(contentString);
        } else {
            horizTitle.setText(titleString);
            horizContent.setText(contentString);
        }
    }

    private void setVisibilityByVerticalStyle() {
        verticalScrollView.setVisibility(verticalStyle ? View.GONE : View.VISIBLE);
        horizontalScrollView.setVisibility(verticalStyle ? View.VISIBLE: View.GONE);
    }

    public static Intent createIntent(Context context, long diaryId) {
        if (diaryId <= 0) {
            return null;
        }
        Intent i = new Intent(context, ViewActivity.class);
        i.putExtra(EditActivity.DIARY_ID, diaryId);
        return i;
    }

}
