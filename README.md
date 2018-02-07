# singleTool
自定义工具
包含经典蓝牙、BLE链接、扫描以及自定义数据传输协议
再OKhttp的基础上分装http
自定义异步数据库可实现异步增删改。同步查，线程安全。
自定义Util工具，字节压缩，图片无损压缩等

1、http的使用
      XpRequest.Builder builder = new XpRequest.Builder();
      builder.url("https://61.152.175.45:8443/ptt/rest/mix/***");
      
      //认证头
       XpHeader header = new XpHeader.Builder().addHeader(AUTH_HEAD_KEY, ApiConnUtil.getAuthorizationHeader(context)).build();
      builder.header(header);
       XpJsonBody jsonBody = new XpJsonBody(json);
       builder.body(jsonBody);
       builder.method("POST");
       XpRequest request = builder.build();
        XpHttpClient.CLIENT.newCall(request, new Callback() {
             @Override
            public void onResponse(XpResponse rsp) throws Exception {

             }

              @Override
             public void onFailure() {

               }
             },false);

2、db 数据库DBHelper使用：基于Handler和Thread、Lock实现的异步数据库增删改，同步查询功能。线程安全。

    (1)初始化DBHelper实例：
    
      dbHelper = DBHelper.getDbHelper(context, new InitDBEvent() {
                @Override
                public void onDbCreate(SQLiteDatabase database) {
                    //重写DB onCreate
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    //重写DB onUpgrade
                }
            });
    (2)增：异步操作，结果在Event中接收
    
     public void insert(Context context,String table,ContentValues values,InsertEvent insertEvent){
            DBHelper.DBAction dbAction = new DBHelper.DBAction(table,values,null,insertEvent);
            dbHelper.insert(dbAction);
        }
        public void insertList(Context context,String table,List<ContentValues> valuesList,InsertEvent insertEvent){
            initDBHelper(context);
            DBHelper.DBAction dbAction = new DBHelper.DBAction(table,valuesList,null,insertEvent);
            dbHelper.insertList(dbAction);
        }
     (3)删：异步操作，结果在Event中接收
        
       public void delete(Context context,String table,String selectionSql,DeleteEvent deleteEvent){
     //        String command = "DELETE FROM "+table+(!TextUtils.isEmpty(whereStr)?" WHERE "+whereStr:"");
             Log.d(TAG,"delete :"+selectionSql);
             DBHelper.DBAction dbAction = new DBHelper.DBAction(table,null,selectionSql,deleteEvent);
             dbHelper.delete(dbAction);
         }
     (4)改：异步操作，结果在Event中接收
     
     public void update(Context context,String table,ContentValues values,String selectionSql,UpdateEvent updateEvent){
             Log.d(TAG, "update :" + selectionSql);
             DBHelper.DBAction dbAction = new DBHelper.DBAction(table,values,selectionSql,updateEvent);
             dbHelper.update(dbAction);
         }
     (5)查：同步操作：同步操作和异步操作是互斥的，有一方在操作时，数据库就处于加锁状态。
     
       public void query(Context context){
            SQLiteDatabase database = dbHelper.getReadDatabase();
             if(database!=null){
                 try{
                     Log.d(TAG, "query");
                     String orderBy = "id asc LIMIT 500";
                     database.beginTransaction();
                     Cursor cursor = database.query(PolicyTable.BEACON_ID_TABLE,null,null,null,null,null,null);
                     if(cursor!=null&& !cursor.isClosed() &&cursor.getCount()>0){
                         while (cursor.moveToNext()){
                             String deviceID = cursor.getString(cursor.getColumnIndexOrThrow(ValidDevTable.BEACON_ID));
                             int policyId = cursor.getInt(cursor.getColumnIndexOrThrow(ValidDevTable.POLICY_ID));
                             Log.d(TAG,"deviceId :"+deviceID+" policyId :"+policyId);
                         }
                     }
                     if(cursor!=null){
                         cursor.close();
                         cursor = null;
                     }
                     database.setTransactionSuccessful();
                     database.endTransaction();
                 }catch (Exception e){

                 }finally {
                     dbHelper.closeDatabase(database);
                 }
             }
         }
