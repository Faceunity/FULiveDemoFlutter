// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'FUMakeupSubModel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FUMakeupSubModel _$FUMakeupSubModelFromJson(Map<String, dynamic> json) {
  return FUMakeupSubModel(
    json['title'] as String?,
    json['imagePath'] as String,
    (json['colors'] as List<dynamic>?)
        ?.map((e) =>
            (e as List<dynamic>).map((e) => (e as num).toDouble()).toList())
        .toList(),
    json['defaultColorIndex'] as int?,
    (json['value'] as num).toDouble(),
    colorIndex: json['colorIndex'] as int?,
  );
}

Map<String, dynamic> _$FUMakeupSubModelToJson(FUMakeupSubModel instance) =>
    <String, dynamic>{
      'title': instance.title,
      'imagePath': instance.imagePath,
      'colors': instance.colors,
      'colorIndex': instance.colorIndex,
      'defaultColorIndex': instance.defaultColorIndex,
      'value': instance.value,
    };
