package com.single.code.tool.bluetooth.ble.api;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import com.single.code.tool.bluetooth.ble.callback.IBLECallback;
import com.single.code.tool.bluetooth.ble.protocol.HweReceiver;
import com.single.code.tool.bluetooth.ble.protocol.HweSender;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Created by andy 2016/1/13.
 * 这个类对bluetoothGattServer进行了封装
 */
public final class BLEServer {
    private static final String TAG ="BLEServer";

    private static BLEServer instance;

    private Context context;
    private BluetoothManager bluetoothManager;

    private BluetoothGattServer gattServer;
    private BluetoothGattServerCallback serverCallback;
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic notifyCharacteristic;

    private BluetoothDevice remoteDevice;

    private Set<IBLECallback> callbackSet = new CopyOnWriteArraySet<>();//在连接状态改变和收到信息时异步调用，不可以改变UI
//    private MsgReceiver msgReceiver;
//    private MsgSender msgSender;
    private HweReceiver hweReceiver;
    private HweSender hweSender;
    private boolean prepared;
    private boolean connected;
//    private MsgQueue<byte[]> msgQueue;//使用消息队列达到异步处理数据发送的问题
    private ConcurrentLinkedQueue linkedQueue;

    private boolean onWrite;//是否正在发送数据
    /**
     * 单例模式获取对象的方法
     *
     * @param context  context对象承接Android系统资源
     *
     * @return BLEServer的实例
     */
    public static BLEServer getInstance(Context context) {
        synchronized (BLEServer.class){
            if (instance == null) {
                instance = new BLEServer(context);
            } else {
                instance.context = context.getApplicationContext();
            }
        }
        return instance;
    }

    /**
     * 启动GattServer以被连接
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean startGattServer() {
        if (!prepared) {
            initGattServerCallback();
            initGattServer();
        }
        if (context == null) {
            return false;
        }
        Log.d(TAG, "startGattServer");
        //开启GattServer
        gattServer = bluetoothManager.openGattServer(context, serverCallback);
        if(gattServer==null){
            Log.d(TAG, "gattServer is null");
            Iterator<IBLECallback> iterator = callbackSet.iterator();
            while (iterator.hasNext()){
                IBLECallback callback =iterator.next();
                callback.onDisconnected();
            }
        }else {
            gattServer.addService(gattService);
        }
        return true;
    }
    public boolean isConnected(){
        return connected;
    }

    /**
     * 停止GattServer
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopGattServer() {
        Log.d(TAG, "stopGattServer");
        if (gattServer != null) {
            if (connected && remoteDevice != null) {
                gattServer.cancelConnection(remoteDevice);
            }
            gattServer.removeService(gattService);
            gattServer.close();
        }
        gattServer = null;
        gattService = null;
        prepared = false;
        remoteDevice=null;
    }

    /**
     * 发送数据给client
     *
     * @param data 要发送的数据
     */
    public void sendDataToClient(byte[] data) {
        if (connected&&hweSender!=null) {
            Log.d(TAG, "sendDataToClient");
            hweSender.sendMessage(data);
        }
//        if (connected&&msgSender!=null) {
//            Log.d(TAG,"sendDataToClient");
//            msgSender.sendMessage(data);
//        }
    }


    public void setCallback(IBLECallback callback) {
        callbackSet.add(callback);
    }
    public void removeCallBack(IBLECallback callback){
        callbackSet.remove(callback);
    }

