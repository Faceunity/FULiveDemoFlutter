import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/business/beauty/beauty_data_provider.dart';
import 'package:fulivedemo_flutter/business/beauty/shape/shape_model.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulive_plugin/beauty_plugin.dart';

class ShapeViewModel extends ChangeNotifier {

  List<ShapeModel> get shapes {
    return BeautyDataProvider.getInstance().shapes;
  }

  // 设备是否高性能机型
  int get devicePerformanceLevel {
    return BeautyDataProvider.getInstance().devicePerformanceLevel;
  }

  int get selectedIndex {
    return BeautyDataProvider.getInstance().selectedShapeIndex;
  }

  void setSelectedIndex(int index) {
    if (index < 0 || index >= shapes.length) {
      return;
    }
    BeautyDataProvider.getInstance().selectedShapeIndex = index;
  }

  void setShapeIntensity(double intensity) {
    if (selectedIndex < 0 || selectedIndex >= shapes.length) {
      return;
    }
    shapes[selectedIndex].currentValue = intensity;
    setIntensity(intensity, shapes[selectedIndex].type);
  }

  void setIntensity(double intensity, BeautyShape type) {
    BeautyPlugin.setShapeIntensity(intensity, type.number);
  }

  void initialize() {
    setAllShapeValues();
    notifyListeners();
  }

  bool get isDefaultValue {
    for (ShapeModel shape in shapes) {
      int currentIntValue = shape.defaultValueInMiddle ? (shape.currentValue * 100 - 50).toInt() : (shape.currentValue * 100).toInt();
      int defaultIntValue = shape.defaultValueInMiddle ? (shape.defaultValue * 100 - 50).toInt() : (shape.defaultValue * 100).toInt();
      if (currentIntValue != defaultIntValue) {
          return false;
      }
    }
    return true;
  }

  // 设置当前所有美型值
  void setAllShapeValues() {
    for (ShapeModel shape in shapes) {
      setIntensity(shape.currentValue, shape.type);
    }
  }

  // 恢复所有美型值为默认
  void recoverAllShapeValuesToDefault() async {
    for (int i = 0; i < shapes.length; i++) {
      shapes[i].currentValue = shapes[i].defaultValue;
      setIntensity(shapes[i].currentValue, shapes[i].type);
    }
    notifyListeners();
  }

  /// 保存美型数据到本地
  void saveShapesPersistently() {
    List jsonList = [];
    for (ShapeModel shape in shapes) {
      Map<String, dynamic> map = shape.toJson();
      jsonList.add(map);
    }
    String jsonString = json.encode(jsonList);
    BeautyPlugin.saveShapeToLocal(jsonString);
  }
}