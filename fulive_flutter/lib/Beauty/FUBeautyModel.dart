import 'package:fulive_flutter/Beauty/FUBeautySubModel.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';
import 'package:fulive_flutter/Beauty/FUBeautySubModelUI.dart';

///美颜模块整体UI模型
class FUBeautyModel {
  late String? title;
  late FUBeautyDefine bizType; //业务类型，美肤、美型、滤镜、风格化
  late List<FUBeautySubModelUI> uiList; //UI模型 和 biz模型一一对应
  late List<FUBeautySubModel> bizList; //业务模型 和 UI模型一一对应
  late int selected; //当前选中的数据(对应的是具体子项，磨破，美白 ...)
  FUBeautyModel(this.title, this.bizType, this.uiList, this.bizList,
      {this.selected = -1});
}
