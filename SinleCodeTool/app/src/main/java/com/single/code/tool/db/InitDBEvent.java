package com.single.code.tool.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/12/5.
 */
public interface InitDBEvent {
    void onDbCreate(SQLiteDatabase database);
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}