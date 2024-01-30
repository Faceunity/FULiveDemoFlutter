import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/classes/beauty/beauty_data_provider.dart';
import 'package:fulivedemo_flutter/classes/beauty/skin/skin_model.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulive_plugin/beauty_plugin.dart';

class SkinViewModel extends ChangeNotifier {

  List<SkinModel> get skins {
    return BeautyDataProvider.getInstance().skins;
  }

  // 设备是否高性能机型
  bool get highPerformanceDevice {
    return BeautyDataProvider.getInstance().highPerformanceDevice;
  }

  // 设备是否支持 NPU
  bool get supportsNPU {
    return BeautyDataProvider.getInstance().supportsNPU;
  }

  int get selectedIndex {
    return BeautyDataProvider.getInstance().selectedSkinIndex;
  }

  void setSelectedIndex(int index) {
    if (index < 0 || index >= skins.length) {
      return;
    }
    BeautyDataProvider.getInstance().selectedSkinIndex = index;
  }

  void setSkinIntensity(double intensity) {
    if (selectedIndex < 0 || selectedIndex >= skins.length) {
      return;
    }
    double current = intensity * skins[selectedIndex].ratio;
    skins[selectedIndex].currentValue = current;
    setIntensity(current, skins[selectedIndex].type);
  }

  void setIntensity(double intensity, BeautySkin type) {
    BeautyPlugin.setSkinIntensity(intensity, type.number);
  }

  void initialize() {
    setAllSkinValues();
    notifyListeners();
  }

  bool get isDefaultValue {
    for (SkinModel skin in skins) {
      int currentIntValue = skin.defaultValueInMiddle ? (skin.currentValue / skin.ratio * 100 - 50).toInt() : (skin.currentValue / skin.ratio * 100).toInt();
      int defaultIntValue = skin.defaultValueInMiddle ? (skin.defaultValue / skin.ratio * 100 - 50).toInt() : (skin.defaultValue / skin.ratio * 100).toInt();
      if (currentIntValue != defaultIntValue) {
          return false;
      }
    }
    return true;
  }

  // 设置当前所有美肤值
  void setAllSkinValues() {
    for (SkinModel skin in skins) {
      setIntensity(skin.currentValue, skin.type);
    }
  }

  // 恢复所有美肤值为默认
  void recoverAllSkinValuesToDefault() async {
    for (int i = 0; i < skins.length; i++) {
      skins[i].currentValue = skins[i].defaultValue;
      setIntensity(skins[i].currentValue, skins[i].type);
    }
    notifyListeners();
  }

  /// 保存美肤数据到本地
  void saveSkinsPersistently() {
    List jsonList = [];
    for (SkinModel skin in skins) {
      Map<String, dynamic> map = skin.toJson();
      jsonList.add(map);
    }
    String jsonString = json.encode(jsonList);
    BeautyPlugin.saveSkinToLocal(jsonString);
  }
}