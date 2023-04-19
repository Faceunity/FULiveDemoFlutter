package com.faceunity.fulive_plugin.utils;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.opengl.EGL14;
import android.opengl.Matrix;
import android.util.Log;

import com.faceunity.core.glview.GLTextureView;
import com.faceunity.core.media.video.OnVideoRecordingListener;
import com.faceunity.core.media.video.encoder.MediaAudioEncoder;
import com.faceunity.core.media.video.encoder.MediaAudioFileEncoder;
import com.faceunity.core.media.video.encoder.MediaEncoder;
import com.faceunity.core.media.video.encoder.MediaMuxerWrapper;
import com.faceunity.core.media.video.encoder.MediaVideoEncoder;
import com.faceunity.core.utils.DecimalUtils;
import com.faceunity.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author benyq
 * @date 2023/2/21
 * @email 1520063035@qq.com
 */

public class VideoRecordHelper {
    private static final boolean DEBUG = true;
    private static final String TAG = "Video_RecordHelper";

    private Context mContext;
    private OnVideoRecordingListener mOnVideoRecordingListener;
    public static final int MAX_VIDEO_LENGTH = 1280;

    public VideoRecordHelper(Context context, OnVideoRecordingListener listener) {
        mContext = context;
        mOnVideoRecordingListener = listener;
    }

    //region 录制相关

    private GLTextureView mGLSurfaceView;

    private MediaMuxerWrapper mMuxer;
    private MediaVideoEncoder mVideoEncoder;

    private int videoOrientation = 0;//文件朝向

    private final Object mRecordLock = new Object();//录制锁
    private volatile Long frameAvailableTime = 0L;//录制写入初始时间
    private File mOutputFile;//保存文件


    private volatile boolean isStopRecording = false;//释放需要暂停录制
    private volatile boolean isRecording = false;//是否正在录制


    private volatile CountDownLatch mCountDownLatch;

    //endregion


    /**
     * 开启录制
     *
     * @param glTextureView
     * @param width
     * @param height
     */
    public void startRecording(GLTextureView glTextureView, int width, int height) {
        if (isRecording) {
            Log.e(TAG, "startRecording failed ,VideoRecordHelper has  Recording now");
            return;
        }
        isRecording = true;
        if (DEBUG) Log.v(TAG, "startRecording:");
        mGLSurfaceView = glTextureView;
        isStopRecording = false;
        frameAvailableTime = 0L;
        try {
            mOutputFile = FileUtils.getCacheVideoFile(mContext);
            mMuxer = new MediaMuxerWrapper(mOutputFile.getAbsolutePath());
            mCountDownLatch = new CountDownLatch(2);
            if (DEBUG) Log.e(TAG, "startRecording  mCountDownLatch" + mCountDownLatch.getCount());
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, (width << 1) >> 1, (height << 1) >> 1);
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开启录制
     *
     * @param glTextureView
     * @param width
     * @param height
     */
    public void startRecording(GLTextureView glTextureView, int width, int height, String filePath) {
        if (isRecording) {
            Log.e(TAG, "startRecording failed ,VideoRecordHelper has  Recording now");
            return;
        }
        isRecording = true;
        if (DEBUG) Log.v(TAG, "startRecording:");
        mGLSurfaceView = glTextureView;
        isStopRecording = false;
        frameAvailableTime = 0L;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever.setDataSource(filePath);
                videoOrientation = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                mediaMetadataRetriever.release();
            }
            mOutputFile = FileUtils.getCacheVideoFile(mContext);
            mMuxer = new MediaMuxerWrapper(mOutputFile.getAbsolutePath());
            mCountDownLatch = new CountDownLatch(2);
            int videoWidth = (videoOrientation == 0 || videoOrientation == 180) ? width : height;
            int videoHeight = (videoOrientation == 0 || videoOrientation == 180) ? height : width;
            //检查宽高如果超过MAX_VIDEO_LENGTH 就压缩
            if (videoWidth > MAX_VIDEO_LENGTH || videoHeight > MAX_VIDEO_LENGTH) {
                float scaleWidth = (float) videoWidth / (float)MAX_VIDEO_LENGTH;
                float scaleHeight= (float)videoHeight / (float)MAX_VIDEO_LENGTH;
                double max = Math.max(scaleWidth,scaleHeight);
                int maxInt = (int) Math.ceil(max);
                videoWidth = (int) (videoWidth / maxInt);
                videoHeight = (int) (videoHeight / maxInt);
            }

            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, (videoWidth << 1) >> 1, (videoHeight << 1) >> 1);
            if (isHasAudio(filePath))
                new MediaAudioFileEncoder(mMuxer, mMediaEncoderListener,filePath);
            else
                mCountDownLatch = new CountDownLatch(1);
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束录制
     */
    public void stopRecording() {
        isStopRecording = true;
        if (mMuxer != null) {
            synchronized (mRecordLock) {
                mVideoEncoder = null;
            }
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }


    /**
     * 写入数据
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     */
    public void frameAvailableSoon(int texId, float[] texMatrix, float[] mvpMatrix) {
        synchronized (mRecordLock) {
            if (mVideoEncoder != null) {
                if (frameAvailableTime == 0L) {
                    frameAvailableTime = System.currentTimeMillis();
                }
                float[] matrix = DecimalUtils.copyArray(mvpMatrix);
                switch (videoOrientation) {
                    case 270:
                        Matrix.rotateM(matrix, 0, 90f, 0f, 0f, 1f);
                        break;
                    case 180:
                        Matrix.rotateM(matrix, 0, 180f, 0f, 0f, 1f);
                        break;
                    case 90:
                        Matrix.rotateM(matrix, 0, 270f, 0f, 0f, 1f);
                        break;
                }
                mVideoEncoder.frameAvailableSoon(texId, texMatrix, matrix);
                if (!isStopRecording) {
                    mOnVideoRecordingListener.onProcess(System.currentTimeMillis() - frameAvailableTime);
                }
            }
        }
    }


    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder) {
                mGLSurfaceView.queueEvent(() -> {
                    ((MediaVideoEncoder) encoder).setEglContext(EGL14.eglGetCurrentContext());
                    synchronized (mRecordLock) {
                        mVideoEncoder = (MediaVideoEncoder) encoder;
                    }
                    mOnVideoRecordingListener.onPrepared();
                });
            }

        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            mCountDownLatch.countDown();
            if (DEBUG) Log.e(TAG, "onStopped  mCountDownLatch" + mCountDownLatch.getCount());
            if (encoder instanceof MediaVideoEncoder) {
                mGLSurfaceView.queueEvent(((MediaVideoEncoder) encoder)::releaseGL);

            }
            if (mCountDownLatch.getCount() == 0) {
                mCountDownLatch = null;
                if (DEBUG) Log.v(TAG, "onStopped  mOutputFile:" + mOutputFile.getAbsolutePath());
                mOnVideoRecordingListener.onFinish(mOutputFile);
                isRecording = false;
            }
        }
    };

    /**
     * 判断一个视频中是否有音频
     * @param filePath
     * @return
     * @throws IOException
     */
    private boolean isHasAudio(String filePath) {
        boolean hasAudio = false;
        MediaExtractor mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackCount = mMediaExtractor.getTrackCount();

        for (int i = 0; i < trackCount; i++) {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                hasAudio = true;
                break;
            }
        }
        return hasAudio;
    }


}
