// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'homepage_module.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

HomepageModule _$HomepageModuleFromJson(Map<String, dynamic> json) =>
    HomepageModule(
      json['title'] as String,
      json['enable'] as bool,
      $enumDecode(_$ModuleEnumMap, json['module']),
      json['authCode'] as String,
    );

Map<String, dynamic> _$HomepageModuleToJson(HomepageModule instance) =>
    <String, dynamic>{
      'title': instance.title,
      'enable': instance.enable,
      'module': _$ModuleEnumMap[instance.module]!,
      'authCode': instance.authCode,
    };

const _$ModuleEnumMap = {
  Module.beauty: 0,
  Module.makeup: 1,
  Module.sticker: 2,
};
