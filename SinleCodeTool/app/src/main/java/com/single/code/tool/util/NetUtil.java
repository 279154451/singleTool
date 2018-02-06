package com.single.code.tool.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * 网络
 * Created by Administrator on 2017/11/6.
 */
public class NetUtil {
    public static class NetState{
        public static int Net2G = 1;
        public static int Net3G = 2;
        public static int Net4G = 3;
        public static int NetNull = 4;
    }

    public static int getNetState(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int NetWorkType = NetState.NetNull;
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                int netState = networkInfo.getSubtype();
                String _strSubTypeName = networkInfo.getSubtypeName();
                if (networkInfo != null) {
                    switch (netState) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            NetWorkType = NetState.Net2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            NetWorkType = NetState.Net3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            NetWorkType = NetState.Net4G;
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                NetWorkType = NetState.Net3G;
                            } else {
                                NetWorkType = NetState.NetNull;
                            }
                            break;
                    }
                }
            }
        }
        return NetWorkType;
    }

    /**
     * 判断当前网络是否可用
     * @param context
     * @return
     */
    public static synchronized boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null){
            return false;
        }else {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos!=null&& networkInfos.length>0){
                for(int i=0;i<networkInfos.length;i++){
                    if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 判断wifi是否连接
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        boolean connected = false;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            connected = mWiFiNetworkInfo.isAvailable()&&mWiFiNetworkInfo.isConnected();

        }
        return connected;
    }

    /**
     * 判断数据网络是否打开
     * @param context
     * @return
     */
    public static boolean isMobileNet(Context context) {
        Class[]getArgarray = null;
        Object[] getArgInvoke = null;
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager!=null){
            if(!hasSimCard(telephonyManager)|| isAirplaneMode(context)){
                return false;
            }
            try{
                Method mGetMethod = telephonyManager.getClass().getMethod("getDataEnabled",getArgarray);
//                mGetMethod.setAccessible(true);
                boolean isopen = (boolean)mGetMethod.invoke(telephonyManager,getArgInvoke);
                if(isopen){
                    return true;
                }else {
                    return false;
                }
            }catch (Exception e){
            }
        }else {
        }
        return false;
    }
    /**
     * 判断是否有SIM卡，且SIM卡有效
     * @param teleManager
     * @return
     */
    private static boolean hasSimCard(TelephonyManager teleManager){
        TelephonyManager telephonyManager =teleManager;
        boolean result ;
        if(teleManager!=null){
            result =true;
            int simState = telephonyManager.getSimState();
            switch (simState){
                case TelephonyManager.SIM_STATE_ABSENT:
                    result = false;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    result =false;
                    break;
                case 8:
                    result =false;
                    break;
            }
        }else {
            result = false;
        }
        return result;
    }

    /**
     * 判断当前是否为飞行模式
     * @param context
     * @return 0：非飞行模式，1：飞行模式
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneMode(Context context){
        return (Settings.Global.getInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,0) == 1?true:false);
    }
}