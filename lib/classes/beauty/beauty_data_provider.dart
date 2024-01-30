import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:fulive_plugin/beauty_plugin.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'package:fulivedemo_flutter/classes/beauty/filter/filter_model.dart';
import 'package:fulivedemo_flutter/classes/beauty/shape/shape_model.dart';
import 'package:fulivedemo_flutter/classes/beauty/skin/skin_model.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';

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

  late List<SkinModel> skins = [];
  late List<ShapeModel> shapes = [];
  late List<FilterModel> filters = [];

  int selectedSkinIndex = -1;

  int selectedShapeIndex = -1;

  // 选中滤镜索引
  int selectedFilterIndex = 1;

  // 设备是否高性能机型
  late bool highPerformanceDevice = true;
  // 设备是否支持 NPU
  late bool supportsNPU = false; 

  void initialize(VoidCallback callback) async {
    String? skinJsonString = await BeautyPlugin.getLocalSkin();
    if (skinJsonString != null) {
      // 本地数据 
      List jsonList = json.decode(skinJsonString);
      skins = skinsFromJsonArray(jsonList);
    } else {
      // 默认数据
      String jsonString = await rootBundle.loadString(CommonUtil.fileNamed("jsons/beauty/beauty_skin.json"));
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
      String jsonString = await rootBundle.loadString(CommonUtil.fileNamed("jsons/beauty/beauty_shape.json"));
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
      selectedFilterIndex = 2;
    }

    highPerformanceDevice = await FaceunityPlugin.isHighPerformanceDevice();
    supportsNPU = await FaceunityPlugin.isNPUSupported();

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