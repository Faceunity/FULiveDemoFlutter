// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'FUMakeupSubTitleModel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FUMakeupSubTitleModel _$FUMakeupSubTitleModelFromJson(
    Map<String, dynamic> json) {
  return FUMakeupSubTitleModel(
    json['name'] as String,
    (json['subModels'] as List<dynamic>)
        .map((e) => FUMakeupSubModel.fromJson(e as Map<String, dynamic>))
        .toList(),
    subIndex: json['subIndex'] as int?,
  );
}

Map<String, dynamic> _$FUMakeupSubTitleModelToJson(
        FUMakeupSubTitleModel instance) =>
    <String, dynamic>{
      'name': instance.name,
      'subIndex': instance.subIndex,
      'subModels': instance.subModels,
    };
