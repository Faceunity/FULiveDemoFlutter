import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:vector_math/vector_math_64.dart' as v;

class FUColorSelectWidget extends StatefulWidget {
  @override
  _FUColorSelectWidgetState createState() => _FUColorSelectWidgetState();
}

class _FUColorSelectWidgetState extends State<FUColorSelectWidget> {
  ScrollController _controller = ScrollController();

  late double _offset = 0;

  @override
  void initState() {
    super.initState();
    _controller.addListener(() {
      double offset = _controller.offset;
      // print("当前滚动偏移位置:$offset");
      // setState(() {
      //   _offset = offset;
      // });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.red,
      height: 200,
      width: 50,
      child: ListView.separated(
          shrinkWrap: true,
          itemCount: 5,
          controller: _controller,
          separatorBuilder: (BuildContext context, int index) {
            return Container(
              height: 20,
            );
          },
          itemBuilder: (BuildContext context, int index) {
            return Container(
                width: 50,
                height: 50,
                child: Flow(
                  delegate: _CustomlayoutChildDelegate(index, _offset),
                  children: [
                    Container(
                      decoration: BoxDecoration(
                          color: Colors.green,
                          borderRadius: BorderRadius.circular(25.0)),
                      width: 50,
                      height: 20,
                    )
                  ],
                ));
          }),
    );
  }
}

class _CustomlayoutChildDelegate extends FlowDelegate {
  late int index;
  late double offset;
  final int totalCount = 5;
  _CustomlayoutChildDelegate(this.index, this.offset);
  @override
  void paintChildren(FlowPaintingContext context) {
    print(offset + 100);
    for (var i = 0; i < context.childCount; i++) {
      double centerX = context.getChildSize(i)!.width / 2;
      var scale = (totalCount / 2 + 1) * 0.2;
      context.paintChild(i,
          transform: Matrix4.compose(v.Vector3(centerX * (1 - scale), 0, 0),
              v.Quaternion(0, 0, 0, 0), v.Vector3(scale, scale, 1)));
      // context.paintChild(i,
      //     transform: new Matrix4.diagonal3Values(scale, scale, 1));
      // context.paintChild(i,
      //     transform: new Matrix4.translationValues(centerX, 0, 0));
    }
  }

  @override
  Size getSize(BoxConstraints constraints) {
    // OffsetBase;
    Size size = super.getSize(constraints);
    return size;
  }

  @override
  bool shouldRepaint(covariant FlowDelegate oldDelegate) {
    return oldDelegate != this;
  }
}
