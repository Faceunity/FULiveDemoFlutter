//子妆标题数据源(内含子妆)
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupSubModel.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupSubTitleModel.dart';
import 'package:fulive_plugin/FUMakeupPlugin.dart';

//管理子妆业务模型
class FUMakeupSubManager extends ChangeNotifier {
  FUMakeupSubManager({this.selectedSubTitleIndex = 0});

  //子妆数据源
  late List<FUMakeupSubTitleModel> makeupSubTitleModels;
  //当前选中的子妆标题的ItemIndex
  late int selectedSubTitleIndex;

  //记录当前是否有子妆被选中，YES，就需要展示sliderValue，来控制妆容强度值
  late bool subShowSlider = false;

  //是否隐藏子妆(点击屏幕隐藏子妆，但是不隐藏子妆标题)
  late bool isHiddenSubMakeup = false;

  Future<List<FUMakeupSubTitleModel>> getSubMakeupModels() async {
    String jsonStr = await rootBundle.loadString("resource/MakeupSub.json");
    final List jsonList = json.decode(jsonStr);

    final makeupSubTitleModels = jsonList.map((map) {
      FUMakeupSubTitleModel model = FUMakeupSubTitleModel.fromJson(map);
      model.subIndex = 0;
      for (FUMakeupSubModel item in model.subModels) {
        //拼接图片地址
        item.imagePath =
            'resource/images/Makeup/MakeupSub/3.0x/' + item.imagePath + '.png';
      }
      return model;
    }).toList();

    this.makeupSubTitleModels = makeupSubTitleModels;
    return makeupSubTitleModels;
  }

  //选中子妆标题item, subTitleIndex标题索引，
  void didSelectedSubTitleItem(int subTitleIndex) {
    selectedSubTitleIndex = subTitleIndex;
    //目前业务来看点击标题没必要对FURenderKit 进行设置。暂时不去做
    // FUMakeupPlugin.didSelectedSubTitleItem(subTitleIndex);
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      subShowSlider = titleModel.subIndex == 0 ? false : true;
    }
    notifyListeners();
  }

  //选中子妆item, subIndex 子妆索引, colorIndex:颜色索引
  // return 返回值表示上次选中和这次选中是否是同一个
  bool didSelectedSubItem(int subIndex, int colorIndex) {
    bool flag = false;
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      //上次选中和这次选中是否是同一个
      flag = titleModel.subIndex != subIndex;
      //更新子妆标题对应的子妆索引值
      titleModel.subIndex = subIndex;
      subShowSlider = subIndex == 0 ? false : true;

      FUMakeupPlugin.didSelectedSubItem(
          selectedSubTitleIndex, subIndex, colorIndex);
      notifyListeners();
    }
    return flag;
  }

  //选中子妆颜色item, colorIndex:颜色索引
  void didSelectedColorItem(int colorIndex) {
    print("调用插件接口:didSelectedColorItem！！！");
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      if (titleModel.subIndex! < titleModel.subModels.length) {
        FUMakeupSubModel subModel = titleModel.subModels[titleModel.subIndex!];
        //更新子妆对应的颜色索引值
        subModel.colorIndex = colorIndex;

        FUMakeupPlugin.didSelectedColorItem(
            selectedSubTitleIndex, titleModel.subIndex!, colorIndex);
        notifyListeners();
      }
    }
  }

  //子妆Slider滑动
  void subsSliderValueChange(double newValue) {
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      FUMakeupSubModel model = titleModel.subModels[titleModel.subIndex!];
      model.value = newValue;
      FUMakeupPlugin.subMakupSliderChangeValueWithValue(
          selectedSubTitleIndex, titleModel.subIndex!, newValue);
      notifyListeners();
    }
  }

  ///隐藏子妆事件
  void hiddenSubMakeup(bool hidden) {
    if (hidden == true || isHiddenSubMakeup == true) {
      isHiddenSubMakeup = hidden;
      notifyListeners();
    }
  }

  //获取当前选中的子妆模型
  FUMakeupSubModel? getCurSubModel() {
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      FUMakeupSubModel subModel = titleModel.subModels[titleModel.subIndex!];
      return subModel;
    }
    return null;
  }

  //获取当前子妆对应的颜色数组
  List<List<Color>> getCurSubColors() {
    List<List<Color>> colorList = [];
    try {
      FUMakeupSubModel? subModel = getCurSubModel();
      if (subModel != null) {
        if (subModel.CacheColors != null) {
          for (List<dynamic> color in subModel.CacheColors!) {
            colorList.add(color as List<Color>);
          }
        } else {
          for (List<double> color in subModel.colors!) {
            List<Color> c = [];
            int step = color.length ~/ 4;
            for (var i = 0; i < step; i++) {
              c.add(Color.fromARGB(
                  (color[i * 4 + 3] * 255).toInt(),
                  (color[i * 4 + 0] * 255).toInt(),
                  (color[i * 4 + 1] * 255).toInt(),
                  (color[i * 4 + 2] * 255).toInt()));
            }
            colorList.add(c);
          }
          subModel.CacheColors = colorList;
        }
      }
    } catch (e) {
      print("获取子妆颜色数组出错:$e");
    }

    return colorList;
  }

  //是否展示颜色组件
  bool isShowColorWidget(int index) {
    //粉底
    if (selectedSubTitleIndex == 0) {
      return false;
    }
    //非粉底，但是卸妆状态
    if (index == 0) {
      return false;
    }
    //其他情况都显示颜色值
    return true;
  }

  //获取当前选中的颜色索引值
  int getColorIndex() {
    return getCurSubModel()!.colorIndex != null
        ? getCurSubModel()!.colorIndex!
        : 0;
  }

  //获取当前标题对应的子妆值
  bool getSliderValue(int index) {
    if (index > 0 && index < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel = makeupSubTitleModels[index];
      FUMakeupSubModel model = titleModel.subModels[titleModel.subIndex!];

      return model.value != 0;
    }

    return false;
  }
}
