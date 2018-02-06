package com.single.code.tool.util;

import android.media.AmrInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件io操作
 * Created by Administrator on 2017/11/6.
 */
public class FileUtil {

    /**
     *复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath,String newPath){
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                FileInputStream inStream = new FileInputStream(new File(oldPath)); //读入原文件
                File file = new File(newPath);
                if(file.exists()){
                    file.delete();
                }
                FileOutputStream outputStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, byteread);
                }
                inStream.close();
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public static void create(File file) throws IOException {
        if(!file.exists()) {
            if(file.isDirectory()) {
                file.mkdirs();
            } else {
                File parent = file.getParentFile();
                if(!file.exists()) {
                    parent.mkdirs();
                }

                file.createNewFile();
            }
        }

    }
    /**
     * 删除文件夹下所有文件
     * @param file
     */
    public static void deleteAllFile(File file){
        if(file!=null&&file.exists()){
            if(file.isFile()){
                file.delete();
            }else if(file.isDirectory()){
                File[] childFiles = file.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    file.delete();
                    return;
                }
                for (int i = 0; i < childFiles.length; i++) {
                    deleteAllFile(childFiles[i]);
                }
                file.delete();
            }
        }
    }

    /**
     * 把一个文件转化为字节
     *
     * @param path
     * @return byte[]
     * @throws Exception
     */
    public static byte[] getByte(String path) {
        byte[] bytes = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                int e = (int) file.length();
                bytes = new byte[e];
                in.read(bytes, 0, e);
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将pcm文件转换成amr文件
     *
     * @param pcmPath
     * @param amrPath
     */
    public static void pcm2Amr(String pcmPath, String amrPath) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(pcmPath);
            pcm2Amr(fis, amrPath);
            fis.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pcm2Amr(InputStream pcmStream, String amrPath) {
        try {

            AmrInputStream ais = new AmrInputStream(pcmStream);
            OutputStream out = new FileOutputStream(amrPath);
            byte[] buf = new byte[4096];
            int len = -1;
            /*
             * 下面的AMR的文件头,缺少这几个字节是不行的
             */
            out.write(0x23);
            out.write(0x21);
            out.write(0x41);
            out.write(0x4D);
            out.write(0x52);
            out.write(0x0A);
            while ((len = ais.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            ais.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}