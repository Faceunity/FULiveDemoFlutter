// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'FUMakeupModel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FUMakeupModel _$FUMakeupModelFromJson(Map<String, dynamic> json) =>
    FUMakeupModel(
      json['title'] as String,
      json['imagePath'] as String,
      (json['value'] as num).toDouble(),
    )..canCustomSub = json['canCustomSub'] as bool?;

Map<String, dynamic> _$FUMakeupModelToJson(FUMakeupModel instance) =>
    <String, dynamic>{
      'title': instance.title,
      'imagePath': instance.imagePath,
      'value': instance.value,
      'canCustomSub': instance.canCustomSub,
    };