    private BLEServer(Context context) {
        this.context = context.getApplicationContext();
        linkedQueue = new ConcurrentLinkedQueue();
//        msgQueue = new MsgQueue<>();
        hweSender = new HweSender(new HweSender.ISender() {
            @Override
            public void inputData(byte[] bytes) {
                linkedQueue.offer(Arrays.copyOf(bytes, bytes.length));
//                msgQueue.enQueue(Arrays.copyOf(bytes,bytes.length));
                startWrite();
            }
        });
        hweReceiver = new HweReceiver(new HweReceiver.IReceiver() {
            @Override
            public void receiveData(byte[] data) {
                Iterator<IBLECallback> iterator = callbackSet.iterator();
                while (iterator.hasNext()){
                    Log.d(TAG, "call back");
                    IBLECallback callback =iterator.next();
                    callback.onDataReceived(data,BLEProfile.Type_Server);
                }
            }
        });
//        msgSender = new MsgSender(new MsgSender.ISender() {
//            //发送数据（byte[]）的地方
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void inputData(byte[] bytes) {
//                msgQueue.enQueue(Arrays.copyOf(bytes,bytes.length));
//                startWrite();
////                if (remoteDevice != null) {
////                    notifyCharacteristic.setValue(Arrays.copyOf(bytes, bytes.length));
////                    gattServer.notifyCharacteristicChanged(remoteDevice, notifyCharacteristic, false);
////                }
//            }
//        });
//        msgReceiver = new MsgReceiver(new MsgReceiver.IReceiver() {
//            //String从这里整合过来
//            @Override
//            public void receiveData(byte[] data) {
//                Iterator<IBLECallback> iterator = callbackSet.iterator();
//                while (iterator.hasNext()){
//                    IBLECallback callback =iterator.next();
//                    callback.onDataReceived(data,BLEProfile.Type_Server);
//                }
//            }
//        });
        prepared = false;
        connected = false;
    }

    public void resetData(){
        Log.d(TAG, "resetData");
        onWrite = false;
        if(linkedQueue!=null&&!linkedQueue.isEmpty()){
            linkedQueue.clear();
            linkedQueue=null;
            linkedQueue = new ConcurrentLinkedQueue();
        }
//        if(!msgQueue.isEmpty()){
//            msgQueue = null;
//            msgQueue = new MsgQueue<>();
//        }
        if(hweReceiver!=null){
            hweReceiver.init();
        }
        if(hweSender!=null){
            hweSender.cancle();
        }
//        if(msgReceiver!=null){
//            msgReceiver.init();
//        }
    }
    private void startWrite(){
        if (onWrite) {
            return;
        }
        Log.d(TAG, "startWrite");
        nextWrite();
    }
    private void nextWrite(){
        if(linkedQueue.isEmpty()){
            onWrite = false;
            Log.d(TAG, "linkedQueue is empty");
            return;
        }
//        if (msgQueue.isEmpty()) {
//            onWrite = false;
//            Log.d(TAG,"msgQueue is empty");
//            return;
//        }
        Log.d(TAG, "nextWrite");
        write();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void write(){
        Log.d(TAG, "write");
        onWrite = true;
        byte[] bytes = (byte[]) linkedQueue.poll();
        if (remoteDevice != null) {
            notifyCharacteristic.setValue(Arrays.copyOf(bytes, bytes.length));
            gattServer.notifyCharacteristicChanged(remoteDevice, notifyCharacteristic, false);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initGattServerCallback() {
        Log.d(TAG, "initGattServerCallback");
        serverCallback = new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i(TAG, "someone connected to this device");
//                        Iterator<IBLECallback> iterator = callbackSet.iterator();
//                        while (iterator.hasNext()){
//                            IBLECallback callback =iterator.next();
//                            callback.onConnected();
//                        }
                        if(connected){
                            Log.d(TAG, "cancelConnection");
                            gattServer.cancelConnection(device);
                        }
                        connected = true;
                    }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.e(TAG, "STATE_DISCONNECTED " + newState);
                        if(remoteDevice!=null){
                            if(remoteDevice.getAddress().equals(device.getAddress())){
                                Log.e(TAG, "the disconnected from RemoteDevice " + newState);
                                connected = false;
                                disConnect();
                                Iterator<IBLECallback> iterator = callbackSet.iterator();
                                while (iterator.hasNext()){
                                    IBLECallback callback =iterator.next();
                                    callback.onDisconnected();
                                }
                            }
                        }else {
                            Log.e(TAG, "the connection disconnected " + newState);
                            connected = false;
                            disConnect();
                            Iterator<IBLECallback> iterator = callbackSet.iterator();
                            while (iterator.hasNext()){
                                IBLECallback callback =iterator.next();
                                callback.onDisconnected();
                            }
                        }
                    }else {
                        Log.e(TAG, "disconnect cient");
                        connected = false;
                        disConnect();
                        Iterator<IBLECallback> iterator = callbackSet.iterator();
                        while (iterator.hasNext()){
                            IBLECallback callback =iterator.next();
                            callback.onDisconnected();
                        }
                    }
                }else {
                    Log.e(TAG, "connect lost " + status);
                    connected = false;
                    disConnect();
                    Iterator<IBLECallback> iterator = callbackSet.iterator();
                    while (iterator.hasNext()){
                        IBLECallback callback =iterator.next();
                        callback.onDisconnected();
                    }
                }
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {

                super.onServiceAdded(status, service);
                Log.i(TAG, "onServiceAdded");
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                Log.i(TAG, "onCharacteristicReadRequest");
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                Log.i(TAG, "onCharacteristicWriteRequest");
                if (characteristic.getUuid().toString().equals(BLEProfile.UUID_CHARACTERISTIC_WRITE)) {
//                    characteristic.setValue(value);
//                    msgReceiver.outputData(value);
                    hweReceiver.OutputData(value);
                    gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
                } else {
                    gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, value);
                }
            }

            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor.getValue());
            }

            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
                Log.i(TAG, "onDescriptorWriteRequest");
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                remoteDevice = device;
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Iterator<IBLECallback> iterator = callbackSet.iterator();
                while (iterator.hasNext()){
                    IBLECallback callback =iterator.next();
                    callback.onConnected(BLEProfile.Type_Server);
                }
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
                Log.i(TAG, "onExecuteWrite");
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                super.onNotificationSent(device, status);
                Log.i(TAG, "onNotificationSent " + status);
                if(status== BluetoothGatt.GATT_SUCCESS){
                    nextWrite();
                }
            }

            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {
                super.onMtuChanged(device, mtu);
                Log.i(TAG, "onMtuChanged");
            }
        };

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public synchronized void disConnect(){
        if(remoteDevice!=null){
            if(gattServer!=null){
                Log.d(TAG, "disConnect");
                resetData();
                gattServer.cancelConnection(remoteDevice);
                remoteDevice=null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initGattServer() {
        if (context == null) {
            prepared = false;
            return;
        }
        Log.d(TAG, "initGattServer");
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothGattDescriptor gattDescriptor = new BluetoothGattDescriptor(UUID.randomUUID(), BluetoothGattDescriptor.PERMISSION_WRITE);
        notifyCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(BLEProfile.UUID_CHARACTERISTIC_NOTIFY),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ);
        notifyCharacteristic.addDescriptor(gattDescriptor);


        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(BLEProfile.UUID_CHARACTERISTIC_WRITE),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        gattService = new BluetoothGattService(UUID.fromString(BLEProfile.UUID_SERVICE), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        gattService.addCharacteristic(notifyCharacteristic);
        gattService.addCharacteristic(writeCharacteristic);
        prepared = true;

    }
}
