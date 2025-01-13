import 'package:fulivedemo_flutter/business/beauty/beauty_data_provider.dart';
import 'package:fulivedemo_flutter/business/beauty/filter/filter_view_model.dart';
import 'package:fulivedemo_flutter/business/beauty/shape/shape_view_model.dart';
import 'package:fulivedemo_flutter/business/beauty/skin/skin_view_model.dart';
import 'package:fulive_plugin/beauty_plugin.dart';

class BeautyViewModel {

  // 当前选中的功能栏索引
  int selectedIndex = -1;

  final SkinViewModel skinViewModel = SkinViewModel();
  final ShapeViewModel shapeViewModel = ShapeViewModel();
  final FilterViewModel filterViewModel = FilterViewModel();

  // 初始化当前美颜
  void initialize() {
    BeautyPlugin.loadBeauty();
    BeautyDataProvider.getInstance().initialize((){
      skinViewModel.initialize();
      shapeViewModel.initialize();
      filterViewModel.initialize();
    });
  }

  void dispose() {
    BeautyDataProvider.dispose();
    BeautyPlugin.unloadBeauty();
  }

  // 保存美颜所有数据到本地
  void saveBeautyData() {
    skinViewModel.saveSkinsPersistently();
    shapeViewModel.saveShapesPersistently();
    filterViewModel.saveFiltersPersistently();
  }

}