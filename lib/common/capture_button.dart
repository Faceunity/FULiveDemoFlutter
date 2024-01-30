import 'dart:async';

import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';

class CaptureButton extends StatefulWidget {
   
  const CaptureButton({super.key, this.captureCallBack, this.startRecordingCallBack, this.stopRecordingCallBack});


  final VoidCallback? captureCallBack;

  final VoidCallback? startRecordingCallBack;

  final VoidCallback? stopRecordingCallBack;

  @override
  State<StatefulWidget> createState() {
    return CaptureButtonState();
  }

}

class CaptureButtonState extends State<CaptureButton> {

  ValueNotifier<bool> isVideoRecording = ValueNotifier(false);
  // 录像进度，0-100（100对应10秒）
  ValueNotifier<int> progress = ValueNotifier(0);

  late Timer timer;

  @override
  void dispose() {
    isVideoRecording.dispose();
    progress.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: Alignment.center,
      children: [
        ValueListenableBuilder(valueListenable: isVideoRecording, builder: (context, value, child) {
          return AnimatedContainer(
            width: value ? 89 : 81,
            height: value ? 89 : 81,
            duration: const Duration(milliseconds: 100),
            child: Image(
              image: CommonUtil.assetImageNamed("render/render_camera_capture"),
              fit: BoxFit.fill,
            ),
          );
        },),

        ValueListenableBuilder(valueListenable: isVideoRecording, builder: (context, value, child) {
          return Visibility(
            visible: value,
            child: SizedBox(
              width: 81.5,
              height: 81.5,
              child: ValueListenableBuilder(valueListenable: progress, builder: (context, value, child) {
                return CircularProgressIndicator(
                  color: Colors.white,
                  valueColor: const AlwaysStoppedAnimation(Color.fromARGB(255, 94, 199, 254)),
                  value: value / 100,
                  strokeWidth: 6,
                );
              },)
            )
          );
        },),

        SizedBox(
          width: 81,
          height: 81,
          child: GestureDetector(
            onTap: () {
              if (widget.captureCallBack != null) {
                widget.captureCallBack!();
              }
            },
            onLongPress: () {
              if (widget.startRecordingCallBack != null) {
                widget.startRecordingCallBack!();
              }
              startRecord();
            },
            onLongPressStart: (details) {
              isVideoRecording.value = true;
            },
            onLongPressEnd: (details) {
              isVideoRecording.value = false;
              if (widget.stopRecordingCallBack != null) {
                widget.stopRecordingCallBack!();
              }
              stopRecord();
            },
          ),
        )
      ],
    );
  }

  void startRecord() {
    timer = Timer.periodic(const Duration(milliseconds: 100), (timer) { 
      progress.value += 1;
      if (progress.value >= 100) {
        stopRecord();
      }
     });
  }

  void stopRecord() {
    progress.value = 0;
    timer.cancel();
  }
  
}