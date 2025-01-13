// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'customized_makeup_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CustomizedMakeupModel _$CustomizedMakeupModelFromJson(
        Map<String, dynamic> json) =>
    CustomizedMakeupModel(
      json['name'] as String,
      (json['subMakeups'] as List<dynamic>)
          .map((e) => SubMakeupModel.fromJson(e as Map<String, dynamic>))
          .toList(),
    )..selectedSubMakeupIndex =
        (json['selectedSubMakeupIndex'] as num?)?.toInt();

Map<String, dynamic> _$CustomizedMakeupModelToJson(
        CustomizedMakeupModel instance) =>
    <String, dynamic>{
      'name': instance.name,
      'subMakeups': instance.subMakeups,
      'selectedSubMakeupIndex': instance.selectedSubMakeupIndex,
    };
