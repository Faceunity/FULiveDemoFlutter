package com.example.fulive_plugin.FUCommon;

import android.content.Context;

import com.example.fulive_plugin.view.BaseGLView;
import com.example.fulive_plugin.view.CameraGlView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class GLDisplayViewFactory extends PlatformViewFactory {
    private final BinaryMessenger mBinaryMessenger;
    private CameraGlView mCameraGlView;
    private BaseGLView.InfoCallback mInfoCallback;

    public GLDisplayViewFactory(BinaryMessenger binaryMessenger) {
        super(StandardMessageCodec.INSTANCE);
        mBinaryMessenger = binaryMessenger;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        mCameraGlView = new CameraGlView(context);
        mCameraGlView.setInfoCallback(mInfoCallback);
        return mCameraGlView;
    }

    public CameraGlView getView() {
        return mCameraGlView;
    }

    public void setInfoCallback(BaseGLView.InfoCallback infoCallback) {
        if (mCameraGlView != null) {
            mCameraGlView.setInfoCallback(infoCallback);
        }
        mInfoCallback = infoCallback;
    }
}
