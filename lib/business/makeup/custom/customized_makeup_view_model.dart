import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_plugin/makeup_plugin.dart';
import 'package:fulivedemo_flutter/business/makeup/custom/customized_makeup_model.dart';
import 'package:fulivedemo_flutter/business/makeup/custom/sub_makeup_model.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';

class CustomizedMakeupViewModel extends ChangeNotifier {

  List<CustomizedMakeupModel> customizedMakeups = [];
  // 选中的类型索引
  int selectedCategoryIndex = 0;

  // 当前自定义子妆是否需要颜色选择器
  bool get needsColorPicker {
    if (customizedMakeups.isNotEmpty) {
      CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
      if (model.selectedSubMakeupIndex != null) {
        int index = model.selectedSubMakeupIndex as int;
        SubMakeupModel subMakeup = model.subMakeups[index];
        if (subMakeup.type != SubMakeupType.foundation && subMakeup.colors != null) {
          return subMakeup.colors!.isNotEmpty;
        }
        // return (subMakeup.type != SubMakeupType.foundation && subMakeup.colors != null);
      }
    }
    return false;
  }

  // 当前自定义子妆可选颜色数组
  List<List<dynamic>>? get currentColors {
    if (customizedMakeups.isNotEmpty) {
      CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
      if (model.selectedSubMakeupIndex != null) {
        int index = model.selectedSubMakeupIndex as int;
        SubMakeupModel subMakeup = model.subMakeups[index];
        return subMakeup.colors;
      }
    }
    return null;
  }

  List<SubMakeupModel> get selectedSubMakeups {
    return customizedMakeups.isNotEmpty ? customizedMakeups[selectedCategoryIndex].subMakeups : [];
  }

  void setSelectedSubMakeupIndex(int index) {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    if (model.selectedSubMakeupIndex == index) {
      return;
    }
    model.selectedSubMakeupIndex = index;
    SubMakeupModel sub = model.subMakeups[model.selectedSubMakeupIndex!];
    if (index == 0) {
      // 卸载子妆
      MakeupPlugin.unloadSubMakeup(sub.type.number);
    } else {
      Map<String, dynamic> map = sub.toJson();
      MakeupPlugin.setSubMakeupBundle(map);
      MakeupPlugin.setSubMakeupIntensity(map);
      MakeupPlugin.setSubMakeupColor(map);
    }
    notifyListeners();
  }

  void setSelectedSubMakeupValue(double value) {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    SubMakeupModel subModel = model.subMakeups[model.selectedSubMakeupIndex!];
    subModel.value = value;
    Map<String, dynamic> map = subModel.toJson();
    MakeupPlugin.setSubMakeupIntensity(map);
  }

  void setSelectedColorIndex(int index) {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    SubMakeupModel subModel = model.subMakeups[model.selectedSubMakeupIndex!];
    if (subModel.colors == null) {
      return;
    }
    if (index < 0 || index >= subModel.colors!.length) {
      return;
    }
    subModel.defaultColorIndex = index;
    Map<String, dynamic> map = subModel.toJson();
    MakeupPlugin.setSubMakeupColor(map);
  }

  void updateCustomizedMakeups(SubMakeupType type, int selectedSubMakeupIndex, double selectedSubMakeupValue, int selectedColorIndex) {
    customizedMakeups[type.number].selectedSubMakeupIndex = selectedSubMakeupIndex;
    customizedMakeups[type.number].subMakeups[selectedSubMakeupIndex].value = selectedSubMakeupIndex == 0 ? 0.0 : selectedSubMakeupValue;
    customizedMakeups[type.number].subMakeups[selectedSubMakeupIndex].defaultColorIndex = selectedColorIndex;
    // 恢复所有其他未选择子妆程度值和颜色
    for (int i = 0; i < customizedMakeups[type.number].subMakeups.length; i++) {
      SubMakeupModel model = customizedMakeups[type.number].subMakeups[i];
      if (model.index != selectedSubMakeupIndex && model.index != 0) {
        model.value = 1.0;
        if (model.type != SubMakeupType.foundation) {
          model.defaultColorIndex = 0;
        }
      }
    }
  }

