package com.single.code.tool.syncdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2017/12/4.
 */
public class AsyncDbTool implements DbEventListener {
    private Handler workHandler;
    private AsyncHandler dbAsyncHandler;
    private DbEventListener listener;
    private Context mContext;
    private static AsyncDbTool helper;
    public static AsyncDbTool getHelper(Context context){
        if(helper == null){
            synchronized (AsyncDbTool.class){
                if(helper == null){
                    helper = new AsyncDbTool(context.getApplicationContext());
                }
            }
        }
        return helper;
    }
    public static class DBCmd{
        public static final int INSERT_CMD =0;
        public static final int INSERT_LIST_CMD = 1;
        public static final int UPDATE_CMD =2;
        public static final int DELETE_CMD = 4;
        public static final int QUERY_CMD = 5;
    }

    private static class DBAction{
        private int token = 100;
        private Uri uri;
        private Object value;
        private String selection;
        private String[] selectionArgs;
        private String[] project;
        private String orderBy;
        private Object checkObj;
        public DBAction(){

        }
        public DBAction(Uri uri,Object checkObj,Object value,String[] project,String selection,String[] selectionArgs,String orderBy){
            this.uri = uri;
            this.checkObj = checkObj;
            this.value = value;
            this.project = project;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
            this.orderBy = orderBy;
        }

    }
    public AsyncDbTool(Context context){
        listener = this;
        mContext = context.getApplicationContext();
        if(workThread!=null&& !workThread.isAlive()){
            workThread.start();
        }
    }
    private Thread workThread = new Thread(){
        @Override
        public void run() {
            Looper.prepare();
            if(dbAsyncHandler == null){
                dbAsyncHandler = new AsyncHandler(mContext,listener);
            }
            Looper.loop();
        }
    };

    public void insert(Uri uri,int token,Object checkObj,ContentValues values){
        if(dbAsyncHandler!=null){
            dbAsyncHandler.startInsert(token,checkObj,uri,values);
        }else if(workThread!=null && !workThread.isAlive()) {
            workThread.start();
        }
    }

    public void delete(Uri uri,int token,Object checkObj,String selection,String[] selectionArgs){
        if(dbAsyncHandler!=null){
            dbAsyncHandler.startDelete(token, checkObj, uri, selection, selectionArgs);
        }else if(workThread!=null && !workThread.isAlive()) {
            workThread.start();
        }
//        DBAction dbAction = new DBAction(uri,checkObj,null,null,selection,selectionArgs,null);
//        Message message = workHandler.obtainMessage();
//        message.what = DBCmd.DELETE_CMD;
//        message.obj = dbAction;
//        workHandler.sendMessage(message);
    }
    public void update(Uri uri,int token,Object checkObj,ContentValues values,String selection,String[] selectionArgs){
        if(dbAsyncHandler!=null){
            dbAsyncHandler.startUpdate(token, checkObj, uri, values, selection, selectionArgs);
        }else if(workThread!=null && !workThread.isAlive()) {
            workThread.start();
        }
//        DBAction dbAction = new DBAction(uri,checkObj,values,null,selection,selectionArgs,null);
//        Message message = workHandler.obtainMessage();
//        message.what = DBCmd.UPDATE_CMD;
//        message.obj = dbAction;
//        workHandler.sendMessage(message);
    }
    public void query(Uri uri,int token,Object checkObj,String[] projection,String selection,String[] selectionArgs,String orderBy){
        if(dbAsyncHandler!=null){
            dbAsyncHandler.startQuery(token, checkObj, uri, projection, selection, selectionArgs, orderBy);
        }else if(workThread!=null && !workThread.isAlive()) {
            workThread.start();
        }
//        DBAction dbAction = new DBAction(uri,checkObj,null,projection,selection,selectionArgs,orderBy);
//        Message message = workHandler.obtainMessage();
//        message.what = DBCmd.QUERY_CMD;
//        message.obj = dbAction;
//        workHandler.sendMessage(message);
    }

    @Override
    public void AsyncUpdateComplete(int token,Object checkObj) {
        if(checkObj!=null &&checkObj instanceof DbEventListener){
            DbEventListener listener = (DbEventListener) checkObj;
            listener.AsyncUpdateComplete(token,checkObj);
        }
    }

    @Override
    public void AsyncQuery(int token,Cursor cursor, Object checkObj) {
        if(checkObj!=null && checkObj instanceof DbEventListener){
            DbEventListener listener = (DbEventListener) checkObj;
            listener.AsyncQuery(token,cursor, checkObj);
        }
    }

    @Override
    public void AsyncDelete(int token,Object checkObj, int result) {
        if(checkObj!=null && checkObj instanceof DbEventListener){
            DbEventListener listener = (DbEventListener) checkObj;
            listener.AsyncDelete(token,checkObj,result);
        }
    }
}