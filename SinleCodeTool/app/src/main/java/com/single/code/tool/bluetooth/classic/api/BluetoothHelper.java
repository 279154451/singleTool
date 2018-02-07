package com.single.code.tool.bluetooth.classic.api;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.single.code.tool.bluetooth.classic.callback.IBluetoothCallback;
import com.single.code.tool.bluetooth.classic.entity.SendData;
import com.single.code.tool.bluetooth.classic.entity.SendRate;
import com.single.code.tool.bluetooth.classic.entity.State;
import com.single.code.tool.bluetooth.classic.protocol.HweProtocol;
import com.single.code.tool.bluetooth.classic.protocol.HweSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yxl on 2017/4/18.
 */

public enum BluetoothHelper {
    HELPER;
    private String TAG = "BluetoothHelper";
    private Set<IBluetoothCallback<byte[]>> callbackSet = new CopyOnWriteArraySet<>();
    private Thread clientThread;
    private Thread serverThread;
    private BluetoothClient bluetoothClient;
    private BluetoothServer bluetoothServer;
    private BluetoothSocket clientSocket;
    private BluetoothSocket serverSocket;
    private InputStream clientInputStream;
    private InputStream serverInputStream;
    private OutputStream clientOutputStream;
    private OutputStream serverOutputStream;
    private State mState = State.STATE_LISTEN;
    private ExecutorService worker = Executors.newSingleThreadExecutor();
    private ConcurrentLinkedQueue sendQueue = new ConcurrentLinkedQueue();
    private boolean sending = false;
    private SendData currentSendData;
    private IBCallBack clientIbCallBack = new IBCallBack() {
        @Override
        public void sendNotify() {
            nextSendToClient();
        }

        @Override
        public void sendRate(SendRate rate) {
            mHandler.obtainMessage(BluetoothProfile.MESSAGE_SNED_RATE, -1, -1, rate).sendToTarget();
        }

        @Override
        public void startSend(long fileid, long startTime) {

        }

        @Override
        public void endSend(long fileid, long endtime) {

        }


        @Override
        public void progress(long fileid,int percent) {

        }
    };
    private IBCallBack serverIbcallback = new IBCallBack() {
        @Override
        public void sendNotify() {
            nextSendToServer();
        }

        @Override
        public void sendRate(SendRate rate) {
            mHandler.obtainMessage(BluetoothProfile.MESSAGE_SNED_RATE, -1, -1, rate).sendToTarget();
        }

        @Override
        public void startSend(long fileid, long startTime) {
        }

        @Override
        public void endSend(long fileid, long endtime) {
        }

        @Override
        public void progress(long fileid, int percent) {
            Log.d(TAG, "upload progress :" + percent);
        }

    };
    public void setCallback(IBluetoothCallback<byte[]> callback){
        callbackSet.add(callback);
    }
    public void removeCallback(IBluetoothCallback<byte[]>callback){
        callbackSet.remove(callback);
    }



    /**
     * 客户端发起连接
     * @param context
     * @param device  要链接的蓝牙设备
     * @param uuid  蓝牙读写   如public static UUID BLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
     */
    public synchronized void startClient(Context context, BluetoothDevice device, UUID uuid){
        bluetoothClient = new BluetoothClient(context.getApplicationContext(),device,uuid) {
            @Override
            protected void manageConnectedSocket(BluetoothSocket socket) {
                Log.d(TAG, "Client manageConnectedSocket");
                if(socket!=null&&socket.isConnected()){
                    setmState(State.STATE_CONNECTED,BluetoothProfile.Type_Client);
                    mHandler.obtainMessage(BluetoothProfile.MESSAGE_DEVICE_NAME, -1, -1, socket.getRemoteDevice().getName()).sendToTarget();
                    clientSocket = socket;
                    try {
                        clientInputStream = clientSocket.getInputStream();
                        clientOutputStream = clientSocket.getOutputStream();
                        clientRead();
                    } catch (IOException e) {
                        setmState(State.STATE_NONE,BluetoothProfile.Type_Client);
                        e.printStackTrace();
                    }
                }else {
                    Log.d(TAG, "bad bluetooth socket");
                    cancelClient();
                }
            }
        };
        clientThread = new Thread(bluetoothClient);
        clientThread.start();
    }

