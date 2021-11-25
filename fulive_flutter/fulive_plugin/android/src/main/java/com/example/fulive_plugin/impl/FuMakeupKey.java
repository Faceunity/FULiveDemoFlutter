package com.example.fulive_plugin.impl;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author Qinyu on 2021-10-09
 * @description
 */
public enum FuMakeupKey {
    configMakeup {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    itemDidSelectedWithParams {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    sliderChangeValueWithValue {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    didSelectedSubTitleItem {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    didSelectedSubItem {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    didSelectedColorItem {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    subMakupSliderChangeValueWithValue {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    },
    disposeMakeup {
        @Override
        public void handle(MethodCall call, MethodChannel.Result result) {

        }
    };

    public abstract void handle(MethodCall call, MethodChannel.Result result);
}
