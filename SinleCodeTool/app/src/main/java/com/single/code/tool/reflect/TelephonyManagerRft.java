package com.single.code.tool.reflect;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.single.code.tool.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by yaoguoju on 2015/10/15.
 */
public class TelephonyManagerRft {
	private final static String ClassName = "android.telephony.TelephonyManager";
	private static String TAG = "TelephonyManagerRft";
	public static void setDataEnabled(boolean enable, Context context) {
		Class<?> c = null;
		try {
			c = Class.forName(ClassName, false, Thread.currentThread()
					.getContextClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, TelephonyManagerRft.class.getName()
					+ " not found");
		}
		try {
			Method meth = c.getDeclaredMethod("setDataEnabled",
					new Class[] { boolean.class });
			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(TELEPHONY_SERVICE);
				meth.invoke(tm, new Object[] { true });
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Logger.e(TAG, "setDataEnabled not found",true);
			e.printStackTrace();
		}

	}

	public static String getImsi(){
		TelephonyManager telephonyManager = null;
		String imsi = "";
		try {
			Class TelephonyManagerClass = Class.forName("android.telephony.TelephonyManager");
			Method getDefault = TelephonyManagerClass.getMethod("getDefault");
			Object object = getDefault.invoke(null);
			if (object != null) {
				telephonyManager = (TelephonyManager) object;
				Method getSubscriberId = TelephonyManagerClass.getMethod("getSubscriberId");
				imsi =(String) getSubscriberId.invoke(telephonyManager);
				Logger.d(TAG, "getLTEMdnByImsi imsi2:" + imsi);
			}


		} catch (ClassNotFoundException e) {
			Log.e(TAG, "ClassNotFoundException", e);
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "NoSuchMethodException", e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e(TAG, "InvocationTargetException", e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e(TAG, "IllegalAccessException", e);
			e.printStackTrace();
		}finally {
			return imsi;
		}
	}

	public static String getImsi(Context context, int subId) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(TELEPHONY_SERVICE);// 取得相关系统服务
		Class<?> telephonyManagerClass = null;
		String imsi = "";
		try {
			telephonyManagerClass = Class.forName("android.telephony.TelephonyManager");

			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
				Method method = telephonyManagerClass.getMethod("getSubscriberId", int.class);
				imsi = (String) method.invoke(telephonyManager, subId);
			} else if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP) {
				Method method = telephonyManagerClass.getMethod("getSubscriberId", long.class);
				imsi = (String) method.invoke(telephonyManager, (long) subId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			return imsi;
		}
	}
}
