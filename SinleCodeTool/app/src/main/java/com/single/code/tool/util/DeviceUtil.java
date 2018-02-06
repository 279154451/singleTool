package com.single.code.tool.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * android 设备
 * Created by Administrator on 2017/11/6.
 */
public class DeviceUtil {

    /**
     * 打开设备Gps
     * @param context
     * @param open
     */
    public static void setDeviceGpsState(Context context,boolean open) {
        if(open) {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        }else {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
        }
    }


    /**
     * 设置手机MIC状态
     * @param context
     * @param open true :静音MIC false :取消MIC静音
     */
    public static void setDeviceMicState(Context context,boolean open){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(am != null ) {
            try{
                int currentMode = am.getMode();
                am.setMode(AudioManager.MODE_NORMAL);
                am.setMicrophoneMute(open);
                am.setMode(currentMode);
            }catch (Exception e){

            }
        }
    }

    /**
     * 设置数据网络状态
     */
    public static void setDataEnabled(boolean enable, Context context) {
        String ClassName = "android.telephony.TelephonyManager";
        Class<?> c = null;
        try {
            c = Class.forName(ClassName, false, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Method meth = c.getDeclaredMethod("setDataEnabled",
                    new Class[] { boolean.class });

            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            meth.invoke(tm, enable);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 切换手机飞行模式状态
     * @param context
     * @param enabling true :打开飞行模式，false：关闭飞行模式
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setAirplaneMode(Context context, boolean enabling) {
        Settings.Global.putInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,enabling ?1:0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        context.sendBroadcast(intent);
    }

    /**
     * 移除WiFi链接
     * @param context
     */
    public static void removeWifiConnect(Context context,WifiInfo wifiInfo){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            wifiManager.disableNetwork(wifiInfo.getNetworkId());
            wifiManager.disconnect();
        }

    }

    /**
     * 判断wifi是否打开
     * @param context
     * @return
     */
    public static boolean isWifiEnable(Context context){
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(manager!=null&&manager.isWifiEnabled()){
            return true;
        }
        return false;
    }
}