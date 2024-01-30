import 'package:fulive_plugin/fulive_plugin.dart';

class MakeupPlugin {
  static const _channel = FaceunityPlugin.methodChannel;

  static const int moduleCode = 1;

  /// 加载组合妆容
  /// @param makeup 组合妆容模型转换的 Map
  static Future<void> loadCombinationMakeup(Map<String, dynamic> makeup) async {
    _channel.invokeMethod("loadCombinationMakeup", {"module" : moduleCode, "arguments" : [{"makeup" : makeup}]});
  }

  /// 设置当前组合妆容的程度值
  /// @param makeup 组合妆容模型转换的 Map
  static Future<void> setCombinationMakeupIntensity(Map<String, dynamic> makeup) async {
    _channel.invokeMethod("setCombinationMakeupIntensity", {"module" : moduleCode, "arguments" : [{"makeup" : makeup}]});
  }

  /// 设置子妆 bundle
  /// @param subMakeup 子妆容模型转换的 Map
  /// @note 绑定子妆到 face_makeup.bundle ，如果当前没有加载 face_makeup.bundle ，需要先加载 face_makeup.bundle
  static Future<void> setSubMakeupBundle(Map<String, dynamic> subMakeup) async {
    _channel.invokeMethod("setSubMakeupBundle", {"module" : moduleCode, "arguments" : [{"subMakeup" : subMakeup}]});
  }

  /// 设置子妆程度值
  /// @param subMakeup 子妆容模型转换的 Map
  static Future<void> setSubMakeupIntensity(Map<String, dynamic> subMakeup) async {
    _channel.invokeMethod("setSubMakeupIntensity", {"module" : moduleCode, "arguments" : [{"subMakeup" : subMakeup}]});
  }

  /// 设置子妆颜色
  /// @param subMakeup 子妆容模型转换的 Map
  static Future<void> setSubMakeupColor(Map<String, dynamic> subMakeup) async {
    _channel.invokeMethod("setSubMakeupColor", {"module" : moduleCode, "arguments" : [{"subMakeup" : subMakeup}]});
  }

  /// 卸载子妆
  /// @param type 子妆类型，参考 SubMakeupType 枚举
  static Future<void> unloadSubMakeup(int type) async {
    _channel.invokeMethod("unloadSubMakeup", {"module" : moduleCode, "arguments" : [{"type" : type}]});
  }

  /// 卸妆（需要同时恢复美颜滤镜）
  static Future<void> unloadCombinationMakeup() async {
    _channel.invokeMethod("unloadCombinationMakeup", {"module" : moduleCode});
  }
}