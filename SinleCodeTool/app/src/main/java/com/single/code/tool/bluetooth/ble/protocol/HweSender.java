package com.single.code.tool.bluetooth.ble.protocol;

import android.util.Log;

import com.single.code.tool.bluetooth.ble.protocol.BleHeader;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by yxl on 2017/6/2.
 */

public class HweSender {

    private static final int DEFAULT_SIZE = 20;
    private ISender sender;
    private String TAG ="HweSender";
//    private MsgQueue msgQueue;
    private ConcurrentLinkedQueue linkedQueue;
    private ICallback callback;
    private boolean sending;
    public HweSender(ISender sender){
        this.sender = sender;
        sending = false;
//        msgQueue = new MsgQueue<>();
        linkedQueue = new ConcurrentLinkedQueue();
        callback = new ICallback() {
            @Override
            public void onWriteNotify() {
                nextSend();
            }
        };
    }
    public void cancle(){
        if(linkedQueue!=null&&!linkedQueue.isEmpty()){
            linkedQueue.clear();
        }
    }

    public void sendMessage(byte[] data) {
        linkedQueue.offer(Arrays.copyOf(data, data.length));
//        msgQueue.enQueue(Arrays.copyOf(data,data.length));
        startSend();

    }
    private void startSend(){
        if(sending){
            return;
        }
        nextSend();
    }
    private void nextSend(){
//        if(msgQueue.isEmpty()){
//            sending = false;
//            return;
//        }
        if(linkedQueue.isEmpty()){
            sending = false;
            return;
        }
        send();
    }
    private void send(){
        sending = true;
//        byte[] data = (byte[]) msgQueue.deQueue();
        byte[] data = (byte[]) linkedQueue.poll();
        if(data!=null){
            sendMessage(data,DEFAULT_SIZE);
        }
        callback.onWriteNotify();
    }

    public void sendMessage(byte[] data,int size){
        byte[] head = new byte[size];
        long datelength = data.length;
        Log.d(TAG, "===========================datalength ================================:" + datelength);
        BleHeader bleHeader = new BleHeader();
        bleHeader.setDataLength(datelength);
        head = getHeadBytes(bleHeader);
        long surplusLength =datelength;
        int pkgLength = size-head.length;
        byte[] buffer = new byte[size];
        int times =0;
        Log.d(TAG, "pkgLength ;" + pkgLength);
        while (surplusLength>=pkgLength){
            Log.d(TAG, "surplusLength :" + surplusLength);
            System.arraycopy(head, 0, buffer, 0, head.length);
            System.arraycopy(data, times * pkgLength, buffer, head.length, pkgLength);
            times++;
            Log.d(TAG, "pkgLength ;" + pkgLength);
            surplusLength = (surplusLength-pkgLength);
            Log.d(TAG, "buffer size :" + buffer.length);
            sender.inputData(buffer);
        }
        Log.d(TAG, "==========================last surplusLength =================================:" + surplusLength + " =============this datalength :" + datelength);
        if(surplusLength!=0){
            int length = times*pkgLength;
            System.arraycopy(head, 0, buffer, 0, head.length);
            System.arraycopy(data, length, buffer, head.length, (int) surplusLength);
            sender.inputData(buffer);
        }

    }




    private byte[] getHeadBytes(BleHeader header){
        byte[] head = longToBytes(header.getDataLength());
        return head;
    }
    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public interface ISender {
        /**
         * 该方法将拆分的数据发送出去
         * @param bytes 拆分后的数据
         */
        void inputData(byte[] bytes);
    }
    public interface ICallback{
        void onWriteNotify();
    }
}
