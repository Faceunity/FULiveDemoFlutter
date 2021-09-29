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

  late final args;
  @override
  void initState() {
    super.initState();
    FUBeautyPlugin.configBeauty();
    _manager = FUBeautifyDataManager();

    adjustPhotoStream = StreamController.broadcast();
  }

  @override
  void dispose() {
    super.dispose();
    adjustPhotoStream.close();
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
      compareCallback: (bool compare) => FULivePlugin.renderOrigin(compare),
      clickItemCallback: (bool flag) =>
          //通知FUBaseWidget调整拍照按钮位置
          adjustPhotoStream.sink.add(flag),
    );

    return ChangeNotifierProvider(
        create: (context) => _manager,
        child: Stack(
          children: [
            FUBaseWidget(
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
                        MainRouters.FULiveModelTypeBeautifyFace),
                  ),
                )..then((value) {
                    print("回退到当前页面页面");
                    FUBeautyPlugin.flutterWillAppear();

                    ///返回时目的是刷新一下子FUBaseWidget，重新开启流式监听
                    setState(() {});
                  });
              },
              adjustPhotoStream: adjustPhotoStream,
              pointYMax: 0.8,
              pointYMin: 0.27,
            ),
          ],
        ));
  }
}
