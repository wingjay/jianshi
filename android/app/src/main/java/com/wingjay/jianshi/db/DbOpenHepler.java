package com.wingjay.jianshi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import retrofit2.http.HEAD;

/**
 * Diary Table:
 * id  diary_id  device_id  title  content  deleted  created_time  modified_time  time_removed
 */
public class DbOpenHepler extends SQLiteOpenHelper {

  public final static int DB_VAERION = 2;
  public final static String DB_NAME = "jianshi";

  private static final String CREATE_TABLE_DIARY_1 = "create table if not exists diary " +
      "(id integer primary key autoincrement," +
      "device_id string not null," +
      "title string not null," +
      "content string not null," +
      "created_time long not null," +
      "modified_time long )";

  private static final String ADD_COLUMN_DELETE_IN_DIARY_2 =
      "alter table diary add column deleted integer default 0";

  private static final String[] sqlArray = new String[2];
  static {
    sqlArray[0] = CREATE_TABLE_DIARY_1;
    sqlArray[1] = ADD_COLUMN_DELETE_IN_DIARY_2;
  }

  public DbOpenHepler(Context context, String name, int version) {
    super(context, name, null, version);
  }

  public DbOpenHepler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE_DIARY_1);
    db.execSQL(ADD_COLUMN_DELETE_IN_DIARY_2);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion == 0) {
      db.execSQL(sqlArray[0]);
      oldVersion++;
    }

    if (oldVersion == 1) {
      db.execSQL(sqlArray[1]);
    }
  }
}