3、syncDB 数据库：基于AsyncQueryHandler和ContentProvider实现线程安全的数据库异步操作

   (1)定义自己的contentProvider。如AsyncProvider
   
   (2)增：
   
       public void insert(Context context){
           AsyncDbTool dbHelper = new AsyncDbTool(context);
           for(int i =0;i<1000;i++){
               ContentValues values  = new ContentValues();
               values.put(ValidDevTable.BEACON_ID,"111111");
               values.put(ValidDevTable.DEV_TYPE,i);
               values.put(ValidDevTable.POLICY_ID,i);
               values.put(ValidDevTable.POLICY_VER, i);
               dbHelper.insert(uri, 1, new DbEventListener() {
                   @Override
                   public void AsyncUpdateComplete(int token, Object checkObj) {

                   }

                   @Override
                   public void AsyncQuery(int token, Cursor cursor, Object checkObj) {

                   }

                   @Override
                   public void AsyncDelete(int token, Object checkObj, int result) {

                   }
               }, values);
           }
       }
   (3)删：
   
   public void delete(Context context){
           AsyncDbTool dbHelper = new AsyncDbTool(context);
           String selection = ValidDevTable.BEACON_ID+" =?"+" AND "+ValidDevTable.POLICY_ID+" = "+1;
           String[] selectionArgs = new String[]{"111111"};
           dbHelper.delete(uri, 3, new DbEventListener() {
               @Override
               public void AsyncUpdateComplete(int token, Object checkObj) {

               }

               @Override
               public void AsyncQuery(int token, Cursor cursor, Object checkObj) {

               }

               @Override
               public void AsyncDelete(int token, Object checkObj, int result) {
                   Log.d(TAG,"AsyncDelete token:"+token+"  result:"+result);
               }
           }, selection, selectionArgs);
       }
   (4)改：
   
    public void update(Context context){
           AsyncDbTool dbHelper = new AsyncDbTool(context);
           String selectionSql = ValidDevTable.BEACON_ID+" =?"+" AND "+ValidDevTable.POLICY_ID+" = "+2;
           String[] selectionArgs = new String[]{"111111"};
           ContentValues values = new ContentValues();
           values.put(ValidDevTable.BEACON_ID,"22222");
           values.put(ValidDevTable.DEV_TYPE,7);
           values.put(ValidDevTable.POLICY_ID,7);
           values.put(ValidDevTable.POLICY_VER,7);
           dbHelper.update(uri, 4, new DbEventListener() {
               @Override
               public void AsyncUpdateComplete(int token, Object checkObj) {
                   Log.d(TAG,"AsyncUpdateComplete :"+token);
               }

               @Override
               public void AsyncQuery(int token, Cursor cursor, Object checkObj) {

               }

               @Override
               public void AsyncDelete(int token, Object checkObj, int result) {

               }
           },values,selectionSql,selectionArgs);
       }
    (5)查：
    
          public void query(Context context){
        AsyncDbTool dbHelper = new AsyncDbTool(context);
        dbHelper.query(uri, 2, new DbEventListener() {
            @Override
            public void AsyncUpdateComplete(int token, Object checkObj) {

            }

            @Override
            public void AsyncQuery(int token, Cursor cursor, Object checkObj) {
                if(cursor!=null&& !cursor.isClosed()&& cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        String deviceID = cursor.getString(cursor.getColumnIndexOrThrow(ValidDevTable.BEACON_ID));
                        int policyId = cursor.getInt(cursor.getColumnIndexOrThrow(ValidDevTable.POLICY_ID));
                        Log.d(TAG, "deviceId :" + deviceID + " policyId :" + policyId+"  token :"+token);
                    }
                }
            }

            @Override
            public void AsyncDelete(int token, Object checkObj, int result) {

            }
        }, null, null, null, null);
    }
        }
4、树形列表：可实现树形列表展示。

    继承TreeListViewAdapter实现自定义的树形列表展示。通过注册Listener来达到节点点击长按事件的监听。可参考SimpleTreeAdapter
    
