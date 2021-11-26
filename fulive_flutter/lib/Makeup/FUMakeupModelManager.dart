import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'Models/FUMakeupModel.dart';
import 'Models/FUMakeupSubTitleModel.dart';
import 'package:fulive_plugin/FUMakeupPlugin.dart';

//管理组合装业务数据模型
class FUMakeupModelManager extends ChangeNotifier {
  FUMakeupModelManager({this.selectedIndex: 0});
  //组合妆数据源
  late List<FUMakeupModel> makeupModels;

  //子妆数据源
  late List<FUMakeupSubTitleModel> makeupSubTitleModels;

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
    if (index >= 0 && index < makeupModels.length) {
      selectedIndex = index;
      showSlider = index == 0 ? false : true;
      FUMakeupPlugin.itemDidSelectedWithParams(index);
    } else {
      print("选中组合妆索引index:$index 越界");
    }
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
      return model.canCustomSub!;
    }
    return false;
  }

  //标记 自定义按钮透明度，点击组合装卸妆时候需要让自定义按钮高亮
  bool changeCustomPicAlpha() {
    if (selectedIndex == 0) {
      return true;
    }
    return false;
  }
}
