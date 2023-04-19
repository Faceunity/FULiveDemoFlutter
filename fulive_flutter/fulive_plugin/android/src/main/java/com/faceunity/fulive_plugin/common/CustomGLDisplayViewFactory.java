package com.faceunity.fulive_plugin.common;

import android.content.Context;

import androidx.annotation.Nullable;

import com.faceunity.fulive_plugin.view.BaseGLView;
import com.faceunity.fulive_plugin.view.PhotoGlView;
import com.faceunity.fulive_plugin.view.VideoGlView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

/**
 * @author Qinyu on 2021-10-14
 * @description
 */
public class CustomGLDisplayViewFactory extends PlatformViewFactory {
    private final BinaryMessenger mBinaryMessenger;
    private String photoPath;
    private String videoPath;
    private PhotoGlView mPhotoGlView;
    private VideoGlView mVideoGlView;
    private BaseGLView.InfoCallback mInfoCallback;

    public CustomGLDisplayViewFactory(BinaryMessenger binaryMessenger) {
        super(StandardMessageCodec.INSTANCE);
        mBinaryMessenger = binaryMessenger;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        if (null != photoPath) {
            mPhotoGlView = new PhotoGlView(photoPath, context);
            mPhotoGlView.setInfoCallback(mInfoCallback);
            return mPhotoGlView;
        } else if (null != videoPath) {
            mVideoGlView = new VideoGlView(context, videoPath);
            mVideoGlView.setInfoCallback(mInfoCallback);
            return mVideoGlView;
        }
        return null;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        this.videoPath = null;
        mVideoGlView = null;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        this.photoPath = null;
        mPhotoGlView = null;
    }

    public BaseGLView getView() {
        if (mPhotoGlView != null) {
            return mPhotoGlView;
        } else if (mVideoGlView != null) {
            return mVideoGlView;
        } else {
            return null;
        }
    }

    public void setInfoCallback(BaseGLView.InfoCallback infoCallback) {
        if (mPhotoGlView != null) {
            mPhotoGlView.setInfoCallback(infoCallback);
        } else if (mVideoGlView != null) {
            mVideoGlView.setInfoCallback(infoCallback);
        }
        mInfoCallback = infoCallback;
    }
}
