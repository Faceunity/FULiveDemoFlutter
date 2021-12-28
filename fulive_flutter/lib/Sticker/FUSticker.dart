import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidget.dart';
import 'package:fulive_flutter/BaseModule/FUBaseWidgetArguments.dart';
import 'package:fulive_flutter/BaseModule/FUSelectedImage.dart';
import 'package:fulive_flutter/Main/MainRouterDefine.dart';
import 'package:fulive_flutter/Sticker/FUStickerManager.dart';
import 'package:fulive_flutter/Sticker/Model/FUStickerModel.dart';
import 'package:fulive_flutter/Sticker/StickerToolsWidget.dart';
import 'package:provider/provider.dart';

class FUSticker extends StatefulWidget {
  static const routerName = '/FUStickerModuleArguments';
  @override
  _FUStickerState createState() => _FUStickerState();
}

class _FUStickerState extends State<FUSticker> {
  late final args;

  late final FUStickerManager _manager;

  //刷新basewidget里面的流式业务数据
  final GlobalKey<FUBaseWidgetState> _baseWidgetKey = GlobalKey();

  @override
  void initState() {
    super.initState();
    _manager = FUStickerManager();

    _manager.plugin.configBiz();
    _manager.plugin.flutterWillAppear();
  }

  @override
  void dispose() {
    super.dispose();
    _manager.plugin.flutterWillDisappear();
    _manager.plugin.dispose();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    args = ModalRoute.of(context)!.settings.arguments as FUBaseWidgetArguments;
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
        create: (context) => _manager,
        child: FutureBuilder<List<FUStickerModel>>(
          builder: (BuildContext context, AsyncSnapshot snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              if (snapshot.hasError) {
                // 请求失败，显示错误
                return Text("Error: ${snapshot.error}");
              } else {
                //请求成功，显示数据
                List<FUStickerModel> dataList = snapshot.data;
                //设置当前默认选中值或缓存值
                _manager.selectedDefault();
                return Stack(
                  children: [
                    FUBaseWidget(
                      key: _baseWidgetKey,
                      neverShowCustomAlbum: true,
                      model: args.model,
                      selectedImagePath: args.selectedImagePath,
                      child: StickerToolsWidget(dataList),
                      backActionCallback: () {
                        //todo: 一些清理FURenderKit的工作交给native去做
                        // FUBeautyPlugin.beautyClean();
                        // _manager.cacheData();
                        //返回到上个页面
                        Navigator.pop(context);
                      },
                      //进入美颜自定义相册
                      jumpCumstomAlbum: () {
                        //通知贴纸页面disappear
                        _manager.plugin.flutterWillDisappear();
                        Navigator.push(
                          context,
                          new MaterialPageRoute(
                            builder: (context) => new FUSelectedImage(
                              MainRouters.FULiveModelTypeItems,
                              data: dataList,
                            ),
                          ),
                        )..then((value) {
                            print("回退到当前页面页面");
                            //设置当前缓存值
                            //设置当前默认选中值或缓存值
                            _manager.selectedDefault();
                            _manager.plugin.flutterWillAppear();
                            if (_baseWidgetKey.currentState != null) {
                              _baseWidgetKey.currentState!.reloadStream();
                            } else {
                              print("_baseWidgetKey.currentState 为空");
                            }
                          });
                      },
                      adjustPhotoStream: null,
                      pointYMax: 0.65,
                      pointYMin: 0.0,
                    ),
                  ],
                );
              }
            } else {
              return Container();
            }
          },
          future: _manager.getMakeupModels(),
        ));
  }
}