5、数字选择器Dialog控件：

    WheelNumberPickerDialog基于Builder模式实现的数字选择器。可做到时、分、秒选择
      new WheelNumberPickerDialog.Builder(this, new WheelNumberPickerDialog.INumberPickerDialogResults() {
                            @Override
                            public void onConfirm(int hourValue, int minValue, int secValue) {
                                Log.d("onConfirm", hourValue + ":" + minValue + ":" + secValue);
                                if (hourValue != 0 || minValue != 0 || secValue != 0) {
                                    int frequency_value = (hourValue * 3600 + minValue * 60 + secValue) * 1000;

                                }
                            }
                        }).setTitle(getResources().getString(R.string.set_gis_frequency))
                                .setHourValue(0)
                                .setMinuteValue(0)
                                .setSecondValue(0)
                                .setBackButton(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setSureButton(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
6、图片无损压缩:

(1)、引入tiny Module：

	<1>、项目最外层Build.gradle中需要配置
    	dependencies {
            classpath 'com.android.tools.build:gradle:2.2.3'
            classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7'
            classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'
        }
	<2>、gradle-wrapper.properties文件的配置：
  	 #Mon Nov 06 11:36:28 CST 2017
  	 distributionBase=GRADLE_USER_HOME
  	 distributionPath=wrapper/dists
  	 zipStoreBase=GRADLE_USER_HOME
  	 zipStorePath=wrapper/dists
  	 distributionUrl=https\://services.gradle.org/distributions/gradle-2.14.1-all.zip
         
(2)、使用：

	<1>初始化：
         projectApplication中初始化：     Tiny.getInstance().debug(true).init(this);
	
	<2>压缩单张图片文件：
	 
       Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
       		//默认情况下使用ARGB_8888。您也可以考虑使用RGB_565，它可以节省一半的内存大小。
             compressOptions.config = Bitmap.Config.ARGB_8888;
	     	//压缩文件的输出路径。
             compressOptions.outfile = "****/**/**";
	     
	     	//压缩质量，取值范围：0〜100 默认76
		
             compressOptions.quality = CompressKit.DEFAULT_QUALITY;
	    	 //硬盘上的最大内存大小，以KB为单位。如果值小于或等于零，{@ link Tiny}将被自动设置。
             compressOptions.size =0;
	     //是否需要覆盖源文件，仅限于文件（file，content：//，file：//）。
             compressOptions.overrideSource = false;
	     //是否保留样本大小的位图宽度和高度。
             compressOptions.isKeepSampling = false;
             Tiny.getInstance().source(file).asFile().withOptions(compressOptions).compress(new FileCallback() {
                 @Override
                 public void callback(boolean isSuccess, String outfile) {
                     //压缩结果
                 }
             });
(3)压缩多张文件：

         Tiny.BatchFileCompressOptions batchFileCompressOptions = new Tiny.BatchFileCompressOptions();
	 //默认情况下使用ARGB_8888。您也可以考虑使用RGB_565，它可以节省一半的内存大小。

          batchFileCompressOptions.config = Bitmap.Config.ARGB_8888;
	//压缩文件的输出路径。
           batchFileCompressOptions.outfiles = new String[]{"****/**/**1","\"****/**/**\"2"};
	//压缩质量，取值范围：0〜100 默认76
        
          batchFileCompressOptions.quality = CompressKit.DEFAULT_QUALITY;
          
	//硬盘上的最大内存大小，以KB为单位。如果值小于或等于零，{@ link Tiny}将被自动设置。
         batchFileCompressOptions.size =0;
	//是否需要覆盖源文件，仅限于文件（file，content：//，file：//）。
         batchFileCompressOptions.overrideSource = false;
	//是否保留样本大小的位图宽度和高度。
         batchFileCompressOptions.isKeepSampling = false;
         Tiny.getInstance().source(files).batchAsFile().withOptions(batchFileCompressOptions).batchCompress(new FileBatchCallback() {
                    @Override
                    public void callback(boolean isSuccess, String[] outfile) {

                    }
                });
            }
(4)单张bitmap压缩：

     Tiny.BitmapCompressOptions bitmapCompressOptions = new Tiny.BitmapCompressOptions();
      bitmapCompressOptions.width = 0;//压缩宽，如果值为零，则默认压缩最大宽度是屏幕宽度或{@link CompressKit＃DEFAULT_MAX_COMPRESS_SIZE}。
      bitmapCompressOptions.height = 0;//压缩高，如果值为零，则默认压缩最大高度是屏幕高度或{@link CompressKit＃DEFAULT_MAX_COMPRESS_SIZE}。
            bitmapCompressOptions.config=CompressKit.DEFAULT_CONFIG;
            Tiny.getInstance().source(bitmap).asBitmap().withOptions(bitmapCompressOptions).compress(new BitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap) {

                }
            });
	    
   (5)多张bitmap压缩：
   
      Tiny.BitmapCompressOptions bitmapCompressOptions = new Tiny.BitmapCompressOptions();
        bitmapCompressOptions.width = 0;//压缩宽，如果值为零，则默认压缩最大宽度是屏幕宽度或{@link CompressKit＃DEFAULT_MAX_COMPRESS_SIZE}。
        bitmapCompressOptions.height = 0;//压缩高，如果值为零，则默认压缩最大高度是屏幕高度或{@link CompressKit＃DEFAULT_MAX_COMPRESS_SIZE}。
       bitmapCompressOptions.config=CompressKit.DEFAULT_CONFIG;
       Tiny.getInstance().source(bitmaps).batchAsBitmap().withOptions(bitmapCompressOptions).batchCompress(new BitmapBatchCallback() {
                 @Override
                 public void callback(boolean isSuccess, Bitmap[] bitmaps) {

                 }
             });
