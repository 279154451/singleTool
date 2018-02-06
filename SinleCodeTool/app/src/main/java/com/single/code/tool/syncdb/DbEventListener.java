package com.single.code.tool.syncdb;

import android.database.Cursor;

/**
 * Created by Administrator on 2017/12/4.
 */
public interface DbEventListener {
    void AsyncUpdateComplete(int token,Object checkObj);
    void AsyncQuery(int token,Cursor cursor,Object checkObj);
    void AsyncDelete(int token,Object checkObj,int result);

}