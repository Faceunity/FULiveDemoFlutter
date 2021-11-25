package com.example.fulive_plugin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fulive_plugin.utils.FileUtils;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.media.photo.OnPhotoRecordingListener;
import com.faceunity.core.media.photo.PhotoRecordHelper;
import com.faceunity.core.media.video.OnVideoRecordingListener;
import com.faceunity.core.media.video.VideoRecordHelper;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.core.weight.GLTextureView;

import java.io.File;

import io.flutter.plugin.platform.PlatformView;

/**
 * @author Qinyu on 2021-10-14
 * @description
 */
public abstract class BaseGLView implements PlatformView {
    protected GLTextureView mGLTextureView;
    protected Context mContext;
    protected static volatile Runnable mGLRunnable; //用完必须回收

    public BaseGLView(Context context) {
        Log.d("BaseGLView", this.getClass().getSimpleName());
        mContext = context;
        mGLTextureView = new GLTextureView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mGLTextureView.setLayoutParams(layoutParams);

        mVideoRecordHelper = new VideoRecordHelper(context, mOnVideoRecordingListener);
        mPhotoRecordHelper = new PhotoRecordHelper(mOnPhotoRecordingListener);
    }

    abstract void createRenderer();

    /* CameraRenderer 回调*/
    protected final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {
        private int currentFrame;
        private double renderTime, fps;
        private int trackCount;
        private long lastRenderTime;

        @Override
        public void onSurfaceCreated() {
            if (mGLRunnable != null) {
                mGLRunnable.run();
            }
        }

        @Override
        public void onSurfaceChanged(int width, int height) {}

        @Override
        public void onRenderBefore(FURenderInputData inputData) {}


        @Override
        public void onRenderAfter(@NonNull FURenderOutputData outputData, FURenderFrameData frameData) {
            recordingData(outputData, frameData.getTexMatrix(), currentFrame);
            currentFrame++;
            if (mInfoCallback != null) {
                trackCount = getTrackCount();
                calculateFps();
                mGLTextureView.post(() -> mInfoCallback.onRenderAfter(outputData.getTexture().getWidth(), outputData.getTexture().getHeight()
                        , fps, renderTime, trackCount > 0)); //call on UI thread
            }
        }

        @Override
        public void onDrawFrameAfter() {}


        @Override
        public void onSurfaceDestroy() {
            FURenderKit.getInstance().release();
        }

        private void calculateFps() {
            long curTime = System.currentTimeMillis();
            if (currentFrame % 10 == 0) {
                renderTime = (curTime - lastRenderTime) / 10d;
                fps = 1000d / renderTime;
                lastRenderTime = curTime;
            }
        }
    };

    /*录制保存*/
    protected void recordingData(FURenderOutputData outputData, float[] texMatrix, int currentFrame) {
        if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
            return;
        }
        if (isRecordingPrepared) {
            mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX);
        }
        if (isTakePhoto) {
            isTakePhoto = false;
            mPhotoRecordHelper.sendRecordingData(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, outputData.getTexture().getWidth(), outputData.getTexture().getHeight());
        }
    }

    protected int getTrackCount() {
        return FUAIKit.getInstance().isTracking();
    }

    public void stopRecord() {
        if (isRecording) {
            isRecording = false;
            onStopRecord();
        }
    }

    public void startRecord() {
        if (!isRecording) {
            isRecording = true;
            onStartRecord();
        }
    }

    public void takePic() {
        isTakePhoto = true;
    }

    //region 视频录制

    protected VideoRecordHelper mVideoRecordHelper;
    protected volatile boolean isRecordingPrepared = false;
    private boolean isRecording = false;
    private volatile long recordTime = 0;

    abstract void onStartRecord();

    protected void onStopRecord() {
        mVideoRecordHelper.stopRecording();
    }

    private OnVideoRecordingListener mOnVideoRecordingListener = new OnVideoRecordingListener() {
        @Override
        public void onPrepared() {
            isRecordingPrepared = true;
        }

        @Override
        public void onProcess(Long time) {
            recordTime = time;
        }

        @Override
        public void onFinish(File file) {
            isRecordingPrepared = false;
            onRecordFinish(file);
        }
    };

    protected void onRecordFinish(File file) {
        if (recordTime < 1100) {
            mGLTextureView.post(() -> Toast.makeText(mContext, "视频太短啦！", Toast.LENGTH_SHORT).show());
        } else {
            String filePath = FileUtils.addVideoToAlbum(mContext, file);
            if (filePath == null || filePath.trim().length() == 0) {
                mGLTextureView.post(() -> Toast.makeText(mContext, "保存视频失败！", Toast.LENGTH_SHORT).show());
            } else {
                mGLTextureView.post(() -> Toast.makeText(mContext, "保存视频成功！", Toast.LENGTH_SHORT).show());
            }
        }
        if (file.exists()) {
            file.delete();
        }
    }

    //endregion 视频录制
    //region 拍照

    private PhotoRecordHelper mPhotoRecordHelper;
    private volatile Boolean isTakePhoto = false;

    /**
     * 获取拍摄的照片
     */
    private final OnPhotoRecordingListener mOnPhotoRecordingListener =new OnPhotoRecordingListener() {
        @Override
        public void onRecordSuccess(Bitmap bitmap) {
            new Thread(() -> {
                String path = FileUtils.addBitmapToAlbum(mContext, bitmap);
                if (path == null) return;
                mGLTextureView.post(() -> Toast.makeText(mContext, "保存照片成功！", Toast.LENGTH_SHORT).show());
            }).start();
        }
    };

    //endregion 拍照

    public interface InfoCallback {
        void onRenderAfter(int width, int height, double fps, double renderTime, boolean hasFace);
    }
    protected InfoCallback mInfoCallback;

    public void setInfoCallback(InfoCallback infoCallback) {
        mInfoCallback = infoCallback;
    }

    public abstract void onResume();

    public abstract void onPause();

    public abstract void rendererSwitch(boolean enable);

    public static void runOnGLThread(Runnable GLRunnable) {
        mGLRunnable = GLRunnable;
    }

}
