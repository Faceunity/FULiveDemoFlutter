import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:fulivedemo_flutter/business/homepage/homepage_module.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulive_plugin/fulive_plugin.dart';

class HomepageModulesData {
  Future<List<HomepageModule>> get getData async {
    late String jsonString;
    try {
      jsonString = await rootBundle.loadString(CommonUtil.fileNamed("jsons/homepage/homepage_data_source.json"));
    } catch(e) {
      if (kDebugMode) {
        print("json解析出错$e");
      }
    }
    final jsonData = json.decode(jsonString);
    List<HomepageModule> modules = [];
    int moduleCode0 = await FaceunityPlugin.getModuleCode(0);
    int moduleCode1 = await FaceunityPlugin.getModuleCode(1);
    for (Map<String, dynamic> item in jsonData) {
      HomepageModule module = HomepageModule.fromJson(item);
      if (module.authCode.isEmpty) {
        module.enable = true;
      } else {
        String authCodeString = module.authCode;
        // 分割权限码
        List<String> codes = authCodeString.split("-");
        int code0 = int.parse(codes[0]),code1 = int.parse(codes[1]);
        // 判断是否有权限(moduleCode0对比code0，moduleCode1对比code1)
        module.enable = (moduleCode0 & code0) > 0 || (moduleCode1 & code1) > 0;
      }
      modules.add(module);
    }
    return modules;
  }
}