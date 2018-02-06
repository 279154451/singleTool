package com.single.code.tool.bluetooth.ble.protocol;

/**
 * Created by yxl on 2017/6/2.
 */

public class BleHeader {
    private long dataLength;

    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }

    public long getDataLength() {
        return dataLength;
    }
}
