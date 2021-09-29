import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupSubModel.dart';
import 'Models/FUMakeupModel.dart';
import 'Models/FUMakeupSubTitleModel.dart';
import 'package:fulive_plugin/FUMakeupPlugin.dart';

class FUMakeupModelManager extends ChangeNotifier {
  //组合妆数据源
  late List<FUMakeupModel> makeupModels;

  //子妆数据源
  late List<FUMakeupSubTitleModel> makeupSubTitleModels;

  //当前选中的组合妆itemIndex
  late int selectedIndex;

  //记录当前是否有组合妆被选中，YES，就需要展示sliderValue，来控制妆容强度值
  late bool showSlider = false;

  //当前选中的子妆标题的ItemIndex
  late int selectedSubTitleIndex;

  //记录当前是否有子妆被选中，YES，就需要展示sliderValue，来控制妆容强度值
  late bool subShowSlider = false;

  //是否隐藏子妆(点击屏幕隐藏子妆，但是不隐藏子妆标题)
  late bool isHiddenSubMakeup = false;

  FUMakeupModelManager({this.selectedIndex: 2, this.selectedSubTitleIndex = 0});

  Future<List<FUMakeupModel>> getMakeupModels() async {
    String jsonStr = await rootBundle.loadString("resource/Makeup.json");
    final List jsonList = json.decode(jsonStr);
    List<String> canCustomSub = ["性感", "甜美", "邻家", "欧美", "妩媚"];
    final makeupModels = jsonList.map((map) {
      FUMakeupModel model = FUMakeupModel.fromJson(map);
      //拼接图片地址
      model.imagePath = 'resource/images/Makeup/MakeupCombination/3.0x/' +
          model.imagePath +
          '.png';
      model.canCustomSub = canCustomSub.contains(model.title);
      return model;
    }).toList();

    this.makeupModels = makeupModels;

    return makeupModels;
  }

  //选中组合装
  void didSelectedItem(int index) {
    selectedIndex = index;
    showSlider = index == 0 ? false : true;
    FUMakeupPlugin.itemDidSelectedWithParams(index);
    notifyListeners();
  }

  //组合装滑条
  void sliderValueChange(double newValue) {
    if (selectedIndex < makeupModels.length) {
      FUMakeupModel model = makeupModels[selectedIndex];
      model.value = newValue;
      FUMakeupPlugin.sliderChangeValueWithValue(selectedIndex, newValue);
      notifyListeners();
    }
  }

  //当前组合装是否可以选择自定义子妆
  bool canCustomSubMakeup() {
    if (selectedIndex < makeupModels.length) {
      FUMakeupModel model = makeupModels[selectedIndex];
      return model.canCustomSub;
    }
    return false;
  }

  //子妆标题数据源(内含子妆)
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
  void didSelectedSubItem(int subIndex, int colorIndex) {
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      //更新子妆标题对应的子妆索引值
      titleModel.subIndex = subIndex;
      subShowSlider = subIndex == 0 ? false : true;

      FUMakeupPlugin.didSelectedSubItem(
          selectedSubTitleIndex, subIndex, colorIndex);
      notifyListeners();
    }
  }

  //选中子妆颜色item, colorIndex:颜色索引
  void didSelectedColorItem(int colorIndex) {
    if (selectedSubTitleIndex < makeupSubTitleModels.length) {
      FUMakeupSubTitleModel titleModel =
          makeupSubTitleModels[selectedSubTitleIndex];
      if (titleModel.subIndex! < titleModel.subModels.length) {
        FUMakeupSubModel subModel = titleModel.subModels[titleModel.subIndex!];
        //更新子妆对应的颜色索引值
        subModel.colorIndex = colorIndex;

        FUMakeupPlugin.didSelectedColorItem(titleModel.subIndex!, colorIndex);
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

  ///隐藏子妆事件
  void hiddenSubMakeup(bool hidden) {
    if (hidden == true || isHiddenSubMakeup == true) {
      isHiddenSubMakeup = hidden;
      notifyListeners();
    }
  }
}
