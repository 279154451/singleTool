package com.single.code.tool.bluetooth.classic.protocol;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/6.
 */
public class PackageData {
    private byte[] fileId = new byte[HweProtocol.FILE_HEAD_BYTE_SIZE];
    private byte[] fileHeads = new byte[HweProtocol.FILE_HEAD_BYTE_SIZE];
    private byte[] fileData = new byte[HweProtocol.VALID_DATA_BYTE_SIZE];
    private byte[] md5 = new byte[HweProtocol.MD5_BYTE_SIZE];
    private boolean checkMd5;
    private FileHeader fileHeader;
    public PackageData(byte[] packageData ,boolean withFileHead){
        if(packageData.length>=HweProtocol.PACKAGE_BYTE_SIZE){
            if(withFileHead){
                fileId = Arrays.copyOfRange(packageData,0,HweProtocol.FILE_ID_BYTE_SIZE);
                fileHeads = Arrays.copyOfRange(packageData,0,HweProtocol.FILE_HEAD_BYTE_SIZE);
                fileData = Arrays.copyOfRange(packageData,HweProtocol.FILE_HEAD_BYTE_SIZE,packageData.length-HweProtocol.MD5_BYTE_SIZE);
                md5 = Arrays.copyOfRange(packageData,HweProtocol.MD5_POINT,packageData.length);
                fileHeader = new FileHeader(fileHeads);
            }else {
                fileId = Arrays.copyOfRange(packageData,0,HweProtocol.FILE_ID_BYTE_SIZE);
                fileData = Arrays.copyOfRange(packageData,HweProtocol.FILE_ID_BYTE_SIZE,packageData.length-HweProtocol.MD5_BYTE_SIZE);
                md5 = Arrays.copyOfRange(packageData,HweProtocol.MD5_POINT,packageData.length);
            }
            byte[] newMd5 = HweProtocol.getMD5(Arrays.copyOfRange(packageData,0,HweProtocol.MD5_POINT));
            checkMd5 = Arrays.equals(md5,newMd5);//校验md5值
        }
    }
    public PackageData(){

    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public void setFileHeads(byte[] fileHeads) {
        this.fileHeads = fileHeads;
    }

    public void setFileId(byte[] fileId) {
        this.fileId = fileId;
    }

    public void setMd5(byte[] md5) {
        this.md5 = md5;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public byte[] getFileHeads() {
        return fileHeads;
    }

    public byte[] getFileId() {
        return fileId;
    }

    public byte[] getMd5() {
        return md5;
    }

    public void setFileHeader(FileHeader fileHeader) {
        this.fileHeader = fileHeader;
    }

    public FileHeader getFileHeader() {
        return fileHeader;
    }

    public void setCheckMd5(boolean checkMd5) {
        this.checkMd5 = checkMd5;
    }

    public boolean isCheckMd5() {
        return checkMd5;
    }

}