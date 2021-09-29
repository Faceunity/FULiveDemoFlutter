import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Main/MainRouterDefine.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulive_flutter/Beauty/FUBeautyCustomStream.dart';

// //路由传参，属于公共基类里面的参数，所以用这个名字
// class SelectedArguments {
//   final MainRouters? type;

//   SelectedArguments(this.type);
// }

class FUSelectedImage extends StatelessWidget {
  // static const routerName = '/FUSelectedImage';
  final MainRouters? type;
  FUSelectedImage(this.type);

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
        child: SelectedHome(),
      ),
    );
  }
}

class SelectedHome extends StatefulWidget {
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
//美颜
      Navigator.push(
        context,
        new MaterialPageRoute(
          builder: (context) => new FUBeautyCustomStream(type),
        ),
      )..then((value) {
          setState(() {});
        });
    }
  }

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    FULivePlugin.listenNative(listenNativeCustomImage);
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
            ClipRRect(
                borderRadius: BorderRadius.circular(15.0),
                child: GestureDetector(
                    onTap: () {
                      _openImage();
                    },
                    child: Container(
                      width: 200,
                      height: 48,
                      color: Colors.purple,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Image(
                            image: AssetImage(
                                "resource/images/beauty/chooseAlbum.png"),
                          ),
                          Text("选择图片",
                              style: TextStyle(
                                  color: Colors.white, fontSize: 18.0)),
                        ],
                      ),
                    ))),
            Container(
              height: 44,
            ),
            ClipRRect(
                borderRadius: BorderRadius.circular(15.0),
                child: GestureDetector(
                    onTap: () {
                      _openVideo();
                    },
                    child: Container(
                      width: 200,
                      height: 48,
                      color: Colors.purple,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Image(
                            image: AssetImage(
                                "resource/images/beauty/chooseVideo.png"),
                          ),
                          Text("选择视频",
                              style: TextStyle(
                                  color: Colors.white, fontSize: 18.0)),
                        ],
                      ),
                    ))),
          ],
        ),
      ),
    );
  }
}
