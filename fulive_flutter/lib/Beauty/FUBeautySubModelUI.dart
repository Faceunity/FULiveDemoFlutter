class FUBeautySubModelUI {
  late String title;
  late String imagePath; //图片模型路径
  late int type; //对应的就是具体美颜功能的子属性ex:磨破、瘦脸、红润。。。

  FUBeautySubModelUI(
    this.title,
    this.type,
    this.imagePath,
  );
}
