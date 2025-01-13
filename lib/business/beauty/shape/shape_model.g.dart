// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'shape_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ShapeModel _$ShapeModelFromJson(Map<String, dynamic> json) => ShapeModel(
      json['name'] as String,
      $enumDecode(_$BeautyShapeEnumMap, json['type']),
      (json['currentValue'] as num).toDouble(),
      (json['defaultValue'] as num).toDouble(),
      json['defaultValueInMiddle'] as bool,
      (json['supportDeviceLevel'] as num).toInt(),
    );

Map<String, dynamic> _$ShapeModelToJson(ShapeModel instance) =>
    <String, dynamic>{
      'name': instance.name,
      'type': _$BeautyShapeEnumMap[instance.type]!,
      'currentValue': instance.currentValue,
      'defaultValue': instance.defaultValue,
      'defaultValueInMiddle': instance.defaultValueInMiddle,
      'supportDeviceLevel': instance.supportDeviceLevel,
    };

const _$BeautyShapeEnumMap = {
  BeautyShape.cheekThinning: 0,
  BeautyShape.cheekV: 1,
  BeautyShape.cheekNarrow: 2,
  BeautyShape.cheekShort: 3,
  BeautyShape.cheekSmall: 4,
  BeautyShape.cheekbones: 5,
  BeautyShape.lowerJaw: 6,
  BeautyShape.eyeEnlarging: 7,
  BeautyShape.eyeCircle: 8,
  BeautyShape.chin: 9,
  BeautyShape.forehead: 10,
  BeautyShape.nose: 11,
  BeautyShape.mouth: 12,
  BeautyShape.lipThick: 13,
  BeautyShape.eyeHeight: 14,
  BeautyShape.canthus: 15,
  BeautyShape.eyeLid: 16,
  BeautyShape.eyeSpace: 17,
  BeautyShape.eyeRotate: 18,
  BeautyShape.longNose: 19,
  BeautyShape.philtrum: 20,
  BeautyShape.smile: 21,
  BeautyShape.browHeight: 22,
  BeautyShape.browSpace: 23,
  BeautyShape.browThick: 24,
};
