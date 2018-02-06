package com.single.code.tool.db;

/**
 * Created by Administrator on 2017/12/1.
 */
public class DbLock {
    private volatile static Object object;
    private volatile static boolean isLocked = false;
    public static boolean lock(){
        if(isLocked){
            return false;
        }else {
            if(object==null){
                synchronized (DbLock.class){
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
            synchronized (DbLock.class){
                if(object!=null){
                    object = null;
                }
            }
        }
        isLocked = false;
    }
}