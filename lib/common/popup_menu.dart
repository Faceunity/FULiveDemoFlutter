// import 'dart:ffi';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class PopPosition {
  final double x;
  final double y;
  PopPosition(this.x, this.y);
}

// ignore: must_be_immutable
class PopupMenu extends StatefulWidget {
  PopupMenu({
    super.key, 
    required this.points,
    required this.bgColor,
    required this.offsetY,
    this.top,
    this.height,
    this.width,
    this.selectedIndex,
    this.formatCallback,
    this.jumpCustomCallback,
    this.clickBlankCallBack
  });

  //决定三角形位置
  final List<PopPosition> points;
  final Color bgColor;
  // 距离顶部距离
  final double? top;
  // 高度
  final double? height;
  // 宽度
  final double? width;
  //三角形Y轴偏移量，决定整个背景偏移父视图Y多少
  final double offsetY;

  //选中分辨率回调
  final Function? formatCallback;
  //进入某个具体自定义视频或图片回调
  final Function? jumpCustomCallback;
  //点击空白处
  final Function? clickBlankCallBack;

  int? selectedIndex = 0;
  
  @override
  State<StatefulWidget> createState() {
    return _PopupMenuState();
  }
}

class _PopupMenuState extends State<PopupMenu> /* with SingleTickerProviderStateMixin*/ {
  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: GestureDetector(
        onTap: () {
          if (widget.clickBlankCallBack != null) {
            widget.clickBlankCallBack!();
          }
        },
        child: Stack(
          alignment: Alignment.topCenter,
          children: [
            Container(color: Colors.transparent, width: ScreenUtil.getScreenW(context), height: ScreenUtil.getScreenH(context),),
            Positioned(
              top: widget.top,
              height: widget.height,
              width: widget.width,
              child: CustomPaint(
                painter: CustomPopupMenu(widget.points, widget.bgColor, widget.offsetY),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    SizedBox(
                      height: widget.offsetY,
                    ),
                    Container(
                      alignment: Alignment.center,
                      child: CupertinoSegmentedControl(
                          children: {
                            0: Container(
                              alignment: Alignment.center,
                              width: 80,
                              child: Text(
                                '480x640',
                                style: TextStyle(color: widget.selectedIndex == 0 ? Colors.black : Colors.white, fontSize: 13, fontWeight: FontWeight.w500),
                              ),
                            ),
                            1: Container(
                              alignment: Alignment.center,
                              width: 80,
                              child: Text(
                                '720x1280',
                                style: TextStyle(color: widget.selectedIndex == 1 ? Colors.black : Colors.white, fontSize: 13, fontWeight: FontWeight.w500),
                              ),
                            ),
                            2: Container(
                              alignment: Alignment.center,
                              width: 90,
                              child: Text(
                                '1080x1920',
                                style: TextStyle(color: widget.selectedIndex == 2 ? Colors.black : Colors.white, fontSize: 13, fontWeight: FontWeight.w500),
                              ),
                            ),
                          },
                          groupValue: widget.selectedIndex,
                          unselectedColor: const Color(0x00000000),
                          selectedColor: Colors.white,
                          borderColor: Colors.white,
                          pressedColor: const Color(0x00000000),
                          onValueChanged: (value) {
                            if (widget.formatCallback != null) {
                              widget.formatCallback!(value);
                            }
                            setState(() {
                              widget.selectedIndex = value;
                            });
                          }),
                    ),
                    const Divider(
                      height: 10,
                      thickness: 0.5,
                      indent: 30,
                      endIndent: 30,
                      color: Color.fromARGB(50, 229, 229, 229),
                    ),
                    GestureDetector(
                        behavior: HitTestBehavior.opaque,
                        onTap: () {
                          if (widget.jumpCustomCallback != null) {
                            widget.jumpCustomCallback!();
                          }
                        },
                        child: Container(
                          padding: const EdgeInsets.fromLTRB(30, 0, 30, 0),
                          child: const Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                "载入图片或视频",
                                style: TextStyle(color: Colors.white, fontSize: 13),
                              ),
                              Icon(
                                Icons.keyboard_arrow_right_outlined,
                                color: Colors.white,
                                size: 25,
                              )
                            ],
                          ),
                        )),
                  ],
                ),
              ),
            ),
          ],
        ),
      )
    );
  }
}

//带三角形的背景
class CustomPopupMenu extends CustomPainter {
  final Paint _paint = Paint();
  //决定三角形位置
  final List<PopPosition> points;
  final Color bgColor;
  //三角形Y轴偏移量，决定整个背景偏移父视图Y多少
  final double offsetY;

  CustomPopupMenu(this.points, this.bgColor, this.offsetY)
      : assert(points.length > 1, "points 长度必须大于1");

  @override
  
  void paint(Canvas canvas, Size size) {
    //用Rect构建矩形
    Rect rect = Rect.fromLTWH(0, offsetY, size.width, size.height - offsetY);
    //根据上面的矩形,构建一个圆角矩形
    RRect rrect = RRect.fromRectAndRadius(rect, const Radius.circular(5.0));
    canvas.drawRRect(
      rrect, _paint
        ..color = bgColor
    );
    // 画三角形
    Path path = Path()
      ..moveTo(points[0].x, points[0].y);
    for (var point in points.sublist(1)) {
      path.lineTo(point.x, point.y);
    }

    canvas.drawPath(
        path,
        _paint
          ..style = PaintingStyle.fill
          ..color = bgColor);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}


class PopupViewRoute extends PopupRoute {
  Widget child;
  PopupViewRoute({required this.child});

  @override
  Color? get barrierColor => null;

  @override
  bool get barrierDismissible => true;

  @override
  String? get barrierLabel => null;

  @override
  Widget buildPage(BuildContext context, Animation<double> animation, Animation<double> secondaryAnimation) {
    return child;
  }

  @override
  Duration get transitionDuration => const Duration(milliseconds: 0);
}



