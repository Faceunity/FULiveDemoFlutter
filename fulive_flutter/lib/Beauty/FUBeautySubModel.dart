import 'package:json_annotation/json_annotation.dart';

///子妆标题数据： 口红、腮红、眉毛、眼影标题名称
// user.g.dart 将在我们运行生成命令后自动生成
part 'FUBeautySubModel.g.dart';

///这个标注是告诉生成器，这个类是需要生成Model类的
@JsonSerializable(
  disallowUnrecognizedKeys: false,
)
//业务数据对应的的模型
class FUBeautySubModel {
  late String title; //UI显示名称
  late String imagePath; //icon 所在相对路径
  late int type; //对应的就是具体美颜功能的子属性ex:磨破、瘦脸、红润。。。
  late double value;
  late bool midSlider; //slider 中间为起点
  late double defaultValue; //默认值
  late double ratio; //参数强度取值比例 进度条因为是归一化 所以要 除以ratio
  late String? strValue; //滤镜字符串值
  late bool differentiateDevicePerformance; //是否区分设备高低性能表示
  late bool isShowPerformanceLevelTips; //是否提示性能等级
  FUBeautySubModel(
    this.title,
    this.imagePath,
    this.type,
    this.ratio,
    this.value,
    this.strValue,
    this.defaultValue,
    this.midSlider,
    this.differentiateDevicePerformance,
  ) {
    this.isShowPerformanceLevelTips = false;
  }

  factory FUBeautySubModel.fromJson(Map<String, dynamic> json) =>
      _$FUBeautySubModelFromJson(json);

  Map<String, dynamic> toJson() => _$FUBeautySubModelToJson(this);
}
