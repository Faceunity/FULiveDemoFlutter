import 'dart:async';

import 'package:flutter/material.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/classes/render/render_channel.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class RenderViewModel extends ChangeNotifier {

  RenderViewModel(this.module);

  final Module module;

  // 是否前置摄像头，默认 true
  bool isFrontCamera = true;

  /// 是否支持图片或者视频渲染，默认为 false
  bool supportMediaRendering = false;

  /// 是否支持分辨率选择，默认为 false
  bool supportPresetSelection = false;

  /// 支持的分辨率
  List<CapturePreset> presets = [CapturePreset.preset480x640, CapturePreset.preset720x1280, CapturePreset.preset1080x1920];

  /// 当前的分辨率
  CapturePreset selectedPreset = CapturePreset.preset720x1280;

  /// 选中索引（iOS：格式 android：单双输入）
  int segmentIndex = 0;

  /// 分辨率选择/图片视频选择视图是否展示
  ValueNotifier<bool> moreFunctionViewShowing = ValueNotifier(false);

  ValueNotifier<bool> faceTracked = ValueNotifier(false);

  ValueNotifier<String> debugInfo = ValueNotifier("resolution:\n720x1280\nfps:30\nrender time:\n0ms");
  // 拍摄按钮距离底部高度
  double captureButtonBottom = ScreenUtil.getInstance().bottomBarHeight + 59;
  // 当前曝光度
  double exposureValue = 0.5;
  // 是否显示曝光度调节器
  ValueNotifier<bool> showExposureSlider = ValueNotifier(false);
  // 是否手动对焦
  ValueNotifier<bool> manualFocus = ValueNotifier(false);
  // 是否隐藏调试信息，默认隐藏
  ValueNotifier<bool> isHiddenDebugInfo = ValueNotifier(true);

  @override
  void dispose() {
    RenderEventChannel.dispose();
    faceTracked.dispose();
    debugInfo.dispose();
    isHiddenDebugInfo.dispose();
    showExposureSlider.dispose();
    manualFocus.dispose();
    super.dispose();
  }

  void listenEventChannel() {
    RenderEventChannel.getInstance().listen((map) {
      if (map.containsKey("faceTracked")) {
        faceTracked.value = map["faceTracked"];
      }
      if (map.containsKey("debugInfo")) {
        debugInfo.value = map["debugInfo"];
      }
    });
  }

  Future<bool> switchCamera() async {
    bool result = await RenderPlugin.switchCamera(!isFrontCamera);
    if (result) {
       isFrontCamera = !isFrontCamera;
    }
    return result;
  }

  // 设置相机曝光度
  void setCameraExposure(double value) async {
    exposureValue = value;
    RenderPlugin.setCameraExposure(value);
  }

  void manualCameraFocus(double dx, double dy) async {
    RenderPlugin.manualFocus(dx, dy);
  }
}