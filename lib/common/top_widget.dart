import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';

/// 更多功能类型
enum MoreFuctionType {
  none, // 隐藏
  single,  // 只有图片选择
  whole // 全功能
}

// ignore: must_be_immutable
class TopWidget extends StatefulWidget {
  TopWidget({super.key, this.backAction, this.switchCameraAction, this.debugAction, this.moreAction, this.segmentAction, this.type, this.selectedSegmentIndex = 0});

  VoidCallback? backAction;
  VoidCallback? switchCameraAction;
  VoidCallback? debugAction;
  VoidCallback? moreAction;
  Function? segmentAction;

  int? selectedSegmentIndex;

  MoreFuctionType? type = MoreFuctionType.none;

  @override
  State<StatefulWidget> createState() {
    return TopState();
  }
}

class TopState extends State<TopWidget> {
  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Row(
          children: [
            IconButton(
              onPressed: (){
                if (widget.backAction != null) {
                  widget.backAction!();
                }
              }, 
              icon: Image(image: CommonUtil.assetImageNamed("render/render_back"))
            ),
            CupertinoSegmentedControl(
              children: {
                0: Container(
                  alignment: Alignment.center,
                  width: 40,
                  child: Text(
                    defaultTargetPlatform == TargetPlatform.iOS ? 'BGRA' : '单输入',
                    style: TextStyle(color: widget.selectedSegmentIndex == 0 ? Colors.black : Colors.white, fontSize: 11, fontWeight: FontWeight.w500),
                  ),
                ),
                1: Container(
                  alignment: Alignment.center,
                  width: 40,
                  child: Text(
                    defaultTargetPlatform == TargetPlatform.iOS ? 'YUV' : '双输入',
                    style: TextStyle(color: widget.selectedSegmentIndex == 1 ? Colors.black : Colors.white, fontSize: 11, fontWeight: FontWeight.w500),
                  ),
                ),
              },
              groupValue: widget.selectedSegmentIndex,
              unselectedColor: const Color(0x00000000),
              selectedColor: Colors.white,
              borderColor: Colors.white,
              pressedColor: const Color(0x00000000),
              onValueChanged: (value) {
                if (widget.segmentAction != null) {
                  widget.segmentAction!(value);
                }
                setState(() {
                  widget.selectedSegmentIndex = value;
                });
              }
            ),
          ]
        ),
        
        Row(
          children: [
            widget.type != MoreFuctionType.none ? IconButton(
              onPressed: (){
                if (widget.moreAction != null) {
                  widget.moreAction!();
                }
              }, 
              icon: Image(image: CommonUtil.assetImageNamed(widget.type == MoreFuctionType.single ? "render/render_picture" : "render/render_more"))
            ) : const SizedBox(),
            IconButton(
              onPressed: (){
                if (widget.debugAction != null) {
                  widget.debugAction!();
                }
              }, 
              icon: Image(image: CommonUtil.assetImageNamed("render/render_debug"))
            ),
            IconButton(
              onPressed: (){
                if (widget.switchCameraAction != null) {
                  widget.switchCameraAction!();
                }
              }, 
              icon: Image(image: CommonUtil.assetImageNamed("render/render_camera_switch"))
            ),
          ],
        ),
      ],
    );
  }
}