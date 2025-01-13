import 'package:flutter/material.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/business/render/video_render_view_model.dart';
import 'package:fulivedemo_flutter/common/display_view.dart';
import 'package:fulivedemo_flutter/common/popup_menu.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';

class VideoRenderView extends StatefulWidget {

  final VideoRenderViewModel viewModel;

  final Function? backAction;

  const VideoRenderView({super.key, required this.viewModel, this.backAction});

  @override
  State<StatefulWidget> createState() {
    return VideoRenderState();
  }
}

class VideoRenderState extends State<VideoRenderView> with WidgetsBindingObserver {
  @override
  void initState() {
    FaceunityPlugin.setFaceProcessorDetectMode(1);
    // 默认首帧渲染
    widget.viewModel.startPreviewing();
    widget.viewModel.exportingCallBack = (bool result){
      showCommonToast(context: context, content: result ? "视频已保存到相册" : "保存视频失败");
      widget.viewModel.stopExporting();
      widget.viewModel.startPreviewing();
      Navigator.pop(context);
    };
    super.initState();
    // 监听 App 状态
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    switch (state) {
      case AppLifecycleState.resumed: {
        // App 切换到前台
        widget.viewModel.startPreviewing();
      }
        break;
      case AppLifecycleState.inactive: {
        // App 转入不活跃状态
        widget.viewModel.stopPreviewing();
        if (widget.viewModel.playing.value) {
          widget.viewModel.stopPlaying();
        }
        if (widget.viewModel.exporting) {
          widget.viewModel.stopExporting();
          Navigator.pop(context);
        }
      }
        break;
      default:
    }
  }

  @override
  void dispose() {
    widget.viewModel.dispose();
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);
  }

  @override
  Widget build(BuildContext context) {
    
    return Stack(
      alignment: Alignment.topCenter,
      children: [
        const DisplayView(identifier: "video_render"),
        Positioned(
          left: 5,
          child: SafeArea(
            child: IconButton(
              splashColor: Colors.transparent,
              highlightColor: Colors.transparent,
              hoverColor: Colors.transparent,
              onPressed: () {
                RenderPlugin.disposeVideoRender();
                Navigator.pop(context);
              }, 
              icon: Image(image: CommonUtil.assetImageNamed("common/back"))
            )
          ),
        ),

        Center(
          child: _trackLabel(),
        ),
        
        AnimatedPositioned(
          bottom: widget.viewModel.captureButtonBottom + 10,
          duration: const Duration(milliseconds: 100),
          child: GestureDetector(
            onTap: () {
              if (widget.viewModel.playing.value) {
                // 停止播放
                widget.viewModel.stopPlaying();
              } else {
                // 停止预览
                widget.viewModel.stopPreviewing();
              }
              // 开始导出视频
              widget.viewModel.startExporting();
              Navigator.push(context, PopupViewRoute(child: _exportingView()));
            },
            child: Container(
              width: 84,
              height: 84,
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(42.0)
              ),
              child: Image(image: CommonUtil.assetImageNamed("render/render_save"), fit: BoxFit.fill),
            ),
          ),
        ),

        Align(
          alignment: Alignment.center,
          child: ValueListenableBuilder(
            valueListenable: widget.viewModel.playing, 
            builder: (context, value, child) {
              return Visibility(
                visible: !value,
                child: SizedBox(
                  height: 84,
                  width: 84,
                  child: IconButton(onPressed: (){
                    // 开始播放
                    widget.viewModel.startPlaying();
                  }, icon: Image(image: CommonUtil.assetImageNamed("render/render_play"), fit: BoxFit.fill,)),
                )
              );
            },
          )
        ),
      ],
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

  Widget _exportingView() {
    return Material(
      color: const Color.fromARGB(127, 0, 0, 0),
      child: Center(
        child: SizedBox(
          height: 150,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              SizedBox(
                width: 66,
                height: 66,
                child: ValueListenableBuilder(valueListenable: widget.viewModel.exportingProgress, builder: (context, value, child) {
                  return CircularProgressIndicator(
                    backgroundColor: const Color(0xFF111226),
                    color: const Color(0xFF111226),
                    valueColor: const AlwaysStoppedAnimation(Color(0xFF5EC7FE)),
                    value: value,
                    strokeWidth: 4,
                    strokeCap: StrokeCap.round,
                  );
                },)
              ),
              const Text("正在努力导出视频，请不要退出应用或锁屏", style: TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.w500),),
              TextButton(
                style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                  overlayColor: MaterialStatePropertyAll(Colors.transparent)
                ),
                onPressed: () {
                  // 停止导出 重新开始预览帧
                  widget.viewModel.stopExporting();
                  widget.viewModel.startPreviewing();
                  Navigator.pop(context);
                }, 
                child: Container(
                  width: 84,
                  height: 28,
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: Colors.white,
                      width: 1.0
                    ),
                    borderRadius: BorderRadius.circular(14.0)
                  ),
                  child: const Center(
                    child: Text("取消", style: TextStyle(color: Colors.white), textAlign: TextAlign.center,)
                  )
                )
              )
            ],
          ),
        ) 
      ),
    );
  }
}