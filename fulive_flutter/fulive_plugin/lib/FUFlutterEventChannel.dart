import 'package:flutter/services.dart';
import 'dart:async';

class FUFlutterEventChannel {
  final EventChannel _channel = EventChannel('FUEventChannel');

  late StreamSubscription _streamSubscription;

  Function messageCallback = (String message) => {};
  Function errorCallback = (Error error) => {};
  FUFlutterEventChannel(this.messageCallback, this.errorCallback) {
    _streamSubscription = _channel
        .receiveBroadcastStream()
        .listen(_onEvent, onError: _onToDartError);
  }

  void _onEvent(message) {
    // print("debug 信息流: $message");
    this.messageCallback(message);
  }

  void _onToDartError(error) {
    print(error);
    this.errorCallback(error);
  }

  void cancel() {
    _streamSubscription.cancel();
  }

  void pause() {
    _streamSubscription.pause();
  }

  bool isPaused() {
    return _streamSubscription.isPaused;
  }

  void resume() {
    //_streamSubscription 没停止也可以调用resume，内部做了安全处理
    _streamSubscription.resume();
  }
}
