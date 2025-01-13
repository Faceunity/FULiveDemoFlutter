import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulivedemo_flutter/business/others/media_picker.dart';
import 'package:fulivedemo_flutter/common/capture_button.dart';
import 'package:fulivedemo_flutter/common/custom_page_route.dart';
import 'package:fulivedemo_flutter/common/display_view.dart';
import 'package:fulivedemo_flutter/business/render/render_view_model.dart';
import 'package:fulivedemo_flutter/common/popup_menu.dart';
import 'package:fulivedemo_flutter/common/slider_view.dart';
import 'package:fulivedemo_flutter/common/top_widget.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

// ignore: must_be_immutable
class RenderView extends StatefulWidget {

  final Function? backAction;

  final Function? viewWillAppear;

  final RenderViewModel viewModel;

  const RenderView({super.key, required this.viewModel, this.backAction, this.viewWillAppear});

  @override
  State<StatefulWidget> createState() {
    return RenderState();
  }
}

class RenderState extends State<RenderView> with SingleTickerProviderStateMixin {

  // 手动对焦动画
  late AnimationController scaleAnimationController;
  late Animation<double> scaleAnimation;

  @override
  void dispose() {
    widget.viewModel.dispose();
    // 恢复默认曝光度
    widget.viewModel.setCameraExposure(0.5);
    scaleAnimationController.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    FaceunityPlugin.setFaceProcessorDetectMode(1);
    FaceunityPlugin.setMaxFaceNumber(4);
    RenderPlugin.startCamera();
    // 设置默认曝光度
    widget.viewModel.setCameraExposure(0.5);

    widget.viewModel.listenEventChannel();

    scaleAnimationController = AnimationController(vsync: this, duration: const Duration(milliseconds: 300));
    scaleAnimation = Tween(begin: 1.0, end: 0.67).animate(scaleAnimationController);
    scaleAnimationController.addListener(() {
      setState(() {
      });
    });
  }

  int operatingTime = 0;
  double dx = 0;
  double dy = 0;

