import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class FUPopPosition {
  final double x;
  final double y;
  FUPopPosition(this.x, this.y);
}

class FUPopupMenu extends StatefulWidget {
  FUPopupMenu(
    GlobalKey<State<StatefulWidget>> popKey, {
    required Key key,
    required this.points,
    required this.bgColor,
    required this.offsetY,
    required this.format,
    this.foramtCallback,
    this.jumpCumstomCallback,
  }) : super(key: key);

  //决定三角形位置
  final List<FUPopPosition> points;
  final Color bgColor;
  //三角形Y轴偏移量，决定整个背景偏移父视图Y多少
  final double offsetY;

  //选中分辨率回调
  final Function? foramtCallback;
  //进入某个具体自定义视频或图片回调
  final Function? jumpCumstomCallback;

  final int format;

  // : assert(points.length > 1, "points 长度必须大于1");

  _FUPopupMenuState createState() => _FUPopupMenuState();
}

class _FUPopupMenuState extends State<FUPopupMenu> {
  @override
  void initState() {
    super.initState();
    _segmentValue = widget.format.toString();
  }

  late String _segmentValue;
  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      painter: CustomPopupMenu(widget.points, widget.bgColor, widget.offsetY),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Container(
            height: widget.offsetY,
            // color: Colors.red,
          ),
          Container(
            alignment: Alignment.center,
            // color: Colors.red,
            child: CupertinoSegmentedControl(
                children: {
                  '0': Container(
                    alignment: Alignment.center,
                    width: 80,
                    child: Text(
                      '480 x 640',
                      style: TextStyle(color: Colors.black),
                    ),
                  ),
                  '1': Container(
                    alignment: Alignment.center,
                    width: 80,
                    child: Text(
                      '720 x 1280',
                      style: TextStyle(color: Colors.black),
                    ),
                  ),
                  '2': Container(
                    alignment: Alignment.center,
                    width: 90,
                    child: Text(
                      '1080 x 1920',
                      style: TextStyle(color: Colors.black),
                    ),
                  ),
                },
                groupValue: _segmentValue,
                unselectedColor: Color(0x0),
                selectedColor: Colors.white,
                borderColor: Color(0x0),
                pressedColor: Color(0x0),
                onValueChanged: (value) {
                  if (widget.foramtCallback != null) {
                    widget.foramtCallback!(int.parse(value as String));
                  }
                  setState(() {
                    _segmentValue = value as String;
                  });
                }),
          ),
          Divider(
            height: 10,
            thickness: 0.5,
            indent: 30,
            endIndent: 30,
            color: Colors.white,
          ),
          GestureDetector(
              behavior: HitTestBehavior.opaque,
              onTap: () {
                if (widget.jumpCumstomCallback != null) {
                  widget.jumpCumstomCallback!();
                }
              },
              child: Container(
                padding: EdgeInsets.fromLTRB(30, 0, 30, 0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      "载入图片或视频",
                      style: TextStyle(color: Colors.white),
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
    );
  }
}

//带三角形的背景
class CustomPopupMenu extends CustomPainter {
  Paint _paint = Paint();
  //决定三角形位置
  final List<FUPopPosition> points;
  final Color bgColor;
  //三角形Y轴偏移量，决定整个背景偏移父视图Y多少
  final double offsetY;

  CustomPopupMenu(this.points, this.bgColor, this.offsetY)
      : assert(points.length > 1, "points 长度必须大于1");

  @override
  void paint(Canvas canvas, Size size) {
    Path path = Path()
      ..moveTo(0, offsetY)
      ..lineTo(points[0].x, points[0].y);
    for (var point in points.sublist(1)) {
      path.lineTo(point.x, point.y);
    }
    path
      ..lineTo(size.width, offsetY)
      ..lineTo(size.width, size.height)
      ..lineTo(0, size.height)
      ..lineTo(0, offsetY);

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
