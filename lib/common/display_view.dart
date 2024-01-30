import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class DisplayView extends StatelessWidget {
  const DisplayView({super.key, required this.identifier});

  /// 通过 creationParams 发送使用场景标识给原生端，区别视图场景（camera_render/image_render/video_render）
  final String identifier;

  @override
  Widget build(BuildContext context) {
    const String viewType = 'faceunity_display_view';
    switch (defaultTargetPlatform) {
      case TargetPlatform.iOS:
        return UiKitView(
            viewType: viewType,
            creationParams: identifier,
            creationParamsCodec: const StringCodec());
      case TargetPlatform.android:
        return AndroidView(
            viewType: viewType,
            creationParams: identifier,
            creationParamsCodec: const StandardMessageCodec());
      default:
        throw UnsupportedError("Unsupported platform view");
    }
  }
}
