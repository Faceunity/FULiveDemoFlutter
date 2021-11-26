import 'package:flutter/cupertino.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidget.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidgetArguments.dart';

class FUSticker extends StatefulWidget {
  static const routerName = '/FUStickerModuleArguments';
  @override
  _FUStickerState createState() => _FUStickerState();
}

class _FUStickerState extends State<FUSticker> {
  late final args;
  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    args = ModalRoute.of(context)!.settings.arguments as FUBaseWidgetArguments;
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        FUBaseWidget(
          model: args.model,
          selectedImagePath: args.selectedImagePath,
          child: Container(),
          viewClick: () {
            // _manager.changeBizType(FUBeautyDefine.FUBeautyMax);
            // //通知FUBaseWidget调整拍照按钮位置
            // adjustPhotoStream.sink.add(false);
          },
          backActionCallback: () {
            //todo: 一些清理FURenderKit的工作交给native去做
            // FUBeautyPlugin.beautyClean();
            // _manager.cacheData();
            //返回到上个页面
            Navigator.pop(context);
          },
          //进入美颜自定义相册
          jumpCumstomAlbum: () {
            // _manager.cacheData();
            // _manager.changeBizType(FUBeautyDefine.FUBeautyMax);
            // //调整相机按钮位置
            // adjustPhotoStream.sink.add(false);
            // print("离开当前页面");
            // FUBeautyPlugin.flutterWillDisappear();
            //美颜
            // Navigator.push(
            //   context,
            //   new MaterialPageRoute(
            //     builder: (context) => new FUSelectedImage(
            //         MainRouters.FULiveModelTypeBeautifyFace),
            //   ),
            // )..then((value) {
            //     print("回退到当前页面页面");
            //     FUBeautyPlugin.flutterWillAppear();

            //     ///返回时目的是刷新一下子FUBaseWidget，重新开启流式监听
            //     setState(() {});
            //   });
          },
          adjustPhotoStream: null,
          pointYMax: 0.6,
          pointYMin: 0.0,
        ),
      ],
    );
  }
}
