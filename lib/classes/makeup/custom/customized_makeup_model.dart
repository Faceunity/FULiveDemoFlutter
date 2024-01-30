import 'package:fulivedemo_flutter/classes/makeup/custom/sub_makeup_model.dart';
import 'package:json_annotation/json_annotation.dart';

part 'customized_makeup_model.g.dart';

@JsonSerializable()

class CustomizedMakeupModel {
  String name;
  List<SubMakeupModel> subMakeups;
  int? selectedSubMakeupIndex = 0;

  CustomizedMakeupModel(this.name, this.subMakeups);

  factory CustomizedMakeupModel.fromJson(Map<String, dynamic> json) => _$CustomizedMakeupModelFromJson(json);
  Map<String, dynamic> toJson() => _$CustomizedMakeupModelToJson(this);
}