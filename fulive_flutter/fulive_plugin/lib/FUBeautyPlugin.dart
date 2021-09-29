import 'package:flutter/services.dart';

import 'fulive_plugin.dart';

class FUBeautyPlugin {
  static const MethodChannel channel = FULivePlugin.channel;

  //美颜
  static const String Beauty = "FUBeauty";

  ///bizType 对应的是 美肤、美型、滤镜
  ///subBizType 对应的具体属性
  /// 参数 method 是对应的美颜模块具体方法名称
  static Future setFUBeautyParams<T>(
      int bizType, int subBizType, T value) async {
    channel.invokeMethod(Beauty, {
      "method": "setFUBeautyParams",
      "bizType": bizType,
      "subBizType": subBizType,
      "value": value
    });
  }

  ///销毁美颜插件
  static Future disposeFUBeauty() async {
    channel.invokeMethod(Beauty, {
      "method": "disposeFUBeauty",
    });
  }

  ///滤镜需要额外设置字符串参数
  static Future setFilterParams<T>(
      int bizType, int subBizType, T value, String stringValue) async {
    channel.invokeMethod(Beauty, {
      "method": "setFilterParams",
      "bizType": bizType,
      "subBizType": subBizType,
      "value": value,
      "stringValue": stringValue
    });
  }

  ///native 初始化美颜
  static Future configBeauty() async {
    channel.invokeMethod(Beauty, {
      "method": "configBeauty",
    });
  }

  ///widget 结束时清除FURenderKit 的配置
  static Future beautyClean() async {
    channel.invokeMethod(Beauty, {
      "method": "beautyClean",
    });
  }

  ///widget 重置美颜某个业务的效果
  static Future resetDefault(int bizType) async {
    channel
        .invokeMethod(Beauty, {"method": "resetDefault", "bizType": bizType});
  }

  ///Flutter 离开当前页面
  static Future flutterWillDisappear() async {
    channel.invokeMethod(Beauty, {
      "method": "FlutterWillDisappear",
    });
  }

  ///Flutter 回到当前页面
  static Future flutterWillAppear() async {
    channel.invokeMethod(Beauty, {
      "method": "FlutterWillAppear",
    });
  }
}
