package com.single.code.tool.bluetooth.classic.protocol;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.single.code.tool.bluetooth.classic.entity.SendData;
import com.single.code.tool.bluetooth.classic.entity.SendRate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Administrator on 2017/11/6.
 */
public class HweSender {
    private String TAG = "HweSender";
    private static HweSender SENDER;
    private Set<SendCallBack> sendCallBackSet = new CopyOnWriteArraySet<>();
    private  volatile static boolean stopSend;
    private long readIndex;
    private int sendCount;
    private final int maxCount = 40;//每40个包统计一次传输速率
    private long startTime;
    private Handler fileHandler;
    private Handler msgHandler;
    public static class SendCmd {
        public static final int SEND_FILE = 0;
        public static final int SEND_MSG = 1;
        public static final int SEND_RSP = 2;
        public static final int STOP_SEND = 3;
    }
    private HweParse.ParseCallBack parseCallBack = new HweParse.ParseCallBack() {
        @Override
        public void parseResponse(long fileId, byte responseCode) {
            if(responseCode!=PackageUtil.ResponseCode.SUCCESS){
                //TODO
            }
        }

        @Override
        public void parseMessage(long messageId, byte[] message) {

        }

        @Override
        public void parseFileFinish(long fileId, byte responseCode) {

        }
    };
    public HweParse.ParseCallBack getParseCallBack(){
        return parseCallBack;
    }