  @override
  Widget build(BuildContext context) {
    double screenWidth = ScreenUtil.getScreenW(context);
    double screenHeight = ScreenUtil.getScreenH(context);
    return Stack(
      alignment: Alignment.topCenter,
      children: [
        const DisplayView(identifier: "camera_render"), 
        // Alignment(x, y)
        GestureDetector(
          onTap: () {
            // 点击主页面，处理曝光度
            widget.viewModel.showExposureSlider.value = true;
            operatingTime = DateTime.now().millisecondsSinceEpoch;
            hideFocusAndLightingViewAfterDelay(1300);
          },
          onTapDown: (details) {
            // 处理手动对焦
            dx = details.globalPosition.dx;
            dy = details.globalPosition.dy;
            widget.viewModel.manualFocus.value = true;
            widget.viewModel.manualCameraFocus(dx, dy);
            // 动画
            scaleAnimationController.reset();
            scaleAnimationController.forward();
            hideFocusAndLightingViewAfterDelay(1300);
          },
          child: Container(color: Colors.transparent,),
        ),
        SafeArea(
          child: TopWidget(
            backAction: (){
              if (widget.backAction != null) {
                widget.backAction!();
              }
              // 恢复相机曝光度
              widget.viewModel.setCameraExposure(0.5);
              // 恢复分辨率
              widget.viewModel.selectedPreset = CapturePreset.preset720x1280;
              RenderPlugin.switchCapturePreset(widget.viewModel.selectedPreset.number);
              if (widget.viewModel.isFrontCamera == false) {
                // 恢复为前置摄像头
                widget.viewModel.switchCamera();
              }
              RenderPlugin.stopCamera();
              Navigator.pop(context);
            },
            switchCameraAction: () async {
              bool result = await widget.viewModel.switchCamera();
              if (!result) {
                // 切换相机失败提示
                // ignore: use_build_context_synchronously
                showCommonToast(context: context, content: "切换相机失败");
              }
            },
            debugAction: (){
              widget.viewModel.isHiddenDebugInfo.value = !widget.viewModel.isHiddenDebugInfo.value;
            },
            moreAction: () {
              if (widget.viewModel.supportMediaRendering && widget.viewModel.supportPresetSelection) {
                widget.viewModel.moreFunctionViewShowing.value = true;
                Navigator.push(context, PopupViewRoute(child: _popup()));
              } else {
                // 停止相机
                RenderPlugin.stopCamera();
                // 跳转到图片视频选择页面
                Navigator.of(context).push(
                  CustomPageRoute(
                    builder: (context) {
                      return MediaPicker(module: widget.viewModel.module);
                    }
                  )
                ).then((value) {
                  // 重新开始相机
                  FaceunityPlugin.setFaceProcessorDetectMode(1);
                  RenderPlugin.startCamera();
                  if (widget.viewWillAppear != null) {
                    widget.viewWillAppear!();
                  }
                  widget.viewModel.listenEventChannel();
                });
              }
            },
            segmentAction: (int value){
              defaultTargetPlatform == TargetPlatform.iOS ? RenderPlugin.switchCameraOutputFormat(value) : RenderPlugin.switchRenderInputType(value);
              widget.viewModel.segmentIndex = value;
            },
            type: (!widget.viewModel.supportMediaRendering && !widget.viewModel.supportPresetSelection) ? MoreFuctionType.none : (widget.viewModel.supportPresetSelection ? MoreFuctionType.whole : MoreFuctionType.single),
            selectedSegmentIndex: widget.viewModel.segmentIndex,
          )
        ),
        
        AnimatedPositioned(
          bottom: widget.viewModel.captureButtonBottom + 10,
          duration: const Duration(milliseconds: 100),
          child: CaptureButton(
            captureCallBack: () async { 
              RenderPlugin.takePhoto((MethodCall call) {
                if(call.method == "takePhotoResult") {
                  bool result = call.arguments as bool;
                  showCommonToast(context: context, content: result ? "图片已保存到相册" : "保存图片失败");
                }
              });
            },
            startRecordingCallBack: () {
              RenderPlugin.startRecord();
            },
            stopRecordingCallBack: () async {
              bool success = await RenderPlugin.stopRecord();
              // ignore: use_build_context_synchronously
              showCommonToast(context: context, content: success? "视频已保存到相册" : "保存视频失败");
            },
          )
        ),
        Positioned(
          left: 15,
          top: 60,
          child: SafeArea(child: _debugLabel())
        ),
        Positioned(
          right: -110,
          top: screenHeight / 2.0 - 60,
          child: Transform.rotate(
            angle: 3 * pi / 2,
            child: _lightSlider(),
          ),
        ),
        Align(
          alignment: Alignment(dx/screenWidth * 2.0 - 1, dy/screenHeight * 2.0 - 1),
          child: ValueListenableBuilder(valueListenable: widget.viewModel.manualFocus, builder: (context, value, child) {
            return Visibility(
              visible: value,
              child: ScaleTransition(
                scale: scaleAnimation,
                alignment: Alignment.center,
                child: Image(image: CommonUtil.assetImageNamed("render/render_adjust"))
              )
            );
          },)
        ),
        Center(
          child: _trackLabel(),
        ),
      ],
    );
  }

  void hideFocusAndLightingViewAfterDelay(int millisecond) {
    Future.delayed(Duration(milliseconds: millisecond), () {
      int current = DateTime.now().millisecondsSinceEpoch;
      if (current - operatingTime > 1290) {
            widget.viewModel.showExposureSlider.value = false;
            widget.viewModel.manualFocus.value = false;
        }
    });
  }

