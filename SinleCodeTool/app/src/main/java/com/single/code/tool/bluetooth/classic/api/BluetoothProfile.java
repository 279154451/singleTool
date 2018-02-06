package com.single.code.tool.bluetooth.classic.api;

import java.util.UUID;

/**
 * Created by yxl on 2017/3/29.
 */

public class BluetoothProfile {
    public static String TAG = "BluetoothProfile";
    /*UUID*/
    public static UUID BLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    /*Message Type*/
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_SNED_RATE = 5;
    public static final int MESSAGE_SEND_START = 6;
    public static final int MESSAGE_SEND_END = 7;
    public static final int MESSAGE_SEND_TIME = 8;
    public static int Type_Server = 0;
    public static int Type_Client = 1;
    public static int Type_readOver = 2;
}
