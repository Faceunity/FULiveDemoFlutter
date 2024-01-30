// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'skin_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SkinModel _$SkinModelFromJson(Map<String, dynamic> json) => SkinModel(
      json['name'] as String,
      $enumDecode(_$BeautySkinEnumMap, json['type']),
      (json['currentValue'] as num).toDouble(),
      (json['defaultValue'] as num).toDouble(),
      json['defaultValueInMiddle'] as bool,
      (json['ratio'] as num).toDouble(),
      json['differentiateDevicePerformance'] as bool,
      json['needsNPUSupport'] as bool,
    );

Map<String, dynamic> _$SkinModelToJson(SkinModel instance) => <String, dynamic>{
      'name': instance.name,
      'type': _$BeautySkinEnumMap[instance.type]!,
      'currentValue': instance.currentValue,
      'defaultValue': instance.defaultValue,
      'defaultValueInMiddle': instance.defaultValueInMiddle,
      'ratio': instance.ratio,
      'differentiateDevicePerformance': instance.differentiateDevicePerformance,
      'needsNPUSupport': instance.needsNPUSupport,
    };

const _$BeautySkinEnumMap = {
  BeautySkin.blurLevel: 0,
  BeautySkin.colorLevel: 1,
  BeautySkin.redLevel: 2,
  BeautySkin.sharpen: 3,
  BeautySkin.faceThreed: 4,
  BeautySkin.eyeBright: 5,
  BeautySkin.toothWhiten: 6,
  BeautySkin.removePouchStrength: 7,
  BeautySkin.removeNasolabialFoldsStrength: 8,
  BeautySkin.antiAcneSpot: 9,
  BeautySkin.clarity: 10,
};
