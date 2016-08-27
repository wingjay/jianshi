package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.Diary;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.ui.adapter.DiaryListAdapter;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.util.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class DiaryListActivity extends BaseActivity {

    private final List<Diary> diaryList = new ArrayList<>();
    private DiaryListAdapter adapter;

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
        adapter = new DiaryListAdapter(DiaryListActivity.this, diaryList);
        diaryListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantUtil.REQUEST_CODE_VIEW_DIARY_FROM_LIST
                && resultCode == RESULT_OK) {
            loadDiaries();
            adapter.notifyItemRangeChanged(0, diaryList.size());
            adapter.notifyDataSetChanged();
        }
    }

    private void loadDiaries() {
        Cursor cursor = DbUtil.getAllDiary();
        if (cursor.getCount() == 0) {
            return;
        }
        diaryList.clear();
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