7、蓝牙

   （1）经典蓝牙：
    
	<1>、扫描：
        
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
        
	<2>、监听匹配状态：
        
	public void registerPairListener(Context context, IPairCallback pairCallback){
	PairBroadcastReceiver mPairBroadcastReceiver = new PairBroadcastReceiver(pairCallback);
		//注册蓝牙配对监听器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		context.registerReceiver(mPairBroadcastReceiver, intentFilter);
	}
	
      /**
	* 取消配对监听
	* @param context
	*/
	public void unregisterPairListener(Context context){
	if(mPairBroadcastReceiver!=null){
		try {
			context.unregisterReceiver(mPairBroadcastReceiver);
		}catch (Exception e){

		}
	}
        
	<3>、连接
        
		模拟服务端：
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
        BluetoothHelper.HELPER.startServer(getApplicationContext(), BluetoothProfile.BLE_UUID);//模拟服务端，等待链接
		
	客户端：
	BluetoothHelper.HELPER.startClient(getApplicationContext(),BluetoothDevice,BluetoothProfile.BLE_UUID);//发起链接
		  
	<4>通信：
	
	BluetoothHelper.HELPER.sendFileToClient(PackageUtil.SendType.MediaShare, System.currentTimeMillis(), PackageUtil.MimeType.Jpg, filePath);//发送文件、文本消息到客户端
	BluetoothHelper.HELPER.sendFileToServer(PackageUtil.SendType.MediaShare, System.currentTimeMillis(), PackageUtil.MimeType.Jpg, filePath);//发送文件、文本消息到服务端
		 
		 
(2)、低功耗BLE：

	<1>、 开关启BLE扫描
	
        BLEScanHelper.HELPER.startScan(getApplicationContext(), new BLEScanner.IScanResultListener() {
            @Override
            public void onResultReceived(ScanData scanData) {
                
            }

            @Override
            public void onScanFailed(int errorCode) {

            }

            @Override
            public void onScanUpdate(ScanData scanData) {

            }
        });
        //关闭扫描
        BLEScanHelper.HELPER.stopScan();
        
	<2>、连接：
	
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
                        
	模拟信标和服务端：
		
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
			
	客户端：
	BLEHelper.HELPER.startConnect(getApplicationContext(),sanDevice.getBleAddress(),sanDevice.getDeviceName());//发起BLE链接
			  
	<3>通信：
        
	byte[] sendBytes = new byte[0];
        BLEHelper.HELPER.sendDataToClient(sendBytes);//发送数据到客户端
        BLEHelper.HELPER.sendDataToServer(sendBytes);//发送数据到服务端
			
    }
		
		

