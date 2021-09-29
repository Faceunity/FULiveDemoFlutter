import 'package:fulive_flutter/Beauty/FUBeautySubModel.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';

///美颜模块整体UI模型
class FUBeautyModel {
  late String? title;
  late FUBeautyDefine bizType; //业务类型，美肤、美型、滤镜、风格化
  late List<FUBeautySubModelUI> uiList; //UI模型 和 biz模型一一对应
  late List<FUBeautySubModel> bizList; //业务模型 和 UI模型一一对应
  late int selected; //当前选中的数据

  FUBeautyModel(this.title, this.bizType, this.uiList, this.bizList,
      {this.selected = 0});
}

class FUStyleModel extends FUBeautyModel {
  late FUBeautyModel skin;
  late FUBeautyModel shape;
  late FUBeautyModel filter;

  FUStyleModel(
      String? title,
      FUBeautyDefine bizType,
      List<FUBeautySubModelUI> uiList,
      List<FUBeautySubModel> bizList,
      int? seleted)
      : super(title, bizType, uiList, bizList) {
    skin = skinModel();
    shape = shapeModel();
    filter = filtersModel();
  }

  FUBeautyModel skinModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];
    List<String> titles = [
      "精细磨皮",
      "美白",
      "红润",
      "锐化",
      "亮眼",
      "美牙",
      "去黑眼圈",
      "去法令纹"
    ];
    List<double> ratio = [6.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0];
    for (var i = 0; i < FUBeautifySkin.FUBeautifySkinMax.index; i++) {
      uiList.add(FUBeautySubModelUI(titles[i], i));
      bizList.add(FUBeautySubModel(i, ratio[i],
          value: 0.0, strValue: '', defaultValue: 0.0));
    }
    return FUBeautyModel(
        '美肤', FUBeautyDefine.FUBeautyDefineSkin, uiList, bizList);
  }

  FUBeautyModel shapeModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];
    List<Map<String, dynamic>> titleAndValues = [
      {
        "key": "瘦脸",
        "value": 0.0,
      },
      {"key": "v脸", "value": 0.0},
      {"key": "窄脸", "value": 0.0},
      {"key": "小脸", "value": 0.0},
      {"key": "瘦颧骨", "value": 0.0},
      {"key": "瘦下颌骨", "value": 0.0},
      {"key": "大眼", "value": 0.0},
      {"key": "圆眼", "value": (0.0)},
      {"key": "下巴", "value": 0.5},
      {"key": "额头", "value": 0.5},
      {"key": "瘦鼻", "value": 0.0},
      {"key": "嘴型", "value": 0.5},
      {"key": "开眼角", "value": 0.0},
      {"key": "眼距", "value": 0.5},
      {"key": "眼睛角度", "value": 0.5},
      {"key": "长鼻", "value": 0.5},
      {"key": "缩人中", "value": 0.5},
      {"key": "微笑嘴角", "value": 0.0}
    ];
    List<double> ratio = [
      1.0,
      1.0,
      0.5,
      0.5,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0
    ];
    for (var i = 0; i < FUBeautifyShape.FUBeautifyShapeMax.index; i++) {
      String key = titleAndValues[i]['key'];
      double value = titleAndValues[i]['value'];
      uiList.add(FUBeautySubModelUI(key, i));
      bizList.add(FUBeautySubModel(i, ratio[i],
          value: value, strValue: '', defaultValue: value));
    }
    return FUBeautyModel(
        '美型', FUBeautyDefine.FUBeautyDefineShape, uiList, bizList);
  }

  FUBeautyModel filtersModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];
    uiList.add(FUBeautySubModelUI('原图', 0));
    bizList.add(FUBeautySubModel(0, 1.0,
        value: 0.4, defaultValue: 0.4, strValue: 'origin'));
    return FUBeautyModel(
        '滤镜', FUBeautyDefine.FUBeautyDefineFilter, uiList, bizList);
  }

  ///数据在native端已经存在，故这边数据只是形式上展示，实际没有用到
  void setStyle(int index) {
    switch (index) {
      case 1:
        style1();
        break;
      case 2:
        style2();
        break;
      case 3:
        style3();
        break;
      case 4:
        style4();
        break;
      case 5:
        style5();
        break;
      case 6:
        style6();
        break;
      case 7:
        style7();
        break;

      default:
        break;
    }
  }

  void style1() {
    //风格推荐只包含一条滤镜数据
    configFilterModel(0, 'bailiang1', '白亮1', 0.2);
    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.5);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 3.0);
    configSkinModel(FUBeautifySkin.FUBeautifySkinEyeBright.index, 0.35);
    configSkinModel(FUBeautifySkin.FUBeautifySkinToothWhiten.index, 0.25);

    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekThinning.index, 0.45);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekV.index, 0.08);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekSmall.index, 0.05);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeEyeEnlarging.index, 0.3);
  }

  void style2() {
    configFilterModel(0, 'ziran3', '自然3', 0.35);
    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.7);
    configSkinModel(FUBeautifySkin.FUBeautifySkinRedLevel.index, 0.3);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 3.0);
    configSkinModel(FUBeautifySkin.FUBeautifySkinEyeBright.index, 0.5);
    configSkinModel(FUBeautifySkin.FUBeautifySkinToothWhiten.index, 0.4);

    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekThinning.index, 0.3);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeIntensityNose.index, 0.5);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeEyeEnlarging.index, 0.25);
  }

  void style3() {
    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.6);
    configSkinModel(FUBeautifySkin.FUBeautifySkinRedLevel.index, 0.1);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 1.8);

    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekThinning.index, 0.3);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekSmall.index, 0.15);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeEyeEnlarging.index, 0.65);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeIntensityNose.index, 0.35);
  }

  void style4() {
    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.25);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 3.0);
  }

  void style5() {
    configFilterModel(0, 'fennen1', '粉嫩1', 0.4);

    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.7);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 3.0);

    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekThinning.index, 0.35);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeEyeEnlarging.index, 0.65);
  }

  void style6() {
    configFilterModel(0, 'fennen1', '粉嫩1', 0.4);

    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.5);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 3.0);
  }

  void style7() {
    configFilterModel(0, 'ziran5', '自然5', 0.55);

    configSkinModel(FUBeautifySkin.FUBeautifySkinColorLevel.index, 0.2);
    configSkinModel(FUBeautifySkin.FUBeautifySkinRedLevel.index, 0.65);
    configSkinModel(FUBeautifySkin.FUBeautifySkinBlurLevel.index, 3.3);

    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekThinning.index, 0.1);
    configShapeModel(FUBeautifyShape.FUBeautifyShapeCheekSmall.index, 0.05);
  }

  //配置美肤数据
  void configSkinModel(int index, double value) {
    this.skin.bizList[index].value = value;
  }

  //配置美型数据
  void configShapeModel(int index, double value) {
    this.shape.bizList[index].value = value;
  }

  //配置滤镜数据
  void configFilterModel(
      int index, String stringValue, String title, double value) {
    this.filter.uiList[index].title = title;
    this.filter.bizList[index].strValue = stringValue;
    this.filter.bizList[index].value = value;
    this.filter.bizList[index].defaultValue = value;
  }
}
