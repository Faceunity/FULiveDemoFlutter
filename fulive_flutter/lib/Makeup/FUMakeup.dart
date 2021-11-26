import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidget.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidgetArguments.dart';

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

  //组合妆当前选中索引，默认选中第一个减龄
  late int _makeUpIndex = 1;

  //子妆业务下，点击屏幕需要收起子妆，所以需要跨组件
  final GlobalKey<FUMakeupSubUIState> _subMakeupKey = GlobalKey();

  //子妆业务下，切换子妆会重新刷新颜色组件FUColorSelectWidget，但是颜色组件因为UI位置等问题无法放到子妆组件，所以通过globalKey处理
  final GlobalKey<FUColorSelectWidgetState> _colorKey = GlobalKey();

  void _switchCustomSubMakeup(bool flag) {
    setState(() {
      _isCustomSubMakeup = flag;
    });
  }

  @override
  void initState() {
    super.initState();
    FUMakeupPlugin.configMakeup();

    _adjustPhotoStream = StreamController.broadcast();
  }

  @override
  void dispose() {
    super.dispose();
    FUMakeupPlugin.disposeMakeup();
    _adjustPhotoStream.close();
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
                height: 200,
                child: _isCustomSubMakeup == true
                    ? FUMakeupSubUI(_subMakeupKey, () {
                        // 子妆切换成组合妆，通知FUBaseWidget调整拍照按钮位置.
                        _adjustPhotoStream.sink.add(false);
                        //刷新页面展示组合妆UI
                        _switchCustomSubMakeup(false);
                      }, (List<List<Color>> color, bool showColorWidget,
                        int index) {
                        //局部刷新颜色值组件
                        if (_colorKey.currentState != null) {
                          _colorKey.currentState!
                              .reloadColors(color, showColorWidget, index);
                        }
                      })
                    : FUMakeupUI(
                        () {
                          // 组合妆切换成子妆回调，通知FUBaseWidget调整拍照按钮位置.
                          _adjustPhotoStream.sink.add(true);
                          //刷新页面展示子妆UI
                          _switchCustomSubMakeup(true);
                          //点击切换子妆回调 在业务上必定是先卸载组合装才能切换子妆，所以组合妆必定是 _makeUpIndex == 0，但此时如果子妆选中了效果又不能调用卸妆接口，所以_makeUpIndex 设置一个越界值让组合装内部不调用native 卸妆接口
                          _makeUpIndex = -1;
                        },
                        selectedIndex: _makeUpIndex,
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
