package com.single.code.tool.tool;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.single.code.tool.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chen.mingyao on 2017/4/24.
 */

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    private long beginTime;
    //    private boolean isRecord = false;
    //    //private int maxDuration = 60 * 1000;//最长录制时间
    //    private int maxSize = 25 * 1024 * 1024;//最大录音大小限制为25M
    //    private static MediaRecorder mMediaRecorder;

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private String AUDIO_AMR_FILENAME;
    private int BufferElements2Rec = 320; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format
    private String mediaDir = Environment.getExternalStorageDirectory() + "/pttRecord/";
    private String mediaDirAMR = Environment.getExternalStorageDirectory() + "/pttRecordAMR/";
    private boolean isShort = false; // 是否录制时间太短

    public static AudioRecorder instance = new AudioRecorder();

    public static final int AMR = 1;
    public static final int PCM = 2;
    public static boolean releaseRecorder = false;
    public void AudioRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (callback != null) {
                if (isShort) {
                    File filePCM = new File(getFilePath(PCM));
                    File fileAMR = new File(getFilePath(AMR));
                    if (filePCM != null && filePCM.exists())
                        filePCM.delete();
                    if (fileAMR != null && fileAMR.exists())
                        fileAMR.delete();

                    callback.onShort();
                } else {
                    callback.onStop(getFilePath(AMR), getFilePath(PCM));
                }
            }
        }
    };

    public void startRecording() {
        releaseRecorder = false;
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferSize);

        recorder.startRecording();
        isRecording = true;

        // 回调通知调用者录音开始
        beginTime = System.currentTimeMillis();
        Log.d(TAG, "AudioRecorder==> startRecord time " + beginTime);
        if (callback != null) {
            callback.onStart();
        }

        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = getFilePath(PCM);
        short sData[] = new short[BufferElements2Rec];
        // byte sData[] = new byte[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // AirtalkeeMediaAudioControl.getInstance().AudioRecorderDataPut(1, sData);
                // writes the data to file from buffer
                // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(releaseRecorder){//判断是否是放弃录制
            Log.d(TAG,"releaseRecorder");
            File armFile = new File(getFilePath(AMR));
            if (armFile != null && armFile.exists())
                armFile.delete();
            File pcmFile = new File(getFilePath(PCM));
            if(pcmFile!=null&&pcmFile.exists()){
                pcmFile.delete();
            }
        }else {
            Log.d(TAG,"stop or short");
            FileUtil.pcm2Amr(getFilePath(PCM), getFilePath(AMR));
            handler.sendMessage(new Message());
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        releaseRecorder = false;
        long delay = System.currentTimeMillis() - beginTime;
        Log.d(TAG, "AudioRecorder==> delay  " + delay);
        isShort = (delay > 1000) ? false : true;
        // stops the recording
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    /**
     * 放弃当前录制
     */
    public void releaseRecording(){
        releaseRecorder = true;
        if(isRecording){
            if(recorder!=null){
                isRecording = false;
                recorder.stop();
                recorder.release();
                recorder = null;
                recordingThread =null;
            }

        }
    }

    public interface Callback {
        void onStop(String amrPath, String pcmPath);

        void onShort();

        void onStart();
    }

    private Callback callback;

    public void setRecordCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 获取音频文件地址
     *
     * @param type 音频格式
     * @return
     */
    public String getFilePath(int type) {
        String path = "";
        File pttDir = new File(mediaDir);
        if (!pttDir.exists()) {
            pttDir.mkdirs();
        }
        switch (type) {
            case AMR:
                path = mediaDir + AUDIO_AMR_FILENAME + ".amr"; // 获取AMR格式音频文件地址
                break;
            case PCM:
                path = mediaDir + AUDIO_AMR_FILENAME + ".pcm"; // 获取PCM格式音频文件地址
                break;

        }
        return path;
    }

    public void changeAMRFileName(String name) {
        if (!isRecording) {
            AUDIO_AMR_FILENAME = name;
        }
    }
}
