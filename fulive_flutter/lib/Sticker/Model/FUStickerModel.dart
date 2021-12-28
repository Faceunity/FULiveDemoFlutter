import 'package:json_annotation/json_annotation.dart';

// user.g.dart 将在我们运行生成命令后自动生成
part 'FUStickerModel.g.dart';

///这个标注是告诉生成器，这个类是需要生成Model类的
@JsonSerializable()

//组合妆模型
class FUStickerModel extends Object {
  //json 存放图片名称，业务层拼接前缀路径，最终得到完整的图像加载路径
  late String imageName;
  FUStickerModel(
    this.imageName,
  );
  factory FUStickerModel.fromJson(Map<String, dynamic> json) =>
      _$FUStickerModelFromJson(json);

  Map<String, dynamic> toJson() => _$FUStickerModelToJson(this);
}
