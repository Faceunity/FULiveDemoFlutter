package com.example.fulive_plugin.impl;

import com.example.fulive_plugin.FULivePlugin;
import com.example.fulive_plugin.entity.FuEvent;
import com.example.fulive_plugin.view.VideoGlView;
import com.faceunity.core.camera.FUCamera;
import com.faceunity.core.faceunity.FURenderKit;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author Qinyu on 2021-10-09
 * @description flutter回调keys
 */
public enum FuPluginKey {
    chooseImageOrVideo {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            Map arguments = (Map) call.arguments;
            int value = (int) arguments.get("value");
            plugin.mGLDisplayViewFactory.getView().onFlutterViewDetached();
            switch (value) {
                case 0:
                    EventBus.getDefault().post(new FuEvent(FuEvent.choose_photo));
                    break;
                case 1:
                    EventBus.getDefault().post(new FuEvent(FuEvent.choose_video));
                    break;
            }
        }
    },
    manualExpose {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    takePhoto {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.getGlView().takePic();
        }
    },
    startRecord {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.getGlView().startRecord();
        }
    },
    stopRecord {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.getGlView().stopRecord();
        }
    },
    disposeCommon {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            FURenderKit.getInstance().release();
        }
    },
    startBeautyStreamListen {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.setInfoCallback((width, height, fps, renderTime, hasFace) -> {
                String debug = "Resolution:\n\t" + width + "X" + height + "\nFPS: " + (int)fps + "\nRender time:\n\t" + (int)renderTime + "ms";
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("debug", debug);
                    jsonObject.put("hasFace", hasFace);
                    if (null != plugin.mEventSink) {
                        plugin.mEventSink.success(jsonObject.toString());
                    }
                } catch (JSONException e) {}
            });
        }
    },
    customImageDispose {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    renderOrigin {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            Map arguments = (Map) call.arguments;
            boolean enable = (boolean) arguments.get("value");
            plugin.getGlView().rendererSwitch(!enable);
        }
    },
    selectedImageOrVideo {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    customRenderOrigin {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            Map arguments = (Map) call.arguments;
            boolean enable = (boolean) arguments.get("value");
            plugin.getGlView().rendererSwitch(!enable);
        }
    },
    customRenderConfig {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    startCustomRenderStremListen {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.setInfoCallback((width, height, fps, renderTime, hasFace) -> {
                if (null != plugin.mEventSink) {
                    plugin.mEventSink.success(hasFace ? "1" : "0");
                }
            });
        }
    },
    adjustSpotlight {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            Map arguments = (Map) call.arguments;
            double value = (double) arguments.get("value");
            float convert = ((float) value + 2) / 4;
            FUCamera.getInstance().setExposureCompensation(convert);
            result.success(null);
        }
    },
    chooseSessionPreset {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            Map arguments = (Map) call.arguments;
            int value = (int) arguments.get("value");
            switch (value) {
                case 0:
                    FUCamera.getInstance().changeResolution(640, 480);
                    break;
                case 1:
                    FUCamera.getInstance().changeResolution(1280, 720);
                    break;
                case 2:
                    FUCamera.getInstance().changeResolution(1920, 1080);
                    break;
            }
            result.success(1);
        }
    },
    changeCameraFront {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            FUCamera.getInstance().switchCamera();
        }
    },
    changeCameraFormat {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    requestVideoProcess {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    customVideoRePlay {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            if (plugin.getGlView() instanceof VideoGlView) {
                ((VideoGlView) plugin.getGlView()).replay();
            }
        }
    },
    downLoadCustomRender {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            Map arguments = (Map) call.arguments;
            int value = (int) arguments.get("value");
            switch (value) {
                case 0:
                case 1:
                    plugin.getGlView().takePic();
                    break;
            }
        }
    };

    public abstract void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result);

}
