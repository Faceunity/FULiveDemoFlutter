package com.example.fulive_plugin.impl;

import com.example.fulive_plugin.FUCommon.DemoConfig;
import com.example.fulive_plugin.FULivePlugin;
import com.example.fulive_plugin.entity.MakeupDataFactory;
import com.example.fulive_plugin.view.BaseGLView;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author Qinyu on 2021-10-09
 * @description
 */
public enum FuMakeupKey {
    configMakeup {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            BaseGLView.runOnGLThread(() -> {
                FUAIKit.getInstance().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
                MakeupDataFactory.configureMakeup();
                //美妆必须单纹理输入
                plugin.getGlView().setInputBuffer(false);
            });
        }
    },
    makeupChange {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                boolean value = (boolean) arguments.get("value");
                if (!value) {
                    MakeupDataFactory.enterCustomMakeup();
                }else {
                    MakeupDataFactory.exitCustomMakeup();
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    itemDidSelectedWithParams {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int index = (int) arguments.get("index");
                MakeupDataFactory.onMakeupCombinationSelected(index);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    sliderChangeValueWithValue {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int index = (int) arguments.get("index");
                double value = (double) arguments.get("value");
                MakeupDataFactory.sliderChangeValueWithValue(index, value);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    didSelectedSubTitleItem {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {

        }
    },
    didSelectedSubItem {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int subIndex = (int) arguments.get("subIndex");//例如 粉底类别下标
                int subTitleIndex = (int) arguments.get("subTitleIndex");//例如 粉底
                MakeupDataFactory.onCustomBeanSelected(subTitleIndex, subIndex);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    didSelectedColorItem {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int subTitleIndex = (int) arguments.get("subTitleIndex");
                int subIndex = (int) arguments.get("subIndex");
                int colorIndex = (int) arguments.get("colorIndex");
                MakeupDataFactory.updateCustomColor(subTitleIndex, colorIndex);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    subMakupSliderChangeValueWithValue {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int subIndex = (int) arguments.get("subIndex");
                int subTitleIndex = (int) arguments.get("subTitleIndex");
                double value = (double) arguments.get("value");//例如 粉底
                MakeupDataFactory.updateCustomItemIntensity(subTitleIndex, subIndex, value);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    },
    disposeMakeup {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            MakeupDataFactory.releaseMakeup();
            plugin.getGlView().setInputBuffer(true);
        }
    },

    requestCustomIndex {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int index = (int) arguments.get("index");
                String resultJson = MakeupDataFactory.createMakeupParamJson(index);
                result.success(resultJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    subMakeupChange {
        @Override
        public void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result) {
            try {
                Map arguments = (Map) call.arguments;
                int index = (int) arguments.get("index");
                result.success(!MakeupDataFactory.checkOldParam());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    ;

    public abstract void handle(FULivePlugin plugin, MethodCall call, MethodChannel.Result result);
}
