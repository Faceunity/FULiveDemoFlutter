import 'dart:async';
import 'dart:io';

import 'package:fulive_plugin/FUFlutterEventChannel.dart';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulive_flutter/Main/MainCellModel.dart';
import 'package:fulive_flutter/BaseModule/FUPopupMenu.dart';
import 'dart:ui';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:provider/provider.dart';

import 'FUBaseStreamManager.dart';
import 'FUCircleInIndicator.dart';

// ignore: must_be_immutable
class FUBaseWidget extends StatefulWidget {
  FUBaseWidget({
    this.neverShowCustomAlbum = false,
    Key? key,
    this.model,
    this.selectedImagePath,
    this.child,
    this.viewClick,
    this.backActionCallback,
    this.jumpCumstomAlbum,
    this.adjustPhotoStream,
    this.prohibitStream,
    this.pointYMax,
    this.pointYMin,
    this.phototAction,
  }) : super(key: key);

  MainCellModel? model;
  String? selectedImagePath;
  final Widget? child;
  // bool late show
  //整个视图被点击
  final Function? viewClick;

  ///返回按钮回调，抛到外面让具体的业务模块选择怎么返回。模拟iOS 上子类继承父类方法然后在扩展。
  late Function? backActionCallback = () => {};

  ///进入自定义相机回调
  final Function? jumpCumstomAlbum;

  //外部组件控制是否调整相机位置的通知
  final StreamController? adjustPhotoStream;

  //订阅外部是否禁止录屏按钮通知
  final StreamController? prohibitStream;

  //点击拍照或者录屏action
  final Function? phototAction;

  //相机的Y轴位偏移的最大位置
  late double? pointYMax;
  //相机的Y轴位偏移的最小位置
  late double? pointYMin;

//部分业务不需要展示CustomAlbum，点击自定义视频/相册 直接跳转
  late bool? neverShowCustomAlbum;

  @override
  FUBaseWidgetState createState() => FUBaseWidgetState();
}

class FUBaseWidgetState extends State<FUBaseWidget> {
  bool _showSpolit = false;

  ///监听native 流式数据通道
  late FUFlutterEventChannel channel;

  ///点击屏幕主动隐藏子组件进入相册UI用
  late StreamController streamEvent;

  //定位拍照按钮位置, false 放在底部，true 弹起来接近中心位置Y轴偏下
  late bool _positionFUCircleInIndicator = false;

  //拍照按钮是否响应事件
  late bool _prohibitFUCircleEnable = false;

  //订阅调整拍照按钮流事件信号
  late StreamSubscription? _adjustTakeSubscription;

  //订阅是否禁止录屏按钮工作
  // ignore: cancel_subscriptions
  late StreamSubscription? _prohibitSubscription;

  late FUBaseStreamManager _streamManager;

  //刷新检测人脸数据流
  void reloadStream() {
    _streamManager.startStream();
  }

  void _changeFUCircleInIndicator(bool state) {
    setState(() {
      _positionFUCircleInIndicator = state;
    });
  }

  //禁止录屏按钮工作
  void _prohibitIndicator(bool state) {
    setState(() {
      _prohibitFUCircleEnable = state;
    });
  }

  int lastTime = 0;
  double lastOffsetY = 0.0;

  double _dx = 0.0;
  double _dy = 0.0;

  @override
  void initState() {
    super.initState();

    _streamManager = FUBaseStreamManager();

    streamEvent = StreamController.broadcast();
    if (widget.adjustPhotoStream != null) {
      _adjustTakeSubscription =
          widget.adjustPhotoStream!.stream.listen((event) {
        _changeFUCircleInIndicator(event);
      });
    } else {
      _adjustTakeSubscription = null;
    }

    if (widget.prohibitStream != null) {
      _prohibitSubscription = widget.prohibitStream!.stream.listen((event) {
        print("禁止录屏event:$event");
        _prohibitIndicator(event);
      });
    } else {
      _prohibitSubscription = null;
    }
  }

  @override
  void dispose() {
    super.dispose();
    _streamManager.cancel();
    streamEvent.close();
    if (_adjustTakeSubscription != null) {
      _adjustTakeSubscription!.cancel();
    }

    if (_prohibitSubscription != null) {
      _prohibitSubscription!.cancel();
    }
    FULivePlugin.disposeCommon();
  }

  @override
  void didUpdateWidget(covariant FUBaseWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    //父组件更新子组件时 重新建立通道，之前通道已经被关闭
    _streamManager.startStream();
  }

