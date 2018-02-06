package com.single.code.tool.syncdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.single.code.tool.policy.PolicyTable;
import com.single.code.tool.policy.ValidDevTable;

/**
 * Created by Administrator on 2017/12/4.
 */
public class DBTestManager {
    private static DBTestManager manager;
    private String TAG = "DBTestManager";
    Uri uri = Uri.parse("content://com.single.code.tool.syncdb.AsyncProvider/"+ PolicyTable.BEACON_ID_TABLE);
    public static DBTestManager getManager(){
        if(manager == null){
            synchronized (DBTestManager.class){
               if(manager == null){
                   manager = new DBTestManager();
               }
            }
        }
        return manager;
    }

    public void insert(Context context){
        AsyncDbTool dbHelper = AsyncDbTool.getHelper(context);
        for(int i =0;i<1000;i++){
            ContentValues values  = new ContentValues();
            values.put(ValidDevTable.BEACON_ID,"111111");
            values.put(ValidDevTable.DEV_TYPE,i);
            values.put(ValidDevTable.POLICY_ID,i);
            values.put(ValidDevTable.POLICY_VER, i);
            dbHelper.insert(uri, 1, null, values);
        }
    }

    public void query(Context context){
        AsyncDbTool dbHelper = new AsyncDbTool(context);
        dbHelper.query(uri, 2, new DbEventListener() {
            @Override
            public void AsyncUpdateComplete(int token, Object checkObj) {

            }

            @Override
            public void AsyncQuery(int token, Cursor cursor, Object checkObj) {
                if(cursor!=null&& !cursor.isClosed()&& cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        String deviceID = cursor.getString(cursor.getColumnIndexOrThrow(ValidDevTable.BEACON_ID));
                        int policyId = cursor.getInt(cursor.getColumnIndexOrThrow(ValidDevTable.POLICY_ID));
                        Log.d(TAG, "deviceId :" + deviceID + " policyId :" + policyId+"  token :"+token);
                    }
                }
            }

            @Override
            public void AsyncDelete(int token, Object checkObj, int result) {

            }
        }, null, null, null, null);
    }

    public void delete(Context context){
        AsyncDbTool dbHelper = new AsyncDbTool(context);
        String selection = ValidDevTable.BEACON_ID+" =?"+" AND "+ValidDevTable.POLICY_ID+" = "+1;
        String[] selectionArgs = new String[]{"111111"};
        dbHelper.delete(uri, 3, new DbEventListener() {
            @Override
            public void AsyncUpdateComplete(int token, Object checkObj) {

            }

            @Override
            public void AsyncQuery(int token, Cursor cursor, Object checkObj) {

            }

            @Override
            public void AsyncDelete(int token, Object checkObj, int result) {
                Log.d(TAG,"AsyncDelete token:"+token+"  result:"+result);
            }
        }, selection, selectionArgs);
    }
    public void update(Context context){
        AsyncDbTool dbHelper = new AsyncDbTool(context);
        String selectionSql = ValidDevTable.BEACON_ID+" =?"+" AND "+ValidDevTable.POLICY_ID+" = "+2;
        String[] selectionArgs = new String[]{"111111"};
        ContentValues values = new ContentValues();
        values.put(ValidDevTable.BEACON_ID,"22222");
        values.put(ValidDevTable.DEV_TYPE,7);
        values.put(ValidDevTable.POLICY_ID,7);
        values.put(ValidDevTable.POLICY_VER,7);
        dbHelper.update(uri, 4, new DbEventListener() {
            @Override
            public void AsyncUpdateComplete(int token, Object checkObj) {
                Log.d(TAG,"AsyncUpdateComplete :"+token);
            }

            @Override
            public void AsyncQuery(int token, Cursor cursor, Object checkObj) {

            }

            @Override
            public void AsyncDelete(int token, Object checkObj, int result) {

            }
        },values,selectionSql,selectionArgs);
    }
}