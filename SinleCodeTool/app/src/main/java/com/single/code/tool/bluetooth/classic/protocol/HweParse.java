package com.single.code.tool.bluetooth.classic.protocol;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/11/6.
 */
public class HweParse {
    private static HweParse PARSE;
    private String TAG = "HweParse";
    private PackageData packageData;
    private FileHeader currentFileHead;
    private String filePath;
    private byte[] surplus = new byte[0];//拼包剩余字节
    private ExecutorService worker = Executors.newSingleThreadExecutor();
    private FileOutputStream fos;
    private long writeLen;//已经写入文件的字节数
    private Map<Long,MessageData> messageDataMap = new ConcurrentHashMap<>();
    private Set<ParseCallBack> parseCallBackSet = new CopyOnWriteArraySet<>();
    public class ParseRunnable implements Runnable {
        private byte[] parseData;//待解析的数据
        public ParseRunnable(byte[] data){
            parseData = data;
        }
        @Override
        public void run() {
            parse(parseData);
        }
    }

    public void setParseCallBack(ParseCallBack parseCallBack) {
        this.parseCallBackSet.add(parseCallBack);
    }
    public void removeParseCallBack(ParseCallBack parseCallBack){
        this.parseCallBackSet.remove(parseCallBack);
    }
//
//    /**
//     * 单例
//     * @return
//     */
//    public static HweParse getParse(){
//        if(PARSE ==null){
//         synchronized (HweParse.class){
//             if(PARSE==null){
//                 PARSE = new HweParse();
//             }
//         }
//        }
//        return PARSE;
//    }


    private void parse(byte[] data){
        int parseLen = data.length;
        int surplusLen = surplus.length;
        if((parseLen+surplusLen)>=HweProtocol.PACKAGE_BYTE_SIZE){
            int addLen = HweProtocol.PACKAGE_BYTE_SIZE-surplusLen;
            byte[] packageBytes = HweProtocol.merge(data,0,surplus,addLen,HweProtocol.PACKAGE_BYTE_SIZE);//拼包
            parseHead(packageBytes);
            surplus = new byte[0];
            surplus = HweProtocol.merge(data,HweProtocol.PACKAGE_BYTE_SIZE-surplusLen,surplus,parseLen-addLen,parseLen-addLen);//保存剩余字节
        }else {
            surplus = HweProtocol.merge(data, 0, surplus, parseLen, parseLen + surplusLen);
        }
    }
    private void parseHead(byte[] packageBytes){
        long fileId = HweProtocol.bytesToLong(Arrays.copyOfRange(packageBytes,0,HweProtocol.FILE_ID_BYTE_SIZE));
        byte sendType = packageBytes[HweProtocol.FILE_ID_BYTE_SIZE];
        if(sendType == PackageUtil.SendType.Response|| sendType == PackageUtil.SendType.ParseFinish){
            //TODO 解析response
            parseResponse(packageBytes);
        }else if(sendType == PackageUtil.SendType.Message){
            //TODO 解析message
            parseMessage(packageBytes);
        }else {
            //TODO 解析文件
            if(currentFileHead!=null&&currentFileHead.getFileId()==fileId){
                packageData = new PackageData(packageBytes,false);
                if(packageData.isCheckMd5()){//校验md5值
                    sendResponse(true,fileId,PackageUtil.SendType.Response);
                    parseFile(packageData.getFileData(),currentFileHead);
                }else {
                    sendResponse(false, fileId,PackageUtil.SendType.Response);
                }
            }else {//新文件
                reset();
                packageData = new PackageData(packageBytes,true);
                if(packageData.isCheckMd5()){//校验md5值
                    currentFileHead = packageData.getFileHeader();
                    sendResponse(true, fileId,PackageUtil.SendType.Response);
                }else {
                    sendResponse(false, fileId,PackageUtil.SendType.Response);
                }
            }
        }
    }
    private  void reset(){
        filePath = "";
        currentFileHead =null;
        writeLen = 0;
    }
    private void parseFinish(){
        //TODO send response
        if(currentFileHead!=null){
            if(currentFileHead.getSendType()==PackageUtil.SendType.upload|| currentFileHead.getSendType() == PackageUtil.SendType.MediaShare){
                //TODO 文件解析结束
                if(writeLen!=currentFileHead.getLength()){
                    sendResponse(false,currentFileHead.getFileId(),PackageUtil.SendType.ParseFinish);
                }else {
                    sendResponse(true,currentFileHead.getFileId(),PackageUtil.SendType.ParseFinish);
                }
            }
        }
        reset();
    }

