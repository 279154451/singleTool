package com.single.code.tool.protocol;

/**
 * Created by czf on 2018/9/7.
 */

public class HCEHeader {
    private long dataLength;

    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }

    public long getDataLength() {
        return dataLength;
    }
}
