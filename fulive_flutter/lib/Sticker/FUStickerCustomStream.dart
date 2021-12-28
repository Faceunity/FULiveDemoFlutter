import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/BaseModule/FUBaseCustomStream.dart';
import 'package:fulive_flutter/Sticker/FUStickerManager.dart';
import 'package:fulive_flutter/Sticker/Model/FUStickerModel.dart';
import 'package:fulive_flutter/Sticker/StickerToolsWidget.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:provider/provider.dart';

class FUStickerCustomStream extends StatefulWidget {
  FUStickerCustomStream(this.type, this.dataList, {this.downLoadBtnHeight});

  //外部组件控制里面的下载按钮高度，不传就默认0.75.具体看代码
  final double? downLoadBtnHeight;
  //0 图片， 1 视频
  final int type;
  final dynamic dataList;

  @override
  _FUStickerCustomStreamState createState() => _FUStickerCustomStreamState();
}

class _FUStickerCustomStreamState extends State<FUStickerCustomStream> {
  late FUStickerManager _manager;
  //如果播放视频，默认是正在播放，等待native 通知结果
  bool _isVideoPlay = false;

  //默认显示下载按钮
  late bool _downloadBtnStatus;

  void changeVideoPlayBtn(bool isPlay) {
    setState(() {
      _isVideoPlay = isPlay;
    });
  }

  void changeDownLoadBtnStatus(bool isShow) {
    setState(() {
      _downloadBtnStatus = isShow;
    });
  }

  //监听native 视频播放结果
  Future listenNativeVideoPlay(MethodCall call) async {
    print(call);
    if (call.method == "videoPlay") {
      bool isPlay = call.arguments["isPlay"];
      changeVideoPlayBtn(isPlay);
      if (isPlay) {
        changeDownLoadBtnStatus(false);
      } else {
        //播放结束时
        changeDownLoadBtnStatus(true);
      }
    }
  }

  @override
  void initState() {
    super.initState();
    // FUBeautyPlugin.configBeauty();
    List<FUStickerModel> dataList = widget.dataList as List<FUStickerModel>;
    _manager = FUStickerManager(stickerModels: dataList);
    //设置初值
    _manager.selectedDefault();
    FULivePlugin.requestVideoProcess();

    _downloadBtnStatus = widget.type == 0 ? true : false;

    //监听native 视频播放回调
    FULivePlugin.listenNative(listenNativeVideoPlay);
  }

  @override
  void dispose() {
    super.dispose();
    FULivePlugin.customImageDispose();
    // _manager.cacheData();
  }

  @override
  Widget build(BuildContext context) {
    //隐藏顶部状态栏
    SystemChrome.setEnabledSystemUIOverlays([SystemUiOverlay.bottom]);
    return ChangeNotifierProvider(
        create: (context) => _manager,
        child: Stack(
          children: [
            Container(
              color: Colors.black,
            ),
            FUBaseCustomStream(widget.type, StickerToolsWidget(widget.dataList),
                () {
              // _manager.changeBizType(FUBeautyDefine.FUBeautyMax);
              //视频正在播放的时候点击屏幕空白处 不显示下载按钮
              if (widget.type == 1) {
                changeDownLoadBtnStatus(!_isVideoPlay);
              } else {
                //图片状态根据底部控件是否展开来确定下载按钮是否隐藏
                changeDownLoadBtnStatus(true);
              }
            }),
            Align(
              alignment: Alignment.center,
              child: Visibility(
                  visible: widget.type == 0 ? false : !_isVideoPlay,
                  child: Container(
                      child: TextButton(
                          onPressed: () {
                            FULivePlugin.customVideoRePlay();
                            changeVideoPlayBtn(true);
                            //重播隐藏下载按钮，播完之后是否显示 根据之前的下载按钮状态来判定
                            changeDownLoadBtnStatus(false);
                          },
                          child: Image(
                              image: AssetImage(
                                  "resource/images/commonImage/Replay_icon.png"))))),
            ),
            Align(
              alignment: Alignment(
                  0.0,
                  widget.downLoadBtnHeight != null
                      ? widget.downLoadBtnHeight!
                      : 0.75),
              child: Visibility(
                  visible: _downloadBtnStatus,
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(85.0 / 2),
                    child: Container(
                        color: Colors.white,
                        child: TextButton(
                            onPressed: () {
                              FULivePlugin.downLoadCustomRender(widget.type);
                              if (widget.type == 1) {
                                changeDownLoadBtnStatus(false);
                              }
                            },
                            child: Image(
                                image: AssetImage(
                                    "resource/images/commonImage/demo_icon_save.png")))),
                  )),
            )
          ],
        ));
  }
}
