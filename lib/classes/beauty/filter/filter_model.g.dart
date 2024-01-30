// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'filter_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FilterModel _$FilterModelFromJson(Map<String, dynamic> json) => FilterModel(
      json['filterKey'] as String,
      json['filterName'] as String,
      (json['filterLevel'] as num).toDouble(),
    );

Map<String, dynamic> _$FilterModelToJson(FilterModel instance) =>
    <String, dynamic>{
      'filterKey': instance.filterKey,
      'filterName': instance.filterName,
      'filterLevel': instance.filterLevel,
    };
