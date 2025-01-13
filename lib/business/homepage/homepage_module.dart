import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:json_annotation/json_annotation.dart';

part 'homepage_module.g.dart';

@JsonSerializable()

class HomepageModule {
  String title;
  bool enable;
  Module module;
  String authCode;

  HomepageModule(this.title, this.enable, this.module, this.authCode);

  factory HomepageModule.fromJson(Map<String, dynamic> json) => _$HomepageModuleFromJson(json);
  Map<String, dynamic> toJson() => _$HomepageModuleToJson(this);
}