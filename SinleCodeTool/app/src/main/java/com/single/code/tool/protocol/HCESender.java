package com.single.code.tool.protocol;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by czf on 2018/9/7.
 */

public class HCESender {
    private boolean isSending;
    private static HCESender sender;
    private static final int DEFAULT_SIZE = 252;
    private static final int HEADER_SIZE = 8;
    private ISender iSender;
    private ICallback callback;
    private ConcurrentLinkedQueue linkedQueue;
    private static byte nfcTagId;
    private String TAG = "HCESender";
    private HCESender(){
        linkedQueue = new ConcurrentLinkedQueue();
        callback = new ICallback() {
            @Override
            public void inputData(byte[] bytes) {
                //TODO send message
            }

            @Override
            public void onWriteNotify() {
                // TODO send next package
                nextSend();
            }
        };
    }

    public static HCESender getSender(byte tagId) {
        nfcTagId = tagId;
        if(sender == null){
            synchronized (HCESender.class){
                if(sender == null){
                    sender = new HCESender();
                }
            }
        }
        return sender;
    }

    public void sendData(byte[] data, ISender iSender){
        this.iSender = iSender;
        linkedQueue.offer(Arrays.copyOf(data,data.length));
        startSend();
    }
    public boolean isSending(){
        return isSending;
    }
    private void startSend(){
        if(isSending()){
            return;
        }
        nextSend();
    }
    private void nextSend(){
        if(linkedQueue.isEmpty()){
            isSending = false;
            if(iSender!=null)
            iSender.sendingState(isSending);
            return;
        }
        send();
    }
    private void send(){
        isSending = true;
        //        byte[] data = (byte[]) msgQueue.deQueue();
        byte[] data = (byte[]) linkedQueue.poll();
        if(data!=null){
            sendMessage(data,DEFAULT_SIZE);
        }
        if(iSender!=null)
        iSender.sendingState(isSending);
        callback.onWriteNotify();
    }

    /**
     * 分包发送 前8字节是数据总长度，后244个字节是有效数据
     * @param data
     * @param size
     */
    public void sendMessage(byte[] data,int size){
        byte[] head = new byte[size];
        long datelength = data.length;
        HCEHeader hceHeader = new HCEHeader();
        hceHeader.setDataLength(datelength);
        head = getHeadBytes(hceHeader);
        long surplusLength =datelength;
        int pkgLength = size-head.length;
        byte[] buffer = new byte[size];
        int times =0;
        while (surplusLength>=pkgLength){
            System.arraycopy(head,0,buffer,0,head.length);
            System.arraycopy(data,times*pkgLength,buffer,head.length,pkgLength);
            times++;
            surplusLength = (surplusLength-pkgLength);
            callback.inputData(buffer);
        }
        if(surplusLength!=0){
            int length = times*pkgLength;
            System.arraycopy(head,0,buffer,0,head.length);
            System.arraycopy(data,length,buffer,head.length, (int) surplusLength);
            callback.inputData(buffer);
        }

    }

    private byte[] getHeadBytes(HCEHeader header){
        byte[] head = longToBytes(header.getDataLength());
        return head;
    }
    private static ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }
    public void cancle(){
        if(linkedQueue!=null&&!linkedQueue.isEmpty()){
            linkedQueue.clear();
        }
    }

    public interface ISender {
        /**
         * 发送状态
         * @param isSending
         */
        void sendingState(boolean isSending);
    }
    public interface ICallback{
        /**
         * 该方法将拆分的数据发送出去
         * @param bytes 拆分后的数据
         */
        void inputData(byte[] bytes);
        void onWriteNotify();
    }
}