    /**
     * 释放客户端相关资源
     */
    public synchronized void cancelClient(){
        if(clientThread!=null){
            Log.d(TAG, "cancelClient");
            try {
                if(clientThread!=null) {
                    clientThread = null;
                    if (clientInputStream != null) {
                        Log.d(TAG, "clientInputStream.close()");
                        clientInputStream.close();
                        clientInputStream = null;
                    }
                    if (clientOutputStream != null) {
                        Log.d(TAG, "clientOutputStream.close()");
                        clientOutputStream.close();
                        clientOutputStream = null;
                    }
                    if (clientSocket != null) {
                        Log.d(TAG, " clientSocket.close()");
                        clientSocket.close();
                        clientSocket = null;
                    }
                    if (bluetoothClient != null) {
                        Log.d(TAG, " bluetoothClient.cancel()");
                        bluetoothClient.cancel();
                        bluetoothClient = null;
                    }
                }
            }catch (IOException e){
                    Log.d(TAG, "cancelClient IOException :" + e.toString());
            }finally {
                setmState(State.STATE_NONE,BluetoothProfile.Type_Client);//断开连接
            }
        }
    }

    /**
     * 设置当前状态
     * @param state
     */
    public void setmState(State state,int type){
        this.mState = state;
        mHandler.obtainMessage(BluetoothProfile.MESSAGE_STATE_CHANGE, -1, type, state).sendToTarget();
    }

    public State getmState() {
        return mState;
    }

