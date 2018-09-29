package com.single.code.tool.reflect;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;


import com.single.code.tool.logger.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * launcher切换 需要系统权限
 * Created by yaoguoju on 16-8-16.
 */
public class LauncherManager {
    private static String TAG = "LauncherManager";
    public static void switchLauncher(Context context, ComponentName activity) {
        PackageManager pm = context.getPackageManager();
        Logger.d(TAG, "switch launcher " + activity,true);
        try {
            Class<?> packageManager = Class.forName("android.content.pm.PackageManager");
            Method replacePreferedActivity = packageManager.getMethod("replacePreferredActivity",IntentFilter.class,int.class,ComponentName[].class,ComponentName.class);
            IntentFilter homeFilter = new IntentFilter(Intent.ACTION_MAIN);
            homeFilter.addCategory(Intent.CATEGORY_HOME);
            homeFilter.addCategory(Intent.CATEGORY_DEFAULT);

            List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
            ComponentName curLauncher = listHomeActivitys(context,resolveInfos);
            if(resolveInfos != null && resolveInfos.size() > 0) {
                ComponentName[] componentNames = new ComponentName[resolveInfos.size()];
                for(int i =0 ; i<resolveInfos.size();i++) {
                    ActivityInfo activityInfo = resolveInfos.get(i).activityInfo;
                    if(activityInfo != null) {
                        ComponentName cn = new ComponentName(activityInfo.packageName, activityInfo.name);
                        componentNames[i] = cn;
                        Logger.d(TAG, "launcher:" + cn,true);
                    }
                }
                replacePreferedActivity.setAccessible(true);
                replacePreferedActivity.invoke(pm, homeFilter, IntentFilter.MATCH_CATEGORY_EMPTY, componentNames, activity);
                if(curLauncher!=null) {
                    killPackage(context,curLauncher.getPackageName());
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(intent);
                Log.d(TAG, "start home");
            }else {
                Log.e(TAG, "get home resolve info empty" + false);
            }

        } catch (Exception e) {
            Log.e(TAG, "1" + e.toString());
        }

    }

    private static ComponentName listHomeActivitys(Context context, List<ResolveInfo> outs){
        PackageManager pm = context.getPackageManager();
        Object cn = null;
        try {
            Class<?> packageManager = Class.forName("android.content.pm.PackageManager");
            Method getHomeActivities = packageManager.getMethod("getHomeActivities",List.class);
            getHomeActivities.setAccessible(true);
            cn = getHomeActivities.invoke(pm,outs);
            Logger.d(TAG, "cn =" + cn,true);
        }catch (Exception e) {
            Log.e(TAG, "2" + e.toString());
            e.printStackTrace();
        }
        return (ComponentName) cn;
    }

    private static void killPackage(Context context, String pkg) {
        Log.d(TAG, "kill package " + pkg);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(pkg);
    }
}