    private Thread sendFileThread  = new Thread(){
        @Override
        public void run() {
            Looper.prepare();
            if(fileHandler == null){
                fileHandler = new Handler(Looper.myLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        int sendCmd = msg.what;
                        switch (sendCmd){
                            case SendCmd.SEND_FILE:
                                SendData FileData = (SendData) msg.obj;
                                if(FileData!=null){
                                    sendFileData(FileData.getOutputStream(),FileData);
                                }
                                break;
                            case SendCmd.SEND_MSG:
//                                SendData msgData = (SendData) msg.obj;
//                                sendMessageData(msgData.getOutputStream(),msgData);
                                break;
                            case SendCmd.SEND_RSP:
//                                SendData rspData = (SendData) msg.obj;
//                                if(rspData!=null){
//                                    sendResponseData(rspData);
//                                }
                                break;
                            case SendCmd.STOP_SEND:
                                break;
                        }
                    }
                };
            }
            Looper.loop();
            return;
        }
    };

    private Thread msgThread = new Thread(){
        @Override
        public void run() {
            Looper.prepare();
            if(msgHandler == null){
                msgHandler = new Handler(Looper.myLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        int sendCmd = msg.what;
                        switch (sendCmd){
                            case SendCmd.SEND_FILE:
                                break;
                            case SendCmd.SEND_MSG:
                                SendData msgData = (SendData) msg.obj;
                                sendMessageData(msgData.getOutputStream(),msgData);
                                break;
                            case SendCmd.SEND_RSP:
                                SendData rspData = (SendData) msg.obj;
                                if(rspData!=null){
                                    sendResponseData(rspData);
                                }
                                break;
                            case SendCmd.STOP_SEND:
                                break;
                        }
                    }
                };
            }
            Looper.loop();
            return;
        }
    };
    public HweSender(){
        if(sendFileThread!=null&& !sendFileThread.isAlive()){
            sendFileThread.start();
        }
        if(msgThread!=null && !msgThread.isAlive()){
            msgThread.start();
        }
    }
    public static HweSender getSender(){
        if(SENDER ==null){
            synchronized (HweSender.class){
                if(SENDER ==null){
                    SENDER = new HweSender();
                }
            }
        }
        return SENDER;
    }

    public void releaseData(){
        //TODO release data
    }

    /**
     *
     * @param md5Ok
     * @param fileId
     * @param sendType PackageUtil.SendType.Response or PackageUtil.SendType.ParseFinish
     */
    public void sendResponse(OutputStream outputStream,boolean md5Ok,long fileId,byte sendType,SendCallBack sendCallBack){
        if(msgHandler!=null){
            Message message = msgHandler.obtainMessage();
            message.what = SendCmd.SEND_RSP;
            Bundle bundle = new Bundle();
            bundle.putBoolean("md5Ok", md5Ok);
            bundle.putLong("fileId", fileId);
            bundle.putByte("sendType", sendType);
            SendData sendData = new SendData(bundle);
            sendData.setSendCallBack(sendCallBack);
            sendData.setOutputStream(outputStream);
            message.obj = sendData;
            msgHandler.sendMessage(message);
        }
    }

    public void sendFile(SendData sendData){
        if(fileHandler!=null){
            Message message = fileHandler.obtainMessage();
            message.what = SendCmd.SEND_FILE;
            message.obj = sendData;
            fileHandler.sendMessage(message);
        }
    }

    /**
     * 发送message
     * @param sendData
     */
    public void sendMessage(SendData sendData){
        if(msgHandler!=null){
            Message message = msgHandler.obtainMessage();
            message.what = SendCmd.SEND_MSG;
            message.obj = sendData;
            msgHandler.sendMessage(message);
        }
    }
    /**
     * 发送response
     * @param
     */
    public void sendResponseData(SendData rspData){
        Log.d(TAG,"sendResponse");
        Bundle bundle = rspData.getBundle();
        OutputStream outputStream = null;
        boolean md5Ok = false;
        long fileId = 0;
        byte sendType = 0;
        if(bundle !=null){
            md5Ok = bundle.getBoolean("md5Ok");
            fileId  = bundle.getLong("fileId");
            sendType = bundle.getByte("sendType");
            outputStream = rspData.getOutputStream();
        }
        try {
            if(outputStream!=null){
                byte[] responseData = getResponsePackage(md5Ok,fileId,sendType);
                outputStream.flush();
                outputStream.write(responseData);
            }
        }catch (IOException e){
            releaseData();
        }finally {
            if(rspData.getSendCallBack()!=null){
                rspData.getSendCallBack().sendFinish(rspData);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeAllHandlerMsg(){
        if(msgHandler!=null){
            msgHandler.getLooper().quitSafely();
        }
        if(fileHandler!=null){
            fileHandler.getLooper().quitSafely();
        }
        if(sendFileThread!=null){
            sendFileThread.stop();
            sendFileThread = null;
        }
        if(msgThread!=null){
            msgThread.stop();;
            msgThread = null;
        }
        SENDER = null;
    }

    /**
     * 发送message
     * @param data
     */
    public void sendMessageData(final OutputStream outputStream, final SendData data){
        Log.d(TAG, "sendMessage " + data.toString());
        try {
            if(outputStream!=null){
                outputStream.flush();
                byte[] message = data.getFilepath().getBytes("UTF-8");
                long messageLen = message.length;
                long messageId = data.getFileId();
                int count = (int) (messageLen/HweProtocol.VALID_MESSAGE_DATE_SIZE);
                long sulSize =(messageLen -(count*HweProtocol.VALID_MESSAGE_DATE_SIZE));
                int readLine = 0;
                while (count>0){
                    count--;
                    byte[] validBytes = Arrays.copyOfRange(message,readLine*HweProtocol.VALID_MESSAGE_DATE_SIZE,(readLine+1)*HweProtocol.VALID_MESSAGE_DATE_SIZE);
                    byte[] messagePackage = getMessagePackage(messageId,validBytes,messageLen);
                    if(outputStream!=null){
                        outputStream.write(messagePackage);
                    }else {
                        break;
                    }
                    readLine++;
                }
                if(sulSize!=0l){
                    byte[] validBytes = Arrays.copyOfRange(message,readLine*HweProtocol.VALID_MESSAGE_DATE_SIZE,message.length);
                    byte[] messagePackage = getMessagePackage(messageId, validBytes, messageLen);
                    if(outputStream!=null){
                        outputStream.write(messagePackage);
                    }
                }
            }
        }catch (UnsupportedEncodingException e){

        } catch (IOException e) {
            releaseData();
        }finally {
            if(data.getSendCallBack()!=null){
                data.getSendCallBack().sendFinish(data);
            }
        }
    }

    private byte[] getMessagePackage(long msgId,byte[] validBytes,long messageLen){
        byte[] messagePackage = new byte[HweProtocol.PACKAGE_BYTE_SIZE];
        Arrays.fill(messagePackage, (byte) 0);//初始化
        byte[] messageId = HweProtocol.longToBytes(msgId);
        System.arraycopy(messageId,0,messagePackage,0,messageId.length);
        messagePackage[messageId.length] = PackageUtil.SendType.Message;
        byte[] messageLength = HweProtocol.longToBytes(messageLen);
        System.arraycopy(messageLength, 0, messagePackage, messageId.length + 1, messageLength.length);
        System.arraycopy(validBytes, 0, messagePackage, HweProtocol.FILE_ID_BYTE_SIZE * 2 + HweProtocol.SEND_TYPE, validBytes.length);
        return messagePackage;
    }

    private   byte[] getResponsePackage(boolean md5Ok,long fileId,byte sendType){
        byte[] packageData = new byte[HweProtocol.PACKAGE_BYTE_SIZE];
        Arrays.fill(packageData, (byte) 0);//初始化
        long responseId = System.currentTimeMillis();
        FileHeader fileHeader;
        if(md5Ok){
            Log.d(TAG,"md5 success");
            fileHeader = new FileHeader(responseId,sendType,fileId,PackageUtil.ResponseCode.SUCCESS);
        }else {
            Log.d(TAG,"md5 failed");
            fileHeader = new FileHeader(responseId,sendType,fileId,PackageUtil.ResponseCode.ERROR);
        }
        byte[] fileHeadBytes = HweProtocol.fileHead2Bytes(fileHeader);
        byte[] addBytes = new byte[HweProtocol.MD5_POINT];
        Arrays.fill(addBytes,(byte) 0);//初始化
        System.arraycopy(fileHeadBytes,0,addBytes,0,fileHeadBytes.length);
        byte[] md5bytes = HweProtocol.getMD5(addBytes);
        System.arraycopy(addBytes,0,packageData,0,addBytes.length);
        System.arraycopy(md5bytes,0,packageData,addBytes.length,md5bytes.length);
        return packageData;
    }

    /**
     * 发送文件数据
     * @param data
     */
    private void sendFileData(final OutputStream outputStream,final SendData data){
        Log.d(TAG,"sendFile :"+data.toString());
        readIndex =0;
        stopSend = false;
        try {
            File sendFile = new File(data.getFilepath());
            if(!sendFile.exists()){
                if(data.getSendCallBack()!=null){
                    data.getSendCallBack().sendFinish(data);
                }
            }
            data.setFileLength(sendFile.length());
            if(data.getSendCallBack()!=null){
                startTime = System.currentTimeMillis();
                data.setStartTime(startTime);
                data.getSendCallBack().sendStart(data);
            }
            if(outputStream!=null){
                FileInputStream fileInputStream = new FileInputStream(sendFile);
                byte[] firstPackage = getPackageData(data,null,0);
                outputStream.flush();
                outputStream.write(firstPackage);
                int readLen =-1;
                byte[] readBuffer = new byte[HweProtocol.VALID_DATA_BYTE_SIZE];
                while ((readLen = fileInputStream.read(readBuffer))>0){
                    synchronized (HweSender.class){
                        if(!stopSend){
                            byte[] packageData = getPackageData(data,readBuffer,readLen);
                            outputStream.write(packageData);
                            readIndex+= readLen;
                            getPercent(data,readIndex);
                            getSendRate(data);
                        }else {
                            break;
                        }
                    }
                }
            }else {
                Log.d(TAG,"outputStream is null");
            }
        } catch (FileNotFoundException e){

        } catch (IOException e){
            releaseData();
        }finally {
            if(data.getSendCallBack()!=null){
                data.getSendCallBack().sendFinish(data);
            }
        }
    }

    public static void stopSend(boolean stop){
        stopSend = stop;
    }
    /**
     * 统计传输进度
     * @return
     */
    private synchronized int getPercent(SendData data,long readIndex){
        int percent = 0;
        if(data.getFileLength()!=0l&&readIndex!=0l){
            percent= (int) Math.round((readIndex/(data.getFileLength()*2.0))*100);
            Log.d(TAG,"fileLen :"+data.getFileLength()+" readIndex ;"+readIndex);
        }
        Log.d(TAG,"send percent ;"+percent);
        if(data.getSendCallBack()!=null){
            data.getSendCallBack().sendPercent(data,percent);
        }
        return percent;
    }

    /**
     * 统计传输速率
     */
    private synchronized void getSendRate(SendData data){
        long currentTime = System.currentTimeMillis();
        sendCount++;
        if(sendCount>=maxCount){
            if(data.getSendCallBack()!=null){
                long sendTime = currentTime-startTime;
                Log.d(TAG,"Send Rate "+"  startTime:"+startTime+" SendTime :"+sendTime);
                startTime = System.currentTimeMillis();
                long sendBytes = HweProtocol.PACKAGE_BYTE_SIZE*sendCount;
                SendRate sendRate = new SendRate(sendBytes,sendTime);
                data.getSendCallBack().sendRate(data,sendRate);
            }
            sendCount=0;
        }
    }
    /**
     * 拼包
     * @param sendData
     * @param fileData
     * @param readLen
     * @return
     */
    private byte[] getPackageData(SendData sendData,byte[] fileData,int readLen){
        byte[] packageData = new byte[HweProtocol.PACKAGE_BYTE_SIZE];
        Arrays.fill(packageData, (byte) 0);//初始化
        if(fileData!=null){//拼正文包
            byte[] fileId = HweProtocol.longToBytes(sendData.getFileId());
            byte[] addBytes = new byte[HweProtocol.MD5_POINT];
            Arrays.fill(addBytes, (byte) 0);//初始化
            System.arraycopy(fileId, 0, addBytes, 0, fileId.length);//前8位为fileId
            addBytes[fileId.length] = sendData.getSendType();//第9位为sendType
            System.arraycopy(fileData, 0, addBytes, fileId.length+HweProtocol.SEND_TYPE, readLen);
            System.arraycopy(addBytes,0,packageData,0,addBytes.length);//中间为正文数据
            byte[] md5bytes = HweProtocol.getMD5(addBytes);
            System.arraycopy(md5bytes,0,packageData,addBytes.length,md5bytes.length);//后16位为md5校验码
        }else {//拼第一个包
            FileHeader fileHeader = new FileHeader(sendData.getFileId(),sendData.getSendType(),sendData.getFileLength(),sendData.getMimeType());
            byte[] fileHeadBytes = HweProtocol.fileHead2Bytes(fileHeader);
            byte[] addBytes = new byte[HweProtocol.MD5_POINT];
            Arrays.fill(addBytes,(byte) 0);//初始化
            System.arraycopy(fileHeadBytes,0,addBytes,0,fileHeadBytes.length);
            System.arraycopy(addBytes,0,packageData,0,addBytes.length);
            byte[] md5bytes = HweProtocol.getMD5(addBytes);
            System.arraycopy(md5bytes,0,packageData,addBytes.length,md5bytes.length);//后16位为md5校验码
        }

        return packageData;
    }


    public interface SendCallBack{
        void sendStart(SendData sendData);
        void sendFinish(SendData sendData);
        void sendRate(SendData sendData,SendRate sendRate);
        void sendPercent(SendData sendData,int percent);
    }
}