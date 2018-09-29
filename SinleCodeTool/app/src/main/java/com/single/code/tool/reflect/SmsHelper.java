package com.single.code.tool.reflect;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.single.code.tool.logger.Logger;
import com.single.code.tool.util.SystemUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by yaoguoju on 16-5-4.
 */
public class SmsHelper {
    private static String TAG = "SmsHelper";
    private static Uri URI_SMS = Uri.parse("content://sms/inbox");
    private static final String CLASS_NAME = "com.android.internal.telephony.SmsApplication";

    /**
     * 设置飞行模式，需要系统权限
     * @param context
     * @param on
     */
    public static void setAirplaneMode(Context context, int on) {
        Settings.System.putLong(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, on);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setAirplaneMode(Context context, boolean on) {
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,on?1:0);
    }

    /**
     * 需要系统权限
     * @param context
     * @param pkg
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setDefaultSmsApp(Context context, String pkg) {
        try {
            Class<?> smsApplication = Class.forName(CLASS_NAME);
            Method method = smsApplication.getMethod("setDefaultApplication",String.class,Context.class);
            method.setAccessible(true);
            method.invoke(null, pkg, context);
        } catch (Exception e) {
            Log.d("YYY", "" + e);
        }

    }
    /**
     * 利用AppOps设置应用的写短信权限，需要系统权限
     * @param allow
     * @param pkgname
     * @param context
     */
    public static void setAppSmsMode(boolean allow, String pkgname, Context context) {
        if (context == null) {
            Log.d(TAG, "setAppSmsMode context null");
            return;
        }
        int code = AppOpsManagerRft.OP_WRITE_SMS;
        int uid = getAppUidByPkgName(pkgname, context);
        int mode = allow ? AppOpsManagerRft.MODE_ALLOWED
                : AppOpsManagerRft.MODE_IGNORED;
        AppOpsManagerRft.setMode(code, uid, pkgname, mode, context);
        Log.d(TAG, "Pkg = " + pkgname + " setAppSmsMode " + allow + " uid =" + uid + " mode =" + mode);
    }
    /**
     * 通过包名获取应用uid
     *
     * @param packageName
     * @param context
     * @return
     */
    private static int getAppUidByPkgName(String packageName, Context context) {
        if (context == null) {
            Log.d(TAG, "getAppUidByPkgName context null");
            return -1;
        }
        int uid = -1;
        PackageManager pm = (PackageManager) context.getPackageManager();
        try {
            @SuppressLint("WrongConstant") ApplicationInfo info = pm.getApplicationInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                uid = info.uid;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "app not found");
            e.printStackTrace();
        }
        return uid;
    }


    /**
     * 清空所有短信
     * @param context
     */
    public static void clearAllSms(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearInboxSms(context);
                clearOutboxSms(context);
                clearSendSms(context);
                clearDraftSms(context);
                clearSmsConversations(context);
                //彩信
                clearInboxMms(context);
                clearOutboxMms(context);
                clearSendMms(context);
                clearDraftMms(context);
            }
        }).start();
    }

    /**
     * 清空收件箱
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearInboxSms(Context context){
        Logger.d(TAG,"clearInboxSms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.Inbox.CONTENT_URI,null,null,null,null);
       if(cursor!= null && cursor.getCount() >0){
           while (cursor!=null && cursor.moveToNext()){
               int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
               Uri sms_uri_in= Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, String.valueOf(id));
               resolver.delete(sms_uri_in,null,null);
           }
       }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空发件箱
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearOutboxSms(Context context){
        Logger.d(TAG,"clearOutboxSms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.Outbox.CONTENT_URI,null,null,null,null);
       if(cursor !=null && cursor.getCount() >0){
           while (cursor!=null && cursor.moveToNext()){
               int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
               Uri sms_uri_in= Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, String.valueOf(id));
               resolver.delete(sms_uri_in,null,null);
           }
       }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空已发送列表
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearSendSms(Context context){
        Logger.d(TAG,"clearSendSms",true );
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.Sent.CONTENT_URI,null,null,null,null);
       if(cursor!=null && cursor.getCount() >0){
           while (cursor!=null && cursor.moveToNext()){
               int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
               Uri sms_uri_in= Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, String.valueOf(id));
               resolver.delete(sms_uri_in,null,null);
           }
       }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空草稿箱
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearDraftSms(Context context){
        Logger.d(TAG,"clearDraftSms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.Draft.CONTENT_URI,null,null,null,null);
        if(cursor !=null && cursor.getCount() >0){
            while (cursor!=null && cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
                Uri sms_uri_in= Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, String.valueOf(id));
                resolver.delete(sms_uri_in,null,null);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
    }


    /**
     * 清空待发送列表
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearSmsConversations(Context context){
        Logger.d(TAG,"clearSmsConversations",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.Conversations.CONTENT_URI,null,null,null,null);
       if(cursor !=null && cursor.getCount() >0){
           while (cursor!=null && cursor.moveToNext()){
               int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
               Uri sms_uri_in= Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, String.valueOf(id));
               resolver.delete(sms_uri_in,null,null);
           }
       }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空彩信收件箱
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearInboxMms(Context context){
        Logger.d(TAG,"clearInboxMms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Mms.Inbox.CONTENT_URI,null,null,null,null);
        if(cursor !=null && cursor.getCount()>0){
            while (cursor!=null && cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
                Uri sms_uri_in= Uri.withAppendedPath(Telephony.Mms.CONTENT_URI, String.valueOf(id));
                resolver.delete(sms_uri_in,null,null);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空彩信发件箱
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearOutboxMms(Context context){
        Logger.d(TAG,"clearOutboxMms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Mms.Outbox.CONTENT_URI,null,null,null,null);
        if(cursor!= null && cursor.getCount() >0){
            while (cursor!=null && cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
                Uri sms_uri_in= Uri.withAppendedPath(Telephony.Mms.CONTENT_URI, String.valueOf(id));
                resolver.delete(sms_uri_in,null,null);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空已发送彩信列表
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void clearSendMms(Context context){
        Logger.d(TAG,"clearSendMms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Mms.Sent.CONTENT_URI,null,null,null,null);
        if(cursor!= null && cursor.getCount() >0){
            while (cursor!=null && cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
                Uri sms_uri_in= Uri.withAppendedPath(Telephony.Mms.CONTENT_URI, String.valueOf(id));
                resolver.delete(sms_uri_in,null,null);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 清空彩信草稿箱
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void  clearDraftMms(Context context){
        Logger.d(TAG,"clearDraftMms",true);
        final String ID_COLUMN_NAME = "_id";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Telephony.Mms.Draft.CONTENT_URI,null,null,null,null);
        if(cursor!= null && cursor.getCount() >0){
            while (cursor!=null && cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
                Uri sms_uri_in= Uri.withAppendedPath(Telephony.Mms.CONTENT_URI, String.valueOf(id));
                resolver.delete(sms_uri_in,null,null);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
    }

}
