import 'package:flutter/material.dart';
import 'package:fulive_flutter/Beauty/FUBeautyModel.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';
import 'package:fulive_flutter/Beauty/FUBeautySubModel.dart';
import 'package:fulive_flutter/Tools/FUImagePixelRatio.dart';
import 'package:fulive_plugin/FUBeautyPlugin.dart';

///美颜模块数据管理
class FUBeautifyDataManager extends ChangeNotifier {
  late List<FUBeautyModel> dataList = [];
  late FUBeautySubModel curBizModel;
  late double curSliderValue;
  bool showDialog = false;
  FUBeautyDefine curBizType = FUBeautyDefine.FUBeautyMax;

  static List<FUBeautyModel> cacheList = [];

  void cacheData() {
    FUBeautifyDataManager.cacheList = dataList;
  }

  FUBeautifyDataManager() {
    generateDataSource();
    curBizModel = dataList[FUBeautyDefine.FUBeautyDefineSkin.index].bizList[0];
    curSliderValue = curBizModel.value;
  }

  bool showSlider() {
    bool flag = true;
    if (curBizType == FUBeautyDefine.FUBeautyDefineFilter) {
      flag = curBizModel.type == 0 ? false : true;
    }

    return flag;
  }

  ///修改当前页面业务
  void changeBizType(FUBeautyDefine bizType) {
    curBizType = bizType;
    notifyListeners();
  }

  ///检查当前bizType是否是默值, true 是 false 不是
  bool isDefaultValue(FUBeautyDefine bizType) {
    bool flag = true;

    if (bizType != FUBeautyDefine.FUBeautyDefineShape &&
        bizType != FUBeautyDefine.FUBeautyDefineSkin) {
      print("当前$bizType没有reset按钮！！！");
    } else {
      List<FUBeautySubModel> tempList = dataList[bizType.index].bizList;
      for (FUBeautySubModel bizModel in tempList) {
        if ((bizModel.value - bizModel.defaultValue).abs() > 0.01) {
          flag = false;
          break;
        }
      }
    }
    return flag;
  }

  void isShowDiglog(bool isShow) {
    showDialog = isShow;
    notifyListeners();
  }

  ///检测当前是否选中风格化
  bool checkStyle() {
    bool flag = false;
    FUBeautyModel model = dataList[FUBeautyDefine.FUBeautyDefineStyle.index];
    if (model.selected != 0) {
      flag = true;
    }

    return flag;
  }

  //更新sliderValue
  void updateSliderValue(newValue) {
    //传递native 生效
    if (curBizType == FUBeautyDefine.FUBeautyDefineFilter) {
      FUBeautyPlugin.setFilterParams(curBizType.index, curBizModel.type,
          newValue * curBizModel.ratio, curBizModel.strValue!);
    } else {
      FUBeautyPlugin.setFUBeautyParams(
          curBizType.index, curBizModel.type, newValue * curBizModel.ratio);
    }

    curSliderValue = newValue;

    bool flag = true;
    if (curBizType == FUBeautyDefine.FUBeautyMax) {
      flag = false;
    } else {
      if (curBizType == FUBeautyDefine.FUBeautyDefineSkin ||
          curBizType == FUBeautyDefine.FUBeautyDefineShape) {
        double value = newValue as double;
        curBizModel.value = value * curBizModel.ratio;
      } else if (curBizType == FUBeautyDefine.FUBeautyDefineFilter) {
        curBizModel.value = newValue as double;
      } else if (curBizType == FUBeautyDefine.FUBeautyDefineStyle) {
        //数据已经定制好，无需更新value值
      } else {
        flag = false;
        print('bizType:$curBizType 类型错误');
      }
    }
    //刷新触发
    // ignore: unnecessary_statements
    if (flag) {
      notifyListeners();
    }
  }

  //更新业务模型索引
  void updateModelIndex(FUBeautyDefine bizType, int type) {
    curBizType = bizType;
    bool flag = true;
    FUBeautyModel model;
    if (bizType == FUBeautyDefine.FUBeautyMax) {
      flag = false;
    } else {
      model = dataList[bizType.index];
      model.selected = type; //更新选中的index

      if (bizType != FUBeautyDefine.FUBeautyDefineStyle) {
        //更新当前业务模型,方便外部调用
        curBizModel = model.bizList[model.selected];

        if (bizType == FUBeautyDefine.FUBeautyDefineFilter) {
          FUBeautyPlugin.setFilterParams(bizType.index, curBizModel.type,
              curBizModel.value, curBizModel.strValue!);
        } else {
          FUBeautyPlugin.setFUBeautyParams(
              bizType.index, type, curBizModel.value);
        }
      } else {
        //风格化
        FUBeautyPlugin.setFUBeautyParams(bizType.index, type, 0);
      }
    }

    //刷新触发
    // ignore: unnecessary_statements
    if (flag) {
      notifyListeners();
    }
  }

