import 'package:flutter/widgets.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';

class CompareButton extends StatefulWidget {

  final void Function(bool tappedDown)? tapStateChanged;

  const CompareButton({super.key, this.tapStateChanged});

  @override
  State<StatefulWidget> createState() {
    return CompareButtonState();
  }
}

class CompareButtonState extends State<CompareButton> {
  bool isTappedDown = false;
  @override
  Widget build(BuildContext context) {
    return Opacity(
      opacity: isTappedDown ? 0.5 : 1.0,
      child: GestureDetector(
        child: Image(image: CommonUtil.assetImageNamed("beauty/compare"),),
        onTapDown: (details) {
          if (widget.tapStateChanged != null) {
            widget.tapStateChanged!(true);
          }
          setState(() {
            isTappedDown = true;
          });
        },
        onTapUp: (details) {
          if (widget.tapStateChanged != null) {
            widget.tapStateChanged!(false);
          }
          setState(() {
            isTappedDown = false;
          });
        },
        onTapCancel: () {
          if (widget.tapStateChanged != null) {
            widget.tapStateChanged!(false);
          }
          setState(() {
            isTappedDown = false;
          });
        },
      ),
    );
  }

}