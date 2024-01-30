import 'dart:async';

import 'package:flutter/services.dart';

class RenderEventChannel {
  RenderEventChannel._internal();
  static RenderEventChannel? _instance;
  static RenderEventChannel getInstance() {
    _instance ??= RenderEventChannel._internal();
    return _instance!;
  }

  static dispose() {
    _instance?._streamSubscription.cancel();
    _instance = null;
  }

    /*
  * 原生端回传 map 数据结构如下：
  * {
  *   "faceTracked":true/false, // 是否检测到人脸
  *   "videoReadingFinished":true, // 视频播放完成
  *   "videoExportingProgress":0.0-1.0, // 视频导出进度
  *   "videoExportingResult":true/false, // 视频导出是否成功
  *   "debugInfo":""            // 调试信息数据，为空时不需要刷新信息
  * }
  */
  final _eventChannel = const EventChannel("render_event_channel");

  late StreamSubscription _streamSubscription;

  void listen(Function listenCallBack) {
    // 设置事件监听
    _streamSubscription = _eventChannel.receiveBroadcastStream().listen((event) {
      Map<Object?, Object?> map = event;
      listenCallBack(map);
    }, cancelOnError: true);
  }
}