  ///
  void reset() {
    FUBeautyPlugin.resetDefault(curBizType.index);

    if (curBizType == FUBeautyDefine.FUBeautyDefineSkin ||
        curBizType == FUBeautyDefine.FUBeautyDefineShape) {
      for (FUBeautySubModel bizItem in dataList[curBizType.index].bizList) {
        bizItem.value = bizItem.defaultValue;
      }
    }
    notifyListeners();
  }

  //生成数据源
  void generateDataSource() {
    if (FUBeautifyDataManager.cacheList.length != 0) {
      this.dataList = FUBeautifyDataManager.cacheList;
    } else {
      //美肤
      FUBeautyModel skinModel = getSkinModel();
      dataList.add(skinModel);

      //美型
      FUBeautyModel shapeModel = getShapeModel();
      dataList.add(shapeModel);
      //滤镜
      FUBeautyModel filterModel = getFilterModel();
      dataList.add(filterModel);
      //风格化
      FUBeautyModel styleModel = getStyleModel();
      dataList.add(styleModel);
    }
  }

  //美肤
  FUBeautyModel getSkinModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];
    List<String> titles = [
      '精细磨皮',
      '美白',
      '红润',
      '锐化',
      '亮眼',
      '美牙',
      '去黑眼圈',
      '去法令纹',
    ];

    String commonPre = FUImagePixelRatio.getImagePathWithRelativePathPre(
        "resource/images/beauty/skin/");

    List<String> imagePaths = List.generate(titles.length, (index) {
      String title = titles[index];
      return commonPre + title;
    });
    List<double> values = [4.2, 0.3, 0.3, 0.2, 0.0, 0.0, 0.0, 0.0];
    List<double> ratio = [6.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0];
    for (var i = 0; i < FUBeautifySkin.FUBeautifySkinMax.index; i++) {
      uiList.add(FUBeautySubModelUI(titles[i], i, imagePath: imagePaths[i]));
      bizList.add(FUBeautySubModel(i, ratio[i],
          value: values[i], strValue: '', defaultValue: values[i]));
    }

    return FUBeautyModel(
        '美肤', FUBeautyDefine.FUBeautyDefineSkin, uiList, bizList,
        selected: 0);
  }

  //美型
  FUBeautyModel getShapeModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];

    List<String> titles = [
      '瘦脸',
      'v脸',
      '窄脸',
      '小脸',
      '瘦颧骨',
      '瘦下颌骨',
      '大眼',
      '圆眼',
      '下巴',
      '额头',
      '瘦鼻',
      '嘴型',
      '开眼角',
      '眼距',
      '眼睛角度',
      '长鼻',
      '缩人中',
      '微笑嘴角',
    ];

    String commonPre = FUImagePixelRatio.getImagePathWithRelativePathPre(
        "resource/images/beauty/shape/");
    List<String> imagePaths = List.generate(titles.length, (index) {
      String title = titles[index];
      return commonPre + title;
    });
    List<bool> midSlider = [
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      true,
      false,
      true,
      false,
      true,
      true,
      true,
      true,
      false
    ];
    List<double> values = [
      0,
      0.5,
      0,
      0,
      0,
      0,
      0.4,
      0.0,
      0.3,
      0.3,
      0.5,
      0.4,
      0,
      0.5,
      0.5,
      0.5,
      0.5,
      0
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
      uiList.add(FUBeautySubModelUI(titles[i], i, imagePath: imagePaths[i]));
      bizList.add(FUBeautySubModel(i, ratio[i],
          value: values[i],
          strValue: '',
          defaultValue: values[i],
          midSlider: midSlider[i]));
    }
    return FUBeautyModel(
        '美型', FUBeautyDefine.FUBeautyDefineShape, uiList, bizList,
        selected: 1);
  }

  //滤镜
  FUBeautyModel getFilterModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];
    Map<String, dynamic> titleAndImagePath = getFilterJson();

    List<String> titlesKey = [
      "origin",
      "ziran1",
      "ziran2",
      "ziran3",
      "ziran4",
      "ziran5",
      "ziran6",
      "ziran7",
      "ziran8",
      "zhiganhui1",
      "zhiganhui2",
      "zhiganhui3",
      "zhiganhui4",
      "zhiganhui5",
      "zhiganhui6",
      "zhiganhui7",
      "zhiganhui8",
      "mitao1",
      "mitao2",
      "mitao3",
      "mitao4",
      "mitao5",
      "mitao6",
      "mitao7",
      "mitao8",
      "bailiang1",
      "bailiang2",
      "bailiang3",
      "bailiang4",
      "bailiang5",
      "bailiang6",
      "bailiang7",
      "fennen1",
      "fennen2",
      "fennen3",
      "fennen5",
      "fennen6",
      "fennen7",
      "fennen8",
      "lengsediao1",
      "lengsediao2",
      "lengsediao3",
      "lengsediao4",
      "lengsediao7",
      "lengsediao8",
      "lengsediao11",
      "nuansediao1",
      "nuansediao2",
      "gexing1",
      "gexing2",
      "gexing3",
      "gexing4",
      "gexing5",
      "gexing7",
      "gexing10",
      "gexing11",
      "xiaoqingxin1",
      "xiaoqingxin3",
      "xiaoqingxin4",
      "xiaoqingxin6",
      "heibai1",
      "heibai2",
      "heibai3",
      "heibai4"
    ];
    String commonPre = FUImagePixelRatio.getImagePathWithRelativePathPre(
        "resource/images/beauty/filter");

    List<String> imagePaths = List.generate(titlesKey.length, (index) {
      String title = titleAndImagePath[titlesKey[index]];
      return commonPre + title;
    });

    for (var i = 0; i < titlesKey.length; i++) {
      String titleKey = titlesKey[i];
      uiList.add(FUBeautySubModelUI(titleAndImagePath[titleKey], i,
          imagePath: imagePaths[i]));
      bizList.add(FUBeautySubModel(i, 1.0,
          value: 0.4, strValue: titleKey, defaultValue: 0.4));
    }
    //默认选中第二个
    // if (uiList.length > 1) {
    //   uiList[1].selected = true;
    // }
    return FUBeautyModel(
        '滤镜', FUBeautyDefine.FUBeautyDefineFilter, uiList, bizList,
        selected: 2);
  }

  //风格推荐
  FUBeautyModel getStyleModel() {
    List<FUBeautySubModelUI> uiList = [];
    List<String> titles = [
      '无',
      '风格1',
      '风格2',
      '风格3',
      '风格4',
      '风格5',
      '风格6',
      '风格7',
    ];
    String commonPre = FUImagePixelRatio.getImagePathWithRelativePathPre(
        "resource/images/beauty/style");
    List<String> imagePaths = List.generate(titles.length, (index) {
      String title = titles[index];
      return commonPre + title;
    });

    FUStyleModel model =
        FUStyleModel('风格推荐', FUBeautyDefine.FUBeautyDefineStyle, [], [], 0);
    for (var i = 0; i < FUBeautyStyleType.FUBeautyStyleMax.index; i++) {
      model.setStyle(i);
      uiList.add(FUBeautySubModelUI(titles[i], i, imagePath: imagePaths[i]));
    }
    model.uiList = uiList;
    return model;
  }

  Map<String, dynamic> getFilterJson() {
    return {
      "origin": "原图",
      "bailiang1": "白亮1",
      "bailiang2": "白亮2",
      "bailiang3": "白亮3",
      "bailiang4": "白亮4",
      "bailiang5": "白亮5",
      "bailiang6": "白亮6",
      "bailiang7": "白亮7",
      "fennen1": "粉嫩1",
      "fennen2": "粉嫩2",
      "fennen3": "粉嫩3",
      "fennen4": "粉嫩4",
      "fennen5": "粉嫩5",
      "fennen6": "粉嫩6",
      "fennen7": "粉嫩7",
      "fennen8": "粉嫩8",
      "gexing1": "个性1",
      "gexing2": "个性2",
      "gexing3": "个性3",
      "gexing4": "个性4",
      "gexing5": "个性5",
      "gexing6": "个性6",
      "gexing7": "个性7",
      "gexing8": "个性8",
      "gexing9": "个性9",
      "gexing10": "个性10",
      "gexing11": "个性11",
      "heibai1": "黑白1",
      "heibai2": "黑白2",
      "heibai3": "黑白3",
      "heibai4": "黑白4",
      "heibai5": "黑白5",
      "lengsediao1": "冷色调1",
      "lengsediao2": "冷色调2",
      "lengsediao3": "冷色调3",
      "lengsediao4": "冷色调4",
      "lengsediao5": "冷色调5",
      "lengsediao6": "冷色调6",
      "lengsediao7": "冷色调7",
      "lengsediao8": "冷色调8",
      "lengsediao9": "冷色调9",
      "lengsediao10": "冷色调10",
      "lengsediao11": "冷色调11",
      "nuansediao1": "暖色调1",
      "nuansediao2": "暖色调2",
      "nuansediao3": "暖色调3",
      "xiaoqingxin1": "小清新1",
      "xiaoqingxin2": "小清新2",
      "xiaoqingxin3": "小清新3",
      "xiaoqingxin4": "小清新4",
      "xiaoqingxin5": "小清新5",
      "xiaoqingxin6": "小清新6",
      "ziran1": "自然1",
      "ziran2": "自然2",
      "ziran3": "自然3",
      "ziran4": "自然4",
      "ziran5": "自然5",
      "ziran6": "自然6",
      "ziran7": "自然7",
      "ziran8": "自然8",
      "mitao1": "蜜桃1",
      "mitao2": "蜜桃2",
      "mitao3": "蜜桃3",
      "mitao4": "蜜桃4",
      "mitao5": "蜜桃5",
      "mitao6": "蜜桃6",
      "mitao7": "蜜桃7",
      "mitao8": "蜜桃8",
      "zhiganhui1": "质感灰1",
      "zhiganhui2": "质感灰2",
      "zhiganhui3": "质感灰3",
      "zhiganhui4": "质感灰4",
      "zhiganhui5": "质感灰5",
      "zhiganhui6": "质感灰6",
      "zhiganhui7": "质感灰7",
      "zhiganhui8": "质感灰8"
    };
  }
}

// class SliderValue exten
