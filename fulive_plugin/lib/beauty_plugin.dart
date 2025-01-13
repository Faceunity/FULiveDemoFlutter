import 'package:fulive_plugin/fulive_plugin.dart';

class BeautyPlugin {
  static const _channel = FaceunityPlugin.methodChannel;

  static const int moduleCode = 0;

  /// 加载美颜
  /// @note 两端插件初始化时已经加载了美颜，可以不用再加载美颜，卸妆后可以调用该方法重新加载
  static Future<void> loadBeauty() async {
    await _channel.invokeMethod("loadBeauty", {"module" : moduleCode});
  }

  // 卸载美颜（释放内存）
  static Future<void> unloadBeauty() async {
    _channel.invokeMethod("unloadBeauty", {"module" : moduleCode});
  }

  // 切换滤镜
  static Future<void>  selectFilter(String key) async {
    _channel.invokeMethod("selectFilter", {"module" : moduleCode, "arguments" : [{"key" : key}]});
  }

  // 设置滤镜值
  static Future<void> setFilterLevel(double level) async {
    _channel.invokeMethod("setFilterLevel", {"module" : moduleCode, "arguments" : [{"level" : level}]});
  }

  /// 设置美肤程度值
  /// @param intensity 程度值
  /// @param type 美肤类型(参考枚举 BeautySkin)
  static Future<void> setSkinIntensity(double intensity, int type) async {
    _channel.invokeMethod("setSkinIntensity", {"module" : moduleCode, "arguments" : [{"intensity" : intensity}, {"type" : type}]});
  }

  /// 设置美型程度值
  /// @param intensity 程度值
  /// @param type 美肤类型(参考枚举 BeautyShape)
  static Future<void> setShapeIntensity(double intensity, int type) async {
    _channel.invokeMethod("setShapeIntensity", {"module" : moduleCode, "arguments" : [{"intensity" : intensity}, {"type" : type}]});
  }

  /// 设置美颜参数，依赖 key
  /// @param key key
  /// @param value 美颜参数
  static Future<void> setBeautyParam(String key, dynamic value) async {
    _channel.invokeMethod("setBeautyParam", {"module": moduleCode, "arguments" : [{"key" : key}, {"value" : value}]});
  }

  /// 保存美肤数据到本地
  /// @param jsonString 由美肤模型数组转换的 json 字符串
  /// @note 原生各端自行决定数据持久化方式
  static Future<void> saveSkinToLocal(String jsonString) async {
    _channel.invokeMethod("saveSkinToLocal", {"module" : moduleCode, "arguments" : [{"json" : jsonString}]});
  }

  /// 保存美型数据到本地
  /// @param jsonString 由美型模型数组转换的 json 字符串
  /// @note 原生各端自行决定数据持久化方式
  static Future<void> saveShapeToLocal(String jsonString) async {
    _channel.invokeMethod("saveShapeToLocal", {"module" : moduleCode, "arguments" : [{"json" : jsonString}]});
  }

  /// 保存滤镜数据到本地
  /// @param jsonString 由滤镜模型数组加选中的滤镜名组合map转换的 json 字符串
  /// @note 格式为 {"filters":[], "selectedFilterKey", ""}
  /// @note 原生各端自行决定数据持久化方式
  static Future<void> saveFilterToLocal(String jsonString) async {
    _channel.invokeMethod("saveFilterToLocal", {"module" : moduleCode, "arguments" : [{"json" : jsonString}]});
  }

  /// 获取本地美肤数据
  /// @note 保存的 json 字符串直接返回
  static Future<String?> getLocalSkin() async {
    String? jsonString = await _channel.invokeMethod("getLocalSkin", {"module" : moduleCode});
    return jsonString;
  }

  /// 获取本地美型数据
  /// @note 保存的 json 字符串直接返回
  static Future<String?> getLocalShape() async {
    String? jsonString = await _channel.invokeMethod("getLocalShape", {"module" : moduleCode});
    return jsonString;
  }

  /// 获取本地滤镜数据
  /// @note 保存的 json 字符串直接返回
  static Future<String?> getLocalFilter() async {
    String? jsonString = await _channel.invokeMethod("getLocalFilter", {"module" : moduleCode});
    return jsonString;
  }
}