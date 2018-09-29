package com.single.code.tool.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 系统属性
 * Created by Administrator on 2017/11/6.
 */
public class SystemUtil {


    /**
     * 杀死进程
     * @param context
     * @param pkgName
     */
    public static void killProcess(Context context,String pkgName) {
        Log.d("kill", "kill app " + pkgName);
        try {
            Class<?> manager = Class.forName("android.app.ActivityManager");
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            Method m = manager.getDeclaredMethod("forceStopPackage", String.class);
            m.invoke(am, pkgName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 关机
     */
    private void shutDown() {
        try {
            Class<?> ServiceManager = Class
                    .forName("android.os.ServiceManager");
            Method getService = ServiceManager.getMethod("getService",
                    String.class);

            Object oRemoteService = getService.invoke(null,
                    Context.POWER_SERVICE);

            Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");

            Method asInterface = cStub.getMethod("asInterface",
                    android.os.IBinder.class);
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown",
                    boolean.class, boolean.class);
            shutdown.invoke(oIPowerManager, false, true);

        } catch (Exception e) {
        }
    }

    /**
     * 静默安装指定路径apk
     *
     * @param apkPath
     * @return
     */
    public static void installApk(String apkPath) {
        String[] args = { "pm", "install", "-r", apkPath };
        runProcess(args);
    }

    public static void runProcess(String[] args) {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read = -1;
        try {
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            String result = new String(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (errIs != null) {
                try {
                    errIs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (inIs != null) {
                try {
                    inIs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 静默卸载指定包名apk
     *
     * @param packageName
     * @return
     */
    public static void unInstallApk(String packageName) {
        String[] args = { "pm", "uninstall", packageName };
        runProcess(args);
    }
    /**
     * 清空最近任务列表
     * @param context
     */
    public static void clearRecentTasks(Context context){
        ActivityManager mActivityManager=null;
        Method mRemoveTask=null;
        try {
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            mRemoveTask = activityManagerClass.getMethod("removeTask", new Class[] { int.class});
            mRemoveTask.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mActivityManager!=null){
            List<ActivityManager.RecentTaskInfo> recents = mActivityManager.getRecentTasks(1000, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
            for(int i=1;i<recents.size();i++){
                removeTask(recents.get(i).persistentId,mActivityManager,mRemoveTask);
            }
        }
    }
    private static boolean removeTask(int taskId,ActivityManager mActivityManager,Method mRemoveTask) {
        try {
            if(mRemoveTask!=null&&mActivityManager!=null){
                return (Boolean) mRemoveTask.invoke(mActivityManager, Integer.valueOf(taskId));
            }
        } catch (Exception ex) {
            Log.i("MyActivityManager", "Task removal failed", ex);
        }
        return false;
    }

    /**
     * 获取当前Launcher 和所有Launcher 列表
     * @param context
     * @param outs 接收返回的Launcher列表
     * @return 当前launcher
     */
    private static ComponentName listHomeActivitys(Context context,List<ResolveInfo> outs){
        PackageManager pm = context.getPackageManager();
        Object cn = null;
        try {
            Class<?> packageManager = Class.forName("android.content.pm.PackageManager");
            Method getHomeActivities = packageManager.getMethod("getHomeActivities",List.class);
            getHomeActivities.setAccessible(true);
            cn = getHomeActivities.invoke(pm,outs);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return (ComponentName) cn;
    }

    /**
     * 获取IMEI
     */
    @TargetApi(26)
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (TelephonyManager.PHONE_TYPE_GSM == telephonyManager.getPhoneType()) {
                imei =  telephonyManager.getDeviceId();
            }else {
                imei = telephonyManager.getImei();
            }
        } catch (Throwable e) {
        }
        return imei;
    }

    /**
     * 获取MEID
     * @param context
     * @return
     */
    public static String getMEID(Context context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (TelephonyManager.PHONE_TYPE_CDMA == telephonyManager.getPhoneType()) {
                return telephonyManager.getDeviceId();
            }
        } catch (Throwable e) {
        }
        return "";
    }
    /**
     *
     * @param slotId 卡槽 0 or 1
     * @param context
     * @return  系统分配给卡槽中卡的id
     */
    public static int getSubId(int slotId, Context context) {
        Uri uri = Uri.parse("content://telephony/siminfo");
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor = contentResolver.query(uri, new String[] {"_id", "sim_id"}, "sim_id = ?", new String[] {String.valueOf(slotId)}, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex("_id"));
                }
            }
        } catch (Exception e) {
            Log.d("getSubId", e.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return -1;
    }

    /**
     * 获取手机卡槽对应的MEID或IMEI
     * @param context
     * @param slotId 卡槽 0 or 1
     * @return MEID 、IMEI or null
     */
    public static String getDevID(Context context,int slotId){
        int subId = getSubId(slotId,context);
        String value = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= 21) {
                Method method = tm.getClass().getMethod("getDeviceId", getMethodParamTypes(tm.getClass(),"getDeviceId"));
                if (subId >= 0) {
                    value = (String) method.invoke(tm, subId);
                }
            }
        } catch (Exception e) {
            Log.d("", e.toString());
        }
        return value;

    }


    /**
     * 根据函数名来获取参数列表
     * @param c 方法对应的Class
     * @param methodName 方法名
     * @return 方法对应的参数列表
     */
    public static Class[] getMethodParamTypes(Class c,String methodName) {
        Class[] params = null;
        try {
            Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    params = methods[i].getParameterTypes();
                    if (params.length >= 1) {
                        Log.d("length:", "" + params.length);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("", e.toString());
        }
        return params;
    }

}