import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulivedemo_flutter/business/makeup/combination/combination_makeup_model.dart';
import 'package:fulivedemo_flutter/business/makeup/custom/sub_makeup_model.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulive_plugin/makeup_plugin.dart';

class CombinationMakeupViewModel extends ChangeNotifier {
  List<CombinationMakeupModel> makeups = [];
  int selectedIndex = -1;

  void setSelectedIndex(int index) {
    selectedIndex = index;
    if (index < 0) {
      // 取消选择，需要更新
      notifyListeners();
    } else if (index == 0) {
      MakeupPlugin.unloadCombinationMakeup();
    } else {
      Map<String, dynamic> map = makeups[selectedIndex].toJson();
      MakeupPlugin.loadCombinationMakeup(map);
    }
  }

  void setSelectedMakeupValue(double value) {
    if (selectedIndex < 0 || selectedIndex >= makeups.length) {
      return;
    }
    makeups[selectedIndex].value = value;
    Map<String, dynamic> map = makeups[selectedIndex].toJson();
    MakeupPlugin.setCombinationMakeupIntensity(map);
  }

  double get selectedMakeupValue {
    return makeups[selectedIndex].value;
  }

  bool get isSelectedMakeupAllowedEdit {
    if (selectedIndex < 0) {
      return false;
    }
    return makeups[selectedIndex].isAllowedEdit ?? false;
  }

  /// 选中组合妆中指定类型子妆的索引
  int subMakeupIndexOfSelectedCombinationMakeupWithType(SubMakeupType type) {
    if (selectedIndex <= 0) {
        return 0;
    }
    CombinationMakeupModel model = makeups[selectedIndex];
    int index = 0;
    switch (type) {
      case SubMakeupType.foundation:
        index = model.foundationModel!.index ?? 0;
      case SubMakeupType.lip:
        index = model.lipstickModel!.index ?? 0;
      case SubMakeupType.blusher:
        index = model.blusherModel!.index ?? 0;
      case SubMakeupType.eyebrow:
        index = model.eyebrowModel!.index ?? 0;
      case SubMakeupType.eyeShadow:
        index = model.eyeShadowModel!.index ?? 0;
      case SubMakeupType.eyeliner:
        index = model.eyelinerModel!.index ?? 0;
      case SubMakeupType.eyelash:
        index = model.eyelashModel!.index ?? 0;
      case SubMakeupType.highlight:
        index = model.highlightModel!.index ?? 0;
      case SubMakeupType.shadow:
        index = model.shadowModel!.index ?? 0;
      case SubMakeupType.pupil:
        index = model.pupilModel!.index ?? 0;
    }
    return index;
  }

  /// 选中组合妆中指定类型子妆的程度值
  double subMakeupValueOfSelectedCombinationMakeupWithType(SubMakeupType type) {
    if (selectedIndex <= 0) {
        return 0;
    }
    CombinationMakeupModel model = makeups[selectedIndex];
    double value = 0;
    switch (type) {
      case SubMakeupType.foundation:
        value = model.foundationModel!.value * selectedMakeupValue;
      case SubMakeupType.lip:
        value = model.lipstickModel!.value * selectedMakeupValue;
      case SubMakeupType.blusher:
        value = model.blusherModel!.value * selectedMakeupValue;
      case SubMakeupType.eyebrow:
        value = model.eyebrowModel!.value * selectedMakeupValue;
      case SubMakeupType.eyeShadow:
        value = model.eyeShadowModel!.value * selectedMakeupValue;
      case SubMakeupType.eyeliner:
        value = model.eyelinerModel!.value * selectedMakeupValue;
      case SubMakeupType.eyelash:
        value = model.eyelashModel!.value * selectedMakeupValue;
      case SubMakeupType.highlight:
        value = model.highlightModel!.value * selectedMakeupValue;
      case SubMakeupType.shadow:
        value = model.shadowModel!.value * selectedMakeupValue;
      case SubMakeupType.pupil:
        value = model.pupilModel!.value * selectedMakeupValue;
    }
    return value;
  }

