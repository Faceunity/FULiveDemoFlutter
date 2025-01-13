import 'package:flutter/material.dart';
import 'package:fulive_plugin/makeup_plugin.dart';
import 'package:fulivedemo_flutter/business/makeup/combination/combination_makeup_view_model.dart';
import 'package:fulivedemo_flutter/business/makeup/custom/customized_makeup_view_model.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';

class MakeupViewModel {
  final CombinationMakeupViewModel combinationMakeupViewModel = CombinationMakeupViewModel();
  final CustomizedMakeupViewModel customizedMakeupViewModel = CustomizedMakeupViewModel();

  ValueNotifier<bool> isCustomizing = ValueNotifier(false);

  void initialize() {
    combinationMakeupViewModel.initialize();
    customizedMakeupViewModel.initizlize();
  }

  void startCustomizing() {
    isCustomizing.value = true;
    // 自定义默认从第一项开始
    customizedMakeupViewModel.selectedCategoryIndex = 0;
    if (combinationMakeupViewModel.selectedIndex >= 0) {
      // 自定义前遍历更新各个子妆
      for (int index = SubMakeupType.foundation.number; index <= SubMakeupType.pupil.number; index++) {
        SubMakeupType type = SubMakeupType.values[index];
        customizedMakeupViewModel.updateCustomizedMakeups(type, combinationMakeupViewModel.subMakeupIndexOfSelectedCombinationMakeupWithType(type), combinationMakeupViewModel.subMakeupValueOfSelectedCombinationMakeupWithType(type), combinationMakeupViewModel.subMakeupColorIndexOfSelectedCombinationMakeupWithType(type));
      }
    }
  }

  void stopCustomizing() {
    isCustomizing.value = false;
    // 选中卸妆返回时需要判断子妆是否变化
    if (combinationMakeupViewModel.selectedIndex >= 0 && combinationMakeupIsChangedByCustoming) {
        // 取消选中
        combinationMakeupViewModel.setSelectedIndex(-1);
    }

  }

  bool get combinationMakeupIsChangedByCustoming {
    for (int index = SubMakeupType.foundation.number; index <= SubMakeupType.pupil.number; index++) {
        SubMakeupType type = SubMakeupType.values[index];
        int index1 = combinationMakeupViewModel.subMakeupIndexOfSelectedCombinationMakeupWithType(type);
        int index2 = customizedMakeupViewModel.subMakeupIndexWithType(type);
        
        double value1 = combinationMakeupViewModel.subMakeupValueOfSelectedCombinationMakeupWithType(type);
        double value2 = customizedMakeupViewModel.subMakeupValueWithType(type);
        
        int colorIndex1 = combinationMakeupViewModel.subMakeupColorIndexOfSelectedCombinationMakeupWithType(type);
        int colorIndex2 = customizedMakeupViewModel.subMakeupColorIndexWithType(type);
        
        bool isSameIndex = index1 == index2;
        bool isSameValue = (value1 - value2).abs() <= 0.01;
        bool isSameColor = colorIndex1 == colorIndex2;
        
        if (!isSameIndex || !isSameValue || !isSameColor) {
            return true;
        }
    }
    return false;
  }

  void dispose() {
    MakeupPlugin.unloadCombinationMakeup();
    isCustomizing.dispose();
  }
}