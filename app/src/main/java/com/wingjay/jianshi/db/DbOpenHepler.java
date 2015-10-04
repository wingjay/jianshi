package com.wingjay.jianshi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wingjay on 9/30/15.
 */
public class DbOpenHepler extends SQLiteOpenHelper {

    public final static int DB_VAERION = 2;
    public final static String DN_NAME = "jianshi";

    private final String CREATE_TABLE_DIARY_1 = "create table if not exists diary " +
            "(id integer primary key autoincrement," +
            "device_id string not null," +
            "title string not null," +
            "content string not null," +
            "created_time long not null," +
            "modified_time long )";

    private final String ADD_COLUMN_DELETE_IN_DIARY_2 = "alter table diary " +
            "add column deleted integer default 0";

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
        if (newVersion == 2) {
            db.execSQL(ADD_COLUMN_DELETE_IN_DIARY_2);
        }
    }
}
