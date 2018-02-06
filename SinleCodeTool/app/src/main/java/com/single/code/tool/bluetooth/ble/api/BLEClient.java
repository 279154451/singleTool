package com.single.code.tool.bluetooth.ble.api;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import com.single.code.tool.bluetooth.ble.callback.IBLECallback;
import com.single.code.tool.bluetooth.ble.protocol.HweReceiver;
import com.single.code.tool.bluetooth.ble.protocol.HweSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Created by andy on 2016/1/13.
 * 对bluetoothGatt进行了封装
 */
public final class BLEClient {
    private static final String TAG = "BLEClient";

    private Context mContext;

    private BluetoothGattCallback gattCallback;
    private BluetoothGatt bluetoothGatt;

//    private MsgReceiver msgReceiver;
//    private MsgSender msgSender;
    private HweSender hweSender;
    private HweReceiver hweReceiver;
    private Set<IBLECallback> ibleCallbackSet =  new CopyOnWriteArraySet<>();


    private BluetoothGattCharacteristic writeChannel;

    private boolean connected;

//    private MsgQueue<byte[]> msgQueue;//使用消息队列达到异步处理数据发送的问题
        private ConcurrentLinkedQueue linkedQueue;
    private boolean onWrite;//是否正在发送数据
    private static BLEClient instance;


    public static BLEClient getInstance(Context context){
        synchronized (BLEClient.class){
            if(instance==null){
                instance = new BLEClient(context);
            }
        }
        return instance;
    }

    private BLEClient(Context context){
        mContext =context.getApplicationContext() ;
        connected = false;
        hweSender = new HweSender(new HweSender.ISender() {
            @Override
            public void inputData(byte[] bytes) {
//                msgQueue.enQueue(Arrays.copyOf(bytes,bytes.length));
                linkedQueue.offer(Arrays.copyOf(bytes, bytes.length));
                startWrite();
            }
        });
        hweReceiver = new HweReceiver(new HweReceiver.IReceiver() {
            @Override
            public void receiveData(byte[] data) {
                Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
                while (iterator.hasNext()){
                    IBLECallback ibleCallback = iterator.next();
                    ibleCallback.onDataReceived(data,BLEProfile.Type_Client);
                }
            }
        });
//        msgSender = new MsgSender(new MsgSender.ISender() {
//            @Override
//            public void inputData(byte[] bytes) {
//                msgQueue.enQueue(Arrays.copyOf(bytes,bytes.length));
//                startWrite();
//            }
//        });
//        msgReceiver = new MsgReceiver(new MsgReceiver.IReceiver() {
//            @Override
//            public void receiveData(byte[] data) {
//                Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
//                while (iterator.hasNext()){
//                    IBLECallback ibleCallback = iterator.next();
//                    ibleCallback.onDataReceived(data,BLEProfile.Type_Client);
//                }
//            }
//        });
//        msgQueue = new MsgQueue<>();
        linkedQueue = new ConcurrentLinkedQueue();
        initGattCallback();
    }

    public void resetData(){
        Log.d(TAG, "resetData");
//        if(!msgQueue.isEmpty()){
//            msgQueue=null;
//            msgQueue =new MsgQueue<>();
//        }
        if(linkedQueue!=null&&!linkedQueue.isEmpty()){
            linkedQueue.clear();
            linkedQueue = null;
            linkedQueue = new ConcurrentLinkedQueue();
        }
        onWrite =false;
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
    public void setCallBack(IBLECallback callBack){
        ibleCallbackSet.add(callBack);
    }
    public void removeCallBack(IBLECallback callback){
        ibleCallbackSet.remove(callback);
    }

    /**
     * 开始使用Gatt连接
     *
     * @param address 要连接的蓝牙设备的地址
     * @return 是否连接成功
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean startConnect(String address) {
        if(!connected){
            Log.e(TAG, "start connect:" + address);
            if(mContext==null){
                Log.d(TAG, "mContext is null");
                return false;
            }
            stopConnect();
            if(bluetoothGatt!=null&&bluetoothGatt.getDevice().getAddress().equals(address)){
                Log.d(TAG, "reconnect");
                return bluetoothGatt.connect();//reconnect
            }else {
                Log.d(TAG, "new connect");
                BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                bluetoothGatt = bluetoothAdapter.getRemoteDevice(address).connectGatt(mContext, false, gattCallback);
            }
        }else {
            Log.d(TAG, "BLE connected");
        }
//        refreshDeviceCache(bluetoothGatt);
        return true;
    }

    /**
     * 刷新缓冲
     * @param gatt
     * @return
     */
    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
//            BluetoothGatt localBluetoothGatt = gatt;
//            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
//            if (localMethod != null) {
//                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
//                return bool;
//            }
            Method refresh = gatt.getClass().getMethod("refresh");
            if (refresh != null) {
                boolean bool = (Boolean) refresh.invoke(gatt);
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e(TAG, "An exception occured while refreshing device");
        }
        return false;
    }

    public boolean isConnected(){
        return connected;
    }

