import 'dart:convert';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:fulive_plugin/beauty_plugin.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulivedemo_flutter/business/beauty/filter/filter_model.dart';
import 'package:fulivedemo_flutter/business/beauty/shape/shape_model.dart';
import 'package:fulivedemo_flutter/business/beauty/skin/skin_model.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';

class BeautyDataProvider {
  BeautyDataProvider._internal();
  static BeautyDataProvider? _instance;
  static BeautyDataProvider getInstance() {
    _instance ??= BeautyDataProvider._internal();
    return _instance!;
  }

  static dispose() {
    _instance = null;
  }

  // 设备是否高性能机型
  late int devicePerformanceLevel = DevicePerformanceLevel.levelTwo;
  late List<int> blackList = [];
  late List<SkinModel> skins = [];
  late List<ShapeModel> shapes = [];
  late List<FilterModel> filters = [];
  int selectedSkinIndex = -1;
  int selectedShapeIndex = -1;
  // 选中滤镜索引
  int selectedFilterIndex = 1;

  void initialize(VoidCallback callback) async {

    devicePerformanceLevel = await FaceunityPlugin.devicePerformanceLevel();
    if (Platform.isAndroid) {
      blackList = await FaceunityPlugin.restrictedSkinParams();
    }
    String? skinJsonString = await BeautyPlugin.getLocalSkin();
    if (skinJsonString != null) {
      // 本地数据 
      List jsonList = json.decode(skinJsonString);
      skins = skinsFromJsonArray(jsonList);
    } else {
      // 默认数据
      String fileNamePath = devicePerformanceLevel == DevicePerformanceLevel.levelMinusOne ? "jsons/beauty/beauty_skin_low.json" : "jsons/beauty/beauty_skin.json";
      String jsonString = await rootBundle.loadString(CommonUtil.fileNamed(fileNamePath));
      final jsonData = json.decode(jsonString);
      skins = skinsFromJsonArray(jsonData);
    }

    String? shapeJsonString = await BeautyPlugin.getLocalShape();
    if (shapeJsonString != null) {
      // 本地数据
      List jsonList = json.decode(shapeJsonString);
      shapes = shapesFromJsonArray(jsonList);
    } else {
      // 默认数据
      String fileNamePath = devicePerformanceLevel == DevicePerformanceLevel.levelMinusOne ? "jsons/beauty/beauty_shape_low.json" : "jsons/beauty/beauty_shape.json";
      String jsonString = await rootBundle.loadString(CommonUtil.fileNamed(fileNamePath));
      final jsonData = json.decode(jsonString);
      shapes = shapesFromJsonArray(jsonData);
    }

    String? filterJsonString = await BeautyPlugin.getLocalFilter();
    // ignore: unnecessary_null_comparison
    if (filterJsonString != null) {
      // 本地有保存的滤镜数据则从本地获取
      Map<String, dynamic> map = json.decode(filterJsonString);
      List jsonList = map["filters"];
      filters = filtersFromJsonArray(jsonList);
      String selectedKey = map["selectedFilterKey"];
      for (int i = 0; i < filters.length; i++) {
        if (filters[i].filterKey == selectedKey) {
          selectedFilterIndex = i;
          break;
        }
      }
    } else {
      // 本地没有保存的滤镜数据则使用默认数据
      String jsonString = await rootBundle.loadString(CommonUtil.fileNamed("jsons/beauty/beauty_filter.json"));
      final jsonData = json.decode(jsonString);
      filters = filtersFromJsonArray(jsonData);
      selectedFilterIndex = devicePerformanceLevel == DevicePerformanceLevel.levelMinusOne ? 0: 2;
    }
    callback();
  }
  
  List<SkinModel> skinsFromJsonArray(List jsonArray) {
    List<SkinModel> skinModels = [];
    for (Map<String, dynamic> item in jsonArray) {
      SkinModel skin = SkinModel.fromJson(item);
      skinModels.add(skin);
    }
    return skinModels;
  }

  List<ShapeModel> shapesFromJsonArray(List jsonArray) {
    List<ShapeModel> shapeModels = [];
    for (Map<String, dynamic> item in jsonArray) {
      ShapeModel shape = ShapeModel.fromJson(item);
      shapeModels.add(shape);
    }
    return shapeModels;
  }

  List<FilterModel> filtersFromJsonArray(List jsonArray) {
    List<FilterModel> filterModels = [];
    for (Map<String, dynamic> item in jsonArray) {
      FilterModel filter = FilterModel.fromJson(item);
      filterModels.add(filter);
    }
    return filterModels;
  }
}