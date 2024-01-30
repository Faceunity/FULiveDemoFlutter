import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

class CommonUtil {
  // 本地图片路径
  static String assetImagePath(String name) {
    return 'lib/resource/images/$name.png';
  }

  // 本地图片
  static AssetImage assetImageNamed(String name) {
    return AssetImage(assetImagePath(name));
  }

  static String fileNamed(String name) {
    return 'lib/resource/$name';
  }
}

void showAlertDialog ({
  required BuildContext context,
  String? title,
  String? content,
  String cancelTitle = "取消",
  String comformTitle = "确定",
  final VoidCallback? cancelPressed,
  final VoidCallback? comformPressed
}) {
  showDialog(
    context: context,
    barrierDismissible: false,
    builder: (context) {
      return AlertDialog(
        title: Center(
          child: Text(title ?? ""),
        ),
        content: Center(
          heightFactor: 1,
          child: Text(content ?? ""),
        ),
        actionsAlignment: MainAxisAlignment.center,
        actions: [
          SizedBox(
            width: 100,
            child: TextButton(
              style: const ButtonStyle(
                backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                overlayColor: MaterialStatePropertyAll(Colors.transparent)
              ),
              onPressed: (){
                if (cancelPressed != null) {
                  cancelPressed();
                }
                Navigator.of(context).pop();
              }, 
              child: Text(cancelTitle, style: const TextStyle(color: Color.fromARGB(255, 44, 46, 48), fontSize: 16),)
            ),
          ),
          SizedBox(
            width: 100,
            child: TextButton(
              style: const ButtonStyle(
                backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                overlayColor: MaterialStatePropertyAll(Colors.transparent)
              ),
              onPressed: (){
                if (comformPressed != null) {
                  comformPressed();
                }
                Navigator.of(context).pop();
              }, 
              child: Text(comformTitle, style: const TextStyle(color: Color.fromARGB(255, 94, 199, 254), fontSize: 16),)
            ),
          ),
        ],
      );
    },
  );
}

void showCommonToast({
    required BuildContext context,
    required String content,
    double? contentFontSize,
    ToastGravity? gravity,
    Color? backgroundColor,
    Duration? duration,
  }) {
    // 先移除
    FToast().removeQueuedCustomToasts();
    FToast().init(context).showToast(
      toastDuration: duration ?? const Duration(milliseconds: 1500),
      gravity: gravity ?? ToastGravity.TOP,
      child: Container(
        padding: const EdgeInsets.fromLTRB(20, 8, 20, 8),
        decoration: BoxDecoration(
          color: backgroundColor ?? const Color.fromARGB(188, 5, 15, 20),
          borderRadius: BorderRadius.circular(4.0)
        ),
        child: Text(content,
          style: TextStyle(
            fontSize: contentFontSize ?? 13.0,
            color: Colors.white
          ),
        ),
      )
    );
  }