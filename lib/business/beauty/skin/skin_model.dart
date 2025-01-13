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
  int supportDeviceLevel;

  SkinExtraModel? extra;

  SkinModel(this.name, this.type, this.currentValue, this.defaultValue, this.defaultValueInMiddle, this.ratio, this.supportDeviceLevel, this.extra);

  factory SkinModel.fromJson(Map<String, dynamic> json) => _$SkinModelFromJson(json);
  Map<String, dynamic> toJson() => _$SkinModelToJson(this);
}

@JsonSerializable()
class SkinExtraModel {
  String key;
  String title;
  double value;
  double defaultValue;
  String leftText;
  String rightText;
  int supportDeviceLevel;

  SkinExtraModel(this.key, this.title, this.value, this.defaultValue, this.leftText, this.rightText, this.supportDeviceLevel);

  factory SkinExtraModel.fromJson(Map<String, dynamic> json) => _$SkinExtraModelFromJson(json);
  Map<String, dynamic> toJson() => _$SkinExtraModelToJson(this);
}