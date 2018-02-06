package com.single.code.tool.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Created by yxl on 2017/6/6.
 */

public enum  DeviceManager {
    MANAGER;
    private HooweDevice hooweDevice = new HooweDevice();

    public void setName(String deviceName){
        hooweDevice.setName(deviceName);
    }
    public void setCurrentBleAddress(String address){
        hooweDevice.setBleAddress(address);
    }
    public void setCurrentBluetoothDevice(BluetoothDevice device){
        hooweDevice.setDevice(device);
    }
    public void setCurrentUUID(UUID uuid){
        hooweDevice.setUuid(uuid);
    }

    public HooweDevice getCurrentHooweDevice() {
        return hooweDevice;
    }
}
