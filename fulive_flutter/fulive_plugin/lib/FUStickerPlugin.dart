import 'package:flutter/services.dart';
import 'package:fulive_plugin/FUBizAbstractWidget.dart';
import 'package:fulive_plugin/fulive_plugin.dart';

class FUStickerPlugin extends FUBizAbstractWidget {
  static const MethodChannel _channel = FULivePlugin.channel;

  //贴纸
  String sticker = "Sticker";

  @override
  Future configBiz() async {
    _channel.invokeMethod(sticker, {
      "method": "configBiz",
    });
  }

  ///Flutter 离开当前页面
  @override
  Future flutterWillDisappear() async {
    _channel.invokeMethod(sticker, {
      "method": "flutterWillDisappear",
    });
  }

  ///Flutter 回到当前页面
  @override
  Future flutterWillAppear() async {
    _channel.invokeMethod(sticker, {
      "method": "flutterWillAppear",
    });
  }

  @override
  Future dispose() async {
    _channel.invokeMethod(sticker, {
      "method": "dispose",
    });
  }

  //index 贴纸索引
  Future<bool> clickItem(int index) async {
    bool ret = await _channel.invokeMethod(sticker, {
      "method": "clickItem",
      "index": index,
    });
    return ret;
  }
}
