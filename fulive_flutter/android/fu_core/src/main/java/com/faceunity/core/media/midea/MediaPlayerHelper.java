package com.faceunity.core.media.midea;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * DESC：MP3文件播放
 * Created on 2021/3/28
 */
public class MediaPlayerHelper {


    public interface MediaPlayerListener {

        /**
         * 开始播放
         */
        void onStart();

        /**
         * 暂停播放
         */
        void onPause();

        /**
         * 停止播放
         */
        void onStop();

        /**
         * 结束播放
         */
        void onCompletion();
    }


    private Context mContext;
    private MediaPlayerListener mMediaPlayerListener;

    private MediaPlayer mMediaPlayer;
    private Handler mPlayerHandler;


    public MediaPlayerHelper(Context context, MediaPlayerListener listener) {
        mContext = context;
        mMediaPlayerListener = listener;
        startPlayerThread();
    }

    private boolean isPreparedMusic = false;

    /**
     * 循环播放
     *
     * @param path
     */
    public void playMusic(String path, boolean isLoop) {
        mPlayerHandler.post(() -> {
            isPreparedMusic = true;
            int fileType = checkFileType(path);
            if (fileType == 0) return;
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                });
                mMediaPlayer.setOnPreparedListener((mp) -> {
                    isPreparedMusic = false;
                    mMediaPlayer.start();
                    mMediaPlayerListener.onStart();
                });
                mMediaPlayer.setOnCompletionListener((mp) -> {
                    if (isPreparedMusic) {
                        mMediaPlayerListener.onCompletion();
                        return;
                    }
                    if (isLoop) {
                        mMediaPlayer.seekTo(0);
                        mMediaPlayer.start();
                    } else {
                        mMediaPlayerListener.onCompletion();
                    }
                });
            } else {
                mMediaPlayer.stop();
                mMediaPlayer.seekTo(0);
            }
            try {
                if (fileType == 1) {
                    AssetFileDescriptor descriptor = mContext.getAssets().openFd(path);
                    mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();
                } else {
                    mMediaPlayer.setDataSource(path);

                }
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 暂停播放
     */
    public void pausePlay() {
        mPlayerHandler.post(() -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mMediaPlayerListener.onPause();
            }
        });
    }


    /**
     * 继续播放
     */
    public void replayMusic() {
        mPlayerHandler.post(() -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mMediaPlayerListener.onStart();
            }
        });
    }


    /**
     * 停止播放
     */
    public void stopPlay() {
        mPlayerHandler.post(() -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayerListener.onStop();
            }
        });
    }

    /**
     * 释放
     */
    public void release() {
        mContext = null;
        mMediaPlayerListener = null;
        mPlayerHandler.removeCallbacksAndMessages(null);
        mPlayerHandler.post(() -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;

            }
        });
        stopPlayerThread();
    }

    /**
     * 获取音乐当前Position
     *
     * @return
     */
    public int getMusicCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 校验音乐文件类型
     *
     * @return Int 1:Assets目录  2：内存目录
     */
    private int checkFileType(String musicPath) {
        int fileType = 0;
        InputStream inputStream;
        try {
            inputStream = mContext.getAssets().open(musicPath);
            fileType = 1;
            inputStream.close();
        } catch (IOException e1) {
            try {
                inputStream = new FileInputStream(musicPath);
                fileType = 2;
                inputStream.close();
            } catch (IOException e2) {

            }
        }
        return fileType;
    }


    private void startPlayerThread() {
        HandlerThread playerThread = new HandlerThread("music_filter");
        playerThread.start();
        mPlayerHandler = new Handler(playerThread.getLooper());
    }

    private void stopPlayerThread() {
        mPlayerHandler.getLooper().quitSafely();
        mPlayerHandler = null;
    }

}
