import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidget.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidgetArguments.dart';
import 'package:fulive_flutter/Makeup/FUMakeupConst.dart';
import 'package:fulive_flutter/Makeup/FUMakeupSubManager.dart';

import 'package:fulive_flutter/Makeup/FUMakeupSubUI.dart';
import 'package:fulive_flutter/Makeup/FUMakeupUI.dart';

import 'package:fulive_plugin/FUMakeupPlugin.dart';

import 'package:fulive_flutter/Makeup/FUColorSelectWidget.dart';

// FUMakeupPlugin
class FUMakeup extends StatefulWidget {
  static const routerName = '/FUMakeupModuleArguments';
  @override
  _FUMakeupState createState() => _FUMakeupState();
}

class _FUMakeupState extends State<FUMakeup> {
  late final args;

  ///点击美颜底部item 通知FUBaseWidget 调整 拍照按钮位置
  late StreamController _adjustPhotoStream;

  //是否是自定义子妆, false 表示组合妆组合装是没有颜色组件的. 所以还控制隐藏颜色组件
  bool _isCustomSubMakeup = false;

  //父组件记录当前组合妆选中索引
  late int _selectedIndex = 1;

  //子妆业务下，点击屏幕需要收起子妆，所以需要跨组件
  final GlobalKey<FUMakeupSubUIState> _subMakeupKey = GlobalKey();

  //子妆业务下，切换子妆会重新刷新颜色组件FUColorSelectWidget，但是颜色组件因为UI位置等问题无法放到子妆组件，所以通过globalKey处理
  final GlobalKey<FUColorSelectWidgetState> _colorKey = GlobalKey();

  //组合妆滑动偏移量记录
  // ignore: unused_field
  late double _makeupOffset;

  void _switchCustomSubMakeup(bool flag) {
    setState(() {
      _isCustomSubMakeup = flag;
    });
  }

  @override
  void initState() {
    super.initState();
    FUMakeupPlugin.configMakeup();
    _makeupOffset = 1.0;
    _adjustPhotoStream = StreamController.broadcast();
  }

  @override
  void dispose() {
    super.dispose();
    _adjustPhotoStream.close();
    FUMakeupPlugin.disposeMakeup();

    FUMakeupSubManager.releaseCacheData();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    args = ModalRoute.of(context)!.settings.arguments as FUBaseWidgetArguments;
  }

  @override
  Widget build(BuildContext context) {
    Widget child = Align(
        alignment: Alignment.bottomCenter,
        child: ClipRect(
            child: Container(
                height: _isCustomSubMakeup == true ? 200 : 140,
                child: _isCustomSubMakeup == true
                    ? FUMakeupSubUI(_subMakeupKey, (bool subMakeupSelected) {
                        // 子妆切换成组合妆，通知FUBaseWidget调整拍照按钮位置.
                        _adjustPhotoStream.sink.add(false);
                        //刷新页面展示组合妆UI
                        _switchCustomSubMakeup(false);
                        if (_selectedIndex < SUBMAKEUPINDEX) {
                          //组合妆不可自定义子妆的前提下
                          if (subMakeupSelected) {
                            //子妆有选中，返回组合妆不能设置为卸妆状态。否则会清除子妆
                            _selectedIndex = MAKEUP_UNLOADINDEX;
                          } else {
                            print("子妆未选中！！！");
                          }
                        } else {
                          print("支持自定义组合妆无需处理返回索引");
                        }
                      }, (List<List<Color>> color, bool showColorWidget,
                        int index) {
                        //局部刷新颜色值组件
                        if (_colorKey.currentState != null) {
                          _colorKey.currentState!
                              .reloadColors(color, showColorWidget, index);
                        }
                      }, canCustomSubIndex: _selectedIndex)
                    : FUMakeupUI(
                        (int canCustomIndex, double offset) {
                          // 组合妆切换成子妆回调，通知FUBaseWidget调整拍照按钮位置.
                          _adjustPhotoStream.sink.add(true);
                          //刷新页面展示子妆UI
                          _switchCustomSubMakeup(true);
                          _selectedIndex = canCustomIndex;

                          _makeupOffset = offset;
                        },
                        unMakeupCallback: () {
                          //卸妆是通用的，全部卸除，不分组合和自定义子妆
                          FUMakeupSubManager.releaseCacheData();
                        },
                        selectedIndex: _selectedIndex,
                        offset: _makeupOffset,
                      ))));

    final args =
        ModalRoute.of(context)!.settings.arguments as FUBaseWidgetArguments;
    return Stack(children: [
      FUBaseWidget(
        model: args.model,
        child: child,
        selectedImagePath: args.selectedImagePath,
        backActionCallback: () {
          //返回到上个页面
          Navigator.pop(context);
        },
        viewClick: () {
          //如果当前页面是在处理子妆，点击空白处需要收起部分子妆UI
          if (_isCustomSubMakeup) {
            if (_subMakeupKey.currentState != null) {
              _subMakeupKey.currentState!.setHiddenSubUI(true);
            }
            //隐藏颜色组件
            if (_colorKey.currentState != null) {
              //收起子妆第三个参数可以随便填
              _colorKey.currentState!.reloadColors([], false, 0);
            }
          }
        },
        adjustPhotoStream: _adjustPhotoStream,
        pointYMax: 0.5,
        pointYMin: 0.3,
      ),
      Visibility(
          visible: _isCustomSubMakeup,
          child: Align(
              alignment: Alignment(0.85, 0.0),
              child: FUColorSelectWidget(_colorKey, (int index) {
                _subMakeupKey.currentState!.selectedColorIndex(index);
              }) //子妆颜色的状态由它自己管理，通过provider 实现，不在这里传子妆对应的颜色数组参数了
              )),
    ]);
  }
}
