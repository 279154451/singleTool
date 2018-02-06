package com.single.code.tool.bluetooth.ble.api;

/**
 * Created by andy on 2016/1/13.
 * BLE需要的UUID
 */
public final class BLEProfile {
    static final String UUID_SERVICE="9a1544ca-9b93-4e20-8adc-5c96bae71c1e";
    static final String UUID_CHARACTERISTIC_NOTIFY="bdfc68c7-2a73-4b4b-856d-5c67616ce72a";
    static final String UUID_CHARACTERISTIC_WRITE="50aef763-a606-4caa-b2d9-1a99c99cc54b";

    /*Message Type*/
    public static final int MESSAGE_DO_NOTHING = 0;
    public static final int MESSAGE_CONNECT = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DISCONNECT = 3;

    public static int Type_Server = 0;
    public static int Type_Client = 1;
}
