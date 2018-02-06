package com.single.code.tool.logger;

import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author yao.guoju
 */
public class Logger {

    private static LoggerSettings settings = null;
    private static final String TAG = "Logger";

    public static void initialize(LoggerSettings s) {
        settings = s;
    }

    public static void v(String tag,Object msg, boolean dump) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.VERBOSE.ordinal()) {
            String info = getMethodName() + msg;
            Log.v(tag, getMethodName() + msg);
            if (dump && settings.getSaveLogEnable()) {
                try {
                    XpFile.dump(settings.getLogFile(), getSystemTimeTStyle() + ":" +tag+"=>"+ info + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getSystemTimeTStyle() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // ��ȡ��ǰ���  
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// ��ȡ��ǰ�·�  
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// ��ȡ��ǰ�·ݵ����ں���  
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//ʱ  
        String mMinute = String.valueOf(c.get(Calendar.MINUTE));//��  
        String mSecond = String.valueOf(c.get(Calendar.SECOND));//��  

        return mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":" + mSecond;
    }

    public static void d(String tag,Object msg, boolean dump) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.DEBUG.ordinal()) {
            String info = getMethodName() + msg;
            Log.d(tag, info);
            if (dump && settings.getSaveLogEnable()) {
                try {
                    XpFile.dump(settings.getLogFile(), getSystemTimeTStyle() + ":" +tag+"=>"+ info + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void i(String tag,Object msg, boolean dump) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.INFO.ordinal()) {
            String info = getMethodName() + msg;
            Log.i(tag, info);
            if (dump && settings.getSaveLogEnable()) {
                try {
                    XpFile.dump(settings.getLogFile(), getSystemTimeTStyle() + ":"+tag+"=>" + info + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void e(String tag,Object msg, boolean dump) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.ERROR.ordinal()) {
            String info = getMethodName() + msg;
            Log.e(tag, info);
            if (dump && settings.getSaveLogEnable()) {
                try {
                    XpFile.dump(settings.getLogFile(), getSystemTimeTStyle() + ":" +tag+"=>"+ info + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void w(String tag,Object msg, boolean dump) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.WARNING.ordinal()) {
            String info = getMethodName() + msg;
            Log.w(tag, getMethodName() + msg);
            if (dump && settings.getSaveLogEnable()) {
                try {
                    XpFile.dump(settings.getLogFile(), getSystemTimeTStyle() + ":" +tag+"=>"+ info + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    public static void d(String tag,Object msg) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.DEBUG.ordinal()) {
            String info = getMethodName() + msg;
            Log.d(tag, info);
        }
    }

    public static void i(String tag,Object msg) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.INFO.ordinal()) {
            String info = getMethodName() + msg;
            Log.i(tag, info);
        }
    }

    public static void e(String tag,Object msg) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.ERROR.ordinal()) {
            String info = getMethodName() + msg;
            Log.e(tag, info);
        }
    }

    public static void w(String tag,Object msg) {
        if (settings == null) {
            new Exception("need init logger setting").printStackTrace();
            return;
        }
        if (settings.getLevel().ordinal() <= LoggerSettings.Level.WARNING.ordinal()) {
            String info = getMethodName() + msg;
            Log.w(tag, getMethodName() + msg);
        }
    }


    /**
     * ��ȡ�������÷���
     *
     * @return
     */
    private static String getMethodName() {
        String thread = Thread.currentThread().getName();
        return "[Thread:" + thread +"]";
    }

    /**
     * ��ȡ����
     *
     * @return
     */
//    private static String getClassName() {
//        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
//        StackTraceElement e = stacktrace[4];
//        String result = e.getClassName();
//        int lastIndex = result.lastIndexOf(".");
//        result = result.substring(lastIndex + 1, result.length());
//        return result;
//    }


}
