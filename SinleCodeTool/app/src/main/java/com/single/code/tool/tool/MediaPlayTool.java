package com.single.code.tool.tool;

import android.media.MediaPlayer;

/**
 * Created by chen.mingyao on 2017/4/27.
 */

public class MediaPlayTool {
    private static MediaPlayTool mMediaPlayTool;
    private MediaPlayer mMediaPlayer;


    public void setPlayOnCompleteListener(MediaPlayer.OnCompletionListener playOnCompleteListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(playOnCompleteListener);
        }
    }


    public static synchronized MediaPlayTool getInstance() {
        if (mMediaPlayTool == null) {
            mMediaPlayTool = new MediaPlayTool();
        }
        return mMediaPlayTool;
    }


    private MediaPlayTool() {
        mMediaPlayer = new MediaPlayer();
    }


    public void play(String soundFilePath) {
        if (mMediaPlayer == null) {
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(soundFilePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }


    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public void release() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }


    public int getDutation() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }


    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            try {
                return mMediaPlayer.isPlaying();
            } catch (IllegalStateException excption) {
                return false;
            }
        } else {
            return false;
        }
    }
}
