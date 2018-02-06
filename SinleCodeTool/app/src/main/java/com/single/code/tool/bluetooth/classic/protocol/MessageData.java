package com.single.code.tool.bluetooth.classic.protocol;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/6.
 */
public class MessageData {
    private long messageId;//message唯一标识
    private long length;//message长度
    private Byte sendType;//发送类型
    private byte[] validData;
    private boolean valid;
    public MessageData(byte[] messagePackage){
        if(messagePackage.length>=HweProtocol.PACKAGE_BYTE_SIZE){
            messageId = HweProtocol.bytesToLong(Arrays.copyOfRange(messagePackage, 0, HweProtocol.FILE_ID_BYTE_SIZE));
            sendType = messagePackage[HweProtocol.FILE_ID_BYTE_SIZE];
            length = HweProtocol.bytesToLong(Arrays.copyOfRange(messagePackage, HweProtocol.FILE_ID_BYTE_SIZE + 1, HweProtocol.FILE_HEAD_BYTE_SIZE - 1));
            if(length<=HweProtocol.VALID_MESSAGE_DATE_SIZE){
                validData = Arrays.copyOfRange(messagePackage, HweProtocol.FILE_HEAD_BYTE_SIZE-1,HweProtocol.VALID_MESSAGE_DATE_SIZE);
                valid = true;
            }else {
                validData = Arrays.copyOfRange(messagePackage, HweProtocol.FILE_HEAD_BYTE_SIZE-1,messagePackage.length);
                valid = false;
            }
        }
    }
    public void setLength(long length) {
        this.length = length;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setSendType(Byte sendType) {
        this.sendType = sendType;
    }

    public Byte getSendType() {
        return sendType;
    }

    public long getLength() {
        return length;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setValidData(byte[] validData) {
        this.validData = validData;
    }

    public byte[] getValidData() {
        return validData;
    }
}