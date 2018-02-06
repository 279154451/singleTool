package com.single.code.tool.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Created by yxl on 2017/5/24.
 */

public class HooweDevice {
    private BluetoothDevice device;
    private UUID uuid;
    private String bleAddress;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public UUID getUuid() {
        return uuid;
    }
}