  void didChangeDependencie() {
    super.didChangeDependencies();

    print("didChangeDependencies");
  }

  void delayHiddenSpolit(int duration) {
    _showSpolit = true;
    Future.delayed(Duration(milliseconds: duration), () {
      if (mounted) {
        //防止widget 从节点树上移除，延时之后还会设置setstate方法导致内存泄漏
        int nowTime = DateTime.now().millisecondsSinceEpoch;
        setState(() {
          if (nowTime - lastTime > duration) {
            _showSpolit = false;
          } else {
            _showSpolit = true;
          }
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    //隐藏顶部状态栏
    SystemChrome.setEnabledSystemUIOverlays([SystemUiOverlay.bottom]);

    return Listener(
      onPointerDown: (PointerDownEvent down) {
        //通知子组件隐藏FUPopupMenu
        streamEvent.sink.add(down);
      },
      child: Scaffold(
          backgroundColor: Colors.black,
          body: WillPopScope(
            onWillPop: () async {
              return false;
            },
            child: SafeArea(
                child: ChangeNotifierProvider(
              create: (context) => _streamManager,
              child: Stack(
                children: [
                  Platform.isIOS == true
                      ? UiKitView(
                          viewType: 'OpenGLDisplayView',
                        )
                      : AndroidView(
                          viewType: 'OpenGLDisplayView',
                        ),
                  GestureDetector(
                    onTapDown: (detail) {
                      double dx = detail.globalPosition.dx;
                      double dy = detail.globalPosition.dy;
                      print("globalPosition.dx = $dx, globalPosition.dy = $dy");
                      FULivePlugin.manualExpose(dx, dy);
                      setState(() {
                        _dx = dx;
                        _dy = dy;
                      });
                    },
                    onTap: () {
                      if (widget.viewClick != null) {
                        widget.viewClick!(); //收起底部栏
                      }

                      lastTime = DateTime.now().millisecondsSinceEpoch;
                      setState(() {
                        delayHiddenSpolit(1000);
                      });

                      // //通知子组件隐藏FUPopupMenu
                      // streamEvent.sink.add("hiddenFUPopupMenu");
                    },
                    child: Container(
                      height: double.infinity,
                      width: double.infinity,
                      color: Color(0x00000000),
                    ),
                  ),
                  Consumer<FUBaseStreamManager>(
                      builder: (context, stream, child) {
                    return Visibility(
                      visible: stream.showDebugInfo,
                      child: Align(
                          //debug信息
                          alignment: Alignment(-0.9, -0.75),
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(5.0),
                            child: Container(
                              alignment: Alignment.center,
                              width: 95,
                              height: 100,
                              color: Color(0xBC4c4c4c),
                              child: Text(
                                stream.debugStr,
                                style: TextStyle(color: Colors.white),
                              ),
                            ),
                          )),
                    );
                  }),

                  Consumer<FUBaseStreamManager>(
                      builder: (context, stream, child) {
                    return Visibility(
                        visible: !stream.hasFace,
                        child: Align(
                            alignment: Alignment.center,
                            child: Text(
                              "未检测到人脸",
                              style: TextStyle(
                                  color: Colors.white, fontSize: 17.0),
                            )));
                  }),

                  Column(mainAxisAlignment: MainAxisAlignment.start, children: [
                    Container(
                        padding: EdgeInsets.fromLTRB(0, 20, 0, 0),
                        child: FUToolBar(widget.neverShowCustomAlbum!,
                            //顶部工具栏组件
                            selectedImagePath: widget.selectedImagePath,
                            backActionCallback: widget.backActionCallback,
                            jumpCumstomAlbum: () {
                              if (widget.jumpCumstomAlbum != null) {
                                widget.jumpCumstomAlbum!();
                              }
                              _streamManager.cancel();
                            },
                            debugCallback: (bool show) =>
                                _streamManager.setShowDebugInfo(show),
                            streamEvent: streamEvent)),
                  ]),
                  Align(
                    alignment: Alignment(
                        0.0,
                        _positionFUCircleInIndicator == true
                            ? widget.pointYMin!
                            : widget.pointYMax!),
                    child: AbsorbPointer(
                      absorbing: _prohibitFUCircleEnable,
                      child: FUCircleInIndicator(
                        takePhoto: () => FULivePlugin.takePhoto(),
                        startRecord: () {
                          if (widget.phototAction != null) {
                            widget.phototAction!(true);
                          }
                          FULivePlugin.startRecord();
                        },
                        stopRecord: () {
                          if (widget.phototAction != null) {
                            widget.phototAction!(false);
                          }
                          FULivePlugin.stopRecord();
                        },
                      ),
                    ),
                  ),

                  widget.child != null ? widget.child! : Container(),

                  ///聚光调节UI
                  Visibility(
                      visible: _showSpolit,
                      child: Align(
                          alignment: Alignment(1.0, -0.2),
                          child: SpotlightUI(
                              true,
                              () {
                                lastTime =
                                    DateTime.now().millisecondsSinceEpoch;
                                delayHiddenSpolit(1300);
                              },
                              lastOffsetY,
                              touchPanEndCallback: (double offsetY) {
                                //更新最新的y偏移量，方便下次创建组件时候在传入来记录上次的位置
                                lastOffsetY = offsetY;
                              }))),
                  Visibility(visible: _showSpolit, child: Calibrate(_dx, _dy)),
                ],
              ),
            )),
          )

          // onTap: () => widget.callback!(),
          ),
    );
  }
}

// ignore: must_be_immutable
class FUToolBar extends StatefulWidget {
  ///返回按钮回调
  final Function? backActionCallback;
  final Function? debugCallback;

  ///进入自定义相机回调
  final Function? jumpCumstomAlbum;

  final StreamController? streamEvent;

  late String? selectedImagePath;

  //部分业务不需要展示CustomAlbum，点击自定义视频/相册 直接跳转
  late bool neverShowCustomAlbum = false;

  FUToolBar(
    this.neverShowCustomAlbum, {
    this.selectedImagePath,
    this.backActionCallback,
    this.jumpCumstomAlbum,
    this.debugCallback,
    this.streamEvent,
  });
  @override
  _FUToolBarState createState() => _FUToolBarState();
}

class _FUToolBarState extends State<FUToolBar> {
  String _segmentValue = 'BGRA';

  //相机按钮前后置选中状态， 前置，false，后置：true
  bool cameraSeleted = true;
  bool _showDebugInfo = false;
  //是否展示自定义相册组件
  bool _showCustomPhotograph = false;

  late StreamSubscription _streamSubscription;

  final screenWidth = window.physicalSize.width / window.devicePixelRatio;
  final screenHeight = window.physicalSize.height / window.devicePixelRatio;

  static GlobalKey _popKey = GlobalKey();
  static GlobalKey _selecetdImageOffKey = GlobalKey();

  //记录子组件选中的相机分辨率格式
  late int _format;

  @override
  void initState() {
    super.initState();
    _format = 1;
    _streamSubscription = widget.streamEvent!.stream.listen((event) {
      //部分业务不需要展示CustomAlbum，点击自定义视频/相册 直接跳转
      if (widget.neverShowCustomAlbum == true) {
        _showCustomPhotograph = false;
        return;
      }
      if (event is PointerDownEvent) {
        //通过坐标点转换确定当前点击区域是否在FUPopupMenu上
        RenderBox? object =
            _popKey.currentContext?.findRenderObject() as RenderBox?;
        if (object != null) {
          Rect? rect = object.paintBounds;
          Offset? local = object.globalToLocal(event.position);
          if (!rect.contains(local)) {
            if (_showCustomPhotograph == true) {
              setState(() {
                _showCustomPhotograph = false;
              });
            }
          }
        } else {
          //这个是选择照片组件本身按钮
          RenderBox? object = _selecetdImageOffKey.currentContext
              ?.findRenderObject() as RenderBox?;
          if (object != null) {
            Rect? rect = object.paintBounds;
            Offset? local = object.globalToLocal(event.position);
            if (rect.contains(local)) {
              setState(() {
                _showCustomPhotograph = !_showCustomPhotograph;
              });
            }
          }
        }
      }
    });
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    //selectedImagePath 有值显示对应的功能图标按钮，没有值显示一个占位Container
    return Column(children: [
      Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          IconButton(
            onPressed: () {
              if (widget.backActionCallback != null) {
                widget.backActionCallback!();
              }
            },
            icon: Image(
              image: AssetImage("resource/images/commonImage/homeIcon.png"),
            ),
          ),
          Container(
            child: CupertinoSegmentedControl(
                padding: EdgeInsets.all(0),
                children: {
                  'BGRA': Container(
                    child: Text(
                      'BGRA',
                      style: TextStyle(color: Colors.black),
                    ),
                  ),
                  'YUV': Container(
                    child: Text(
                      'YUV',
                      style: TextStyle(color: Colors.black),
                    ),
                  ),
                },
                groupValue: _segmentValue,
                unselectedColor: Color(0x0),
                selectedColor: Colors.white,
                borderColor: Color(0x0),
                pressedColor: Color(0x0),
                onValueChanged: (value) {
                  setState(() {
                    _segmentValue = value as String;
                    FULivePlugin.changeCameraFormat();
                  });
                }),
            color: Color(0x10FFFFFF),
          ),
          widget.selectedImagePath != null
              ? IconButton(
                  key: _selecetdImageOffKey,
                  onPressed: () {
                    if (widget.neverShowCustomAlbum) {
                      if (widget.jumpCumstomAlbum != null) {
                        widget.jumpCumstomAlbum!();
                      }
                    } else {
                      setState(() {
                        ///全局hook 手势点击事件，针对隐藏还是显示这个组件，所以这个组件本身的逻辑要关联_streamSubscription 的流式状态
                        ///_streamSubscription负责隐藏这个组件隐藏和显示
                        // _showCustomPhotograph = !_showCustomPhotograph;
                        //不管debug显不显示，都关掉
                        _showDebugInfo = false;
                      });
                      if (widget.debugCallback != null) {
                        widget.debugCallback!(_showDebugInfo);
                      }
                    }
                  },
                  icon: Image(
                    image: AssetImage(widget.selectedImagePath!),
                  ),
                )
              : SizedBox(
                  child: Container(
                    padding: EdgeInsets.fromLTRB(10, 0, 0, 0),
                  ),
                ),

          /// debug
          IconButton(
            onPressed: () {
              _showDebugInfo = !_showDebugInfo;
              if (widget.debugCallback != null) {
                widget.debugCallback!(_showDebugInfo);
              }
              setState(() {
                //不管自定义相册显不显示，都关掉
                _showCustomPhotograph = false;
              });
            },
            icon: Image(
              image: AssetImage("resource/images/commonImage/bugly.png"),
            ),
          ),
          IconButton(
            onPressed: () {
              cameraSeleted = !cameraSeleted;
              FULivePlugin.changeCameraFront(cameraSeleted);
            },
            icon: Image(
              image: AssetImage("resource/images/commonImage/cameraIcon.png"),
            ),
          )
        ],
      ),
      Visibility(
        visible: _showCustomPhotograph,
        child: Container(
          width: screenWidth - 30,
          height: 150,
          child: FUPopupMenu(
            _popKey,
            points: [
              FUPopPosition((screenWidth - 30) * 0.5, 10),
              FUPopPosition((screenWidth - 30) * 0.5 + 10, 0),
              FUPopPosition((screenWidth - 30) * 0.5 + 20, 10)
            ],
            bgColor: Color(0xA0000000),
            offsetY: 10.0,
            format: _format,
            foramtCallback: (int format) {
              _format = format;
              Future<int?> ret = FULivePlugin.chooseSessionPreset(format);
              // ignore: unrelated_type_equality_checks
              if (ret == 1) {
                //展示toast

                Fluttertoast.showToast(
                  msg: "摄像机不支持",
                  gravity: ToastGravity.CENTER,
                );
              }
            },
            jumpCumstomCallback: () {
              if (widget.jumpCumstomAlbum != null) {
                widget.jumpCumstomAlbum!();
              }
            },
            key: _popKey,
          ),
        ),
      )
    ]);
  }
}

//手动校准组件
// ignore: must_be_immutable
class Calibrate extends StatefulWidget {
  late double dx = 0.0;
  late double dy = 0.0;
  Calibrate(this.dx, this.dy);

  @override
  _CalibrateState createState() => new _CalibrateState();
}

class _CalibrateState extends State<Calibrate>
    with SingleTickerProviderStateMixin {
  late Animation<double> animation;
  late AnimationController controller;

  @override
  void initState() {
    super.initState();
    controller = AnimationController(
        duration: const Duration(milliseconds: 300), vsync: this);
    animation = Tween(begin: 0.0, end: 0.67 * 77).animate(controller)
      ..addListener(() {
        setState(() {});
      });
    controller.forward();
  }

  ///上层树变化，重新执行动画
  void didChangeDependencies() {
    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    return Positioned(
        left: widget.dx,
        top: widget.dy,
        child: Image(
          width: animation.value,
          height: animation.value,
          image: AssetImage("resource/images/commonImage/calibrate.png"),
        ));
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}

///聚光条
// ignore: must_be_immutable
class SpotlightUI extends StatefulWidget {
  bool delayHidden = false;
  int delayDuration;
  double lastOffsetY;
  //进度条滑动回调
  Function sliderValueChangeCallback = () => {};
  //进度条滑松手回调
  Function? touchPanEndCallback = (double offsetY) => {};
  SpotlightUI(
      this.delayHidden, this.sliderValueChangeCallback, this.lastOffsetY,
      {this.delayDuration = 1000, this.touchPanEndCallback});
  @override
  _SpotlightUIState createState() => _SpotlightUIState();
}

class _SpotlightUIState extends State<SpotlightUI> {
  void sliderCallback(double newValue) {
    FULivePlugin.adjustSpotlight(newValue);
    widget.sliderValueChangeCallback();
  }

  @override
  Widget build(BuildContext context) {
    return Visibility(
        visible: widget.delayHidden,
        child: Container(
          padding: EdgeInsets.fromLTRB(0, 0, 20, 0),
          height: 280,
          width: 40,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Image(
                image:
                    AssetImage("resource/images/commonImage/spotlight_sun.png"),
              ),
              Stack(
                alignment: AlignmentDirectional.center,
                children: [
                  Container(
                    height: 200.0,
                    width: 3.0,
                    color: Colors.white,
                  ),
                  Container(
                    height: 2.0,
                    width: 15,
                    color: Colors.white,
                  ),
                  Container(
                    height: 200,
                    child: CustomSlider(widget.lastOffsetY, sliderCallback,
                        widget.touchPanEndCallback),
                  ),
                ],
              ),
              Image(
                image: AssetImage(
                    "resource/images/commonImage/spotlight_moon.png"),
              ),
            ],
          ),
        ));
  }
}

// ignore: must_be_immutable
class CustomSlider extends StatefulWidget {
  double defaultOffsetY = 0.0;

