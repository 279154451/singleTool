package com.single.code.tool.syncdb;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Administrator on 2017/12/4.
 */
public class AsyncHandler extends AsyncQueryHandler {
    private DbEventListener dbEventListener;
    public AsyncHandler(ContentResolver cr) {
        super(cr);
    }
    public AsyncHandler(Context context,DbEventListener listener){
        super(context.getApplicationContext().getContentResolver());
        dbEventListener = listener;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        if(dbEventListener !=null){
            dbEventListener.AsyncDelete(token,cookie,result);
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(dbEventListener !=null){
            dbEventListener.AsyncQuery(token,cursor,cookie);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if(dbEventListener !=null){
            dbEventListener.AsyncUpdateComplete(token,cookie);
        }
    }
}