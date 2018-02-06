package com.single.code.tool.bluetooth.ble.utils;

/**
 * Created by Administrator on 2018/2/1.
 */
public class BytesUtil {

    public static byte[] getByteByNumber(String number) {
        byte[] nums = number.getBytes();
        int len = nums.length;
        int outlen = (len % 2 == 0)?len/2:len/2+1;
        byte[] out = new byte[outlen];
        for(int i= 0,j=0;i<len;i+=2,j++) {
            byte h = nums[i];
            byte l ;
            if(i+1>=len) {
                l = 0x0f;
            }else {
                l = nums[i+1];
            }
            byte o = (byte) ((h<<4 & 0xf0)|(l & 0x0f));
            out[j]=o;
        }
        return out;
    }

    public static String getNumberByBytes(byte[] nums) {
        int len = nums.length;
        StringBuilder builder = new StringBuilder();
        for(byte n : nums) {
            builder.append((n>>4 & 0x0f));
            if((n & 0x0f)!= 0x0f) {
                builder.append((n & 0x0f));
            }
        }
        return builder.toString();
    }
}