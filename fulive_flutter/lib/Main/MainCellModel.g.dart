// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MainCellModel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MainCellModel _$MainCellModelFromJson(Map<String, dynamic> json) {
  return MainCellModel(
    json['maxFace'] as int,
    json['itemName'] as String,
    json['enable'] as bool,
    _$enumDecode(_$MainRoutersEnumMap, json['itemType']),
    json['conpareCode'] as int,
    (json['modules'] as List<dynamic>).map((e) => e as int).toList(),
    json['imagePath'] as String? ?? 'resource/images/homeView/beautyIcon.png',
  );
}

Map<String, dynamic> _$MainCellModelToJson(MainCellModel instance) =>
    <String, dynamic>{
      'maxFace': instance.maxFace,
      'itemName': instance.itemName,
      'enable': instance.enable,
      'itemType': _$MainRoutersEnumMap[instance.itemType],
      'conpareCode': instance.conpareCode,
      'modules': instance.modules,
      'imagePath': instance.imagePath,
    };

K _$enumDecode<K, V>(
  Map<K, V> enumValues,
  Object? source, {
  K? unknownValue,
}) {
  if (source == null) {
    throw ArgumentError(
      'A value must be provided. Supported values: '
      '${enumValues.values.join(', ')}',
    );
  }

  return enumValues.entries.singleWhere(
    (e) => e.value == source,
    orElse: () {
      if (unknownValue == null) {
        throw ArgumentError(
          '`$source` is not one of the supported values: '
          '${enumValues.values.join(', ')}',
        );
      }
      return MapEntry(unknownValue, enumValues.values.first);
    },
  ).key;
}

const _$MainRoutersEnumMap = {
  MainRouters.FULiveModelTypeBeautifyFace: 'FULiveModelTypeBeautifyFace',
  MainRouters.FULiveModelTypeMakeUp: 'FULiveModelTypeMakeUp',
  MainRouters.FULiveModelTypeItems: 'FULiveModelTypeItems',
  MainRouters.FULiveModelTypeAnimoji: 'FULiveModelTypeAnimoji',
  MainRouters.FULiveModelTypeHair: 'FULiveModelTypeHair',
  MainRouters.FULiveModelTypeLightMakeup: 'FULiveModelTypeLightMakeup',
  MainRouters.FULiveModelTypeARMarsk: 'FULiveModelTypeARMarsk',
  MainRouters.FULiveModelTypeHilarious: 'FULiveModelTypeHilarious',
  MainRouters.FULiveModelTypePoster: 'FULiveModelTypePoster',
  MainRouters.FULiveModelTypeExpressionRecognition:
      'FULiveModelTypeExpressionRecognition',
  MainRouters.FULiveModelTypeMusicFilter: 'FULiveModelTypeMusicFilter',
  MainRouters.FULiveModelTypeHahaMirror: 'FULiveModelTypeHahaMirror',
  MainRouters.FULiveModelTypeBody: 'FULiveModelTypeBody',
  MainRouters.FULiveModelTypeWholeAvatar: 'FULiveModelTypeWholeAvatar',
  MainRouters.FULiveModelTypeBGSegmentation: 'FULiveModelTypeBGSegmentation',
  MainRouters.FULiveModelTypeGestureRecognition:
      'FULiveModelTypeGestureRecognition',
  MainRouters.FULiveModelTypeLvMu: 'FULiveModelTypeLvMu',
  MainRouters.FULiveModelTypeQSTickers: 'FULiveModelTypeQSTickers',
};
