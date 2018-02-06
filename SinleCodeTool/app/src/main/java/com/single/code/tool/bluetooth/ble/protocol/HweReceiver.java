package com.single.code.tool.bluetooth.ble.protocol;

import android.util.Log;

import com.single.code.tool.bluetooth.ble.protocol.BleHeader;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by yxl on 2017/6/5.
 */

public class HweReceiver {
    private static final int DEFAULT_SIZE = 20;
    private static byte[] totalBytes =new byte[0];
    private IReceiver receiver;
    private static String TAG = "HweReceiver";
    private static ByteBuffer buffer = ByteBuffer.allocate(8);
    private static long dataLength;
    private static byte[] datas;
    private ConcurrentLinkedQueue linkedQueue;
    public HweReceiver(IReceiver receiver) {
        init();
        this.receiver = receiver;
    }

    public  void init() {
        Log.d(TAG, "init ");
        totalBytes = new byte[0];
        dataLength = 0;
        datas = new byte[0];
    }


    public void OutputData(byte[] data){
        int datalength = data.length;
        Log.d(TAG, "recevicer data length :" + datalength);
        int surplusLength = totalBytes.length;
        int supplementLength = DEFAULT_SIZE-surplusLength;
        Log.d(TAG, "surplusLength :" + surplusLength);
        if(surplusLength<=DEFAULT_SIZE){
            if(datalength>=supplementLength){//接收的数据长度>=拼下个包所需的字节数
                byte[] packageBytes;
                packageBytes = merge(data,0,totalBytes,supplementLength,DEFAULT_SIZE);//拼包
                totalBytes = new byte[0];
                Log.d(TAG, "packageBytes length :" + packageBytes.length);
                BleHeader header = parseHeader(packageBytes);
                byte[] dataBytes = parseMsgData(packageBytes);//正文数据
                if(header.getDataLength()!=dataLength){//新数据
                    init();
                    dataLength = header.getDataLength();
                    Log.d(TAG, "======================dataLength ====================;" + dataLength);
                    int length = (int) (dataLength-datas.length);
                    Log.d(TAG, "dataLength-datas.length =" + length);
                    if(length<=dataBytes.length){
                        datas = merge(dataBytes,0,datas,length,length+datas.length);
                        Log.d(TAG, "======================readDatas length ====================;" + datas.length);
                        receiver.receiveData(datas);
                        init();
                    }else {
                        datas = merge(dataBytes,0,datas,dataBytes.length,datas.length+dataBytes.length);
                        int suplength  = datalength-supplementLength;
                        totalBytes = merge(data,supplementLength,totalBytes,suplength,suplength);
                    }
//                    OutputData(data);
                }else {//属于上一个数据
                    int length = (int) (dataLength-datas.length);
                    Log.d(TAG, "dataLength-datas.length =" + length);
                    if(length<=dataBytes.length){
                        datas = merge(dataBytes,0,datas,length,length+datas.length);
                        Log.d(TAG, "======================readDatas length ====================;" + datas.length);
                        receiver.receiveData(datas);
                        init();
                    }else {
                        datas = merge(dataBytes,0,datas,dataBytes.length,datas.length+dataBytes.length);
                        int suplength  = datalength-supplementLength;
                        totalBytes = merge(data,supplementLength,totalBytes,suplength,suplength);
                    }
                }
            }else {
                int allLength = datalength+surplusLength;
                totalBytes = merge(data,0,totalBytes,datalength,allLength);
            }
        }

    }

    public  byte[] merge(byte[] src, int srcPos, byte[] dst, int lengtht,int lastCopyLength){
        int length2=dst.length;
        byte[] bytes = new byte[lastCopyLength];
        System.arraycopy(dst, 0, bytes, 0, length2);
        System.arraycopy(src, srcPos, bytes, length2, lengtht);
        return bytes;
    }
    private BleHeader parseHeader(byte[] packagebyte){
        BleHeader header = new BleHeader();
        header.setDataLength(bytesToLong(Arrays.copyOfRange(packagebyte, 0, 8)));
        return header;
    }
    private byte[] parseMsgData(byte[] packageByte){
        int datalength = 12;
        byte[] bytes = new byte[datalength];
        System.arraycopy(packageByte, 8, bytes, 0, datalength);
        return bytes;
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.rewind();
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
    public interface IReceiver {
        /**
         * 接收整合后的数据
         * @param data 整合后的数据
         */
        void receiveData(byte[] data);
    }
}
