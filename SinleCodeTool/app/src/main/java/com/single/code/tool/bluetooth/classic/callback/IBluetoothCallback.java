package com.single.code.tool.bluetooth.classic.callback;


import com.single.code.tool.bluetooth.classic.entity.SendRate;
import com.single.code.tool.bluetooth.classic.entity.State;

/**
 * @Description: 消息回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/18 21:24.
 */
public interface IBluetoothCallback<T> {
    void connectStateChange(State state, int type);
    void writeData(T data, int type);
    void readData(T data, int type);
    void setDeviceName(String name);
    void sendRate(SendRate sendRate);
    void startSend(long fileid, long startTime);
    void endSend(long fileid, long endTime);
}
