package com.single.code.tool.syncdb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.single.code.tool.policy.PolicyTable;

/**
 * Created by Administrator on 2017/12/5.
 */
public class AsyncProvider extends ContentProvider {
    private DBHelper dbHelper;
    public static final String AUTHORITY = "com.single.code.tool.syncdb.AsyncProvider";
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final  int BEACON_ID = 1;
    static {
        matcher.addURI(AUTHORITY, PolicyTable.BEACON_ID_TABLE,BEACON_ID);
    }
    @Override
    public boolean onCreate() {
        if(dbHelper == null){
            dbHelper = DBHelper.getHelper(getContext());
        }
        if(dbHelper ==null){
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int id = matcher.match(uri);
        Cursor cursor = null;
        switch (id){
            case BEACON_ID:
                cursor = dbHelper.getReadableDatabase().query(PolicyTable.BEACON_ID_TABLE,null,s,strings1,null,null,s1);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int id = matcher.match(uri);
        switch (id){
            case BEACON_ID:
                dbHelper.getWritableDatabase().insert(PolicyTable.BEACON_ID_TABLE,null,contentValues);
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int id = matcher.match(uri);
        int count =0;
        switch (id){
            case BEACON_ID:
                count = dbHelper.getWritableDatabase().delete(PolicyTable.BEACON_ID_TABLE,s,strings);
                break;
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int id = matcher.match(uri);
        int count = 0;
        switch (id){
            case BEACON_ID:
                count = dbHelper.getWritableDatabase().update(PolicyTable.BEACON_ID_TABLE,contentValues,s,strings);
                break;
        }
        return count;
    }
}