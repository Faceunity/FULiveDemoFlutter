//UI对应的模型
class FUBeautySubModelUI {
  late String title;
  late String imagePath; //图片模型路径
  late int type; //对应的就是具体美颜功能的子属性ex:磨破、瘦脸、红润。。。
  // late bool selected; //标记是否选中

  FUBeautySubModelUI(this.title, this.type, {this.imagePath = ''});
}

//业务数据对应的的模型
class FUBeautySubModel {
  late int type; //对应的就是具体美颜功能的子属性ex:磨破、瘦脸、红润。。。
  late double value;
  late bool midSlider; //slider 中间为起点
  late double defaultValue; //默认值
  late double ratio; //参数强度取值比例 进度条因为是归一化 所以要 除以ratio
  late String? strValue; //滤镜字符串值
  FUBeautySubModel(this.type, this.ratio,
      {this.value = 0.0,
      this.strValue = '',
      this.defaultValue = 0.0,
      this.midSlider = false});
}
