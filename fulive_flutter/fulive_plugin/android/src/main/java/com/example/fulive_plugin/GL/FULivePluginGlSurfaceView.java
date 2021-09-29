package com.example.fulive_plugin.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.faceunity.core.entity.FUCameraConfig;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.renderer.CameraRenderer;

import io.flutter.plugin.platform.PlatformView;

public class FULivePluginGlSurfaceView implements PlatformView {

    private GLSurfaceView mGLSurfaceView;
    private CameraRenderer mCameraRenderer;
    private FURenderKit mFURenderKit;
    private FUAIKit mFUAIKit;
    private Context mContext;

    public FULivePluginGlSurfaceView(Context context) {
        mContext = context;
    }

    @Override
    public View getView() {
        mGLSurfaceView = new GLSurfaceView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mGLSurfaceView.setLayoutParams(layoutParams);
        //相机配置
        FUCameraConfig cameraConfig = new FUCameraConfig();
        mCameraRenderer = new CameraRenderer(mGLSurfaceView, cameraConfig, mOnGlRendererListener);
        mCameraRenderer.onResume();
        mFURenderKit = FURenderKit.getInstance();
        mFUAIKit = FUAIKit.getInstance();
        return mGLSurfaceView;
    }

    @Override
    public void dispose() {
        mCameraRenderer.onPause();
        mCameraRenderer = null;
        mGLSurfaceView = null;
        mFURenderKit = null;
        mFUAIKit = null;
    }

    /* CameraRenderer 回调*/
    private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {

        @Override
        public void onSurfaceCreated() {
//            configureFURenderKit();
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
        }

        @Override
        public void onRenderBefore(FURenderInputData inputData) {
        }


        @Override
        public void onRenderAfter(@NonNull FURenderOutputData outputData, FURenderFrameData frameData) {
        }

        @Override
        public void onDrawFrameAfter() {
        }


        @Override
        public void onSurfaceDestroy() {
            FURenderKit.getInstance().release();
        }
    };

//    /**
//     * 特效配置
//     */
//    protected void configureFURenderKit() {
//        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
//    }
}
