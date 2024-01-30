import 'package:fulive_plugin/fulive_plugin.dart';

class StickerPlugin {
  static const _channel = FaceunityPlugin.methodChannel;

  static const int moduleCode = 2;

  static Future<void>  selectSticker(String name) async {
    _channel.invokeMethod("selectSticker", {"module" : moduleCode, "arguments" : [{"name" : name}]});
  }

  // 移除当前贴纸，释放内存
  static Future<void> removeSticker() async {
    _channel.invokeMethod("removeSticker", {"module" : moduleCode});
  }
}