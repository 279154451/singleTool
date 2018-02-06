package com.single.code.tool.policy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.single.code.tool.db.DBTool;
import com.single.code.tool.db.DeleteEvent;
import com.single.code.tool.db.InsertEvent;
import com.single.code.tool.db.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */
public class PolicyManager {
    private String TAG = "PolicyManager";
    private static PolicyManager manager;

    public static PolicyManager getManager(){
        if(manager == null){
            synchronized (PolicyManager.class){
                if(manager == null){
                    manager = new PolicyManager();
                }
            }
        }
        return manager;
    }
    public void insert(Context context){
        ContentValues values  = new ContentValues();
        values.put(ValidDevTable.BEACON_ID,"111111");
        values.put(ValidDevTable.DEV_TYPE,0);
        values.put(ValidDevTable.POLICY_ID,1);
        values.put(ValidDevTable.POLICY_VER,1);
        DBTool.getTool(context).insert(context, PolicyTable.BEACON_ID_TABLE, values, new InsertEvent() {
            @Override
            public void onInsertComplete(long result) {
                Log.d(TAG,"onInsertComplete :"+result);
            }

            @Override
            public void onAsyncDbFailed() {
                Log.d(TAG,"onAsyncDbFailed");
            }
        });
    }
    public void insertList(Context context){
        List<ContentValues> valuesList = new ArrayList<>();
        for(int i =0;i<1000;i++){
            ContentValues values  = new ContentValues();
            values.put(ValidDevTable.BEACON_ID,"111111");
            values.put(ValidDevTable.DEV_TYPE,i);
            values.put(ValidDevTable.POLICY_ID,i);
            values.put(ValidDevTable.POLICY_VER,i);
            valuesList.add(values);
        }
        DBTool.getTool(context).insertList(context, PolicyTable.BEACON_ID_TABLE, valuesList, new InsertEvent() {
            @Override
            public void onInsertComplete(long result) {
                Log.d(TAG,"onInsertComplete :"+result);
            }

            @Override
            public void onAsyncDbFailed() {
                Log.d(TAG,"onAsyncDbFailed");
            }
        });
    }
    public void query(Context context){
       SQLiteDatabase database = DBTool.getTool(context).getDataBase(context);
        if(database!=null){
            try{
                Log.d(TAG, "query");
                String orderBy = "id asc LIMIT 500";
                database.beginTransaction();
                Cursor cursor = database.query(PolicyTable.BEACON_ID_TABLE,null,null,null,null,null,null);
                if(cursor!=null&& !cursor.isClosed() &&cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        String deviceID = cursor.getString(cursor.getColumnIndexOrThrow(ValidDevTable.BEACON_ID));
                        int policyId = cursor.getInt(cursor.getColumnIndexOrThrow(ValidDevTable.POLICY_ID));
                        Log.d(TAG,"deviceId :"+deviceID+" policyId :"+policyId);
                    }
                }
                if(cursor!=null){
                    cursor.close();
                    cursor = null;
                }
                database.setTransactionSuccessful();
                database.endTransaction();
            }catch (Exception e){

            }finally {
                DBTool.getTool(context).closeDb(database);
            }
        }
    }

    public void delete(Context context){
        final String selectionSql = ValidDevTable.BEACON_ID+" = "+"111111";
        DBTool.getTool(context).delete(context, PolicyTable.BEACON_ID_TABLE, selectionSql, new DeleteEvent() {
            @Override
            public void onDeleteComplete(long result) {
                Log.d(TAG,"onDeleteComplete :"+result);
            }

            @Override
            public void onAsyncDbFailed() {
                Log.d(TAG,"onAsyncDbFailed");
            }
        });
    }
    public void update(Context context){
        String selectionSql = ValidDevTable.BEACON_ID+" = "+"111111"+" AND "+ValidDevTable.POLICY_ID+" = "+1;
        ContentValues values = new ContentValues();
        values.put(ValidDevTable.BEACON_ID,"22222");
        values.put(ValidDevTable.DEV_TYPE,7);
        values.put(ValidDevTable.POLICY_ID,7);
        values.put(ValidDevTable.POLICY_VER,7);
        DBTool.getTool(context).update(context, PolicyTable.BEACON_ID_TABLE, values, selectionSql, new UpdateEvent() {
            @Override
            public void onUpdateComplete(long result) {
                Log.d(TAG,"onUpdateComplete :"+result);
            }

            @Override
            public void onAsyncDbFailed() {
                Log.d(TAG,"onAsyncDbFailed");
            }
        });
    }
}