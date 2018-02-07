package com.single.code.tool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.single.code.tool.bluetooth.ble.api.BLEAdvertiser;
import com.single.code.tool.bluetooth.ble.api.BLEHelper;
import com.single.code.tool.bluetooth.ble.api.BLEProfile;
import com.single.code.tool.bluetooth.ble.api.BLEScanHelper;
import com.single.code.tool.bluetooth.ble.api.BLEScanner;
import com.single.code.tool.bluetooth.ble.callback.IBLECallback;
import com.single.code.tool.bluetooth.ble.entity.ScanData;
import com.single.code.tool.bluetooth.classic.api.BluetoothHelper;
import com.single.code.tool.bluetooth.classic.api.BluetoothProfile;
import com.single.code.tool.bluetooth.classic.api.BluetoothScanner;
import com.single.code.tool.bluetooth.classic.callback.IBluetoothCallback;
import com.single.code.tool.bluetooth.classic.callback.IPairCallback;
import com.single.code.tool.bluetooth.classic.callback.IScanCallback;
import com.single.code.tool.bluetooth.classic.entity.SendRate;
import com.single.code.tool.bluetooth.classic.entity.State;
import com.single.code.tool.bluetooth.classic.protocol.PackageUtil;
import com.single.code.tool.bluetooth.classic.receiver.PairBroadcastReceiver;
import com.single.code.tool.policy.PolicyManager;
import com.single.code.tool.bluetooth.classic.protocol.ZipTool;
import com.single.code.tool.syncdb.DBTestManager;
import com.single.code.tool.util.SystemUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends Activity {
    private PolicyManager policyManager;
    private String TAG = "PolicyManager";
    private Handler handler = new Handler();
    private DBTestManager dbTestManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    @TargetApi(26)
    private  void initData(){
        policyManager = PolicyManager.getManager();
//        dbTestManager = DBTestManager.getManager();
        String devId0 = SystemUtil.getDevID(getApplicationContext(),0);
        String devId1 = SystemUtil.getDevID(getApplicationContext(),1);
        String Meid = SystemUtil.getMEID(getApplicationContext());
        String IMEI = SystemUtil.getIMEI(getApplicationContext());
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String devId = telephonyManager.getImei();
        String imei0 = telephonyManager.getImei(0);
        String iemi1 = telephonyManager.getImei(1);
        String devMEID = telephonyManager.getDeviceId();
        Log.d(TAG,"dev0:"+devId0+" dev1:"+devId1+" meid:"+Meid+" IMEI:"+IMEI);
        Log.d(TAG,"devID :"+devId+" devMEID :"+devMEID+" imei0:"+imei0+" imei1:"+iemi1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i<2){
                    i++;
                    Log.d(TAG, "insert Thread");
                    try {
                        int j=2000;
                        String zipbe = "hadsaofasoiufasfasdfudsafjdsamfkldsjaifuasfmeqwrasfgadslgkasdgkasdglasdglgasdgadfgadafdagsdfagdsagsdagsdagdsagsdagdsagasdgasdgdasgdsagdasgasdgadasgasdgasgasdgdasgdsagdsagsdagsdagasdgasdgasdgdsagasdgdsagdsagsdagdsagadsagdsagasdgdsag";
                       StringBuffer stringBuffer = new StringBuffer();
                        while (j>0){
                            j--;
                            stringBuffer.append(zipbe);
                        }
                        final byte[] zipbeBy = stringBuffer.toString().getBytes("UTF-8");
                        final byte[][] byGzip = new byte[1][1];
                        final byte[][] byunGzip = new byte[1][1];
                        ZipTool.getZipTool().gZip(zipbeBy, new ZipTool.ZipCallback() {
                            @Override
                            public void zipBytes(byte[] zipBytes) {
                                byGzip[0] = zipBytes;
                                Log.d("ZIPTOOL", "zipbeBy :" + zipbeBy.length + "  byGzip :" + byGzip[0].length);
                                ZipTool.getZipTool().unGZip(byGzip[0], new ZipTool.ZipCallback() {
                                    @Override
                                    public void zipBytes(byte[] zipBytes) {

                                    }

                                    @Override
                                    public void unZipBytes(byte[] unZipBytes) {
                                        byunGzip[0] = unZipBytes;
                                        try {
                                            Log.d("ZIPTOOL", "byunGzip :" + byunGzip[0].length + "   " + new String(byunGzip[0], "UTF-8"));
                                        } catch (UnsupportedEncodingException e) {

                                        }
                                    }
                                });
                            }

                            @Override
                            public void unZipBytes(byte[] unZipBytes) {

                            }
                        });
                        byte[] byzip  = ZipTool.getZipTool().zip(zipbeBy);
                        Log.d("ZIPTOOL","zipbeBy :"+zipbeBy.length+" byzip :"+byzip.length);
                        byte[] byunzip = ZipTool.getZipTool().unZip(byzip);
                        Log.d("ZIPTOOL","byunzip :"+byunzip.length+"   "+new String(byunzip,"UTF-8"));
//                        Log.d("ZIPTOOL","zipbe :"+zipbe+" bytesLen :"+zipbeBy.length);
//                        String zipov = ZipTool.getZipTool().compress(zipbe);
//                        Log.d("ZIPTOOL","zipov :"+zipov);
//                        byte[] zipby = zipov.getBytes("UTF-8");
//                        Log.d("ZIPTOOL","zipbyLen :"+zipby.length);
//                        String unzipby = new String(zipby,"UTF-8");
//                        Log.d("ZIPTOOL","unzipby :"+unzipby);
//                        String unzip = ZipTool.getZipTool().uncompress(unzipby);
//                        Log.d("ZIPTOOL","unzip :"+unzip);
                    }catch (IOException e){

                    }
//                    dbTestManager.insert(getApplicationContext());
//                    policyManager.insert(getApplicationContext());
//                    policyManager.insertList(getApplicationContext());
//                    policyManager.query(getApplicationContext());
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i<100){
                    i++;
                    Log.d(TAG, "query Thread");
//                    policyManager.insertList(getApplicationContext());
//                    dbTestManager.query(getApplicationContext());
//                    policyManager.query(getApplicationContext());
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i<100){
                    i++;
                    Log.d(TAG,"update Thread");
//                    dbTestManager.update(getApplicationContext());
//                    policyManager.update(getApplicationContext());
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i<100){
                    i++;
                    Log.d(TAG,"delete Thread");
//                    dbTestManager.delete(getApplicationContext());
//                    policyManager.delete(getApplicationContext());
                }
            }
        }).start();
    }

    private void testBle(){
        BluetoothScanner bluetoothScanner = new BluetoothScanner(getApplicationContext(), new IScanCallback<BluetoothDevice>() {
            @Override
            public void discoverDevice(BluetoothDevice bluetoothDevice) {
                //TODO 扫描到的经典蓝牙设备
            }

            @Override
            public void scanTimeout() {

            }

            @Override
            public void scanFinish(List<BluetoothDevice> bluetoothDevices) {

            }
        });
        bluetoothScanner.startScan();//开启扫描
        bluetoothScanner.stopScan();//关闭扫描
    }

    private void testBleCon(){
        //注册Listener接收链接状态和读写数据结果
        BluetoothHelper.HELPER.setCallback(new IBluetoothCallback<byte[]>() {
            @Override
            public void connectStateChange(State state, int type) {

            }

            @Override
            public void writeData(byte[] data, int type) {

            }

            @Override
            public void readData(byte[] data, int type) {

            }

            @Override
            public void setDeviceName(String name) {

            }

            @Override
            public void sendRate(SendRate sendRate) {

            }

            @Override
            public void startSend(long fileid, long startTime) {

            }

            @Override
            public void endSend(long fileid, long endTime) {

            }
        });
    }

    private void testBLE(){

        BLEScanHelper.HELPER.startScan(getApplicationContext(), new BLEScanner.IScanResultListener() {
            @Override
            public void onResultReceived(ScanData sanDevice) {
                BLEHelper.HELPER.startConnect(getApplicationContext(), sanDevice.getBleAddress(), sanDevice.getDeviceName());//发起BLE链接
            }

            @Override
            public void onScanFailed(int errorCode) {

            }

            @Override
            public void onScanUpdate(ScanData scanData) {

            }
        });
        //注册Listener接收链接状态和接收数据
        BLEHelper.HELPER.setBLECallBack(new IBLECallback() {
            @Override
            public void onConnected(int type) {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onDataReceived(byte[] data, int type) {
                //接收到的数据
            }
        });
        //模拟BLE信标
        BLEHelper.HELPER.startBleAdvertiser(getApplicationContext(), new BLEAdvertiser.IAdvertiseResultListener() {
            @Override
            public void onAdvertiseSuccess() {
                //模拟信标成功后，开启服务端，等待链接和接收数据
                BLEHelper.HELPER.startBleServer(getApplicationContext());
            }

            @Override
            public void onAdvertiseFailed(int errorCode) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
