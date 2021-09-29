import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:fulive_plugin/FUFlutterEventChannel.dart';
import 'package:fulive_plugin/fulive_plugin.dart';

///管理基础类 美颜流式数据 (debug信息 和检测人脸信息)
class FUBaseStreamManager extends ChangeNotifier {
  late String debugStr = '';
  bool showDebugInfo = false;

  ///监听native 流式数据通道
  late FUFlutterEventChannel _channel;

  late Error error;

  //检测是否有人脸
  bool hasFace = false;
  FUBaseStreamManager() {
    startStream();
  }

  void _onData(message) {
    var jsonStr = message;
    Map<String, dynamic> par = json.decode(jsonStr);
    this.debugStr = par["debug"];
    this.hasFace = par["hasFace"];
    notifyListeners();
  }

  void setShowDebugInfo(bool show) {
    showDebugInfo = show;
    notifyListeners();
  }

  void cancel() {
    _channel.cancel();
  }

  void _error(Error error) {
    this.error = error;
    notifyListeners();
  }

  void startStream() {
    //开启流式通道
    FULivePlugin.startBeautyStreamListen();
    _channel = FUFlutterEventChannel(
        (message) => _onData(message), (Error error) => _error(error));
  }
}
