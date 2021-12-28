import 'dart:async';
import 'package:flutter/material.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidget.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidgetArguments.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';
import 'dart:core';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulive_plugin/FUBeautyPlugin.dart';
import 'package:flutter/cupertino.dart';
import 'package:fulive_flutter/BaseModule/FUSelectedImage.dart';
import 'package:fulive_flutter/Main/MainRouterDefine.dart';
import 'package:provider/provider.dart';
import 'BeautyToolsWidget.dart';
import 'FUBeautyDataManager.dart';

class FUBeauty extends StatefulWidget {
  static const routerName = '/FUBeautyModuleArguments';
  @override
  _FUBeautyState createState() => _FUBeautyState();
}

class _FUBeautyState extends State<FUBeauty> {
  late FUBeautifyDataManager _manager;

  ///点击美颜底部item 通知FUBaseWidget 调整 拍照按钮位置
  late StreamController adjustPhotoStream;

  /// 对比按钮按住时候禁止录屏操作控制流
  late StreamController prohibitStream;

  // 录屏或者拍照action时候禁止点击对比按钮
  late StreamController prohibitCompare;

  //刷新basewidget里面的流式业务数据
  final GlobalKey<FUBaseWidgetState> _baseWidgetKey = GlobalKey();

  late final args;
  @override
  void initState() {
    super.initState();
    FUBeautyPlugin.configBeauty();
    _manager = FUBeautifyDataManager();

    adjustPhotoStream = StreamController.broadcast();
    prohibitStream = StreamController.broadcast();
    prohibitCompare = StreamController.broadcast();
  }

  @override
  void dispose() {
    super.dispose();
    adjustPhotoStream.close();
    prohibitStream.close();
    prohibitCompare.close();
    //告知native 页面消失
    FUBeautyPlugin.flutterWillAppear();
    FUBeautyPlugin.disposeFUBeauty();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    args = ModalRoute.of(context)!.settings.arguments as FUBaseWidgetArguments;
  }

  @override
  Widget build(BuildContext context) {
    Widget child = BeautyToolsWidget(
      showFilterTips: true,
      bizType: _manager.curBizType,
      dataList: _manager.dataList,
      compareCallback: (bool compare) {
        FULivePlugin.renderOrigin(compare);
        //同时禁止录屏按钮点击
        prohibitStream.sink.add(compare);
      },
      clickItemCallback: (bool flag) =>
          //通知FUBaseWidget调整拍照按钮位置
          adjustPhotoStream.sink.add(flag),
      prohibitCompare: prohibitCompare,
    );

    return ChangeNotifierProvider(
        create: (context) => _manager,
        child: Stack(
          children: [
            FUBaseWidget(
              key: _baseWidgetKey,
              neverShowCustomAlbum: false,
              model: args.model,
              selectedImagePath: args.selectedImagePath,
              child: child,
              viewClick: () {
                _manager.changeBizType(FUBeautyDefine.FUBeautyMax);
                //通知FUBaseWidget调整拍照按钮位置
                adjustPhotoStream.sink.add(false);
              },
              backActionCallback: () {
                //todo: 一些清理FURenderKit的工作交给native去做
                FUBeautyPlugin.beautyClean();
                _manager.cacheData();
                //返回到上个页面
                Navigator.pop(context);
              },
              //进入美颜自定义相册
              jumpCumstomAlbum: () {
                _manager.cacheData();
                _manager.changeBizType(FUBeautyDefine.FUBeautyMax);
                //调整相机按钮位置
                adjustPhotoStream.sink.add(false);
                print("离开当前页面");
                FUBeautyPlugin.flutterWillDisappear();

                //美颜
                Navigator.push(
                  context,
                  new MaterialPageRoute(
                      builder: (context) => new FUSelectedImage(
                            MainRouters.FULiveModelTypeBeautifyFace,
                          )),
                )..then((value) {
                    print("回退到当前页面页面");
                    FUBeautyPlugin.flutterWillAppear();
                    if (_baseWidgetKey.currentState != null) {
                      _baseWidgetKey.currentState!.reloadStream();
                    } else {
                      print("_baseWidgetKey.currentState 为空");
                    }
                  });
              },
              prohibitStream: prohibitStream,
              adjustPhotoStream: adjustPhotoStream,
              pointYMax: 0.8,
              pointYMin: 0.27,
              phototAction: (bool flag) {
                prohibitCompare.sink.add(flag);
              },
            ),
          ],
        ));
  }
}
