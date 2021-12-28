import 'dart:async';

import 'package:flutter/services.dart';

class FULivePlugin {
  static const MethodChannel channel = const MethodChannel('fulive_plugin');
  //标识流式数据通道
  static const String streamChannelPre = "streamChannel_";
//作为methodChannel_前缀标志，表明需要从naitive 获取methodChannel_实例作为参数
  static const String methodChannel = "methodChannel_";

  //页面顶部common模块，不属于基类
  static const String common = "Common";

  //自定义相机
  static const String imagePick = "ImagePick";

  //自定义相机
  static const String customRender = "FUCustomRender";

  static Future<String?> platformVersion(int count) async {
    final String? version =
        await channel.invokeMethod('getPlatformVersion', {'count': count});
    return version;
  }

  //获取native的权限
  static Future<List> getModuleCode() async {
    final List modules = await channel.invokeMethod('getModuleCode');
    return modules;
  }

  ///选择视频或则图片 0, 视频，1图片
  static Future<bool?> chooseImageOrVideo(int index) async {
    final bool? result = await channel.invokeMethod(methodChannel + imagePick, {
      "method": "chooseImageOrVideo",
      "value": index,
    });
    return result;
  }

  ///手动曝光点
  static Future manualExpose(double dx, double dy) async {
    channel.invokeMethod(common, {
      "method": "manualExpose",
      "value": [dx, dy]
    });
  }

  ///native 拍照
  static Future takePhoto() async {
    channel.invokeMethod(common, {
      "method": "takePhoto",
    });
  }

  ///native 开始录像
  static Future startRecord() async {
    channel.invokeMethod(common, {
      "method": "startRecord",
    });
  }

  ///native 开始录像
  static Future stopRecord() async {
    channel.invokeMethod(common, {
      "method": "stopRecord",
    });
  }

  ///销毁美颜插件
  static Future disposeCommon() async {
    channel.invokeMethod(common, {
      "method": "disposeCommon",
    });
  }

  //监听原生native 方法 calback 是外面传进来的回调，设置到setMethodCallHandler 让flutter 底部调用。放这里为了可复用。
  static Future listenNative(Function calback) async {
    channel.setMethodCallHandler((call) => calback(call));
  }

  ///开启模块流式监听，每帧获取 debug信息和检测人脸信息, 数据以json 格式返回{"debug": "debug内容", "hasFace": bool}
  static Future startBeautyStreamListen() async {
    channel.invokeMethod(streamChannelPre + common, {
      "method": "startBeautyStreamListen",
    });
  }

  static Future customImageDispose() async {
    channel.invokeMethod(customRender, {
      "method": "customImageDispose",
    });
  }

  ///自定义视频渲染的对比按钮事件
  static Future renderOrigin(bool compare) async {
    channel.invokeMethod(common, {"method": "renderOrigin", "value": compare});
  }

  //自定义视频清理定时器(iOS)，否则会循环引用
  static Future selectedImageOrVideo(int type) async {
    channel.invokeMethod(
        customRender, {"method": "selectedImageOrVideo", "value": type});
  }

  ///自定义视频渲染的对比按钮事件
  static Future customRenderOrigin(bool compare) async {
    channel.invokeMethod(
        customRender, {"method": "customRenderOrigin", "value": compare});
  }

  ///自定义视频渲染的对比按钮事件
  static Future customRenderConfig() async {
    channel.invokeMethod(customRender, {"method": "customRenderConfig"});
  }

  ///开启自定义视频和相册模块的流式监听
  static Future startCustomRenderStremListen() async {
    channel.invokeMethod(streamChannelPre + customRender, {
      "method": "startCustomRenderStremListen",
    });
  }

  ///调整亮度 value [-2, 2]
  static Future adjustSpotlight(double value) async {
    channel.invokeMethod(common, {"method": "adjustSpotlight", "value": value});
  }

  ///分辨率选择, return == 0 表示当前设备不支持设备分辨率，1 表示正常支持， -1 表示参数传递错误
  static Future<int?> chooseSessionPreset(int index) async {
    final int? result = await channel.invokeMethod(common, {
      "method": "chooseSessionPreset",
      "value": index,
    });
    return result;
  }

  ///切换摄像头
  static Future changeCameraFront(bool front) async {
    channel
        .invokeMethod(common, {"method": "changeCameraFront", "value": front});
  }

  ///切换相机码流格式
  static Future changeCameraFormat() async {
    channel.invokeMethod(common, {"method": "changeCameraFormat"});
  }

  ///获取native 视频播放进度接口
  static Future requestVideoProcess() async {
    channel.invokeMethod(methodChannel + customRender, {
      "method": "requestVideoProcess",
    });
  }

  ///自定义视频渲染的对比按钮事件
  static Future customVideoRePlay() async {
    channel.invokeMethod(customRender, {"method": "customVideoRePlay"});
  }

  ///下载视频或者图片，type == 0 图片，1 视频
  static Future downLoadCustomRender(int type) async {
    channel.invokeMethod(
        customRender, {"method": "downLoadCustomRender", "value": type});
  }

  // //common widget 即将显示
  // static Future flutterWillAppear() async {
  //   channel.invokeMethod(common, {
  //     "method": "flutterWillAppear",
  //   });
  // }

  // //common widget 即将消失
  // static Future flutterWillDisappear() async {
  //   channel.invokeMethod(common, {
  //     "method": "flutterWillDisappear",
  //   });
  // }

  static Future imagePickDispose() async {
    channel.invokeListMethod(imagePick, {
      "method": "imagePickDispose",
    });
  }
}
