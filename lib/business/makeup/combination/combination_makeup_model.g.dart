// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'combination_makeup_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CombinationMakeupModel _$CombinationMakeupModelFromJson(
        Map<String, dynamic> json) =>
    CombinationMakeupModel(
      json['name'] as String,
      json['bundleName'] as String,
      json['icon'] as String,
      (json['value'] as num).toDouble(),
      json['isCombined'] as bool?,
      json['isAllowedEdit'] as bool?,
      json['selectedFilter'] as String?,
      (json['selectedFilterLevel'] as num?)?.toDouble(),
      json['foundationModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['foundationModel'] as Map<String, dynamic>),
      json['lipstickModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['lipstickModel'] as Map<String, dynamic>),
      json['blusherModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['blusherModel'] as Map<String, dynamic>),
      json['eyebrowModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['eyebrowModel'] as Map<String, dynamic>),
      json['eyeShadowModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['eyeShadowModel'] as Map<String, dynamic>),
      json['eyelinerModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['eyelinerModel'] as Map<String, dynamic>),
      json['eyelashModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['eyelashModel'] as Map<String, dynamic>),
      json['highlightModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['highlightModel'] as Map<String, dynamic>),
      json['shadowModel'] == null
          ? null
          : SubMakeupModel.fromJson(
              json['shadowModel'] as Map<String, dynamic>),
      json['pupilModel'] == null
          ? null
          : SubMakeupModel.fromJson(json['pupilModel'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$CombinationMakeupModelToJson(
        CombinationMakeupModel instance) =>
    <String, dynamic>{
      'name': instance.name,
      'bundleName': instance.bundleName,
      'icon': instance.icon,
      'value': instance.value,
      'isCombined': instance.isCombined,
      'isAllowedEdit': instance.isAllowedEdit,
      'selectedFilter': instance.selectedFilter,
      'selectedFilterLevel': instance.selectedFilterLevel,
      'foundationModel': instance.foundationModel!.toJson(),
      'lipstickModel': instance.lipstickModel!.toJson(),
      'blusherModel': instance.blusherModel!.toJson(),
      'eyebrowModel': instance.eyebrowModel!.toJson(),
      'eyeShadowModel': instance.eyeShadowModel!.toJson(),
      'eyelinerModel': instance.eyelinerModel!.toJson(),
      'eyelashModel': instance.eyelashModel!.toJson(),
      'highlightModel': instance.highlightModel!.toJson(),
      'shadowModel': instance.shadowModel!.toJson(),
      'pupilModel': instance.pupilModel!.toJson(),
    };
