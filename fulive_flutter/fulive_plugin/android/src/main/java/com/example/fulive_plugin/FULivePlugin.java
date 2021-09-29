package com.example.fulive_plugin;

import androidx.annotation.NonNull;

import com.example.fulive_plugin.GL.FuLivePluginGlSurfaceViewFactory;
import com.faceunity.core.faceunity.FURenderKit;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FULivePlugin
 */
public class FULivePlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private FuLivePluginGlSurfaceViewFactory mFuLivePluginGlSurfaceViewFactory;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "fulive_plugin");
        channel.setMethodCallHandler(this);

        BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        mFuLivePluginGlSurfaceViewFactory = new FuLivePluginGlSurfaceViewFactory(messenger);
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("fu_plugin/GLSurfaceView", mFuLivePluginGlSurfaceViewFactory);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getModuleCode")) {
            int moduleCode0 = FURenderKit.getInstance().getModuleCode(0);
            int moduleCode1 = FURenderKit.getInstance().getModuleCode(1);
            result.success(new int[]{moduleCode0, moduleCode1});
        } else if (call.method.equals("changeCameraFront")) {

            result.success(true);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
