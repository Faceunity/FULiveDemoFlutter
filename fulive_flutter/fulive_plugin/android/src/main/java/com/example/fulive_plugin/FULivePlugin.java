package com.example.fulive_plugin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.example.fulive_plugin.FUCommon.CustomGLDisplayViewFactory;
import com.example.fulive_plugin.FUCommon.GLDisplayViewFactory;
import com.example.fulive_plugin.entity.FuEvent;
import com.example.fulive_plugin.impl.FuBeautyKey;
import com.example.fulive_plugin.impl.FuMakeupKey;
import com.example.fulive_plugin.impl.FuPluginKey;
import com.example.fulive_plugin.impl.FuStickerKey;
import com.example.fulive_plugin.view.BaseGLView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FULivePlugin
 */
public class FULivePlugin implements FlutterPlugin, MethodCallHandler {
    private static final String TAG = "FULivePlugin";
    public static final int STATE_DISPLAY = 0, STATE_CUSTOM = 1;
    @IntDef({STATE_DISPLAY, STATE_CUSTOM})
    @interface State {}

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity

    public MethodChannel channel;
    private static Context appContext;
    public GLDisplayViewFactory mGLDisplayViewFactory;
    public CustomGLDisplayViewFactory mCustomGLDisplayViewFactory;
    private @State int state = STATE_DISPLAY;
    public EventChannel.EventSink mEventSink;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "fulive_plugin");
        channel.setMethodCallHandler(this);
        appContext = flutterPluginBinding.getApplicationContext();

        BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        mGLDisplayViewFactory = new GLDisplayViewFactory(messenger);
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("OpenGLDisplayView", mGLDisplayViewFactory);
        mCustomGLDisplayViewFactory = new CustomGLDisplayViewFactory(messenger);
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("CustomGLDisplayView", mCustomGLDisplayViewFactory);

        new EventChannel(flutterPluginBinding.getBinaryMessenger(), "FUEventChannel").setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                mEventSink = events;
            }

            @Override
            public void onCancel(Object arguments) {

            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getModuleCode")) {
            result.success(new int[]{0x7ebf7f7f, 0xfffff});
        } else if (call.arguments instanceof Map) {
            String method = (String) ((Map) call.arguments).get("method");
            Log.i(TAG, "onMethodCall: " + method);
            for (FuPluginKey key : FuPluginKey.values()) {
                if (method.equals(key.name())) {
                    key.handle(this, call, result);
                    return;
                }
            }
            for (FuMakeupKey key : FuMakeupKey.values()) {
                if (method.equals(key.name())) {
                    key.handle(this, call, result);
                    return;
                }
            }
            for (FuBeautyKey key : FuBeautyKey.values()) {
                if (method.equals(key.name())) {
                    key.handle(this, call, result);
                    return;
                }
            }
            for (FuStickerKey key : FuStickerKey.values()) {
                if (method.equals(key.name())) {
                    key.handle(this, call, result);
                    return;
                }
            }
            result.notImplemented();
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        EventBus.getDefault().unregister(this);
    }

    public void setState(@State int state) {
        this.state = state;
    }

    public BaseGLView getGlView() {
        if (state == STATE_DISPLAY) {
            return mGLDisplayViewFactory.getView();
        } else {
            return mCustomGLDisplayViewFactory.getView();
        }
    }

    public void setInfoCallback(BaseGLView.InfoCallback infoCallback) {
        if (state == STATE_DISPLAY) {
            mGLDisplayViewFactory.setInfoCallback(infoCallback);
        } else {
            mCustomGLDisplayViewFactory.setInfoCallback(infoCallback);
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(FuEvent event) {
        String path = (String) event.getData();
        switch (event.getCode()) {
            case FuEvent.choose_result_photo: {
                mCustomGLDisplayViewFactory.setPhotoPath(path);
                Map<String, Integer> map = new HashMap<>();
                map.put("type", 0);
                channel.invokeMethod("customSelectedImage", map);
                break;
            }
            case FuEvent.choose_result_video: {
                mCustomGLDisplayViewFactory.setVideoPath(path);
                Map<String, Integer> map = new HashMap<>();
                map.put("type", 1);
                channel.invokeMethod("customSelectedImage", map);
                break;
            }
            case FuEvent.start_video_play: {
                Map<String, Boolean> map = new HashMap<>();
                map.put("isPlay", true);
                channel.invokeMethod("videoPlay", map);
                break;
            }
            case FuEvent.stop_video_play: {
                Map<String, Boolean> map = new HashMap<>();
                map.put("isPlay", false);
                channel.invokeMethod("videoPlay", map);
                break;
            }
            case FuEvent.activity_pause: {
                if (mGLDisplayViewFactory.getView() != null && state == STATE_DISPLAY) {
                    mGLDisplayViewFactory.getView().onPause();
                }
                if (mCustomGLDisplayViewFactory.getView() != null) {
                    mCustomGLDisplayViewFactory.getView().onPause();
                }
                break;
            }
            case FuEvent.activity_resume: {
                if (mGLDisplayViewFactory.getView() != null && state == STATE_DISPLAY) {
                    mGLDisplayViewFactory.getView().onResume();
                }
                if (mCustomGLDisplayViewFactory.getView() != null) {
                    mCustomGLDisplayViewFactory.getView().onResume();
                }
                break;
            }
        }
    }
}
