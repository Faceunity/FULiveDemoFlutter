import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/classes/beauty/beauty_data_provider.dart';
import 'package:fulivedemo_flutter/classes/beauty/filter/filter_model.dart';
import 'package:fulive_plugin/beauty_plugin.dart';

class FilterViewModel extends ChangeNotifier {
  
  List<FilterModel> get filters {
    return BeautyDataProvider.getInstance().filters;
  }

  // 选中滤镜索引
  int get selectedIndex {
    return BeautyDataProvider.getInstance().selectedFilterIndex;
  }

  void setSelectedIndex(int index) {
    if (index < 0 || index >= filters.length) {
      return;
    }
    BeautyDataProvider.getInstance().selectedFilterIndex = index;
    BeautyPlugin.selectFilter(filters[index].filterKey);
    setFilterLevel(filters[index].filterLevel);
  }

  void setFilterLevel(double level) {
    if (selectedIndex < 0 || selectedIndex >= filters.length) {
      return;
    }
    level = level < 0 ? 0 : (level > 1 ? 1 : level);
    filters[selectedIndex].filterLevel = level;
    BeautyPlugin.setFilterLevel(level);
  }

  void initialize() {
    setSelectedIndex(BeautyDataProvider.getInstance().selectedFilterIndex);
    notifyListeners();
  }

  /// 保存滤镜数据到本地
  void saveFiltersPersistently() {
    List jsonList = [];
    for (FilterModel filter in filters) {
      Map<String, dynamic> map = filter.toJson();
      jsonList.add(map);
    }
    // 滤镜需要同时保存选中滤镜列表和选中的滤镜
    Map<String, dynamic> data = {"filters":jsonList, "selectedFilterKey":filters[selectedIndex].filterKey};
    String jsonString = json.encode(data);
    BeautyPlugin.saveFilterToLocal(jsonString);
  }

}