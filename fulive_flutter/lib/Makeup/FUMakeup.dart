import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidget.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidgetArguments.dart';
import 'FUColorSelectWidget.dart';
import 'Models/FUMakeupModel.dart';
import 'Models/FUMakeupSubTitleModel.dart';
import 'FUMakeupSubUI.dart';
import 'package:fulive_plugin/FUMakeupPlugin.dart';
import 'package:provider/provider.dart';

import 'FUMakeupModelManager.dart';
import 'FUMakeupUI.dart';

// FUMakeupPlugin
class FUMakeup extends StatefulWidget {
  static const routerName = '/FUMakeupModuleArguments';
  @override
  _FUMakeupState createState() => _FUMakeupState();
}

class _FUMakeupState extends State<FUMakeup> {
  late final args;
  late final FUMakeupModelManager _manager;

  ///点击美颜底部item 通知FUBaseWidget 调整 拍照按钮位置
  late StreamController adjustPhotoStream;

  //是否是自定义子妆
  bool _isCustomSubMakeup = false;

  void _switchCustomSubMakeup(bool flag) {
    setState(() {
      _isCustomSubMakeup = flag;
    });
  }

  @override
  void initState() {
    super.initState();
    FUMakeupPlugin.configMakeup();
    _manager = FUMakeupModelManager();
    //默认选中第一个组合装(减龄)
    _manager.didSelectedItem(1);
    adjustPhotoStream = StreamController.broadcast();
  }

  @override
  void dispose() {
    super.dispose();
    FUMakeupPlugin.disposeMakeup();
    adjustPhotoStream.close();
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
        child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 1.0, sigmaY: 1.0),
            child: Opacity(
                opacity: 0.9,
                child: _isCustomSubMakeup == true
                    ? _customSubMakeup()
                    : _makeupUI())),
      )),
    );

    final args =
        ModalRoute.of(context)!.settings.arguments as FUBaseWidgetArguments;
    return ChangeNotifierProvider(
        create: (context) => _manager,
        child: Stack(children: [
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
                _manager.hiddenSubMakeup(true);
              }
            },
            adjustPhotoStream: adjustPhotoStream,
            pointYMax: 0.5,
            pointYMin: 0.3,
          ),
          Align(
            alignment: Alignment.centerRight,
            child: FUColorSelectWidget(),
          ),
        ]));
  }

  //组合妆UI
  Widget _makeupUI() {
    return FutureBuilder<List<FUMakeupModel>>(
      builder: (BuildContext context, AsyncSnapshot snapshot) {
        if (snapshot.connectionState == ConnectionState.done) {
          if (snapshot.hasError) {
            // 请求失败，显示错误
            return Text("Error: ${snapshot.error}");
          } else {
            // // 请求成功，显示数据
            List<FUMakeupModel> dataList = snapshot.data;
            return FUMakeupUI(dataList, () {
              // 切换成子妆，通知FUBaseWidget调整拍照按钮位置.
              adjustPhotoStream.sink.add(true);
              //刷新页面展示子妆UI展示子妆UI
              _switchCustomSubMakeup(true);
            });
          }
        } else {
          // 请求未结束，显示loading
          // return CircularProgressIndicator();
          return Container();
        }
      },
      future: _manager.getMakeupModels(),
    );
  }

  //自定义子妆UI
  Widget _customSubMakeup() {
    return FutureBuilder<List<FUMakeupSubTitleModel>>(
      builder: (BuildContext context, AsyncSnapshot snapshot) {
        if (snapshot.connectionState == ConnectionState.done) {
          if (snapshot.hasError) {
            // 请求失败，显示错误
            return Text("Error: ${snapshot.error}");
          } else {
            // // 请求成功，显示数据
            List<FUMakeupSubTitleModel> dataList = snapshot.data;
            return FUMakeupSubUI(dataList, () {
              // 切换成子妆，通知FUBaseWidget调整拍照按钮位置.
              adjustPhotoStream.sink.add(false);
              //刷新页面展示子妆UI展示子妆UI
              _switchCustomSubMakeup(false);
            });
          }
        } else {
          // 请求未结束，显示loading
          // return CircularProgressIndicator();
          return Container();
        }
      },
      future: _manager.getSubMakeupModels(),
    );
  }
}
