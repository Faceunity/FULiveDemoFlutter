// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sub_makeup_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SubMakeupModel _$SubMakeupModelFromJson(Map<String, dynamic> json) =>
    SubMakeupModel(
      type: $enumDecode(_$SubMakeupTypeEnumMap, json['type']),
      value: (json['value'] as num).toDouble(),
      color: json['color'] as List<dynamic>?,
    )
      ..icon = json['icon'] as String?
      ..title = json['title'] as String?
      ..bundleName = json['bundleName'] as String?
      ..colors = (json['colors'] as List<dynamic>?)
          ?.map((e) => e as List<dynamic>)
          .toList()
      ..defaultColorIndex = json['defaultColorIndex'] as int?
      ..index = json['index'] as int?
      ..isTwoColorLipstick = json['isTwoColorLipstick'] as bool?
      ..lipstickType = json['lipstickType'] as int?
      ..isBrowWarp = json['isBrowWarp'] as bool?
      ..browWarpType = json['browWarpType'] as int?;

Map<String, dynamic> _$SubMakeupModelToJson(SubMakeupModel instance) =>
    <String, dynamic>{
      'type': _$SubMakeupTypeEnumMap[instance.type]!,
      'icon': instance.icon,
      'title': instance.title,
      'bundleName': instance.bundleName,
      'value': instance.value,
      'color': instance.color,
      'colors': instance.colors,
      'defaultColorIndex': instance.defaultColorIndex,
      'index': instance.index,
      'isTwoColorLipstick': instance.isTwoColorLipstick,
      'lipstickType': instance.lipstickType,
      'isBrowWarp': instance.isBrowWarp,
      'browWarpType': instance.browWarpType,
    };

const _$SubMakeupTypeEnumMap = {
  SubMakeupType.foundation: 0,
  SubMakeupType.lip: 1,
  SubMakeupType.blusher: 2,
  SubMakeupType.eyebrow: 3,
  SubMakeupType.eyeShadow: 4,
  SubMakeupType.eyeliner: 5,
  SubMakeupType.eyelash: 6,
  SubMakeupType.highlight: 7,
  SubMakeupType.shadow: 8,
  SubMakeupType.pupil: 9,
};