  /// 选中组合妆中指定类型子妆的颜色索引
  int subMakeupColorIndexOfSelectedCombinationMakeupWithType(SubMakeupType type) {
    if (selectedIndex <= 0) {
        return 0;
    }
    CombinationMakeupModel model = makeups[selectedIndex];
    int index = 0;
    switch (type) {
      case SubMakeupType.foundation:
        index = model.foundationModel!.defaultColorIndex ?? 0;
      case SubMakeupType.lip:
        index = model.lipstickModel!.defaultColorIndex ?? 0;
      case SubMakeupType.blusher:
        index = model.blusherModel!.defaultColorIndex ?? 0;
      case SubMakeupType.eyebrow:
        index = model.eyebrowModel!.defaultColorIndex ?? 0;
      case SubMakeupType.eyeShadow:
        index = model.eyeShadowModel!.defaultColorIndex ?? 0;
      case SubMakeupType.eyeliner:
        index = model.eyelinerModel!.defaultColorIndex ?? 0;
      case SubMakeupType.eyelash:
        index = model.eyelashModel!.defaultColorIndex ?? 0;
      case SubMakeupType.highlight:
        index = model.highlightModel!.defaultColorIndex ?? 0;
      case SubMakeupType.shadow:
        index = model.shadowModel!.defaultColorIndex ?? 0;
      case SubMakeupType.pupil:
        index = model.pupilModel!.defaultColorIndex ?? 0;
    }
    return index;
  }

