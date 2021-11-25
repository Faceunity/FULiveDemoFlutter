package com.faceunity.core.media.video;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.opengl.EGL14;
import android.opengl.Matrix;
import android.util.Log;

import com.faceunity.core.media.video.encoder.MediaAudioEncoder;
import com.faceunity.core.media.video.encoder.MediaEncoder;
import com.faceunity.core.media.video.encoder.MediaMuxerWrapper;
import com.faceunity.core.media.video.encoder.MediaVideoEncoder;
import com.faceunity.core.utils.DecimalUtils;
import com.faceunity.core.utils.FileUtils;
import com.faceunity.core.weight.GLTextureView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * DESC：
 * Created on 2021/4/12
 */
public class VideoRecordHelper {
    private static final boolean DEBUG = true;
    private static final String TAG = "Video_RecordHelper";

    private Context mContext;
    private OnVideoRecordingListener mOnVideoRecordingListener;
    public static final int MAX_VIDEO_LENGTH = 1920;

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
     * @param glSurfaceView
     * @param width
     * @param height
     */
    public void startRecording(GLTextureView glSurfaceView, int width, int height) {
        if (isRecording) {
            Log.e(TAG, "startRecording failed ,VideoRecordHelper has  Recording now");
            return;
        }
        isRecording = true;
        if (DEBUG) Log.v(TAG, "startRecording:");
        mGLSurfaceView = glSurfaceView;
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
     * @param glSurfaceView
     * @param width
     * @param height
     */
    public void startRecording(GLTextureView glSurfaceView, int width, int height, String filePath) {
        if (isRecording) {
            Log.e(TAG, "startRecording failed ,VideoRecordHelper has  Recording now");
            return;
        }
        isRecording = true;
        if (DEBUG) Log.v(TAG, "startRecording:");
        mGLSurfaceView = glSurfaceView;
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
            //检查宽高如果超过1920 就压缩
            int scale = 1;

            if (videoWidth > MAX_VIDEO_LENGTH || videoHeight > MAX_VIDEO_LENGTH) {
                while (videoWidth / scale >= MAX_VIDEO_LENGTH && videoHeight / scale >= MAX_VIDEO_LENGTH) {
                    scale++;
                }

                videoWidth = videoWidth / scale;
                videoHeight = videoHeight / scale;
            }

            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, (videoWidth << 1) >> 1, (videoHeight << 1) >> 1);
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
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


}