    /**
     * 开启server等待连接线程
     * @param context
     * @param uuid  蓝牙读写UUID     如public static UUID BLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
     */
    public void startServer(Context context, UUID uuid){
        if(serverSocket!=null&& serverSocket.isConnected()){
            Log.d(TAG, "startServer  close old socket");
            try {
                serverInputStream.close();
                serverOutputStream.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(bluetoothServer==null){
            Log.d(TAG, "startServer");
            bluetoothServer = new BluetoothServer(context,this,BluetoothHelper.class.getName(),uuid) {
                @Override
                protected void manageConnectedSocket(BluetoothSocket socket) {
                    Log.d(TAG, "manageConnectedSocket");
                    if(socket!=null&& socket.isConnected()){
                        setmState(State.STATE_CONNECTED,BluetoothProfile.Type_Server);
                        serverSocket = socket;
                        try {
                            serverInputStream = serverSocket.getInputStream();
                            serverOutputStream = serverSocket.getOutputStream();
                            serverRead();
                        } catch (IOException e) {
                            setmState(State.STATE_NONE,BluetoothProfile.Type_Server);//连接断开
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG, "Bad BluetoothSockect");
                    }
                }
            };
            serverThread = new Thread(bluetoothServer);
            serverThread.start();
        }
    }


    /**
     * 释放服务端的相关sockect资源
     */
    public void cancelServer(){
        if(serverThread!=null){
            Log.d(TAG, "cancelServer");
            try {
                if(serverInputStream!=null){
                    serverInputStream.close();
                    serverInputStream = null;
                }
                if(serverOutputStream!=null){
                    serverOutputStream.close();
                    serverOutputStream = null;
                }
                if(serverSocket!=null){
                    serverSocket.close();
                    serverSocket= null;
                }
                if(bluetoothServer!=null){
                    bluetoothServer.cancel();
                    bluetoothServer = null;
                }
                if(serverThread!=null){
                    serverThread =null;
                }
            }catch (IOException e){
                Log.d(TAG, " cancelServer e:" + e.toString());
            }finally {
                setmState(State.STATE_NONE,BluetoothProfile.Type_Server);//断开连接
            }
        }
    }


    /**
     * 发送数据给client
     * @param message
     */
    public void sendDataToClient(String message){
        try {
            byte[] data = message.getBytes("UTF-8");
            writeToCient(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据给server
     * @param message
     */
    public void sendDataToServer(String message){
        try {
            byte[] data = message.getBytes("UTF-8");
            writeToServer(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     *  向客户端发送文件
     * @param sendtype PackageUtil.SendType
     * @param fileid  文件、文本的唯一标识
     * @param minetype  PackageUtil.MimeType
     * @param file  文件路径、文本消息
     */
    public void sendFileToClient(final byte sendtype,final long fileid, final byte minetype, final String file){
        SendData sendData = new SendData(fileid,minetype,sendtype,file);
        sendQueue.offer(sendData);
//        wirteFileToClient(cmdid,minetype,file);
        startSendToClient();

    }

    private void startSendToClient(){
        if(sending){
            Log.d(TAG, "is sending ");
            return;
        }
        nextSendToClient();
    }
    private void nextSendToClient(){
        if(sendQueue.isEmpty()){
            sending = false;
            currentSendData=null;
            return;
        }
        sending = true;
       SendData sendData = (SendData) sendQueue.poll();
        if(sendData!=null){
            currentSendData = sendData;
            wirteFileToClient(sendData);
        }else {
            nextSendToClient();
        }
    }

    public boolean cleanSendQueue(){
        while (!sendQueue.isEmpty()){
            sendQueue.clear();
        }
        sending = false;
        currentSendData=null;
        return true;
    }
    public ConcurrentLinkedQueue getSendQueue(){
        return sendQueue;
    }

    public void writeToCient(final byte[] data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(serverOutputStream!=null){
                    try {
                        serverOutputStream.flush();
                        serverOutputStream.write(data);
                        serverOutputStream.flush();
                        Log.d(TAG, "send data to client ");
//                        mHandler.obtainMessage(BluetoothProfile.MESSAGE_WRITE, -1, -1, data).sendToTarget();
                    } catch (IOException e) {
                        try {
                            if(serverSocket!=null){
                                serverSocket.close();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }finally {
                            setmState(State.STATE_NONE,BluetoothProfile.Type_Server);//断开连接
                        }
                    }
                }else {
                    setmState(State.STATE_LISTEN,BluetoothProfile.Type_Server);//未连接
                }
            }
        }).start();
    }

    /**
     * 发送文件到服务端
     * @param cmdid
     * @param minetype
     * @param filePath
     */
    public void  sendFileToServer(final byte sendtype,final long cmdid, final byte minetype, final String filePath){
        SendData sendData = new SendData(cmdid,minetype,sendtype,filePath);
        sendQueue.offer(sendData);
//        writeFileToServer(cmdid,minetype,filePath);
        startSendToServer();
    }
    private void startSendToServer(){
        if(sending){
            return;
        }
        Log.d(TAG, "startSendToServer");
        nextSendToServer();
    }
    private void nextSendToServer(){
            if(sendQueue.isEmpty()){
                sending  =false;
                currentSendData=null;
                return;
            }
        Log.d(TAG, "nextSendToServer");
        sending = true;
        SendData sendData = (SendData) sendQueue.poll();
        if(sendData!=null){
            currentSendData = sendData;
            writeFileToServer(sendData);
        }else {
            nextSendToServer();
        }
    }

    /**
     * 获取正在发送的文件的fileid
     * @return
     */
    public SendData getCurrentSendData(){
        return currentSendData;
    }

    /**
     * 判断当前是否在发送中
     * @return
     */
    public boolean isSending(){
        return sending;
    }

    private void writeFileToServer(SendData sendData){

    }

    /**
     * 向客户端发送文件
     */
    private void wirteFileToClient(SendData sendData){
        if(serverOutputStream!=null){
            sendData.setOutputStream(serverOutputStream);
            HweSender.getSender().sendFile(sendData);
        }else {
            setmState(State.STATE_LISTEN,BluetoothProfile.Type_Server);//未连接
        }
    }

    private void writeToServer(final byte[] data){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(clientOutputStream!=null){
                    try {
                        clientOutputStream.write(data);
//                        mHandler.obtainMessage(BluetoothProfile.MESSAGE_WRITE, -1, -1, data).sendToTarget();
                    } catch (IOException e) {
                        setmState(State.STATE_NONE,BluetoothProfile.Type_Client);//断开连接
                    }
                }else {
                    setmState(State.STATE_LISTEN,BluetoothProfile.Type_Client);//未连接
                }
            }
        }).start();
    }


    /**
     * 客户端读数据
     */
    public void clientRead(){
        int bytes;
        while (true){
            if(clientInputStream!=null){
                try {
                    int bufferlength = clientInputStream.available();
                    byte[] buffer = new byte[bufferlength];
                    if(clientInputStream!=null&&(bytes = clientInputStream.read(buffer))>0){
                        byte[] data = new byte[bytes];
                        System.arraycopy(buffer, 0, data, 0, data.length);
                        if ( callbackSet!= null) {
                            Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                            while (iterator.hasNext()){
                                IBluetoothCallback<byte[]> callback = iterator.next();
                                callback.readData(data, BluetoothProfile.Type_Client);
                            }
                        }
//                        mHandler.obtainMessage(BluetoothProfile.MESSAGE_READ, bytes, BluetoothProfile.Type_Client, data).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.d(TAG, "clientRead IOException :" + e.toString());
                    cancelClient();
                    break;
                }
            }else {
                setmState(State.STATE_LISTEN,BluetoothProfile.Type_Client);//未连接
                break;
            }
        }
    }

    /**
     * 服务端读数据
     */
    public void serverRead(){
        int bytes;
        byte[] buffer = new byte[HweProtocol.PACKAGE_BYTE_SIZE];
        while (true){
            if(serverInputStream!=null){
                try {
                    if((bytes = serverInputStream.read(buffer))>0){
                        byte[] data = new byte[bytes];
                        System.arraycopy(buffer, 0, data, 0, data.length);
                        if ( callbackSet!= null) {
                            Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                            while (iterator.hasNext()){
                                IBluetoothCallback<byte[]> callback = iterator.next();
                                callback.readData(data, BluetoothProfile.Type_Server);
                            }
                        }
//                        mHandler.obtainMessage(BluetoothProfile.MESSAGE_READ, bytes, BluetoothProfile.Type_Server, data).sendToTarget();
                    }
                }catch (Exception e){
                    Log.d(TAG, "serverRead Exception :" + e.toString());
                    try {
                        if(serverSocket!=null){
                            serverSocket.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }finally {
                        setmState(State.STATE_NONE,BluetoothProfile.Type_Server);//连接断开
                        break;
                    }
                }
            }else {
                setmState(State.STATE_LISTEN,BluetoothProfile.Type_Server);//未连接
                break;
            }
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.obj == null) {
                return;
            }
            switch (msg.what) {
                case BluetoothProfile.MESSAGE_STATE_CHANGE:
                    if ( callbackSet!= null) {
                        Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                        while (iterator.hasNext()){
                            IBluetoothCallback<byte[]> callback = iterator.next();
                            callback.connectStateChange((State) msg.obj,msg.arg2);
                        }
                    }
                    break;
                case BluetoothProfile.MESSAGE_WRITE:
                    if ( callbackSet!= null) {
                        Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                        while (iterator.hasNext()){
                            IBluetoothCallback<byte[]> callback = iterator.next();
                            callback.writeData((byte[]) msg.obj, 0);
                        }
                    }
                    break;
                case BluetoothProfile.MESSAGE_READ:
                    if ( callbackSet!= null) {
                        Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                        while (iterator.hasNext()){
                            IBluetoothCallback<byte[]> callback = iterator.next();
                            callback.readData((byte[]) msg.obj, msg.arg2);
                        }
                    }
                    break;
                case BluetoothProfile.MESSAGE_DEVICE_NAME:
                    if ( callbackSet!= null) {
                        Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                        while (iterator.hasNext()){
                            IBluetoothCallback<byte[]> callback = iterator.next();
                            callback.setDeviceName((String) msg.obj);
                        }
                    }
                    break;
                case BluetoothProfile.MESSAGE_SNED_RATE:
                    if ( callbackSet!= null) {
                        Iterator<IBluetoothCallback<byte[]>> iterator = callbackSet.iterator();
                        while (iterator.hasNext()){
                            IBluetoothCallback<byte[]> callback = iterator.next();
                            callback.sendRate((SendRate) msg.obj);
                        }
                    }
                    break;
                case BluetoothProfile.MESSAGE_SEND_START:
                    break;
                case BluetoothProfile.MESSAGE_SEND_END:
                    break;
                case BluetoothProfile.MESSAGE_SEND_TIME:

                    break;
            }
        }
    };


    public interface IBCallBack{
        void sendNotify();
        void sendRate(SendRate rate);
        void startSend(long fileid, long startTime);
        void endSend(long fileid, long endtime);
        void progress(long fileid, int pecent);
    }
}
