import 'FUMakeupSubModel.dart';
import 'package:json_annotation/json_annotation.dart';

///子妆标题数据： 口红、腮红、眉毛、眼影标题名称
// user.g.dart 将在我们运行生成命令后自动生成
part 'FUMakeupSubTitleModel.g.dart';

///这个标注是告诉生成器，这个类是需要生成Model类的
@JsonSerializable()
class FUMakeupSubTitleModel extends Object {
  late String name; //子妆标题，比如粉底、口红、腮红、眉毛等
  //子妆对应索引，比如口红子妆里面雾、润泽、珠光等
  late int? subIndex = 0;
  //具体子妆
  late List<FUMakeupSubModel> subModels;

  FUMakeupSubTitleModel(this.name, this.subModels, {this.subIndex = 0});
  factory FUMakeupSubTitleModel.fromJson(Map<String, dynamic> json) =>
      _$FUMakeupSubTitleModelFromJson(json);

  Map<String, dynamic> toJson() => _$FUMakeupSubTitleModelToJson(this);
}
