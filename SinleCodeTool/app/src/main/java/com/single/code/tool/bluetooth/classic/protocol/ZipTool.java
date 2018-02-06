package com.single.code.tool.bluetooth.classic.protocol;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2017/12/7.
 */
public class ZipTool {
    private static ZipTool zipTool;
    private String TAG = "ZipTool";
    public static ZipTool getZipTool(){
        if(zipTool == null){
            synchronized (ZipTool.class){
                if(zipTool == null){
                    zipTool = new ZipTool();
                }
            }
        }
        return zipTool;
    }

    public interface ZipCallback{
        void zipBytes(byte[] zipBytes);
        void unZipBytes(byte[] unZipBytes);
    }

    /**
     * @param input 需要压缩的字符串
     * @return 压缩后的字符串
     * @throws IOException IO
     */
    public  String compress(String input) throws IOException {
        if (input == null || input.length() == 0) {
            return input;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOs = new GZIPOutputStream(out);
        gzipOs.write(input.getBytes());
        gzipOs.close();
        return out.toString("ISO-8859-1");
    }
    /**
     * @param zippedStr 压缩后的字符串
     * @return 解压缩后的
     * @throws IOException IO
     */
    public  String uncompress(String zippedStr) throws IOException {
        if (zippedStr == null || zippedStr.length() == 0) {
            return zippedStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(zippedStr
                .getBytes("ISO-8859-1"));
        GZIPInputStream gzipIs = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gzipIs.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
        return out.toString();
    }

    /***
     * 压缩GZip
     *
     * @param data
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public  void gZip(final byte[] data, final ZipCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] b = null;
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DeflaterOutputStream gzip = new DeflaterOutputStream(bos,new Deflater(Deflater.BEST_COMPRESSION,true),data.length,false);
                    gzip.write(data);
                    gzip.finish();
                    gzip.close();
                    b = bos.toByteArray();
                    bos.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d(TAG,"gZip :"+ex.toString());
                }finally {
                    if(callback!=null){
                        callback.zipBytes(b);
                    }
                }
            }
        }).start();
    }
    /***
     * 解压GZip
     *
     * @param data
     * @return
     */
    public  void  unGZip(final byte[] data, final ZipCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] b = null;
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(data);

                    InflaterInputStream gzip = new InflaterInputStream(bis,new Inflater(true),data.length);
                    byte[] buf = new byte[1024];
                    int num = -1;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                        baos.write(buf, 0, num);
                    }
                    b = baos.toByteArray();
                    baos.flush();
                    baos.close();
                    gzip.close();
                    bis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d(TAG, "unGZip :" + ex.toString());
                }finally {
                    if(callback!=null){
                        callback.unZipBytes(b);
                    }
                }
            }
        }).start();
    }

    /***
     * 压缩Zip
     *
     * @param data
     * @return
     */
    public  byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
    /***
     * 解压Zip
     *
     * @param data
     * @return
     */
    public  byte[] unZip(byte[] data) {
        byte[] b = null;
        try {

            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
}