package com.single.code.tool.bluetooth.classic.api;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


/**
 * 
 * @author yao.guoju
 */
public abstract class BluetoothClient implements Runnable {

	private final static String TAG = "BluetoothClient";
	private BluetoothDevice device;
	private UUID uuid;
	private BluetoothSocket socket;
	private Context context;
	public BluetoothClient(Context context, BluetoothDevice device, UUID uuid) {
		this.device = device;
	    this.context = context.getApplicationContext();
		this.uuid    = uuid;
	}



    @Override
	public void run() {
		// TODO Auto-generated method stub
        Log.d(BluetoothProfile.TAG, "Client run");
		if(BluetoothApi.initDeviceBle(context)) {
			if(BluetoothApi.getBleAdpter(context).isDiscovering()){
				BluetoothApi.getBleAdpter(context).cancelDiscovery();
			}
			try {
				Log.d(BluetoothProfile.TAG, "createRfcommSocketToServiceRecord");
				socket = device.createRfcommSocketToServiceRecord(uuid);//根据UUID创建并返回一个BluetoothSocket这个方法也是我们获取BluetoothDevice的目的——创建BluetoothSocket
//				socket = device.createInsecureRfcommSocketToServiceRecord(uuid);//根据UUID创建并返回一个BluetoothSocket这个方法也是我们获取BluetoothDevice的目的——创建BluetoothSocket
			     if(socket!=null){
                    socket.connect();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
				Log.d(TAG, "createRfcommSocket error :" + e.toString());
				try {
					socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device, Integer.valueOf(1));
					socket.connect();
				}catch (Exception oie){

				}

			}
			manageConnectedSocket(socket);
		}
	}

	/**
	 * 取消socket请求
	 */
	public void cancel() {
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 抽象方法处理监听到的socket请求
	 * @param socket
	 */
	protected abstract void manageConnectedSocket(BluetoothSocket socket);
}
