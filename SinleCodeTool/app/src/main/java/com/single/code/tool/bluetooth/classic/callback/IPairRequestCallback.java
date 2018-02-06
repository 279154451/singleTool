package com.single.code.tool.bluetooth.classic.callback;

import android.bluetooth.BluetoothDevice;

/**
 * Created by yxl on 2017/6/15.
 */

public interface IPairRequestCallback {
    void pairRequest(BluetoothDevice device);
    void bondStateChange(BluetoothDevice device);
}
