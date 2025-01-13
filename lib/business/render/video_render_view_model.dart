import 'package:flutter/material.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/business/render/render_channel.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class VideoRenderViewModel extends ChangeNotifier {
  VideoRenderViewModel() : super() {
    RenderEventChannel.getInstance().listen((map) {
      if (map.containsKey("faceTracked")) {
        faceTracked.value = map["faceTracked"] as bool;
      }
      if (map.containsKey("videoPlayingFinished")) {
        // 播放完成
        playing.value = false;
      }
      if (map.containsKey("videoExportingProgress")) {
        // 导出视频进度
        double progress = map["videoExportingProgress"] as double;
        progress = progress < 0 ? 0 : (progress > 1 ? 1 : progress);
        exportingProgress.value = progress;
      }
      if (map.containsKey("videoExportingResult")) {
        // 导出视频结果
        bool exportingResult = map["videoExportingResult"] as bool;
        if (exportingCallBack != null) {
          exportingCallBack!(exportingResult);
        }
      }
    });
  }

  ValueNotifier<bool> faceTracked = ValueNotifier(false);

  // 保存按钮距离底部高度
  double captureButtonBottom = ScreenUtil.getInstance().bottomBarHeight + 59;
  // 是否正在播放
  ValueNotifier<bool> playing = ValueNotifier(false);
  // 是否正在导出视频
  bool exporting = false;
  // 导出视频进度
  ValueNotifier<double> exportingProgress = ValueNotifier(0.0);

  // late StreamSubscription _streamSubscription;

  Function? exportingCallBack;

  void startPreviewing() {
    RenderPlugin.startPreviewingVideo();
  }

  void stopPreviewing() {
    RenderPlugin.stopPreviewingVideo();
  }

  void startPlaying() {
    RenderPlugin.startPlayingVideo();
    playing.value = true;
  }

  void stopPlaying() {
    RenderPlugin.stopPlayingVideo();
    playing.value = false;
  }

  void startExporting() {
    exporting = true;
    exportingProgress.value = 0.0;
    RenderPlugin.startExportingVideo();
  }

  void stopExporting() {
    exporting = false;
    exportingProgress.value = 0.0;
    RenderPlugin.stopExportingVideo();
  }

  @override
  void dispose() {
    // _streamSubscription.cancel();
    faceTracked.dispose();
    playing.dispose();
    RenderPlugin.disposeVideoRender();
    super.dispose();
  }
}