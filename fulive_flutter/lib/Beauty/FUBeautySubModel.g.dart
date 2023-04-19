// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'FUBeautySubModel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FUBeautySubModel _$FUBeautySubModelFromJson(Map<String, dynamic> json) =>
    FUBeautySubModel(
      json['title'] as String,
      json['imagePath'] as String,
      json['type'] as int,
      (json['ratio'] as num).toDouble(),
      (json['value'] as num).toDouble(),
      json['strValue'] as String?,
      (json['defaultValue'] as num).toDouble(),
      json['midSlider'] as bool,
      json['differentiateDevicePerformance'] as bool,
    )..isShowPerformanceLevelTips = json['isShowPerformanceLevelTips'] as bool;

Map<String, dynamic> _$FUBeautySubModelToJson(FUBeautySubModel instance) =>
    <String, dynamic>{
      'title': instance.title,
      'imagePath': instance.imagePath,
      'type': instance.type,
      'value': instance.value,
      'midSlider': instance.midSlider,
      'defaultValue': instance.defaultValue,
      'ratio': instance.ratio,
      'strValue': instance.strValue,
      'differentiateDevicePerformance': instance.differentiateDevicePerformance,
      'isShowPerformanceLevelTips': instance.isShowPerformanceLevelTips,
    };
