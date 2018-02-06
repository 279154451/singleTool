package com.single.code.tool.bluetooth.ble.entity;

/**
 * Created by yxl on 2017/4/20.
 */

public class ScanData {
    public String deviceName;
    public String bleAddress;
    public String classicAddress;
    public String phoneNumber;
    public boolean lcdevice;//last connect device

    public void setLcdevice(boolean lcdevice) {
        this.lcdevice = lcdevice;
    }

    public boolean isLcdevice() {
        return lcdevice;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public void setClassicAddress(String classicAddress) {
        this.classicAddress = classicAddress;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public String getClassicAddress() {
        return classicAddress;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getDeviceName() {
        return deviceName;
    }

}
