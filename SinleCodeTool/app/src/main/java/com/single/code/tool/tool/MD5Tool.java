package com.single.code.tool.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/6.
 */
public class MD5Tool {
    public static final int MD5_BYTE_SIZE = 16;
    public static byte[] getMD5(byte[] buffer) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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
}