  Widget _popup() {
    double screenWidth = ScreenUtil.getInstance().screenWidth;
    double top = ScreenUtil.getInstance().statusBarHeight;
    return PopupMenu(
      points: [
        PopPosition(screenWidth - 145, 10),
        PopPosition(screenWidth - 135, 0),
        PopPosition(screenWidth - 125, 10)
      ],
      bgColor: const Color(0xA0000000), 
      offsetY: 10, 
      selectedIndex: widget.viewModel.selectedPreset.number,
      top: top + 45,
      height: 132,
      width: screenWidth - 30,
      formatCallback: (index) async {
        bool result = await RenderPlugin.switchCapturePreset(index);
        if (result) {
          widget.viewModel.selectedPreset = CapturePreset.values[index];
        } else {
          // ignore: use_build_context_synchronously
          showCommonToast(context: context, content: "设备不支持该分辨率");
        }
      },
      jumpCustomCallback: () {
        Navigator.of(context).pop();
        // 停止相机
        RenderPlugin.stopCamera();
        // 跳转到图片视频选择页面
        Navigator.of(context).push(
          CustomPageRoute(
            builder: (context) {
              return MediaPicker(module: widget.viewModel.module);
            }
          )
        ).then((value) {
          // 重新开始相机
          FaceunityPlugin.setFaceProcessorDetectMode(1);
          RenderPlugin.startCamera();
          if (widget.viewWillAppear != null) {
            widget.viewWillAppear!();
          }
          widget.viewModel.listenEventChannel();
        });
      },
      clickBlankCallBack: () {
        Navigator.of(context).pop();
      },

    );
  }
  
  Widget _trackLabel() {
    return ValueListenableBuilder(
      valueListenable: widget.viewModel.faceTracked, 
      builder: (context, value, child) {
        return Visibility(
          visible: !value,
          child: const Text("未检测到人脸", style: TextStyle(color: Colors.white, fontSize: 17),)
        );
      },
    );
  }

  Widget _debugLabel() {
    return ValueListenableBuilder(
      valueListenable: widget.viewModel.isHiddenDebugInfo, 
      builder: (context, value, child) {
        return Visibility(
          visible: !value,
          child: ValueListenableBuilder(
            valueListenable: widget.viewModel.debugInfo, 
            builder: (context, value, child) {
              return Container(
                padding: const EdgeInsets.fromLTRB(5, 5, 5, 5),
                decoration: BoxDecoration(
                  color: const Color.fromARGB(188, 84, 84, 84),
                  borderRadius: BorderRadius.circular(5.0)
                ),
                child: Text(value,
                  style: const TextStyle(
                    fontSize: 13.0,
                    color: Colors.white
                  ),
                ),
              );
            },
          )
        );
      },
    );
  }

  // Widget _manualFocusWidget() {

  // }

  Widget _lightSlider() {
    return ValueListenableBuilder(valueListenable: widget.viewModel.showExposureSlider, builder: (context, value, child) {
      return Visibility(
        visible: value,
        child: SizedBox(
          width: 280,
          height: 20,
          child: Stack(
            children: [ 
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Transform.rotate(
                    angle: pi / 2,
                    child: Image(image: CommonUtil.assetImageNamed("render/render_lighting_moon")),
                  ),
                  SizedBox(
                    width: 220,
                    child: SliderTheme(
                      data: SliderThemeData(
                        trackHeight: 4,
                        trackShape: FullWidthTrackShape(),
                        activeTrackColor: Colors.white,
                        inactiveTrackColor: Colors.white,
                        overlayColor: Colors.transparent,
                        overlayShape: SliderComponentShape.noOverlay,
                        thumbColor: Colors.white,
                      ), 
                      child: Slider(
                        value: widget.viewModel.exposureValue, 
                        onChanged: (value){
                          setState(() {
                            operatingTime = DateTime.now().millisecondsSinceEpoch;
                            hideFocusAndLightingViewAfterDelay(1300);
                            widget.viewModel.setCameraExposure(value);
                          });
                        },
                        onChangeEnd: (value) {
                          
                        },
                      ),
                    ), 
                  ),
                  Image(image: CommonUtil.assetImageNamed("render/render_lighting_sun")),
                ],
              ),
              Center(
                child: Container(color: Colors.white, width: 2, height: 10,)
              )
            ],
          ),
        )
      );
    },);
  }
}