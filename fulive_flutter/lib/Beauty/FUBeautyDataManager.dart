import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fulive_flutter/Beauty/FUBeautyModel.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';
import 'package:fulive_flutter/Beauty/FUBeautySubModel.dart';
import 'package:fulive_flutter/Beauty/FUBeautySubModelUI.dart';
// import 'package:fulive_flutter/Tools/FUImagePixelRatio.dart';
import 'package:fulive_plugin/FUBeautyPlugin.dart';
import 'package:fulive_plugin/fulive_plugin.dart';

///美颜模块数据管理
class FUBeautifyDataManager extends ChangeNotifier {
  late List<FUBeautyModel> dataList = [];
  late FUBeautySubModel? curBizModel = null;
  late double curSliderValue = 0.0;
  bool showDialog = false;
  FUBeautyDefine curBizType = FUBeautyDefine.FUBeautyMax;

  static List<FUBeautyModel> cacheList = [];

  void cacheData() {
    FUBeautifyDataManager.cacheList = dataList;
  }

  FUBeautifyDataManager() {}

  //异步读取json 外部业务主动调用
  Future<List<FUBeautyModel>> generateDataSource() async {
    //从native 获取设备性能等级
    int performaceLevel = await FULivePlugin.getPerformanceLevel();

    List<FUBeautyModel> tempList = [];
    if (FUBeautifyDataManager.cacheList.length != 0) {
      tempList = FUBeautifyDataManager.cacheList;
    } else {
      //美肤
      String jsonStr_skin =
          await rootBundle.loadString("resource/beauty_skin.json");
      FUBeautyModel skinModel = getModel(jsonStr_skin,
          FUBeautyDefine.FUBeautyDefineSkin, '美肤', 0, performaceLevel);
      tempList.add(skinModel);

      //美型
      String jsonStr_shape =
          await rootBundle.loadString("resource/beauty_shape.json");
      FUBeautyModel shapeModel = getModel(jsonStr_shape,
          FUBeautyDefine.FUBeautyDefineShape, '美型', 1, performaceLevel);
      tempList.add(shapeModel);

      //滤镜
      String jsonStr_filter =
          await rootBundle.loadString("resource/beauty_filter.json");
      FUBeautyModel filterModel = getModel(jsonStr_filter,
          FUBeautyDefine.FUBeautyDefineFilter, '滤镜', 2, performaceLevel);
      tempList.add(filterModel);
    }

    this.dataList = tempList;
    return tempList;
  }

  void sliderShowRefresh() {
    //只是为了触发 slider 监听的consumer 刷新而已，具体逻辑已经写在 showSlider 方法里面
    notifyListeners();
  }

  bool showSlider() {
    if (curBizModel == null) {
      return false;
    }
    //先判断高低性能，低性能手机上部分选项不支持设置，所以不展示slider
    if (curBizModel!.isShowPerformanceLevelTips) {
      return false;
    }

    bool flag = true;
    if (curBizType == FUBeautyDefine.FUBeautyDefineFilter) {
      flag = curBizModel!.type == 0 ? false : true;
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
        var diff = ((bizModel.value - bizModel.defaultValue).abs() * 100).round();
        if (diff >= 1) {
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

  //更新sliderValue
  void updateSliderValue(newValue) {
    if (curBizModel == null) {
      return;
    }
    //传递native 生效
    if (curBizType == FUBeautyDefine.FUBeautyDefineFilter) {
      FUBeautyPlugin.setFilterParams(curBizType.index, curBizModel!.type,
          newValue * curBizModel!.ratio, curBizModel!.strValue!);
    } else {
      FUBeautyPlugin.setFUBeautyParams(
          curBizType.index, curBizModel!.type, newValue * curBizModel!.ratio);
    }

    curSliderValue = newValue;

    bool flag = true;
    if (curBizType == FUBeautyDefine.FUBeautyMax) {
      flag = false;
    } else {
      if (curBizType == FUBeautyDefine.FUBeautyDefineSkin ||
          curBizType == FUBeautyDefine.FUBeautyDefineShape) {
        double value = newValue as double;
        curBizModel!.value = value * curBizModel!.ratio;
      } else if (curBizType == FUBeautyDefine.FUBeautyDefineFilter) {
        curBizModel!.value = newValue as double;
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

      //更新当前业务模型,方便外部调用
      curBizModel = model.bizList[model.selected];

      if (bizType == FUBeautyDefine.FUBeautyDefineFilter) {
        FUBeautyPlugin.setFilterParams(bizType.index, curBizModel!.type,
            curBizModel!.value, curBizModel!.strValue!);
      } else {
        //低性能限制 就不发送给native 消息了
        if (!curBizModel!.isShowPerformanceLevelTips) {
          FUBeautyPlugin.setFUBeautyParams(
              bizType.index, type, curBizModel!.value);
        }
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

  // bizType 表示模块索引， 美颜、美肤、滤镜， seletedIndex,表示被选中的索引
  FUBeautyModel getModel(String jsonStr, FUBeautyDefine bizType, String title,
      int selectedIndex, int performanceLevel) {
    //int 和枚举转换
    FUDevicePerformanceLevel type =
        FUDevicePerformanceLevel.FUDevicePerformanceLevelHigh;
    if (performanceLevel == 0) {
      type = FUDevicePerformanceLevel.FUDevicePerformanceLevelLow;
    } else if (performanceLevel == 1) {
      type = FUDevicePerformanceLevel.FUDevicePerformanceLevelMedium;
    }

    List<FUBeautySubModelUI> uiList = [];
    List<FUBeautySubModel> bizList = [];
    final List jsonList = json.decode(jsonStr);
    jsonList.map((map) {
      //人为处理下，没有 isShowPerformanceLevelTips 字段就添加，使用可选类型?不安全
      Map<String, dynamic> newMap = Map.from(map);
      if (!map.containsKey("isShowPerformanceLevelTips")) {
        newMap['isShowPerformanceLevelTips'] = false;
      }
      FUBeautySubModel model = FUBeautySubModel.fromJson(newMap);
      if (model.differentiateDevicePerformance) {
        if (type != FUDevicePerformanceLevel.FUDevicePerformanceLevelHigh) {
          model.isShowPerformanceLevelTips = true;
        }
      }
      uiList.add(FUBeautySubModelUI(
        model.title,
        model.type,
        model.imagePath,
      ));
      bizList.add(model);
      return model;
    }).toList();

    return FUBeautyModel(title, bizType, uiList, bizList,
        selected: selectedIndex);
  }
}

// class SliderValue exten
