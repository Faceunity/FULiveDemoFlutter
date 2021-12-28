import 'package:flutter/services.dart' show rootBundle;
import 'dart:convert';
import 'dart:async';
import 'package:fulive_flutter/Main/MainCellModel.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
import 'dart:developer' as developer;

class MainDataModel {
  Future<List<List<MainCellModel>>> get getModels async {
    late String jsonString;
    try {
      jsonString =
          await rootBundle.loadString("resource/MainDataSourceAll.json");
    } catch (e) {
      developer.log(e.toString() + "json解析出错！！！");
    }

    List checkCodes = await FULivePlugin.getModuleCode();
    var checkCode0 = 0;
    var checkCode1 = 0;
    if (checkCodes.length > 1) {
      checkCode0 = checkCodes[0];
      checkCode1 = checkCodes[1];
    }

    final jsonResult = json.decode(jsonString);
    List<List<MainCellModel>> retList = [];
    for (List item in jsonResult) {
      late List<MainCellModel> temp = [];
      for (Map<String, dynamic> info in item) {
        //native 校验权限码
        MainCellModel model = MainCellModel.fromJson(info);
        for (var num in model.modules) {
          if (model.conpareCode == 0) {
            model.enable = (checkCode0 & num) > 0 ? true : false;
          } else {
            model.enable = (checkCode1 & num) > 0 ? true : false;
          }
        }
        temp.add(model);
      }
      retList.add(temp);
    }
    if (retList.length > 0) {
      return retList;
    } else {
      return [];
    }
  }
}
