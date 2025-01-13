import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/business/render/image_render_view_model.dart';
import 'package:fulivedemo_flutter/common/display_view.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';

class ImageRenderView extends StatefulWidget {

  final ImageRenderViewModel viewModel;

  final Function? backAction;

  const ImageRenderView({super.key, required this.viewModel, this.backAction});

  @override
  State<StatefulWidget> createState() {
    return ImageRenderState();
  }
}

class ImageRenderState extends State<ImageRenderView> {
  @override
  void initState() {
    FaceunityPlugin.setFaceProcessorDetectMode(0);
    RenderPlugin.startImageRender();
    super.initState();
  }

  @override
  void dispose() {
    widget.viewModel.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: Alignment.topCenter,
      children: [
        const DisplayView(identifier: "image_render"),
        Positioned(
          left: 5,
          child: SafeArea(
            child: IconButton(
              splashColor: Colors.transparent,
              highlightColor: Colors.transparent,
              hoverColor: Colors.transparent,
              onPressed: () {
                RenderPlugin.stopImageRender();
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
            onTap: () async {
              RenderPlugin.captureImage((MethodCall call) {
                if(call.method == "captureImageResult") {
                  bool result = call.arguments as bool;
                  showCommonToast(context: context, content: result ? "图片已保存到相册" : "保存图片失败");
                }
              });
            },
            child: Container(
              width: 84,
              height: 84,
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(42.0)
              ),
              child: Image(image: CommonUtil.assetImageNamed("render/render_save")),
            ),
          ),
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
}