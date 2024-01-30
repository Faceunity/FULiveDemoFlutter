import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:json_annotation/json_annotation.dart';

part 'sub_makeup_model.g.dart';

@JsonSerializable()

class SubMakeupModel {
  // 子妆容类型
  SubMakeupType type;
  String? icon;
  // 名称
  String? title;
  // 子妆bundle名
  String? bundleName;
  // 妆容程度值
  double value;
  // 子妆当前颜色
  List<dynamic>? color;
  // 可选颜色数组
  List<List<dynamic>>? colors;
  // 默认选择颜色索引
  int? defaultColorIndex;
  // 子妆在自定义子妆可选列表中的索引
  int? index;

  /// 口红专用
  // 是否双色口红
  bool? isTwoColorLipstick;
  // 口红类型
  int? lipstickType;

  /// 眉毛专用
  // 是否使用眉毛变形
  bool? isBrowWarp;
  // 眉毛类型
  int? browWarpType;

  SubMakeupModel({required this.type, required this.value, required this.color});

  factory SubMakeupModel.fromJson(Map<String, dynamic> json) => _$SubMakeupModelFromJson(json);
  Map<String, dynamic> toJson() => _$SubMakeupModelToJson(this);

}