package com.single.code.tool.reflect;

import android.app.AppOpsManager;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author yaoguoju
 */
public class AppOpsManagerRft {
	private static String TAG = "AppOpsManagerRft";
	private final static String ClassName = "android.app.AppOpsManager";
    public static final int OP_GPS = 2;
    public static final int OP_WRITE_SMS = 15;
	public static final int OP_CAMERA = 26;
	public static final int OP_RECORD_AUDIO = 27;
	public static final int OP_PLAY_AUDIO = 28;
    public static final int OP_MUTE_MICROPHONE = 44;

    public static final int MODE_ALLOWED = 0;
    public static final int MODE_IGNORED = 1;
	
    public static boolean setMode(int code, int uid, String packageName,
			int mode, Context context) {
		Class<?> c = null;
		try {
			c = Class.forName(ClassName, false, Thread.currentThread()
					.getContextClassLoader());
		} catch (ClassNotFoundException e) {
			Log.e(TAG, AppOpsManagerRft.class.getName()
					+ " not found");
		}

		try {
			Method method = c.getDeclaredMethod("setMode", new Class[] {
					int.class, int.class, String.class, int.class });

			try {
				AppOpsManager aom = (AppOpsManager) context
						.getSystemService(Context.APP_OPS_SERVICE);
				method.invoke(aom,
						new Object[] { code, uid, packageName, mode });
				return true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		} catch (NoSuchMethodException e) {
			Log.e(TAG, "setActiveAdmin method not found");
		}
		return false;

	}
}
