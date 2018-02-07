package com.single.code.tool.bluetooth.classic.api;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.single.code.tool.bluetooth.classic.entity.State;

import java.io.IOException;
import java.util.UUID;


/**
 * 
 * @author yao.guoju
 */
public abstract class BluetoothServer implements Runnable {

	private final static String TAG = "BluetoothServer";
	private String name;
	private UUID uuid;
	private Context context;
	private int TimeOut = 10000;
	BluetoothServerSocket serSocket = null;
    private BluetoothHelper mBluetoothHelper;
	private BluetoothSocket socket;
	public BluetoothServer(Context context, BluetoothHelper helper, String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
		this.context = context.getApplicationContext();
        mBluetoothHelper = helper;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(BluetoothApi.initDeviceBle(context)) {
			Log.d(BluetoothProfile.TAG, "Server Run");
			try {
				serSocket = BluetoothApi.getBleAdpter(context).listenUsingRfcommWithServiceRecord(name, uuid);//UUID创建并返回BluetoothServerSocket，这是创建BluetoothSocket服务器端的第一步,如同TCP连接中的端口号
//				serSocket = BluetoothApi.getBleAdpter(context).listenUsingInsecureRfcommWithServiceRecord(name, uuid);//UUID创建并返回BluetoothServerSocket，这是创建BluetoothSocket服务器端的第一步,如同TCP连接中的端口号
				Log.d(BluetoothProfile.TAG, "listenUsingRfcommWithServiceRecord");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(serSocket != null) {
				while(mBluetoothHelper.getmState()!= State.STATE_CONNECTED) {//不在连状态就接收
					try {
                        synchronized (this){
                            if(serSocket!=null){
                                socket = serSocket.accept();//建立连接，返回读写socket
								Log.d(BluetoothProfile.TAG, "accept");
                                if(socket != null) {
                                    if(mBluetoothHelper.getmState()!=State.STATE_CONNECTED){
                                        manageConnectedSocket(socket);
                                    }
                                }
                            }
                        }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
			}
		}
	}
	
	/**
	 * 取消服务监听
	 */
	public void cancel() {
		try{
			if(serSocket!=null){
				serSocket.close();
				serSocket =null;
			}
			if(socket!=null){
				socket.close();
				socket = null;
			}
		}catch (IOException e){

		}
	}
	
	/**
	 * 抽象方法处理监听到的socket请求
	 * @param socket
	 */
	protected abstract void manageConnectedSocket(BluetoothSocket socket);
	
}
