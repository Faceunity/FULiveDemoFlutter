import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulivedemo_flutter/classes/beauty/beauty_image_render.dart';
import 'package:fulivedemo_flutter/classes/beauty/beauty_video_render.dart';
import 'package:fulivedemo_flutter/classes/sticker/sticker_image_render.dart';
import 'package:fulivedemo_flutter/classes/sticker/sticker_video_render.dart';
import 'package:fulivedemo_flutter/common/custom_page_route.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class MediaPicker extends StatefulWidget {

  const MediaPicker({super.key, required this.module});

  final Module module;

  @override
  State<StatefulWidget> createState() {
    return MediaPickerState();
  }
}

class MediaPickerState extends State<MediaPicker> {
  @override
  void initState() {
    // 监听从原生相册选择图片和视频回调
    FaceunityPlugin.requestAlbumCallBack((MethodCall call) {
      _analysisCallBack(call);    
    });
    super.initState();
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        leading: IconButton(
          highlightColor: Colors.transparent,
          splashColor: Colors.transparent,
          hoverColor: Colors.transparent,
          onPressed: (){
            Navigator.pop(context);
          },
          icon: Image(image: CommonUtil.assetImageNamed("common/back"))),
      ), 
      backgroundColor: Colors.black,
      body: _body(context),
    );
  }

  Widget _body(BuildContext context) {
    double screenHeight = ScreenUtil.getInstance().screenHeight;
    return Stack(
      alignment: Alignment.center,
      children: [
        Positioned(
          top: screenHeight / 2.0 - 18,
          child: ElevatedButton(
            onPressed: (){
              FaceunityPlugin.requestAlbumForType(1);
            }, 
            style: const ButtonStyle(
              backgroundColor: MaterialStatePropertyAll(Colors.transparent),
              overlayColor: MaterialStatePropertyAll(Colors.transparent)
            ),
            child: Stack(
              alignment: AlignmentDirectional.center,
              children: [
                Image(image: CommonUtil.assetImageNamed("common/selectedBg")),
                Wrap(
                  spacing: 5,
                  children: [
                    Image(image: CommonUtil.assetImageNamed("common/selector_video")),
                    const Text("选择视频", style: TextStyle(color: Colors.white, fontSize: 17, fontWeight: FontWeight.w500),)
                  ],
                )
              ],
            )
          )
        ),

        Positioned(
          top: screenHeight / 2.0 - 108,
          child: ElevatedButton(
            onPressed: (){
              FaceunityPlugin.requestAlbumForType(0);
            }, 
            style: const ButtonStyle(
              backgroundColor: MaterialStatePropertyAll(Colors.transparent),
              overlayColor: MaterialStatePropertyAll(Colors.transparent)
            ),
            child: Stack(
              alignment: AlignmentDirectional.center,
              children: [
                Image(image: CommonUtil.assetImageNamed("common/selectedBg")),
                Wrap(
                  spacing: 5,
                  children: [
                    Image(image: CommonUtil.assetImageNamed("common/selector_picture")),
                    const Text("选择图片", style: TextStyle(color: Colors.white, fontSize: 17, fontWeight: FontWeight.w500),)
                  ],
                )
              ],
            )
          )
        ),

        Positioned(
          top: screenHeight / 2.0 - 208,
          child: const Center(
            child: Text("请从相册中选择图片或视频", style: TextStyle(color: Colors.white, fontSize: 17),),
         ),
        ),

      ],
    );
  }

  void _analysisCallBack(MethodCall call) {
    if(call.method == "photoSelected") {
      bool result = call.arguments as bool;
      if (result) {
        Navigator.of(context).push(CustomPageRoute(builder: (context) {
          switch (widget.module) {
            case Module.beauty:
              return const BeautyImageRender();
            case Module.sticker:
              return const StickerImageRender();
            default:
              return Container();
          }
        },)).then((value){
          // 监听从原生相册选择图片和视频回调
          FaceunityPlugin.requestAlbumCallBack((MethodCall call) {
            _analysisCallBack(call);    
          });
        });
      }
    } else if(call.method == "videoSelected") {
      bool result = call.arguments as bool;
      if (result) {
        Navigator.of(context).push(CustomPageRoute(builder: (context) {
          switch (widget.module) {
            case Module.beauty:
              return const BeautyVideoRender();
            case Module.sticker:
              return const StickerVideoRender();
            default:
              return Container();
          }
          
        },)).then((value){
          // 监听从原生相册选择图片和视频回调
          FaceunityPlugin.requestAlbumCallBack((MethodCall call) {
            _analysisCallBack(call);    
          });
        });
      }
    }
  }

}