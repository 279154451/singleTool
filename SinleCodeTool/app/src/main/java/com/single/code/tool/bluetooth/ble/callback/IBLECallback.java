package com.single.code.tool.bluetooth.ble.callback;

/**
 * Created by andy on 2016/1/14.
 *
 */
public interface IBLECallback {
    /**
     * 连接成功
     * @param type 0:server  1:client
     */
    void onConnected(int type);

    /**
     * 连接断开
     */
    void onDisconnected();

    /**
     *    * 此方法会在收到数据时调用
     * @param data 收到的数据
     * @param type 0:server 1:client
     */
    void onDataReceived(byte[] data, int type);
}
