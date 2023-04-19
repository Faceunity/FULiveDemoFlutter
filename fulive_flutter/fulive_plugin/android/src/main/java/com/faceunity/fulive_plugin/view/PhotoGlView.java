package com.faceunity.fulive_plugin.view;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.fulive_plugin.renderer.PhotoGLTextureRenderer;

/**
 * @author Qinyu on 2021-10-14
 * @description
 */
public class PhotoGlView extends BaseGLView {
    private static final String TAG = "PhotoGlView";
    private String photoPath;
    private PhotoGLTextureRenderer mRenderer;

    public PhotoGlView(String photoPath, Context context) {
        super(context);
        this.photoPath = photoPath;
        createRenderer();
    }

    @Override
    void createRenderer() {
        mRenderer = new PhotoGLTextureRenderer(mGLTextureView, photoPath, mOnGlRendererListener);
    }

    @Override
    public View getView() {
        Log.d(TAG, "getView: ");
        return mGLTextureView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        Log.d(TAG, "onFlutterViewAttached: ");
        mRenderer.onResume();
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

    @Override
    void onStartRecord() {
    }

    @Override
    public void onPause() {
        if (mRenderer != null) {
            mRenderer.onPause();
        }
    }

    @Override
    public void onResume() {
        if (mRenderer != null) {
            mRenderer.onResume();
        }
    }

    @Override
    public void rendererSwitch(boolean enable) {
        mRenderer.setFURenderSwitch(enable);
    }
}