  int get selectedSubMakeupIndex {
    if (customizedMakeups.isNotEmpty) {
      CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
      return model.selectedSubMakeupIndex as int;
    }
    return 0;
  }

  int get selectedColorIndex {
    if (customizedMakeups.isNotEmpty) {
      CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
      int selectedSubIndex = model.selectedSubMakeupIndex as int;
      return model.subMakeups[selectedSubIndex].defaultColorIndex ?? 0;
    }
    return 0;
  }

  double get selectedSubMakeupValue {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    int selectedSubIndex = model.selectedSubMakeupIndex as int;
    SubMakeupModel selectedSubMakeup = model.subMakeups[selectedSubIndex];
    return selectedSubMakeup.value;
  }

  String? get selectedSubMakeupTitle {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    int selectedSubIndex = model.selectedSubMakeupIndex as int;
    SubMakeupModel selectedSubMakeup = model.subMakeups[selectedSubIndex];
    return selectedSubMakeup.title;
  }

  // 子妆容是否有值
  bool hasValidValueAtCategoryIndex(int index) {
    CustomizedMakeupModel model = customizedMakeups[index];
    return model.selectedSubMakeupIndex! > 0 && model.subMakeups[model.selectedSubMakeupIndex!].value > 0;
  }

  // 当前自定义妆容中指定类型子妆的索引
  int subMakeupIndexWithType(SubMakeupType type) {
    return customizedMakeups[type.number].selectedSubMakeupIndex ?? 0;
  }

  // 当前自定义妆容中指定类型子妆的程度值
  double subMakeupValueWithType(SubMakeupType type) {
    CustomizedMakeupModel model = customizedMakeups[type.number];
    SubMakeupModel sub = model.subMakeups[model.selectedSubMakeupIndex!];
    return sub.value;
  }

  // 当前自定义妆容中指定类型子妆的颜色索引
  int subMakeupColorIndexWithType(SubMakeupType type) {
    CustomizedMakeupModel model = customizedMakeups[type.number];
    SubMakeupModel sub = model.subMakeups[model.selectedSubMakeupIndex!];
    return sub.defaultColorIndex ?? 0;
  }

  // 根据索引获取子妆容 Icon
  AssetImage? subMakeupImageAtIndex(int index) {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    SubMakeupModel subMakeup = model.subMakeups[index];
    return (subMakeup.type == SubMakeupType.foundation && index > 0) ? null : CommonUtil.assetImageNamed("makeup/custom/${subMakeup.icon}"); 
  }

  // 根据索引获取子妆容视图背景颜色
  Color subMakeupBackgroundColorAtIndex(int index) {
    CustomizedMakeupModel model = customizedMakeups[selectedCategoryIndex];
    SubMakeupModel subMakeup = model.subMakeups[index];
    if (subMakeup.type == SubMakeupType.foundation && index > 0) {
      List<dynamic> color = subMakeup.colors![subMakeup.defaultColorIndex!];
      // double 
      return Color.fromARGB(((color[3] * 255) as double).toInt(), ((color[0] * 255) as double).toInt(), ((color[1] * 255) as double).toInt(), ((color[2] * 255) as double).toInt());
    }
    return Colors.transparent;
  }

  void initizlize() async {
    // Flutter 中加载本地文件要求异步
    rootBundle.loadString(CommonUtil.fileNamed("jsons/makeup/custom/customized_makeups.json")).then((value) {
      String jsonString = value;
      final jsonData = json.decode(jsonString);
      List<CustomizedMakeupModel> makeups = [];
      for (Map<String, dynamic> item in jsonData) {
        CustomizedMakeupModel model = CustomizedMakeupModel.fromJson(item);
        for (int i = 0; i < model.subMakeups.length; i++) {
          // 手动设置索引
          model.subMakeups[i].index = i;
        }
        makeups.add(model);
      }
      customizedMakeups = makeups;
      // 通知所有监听者更新UI
      notifyListeners();
    });
  }
}