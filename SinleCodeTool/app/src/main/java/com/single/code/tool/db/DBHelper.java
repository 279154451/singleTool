package com.single.code.tool.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2017/12/1.
 */
public class DBHelper extends SQLiteOpenHelper {
    private String TAG = "AsyncDbTool";
    private Handler dbHandler;
    private ConcurrentLinkedQueue messageQueue = new ConcurrentLinkedQueue();
    private static DBHelper dbHelper;
    private Long  maxQueueSize = 10*1000l;
    private long messageDelay = 0l;
    private static InitDBEvent initDBEvent;
    public static class DBCmd{
        public static final int INSERT_CMD =0;
        public static final int INSERT_LIST_CMD = 1;
        public static final int UPDATE_CMD =2;
        public static final int DELETE_CMD = 4;
        public static final int QUERY_CMD = 5;
    }
    public static DBHelper getDbHelper(Context context,InitDBEvent event){
        initDBEvent = event;
        if(dbHelper == null){
            synchronized (DBHelper.class){
                if(dbHelper == null){
                    dbHelper = new DBHelper(context);
                }
            }
        }
        return dbHelper;
    }
    public static class DBAction{
        private String table;
        private Object value;
        private String command;
        private DBEvent listener;
        private long result;

        public DBAction(){

        }
        public DBAction(String table,Object value,String command,DBEvent eventListener){
            this.table = table;
            this.value = value;
            this.command = command;
            this.listener = eventListener;
        }
        @Override
        public String toString() {
            return "DBAction{" +
                    "table='" + table + '\'' +
                    ", value=" + value +
                    ", command='" + command +
                    '}';
        }
    }
    private Thread workThread = new Thread(){
        @SuppressLint("NewApi")
        @Override
        public void run() {
            Looper.prepare();
            Log.d(TAG,"workThread run");
            if(dbHandler == null){
                Log.d(TAG,"workThread init Handler");
                dbHandler = new Handler(Looper.myLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        try {
                            Message message  = eventHandler.obtainMessage();
                            int whatCmd = msg.what;
                            DBAction dbAction = (DBAction) msg.obj;
                            Log.d(TAG, "whatCmd :" + whatCmd);
                            SQLiteDatabase database = getWriteDatabase();
                            if(dbAction == null){
                                Log.d(TAG,"dbAction is null");
                                return;
                            }
                            if(database == null){
                                Log.d(TAG,"database is null");
                                dbAction.result = -1;
                            }
                            if(database!=null&& dbAction!=null){
                                Log.d(TAG, "cmd :" + whatCmd + "  obj:" + dbAction.toString());
                                switch (whatCmd){
                                    case DBCmd.INSERT_CMD:
                                        ContentValues values = (ContentValues) dbAction.value;
                                        if(values!=null){
                                            database.beginTransaction();
                                            dbAction.result = database.insert(dbAction.table, null, values);
                                            database.setTransactionSuccessful();
                                            database.endTransaction();
                                        }
                                        Log.d(TAG,"INSERT_CMD");
                                        break;
                                    case DBCmd.INSERT_LIST_CMD:
                                        List<ContentValues> valuesList = (List<ContentValues>) dbAction.value;
                                        if(valuesList!=null&& !valuesList.isEmpty()){
                                            for(ContentValues value:valuesList){
                                                database.beginTransaction();
                                                dbAction.result =database.insert(dbAction.table, null, value);
                                                database.setTransactionSuccessful();
                                                database.endTransaction();
                                            }
                                        }
                                        Log.d(TAG,"INSERT_LIST_CMD");
                                        break;
                                    case DBCmd.DELETE_CMD:
                                        database.beginTransaction();
                                        dbAction.result = database.delete(dbAction.table,dbAction.command,null);
//                                        database.execSQL(dbAction.command);//直接执行sql语句
                                        database.setTransactionSuccessful();
                                        database.endTransaction();
                                        Log.d(TAG, "DELETE_CMD");
                                        break;
                                    case DBCmd.UPDATE_CMD:
                                        ContentValues updateValue = (ContentValues) dbAction.value;
                                        database.beginTransaction();
                                        dbAction.result = database.update(dbAction.table, updateValue, dbAction.command, null);//直接执行sql语句
                                        database.setTransactionSuccessful();
                                        database.endTransaction();
                                        Log.d(TAG, "UPDATE_CMD");
                                        break;
                                    case DBCmd.QUERY_CMD:
                                        break;
                                }
                                closeDatabase(database);
                            }
                            message.what = whatCmd;
                            message.obj = dbAction;
                            eventHandler.sendMessage(message);
                        }catch (Exception e){
                            Log.d(TAG,"DB sql error :"+e.getMessage());
                        }
                    }
                };
            }
            try{
                Thread.sleep(10);
            } catch (Exception exception){

            }
            Looper.loop();
            return;
        }
    };

    public SQLiteDatabase getWriteDatabase(){
        SQLiteDatabase database = null;
        try {
            if(DbLock.lock()){
                database = getWritableDatabase();
            }
        }catch (Exception e){
//            DbLock.release();
        }finally {
            return database;
        }
    }
    public void closeDatabase(SQLiteDatabase database){
        if(database!=null&& database.isOpen()){
            Log.d(TAG,"closeDatabase");
            database.close();
        }
        DbLock.release();
    }

    public SQLiteDatabase getReadDatabase(){
        SQLiteDatabase database = null;
        try{
            if(DbLock.lock()){
                database =  getReadableDatabase();
            }
        }catch (Exception e){
//            DbLock.release();
        }finally {
            return database;
        }
    }


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context){
        super(context, DBTable.BASE_DB_NAME,null,DBTable.DBVersion);
        if(workThread!=null&& !workThread.isAlive()){
            workThread.start();
        }
    }


    public void delete(DBAction dbaction) {
        while (true){
            if(dbHandler!=null){
                Log.d(TAG, "delete");
                Message message = dbHandler.obtainMessage();
                message.what = DBCmd.DELETE_CMD;
                message.obj = dbaction;
                dbHandler.sendMessageDelayed(message, messageDelay);
                return;
            }else {
                if(workThread!=null&& !workThread.isAlive()){
                    workThread.start();
                }
            }
        }
    }

    private boolean checkQueue(){
        if(messageQueue.size()<maxQueueSize){
            return true;
        }
        return false;
    }
    public void insert(DBAction dbAction){
        while (true){
            if(dbHandler !=null){
                Log.d(TAG, "insert");
                Message message = dbHandler.obtainMessage();
                message.what  = DBCmd.INSERT_CMD;
                message.obj = dbAction;
                dbHandler.sendMessageDelayed(message, messageDelay);
                return;
            }else {
                if(workThread!=null&& !workThread.isAlive()){
                    workThread.start();
                }
            }
        }
    }
    public void update(DBAction dbAction){
        while (true){
            if(dbHandler !=null){
                Log.d(TAG,"update");
                Message message = dbHandler.obtainMessage();
                message.what  = DBCmd.UPDATE_CMD;
                message.obj = dbAction;
                dbHandler.sendMessageDelayed(message, messageDelay);
                return;
            }else {
                if(workThread!=null&& !workThread.isAlive()){
                    workThread.start();
                }
            }
        }
    }
    public void insertList(DBAction dbAction){
        while (true){
            if(dbHandler !=null){
                Log.d(TAG,"insertList");
                Message message = dbHandler.obtainMessage();
                message.what  = DBCmd.INSERT_LIST_CMD;
                message.obj = dbAction;
                dbHandler.sendMessageDelayed(message, messageDelay);
                return;
            }else {
                if(workThread!=null&& !workThread.isAlive()){
                    workThread.start();
                }
            }
        }
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

    private Handler eventHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.what;
            DBAction dbAction = (DBAction) msg.obj;
            if(dbAction!=null){
                switch (cmd){
                    case DBCmd.INSERT_CMD:
                    case DBCmd.INSERT_LIST_CMD:
                        InsertEvent insertEvent = (InsertEvent) dbAction.listener;
                        if(insertEvent!=null){
                           if(dbAction.result<0){
                               insertEvent.onAsyncDbFailed();
                           }else {
                               insertEvent.onInsertComplete(dbAction.result);
                           }
                        }
                        break;
                    case DBCmd.DELETE_CMD:
                        DeleteEvent deleteEvent = (DeleteEvent) dbAction.listener;
                        if(deleteEvent!=null){
                            if(dbAction.result<0){
                                deleteEvent.onAsyncDbFailed();
                            }else {
                                deleteEvent.onDeleteComplete(dbAction.result);
                            }
                        }
                        break;
                    case DBCmd.UPDATE_CMD:
                        UpdateEvent updateEvent = (UpdateEvent) dbAction.listener;
                        if(updateEvent!=null){
                            if(dbAction.result<0){
                                updateEvent.onAsyncDbFailed();
                            }else {
                                updateEvent.onUpdateComplete(dbAction.result);
                            }
                        }
                        break;

                }
            }
        }
    };
}