package com.single.code.tool.bluetooth.ble.api;

import android.content.Context;

/**
 * Created by yxl on 2017/6/9.
 */

public enum  BLEScanHelper {
    HELPER;
    private BLEScanner bleScanner;

    public void startScan(Context context,BLEScanner.IScanResultListener listener){
        bleScanner = BLEScanner.getInstance(context.getApplicationContext(),listener);
        bleScanner.startScan();
    }

    public void stopScan(){
        if(bleScanner!=null){
            bleScanner.stopScan();
            bleScanner = null;
        }
    }
}
