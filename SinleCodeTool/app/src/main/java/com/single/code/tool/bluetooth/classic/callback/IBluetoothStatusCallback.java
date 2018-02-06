package com.single.code.tool.bluetooth.classic.callback;

/**
 * Created by yxl on 2017/5/5.
 */

public interface IBluetoothStatusCallback {
    void StateOn();
    void StateOff();
    void turningOn();
    void turningOff();
    void disConnected();
}
