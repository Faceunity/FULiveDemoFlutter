import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Makeup/FUMakeupConst.dart';
import 'package:fulive_flutter/Tools/FUImagePixelRatio.dart';
import 'Models/FUMakeupModel.dart';
import 'package:fulive_plugin/FUMakeupPlugin.dart';
import 'package:fulive_flutter/Tools/ArrayExtension.dart';

//管理组合装业务数据模型
class FUMakeupModelManager extends ChangeNotifier {
  FUMakeupModelManager({this.selectedIndex: 0});
  //组合妆数据源
  late List<FUMakeupModel> makeupModels;

  //当前选中的组合妆itemIndex
  late int selectedIndex = 0;

  //记录当前是否有组合妆被选中，YES，就需要展示sliderValue，来控制妆容强度值
  late bool showSlider = false;

  //是否隐藏子妆(点击屏幕隐藏子妆，但是不隐藏子妆标题)
  late bool isHiddenSubMakeup = false;

  Future<List<FUMakeupModel>> getMakeupModels() async {
    String jsonStr = await rootBundle.loadString("resource/Makeup.json");
    final List jsonList = json.decode(jsonStr);
    List<String> canCustomSub = ["性感", "甜美", "邻家", "欧美", "妩媚"];
    final makeupModels = jsonList.map((map) {
      FUMakeupModel model = FUMakeupModel.fromJson(map);
      //拼接图片地址
      String commonPre = FUImagePixelRatio.getImagePathWithRelativePathPre(
          "resource/images/Makeup/MakeupCombination");
      model.imagePath = commonPre + model.imagePath + '.png';
      model.canCustomSub = canCustomSub.contains(model.title);
      return model;
    }).toList();

    this.makeupModels = makeupModels;
    //当前是可支持自定义子妆的组合妆并且从子妆切换回来的时候有变化,此时没有选中的索引
    if (canCustomSubMakeup()) {
      bool ret = await customMakeupChange();
      if (ret) {
        selectedIndex = MAKEUP_UNLOADINDEX;
      }
    }

    return makeupModels;
  }

  //设置当前默认选中值或缓存值
  void selectedDefault() {
    didSelectedItem(this.selectedIndex);
  }

  //选中组合装
  void didSelectedItem(int index) {
    if (makeupModels.inRange(index)) {
      showSlider = index == 0 ? false : true;
      FUMakeupPlugin.itemDidSelectedWithParams(index);
    } else {
      print("选中组合妆索引index:$index 越界");
    }
    selectedIndex = index;
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
    if (makeupModels.inRange(selectedIndex)) {
      FUMakeupModel model = makeupModels[selectedIndex];
      return model.canCustomSub!;
    }

    //标识当前是子妆状态，
    if (selectedIndex == MAKEUP_UNLOADINDEX) {
      return true;
    }

    return false;
  }

  //标记 自定义按钮透明度，点击组合装、卸妆、或者无组合妆情况下要让自定义按钮高亮
  bool changeCustomPicAlpha() {
    if (selectedIndex <= 0 || (canCustomSubMakeup())) {
      return true;
    }
    return false;
  }

  //组合妆切换子妆
  void makeupChange() {
    //当前组合妆不可以自定义子妆时必定要要先卸妆，才可以切换子妆，所以selectedIndex = -1,防止子妆再切回组合妆时默认选中selectedIndex = 0的卸妆状态
    if (canCustomSubMakeup() == false) {
      selectedIndex = MAKEUP_UNLOADINDEX;
    } else {
      //让native plugin 处理可自定义子妆的组合装对应的子妆索引。然后把索引数据返回给Flutter侧，放到子妆组件做比较合适。
    }
    FUMakeupPlugin.makeupChange(false);
  }

  //获slider取索引值
  double getSliderValue() {
    if (makeupModels.inRange(selectedIndex)) {
      return makeupModels[selectedIndex].value;
    }
    return 0.0;
  }

  Future<bool> customMakeupChange() async {
    bool res = await FUMakeupPlugin.subMakeupChange(selectedIndex);
    return res;
  }
}
