import 'package:json_annotation/json_annotation.dart';

// user.g.dart 将在我们运行生成命令后自动生成
part 'FUMakeupModel.g.dart';

///这个标注是告诉生成器，这个类是需要生成Model类的
@JsonSerializable()

//组合妆模型
class FUMakeupModel extends Object {
  late String title;
  late String imagePath;
  late double value; //组合妆整体程度值

  //是否支持自定义子妆的组合装标识
  late bool canCustomSub = false;

  FUMakeupModel(this.title, this.imagePath, this.value);
  factory FUMakeupModel.fromJson(Map<String, dynamic> json) =>
      _$FUMakeupModelFromJson(json);

  Map<String, dynamic> toJson() => _$FUMakeupModelToJson(this);
}
