package com.single.code.tool.bluetooth.classic.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.single.code.tool.bluetooth.classic.callback.IPairRequestCallback;


/**
 * 蓝牙配对请求
 * Created by yxl on 2017/6/15.
 */

public class PairRequestReceiver extends BroadcastReceiver {
    private IPairRequestCallback pairRequestCallback;
    public PairRequestReceiver(IPairRequestCallback callback){
        this.pairRequestCallback = callback;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(pairRequestCallback!=null){
                pairRequestCallback.pairRequest(device);
            }
        }else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(pairRequestCallback!=null){
                pairRequestCallback.bondStateChange(device);
            }
        }
    }
}
