package com.single.code.tool.bluetooth.classic.api;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 
 * @author yaoguoju
 */
@SuppressLint("NewApi")
public class BluetoothApi {
	/**
	 * 获取设备蓝牙服务
	 * @param context
	 * @return
	 */
	public static BluetoothManager getBleManager(Context context) {
		BluetoothManager bm = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		return bm;
	}
	
    /**
     * 获取蓝牙适配器
     * @param context
     * @return
     */
	public static BluetoothAdapter getBleAdpter(Context context) {
		BluetoothManager bm = getBleManager(context);
		if (bm == null) {
			return null;
		}
		if(bm.getAdapter() == null) {
		}
		return bm.getAdapter();
	}

    /**
     * 蓝牙开关状态
     * @param context
     * @return
     */
    public static boolean BluetoothEnable(Context context){
        BluetoothAdapter bluetoothAdapter = getBleAdpter(context);
        if(bluetoothAdapter!=null){
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 设置蓝牙状态
     * @param context
     * @param enable
     */
    public static void setBluetoothState(Context context,boolean enable){
        BluetoothAdapter bluetoothAdapter = getBleAdpter(context);
        if(bluetoothAdapter!=null){
            if(enable){
                bluetoothAdapter.enable();
            }else {
                bluetoothAdapter.disable();
            }
        }
    }

    /**
     * 获取蓝牙的mac地址
     * @param context
     * @return
     */
    public static String getBluetoothDeviceMac(Context context){
        BluetoothManager btManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        String btMac;
//在6.0版本以后，获取硬件ID变得更加严格，所以通过设备的地址映射得到mac地址
        if(Build.VERSION.SDK_INT >= 23) {
            btMac = android.provider.Settings.Secure.getString(context.getApplicationContext().getContentResolver(), "bluetooth_address");
            Log.d("MACADDRESS", "mac address :" + btMac);
        }else {
            btMac = btAdapter.getAddress();
        }
        return btMac;
    }

	/**
	 * 初始化设备蓝牙
	 * @param context
	 * @return
	 */
	public static boolean initDeviceBle(Context context) {
		boolean inited = false;
		BluetoothAdapter bleAdapter = getBleAdpter(context);
		if(bleAdapter == null) {
            return inited;
		}else {
		    if(!bleAdapter.isEnabled()) {//判断蓝牙是否开启
			    inited  = bleAdapter.enable();
	     	}else {
	     		inited = true;
	     	}
		}
		return inited;
	}

    /**
     * 设置蓝牙始终可见
     * @param timeout
     */
    public static void setDiscoverableTimeout(int timeout) {
        BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭蓝牙可见性
     */
    public static void closeDiscoverableTimeout() {
        BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
