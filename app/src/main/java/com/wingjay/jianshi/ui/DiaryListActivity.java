package com.wingjay.jianshi.ui;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.data.Diary;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.ui.adapter.DiaryListAdapter;
import com.wingjay.jianshi.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class DiaryListActivity extends BaseActivity {

    private List<Diary> diaryList;

    @InjectView(R.id.diary_list)
    RecyclerView diaryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        diaryListView.setHasFixedSize(true);
        diaryListView.setLayoutManager(new LinearLayoutManager(DiaryListActivity.this));

        // get all local diaries
        loadDiaries();
        DiaryListAdapter adapter = new DiaryListAdapter(DiaryListActivity.this, diaryList);
        diaryListView.setAdapter(adapter);
    }

    private void loadDiaries() {
        diaryList = new ArrayList<>();
        Cursor cursor = DbUtil.getAllDiary();
        if (cursor.getCount() == 0) {
            return;
        }
        if (cursor.moveToFirst()) {
            do {
                Diary d = new Diary();
                d.setId(cursor.getLong(cursor.getColumnIndex(Diary._ID)));
                d.setTitle(cursor.getString(cursor.getColumnIndex(Diary.TITLE)));
                d.setContent(cursor.getString(cursor.getColumnIndex(Diary.CONTENT)));
                d.setCreatedTime(cursor.getLong(cursor.getColumnIndex(Diary.CREATED_TIME)));
                diaryList.add(d);
            } while (cursor.moveToNext());
        }
    }

}
