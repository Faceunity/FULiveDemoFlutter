package com.faceunity.fulive_plugin.view;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.faceunity.core.entity.FUCameraConfig;
import com.faceunity.core.renderer.texture.CameraGLTextureRenderer;

/**
 * @author Qinyu on 2021-10-14
 * @description
 */
public class CameraGlView extends BaseGLView {
    private static final String TAG = "CameraGlView";
    private CameraGLTextureRenderer mCameraRenderer;

    public CameraGlView(Context context) {
        super(context);
        createRenderer();
    }

    @Override
    void createRenderer() {
        mCameraRenderer = new CameraGLTextureRenderer(mGLTextureView, new FUCameraConfig(), mOnGlRendererListener);
    }

    @Override
    public View getView() {
        Log.d(TAG, "getView: ");
        return mGLTextureView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        Log.d(TAG, "onFlutterViewAttached: ");
        mCameraRenderer.onResume();
    }

    @Override
    public void onFlutterViewDetached() {
        Log.d(TAG, "onFlutterViewDetached: ");
        mCameraRenderer.onPause();
    }

    @Override
    public void dispose() {
        Log.d(TAG, "dispose: ");
        mCameraRenderer.onPause();
        mCameraRenderer.onDestroy();
        mCameraRenderer = null;
        mGLTextureView = null;
    }

    @Override
    void onStartRecord() {
        mVideoRecordHelper.startRecording(mGLTextureView, mCameraRenderer.getFUCamera().getCameraHeight(), mCameraRenderer.getFUCamera().getCameraWidth());
    }

    /**
     * 手动resume
     */
    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        if (mCameraRenderer != null) {
            mCameraRenderer.onResume();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        if (mCameraRenderer != null) {
            mCameraRenderer.onPause();
        }
    }

    @Override
    public void rendererSwitch(boolean enable) {
        mCameraRenderer.setFURenderSwitch(enable);
    }
}
