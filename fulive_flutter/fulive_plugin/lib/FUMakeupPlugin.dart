import 'dart:async';
import 'fulive_plugin.dart';
import 'package:flutter/services.dart';

class FUMakeupPlugin {
  static const MethodChannel _channel = FULivePlugin.channel;

  //美颜
  static const String Makeup = "FUMakeup";

  ///native 初始化美妆
  static Future configMakeup() async {
    _channel.invokeMethod(Makeup, {
      "method": "configMakeup",
    });
  }

  ///选择某个组合装
  ///index组合妆索引
  static Future itemDidSelectedWithParams(int index) async {
    _channel.invokeMethod(
        Makeup, {"method": "itemDidSelectedWithParams", "index": index});
  }

  ///滑动某个组合装slider
  ///index组合妆索引
  ///value 组合妆整体程度值
  static Future sliderChangeValueWithValue(int index, double value) async {
    _channel.invokeMethod(Makeup, {
      "method": "sliderChangeValueWithValue",
      "index": index,
      "value": value
    });
  }

  ///选择某个子妆标题,不影响实际子妆设置
  ///subTitleIndex 子妆标题索引 如口红、眉毛
  static Future didSelectedSubTitleItem(int subTitleIndex) async {
    _channel.invokeMethod(Makeup, {
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
    _channel.invokeMethod(Makeup, {
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
    _channel.invokeMethod(Makeup, {
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
    _channel.invokeMethod(Makeup, {
      "method": "subMakupSliderChangeValueWithValue",
      "subTitleIndex": subTitleIndex,
      "subIndex": subIndex,
      "value": value
    });
  }

  ///native 释放美妆插件
  static Future disposeMakeup() async {
    _channel.invokeMethod(Makeup, {
      "method": "disposeMakeup",
    });
  }

  ///子妆和组合妆切换 flag == true 子妆切换成组合妆， false 反之
  ///目前android在用
  static Future makeupChange(bool flag) async {
    _channel.invokeMethod(Makeup, {
      "method": "makeupChange",
      "value": flag,
    });
  }

  /// 业务数据逻辑利用native 处理检测支持自定义子妆的组合妆，从组合妆切换成自定义子妆的时候对应的具体子妆的数据状态
  ///canCustomIndex, 支持自定义子妆的组合妆索引
  ///返回值数据结构描述, json string 形式给出
  ///   {
  ///    sub: [{
  ///            title: "口红",
  ///            bundleIndex: 0 , 具体子妆的索引，如口红的红润、珠光、
  ///            colorIndex: 0 , 具体子妆颜色值索引
  ///            value : "0.0",  //整体妆容程度值 * 具体子妆对应的程度值, 字符串
  ///         },
  ///          {
  ///            title: "眉毛",
  ///            bundleIndex: 0 , 具体子妆的索引，如眉毛的 标准眉、柳叶眉
  ///            colorIndex: 0 , 具体子妆颜色值索引
  ///            value : "0.0",  //整体妆容程度值 * 具体子妆对应的程度值
  ///          }]
  ///   }
  static Future<String> requestCustomIndex(int canCustomIndex) async {
    String indexs = await _channel.invokeMethod(
        Makeup, {"method": "requestCustomIndex", "index": canCustomIndex});
    return indexs;
  }

  /// 业务数据逻辑利用native 处理检测支持自定义子妆的组合妆 从子妆切换回组合妆的模式时候是否有变化
  /// 返回值表明支持自定义子妆的组合妆的子妆值是否有变化, true 变化，false 没变
  static Future<bool> subMakeupChange(int canCustomIndex) async {
    bool isChange = await _channel.invokeMethod(
        Makeup, {"method": "subMakeupChange", "index": canCustomIndex});
    return isChange;
  }
}
