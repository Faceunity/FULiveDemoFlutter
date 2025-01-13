import 'package:fulivedemo_flutter/business/makeup/custom/sub_makeup_model.dart';
import 'package:json_annotation/json_annotation.dart';

part 'combination_makeup_model.g.dart';

@JsonSerializable()

class CombinationMakeupModel {
  String name;
  String bundleName;
  String icon;
  double value;
  bool? isCombined = false;
  // 是否允许自定义
  bool? isAllowedEdit = false;
  /// 滤镜名称（v8.0.0之后不需要）
  String? selectedFilter;
  /// 滤镜程度
  double? selectedFilterLevel;
  /// 粉底
  SubMakeupModel? foundationModel;
  /// 口红
  SubMakeupModel? lipstickModel;
  /// 腮红
  SubMakeupModel? blusherModel;
  /// 眉毛
  SubMakeupModel? eyebrowModel;
  /// 眼影
  SubMakeupModel? eyeShadowModel;
  /// 眼线
  SubMakeupModel? eyelinerModel;
  /// 睫毛
  SubMakeupModel? eyelashModel;
  /// 高光
  SubMakeupModel? highlightModel;
  /// 阴影
  SubMakeupModel? shadowModel;
  /// 美瞳
  SubMakeupModel? pupilModel;

  CombinationMakeupModel(this.name, this.bundleName, this.icon, this.value, this.isCombined, this.isAllowedEdit, this.selectedFilter, this.selectedFilterLevel, this.foundationModel, this.lipstickModel, this.blusherModel, this.eyebrowModel,this.eyeShadowModel, this.eyelinerModel, this.eyelashModel, this.highlightModel, this.shadowModel, this.pupilModel);

  factory CombinationMakeupModel.fromJson(Map<String, dynamic> json) => _$CombinationMakeupModelFromJson(json);
  Map<String, dynamic> toJson() => _$CombinationMakeupModelToJson(this);
}