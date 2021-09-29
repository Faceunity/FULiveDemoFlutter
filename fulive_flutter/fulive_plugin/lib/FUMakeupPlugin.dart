import 'package:flutter/services.dart';
import 'dart:async';
import 'fulive_plugin.dart';

class FUMakeupPlugin {
  static const MethodChannel channel = FULivePlugin.channel;

  //美颜
  static const String Makeup = "FUMakeup";

  ///native 初始化美妆
  static Future configMakeup() async {
    channel.invokeMethod(Makeup, {
      "method": "configMakeup",
    });
  }

  ///选择某个组合装
  static Future itemDidSelectedWithParams(int index) async {
    channel.invokeMethod(
        Makeup, {"method": "itemDidSelectedWithParams", "index": index});
  }

  ///滑动某个组合装slider
  static Future sliderChangeValueWithValue(int index, double value) async {
    channel.invokeMethod(Makeup, {
      "method": "sliderChangeValueWithValue",
      "index": index,
      "value": value
    });
  }

  ///选择某个子妆标题,不影响实际子妆设置
  static Future didSelectedSubTitleItem(int titleIndex) async {
    channel.invokeMethod(Makeup, {
      "method": "didSelectedSubTitleItem",
      "titleIndex": titleIndex,
    });
  }

  ///选择某个子妆
  static Future didSelectedSubItem(
      int subTitleIndex, int subIndex, int colorIndex) async {
    channel.invokeMethod(Makeup, {
      "method": "didSelectedSubItem",
      "subTitleIndex": subTitleIndex,
      "subIndex": subIndex,
    });
  }

  ///选择某个颜色
  static Future didSelectedColorItem(int subIndex, int colorIndex) async {
    channel.invokeMethod(Makeup, {
      "method": "didSelectedColorItem",
      "subIndex": subIndex,
      "colorIndex": colorIndex,
    });
  }

  ///滑动某个子妆slider
  static Future subMakupSliderChangeValueWithValue(
      int subTitleIndex, int subIndex, double value) async {
    channel.invokeMethod(Makeup, {
      "method": "subMakupSliderChangeValueWithValue",
      "subTitleIndex": subTitleIndex,
      "subIndex": subIndex,
      "value": value
    });
  }

  ///native 释放美妆插件
  static Future disposeMakeup() async {
    channel.invokeMethod(Makeup, {
      "method": "disposeMakeup",
    });
  }
}
