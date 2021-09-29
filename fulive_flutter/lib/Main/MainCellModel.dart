import 'package:json_annotation/json_annotation.dart';
import 'package:fulive_flutter/Main/MainRouterDefine.dart';

// user.g.dart 将在我们运行生成命令后自动生成
part 'MainCellModel.g.dart';

///这个标注是告诉生成器，这个类是需要生成Model类的
@JsonSerializable()
class MainCellModel {
  MainCellModel(this.maxFace, this.itemName, this.enable, this.itemType,
      this.conpareCode, this.modules, this.imagePath);
  late int maxFace; //支持最大人脸数
  late String itemName; //cell 名称
  late bool enable; //是否可点击
  late MainRouters itemType; //标识当前是哪个cell
  late int conpareCode; //权限码标识符，前32位还是后32位 == 0 前32位，==1 后32位
  late List<int> modules; //权限码

  @JsonKey(defaultValue: "resource/images/homeView/beautyIcon.png")
  late String imagePath; //icon图片路径

  factory MainCellModel.fromJson(Map<String, dynamic> json) =>
      _$MainCellModelFromJson(json);

  Map<String, dynamic> toJson() => _$MainCellModelToJson(this);
}
