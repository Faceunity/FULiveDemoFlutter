import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:json_annotation/json_annotation.dart';

part 'skin_model.g.dart';

@JsonSerializable()

class SkinModel {
  String name;
  BeautySkin type;
  double currentValue;
  double defaultValue;
  /// 默认值是否中位数
  bool defaultValueInMiddle;
  double ratio;
  /// 是否区分高低端机
  bool differentiateDevicePerformance;
  /// 是否需要 NPU 支持
  bool needsNPUSupport;

  SkinModel(this.name, this.type, this.currentValue, this.defaultValue, this.defaultValueInMiddle, this.ratio, this.differentiateDevicePerformance, this.needsNPUSupport);

  factory SkinModel.fromJson(Map<String, dynamic> json) => _$SkinModelFromJson(json);
  Map<String, dynamic> toJson() => _$SkinModelToJson(this);
}