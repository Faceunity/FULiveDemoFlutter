import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';


// ignore: must_be_immutable
class SliderView extends StatefulWidget {

  // 零点是否在中间
  final bool defaulInMiddle;
  double value;

  final ValueChanged<double>? onChanged;

  final VoidCallback? onChangeEnd;

  SliderView({
    super.key, 
    required this.value, 
    this.defaulInMiddle = false, 
    this.onChanged, 
    this.onChangeEnd
  });

  @override
  State<StatefulWidget> createState() {
    return SliderViewState();
  }
}

class SliderViewState extends State<SliderView> {
  @override
  Widget build(BuildContext context) {
    if (widget.defaulInMiddle) {
      // 零轴在中间
      double sliderWidth = ScreenUtil.getScreenW(context) - 112;
      double currentValue = widget.value - 0.5;
      double width = currentValue * sliderWidth;
      if (width < 0) {
        width = -width;
      }
      double left = currentValue > 0 ? sliderWidth / 2.0 : sliderWidth / 2.0 - width;
      return Stack(
        alignment: Alignment.center,
        children: [
          _CustomSlider(true),
          Positioned.fromRect(
            rect: Rect.fromLTWH(left, 23, width, 4), 
            child: Container(
              color: const Color.fromARGB(255, 94, 199, 254),
            )
          ),
          Container(
            width: 2,
            height: 10,
            clipBehavior: Clip.hardEdge,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(1)
            ),
          )
        ],
      );
    } else {
      return _CustomSlider(false);
    } 
  }

  // ignore: non_constant_identifier_names
  SliderTheme _CustomSlider(bool middle) {
    int intValue = middle ? (widget.value * 100).toInt() - 50 : (widget.value * 100).toInt();
    String labelText = "$intValue";
    return SliderTheme(
      data: SliderThemeData(
        trackHeight: 4,
        trackShape: FullWidthTrackShape(),
        activeTrackColor: middle ? Colors.white : const Color.fromARGB(255, 94, 199, 254),
        inactiveTrackColor: Colors.white,
        overlayColor: Colors.transparent,
        overlayShape: SliderComponentShape.noOverlay,
        thumbColor: const Color.fromARGB(255, 94, 199, 254),
        showValueIndicator: ShowValueIndicator.always,
        valueIndicatorShape: const DropSliderValueIndicatorShape(),
        valueIndicatorColor: const Color.fromARGB(255, 94, 199, 254),
        valueIndicatorTextStyle: const TextStyle(fontSize: 10, fontWeight: FontWeight.w500)
      ), 

      child: Slider(
        value: widget.value, 
        label: labelText,
        onChanged: (value) {
          if (widget.onChanged != null) {
              widget.onChanged!(value);
          }
          setState(() {
            widget.value = value;
          });
        },
        onChangeEnd: (value) {
          if (widget.onChangeEnd != null) {
              widget.onChangeEnd!();
          }
        },
      )
    );
  }
}

class FullWidthTrackShape extends RoundedRectSliderTrackShape {
  @override

  // 去掉默认边距
  Rect getPreferredRect({
    required RenderBox parentBox,
    Offset offset = Offset.zero,
    required SliderThemeData sliderTheme,
    bool isEnabled = false,
    bool isDiscrete = false,
  }) {
    final double? trackHeight = sliderTheme.trackHeight;
    final double trackLeft = offset.dx;
    final double trackTop =
        offset.dy + (parentBox.size.height - trackHeight!) / 2;
    final double trackWidth = parentBox.size.width;
    return Rect.fromLTWH(trackLeft, trackTop, trackWidth, trackHeight);
  }

  @override
  void paint(
    PaintingContext context, 
    Offset offset, 
    {
      required RenderBox parentBox, 
      required SliderThemeData sliderTheme, 
      required Animation<double> enableAnimation, 
      required TextDirection textDirection, required 
      Offset thumbCenter, Offset? secondaryOffset, 
      bool isDiscrete = false, 
      bool isEnabled = false, 
      double additionalActiveTrackHeight = 0
    }) {

    super.paint(
      context, 
      offset, 
      parentBox: parentBox, 
      sliderTheme: sliderTheme, 
      enableAnimation: enableAnimation, 
      textDirection: textDirection, 
      thumbCenter: thumbCenter, 
      secondaryOffset: secondaryOffset, 
      isDiscrete: isDiscrete, 
      isEnabled: isEnabled, 
      additionalActiveTrackHeight: additionalActiveTrackHeight
    );
  }
}