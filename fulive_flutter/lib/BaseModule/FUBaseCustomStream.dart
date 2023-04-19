//自定义视频流的基础组件
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_plugin/FUFlutterEventChannel.dart';
import 'package:fulive_plugin/fulive_plugin.dart';

///自定义视频或者图片的页面 -> 渲染组件，可复用

class FUBaseCustomStream extends StatefulWidget {
  //0 图片， 1 视频
  final int type;
  final Widget? child; //底部的业务组件
  final Function? viewClick; //点击空白处的事件回调，不同业务可能有不同的处理逻辑
  //刷新底部控件回调
  FUBaseCustomStream(this.type, this.child, this.viewClick);
  @override
  _FUBaseCustomStreamState createState() => _FUBaseCustomStreamState();
}

class _FUBaseCustomStreamState extends State<FUBaseCustomStream> {
  @override
  Widget build(BuildContext context) {
    //隐藏顶部状态栏
    SystemChrome.setEnabledSystemUIOverlays([SystemUiOverlay.bottom]);
    return Scaffold(
      backgroundColor: Colors.black,
      body: Center(
        child: CustomStreamHome(widget.type, widget.child, widget.viewClick),
      ),
    );
  }
}

class CustomStreamHome extends StatefulWidget {
  //0 图片， 1 视频
  final int type;
  final Widget? child;
  final Function? viewClick;
  CustomStreamHome(this.type, this.child, this.viewClick);
  @override
  _CustomStreamHomeState createState() => _CustomStreamHomeState();
}

class _CustomStreamHomeState extends State<CustomStreamHome> {
  //检测是否有人脸
  bool _hasFace = false;

  ///监听native 流式数据通道
  late FUFlutterEventChannel channel;

  @override
  void initState() {
    super.initState();
    FULivePlugin.selectedImageOrVideo(widget.type);

    //开启流式通道
    FULivePlugin.startCustomRenderStremListen();
    channel = FUFlutterEventChannel(
        (message) => {
              setState(() {
                var jsonStr = message;
                Map<String, dynamic> par = json.decode(jsonStr);
                message = par["hasFace"];
                _hasFace = message;
              })
            },
        (Error error) => {});
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        return false;
      },
      child: SafeArea(
          child: Stack(
        children: [
          Platform.isIOS == true
              ? UiKitView(
                  viewType: 'CustomGLDisplayView',
                  creationParams: {"type": widget.type},
                  creationParamsCodec: const StandardMessageCodec())
              : AndroidView(
                  viewType: 'CustomGLDisplayView',
                  creationParams: {"type": widget.type},
                  creationParamsCodec: const StandardMessageCodec()),
          GestureDetector(
            onTap: () {
              // ignore: unnecessary_statements
              widget.viewClick != null ? widget.viewClick!() : null;
            },
          ),
          Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Align(
                alignment: Alignment.topLeft,
                child: IconButton(
                  icon: new Icon(
                    Icons.arrow_back_ios,
                    color: Colors.white,
                  ),
                  onPressed: () => {Navigator.of(context).pop()},
                ),
              ),
            ],
          ),
          Container(child: widget.child),
          Visibility(
              visible: !_hasFace,
              child: Align(
                  alignment: Alignment.center,
                  child: Text(
                    "未检测到人脸",
                    style: TextStyle(color: Colors.white, fontSize: 17.0),
                  ))),
        ],
      )),
    );
  }

  @override
  void dispose() {
    super.dispose();
    channel.cancel();
  }
}
