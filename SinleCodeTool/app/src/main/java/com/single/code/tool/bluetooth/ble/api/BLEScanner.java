package com.single.code.tool.bluetooth.ble.api;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;


import com.single.code.tool.bluetooth.DeviceManager;
import com.single.code.tool.bluetooth.HooweDevice;
import com.single.code.tool.bluetooth.ble.entity.ScanData;
import com.single.code.tool.bluetooth.ble.utils.BytesUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 2016/1/13.
 * 这个类对BluetoothLeScanner进行了封装
 */
public final class BLEScanner {

    private static final String TAG = "BLEScanner";

    private WeakReference<Context> contextWeakReference;

    private IScanResultListener scanResultListener;
    private BluetoothLeScanner scanner;
    private ScanCallback scanCallback;
    private ScanSettings scanSettings;
    private List<ScanFilter> filters;
    private static Map<String,ScanData> scanDataMap = new ConcurrentHashMap<>();
    private static BLEScanner instance;

    /**
     * 单例模式
     *
     * @param context  保存context的引用
     * @param listener 扫描结果的listener
     * @return BLEScanner的实例
     */
    public static BLEScanner getInstance(Context context, IScanResultListener listener) {
        if (instance == null) {
            instance = new BLEScanner(context);
        } else {
            instance.contextWeakReference = new WeakReference<Context>(context);
        }
        instance.scanResultListener = listener;
        return instance;
    }

    /**
     * 开始扫描周围的设备
     *
     * @return 开始扫描成功返回true, 否则返回false
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean startScan() {
        scanDataMap.clear();
        Context context = contextWeakReference.get();
        if (context == null) {
            return false;
        }

        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(bluetoothAdapter!=null&&bluetoothAdapter.getState()== BluetoothAdapter.STATE_ON){
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            if (scanner == null) {
                Log.e(TAG, "bluetoothLeScanner is null");
                return false;
            }
            scanner.startScan(filters, scanSettings, scanCallback);
        }else {
            Log.e(TAG, "bluetoothLeScanner is null or bluetooth not open");
            return false;
        }
        Log.i(TAG, "Start scan success");
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScan() {
        if (scanner == null || scanCallback == null) {
            return;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter!=null&&adapter.getState()== BluetoothAdapter.STATE_ON){
            scanner.stopScan(scanCallback);
        }
        if(scanResultListener!=null){
            scanResultListener = null;
        }
        scanDataMap.clear();
    }

    private BLEScanner(Context context) {
        contextWeakReference = new WeakReference<>(context);
        initScanData();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initScanData() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.i(TAG, "onScanResult" + result);
                ScanData scanData = new ScanData();
                String bleaddress = result.getDevice().getAddress();
                String name;
                String phone="";
                ScanRecord scanRecord = result.getScanRecord();
                byte[] ManufacturerSpecificData = scanRecord.getManufacturerSpecificData(124);
                if(ManufacturerSpecificData!=null){
                    int size = ManufacturerSpecificData.length;
                    byte[] number = new byte[size-2];
                    System.arraycopy(ManufacturerSpecificData, 2, number, 0, size - 2);
                    phone = BytesUtil.getNumberByBytes(number);
                    Log.d(TAG, "phone number :" + phone);
                }
                if(scanRecord==null){
                    name = "unknown";
                }else {
                    name =scanRecord.getDeviceName();
                    if(TextUtils.isEmpty(name)){
                        name = "unknown";
                    }
                }
                scanData.setBleAddress(bleaddress);
                scanData.setDeviceName(name);
                scanData.setPhoneNumber(phone);
                synchronized (this){
                    boolean isPrivateUuid = false;
                    int flage = scanRecord.getAdvertiseFlags();
                    List<ParcelUuid> serviceUUids = scanRecord.getServiceUuids();
                    if(serviceUUids!=null){
                        for(int i=0;i<serviceUUids.size();i++){
                            ParcelUuid parcelUuid = serviceUUids.get(i);
                            String uuid =parcelUuid.getUuid().toString();
                            if(!TextUtils.isEmpty(uuid)&&uuid.equals(BLEProfile.UUID_SERVICE)){
                                isPrivateUuid= true;
                            }
                        }
                    }
                    if(flage == AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM&& isPrivateUuid){
                        if(!scanDataMap.containsKey(bleaddress)){
                            scanDataMap.put(bleaddress,scanData);
                            if(scanResultListener!=null){
                                HooweDevice device = DeviceManager.MANAGER.getCurrentHooweDevice();
                                String deviceName = device.getName();
                                if(!TextUtils.isEmpty(deviceName)&&deviceName.equals(name)){
                                    scanData.setLcdevice(true);
                                }else {
                                    scanData.setLcdevice(false);
                                }
                                scanResultListener.onResultReceived(scanData);
                            }
                        }
                        if(scanResultListener!=null){
                            scanResultListener.onScanUpdate(scanData);
                        }
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.e(TAG, "onBatchScanResults");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e(TAG, "onScanFailed :" + errorCode);
                scanResultListener.onScanFailed(errorCode);
            }
        };
        filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().build());
        scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
    }

    public interface IScanResultListener {
        /**
         * 这个方法会在成功接收到扫描结果时调用
         *scanDatas 扫描到的ble设备
         */
        void onResultReceived(ScanData scanData);

        /**
         * 这个方法会在扫描失败时调用，
         *
         * @param errorCode 请查阅ScanCallback类的API
         */
        void onScanFailed(int errorCode);

        void onScanUpdate(ScanData scanData);
    }

}
