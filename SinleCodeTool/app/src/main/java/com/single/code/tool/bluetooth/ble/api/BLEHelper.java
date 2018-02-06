package com.single.code.tool.bluetooth.ble.api;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.single.code.tool.bluetooth.DeviceManager;
import com.single.code.tool.bluetooth.ble.callback.IBLECallback;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by yxl on 2017/4/20.
 */

public enum  BLEHelper implements IBLECallback {
    HELPER;
    private String TAG = "BLEHelper";
    private BLEClient bleClient;
    private BLEServer bleServer;
    private BLEAdvertiser bleAdvertiser;
    public String currentAddress="";
    private Set<IBLECallback> callbackSet = new CopyOnWriteArraySet<>();

    public void setBLECallBack(IBLECallback callBack){
        callbackSet.add(callBack);
    }
    public void removeBLECallBack(IBLECallback callback){
        callbackSet.remove(callback);
    }
    private  final Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                Log.d(TAG, "handler message null");
                return;
            }
            Log.d(TAG, "handler message :" + msg.what);
            switch (msg.what){
                case BLEProfile.MESSAGE_CONNECT:
                    Iterator<IBLECallback> iterator = callbackSet.iterator();
                    while (iterator.hasNext()){
                        IBLECallback callback = iterator.next();
                        callback.onConnected(msg.arg1);
                    }
                    break;
                case BLEProfile.MESSAGE_DISCONNECT:
                    Iterator<IBLECallback> iterator1 = callbackSet.iterator();
                    while (iterator1.hasNext()){
                        IBLECallback callback = iterator1.next();
                        callback.onDisconnected();
                    }
                    if(bleServer!=null){
                        bleServer.resetData();
                    }
                    if(bleClient!=null){
                        bleClient.resetData();
                    }
                    break;
                case BLEProfile.MESSAGE_READ:
                    Iterator<IBLECallback> iterator2 = callbackSet.iterator();
                    while (iterator2.hasNext()){
                        Log.d(TAG, "MESSAGE_READ");
                        IBLECallback callback = iterator2.next();
                        callback.onDataReceived((byte[])msg.obj,msg.arg1);
                    }
                    break;
                case BLEProfile.MESSAGE_DO_NOTHING:
                    break;
            }
        }
    };

    public void initHandle(){
        mHandler.obtainMessage(BLEProfile.MESSAGE_DO_NOTHING).sendToTarget();
    }

    /**
     * 发送数据给client
     * @param data
     */
    public void sendDataToClient(byte[] data){
        if(bleServer!=null){
            bleServer.sendDataToClient(data);
        }else {
            mHandler.obtainMessage(BLEProfile.MESSAGE_DISCONNECT).sendToTarget();
            Log.d(TAG, "未连接");
        }
    }

    /**
     * 发送数据给server
     * @param data
     */
    public void sendDataToServer(byte[] data){
        if(bleClient!=null){
            bleClient.sendDataToServer(data);
        }else {
            mHandler.obtainMessage(BLEProfile.MESSAGE_DISCONNECT).sendToTarget();
            Log.d(TAG, "未连接");
        }
    }

    public boolean isConnected(){
        boolean connected = false;
        if(bleClient!=null){
            connected =bleClient.isConnected();
        }
        if(bleServer!=null){
            connected = bleServer.isConnected();
        }
        return connected;
    }
    /**
     * 客户端发起连接
     * @param bleAddress
     */
    public synchronized void startConnect(Context context, String bleAddress,String BleName){
        if(isConnected()){
            Log.d(TAG, "device connected now,please disconnect current connected");
            return;
//            stopBleServer();
//            bleClient.stopConnect();
//            bleClient.removeCallBack(this);
//            bleClient = null;
        }
//        if(bleClient!=null){
//            stopConnect();
//        }
        bleClient = BLEClient.getInstance(context);
        if(bleClient!=null){
            Log.d(TAG, "startConnect");
            bleClient.setCallBack(this);
            bleClient.startConnect(bleAddress);
            currentAddress = bleAddress;
        }
        DeviceManager.MANAGER.setName(BleName);
        DeviceManager.MANAGER.setCurrentBleAddress(bleAddress);
    }

    /**
     * 客户端主动断开连接
     */
    public synchronized void stopConnect(boolean notify){
        if(bleClient!=null){
            Log.d(TAG, "stopConnect");
            bleClient.stopConnect();
            bleClient.removeCallBack(this);
            if(notify){
                mHandler.obtainMessage(BLEProfile.MESSAGE_DISCONNECT).sendToTarget();
            }
            bleClient =null;
        }
    }

    /**
     * 服务端开启广播
     * @param listener
     */
    public void startBleAdvertiser(Context context, BLEAdvertiser.IAdvertiseResultListener listener){
        if(bleAdvertiser==null){
            bleAdvertiser = new BLEAdvertiser(context,listener);
        }
        if(BluetoothAdapter.getDefaultAdapter().getState() == BluetoothAdapter.STATE_ON){
            bleAdvertiser.startAdvertise();
        }else {
            Log.e(TAG, "bluetooth not open can't advertiser");
        }
    }

    /**
     * 服务端关闭广播
     */
    public void stopBleAdverstiser(){
        if(bleAdvertiser!=null){
            if(BluetoothAdapter.getDefaultAdapter().getState() == BluetoothAdapter.STATE_ON){
                bleAdvertiser.stopAdvertise();
                bleAdvertiser= null;
            }
        }
    }
    /**
     * 启动gatt服务器
     */
    public void startBleServer(Context context){
        Log.d(TAG, "startBleServer");
        bleServer =BLEServer.getInstance(context);
        if(bleServer!=null){
            bleServer.setCallback(this);
            bleServer.startGattServer();
        }else {
            Log.d(TAG, "startBleServer  ble server is null");
        }
    }

    public void DisConnectFromServer(){
        Log.d(TAG, "DisConnectFromServer");
        if(bleServer!=null){
            bleServer.disConnect();
        }
    }

    /**
     * 关闭gatt服务器
     */
    public void stopBleServer(){
        if(bleServer!=null){
            if(BluetoothAdapter.getDefaultAdapter().getState()== BluetoothAdapter.STATE_ON|| BluetoothAdapter.getDefaultAdapter().getState()== BluetoothAdapter.STATE_TURNING_OFF){
                Log.d(TAG, "stopBleServer");
                bleServer.stopGattServer();
                bleServer.removeCallBack(this);
                bleServer = null;
            }
        }
//        if(bleServer!=null&&BluetoothAdapter.getDefaultAdapter().getState()==BluetoothAdapter.STATE_ON){
//            Log.d(TAG,"stopBleServer");
//            bleServer.stopGattServer();
//            bleServer.removeCallBack(this);
//            bleServer = null;
//        }
    }


    @Override
    public void onConnected(int type) {//连接建立
        Log.d(TAG, "连接成功");
        mHandler.obtainMessage(BLEProfile.MESSAGE_CONNECT,type,-1).sendToTarget();

    }

    @Override
    public void onDisconnected() {//连接断开
        Log.d(TAG, "连接断开");
        if(bleServer!=null){
            bleServer.resetData();
        }
        if(bleClient!=null){
            bleClient.resetData();
        }
        mHandler.obtainMessage(BLEProfile.MESSAGE_DISCONNECT).sendToTarget();
    }

    @Override
    public void onDataReceived(byte[] data,int type) {//接收到数据
        mHandler.obtainMessage(BLEProfile.MESSAGE_READ,type,-1,data).sendToTarget();
    }
}
