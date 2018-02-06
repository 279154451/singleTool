package com.single.code.tool.bluetooth.classic.protocol;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/6.
 */
public class FileHeader {
    private long fileId;//文件、message和response唯一标识
    private Byte mimeType;//文件类型(如果时response则为responseCode)
    private long length;//文件长度、response对应的fileId
    private Byte sendType;//发送类型

    public FileHeader(long fileId,byte sendType,long length,byte mimeType){
        this.fileId = fileId;
        this.sendType = sendType;
        this.length = length;
        this.mimeType = mimeType;
    }
    public FileHeader(byte[] fileHeaderBytes){
        if(fileHeaderBytes.length>=HweProtocol.FILE_HEAD_BYTE_SIZE){
            fileId = HweProtocol.bytesToLong(Arrays.copyOfRange(fileHeaderBytes,0,HweProtocol.FILE_ID_BYTE_SIZE));
            sendType = fileHeaderBytes[HweProtocol.FILE_ID_BYTE_SIZE];
            length = HweProtocol.bytesToLong(Arrays.copyOfRange(fileHeaderBytes,HweProtocol.FILE_ID_BYTE_SIZE+1,HweProtocol.FILE_HEAD_BYTE_SIZE-1));
            mimeType = fileHeaderBytes[HweProtocol.FILE_HEAD_BYTE_SIZE-1];
        }
    }
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setMimeType(Byte mimeType) {
        this.mimeType = mimeType;
    }

    public void setSendType(Byte sendType) {
        this.sendType = sendType;
    }

    public Byte getMimeType() {
        return mimeType;
    }

    public Byte getSendType() {
        return sendType;
    }

    public long getFileId() {
        return fileId;
    }

    public long getLength() {
        return length;
    }
}