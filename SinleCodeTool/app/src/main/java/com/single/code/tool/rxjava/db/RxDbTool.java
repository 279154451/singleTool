package com.single.code.tool.rxjava.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.single.code.tool.db.DbLock;
import com.single.code.tool.db.InitDBEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2018/2/5.
 */
public class RxDbTool implements InitDBEvent {
    private static RxDbTool rxDbTool;
    private ConcurrentLinkedQueue messageQueue = new ConcurrentLinkedQueue();
    private RxDbHelper dbHelper;
    private Handler messageHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DBAction.DBCmd dbCmd = (DBAction.DBCmd) msg.obj;
            switch (dbCmd){
                case INSERT_CMD:

                    break;
                case INSERT_LIST_CMD:

                    break;
                case UPDATE_CMD:

                    break;
                case DELETE_CMD:

                    break;
                case QUERY_CMD:

                    break;
            }
        }
    };
    public static RxDbTool get(Context context){
        if(rxDbTool ==null){
            synchronized (RxDbTool.class){
                if(rxDbTool == null){
                    rxDbTool = new RxDbTool(context);
                }
            }
        }
        return rxDbTool;
    }
    public RxDbTool(Context context){
        if(dbHelper==null){
            dbHelper = RxDbHelper.getHelper(context,this);
        }
    }

    public void insert(Context context,DBAction action){
        DBAction.DBCmd dbCmd = action.getCmd();
        switch (dbCmd){
            case INSERT_CMD:

                break;
            case INSERT_LIST_CMD:

                break;
        }
    }


    @Override
    public void onDbCreate(SQLiteDatabase database) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}