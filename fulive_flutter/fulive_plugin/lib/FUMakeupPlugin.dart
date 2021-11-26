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
  ///index组合妆索引
  static Future itemDidSelectedWithParams(int index) async {
    channel.invokeMethod(
        Makeup, {"method": "itemDidSelectedWithParams", "index": index});
  }

  ///滑动某个组合装slider
  ///index组合妆索引
  ///value 组合妆整体程度值
  static Future sliderChangeValueWithValue(int index, double value) async {
    channel.invokeMethod(Makeup, {
      "method": "sliderChangeValueWithValue",
      "index": index,
      "value": value
    });
  }

  ///选择某个子妆标题,不影响实际子妆设置
  ///subTitleIndex 子妆标题索引 如口红、眉毛
  static Future didSelectedSubTitleItem(int subTitleIndex) async {
    channel.invokeMethod(Makeup, {
      "method": "didSelectedSubTitleItem",
      "titleIndex": subTitleIndex,
    });
  }

  ///选择某个子妆
  ///subTitleIndex 子妆标题索引 如口红、眉毛
  ///subIndex 选中的具体子妆索引比如口红子妆里面雾、润泽、珠光等
  ///colorIndex 颜色值索引
  static Future didSelectedSubItem(
      int subTitleIndex, int subIndex, int colorIndex) async {
    channel.invokeMethod(Makeup, {
      "method": "didSelectedSubItem",
      "subTitleIndex": subTitleIndex,
      "subIndex": subIndex,
    });
  }

  ///选择某个颜色
  /// subTitleIndex 子妆标题索引 如口红、眉毛
  /// subIndex 选中的具体子妆索引比如口红子妆里面雾、润泽、珠光等
  /// colorIndex 颜色值索引
  static Future didSelectedColorItem(
      int subTitleIndex, int subIndex, int colorIndex) async {
    channel.invokeMethod(Makeup, {
      "method": "didSelectedColorItem",
      "subTitleIndex": subTitleIndex,
      "subIndex": subIndex,
      "colorIndex": colorIndex,
    });
  }

  ///滑动某个子妆slider
  ///subTitleIndex 子妆标题索引如 口红、眉毛这些
  ///subIndex 选中的具体子妆索引比如口红子妆里面雾、润泽、珠光等
  ///value 子妆强度值
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
