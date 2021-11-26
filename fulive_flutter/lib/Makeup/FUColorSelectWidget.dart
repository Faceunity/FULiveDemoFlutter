import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupColorModel.dart';
import 'package:vector_math/vector_math_64.dart' as v;

//实际滑动选中
typedef SelectedColorCallback = Function(int index);

class FUColorSelectWidget extends StatefulWidget {
  FUColorSelectWidget(Key key, this.callback) : super(key: key);

  final SelectedColorCallback? callback;
  @override
  FUColorSelectWidgetState createState() => FUColorSelectWidgetState();
}

class FUColorSelectWidgetState extends State<FUColorSelectWidget>
    with WidgetsBindingObserver {
  late CustomScrollController _controller =
      CustomScrollController((ScrollPosition position) {
    //猜测是ScrollView 每次在attach Position 时候 如果执行一个offset = 0.0的animation动画，实际都默认不滚动！所以设置为1.0时候有偏移量才会实际滚动一下。看源码找结论！
    position.animateTo(_offset,
        duration: const Duration(milliseconds: 200), curve: Curves.ease);
  });

  //滚动偏移量，计算定位当前滚动到那个item使用
  late double _offset = 1.0;
  //最大可滚动距离，计算定位当前滚动到那个item使用
  late double _maxScrollExtent = 240;

  late List<List<Color>> _colorsList = [];
  late bool _isShow = false;

  //标记是否应该放处理弃滑动结束监听通知
  late bool _giveUp = false;

  //外部调用刷新颜色值,index 表示当前显示的颜色UI索引
  void reloadColors(List<List<Color>> colors, bool isShow, int index) {
    setState(() {
      if (colors.length != 0) {
        _colorsList = colors;
      }
      _isShow = isShow;
    });

    if (isShow) {
      _offset = cacleOffsetWithIndex(colors.length, index, _maxScrollExtent);
      // print("偏移量为$_offset");
      if (_offset == 0.0) {
        //猜测是ScrollView 每次在attach Position 时候 如果执行一个offset = 0.0的animation动画，实际都默认不滚动！所以设置为1.0时候有偏移量才会实际滚动一下。看源码找结论！
        _offset = 1.0;
      }
      // ignore: invalid_use_of_protected_member
      if (_controller.positions.isNotEmpty) {
        _controller.animateTo(_offset,
            duration: const Duration(milliseconds: 200), curve: Curves.ease);
      }
    }
  }

  //为了方便把颜色和对应的渐变色坐标一起返回[0]是颜色数组，[1]渐进色是坐标数组
  List<dynamic> getLeainerColorsWithColor(List<Color> color) {
    List<Color> linearColors;
    List<double> linearStops;
    //makeuoSubUI选择不同子妆会刷新notifyListeners，监听并且刷新颜色UI

    if (color.length == 1) {
      linearColors = [color[0], color[0]];
      linearStops = [0.0, 1.0];
    } else if (color.length == 2) {
      linearColors = [
        color[0],
        color[0],
        color[0],
        color[1],
        color[1],
        color[1]
      ];
      linearStops = [0.0, 0.49, 0.50, 0.50, 0.51, 1.0];
    } else if (color.length == 3) {
      linearColors = [
        color[0],
        color[0],
        color[0],
        color[1],
        color[1],
        color[1],
        color[2],
        color[2],
        color[2]
      ];
      linearStops = [0, 0.33, 0.34, 0.34, 0.66, 0.67, 0.67, 0.99, 1.0];
    } else if (color.length == 4) {
      linearColors = [
        color[0],
        color[0],
        color[0],
        color[1],
        color[1],
        color[1],
        color[2],
        color[2],
        color[2],
        color[3],
        color[3],
        color[3]
      ];
      linearStops = [
        0,
        0.24,
        0.25,
        0.25,
        0.49,
        0.50,
        0.74,
        0.75,
        0.75,
        0.99,
        0.99,
        1.0
      ];
    } else {
      linearColors = [];
      linearStops = [];
    }

    return [linearColors, linearStops];
  }

  double cacleOffsetWithIndex(int length, int index, double maxScrollExtent) {
    return (maxScrollExtent / (length - 1)) * index;
  }

  //校准结束滑动之后的位置
  double _calibrate(int length, double offset, double maxScrollExtent) {
    //每个颜色组件所占用的单位偏移量
    double unint = maxScrollExtent / (length - 1);

    int index = (offset / unint).round();

    // print("!!!!!滑动到Index == $index 附近!!!!!!");

    return index * unint;
  }

  //主要是计算缩放因子
  FUMakeupColorModel cacluteRatio(
      int length, int index, double offset, double maxScrollExtent) {
    //每个颜色组件所占用的单位偏移量
    double unint = maxScrollExtent / (length - 1);
    //当前index 组件在sliver 的位置
    double curOffset = index * unint;
    //当前组件可见范围: 距离中心点上下两个单位自+ 中心的一个单位 = 一共三个
    double visiableOffset = 3 * unint;
    double ratio = 0.0;
    final double delta = offset - curOffset;

    if (delta.abs() < visiableOffset) {
      ratio = 1.0 - (delta.abs() / visiableOffset) * 0.9;
    } else {
      //不在上面范围内直接显示不可见
      ratio = 0.0;
    }
    return FUMakeupColorModel(curOffset, maxScrollExtent, ratio, index);
  }

  @override
  void dispose() {
    super.dispose();
    _controller.dispose();
  }

  @override
  void initState() {
    super.initState();

    _controller.addListener(() {
      double offset = _controller.offset;
      double maxScrollExtent = _controller.position.maxScrollExtent;
      // print("offset:$offset");
      if (offset < 0) {
        offset = 0;
      } else if (offset >= maxScrollExtent) {
        offset = maxScrollExtent;
      }

      setState(() {
        _offset = offset;
        _maxScrollExtent = maxScrollExtent;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> itemList;
    List<FUMakeupColorModel> models;
    //创建颜色列表
    int length = _colorsList.length;
    models = [];

    itemList = List.generate(length, (index) {
      FUMakeupColorModel model =
          cacluteRatio(length, index, _offset, _maxScrollExtent);
      models.add(model);
      var arr = getLeainerColorsWithColor(_colorsList[index]);
      List<Color> colors = arr[0];
      List<double> stops = arr[1];
      return GestureDetector(
          onTap: () {
            //点击事件引起的滑动代理监听都忽略掉，都是重复监听回调。
            _giveUp = true;
            //处理UI
            _controller.animateTo(model.offset,
                duration: const Duration(milliseconds: 250),
                curve: Curves.ease);

            //处理插件接口通信
            if (widget.callback != null) {
              widget.callback!(index);
            }
          },
          child: Container(
            width: 50,
            height: 50,
            child: Flow(
              delegate: _CustomlayoutChildDelegate(model.ratio),
              children: [
                Container(
                  decoration: BoxDecoration(
                      gradient: LinearGradient(
                          //3色渐变色数组，这样设置，如果是二色或者是单色可以直接少设置点
                          colors: colors,
                          stops: stops,
                          tileMode: TileMode.repeated,
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter),
                      borderRadius: BorderRadius.circular(25.0)),
                )
              ],
            ),
          ));
    });

    return Visibility(
        visible: _isShow,
        child: Container(
            height: 300,
            width: 50,
            child: NotificationListener<ScrollNotification>(
              onNotification: (ScrollNotification notification) {
                if (notification is ScrollEndNotification) {
                  if (!_giveUp) {
                    print("滑动停止");
                    //执行完_controller.animateTo会导致在一次调用ScrollEndNotification回调，这次是多余的。所以需要放弃处理。
                    //但是如果滑动超过最大/最小边界回弹回来的情况下，在调用_controller.animateTo确又不多次触发，需要知道什么时候是回弹回来的
                    if (notification.metrics.pixels ==
                            notification.metrics.maxScrollExtent ||
                        notification.metrics.pixels ==
                            notification.metrics.minScrollExtent) {
                      _giveUp = false;
                    } else {
                      _giveUp = true;
                    }

                    //延时执行:应该是还在结束的任务回调里面立即调用animateTo执行动画无效果，所以加个小延时
                    Future.delayed(const Duration(milliseconds: 5), () {
                      _controller.animateTo(
                          _calibrate(models.length, notification.metrics.pixels,
                              _maxScrollExtent),
                          duration: const Duration(milliseconds: 250),
                          curve: Curves.ease);
                      if (widget.callback != null) {
                        //每个颜色组件所占用的单位偏移量
                        double unint = _maxScrollExtent / (length - 1);
                        int index =
                            (notification.metrics.pixels / unint).round();
                        widget.callback!(index);
                      }
                    });
                  } else {
                    print("放弃滑动停止代理监听！");
                    _giveUp = false;
                  }
                } else if (notification is OverscrollNotification) {
                  print("overscroll");
                }

                return false;
              },
              child: Stack(children: [
                CustomScrollView(
                  controller: _controller,
                  slivers: <Widget>[
                    // ListView(),
                    SliverPadding(
                      padding: const EdgeInsets.fromLTRB(0, 125, 0, 125),
                      sliver: SliverGrid.count(
                        crossAxisCount: 1,
                        mainAxisSpacing: 10,
                        crossAxisSpacing: 20,
                        children: itemList,
                      ),
                    )
                  ],
                ),
                Align(
                    alignment: Alignment.center,
                    child: IgnorePointer(
                      child: Container(
                        decoration: BoxDecoration(
                            border: Border.all(color: Colors.white, width: 4.0),
                            borderRadius: BorderRadius.circular(25.0)),
                        width: 50,
                        height: 50,
                      ),
                    )),
              ]),
            )));
  }
}

class CustomScrollController extends ScrollController {
  CustomScrollController(this.attachCallback);

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

class _CustomlayoutChildDelegate extends FlowDelegate {
  final double ratio;
  _CustomlayoutChildDelegate(this.ratio);

  @override
  void paintChildren(FlowPaintingContext context) {
    for (var i = 0; i < context.childCount; i++) {
      double centerX = context.getChildSize(i)!.width / 2;
      double centerY = context.getChildSize(i)!.height / 2;
      var scale = ratio;
      context.paintChild(i,
          transform: Matrix4.compose(
              v.Vector3(centerX * (1 - scale), centerY * (1 - scale), 0),
              v.Quaternion(0, 0, 0, 0),
              v.Vector3(scale, scale, 1)));
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
