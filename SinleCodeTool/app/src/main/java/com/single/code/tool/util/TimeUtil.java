package com.single.code.tool.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间
 * Created by Administrator on 2017/11/6.
 */
public class TimeUtil {
    public static String getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static String getSystemTimeTStyle() {
        SimpleDateFormat Formater = new SimpleDateFormat("yyyyMMdd\'T\'HHmmss");
        Calendar calendar = Calendar.getInstance();
        long unixTime = calendar.getTimeInMillis();
        long unixTimeGMT = unixTime - (long) TimeZone.getDefault().getRawOffset();
        Date curDate = new Date(unixTimeGMT);
        String str = Formater.format(curDate);
        return str;
    }
    /**
     * 时间长度转成00:00:00形式
     */
    public static String msec2Minute(int l) {
        long day = l / (24 * 60 * 60 * 1000);
        long hour = l / (60 * 60 * 1000 - day * 24);
        long min = (l / (60 * 1000)) - day * 24 * 60 - hour * 60;
        long sec = l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;

        hour = hour + day * 24;

        String secondStr = String.valueOf(sec);
        String minuteStr = String.valueOf(min);
        String hourStr = String.valueOf(hour);


        if (secondStr.length() < 2) {
            secondStr = "0" + secondStr;
        }

        if (minuteStr.length() < 2) {
            minuteStr = "0" + minuteStr;
        }

        if (hourStr.length() < 2) {
            hourStr = "0" + hourStr;
        }

        return hourStr + ":" + minuteStr + ":" + secondStr;
    }
}