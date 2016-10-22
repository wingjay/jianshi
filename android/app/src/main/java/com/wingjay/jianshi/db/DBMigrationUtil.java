package com.wingjay.jianshi.db;


import android.database.Cursor;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.wingjay.jianshi.bean.Diary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBMigrationUtil {

  public static void migrateToDBFlow() {
    List<com.wingjay.jianshi.db.model.Diary> diaryList = new ArrayList<>();
    Cursor cursor = DbUtil.getAllDiary();
    if (cursor.getCount() == 0) {
      return;
    }
    if (cursor.moveToFirst()) {
      do {
        com.wingjay.jianshi.db.model.Diary diary = new com.wingjay.jianshi.db.model.Diary();
        diary.setTitle(cursor.getString(cursor.getColumnIndex(Diary.TITLE)));
        diary.setContent(cursor.getString(cursor.getColumnIndex(Diary.CONTENT)));
        diary.setTime_created(cursor.getLong(cursor.getColumnIndex(Diary.CREATED_TIME)));
        diary.setTime_modified(cursor.getLong(cursor.getColumnIndex(Diary.MODIFIED_TIME)));
        diary.setUuid(UUID.randomUUID().toString().toUpperCase());
        diaryList.add(diary);
      } while (cursor.moveToNext());
    }
    FastStoreModelTransaction<com.wingjay.jianshi.db.model.Diary>
        fastStoreModelTransaction = FastStoreModelTransaction
        .saveBuilder(
            FlowManager.getModelAdapter(com.wingjay.jianshi.db.model.Diary.class))
        .addAll(diaryList).build();
    DatabaseDefinition database = FlowManager.getDatabase(JianshiDatabase.class);
    Transaction transaction = database.beginTransactionAsync(fastStoreModelTransaction).build();
    transaction.execute();
  }
}
