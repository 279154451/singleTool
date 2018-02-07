package com.single.code.tool.bluetooth.classic.api;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;


import com.single.code.tool.bluetooth.classic.callback.IPairCallback;
import com.single.code.tool.bluetooth.classic.callback.IScanCallback;
import com.single.code.tool.bluetooth.classic.receiver.PairBroadcastReceiver;
import com.single.code.tool.bluetooth.classic.receiver.ScanBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxl on 2017/2/13.
 */

public class BluetoothScanner {

    private BluetoothAdapter mBleAdpater;
    private List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
    private Context mContext;
    private IScanCallback<BluetoothDevice> scanCallback;
    private ScanBroadcastReceiver scanBroadcastReceiver;
    private PairBroadcastReceiver mPairBroadcastReceiver;
    public BluetoothScanner(Context context, IScanCallback<BluetoothDevice> scanCallback){
        mContext = context.getApplicationContext();
       this.scanCallback = scanCallback;
    }
    /**
     * 获取到扫描到设备的列表
     * @return
     */
    public List<BluetoothDevice> getBleDevices() {
        return devicelist;
    }

    /**
     * 开启蓝牙设备扫描
     */
    public void startScan() {
        if(BluetoothApi.initDeviceBle(mContext)) {
            if(scanCallback != null&& scanBroadcastReceiver==null){
                scanBroadcastReceiver = new ScanBroadcastReceiver(scanCallback);
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(scanBroadcastReceiver, intentFilter);
            mBleAdpater = BluetoothApi.getBleAdpter(mContext);
            mBleAdpater.startDiscovery();
        }
    }

    /**
     * 配对监听
     * @param context
     * @param pairCallback
     */
    public void registerPairListener(Context context, IPairCallback pairCallback){
        if(mPairBroadcastReceiver == null){
            mPairBroadcastReceiver = new PairBroadcastReceiver(pairCallback);
        }
        //注册蓝牙配对监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(mPairBroadcastReceiver, intentFilter);
    }

    /**
     * 取消配对监听
     * @param context
     */
    public void unregisterPairListener(Context context){
        if(mPairBroadcastReceiver!=null){
            try {
                context.unregisterReceiver(mPairBroadcastReceiver);
            }catch (Exception e){

            }
        }
    }
    /**
     * 停止蓝牙设备扫描
     */
    public void stopScan() {
        if(BluetoothApi.initDeviceBle(mContext)) {
            if(scanBroadcastReceiver!=null){
                mContext.unregisterReceiver(scanBroadcastReceiver);
            }
            mBleAdpater = BluetoothApi.getBleAdpter(mContext);
            mBleAdpater.cancelDiscovery();
//            devicelist.clear();
        }
    }
}
