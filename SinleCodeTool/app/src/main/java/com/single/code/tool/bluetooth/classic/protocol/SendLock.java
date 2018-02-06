package com.single.code.tool.bluetooth.classic.protocol;

/**
 * Created by Administrator on 2017/12/6.
 */
public class SendLock {
    private volatile static Object object;
    private volatile static boolean isLocked = false;
    public static boolean lock(){
        if(isLocked){
            return false;
        }else {
            if(object==null){
                synchronized (SendLock.class){
                    if(object == null){
                        object = new Object();
                        isLocked =true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isLocked() {
        return isLocked;
    }

    public static void release(){
        if(object!=null){
            synchronized (SendLock.class){
                if(object!=null){
                    object = null;
                }
            }
        }
        isLocked = false;
    }
}