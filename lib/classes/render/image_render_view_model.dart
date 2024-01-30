import 'package:flutter/material.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/classes/render/render_channel.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class ImageRenderViewModel extends ChangeNotifier {
  ImageRenderViewModel() : super() {
    RenderEventChannel.getInstance().listen((map) {
      if (map.containsKey("faceTracked")) {
        faceTracked.value = map["faceTracked"];
      }
    });
  }

  ValueNotifier<bool> faceTracked = ValueNotifier(false);

  // 保存按钮距离底部高度
  double captureButtonBottom = ScreenUtil.getInstance().bottomBarHeight + 59;

  // 拍摄
  void capture() {

  }

  @override
  void dispose() {
    // _streamSubscription.cancel();
    faceTracked.dispose();
    RenderPlugin.disposeImageRender();
    super.dispose();
  }
}