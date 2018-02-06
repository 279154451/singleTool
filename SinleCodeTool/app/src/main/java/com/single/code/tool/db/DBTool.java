package com.single.code.tool.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.single.code.tool.policy.PolicyTable;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */
public class DBTool implements InitDBEvent {
    private String TAG = "DBTool";
    private static DBTool tool;
    private static DBHelper dbHelper;
    public static DBTool getTool(Context context){
            if(tool == null){
                synchronized (DBTool.class){
                    if(tool == null){
                        tool = new DBTool(context);
                    }
                }
            }
        return tool;
    }
    public DBTool(Context context){
        initDBHelper(context.getApplicationContext());
    }
    private void initDBHelper(Context context){
        if(dbHelper==null){
           dbHelper = DBHelper.getDbHelper(context,this);
        }
    }

    public void insert(Context context,String table,ContentValues values,InsertEvent insertEvent){
        DBHelper.DBAction dbAction = new DBHelper.DBAction(table,values,null,insertEvent);
        dbHelper.insert(dbAction);
    }
    public void insertList(Context context,String table,List<ContentValues> valuesList,InsertEvent insertEvent){
        initDBHelper(context);
        DBHelper.DBAction dbAction = new DBHelper.DBAction(table,valuesList,null,insertEvent);
        dbHelper.insertList(dbAction);
    }
    public void delete(Context context,String table,String selectionSql,DeleteEvent deleteEvent){
//        String command = "DELETE FROM "+table+(!TextUtils.isEmpty(whereStr)?" WHERE "+whereStr:"");
        Log.d(TAG,"delete :"+selectionSql);
        DBHelper.DBAction dbAction = new DBHelper.DBAction(table,null,selectionSql,deleteEvent);
        dbHelper.delete(dbAction);
    }
    public void update(Context context,String table,ContentValues values,String selectionSql,UpdateEvent updateEvent){
        Log.d(TAG, "update :" + selectionSql);
        DBHelper.DBAction dbAction = new DBHelper.DBAction(table,values,selectionSql,updateEvent);
        dbHelper.update(dbAction);
    }
    public SQLiteDatabase getDataBase(Context context){
        return dbHelper.getReadDatabase();
    }
    public void closeDb(SQLiteDatabase database){
        dbHelper.closeDatabase(database);
    }

    @Override
    public void onDbCreate(SQLiteDatabase database) {
        database.execSQL(PolicyTable.BeaconIdSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}