  Future<void> initialize() async {
    String jsonString = await rootBundle.loadString(CommonUtil.fileNamed("jsons/makeup/combination/combination_makeups.json"));
    final jsonData = json.decode(jsonString);
    List<CombinationMakeupModel> makeupModels = [];
    for (Map<String, dynamic> item in jsonData) {
      CombinationMakeupModel model = CombinationMakeupModel.fromJson(item);
      if (model.name != "卸妆") {
        // 解析对应组合妆的json文件，获取子妆容
        String combinationPath = await rootBundle.loadString(CommonUtil.fileNamed("jsons/makeup/combination/${model.bundleName}.json"));
        final Map<String, dynamic> combinationDictionary = json.decode(combinationPath);
        // 设置子妆内容
        SubMakeupModel foundation = SubMakeupModel(type: SubMakeupType.foundation, value: combinationDictionary.containsKey("makeup_intensity_foundation") ? combinationDictionary["makeup_intensity_foundation"] as double : 0, color: combinationDictionary.containsKey("makeup_foundation_color") ? combinationDictionary["makeup_foundation_color"] : []);
        model.foundationModel = foundation;
        
        SubMakeupModel lip = SubMakeupModel(type: SubMakeupType.lip, value: combinationDictionary["makeup_intensity_lip"] as double, color: combinationDictionary["makeup_lip_color"]);
        lip.isTwoColorLipstick = combinationDictionary["is_two_color"] == 1? true : false;
        lip.lipstickType = combinationDictionary["lip_type"] as int;
        model.lipstickModel = lip;
        SubMakeupModel blusher = SubMakeupModel(type: SubMakeupType.blusher, value: combinationDictionary["makeup_intensity_blusher"] as double, color: combinationDictionary["makeup_blusher_color"]);
        model.blusherModel = blusher;
        SubMakeupModel eyebrow = SubMakeupModel(type: SubMakeupType.eyebrow, value: combinationDictionary["makeup_intensity_eyeBrow"] as double, color: combinationDictionary.containsKey("makeup_eyeBrow_color") ? combinationDictionary["makeup_eyeBrow_color"] : []);
        eyebrow.isBrowWarp = combinationDictionary["brow_warp"] == 0 ? false : true;
        eyebrow.browWarpType = combinationDictionary["brow_warp_type"] as int;
        model.eyebrowModel = eyebrow;
        SubMakeupModel eyeShadow = SubMakeupModel(type: SubMakeupType.eyeShadow, value: combinationDictionary["makeup_intensity_eye"] as double, color: combinationDictionary["makeup_eye_color"]);
        model.eyeShadowModel = eyeShadow;
        SubMakeupModel eyeliner = SubMakeupModel(type: SubMakeupType.eyeliner, value: combinationDictionary["makeup_intensity_eyeLiner"] as double, color: combinationDictionary["makeup_eyeLiner_color"]);
        model.eyelinerModel = eyeliner;
        SubMakeupModel eyelash = SubMakeupModel(type: SubMakeupType.eyelash, value: combinationDictionary["makeup_intensity_eyelash"] as double, color: combinationDictionary.containsKey("makeup_eyelash_color") ? combinationDictionary["makeup_eyelash_color"] : []);
        model.eyelashModel = eyelash;
        SubMakeupModel highlight = SubMakeupModel(type: SubMakeupType.highlight, value: combinationDictionary.containsKey("makeup_intensity_highlight") ? combinationDictionary["makeup_intensity_highlight"] as double : 0, color: combinationDictionary.containsKey("makeup_highlight_color") ? combinationDictionary["makeup_highlight_color"] : []);
        model.highlightModel = highlight;
        SubMakeupModel shadow = SubMakeupModel(type: SubMakeupType.shadow, value: combinationDictionary.containsKey("makeup_intensity_shadow") ? combinationDictionary["makeup_intensity_shadow"] as double : 0, color: combinationDictionary.containsKey("makeup_shadow_color") ? combinationDictionary["makeup_shadow_color"] : []);
        model.shadowModel = shadow;
        SubMakeupModel pupil = SubMakeupModel(type: SubMakeupType.pupil, value: combinationDictionary.containsKey("makeup_intensity_pupil") ? combinationDictionary["makeup_intensity_pupil"] as double : 0, color: combinationDictionary.containsKey("makeup_pupil_color") ? combinationDictionary["makeup_pupil_color"] : []);
        model.pupilModel = pupil;
        
        // 允许自定义组合妆包含的各个子妆对应自定义子妆索引和颜色索引
        if (model.name == "性感") {
            foundation.index = 1;
            lip.index = 1;
            lip.defaultColorIndex = 0;
            blusher.index = 2;
            blusher.defaultColorIndex = 0;
            eyebrow.index = 1;
            eyebrow.defaultColorIndex = 0;
            eyeShadow.index = 2;
            eyeShadow.defaultColorIndex = 0;
            eyeliner.index = 1;
            eyeliner.defaultColorIndex = 0;
            eyelash.index = 4;
            eyelash.defaultColorIndex = 0;
            highlight.index = 2;
            highlight.defaultColorIndex = 0;
            shadow.index = 1;
            shadow.defaultColorIndex = 0;
            pupil.index = 0;
            pupil.defaultColorIndex = 0;
        } else if (model.name == "甜美") {
            foundation.index = 2;
            lip.index = 1;
            lip.defaultColorIndex = 1;
            blusher.index = 4;
            blusher.defaultColorIndex = 1;
            eyebrow.index = 4;
            eyebrow.defaultColorIndex = 0;
            eyeShadow.index = 1;
            eyeShadow.defaultColorIndex = 0;
            eyeliner.index = 2;
            eyeliner.defaultColorIndex = 1;
            eyelash.index = 2;
            eyelash.defaultColorIndex = 0;
            highlight.index = 1;
            highlight.defaultColorIndex = 1;
            shadow.index = 1;
            shadow.defaultColorIndex = 0;
            pupil.index = 0;
            pupil.defaultColorIndex = 0;
        } else if (model.name == "邻家") {
            foundation.index = 3;
            lip.index = 1;
            lip.defaultColorIndex = 2;
            blusher.index = 1;
            blusher.defaultColorIndex = 2;
            eyebrow.index = 2;
            eyebrow.defaultColorIndex = 0;
            eyeShadow.index = 1;
            eyeShadow.defaultColorIndex = 0;
            eyeliner.index = 6;
            eyeliner.defaultColorIndex = 2;
            eyelash.index = 1;
            eyelash.defaultColorIndex = 0;
            highlight.index = 0;
            highlight.defaultColorIndex = 0;
            shadow.index = 0;
            shadow.defaultColorIndex = 0;
            pupil.index = 0;
            pupil.defaultColorIndex = 0;
        } else if (model.name == "欧美") {
            foundation.index = 2;
            lip.index = 1;
            lip.defaultColorIndex = 3;
            blusher.index = 2;
            blusher.defaultColorIndex = 3;
            eyebrow.index = 1;
            eyebrow.defaultColorIndex = 0;
            eyeShadow.index = 4;
            eyeShadow.defaultColorIndex = 0;
            eyeliner.index = 5;
            eyeliner.defaultColorIndex = 3;
            eyelash.index = 5;
            eyelash.defaultColorIndex = 0;
            highlight.index = 2;
            highlight.defaultColorIndex = 3;
            shadow.index = 1;
            shadow.defaultColorIndex = 3;
            pupil.index = 0;
            pupil.defaultColorIndex = 0;
        } else if (model.name == "妩媚") {
            foundation.index = 4;
            lip.index = 1;
            lip.defaultColorIndex = 4;
            blusher.index = 3;
            blusher.defaultColorIndex = 4;
            eyebrow.index = 1;
            eyebrow.defaultColorIndex = 0;
            eyeShadow.index = 2;
            eyeShadow.defaultColorIndex = 1;
            eyeliner.index = 3;
            eyeliner.defaultColorIndex = 2;
            eyelash.index = 3;
            eyelash.defaultColorIndex = 0;
            highlight.index = 1;
            highlight.defaultColorIndex = 4;
            shadow.index = 0;
            shadow.defaultColorIndex = 0;
            pupil.index = 0;
            pupil.defaultColorIndex = 0;
        }
      }
      makeupModels.add(model);
    }
    makeups = makeupModels;
    // 默认选中第一个
    setSelectedIndex(1);
    notifyListeners();
  }

}