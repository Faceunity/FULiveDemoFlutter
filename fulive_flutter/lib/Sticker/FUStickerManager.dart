import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Sticker/Model/FUStickerModel.dart';
import 'package:fulive_flutter/Tools/FUImagePixelRatio.dart';
import 'package:fulive_flutter/Tools/ArrayExtension.dart';
import 'package:fulive_plugin/FUStickerPlugin.dart';

class FUStickerManager extends ChangeNotifier {
  FUStickerManager({this.selectedIndex = 1, this.stickerModels});
  //贴纸数据源,要么外面传进来，要么通过getMakeupModels 异步获取
  late List<FUStickerModel>? stickerModels;
  late int selectedIndex;

  //单独订阅信号量
  late final ValueNotifier<bool> loadingNotifier = ValueNotifier(false);

  final FUStickerPlugin plugin = FUStickerPlugin();

  Future<List<FUStickerModel>> getMakeupModels() async {
    String jsonStr = await rootBundle.loadString("resource/Sticker.json");
    final List jsonList = json.decode(jsonStr);
    final stickerModels = jsonList.map((map) {
      FUStickerModel model = FUStickerModel.fromJson(map);
      //拼接图片地址
      String commonPre = model.imageName == "resetItem"
          ? FUImagePixelRatio.getImagePathWithRelativePathPre(
              "resource/images/commonImage/")
          : FUImagePixelRatio.getImagePathWithRelativePathPre(
              "resource/images/sticker/");
      model.imageName = commonPre + model.imageName + '.png';
      return model;
    }).toList();
    this.stickerModels = stickerModels;
    return stickerModels;
  }

  //设置当前默认选中值或缓存值
  void selectedDefault() {
    selectedItemWithIndex(this.selectedIndex);
  }

  //选中某个item
  void selectedItemWithIndex(int index) async {
    if (stickerModels!.inRange(index)) {
      selectedIndex = index;

      _startLoading();
      notifyListeners();

      //耗时操作，等待native plugin 返回结果，然后停止loading
      await plugin.clickItem(index);

      //不管成功失败都要停转菊花，具体是否需要业务层提示成功失败和原生对齐，native 目前不提示
      _stopLoading();
    } else {
      print("数组越界或stickerModels数据错误");
    }
  }

  bool canLoading(int index) {
    //等待贴纸加载结果返回
    if (index == selectedIndex) {
      return true;
    }
    return false;
  }

  void _startLoading() {
    loadingNotifier.value = true;
  }

  void _stopLoading() {
    loadingNotifier.value = false;
  }
}