  ///更新sliderValue值
  Function sliderUpdateCallback = (double value) => {};

  ///手势结束回掉
  Function? sliderPanEndCallback = () => {};
  CustomSlider(this.defaultOffsetY, this.sliderUpdateCallback,
      this.sliderPanEndCallback);
  @override
  _CustomSliderState createState() => _CustomSliderState();
}

class _CustomSliderState extends State<CustomSlider> {
  double _offsetY = 0.0;

  void _changeValue(detail) {
    setState(() {
      _offsetY += detail.delta.dy;
      double spotlitValue;
      if (_offsetY > 95) {
        _offsetY = 95;
      } else if (_offsetY < -95) {
        _offsetY = -95;
      }

      /// [-2. 2] 是FURenderKit 对应的接口最小/大参数值
      spotlitValue = -_offsetY * 2 / 95.0;
      //更新slider 值回掉到父组件 和native 通信
      widget.sliderUpdateCallback(spotlitValue);
    });
  }

  @override

  ///用来记录上次oldWidget的最后offsetY。
  void didChangeDependencies() {
    super.didChangeDependencies();
    _offsetY = widget.defaultOffsetY;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 280,
      height: 40,
      color: Colors.green.withAlpha(11),
      child: CustomSingleChildLayout(
          delegate: _customChildDelegate(_offsetY),
          child: Stack(
            children: [
              Container(),
              GestureDetector(
                  child: Align(
                    alignment: Alignment.center,
                    child: Image(
                      image: AssetImage(
                          "resource/images/commonImage/spotlightIcon.png"),
                    ),
                  ),
                  onPanEnd: (detail) {
                    widget.sliderPanEndCallback!(_offsetY);
                  },
                  onPanUpdate: (detail) => _changeValue(detail)),
            ],
          )),
    );
  }
}

// ignore: camel_case_types
class _customChildDelegate extends SingleChildLayoutDelegate {
  final double offsetY;
  _customChildDelegate(this.offsetY);

  @override
  bool shouldRelayout(covariant SingleChildLayoutDelegate oldDelegate) {
    return true;
  }

  @override
  Size getSize(BoxConstraints constraints) {
    // OffsetBase;
    return super.getSize(constraints);
  }

  @override
  Offset getPositionForChild(Size size, Size childSize) {
    return Offset(size.width - childSize.width, offsetY);
  }

  @override
  BoxConstraints getConstraintsForChild(BoxConstraints constraints) {
    return super.getConstraintsForChild(constraints);
  }
}
