// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'FUMakeupModel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FUMakeupModel _$FUMakeupModelFromJson(Map<String, dynamic> json) {
  return FUMakeupModel(
    json['title'] as String,
    json['imagePath'] as String,
    (json['value'] as num).toDouble(),
  );
}

Map<String, dynamic> _$FUMakeupModelToJson(FUMakeupModel instance) =>
    <String, dynamic>{
      'title': instance.title,
      'imagePath': instance.imagePath,
      'value': instance.value,
    };
