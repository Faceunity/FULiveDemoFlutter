package com.example.fulive_plugin.impl;

import com.example.fulive_plugin.FUCommon.DemoConfig;
import com.example.fulive_plugin.FULivePlugin;
import com.example.fulive_plugin.entity.FaceBeautyDataFactory;
import com.example.fulive_plugin.view.BaseGLView;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.facebeauty.FaceBeauty;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author Qinyu on 2021-10-09
 * @description
 */
public enum FuBeautyKey {
    setFUBeautyParams {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int type = (int) arguments.get("bizType");
                int index = (int) arguments.get("subBizType");
                switch (type) {
                    case 0: {
                        double value = (double) arguments.get("value");
                        FaceBeautyDataFactory.setSkinBeauty(index, value);
                        break;
                    }
                    case 1: {
                        double value = (double) arguments.get("value");
                        FaceBeautyDataFactory.setShapeBeauty(index, value);
                        break;
                    }
                    case 3: //风格推荐
                        FaceBeautyDataFactory.setStyle(index);
                        break;
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    disposeFUBeauty {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            FURenderKit.getInstance().setFaceBeauty(null);
        }
    },
    setFilterParams {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            FaceBeauty faceBeauty = FaceBeautyDataFactory.getFaceBeauty();
            try {
                Map arguments = (Map) call.arguments;
                double value = (double) arguments.get("value");
                String name = (String) arguments.get("stringValue");
                faceBeauty.setFilterName(name);
                faceBeauty.setFilterIntensity(value);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    configBeauty {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            BaseGLView.runOnGLThread(() -> {
                FUAIKit.getInstance().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.getFaceBeauty());
            });
        }
    },
    beautyClean {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            FURenderKit.getInstance().setFaceBeauty(null);
        }
    },
    resetDefault {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            FaceBeauty faceBeauty = FaceBeautyDataFactory.getFaceBeauty();
            try {
                Map arguments = (Map) call.arguments;
                int type = (int) arguments.get("bizType");
                switch (type) {
                    case 0:
                        FaceBeautyDataFactory.resetSkinBeauty(faceBeauty);
                        break;
                    case 1:
                        FaceBeautyDataFactory.resetShapeBeauty(faceBeauty);
                        break;
                    case 2:
                        FaceBeautyDataFactory.resetFilter(faceBeauty);
                        break;
                }
            } catch (ClassCastException e) {

            }
        }
    },
    FlutterWillDisappear {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.getGlView().onPause();
            plugin.setState(FULivePlugin.STATE_CUSTOM);
        }
    },
    FlutterWillAppear {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.setState(FULivePlugin.STATE_DISPLAY);
            plugin.getGlView().onResume();
            BaseGLView.runOnGLThread(() -> {
                FUAIKit.getInstance().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.getFaceBeauty());
            });
        }
    };

    public abstract void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result);

}
