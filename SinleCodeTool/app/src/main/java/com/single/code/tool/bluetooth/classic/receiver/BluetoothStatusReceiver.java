package com.single.code.tool.bluetooth.classic.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.single.code.tool.bluetooth.classic.callback.IBluetoothStatusCallback;


/**
 * Created by yxl on 2017/5/5.
 */

public class BluetoothStatusReceiver extends BroadcastReceiver {
    private IBluetoothStatusCallback statusCallback;
    public BluetoothStatusReceiver(IBluetoothStatusCallback callback){
        this.statusCallback = callback;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state){
                case BluetoothAdapter.STATE_ON:
                    if(statusCallback!=null){
                        statusCallback.StateOn();
                    }
                    break;
                case BluetoothAdapter.STATE_OFF:
                    if(statusCallback!=null){
                        statusCallback.StateOff();
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    if(statusCallback!=null){
                        statusCallback.turningOff();
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    if(statusCallback!=null){
                        statusCallback.turningOn();
                    }
                    break;

            }
        }else if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR); //当前的连接状态
            Log.d("ProjectApplication", "state :" + state);
            switch (state){
                case BluetoothAdapter.STATE_DISCONNECTED:
                    if(statusCallback!=null){
                        statusCallback.disConnected();
                    }
                    break;
            }
        }
    }
}