    /**
     * 断开连接
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopConnect() {
        resetData();
        if (bluetoothGatt!=null) {
            bluetoothGatt.disconnect();
            if(bluetoothGatt!=null){
                bluetoothGatt.close();
            }
            bluetoothGatt=null;
        }
        connected = false;
    }


    /**
     * 发送消数据
     *
     * @param data 要发送的数据
     */
    public void sendDataToServer(byte[] data) {
        if (connected) {
            Log.d(TAG, "sendDataToServer");
            hweSender.sendMessage(data);
//            msgSender.sendMessage(data);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initGattCallback() {
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.i(TAG, "onConnectionStateChange");
                if(status == BluetoothGatt.GATT_SUCCESS){
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i(TAG, "Client success & start discover services");
                        gatt.discoverServices();
                        connected = true;
                    }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.e(TAG, "STATE_DISCONNECTED");
                        Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
                        while (iterator.hasNext()){
                            IBLECallback ibleCallback = iterator.next();
                            ibleCallback.onDisconnected();
                        }
                        gatt.disconnect();
                        gatt.close();
                        stopConnect();
//                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                        if(bluetoothAdapter!=null){
//                            if(bluetoothAdapter.getState()==BluetoothAdapter.STATE_ON){
//                                boolean success =gatt.connect();//重新发起连接
//                                if(!success){
//                                    Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
//                                    while (iterator.hasNext()){
//                                        IBLECallback ibleCallback = iterator.next();
//                                        ibleCallback.onDisconnected();
//                                    }
//                                    gatt.disconnect();
//                                    gatt.close();
//                                    stopConnect();
//                                }
//                            }else {
//                                Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
//                                while (iterator.hasNext()){
//                                    IBLECallback ibleCallback = iterator.next();
//                                    ibleCallback.onDisconnected();
//                                }
//                                gatt.disconnect();
//                                gatt.close();
//                                stopConnect();
//                            }
//                        }else {
//                            Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
//                            while (iterator.hasNext()){
//                                IBLECallback ibleCallback = iterator.next();
//                                ibleCallback.onDisconnected();
//                            }
//                            gatt.disconnect();
//                            gatt.close();
//                            stopConnect();
//                        }
                    }else {
                        Log.e(TAG, "disconnect server");
                        gatt.disconnect();
                        gatt.close();
                        stopConnect();
                        //未连接上server
                        Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
                        while (iterator.hasNext()){
                            IBLECallback ibleCallback = iterator.next();
                            ibleCallback.onDisconnected();
                        }
                        connected = false;
                    }
                }else {
                    boolean bl =refreshDeviceCache(gatt);
                    Log.d(TAG, "refresh :" + bl);
                    Log.e(TAG, "connect lost :" + status);
                    gatt.disconnect();
                    gatt.close();
                    stopConnect();
                    //断开连接
                    Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
                    while (iterator.hasNext()){
                        IBLECallback ibleCallback = iterator.next();
                        ibleCallback.onDisconnected();
                    }
                    connected = false;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.i(TAG, "onServicesDiscovered");

                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "Discover Services failed");
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(BLEProfile.UUID_SERVICE));
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(BLEProfile.UUID_CHARACTERISTIC_NOTIFY));
                    if (characteristic != null) {
                        //订阅通知，这段代码对iOS的peripheral也能订阅
                        Log.i(TAG, "SetNotification");
                        bluetoothGatt.setCharacteristicNotification(characteristic, true);
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }

                    } else {
                        Log.e(TAG, "The notify characteristic is null");
                    }
                    writeChannel = service.getCharacteristic(UUID.fromString(BLEProfile.UUID_CHARACTERISTIC_WRITE));
                    if (characteristic == null) {
                        Log.e(TAG, "The write characteristic is null");
                    }
                } else {
                    Log.e(TAG, "The special service is null");
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                Log.i(TAG, "onCharacteristicRead");
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "Read Characteristic failed");
                }

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.i(TAG, "onCharacteristicWrite  :" + status);
                nextWrite();
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                //处理notify
                super.onCharacteristicChanged(gatt, characteristic);
                Log.i(TAG, "Notify: onCharacteristicChange");
                byte[] value = characteristic.getValue();
                hweReceiver.OutputData(value);
//                msgReceiver.outputData(value);//将数据整合成String
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                Log.i(TAG, "onDescriptorRead");
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.i(TAG, "onDescriptorWrite");
                Iterator<IBLECallback> iterator = ibleCallbackSet.iterator();
                while (iterator.hasNext()){
                    IBLECallback ibleCallback = iterator.next();
                    ibleCallback.onConnected(BLEProfile.Type_Client);
                }
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                Log.i(TAG, "onReliableWriteCompleted");
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Log.i(TAG, "onReadRemoteRssi");
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                Log.i(TAG, "onMtuChanged");
            }
        };
    }





    /*
     * 控制稳定write区域
     */
    private void startWrite() {
        if (onWrite) {
            return;
        }
        nextWrite();
    }

    /*
     * 控制稳定write区域
     */
    private void nextWrite() {
        if(linkedQueue.isEmpty()){
            onWrite = false;
            return;
        }
//        if (msgQueue.isEmpty()) {
//            onWrite = false;
//            return;
//        }
        write();
    }

    /*
     * 控制稳定write区域
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void write() {
        onWrite = true;
//        byte[] bytes = msgQueue.deQueue();
        byte[] bytes = (byte[]) linkedQueue.poll();
        try {
            writeChannel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            writeChannel.setValue(bytes);
            bluetoothGatt.writeCharacteristic(writeChannel);
        } catch (NullPointerException e) {
            Log.e(TAG, "null pointer on characteristic");
        }
    }

}
