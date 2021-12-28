package com.example.fulive_plugin.impl;

import com.example.fulive_plugin.FUCommon.DemoConfig;
import com.example.fulive_plugin.FULivePlugin;
import com.example.fulive_plugin.entity.FaceBeautyDataFactory;
import com.example.fulive_plugin.entity.MakeupDataFactory;
import com.example.fulive_plugin.entity.StickerDataFactory;
import com.example.fulive_plugin.view.BaseGLView;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author benyq
 * @date 2021/12/1
 * @email 1520063035@qq.com
 */
public enum FuStickerKey {

    configBiz {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            BaseGLView.runOnGLThread(() -> {
                FUAIKit.getInstance().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
                StickerDataFactory.configBiz();
            });
        }
    },

    dispose {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            StickerDataFactory.release();
        }
    },
    clickItem {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int index = (int) arguments.get("index");
                StickerDataFactory.onItemSelected(index);
                result.success(true);
            } catch (ClassCastException e) {
                e.printStackTrace();
                result.success(false);
            }
        }
    },
    flutterWillDisappear {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.getGlView().onPause();
            plugin.setState(FULivePlugin.STATE_CUSTOM);
        }
    },
    flutterWillAppear {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            plugin.setState(FULivePlugin.STATE_DISPLAY);
            if (plugin.getGlView() != null) {
                plugin.getGlView().onResume();
            }
            BaseGLView.runOnGLThread(() -> {
                FUAIKit.getInstance().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
                StickerDataFactory.configBiz();
            });
        }
    };


    public abstract void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result);

}
