package com.single.code.tool.bluetooth.classic.entity;

import android.os.Bundle;

import com.single.code.tool.bluetooth.classic.protocol.HweSender;

import java.io.OutputStream;

/**
 * 发送的数据
 * Created by Administrator on 2017/11/7.
 */
public class SendData {
    private long fileId;//文件、message 唯一标识
    private byte sendType;//发送类型：现场上报、媒体共享、message、response
    private byte mimeType;//文件类型
    private String filepath;//文件路劲、message正文
    private long fileLength;//文件长度
    private long startTime;//开始发送时间
    private Bundle bundle;
    private OutputStream outputStream;
    private HweSender.SendCallBack sendCallBack;
    public SendData(Bundle bundle){
        this.bundle = bundle;
    }
    public SendData(long fileId,byte sendType,byte mimeType,String filepath){
        this.fileId = fileId;
        this.sendType = sendType;
        this.mimeType = mimeType;
        this.filepath = filepath;
    }

    public void setSendCallBack(HweSender.SendCallBack sendCallBack) {
        this.sendCallBack = sendCallBack;
    }

    public HweSender.SendCallBack getSendCallBack() {
        return sendCallBack;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setSendType(byte sendType) {
        this.sendType = sendType;
    }

    public void setMimeType(byte mimeType) {
        this.mimeType = mimeType;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public long getFileId() {
        return fileId;
    }

    public byte getMimeType() {
        return mimeType;
    }

    public byte getSendType() {
        return sendType;
    }

    public String getFilepath() {
        return filepath;
    }

    @Override
    public String toString() {
        return "SendData{" +
                "fileId=" + fileId +
                ", sendType=" + sendType +
                ", mimeType=" + mimeType +
                ", filepath='" + filepath + '\'' +
                ", fileLength=" + fileLength +
                ", startTime=" + startTime +
                '}';
    }
}