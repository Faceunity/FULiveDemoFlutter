import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

typedef OnChange = void Function(int index);

// 继承 StatefulWidget
class SegmentBar extends StatefulWidget {

  // 初始化方法
  const SegmentBar({Key? key, required this.onChange, required this.items}) : super(key: key);

  // 点击回调
  final OnChange onChange;
  // 列表数据
  final List<String> items;

  // StatefulWidget 必须实现 createState
  @override
  State<StatefulWidget> createState() {
    return SegmentBarState();
  }
}

class SegmentBarState extends State<SegmentBar> {

  // 当前选中索引
  late int selectedIndex;

  // 初始化状态
  @override
  void initState() {
    super.initState();
    selectedIndex = -1;
  }

  // 构建界面
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemBuilder: (context, index) {
        return _itemCell(index);
      },
      itemCount: widget.items.length,
      scrollDirection: Axis.horizontal
    );
  }

  Widget _itemCell(int index) {
    double screenWidth = ScreenUtil.getInstance().screenWidth;
    double itemWidth = widget.items.length < 7 ? screenWidth / widget.items.length : ScreenUtil.getInstance().screenWidth / 7; 
    return SizedBox(
      width: itemWidth,
      height: double.infinity,
      child: TextButton(
        style: const ButtonStyle(
          backgroundColor: MaterialStatePropertyAll(Colors.transparent),
          overlayColor: MaterialStatePropertyAll(Colors.transparent)
        ),
        onPressed: () {
          // setState 方法刷新控件
          setState(() {
            selectedIndex = selectedIndex == index ? -1 : index;
            widget.onChange(selectedIndex);
          });
        }, 
        child: Center(
          child: Text(
            widget.items[index],
            style: TextStyle(
              color: index == selectedIndex ? const Color.fromARGB(255, 94, 199, 254) : Colors.white,
              fontSize: 13,
            ),
          ),
        )
      ),
    );
  }
}