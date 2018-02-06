package com.single.code.tool.rxjava.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.single.code.tool.db.InitDBEvent;


/**
 * Created by Administrator on 2018/2/5.
 */
public class RxDbHelper extends SQLiteOpenHelper {
    private static RxDbHelper rxDbHelper;
    private static InitDBEvent initDBEvent;
    public static RxDbHelper getHelper(Context context,InitDBEvent initEvent){
        initDBEvent = initEvent;
        if(rxDbHelper==null){
            synchronized (RxDbHelper.class){
                if(rxDbHelper == null){
                    rxDbHelper = new RxDbHelper(context);
                }
            }
        }
        return rxDbHelper;
    }
    public RxDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public RxDbHelper(Context context){
        super(context, DBTable.BASE_DB_NAME,null,DBTable.DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            if(initDBEvent!=null){
                initDBEvent.onDbCreate(db);
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(initDBEvent!=null){
            initDBEvent.onUpgrade(db,oldVersion,newVersion);
        }
    }
}