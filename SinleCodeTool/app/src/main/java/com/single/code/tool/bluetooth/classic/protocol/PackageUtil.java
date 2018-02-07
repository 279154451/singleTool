package com.single.code.tool.bluetooth.classic.protocol;

import android.os.Environment;

/**
 * MimeType 和Cmdid常量
 * Created by yxl on 2017/4/18.
 */

public class PackageUtil {
    public static String sendFileDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/QchatRemote/qchatSend";
    /**
     * cmdid   0x00:media share 0x01:upload
     * mimetyte 0x01:jpg 0x02:png 0x03:mp3 0x04:mp4
     * length   0x01010102
     * content:
     * @param
     */
    public class MimeType{
        public static final byte Jpg = 0x01;
        public static final byte Png = 0x02;
        public static final byte Mp3 = 0x03;
        public static final byte Mp4 = 0x04;
    }
    public class SendType{
        public static final byte MediaShare = 0x00;//媒体共享
        public static final byte upload = 0x01;//现场上报
        public static final byte Message = 0x02;//消息
        public static final byte Response = 0x03;//response
        public static final byte ParseFinish = 0x04;//配对状态
    }

    public class ResponseCode{
        public static final byte SUCCESS = 0x00;
        public static final byte ERROR = 0x01;
    }
}
