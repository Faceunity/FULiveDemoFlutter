import 'package:flutter/widgets.dart';

class FUCustomController extends ScrollController {
  FUCustomController(this.attachCallback);

  //position attach到controller 上回调
  final Function? attachCallback;

  @override
  void attach(ScrollPosition position) {
    super.attach(position);
    print("attach Position");
    if (attachCallback != null) {
      attachCallback!(position);
    }
  }
}
