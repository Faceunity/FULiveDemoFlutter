import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/BaseModule/FUBaseCustomStream.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:provider/provider.dart';
import 'BeautyToolsWidget.dart';
import 'FUBeautyDataManager.dart';

///自定义视频或者图片的页面 ->业务组件，针对不同业务有不同的逻辑

class FUBeautyCustomStream extends StatefulWidget {
  //0 图片， 1 视频
  final int type;

  FUBeautyCustomStream(this.type);
  @override
  _FUBeautyCustomStreamState createState() => _FUBeautyCustomStreamState();
}

class _FUBeautyCustomStreamState extends State<FUBeautyCustomStream> {
  @override
  Widget build(BuildContext context) {
    //隐藏顶部状态栏
    SystemChrome.setEnabledSystemUIOverlays([SystemUiOverlay.bottom]);
    return CustomStreamHome(widget.type);
  }
}

class CustomStreamHome extends StatefulWidget {
  //0 图片， 1 视频
  final int type;
  CustomStreamHome(this.type);
  @override
  _CustomStreamHomeState createState() => _CustomStreamHomeState();
}

class _CustomStreamHomeState extends State<CustomStreamHome> {
  late FUBeautifyDataManager _manager;

  //底部美颜UI是展开还是隐藏
  bool _bottomStatus = false;

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
        //正在播放时
        changeDownLoadBtnStatus(false);
      } else {
        //播放结束时
        //状态和底部美颜UI展开关闭 互逆
        changeDownLoadBtnStatus(!_bottomStatus);
      }
    }
  }

  @override
  void initState() {
    super.initState();
    // FUBeautyPlugin.configBeauty();
    _manager = FUBeautifyDataManager();

    FULivePlugin.requestVideoProcess();

    _downloadBtnStatus = widget.type == 0 ? true : false;
  }

  @override
  void dispose() {
    super.dispose();
    FULivePlugin.customImageDispose();
    _manager.cacheData();
  }

  @override
  Widget build(BuildContext context) {
    //监听native 视频播放回调
    FULivePlugin.listenNative(listenNativeVideoPlay);
    //底部美颜组件
    Widget child = BeautyToolsWidget(
        showFilterTips: false,
        bizType: _manager.curBizType,
        dataList: _manager.dataList,
        compareCallback: (bool compare) =>
            FULivePlugin.customRenderOrigin(compare),
        clickItemCallback: (bool flag) {
          _bottomStatus = flag;
          //flag == false 标识底部栏是隐藏的 显示下载按钮，true 表示出现，隐藏下载按钮
          if (_isVideoPlay) {
            changeDownLoadBtnStatus(false);
          } else {
            changeDownLoadBtnStatus(!flag);
          }
        });
    return ChangeNotifierProvider(
      create: (context) => _manager,
      child: Stack(
        children: [
          Container(
            color: Colors.black,
          ),
          FUBaseCustomStream(widget.type, child, () {
            _manager.changeBizType(FUBeautyDefine.FUBeautyMax);
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
            alignment: Alignment(0.0, 0.75),
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
      ),
    );
  }
}
