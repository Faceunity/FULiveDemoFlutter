import 'package:json_annotation/json_annotation.dart';

///子妆: 口红、腮红、眉毛、眼影。。。。

// user.g.dart 将在我们运行生成命令后自动生成
part 'FUMakeupSubModel.g.dart';

///这个标注是告诉生成器，这个类是需要生成Model类的
@JsonSerializable()
class FUMakeupSubModel extends Object {
  //比如口红子妆里面雾、润泽、珠光等提示语,粉底无提示语，所以可能为空值
  late String? title;
  //子妆对应的具体的UI展示图片路径
  late String imagePath;
  //子妆对应的具体颜色 一般子妆只有[rgba] 1组颜色 所以colors[index] = 4 , 眼影比较特殊， 12位/4 = 3 组颜色 ,colors[colors.length].length = 12
  late List<List<double>>? colors;
  //当前选中的具体颜色索引
  late int? colorIndex;
  late int? defaultColorIndex;
  //子妆强度值
  late double value;
  FUMakeupSubModel(this.title, this.imagePath, this.colors,
      this.defaultColorIndex, this.value,
      {this.colorIndex = 0});
  factory FUMakeupSubModel.fromJson(Map<String, dynamic> json) =>
      _$FUMakeupSubModelFromJson(json);

  Map<String, dynamic> toJson() => _$FUMakeupSubModelToJson(this);
}