    public void cancle(){
        surplus = new byte[0];
        filePath = "";
        currentFileHead =null;
        writeLen = 0;
        packageData = null;
        messageDataMap.clear();
    }
    private void parseResponse(byte[] packageBytes){
        Log.d(TAG,"parseResponse");
        PackageData packageData = new PackageData(packageBytes,true);
        long fileId = packageData.getFileHeader().getLength();
        byte sendType = packageData.getFileHeader().getSendType();
        byte responseCode =packageData.getFileHeader().getMimeType();
        if(sendType == PackageUtil.SendType.Response){
            Iterator<ParseCallBack> iterator = parseCallBackSet.iterator();
            while (iterator.hasNext()){
                ParseCallBack callBack = iterator.next();
                callBack.parseResponse(fileId,responseCode);
            }
        }else if(sendType == PackageUtil.SendType.ParseFinish){
            Iterator<ParseCallBack> iterator = parseCallBackSet.iterator();
            while (iterator.hasNext()){
                ParseCallBack callBack = iterator.next();
                callBack.parseFileFinish(fileId, responseCode);
            }
        }
    }

    private void parseMessage(byte[] packageBytes){
        Log.d(TAG,"parseMessage");
        byte[] validData = new byte[0];
        long messageId = 0;
        MessageData newMessageData = new MessageData(packageBytes);
        if(newMessageData.isValid()){
            if(messageDataMap.containsKey(newMessageData.getMessageId())){
                messageDataMap.remove(newMessageData.getMessageId());
            }
            messageId = newMessageData.getMessageId();
            validData = newMessageData.getValidData();
        }else {
            if(messageDataMap.containsKey(newMessageData.getMessageId())){
                MessageData oldMessage = messageDataMap.get(newMessageData.getMessageId());
                byte[]oldData = oldMessage.getValidData();
                byte[] newData = newMessageData.getValidData();
                long validLen =oldMessage.getLength();
                messageId = oldMessage.getMessageId();
                if(validLen<=(oldData.length+newData.length)){
                    validData = new byte[(int) validLen];
                    System.arraycopy(oldData,0,validData,0,oldData.length);
                    System.arraycopy(newData,0,validData,oldData.length, (int) (validLen-oldData.length));
                    messageDataMap.remove(messageId);
                }else {
                    messageDataMap.remove(messageId);
                    byte[] sValidData = new byte[oldData.length+newData.length];
                    System.arraycopy(oldData,0,sValidData,0,oldData.length);
                    System.arraycopy(newData, 0, sValidData, oldData.length, newData.length);
                    oldMessage.setValidData(sValidData);
                    messageDataMap.put(messageId,oldMessage);
                    return;
                }
            }else {
                messageDataMap.put(newMessageData.getMessageId(),newMessageData);
                return;
            }
        }
        if(validData.length>0){
            Iterator<ParseCallBack> iterator = parseCallBackSet.iterator();
            while (iterator.hasNext()){
                ParseCallBack callBack = iterator.next();
                callBack.parseMessage(messageId,newMessageData.getValidData());
            }
        }
    }
    private void parseFile(byte[] fileData,FileHeader fileHeader){
        Log.d(TAG,"parseFile");
        if(fileHeader!=null){
            switch (fileHeader.getMimeType()){
                case PackageUtil.MimeType.Jpg:
                    filePath = PackageUtil.sendFileDir+ File.separator+fileHeader.getFileId()+".jpg";
                    break;
                case PackageUtil.MimeType.Png:
                    filePath =  PackageUtil.sendFileDir+ File.separator+fileHeader.getFileId()+".png";
                    break;
                case PackageUtil.MimeType.Mp3:
                    filePath =  PackageUtil.sendFileDir+ File.separator+fileHeader.getFileId()+".mp3";
                    break;
                case PackageUtil.MimeType.Mp4:
                    filePath =  PackageUtil.sendFileDir+ File.separator+fileHeader.getFileId()+".mp4";
                    break;
                default:
                    filePath = PackageUtil.sendFileDir+File.separator+fileHeader.getFileId()+".txt";
                    break;
            }
            try {
                fos = new FileOutputStream(filePath);

            } catch (FileNotFoundException e) {

            }
        }else {
            return;
        }
        if(fileData!=null){
            try{
                long waitWriteLen = fileHeader.getLength()-writeLen;//文件总大小减去已经写入的大小
                if(waitWriteLen<HweProtocol.VALID_DATA_BYTE_SIZE){
                    writeLen+=waitWriteLen;
                    fos.write(fileData,0, (int) waitWriteLen);
                    parseFinish();
                }else {
                    writeLen +=fileData.length;
                    fos.write(fileData,0,fileData.length);
                }
            }catch (IOException e){

            }finally {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }
    private void sendResponse(boolean md5OK,long fileId,byte sendType){
        byte[] packageData = new byte[HweProtocol.PACKAGE_BYTE_SIZE];
        long responseId = System.currentTimeMillis();
        FileHeader fileHeader;
        if(md5OK){
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
        System.arraycopy(md5bytes, 0, packageData, addBytes.length, md5bytes.length);
        //TODO 发回response的包
    }

    public interface ParseCallBack{
        void parseResponse(long fileId,byte responseCode);
        void parseMessage(long messageId,byte[] message);
        void parseFileFinish(long fileId,byte responseCode);
    }

    public void receiver(byte[] data){
        ParseRunnable parseRunnable = new ParseRunnable(data);
        worker.execute(parseRunnable);
    }
}