import 'dart:io';

class FUImagePixelRatio {
  ///pathPre：不包含图片名称的图片前缀路径，ex:resource/images/beauty/skin
  static String getImagePathWithRelativePathPre(String pathPre) {
    String imagePath = pathPre;
    //处理 '/'问题
    if (!imagePath.endsWith('/')) {
      imagePath = imagePath + '/';
    }
    if (Platform.isIOS) {
      ///在设备像素比率为1.8的设备上，.../2.0x/my_icon.png 将被选择。对于2.7的设备像素比率，.../3.0x/my_icon.png将被选择。
      /// iphone 上面不是这个规则，以 逻辑点距和像素点之间关系来决定几倍图 例如iphone 屏幕像素比和点距比例例为 1.78, 但是用的2x图片来显示，所以需要区分平台来加载，这里iOS 统一用高分别率图片。不区分了
      imagePath = imagePath + '3.0x/';
    } else {
      imagePath = pathPre;
    }
    return imagePath;
  }
}
