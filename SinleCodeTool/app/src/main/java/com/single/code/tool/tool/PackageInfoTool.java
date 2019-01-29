package com.single.code.tool.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.security.MessageDigest;

/**
 * 获取应用签名
 * Created by czf on 2018/10/11.
 */

public class PackageInfoTool {
    private  static String TAG = "PackageInfoTool";
    /**
     * 开始获得签名
     * @param packageName 包名
     * @return
     */
    public static String getSignMD5(Context context,String packageName) {
        String signMd5 = "";
        Signature[] arrayOfSignature = getRawSignature(context, packageName);
        if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)){
            Log.d("","signs is null");
            return signMd5;
        }

       signMd5 = getMessageDigest(arrayOfSignature[0].toByteArray());
//        signMd5 = getRawDigest(arrayOfSignature[0].toByteArray());
        Log.d(TAG,"signMd5 :"+signMd5);
        return signMd5;
    }
    private static Signature[] getRawSignature(Context paramContext, String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            Log.d(TAG,"获取签名失败，包名为 null");
            return null;
        }
        PackageManager localPackageManager = paramContext.getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                Log.d(TAG,"信息为 null, 包名 = " + paramString);
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            Log.d(TAG,"包名没有找到...");
            return null;
        }
        return localPackageInfo.signatures;
    }

    private static final String getMessageDigest(byte[] signatureBytes)
    {
        String hexStr = binaryToHexString(signatureBytes);
        Log.d(TAG,"signHexStr :"+hexStr);
        char[] arrayOfChar1 = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(signatureBytes);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true)
            {
                if (j >= i)
                    return new String(arrayOfChar2);
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException){

        }
        return "";
    }
    public static String binaryToHexString(byte[] bytes){
        String hexStr =  "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for(int i=0;i<bytes.length;i++){
            //字节高4位
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
            //字节低4位
            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
            result +=hex;
        }
        return result;
    }
    private static final String getRawDigest(byte[] paramArrayOfByte)
    {
        try
        {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            return new String(arrayOfByte);
        } catch (Exception localException){

        }
        return null;
    }
}
