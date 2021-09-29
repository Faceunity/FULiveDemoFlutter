import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';

class FUCircleInIndicator extends StatefulWidget {
  final Function? takePhoto;

  final Function? startRecord;

  final Function? stopRecord;

  FUCircleInIndicator({this.takePhoto, this.startRecord, this.stopRecord});

  @override
  _FUCircleInIndicatorState createState() => _FUCircleInIndicatorState();
}

class _FUCircleInIndicatorState extends State<FUCircleInIndicator> {
  late double _indicatorValue = 0.0;
  int _count = 0;
  Timer? _timer;
  @override
  void initState() {
    super.initState();
  }

  void createTimer() {
    if (_timer == null) {
      _count = 0;
      _timer = Timer.periodic(Duration(milliseconds: 100), (timer) {
        _count++;

        if (_count - 4 >= 0) {
          if (_count - 4 == 0) {
            if (widget.startRecord != null) {
              widget.startRecord!();
            }
          }
          _addValue(0.01);
        }
        //超过10秒结束
        if (_count - 4 > 100) {
          timer.cancel();
          _count = 0;

          //停止录像回调出去
          if (widget.stopRecord != null) {
            widget.stopRecord!();
          }
          //清零
          resetIndicator(0);
        }
      });
    }
  }

  void disposeTimer() {
    if (_timer != null) {
      _timer!.cancel();
      _count = 0;
      _timer = null;
      resetIndicator(0);
    }
  }

  @override
  void dispose() {
    super.dispose();
    disposeTimer();
  }

  //判断拍照还是录像业务
  void _judegeTakePhoto() {
    if (_count <= 3) {
      if (widget.takePhoto != null) {
        widget.takePhoto!();
      }
    } else {
      if (widget.stopRecord != null) {
        widget.stopRecord!();
      }
    }
  }

  void _addValue(double value) {
    setState(() {
      _indicatorValue += value;
      _indicatorValue = min(1.0, _indicatorValue);
    });
  }

  void resetIndicator(double value) {
    setState(() {
      _indicatorValue = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: (TapDownDetails details) => createTimer(),
      onTapUp: (TapUpDetails details) {
        _judegeTakePhoto();
        //清掉定时器
        disposeTimer();
      },
      onLongPressEnd: (LongPressEndDetails details) {
        _judegeTakePhoto();
        disposeTimer();
      },
      child: SizedBox(
        height: 75.0,
        width: 75.0,
        child: CircularProgressIndicator(
            backgroundColor: Colors.white,
            valueColor: AlwaysStoppedAnimation(Colors.blue),
            value: _indicatorValue,
            strokeWidth: 5.0),
      ),
    );
  }
}
