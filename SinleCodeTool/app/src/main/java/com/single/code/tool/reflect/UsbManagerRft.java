package com.single.code.tool.reflect;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yaoguoju on 2015/10/15.
 */
public class UsbManagerRft {
	private final static String ClassName = "android.hardware.usb.UsbManager";
    private static String TAG = "UsbManagerRft";

    /**
     * 设置usb为充电模式，禁用USB读写
     * @param context
     */
	public static void setUsbCharging(Context context) {
		Class<?> c = null;
		try {
			c = Class.forName(ClassName, false, Thread.currentThread()
                    .getContextClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, UsbManagerRft.class.getName()
                    + " not found");
		}
		try {
            if(Build.VERSION.SDK_INT<=22){
                Method meth = c.getDeclaredMethod("setCurrentFunction",
                        new Class[] { String.class, boolean.class });
                try {
                    UsbManager um = (UsbManager) context
                            .getSystemService(Context.USB_SERVICE);
                    meth.invoke(um, new Object[] { "charging", true });
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
            }else {
                Method method = c.getDeclaredMethod("setCurrentFunction",
                        new Class[] { String.class});
                try {
                    UsbManager um = (UsbManager) context
                            .getSystemService(Context.USB_SERVICE);
                    method.invoke(um, new Object[] { "charging" });
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
            }
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "setCurrentFunction not found");
			e.printStackTrace();
		}

	}

    /**
     * 设置USB为媒体设备连接
     */
    public static void setUsbToMtp(Context context){
        Class<?> c = null;
        try {
            c = Class.forName(ClassName, false, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, UsbManagerRft.class.getName() + " not found");
        }
        try {
            if(Build.VERSION.SDK_INT<=22){
                Method meth = c.getDeclaredMethod("setCurrentFunction",
                        new Class[] { String.class, boolean.class });
                try {
                    UsbManager um = (UsbManager) context
                            .getSystemService(Context.USB_SERVICE);
                    meth.invoke(um, new Object[] { "mtp", true });
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
            }else {
                Method method = c.getDeclaredMethod("setCurrentFunction",
                        new Class[] { String.class});
                try {
                    UsbManager um = (UsbManager) context
                            .getSystemService(Context.USB_SERVICE);
                    method.invoke(um, new Object[] { "mtp" });
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
            }
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "setCurrentFunction not found");
            e.printStackTrace();
        }
    }

}
