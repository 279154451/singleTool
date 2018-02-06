package com.single.code.tool.bluetooth.ble.api;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;


import com.single.code.tool.bluetooth.ble.utils.BytesUtil;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * Created by andy on 2016/1/13.
 * 对bluetoothLeAdvertiser进行了封装
 */
public final class BLEAdvertiser {
    private static final String TAG = "BLEAdvertiser";

    private static BLEAdvertiser instance;

    private BluetoothLeAdvertiser advertiser;
    private AdvertiseCallback advertiseCallback;
    private AdvertiseSettings advertiseSettings;
    private AdvertiseData advertiseData;
    private AdvertiseData advertiseReponse;
    private String userNo;

    private WeakReference<Context> contextWeakReference;//使用弱引用防止内存泄漏
    private IAdvertiseResultListener advertiseResultListener;

    private boolean prepared;//是否准备好广播


    /**
     * 开始广播
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertise() {
        if (!prepared) {
            initAdvertiseData();
        }
        if (advertiser == null) {
            return;
        }
//        advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
        advertiser.startAdvertising(advertiseSettings,advertiseData,advertiseReponse,advertiseCallback);
    }

    /**
     * 停止广播
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopAdvertise() {
        if (prepared) {
            advertiser.stopAdvertising(advertiseCallback);
            advertiser= null;
        }
        prepared = false;
    }


    public BLEAdvertiser(Context context, IAdvertiseResultListener listener) {
        prepared = false;
        contextWeakReference = new WeakReference<>(context);
        advertiseResultListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initAdvertiseData() {
        //初始化Advertise的设定
        Context mContext = contextWeakReference.get();
        if (mContext == null) {
            prepared = false;
            return;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.i(TAG, "Advertise success");

                advertiseResultListener.onAdvertiseSuccess();

                if (settingsInEffect != null) {
                    Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                            + " timeout=" + settingsInEffect.getTimeout());
                } else {
                    Log.e(TAG, "onStartSuccess, settingInEffect is null");
                }
                Log.i(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.e(TAG, "Advertise failed.Error code: " + errorCode);
                advertiseResultListener.onAdvertiseFailed(errorCode);
            }
        };
//        ByteBuffer mManufacturerData = ByteBuffer.allocate(13);
//        UUID serveruuid = UUID.fromString(BLEProfile.UUID_SERVICE);
//        byte[] uuid = UUIDUtil.toByte(serveruuid);
        setAdvertiseSetting();
        setAdvertiseData();
        prepared = true;
    }


    /**
     * 检查是否能够广播
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean ckeckAdvertise(Context context){
        return ((BluetoothManager)context.getSystemService(BLUETOOTH_SERVICE)).getAdapter()
                .isMultipleAdvertisementSupported();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAdvertiseSetting(){
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingsBuilder.setConnectable(true);
        settingsBuilder.setTimeout(0);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        advertiseSettings = settingsBuilder.build();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAdvertiseData(){
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        if(!TextUtils.isEmpty(userNo)){
            byte[] number = BytesUtil.getByteByNumber(userNo);
            int length = number.length;
            length = length+2;
            ByteBuffer mManufacturerData = ByteBuffer.allocate(length);
            Log.d(TAG, "number size:" + number.length);
            mManufacturerData.put(0, (byte)0xBE); // Beacon Identifier
            mManufacturerData.put(1, (byte)0xAC); // Beacon Identifier
            for (int i=2; i<=length-1; i++) {
                mManufacturerData.put(i, number[i-2]); // adding the UUID
            }
            dataBuilder.addManufacturerData(124,mManufacturerData.array());
        }
//        dataBuilder.setIncludeDeviceName(true);
        dataBuilder.addServiceUuid(ParcelUuid.fromString(BLEProfile.UUID_SERVICE));
        advertiseData = dataBuilder.build();
        advertiseReponse = new AdvertiseData.Builder().setIncludeDeviceName(true).build();
    }


    public interface IAdvertiseResultListener {
        /**
         * 这个方法会在广播成功时调用
         */
        void onAdvertiseSuccess();

        /**
         * 这个方法会在广播失败时调用
         * @param errorCode 请查阅AdvertiseCallback的API
         */
        void onAdvertiseFailed(int errorCode);
    }

}
