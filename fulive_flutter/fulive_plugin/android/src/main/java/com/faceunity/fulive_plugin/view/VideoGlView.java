package com.faceunity.fulive_plugin.view;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.faceunity.fulive_plugin.entity.FuEvent;
import com.faceunity.fulive_plugin.renderer.VideoGLTextureRenderer;
import com.faceunity.fulive_plugin.utils.FileUtils;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.listener.OnVideoPlayListener;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.fulive_plugin.utils.ToastHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * @author Qinyu on 2021-10-14
 * @description
 */
public class VideoGlView extends BaseGLView {
    private static final String TAG = "VideoGlView";

    private String videoPath;
    private VideoGLTextureRenderer mRenderer;
    private int outputW, outputH;
    private File recordFile;
    private volatile boolean videoPause = false;

    public VideoGlView(Context context, String videoPath) {
        super(context);
        this.videoPath = videoPath;
        createRenderer();
    }

    @Override
    void createRenderer() {
        mRenderer = new VideoGLTextureRenderer(mGLTextureView, videoPath, mOnGlRendererListener);
        autoPlay();
    }


    /*录制保存*/
    @Override
    protected void recordingData(FURenderOutputData outputData, float[] texMatrix, int currentFrame) {
        if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
            return;
        }
        outputW = outputData.getTexture().getWidth();
        outputH = outputData.getTexture().getHeight();
        if (isRecordingPrepared) {
            mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX);
        }
        if (currentFrame == 5) {
            startRecord();
        }
    }

    @Override
    protected void onRecordFinish(File file) {
        recordFile = file;
    }

    @Override
    void onStartRecord() {
        mVideoRecordHelper.startRecording(mGLTextureView, outputW, outputH, videoPath);
    }

    @Override
    public View getView() {
        return mGLTextureView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        Log.d(TAG, "onFlutterViewAttached: ");
        mRenderer.onResume();
        mRenderer.startMediaPlayer(mOnVideoPlayListener);
        EventBus.getDefault().post(new FuEvent(FuEvent.start_video_play));
    }

    @Override
    public void onFlutterViewDetached() {
        Log.d(TAG, "onFlutterViewDetached: ");
        mRenderer.onPause();
    }

    @Override
    public void dispose() {
        Log.d(TAG, "dispose: ");
        mRenderer.onPause();
        mRenderer.onDestroy();
        mRenderer = null;
        mGLTextureView = null;
    }

    public void autoPlay() {
        mRenderer.onResume();
        mRenderer.startMediaPlayer(mOnVideoPlayListener);
        EventBus.getDefault().post(new FuEvent(FuEvent.start_video_play));
    }

    /**
     * 视频回调
     */
    private final OnVideoPlayListener mOnVideoPlayListener = new OnVideoPlayListener() {
        @Override
        public void onError(String error) {
            Log.e(TAG, "onError: " + error);
        }

        @Override
        public void onPlayFinish() {
            stopRecord();
            EventBus.getDefault().post(new FuEvent(FuEvent.finish_video_play));
        }
    };

    /**
     * 对外
     */

    @Override
    public void takePic() {
        String filePath = FileUtils.addVideoToAlbum(mContext, recordFile);
        if (filePath == null || filePath.trim().length() == 0) {
            mGLTextureView.post(() -> ToastHelper.showNormalToast(mContext, "保存视频失败！"));
        } else {
            mGLTextureView.post(() -> ToastHelper.showNormalToast(mContext, "视频已保存到相册！"));
        }
        if (recordFile.exists()) {
            recordFile.delete();
        }
    }

    public void replay() {
        if (mRenderer == null) return;
        mRenderer.startMediaPlayer(mOnVideoPlayListener);
        startRecord();
        EventBus.getDefault().post(new FuEvent(FuEvent.start_video_play));
    }

    @Override
    public void onResume() {
        if (mRenderer != null) {
            Log.d(TAG, "onResume: ");
            mRenderer.onResume();
        }
        if (videoPause) {
            videoPause = false;
            replay();
        }
    }

    @Override
    public void onPause() {
        stopRecord();
        videoPause = true;
        if (mRenderer != null) {
            Log.d(TAG, "onPause: ");
            mRenderer.onPause();
            EventBus.getDefault().post(new FuEvent(FuEvent.stop_video_play));
        }
    }

    @Override
    public void rendererSwitch(boolean enable) {
        mRenderer.setFURenderSwitch(enable);
    }
}
