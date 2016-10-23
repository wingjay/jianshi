package com.wingjay.jianshi.db;


import android.database.Cursor;
import android.util.Log;

import com.wingjay.jianshi.bean.Diary;

import java.util.UUID;

class DBMigrationUtil {

  static void migrateToDBFlow() {
    Log.i("Migrate", Thread.currentThread().getName());
    Cursor cursor = DbUtil.getAllDiary();
    if (cursor.getCount() == 0) {
      cursor.close();
      return;
    }
    if (cursor.moveToFirst()) {
      do {
        com.wingjay.jianshi.db.model.Diary diary = new com.wingjay.jianshi.db.model.Diary();
        diary.setTitle(cursor.getString(cursor.getColumnIndex(Diary.TITLE)));
        diary.setContent(cursor.getString(cursor.getColumnIndex(Diary.CONTENT)));
        diary.setTime_created(cursor.getLong(cursor.getColumnIndex(Diary.CREATED_TIME)) * 1000);
        diary.setTime_modified(cursor.getLong(cursor.getColumnIndex(Diary.CREATED_TIME)) * 1000);
        diary.setUuid(UUID.randomUUID().toString().toUpperCase());
        diary.save();
        Log.i("Migrate", "save success" + diary.getUuid());
      } while (cursor.moveToNext());
    }
    cursor.close();
  }
}
