package com.example.fulive_plugin.GL;

import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class FuLivePluginGlSurfaceViewFactory extends PlatformViewFactory {
    private final BinaryMessenger mBinaryMessenger;
    private FULivePluginGlSurfaceView mFULivePluginGlSurfaceView;

    public FuLivePluginGlSurfaceViewFactory(BinaryMessenger binaryMessenger) {
        super(StandardMessageCodec.INSTANCE);
        mBinaryMessenger = binaryMessenger;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        mFULivePluginGlSurfaceView = new FULivePluginGlSurfaceView(context);
        return new FULivePluginGlSurfaceView(context);
    }
}
