//FUSubMakeupIndexModel 的外壳模型
class FUMakeupIndexModel {
  FUMakeupIndexModel(this.sub);

  late List<FUSubMakeupIndexModel> sub;

  factory FUMakeupIndexModel.fromJson(Map json) {
    return FUMakeupIndexModel((json['sub'] as List<dynamic>)
        .map((e) => FUSubMakeupIndexModel.fromJson(e as Map<String, dynamic>))
        .toList());
  }
}

class FUSubMakeupIndexModel {
  FUSubMakeupIndexModel(
      this.title, this.bundleIndex, this.colorIndex, this.value);
  late String title; //具体子妆名称
  late int bundleIndex; // 具体子妆的索引，如口红的红润、珠光、
  late int colorIndex; // 具体子妆颜色值索引
  late String value;
  factory FUSubMakeupIndexModel.fromJson(Map json) {
    return FUSubMakeupIndexModel(
        json['title'] as String,
        json['bundleIndex'] as int,
        json['colorIndex'] as int,
        json['value'] as String);
  }
}
