package com.single.code.tool.syncdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.single.code.tool.policy.PolicyTable;

/**
 * Created by Administrator on 2017/12/5.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper helper;
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context){
        super(context,"Aysnc.db",null,1);
    }
    public static DBHelper getHelper(Context context){
        if(helper == null){
            synchronized (DBHelper.class){
                if(helper == null){
                    helper = new DBHelper(context);
                }
            }
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PolicyTable.BeaconIdSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}