package com.single.code.tool.bluetooth.classic.entity;

/**
 * 发送频率
 * Created by Administrator on 2017/8/31.
 */
public class SendRate {
    private long sendBytes;
    private long sendTime;

    public SendRate(long sendBytes, long sendTime){
        this.sendBytes=sendBytes;
        this.sendTime=sendTime;
    }
    public void setSendBytes(long sendBytes) {
        this.sendBytes = sendBytes;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getSendBytes() {
        return sendBytes;
    }

    public long getSendTime() {
        return sendTime;
    }
}