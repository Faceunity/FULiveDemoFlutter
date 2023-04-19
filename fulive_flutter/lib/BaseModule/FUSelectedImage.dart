import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Beauty/FUBeautyCustomStream.dart';
import 'package:fulive_flutter/Main/MainRouterDefine.dart';
import 'package:fulive_flutter/Sticker/FUStickerCustomStream.dart';
import 'package:fulive_plugin/fulive_plugin.dart';

typedef JumpToRenderCallback = Function(int type);

class FUSelectedImage extends StatelessWidget {
  FUSelectedImage(this.type, {this.data});

  final MainRouters? type;
  //部分widget 需要传数据进去，
  final dynamic data;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: new IconButton(
          icon: new Icon(Icons.arrow_back_ios),
          onPressed: () => {Navigator.of(context).pop()},
        ),
      ),
      backgroundColor: Color(0xFF310),
      body: Center(
        child: SelectedHome(
            this.type != null
                ? this.type!
                : MainRouters.FULiveModelTypeBeautifyFace,
            data: this.data != null ? this.data! : []),
      ),
    );
  }
}

class SelectedHome extends StatefulWidget {
  SelectedHome(this.modyleType, {this.data});
  final MainRouters modyleType;
  //部分widget 需要传数据进去，
  final dynamic data;

  @override
  _SelectedHomeState createState() => _SelectedHomeState();
}

class _SelectedHomeState extends State<SelectedHome> {
  _openImage() {
    FULivePlugin.chooseImageOrVideo(0);
  }

  _openVideo() {
    FULivePlugin.chooseImageOrVideo(1);
  }

  //监听native 选择图片或者视频的结果进行跳转
  Future listenNativeCustomImage(MethodCall call) async {
    print(call);
    if (call.method == "customSelectedImage") {
      var type = call.arguments["type"];
      switch (widget.modyleType) {
        case MainRouters.FULiveModelTypeBeautifyFace:
          //回调进入美颜
          Navigator.push(
            context,
            new MaterialPageRoute(
              builder: (context) => new FUBeautyCustomStream(type),
            ),
          )..then((value) => FULivePlugin.listenNative(
              listenNativeCustomImage)); //此处调用是因为Flutter 重新监听native 回调。
          break;
        case MainRouters.FULiveModelTypeItems:
          //贴纸
          Navigator.push(
            context,
            new MaterialPageRoute(
              builder: (context) => new FUStickerCustomStream(
                type,
                widget.data != null ? widget.data! : [],
                downLoadBtnHeight: 0.65,
              ),
            ),
          )..then(
              (value) => FULivePlugin.listenNative(listenNativeCustomImage));
          break;
        default:
      }
    }
  }

  @override
  void initState() {
    super.initState();
    FULivePlugin.listenNative(listenNativeCustomImage);
  }

  @override
  void dispose() {
    super.dispose();
    FULivePlugin.imagePickDispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: WillPopScope(
        onWillPop: () async {
          return false;
        },
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text("请从相册中选择图片或视频",
                style: TextStyle(color: Colors.white, fontSize: 16)),
            Container(
              height: 44,
            ),
            GestureDetector(
                onTap: () {
                  _openImage();
                },
                child: Container(
                  width: 200,
                  height: 48,
                  child: Stack(
                    alignment: AlignmentDirectional.center,
                    children: [
                      Image(
                        image:
                            AssetImage("resource/images/beauty/selectedBg.png"),
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Image(
                            image: AssetImage(
                                "resource/images/beauty/chooseAlbum.png"),
                          ),
                          Text("  选择图片",
                              style: TextStyle(
                                  color: Colors.white, fontSize: 18.0)),
                        ],
                      ),
                    ],
                  ),
                )),
            Container(
              height: 44,
            ),
            GestureDetector(
                onTap: () {
                  _openVideo();
                },
                child: Container(
                  width: 200,
                  height: 48,
                  child:
                      Stack(alignment: AlignmentDirectional.center, children: [
                    Image(
                      image:
                          AssetImage("resource/images/beauty/selectedBg.png"),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Image(
                          image: AssetImage(
                              "resource/images/beauty/chooseVideo.png"),
                        ),
                        Text("  选择视频",
                            style:
                                TextStyle(color: Colors.white, fontSize: 18.0)),
                      ],
                    ),
                  ]),
                )),
          ],
        ),
      ),
    );
  }
}
