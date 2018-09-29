package com.single.code.tool.reflect;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;


import com.single.code.tool.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yaoguoju on 2015/10/15.
 */
public class DevicePolicyManagerRft {
	private static String TAG ="DevicePolicyManagerRft";
	private final static String ClassName = "android.app.admin.DevicePolicyManager";

	public static boolean setActiveAdmin(ComponentName componentName,
			Context context) {
		Class<?> c = null;
		try {
			c = Class.forName(ClassName, false, Thread.currentThread()
					.getContextClassLoader());
		} catch (ClassNotFoundException e) {
			Logger.e(TAG, DevicePolicyManagerRft.class.getName()
					+ " not found",true);
			e.printStackTrace();
		}

		try {
			Method method = c.getDeclaredMethod("setActiveAdmin", new Class[] {
					ComponentName.class, boolean.class });

			try {
				Log.d(TAG, "setAciveAdmin start");
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				method.invoke(dpm, new Object[] { componentName, true });
				return true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		} catch (NoSuchMethodException e) {
			Logger.e(TAG, "setActiveAdmin method not found",true);
			e.printStackTrace();
		}

		return false;

	}

}
