import 'package:json_annotation/json_annotation.dart';

part 'filter_model.g.dart';

@JsonSerializable()

class FilterModel {
  String filterKey;
  String filterName;
  double filterLevel;

  FilterModel(this.filterKey, this.filterName, this.filterLevel);

  factory FilterModel.fromJson(Map<String, dynamic> json) => _$FilterModelFromJson(json);
  Map<String, dynamic> toJson() => _$FilterModelToJson(this);
}