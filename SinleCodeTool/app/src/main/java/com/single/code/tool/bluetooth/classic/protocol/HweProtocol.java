package com.single.code.tool.bluetooth.classic.protocol;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/6.
 */
public class HweProtocol {
    public static int FILE_ID_BYTE_SIZE =8;//文件唯一标识大小
    public static int SEND_TYPE = 1;//发送类型
    public static int FILE_HEAD_BYTE_SIZE = 18;//文件头大小
    public static int MD5_BYTE_SIZE = 16;//md5校验码大小
    public static int PACKAGE_BYTE_SIZE = 2048;//数据包大小
    public static int VALID_DATA_BYTE_SIZE = PACKAGE_BYTE_SIZE-FILE_ID_BYTE_SIZE-SEND_TYPE-MD5_BYTE_SIZE;//有效数据大小
    public static final int MD5_POINT = PACKAGE_BYTE_SIZE-MD5_BYTE_SIZE;//2048-16
    public static int VALID_MESSAGE_DATE_SIZE = PACKAGE_BYTE_SIZE-(FILE_ID_BYTE_SIZE*2)-SEND_TYPE;//message有效数据

    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.rewind();
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static byte[] merge(byte[] src, int srcPos, byte[] dst, int lengtht,int lastCopyLength){
        int length2=dst.length;
        byte[] bytes = new byte[lastCopyLength];
        System.arraycopy(dst, 0, bytes, 0, length2);
        System.arraycopy(src, srcPos, bytes, length2, lengtht);
        return bytes;
    }

    public static byte[] getMD5(byte[] buffer) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5Tool");
            md.update(buffer);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] nullMd5 = new byte[MD5_BYTE_SIZE];
        Arrays.fill(nullMd5, (byte) 0);//初始化
        return nullMd5;
    }

    public static byte[] fileHead2Bytes(FileHeader fileHeader){
        byte[] h = new byte[HweProtocol.FILE_HEAD_BYTE_SIZE];
        byte[] fileId = longToBytes(fileHeader.getFileId());
        System.arraycopy(fileId,0,h,0,8);
        h[8] = fileHeader.getSendType();
        byte[] len = longToBytes(fileHeader.getLength());
        System.arraycopy(len,0,h,9,8);
        h[17] = fileHeader.getMimeType();
        return h;
    }
}