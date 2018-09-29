package com.single.code.tool.reflect;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 设置statusBar状态 需要系统权限
 * Created by yaoguoju on 16-8-16.
 */
public class StatusBarManager {
    private static String TAG = "StatusBarManager";
    public enum StatusBarCmd {
        DISABLE_EXPAND,//
        DISABLE_NONE,
        DISABLE_RECENT,
        DISABLE_NOTIFICATION_ICONS
    }

    public static void disableStatusBar(Context context, StatusBarCmd cmd) {
        try {
            Object service = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method disable = statusbarManager.getMethod("disable",int.class);
            disable.setAccessible(true);
            Field disable_expand = statusbarManager.getField(cmd.name());
            disable_expand.setAccessible(true);
            int disable_code  = disable_expand.getInt(statusbarManager);
            Log.d(TAG,"statusbar disable code " + disable_code);
            disable.invoke(service,disable_code);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
