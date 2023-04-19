import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:fulive_flutter/Makeup/FUMakeupConst.dart';
import 'package:fulive_flutter/Makeup/FUMakeupModelManager.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupModel.dart';
import 'package:fulive_flutter/Tools/FUCustomController.dart';
import 'package:provider/provider.dart';

//切换自定义子妆回调,表示当前可支持自定义子妆的组合妆回调索引
typedef SwitchCustomCallback = Function(int canCustomIndex, double offset);

//组合妆UI
class FUMakeupUI extends StatefulWidget {
  FUMakeupUI(
    this.switchCustomCallback, {
    this.unMakeupCallback,
    this.selectedIndex = MAKEUP_UNLOADINDEX,
    this.offset = 1.0,
  });

  //切换自定义子妆回调
  final SwitchCustomCallback? switchCustomCallback;

  //卸妆按钮回调
  final Function? unMakeupCallback;

  //初始化组合妆索引
  final int selectedIndex;

  //滑动偏移量
  final double offset;
  @override
  _FUMakeupUIState createState() => _FUMakeupUIState();
}

class _FUMakeupUIState extends State<FUMakeupUI> {
  final _screenWidth = window.physicalSize.width / window.devicePixelRatio;
  late final FUMakeupModelManager _manager;
  // // 请求成功，显示数据
  late final List<FUMakeupModel> _dataList;

  late final FUCustomController _controller;

  late double _lastOffset;
  @override
  void initState() {
    super.initState();
    _lastOffset = widget.offset;
    //好好考虑一下初始值
    _manager = FUMakeupModelManager(selectedIndex: widget.selectedIndex);
    _controller = FUCustomController((ScrollPosition position) {
      //
      position.animateTo(_lastOffset,
          duration: const Duration(milliseconds: 200), curve: Curves.ease);
    });

    _controller.addListener(() {
      _lastOffset = _controller.offset;
      if (_lastOffset <= 1.0) {
        //由于 传入0.0 给position.animateTo这个方法会报空值错误，所以给1.0, 应该是flutter 下面处理好
        _lastOffset = 1.0;
      }
    });
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    //组合妆UI
    return FutureBuilder<List<FUMakeupModel>>(
      builder: (BuildContext context, AsyncSnapshot snapshot) {
        if (snapshot.connectionState == ConnectionState.done) {
          if (snapshot.hasError) {
            // 请求失败，显示错误
            return Text("Error: ${snapshot.error}");
          } else {
            //默认选中第一个组合装(减龄)
            _manager.selectedDefault();
            _dataList = snapshot.data;
            return _setUpUI(_dataList);
          }
        } else {
          // 请求未结束，显示loading
          // return CircularProgressIndicator();
          return Container();
        }
      },
      future: _manager.getMakeupModels(),
    );
  }

  Widget _setUpUI(List<FUMakeupModel> dataList) {
    return Container(
      child: BackdropFilter(
        filter: ImageFilter.blur(sigmaX: 1.0, sigmaY: 1.0),
        child: Container(
            child: Opacity(
                opacity: 0.8,
                child: ChangeNotifierProvider(
                    create: (context) => _manager,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        _makeupSliderView(),
                        _makeupUI(dataList),
                        Container(
                          height: 5,
                          color: Colors.black,
                        )
                      ],
                    )))),
      ),
    );
  }

  //组合装列表UI
  Widget _makeupUI(List<FUMakeupModel> dataList) {
    return Container(
      color: Colors.black,
      height: 90.0,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Consumer<FUMakeupModelManager>(builder: (context, manager, child) {
            return Padding(
                padding: const EdgeInsets.fromLTRB(15, 3, 0, 0),
                child: Opacity(
                  opacity: manager.changeCustomPicAlpha() ? 1.0 : 0.7,
                  child: GestureDetector(
                    onTap: () {
                      //当前组合装状态不是卸妆不允许点击自定义子妆
                      if (manager.selectedIndex == 0 ||
                          manager.canCustomSubMakeup()) {
                        if (widget.switchCustomCallback != null) {
                          widget.switchCustomCallback!(
                              manager.selectedIndex, _lastOffset);
                          manager.makeupChange();
                        }
                      }
                    },
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        Image(
                          height: 54.0,
                          width: 54.0,
                          image: AssetImage(
                              "resource/images/Makeup/3.0x/makeup_custom_nor.png"),
                        ),
                        Text("自定义",
                            style:
                                TextStyle(color: Colors.white, fontSize: 10)),
                      ],
                    ),
                  ),
                ));
          }),
          Container(
            width: 21,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Align(
                  alignment: Alignment.center,
                  child: Container(
                    height: 54.0,
                    width: 1,
                    color: Colors.white24,
                  ),
                ),
                Text("占位",
                    style: TextStyle(color: Colors.transparent, fontSize: 10)),
              ],
            ),
          ),
          Container(
            width: _screenWidth - 95,
            child: _makeupListView(dataList),
          ),
        ],
      ),
    );
  }

  //列表
  Consumer _makeupListView(List<FUMakeupModel> dataList) {
    return Consumer<FUMakeupModelManager>(builder: (context, manager, child) {
      return ListView.separated(
        padding: const EdgeInsets.fromLTRB(0, 0, 15, 0),
        scrollDirection: Axis.horizontal,
        controller: _controller,
        separatorBuilder: (BuildContext context, int index) {
          return VerticalDivider(
            width: 10,
            color: Color(0x00000000),
          );
        },
        itemBuilder: (BuildContext context, int index) {
          String imagePath = dataList[index].imagePath;
          String title = dataList[index].title;
          bool selected = false;
          if (manager.selectedIndex == index) {
            selected = true;
          }
          return Container(
            // color: Colors.red,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                GestureDetector(
                  onTap: () {
                    manager.didSelectedItem(index);
                    //
                    if (widget.unMakeupCallback != null &&
                        manager.canCustomSubMakeup() == false) {
                      widget.unMakeupCallback!();
                    }
                  },
                  child: Container(
                      decoration: BoxDecoration(
                          border: Border.all(
                              color: selected == true
                                  ? Color(0xFF5EC7FE)
                                  : Colors.transparent,
                              width: 3.0),
                          borderRadius: BorderRadius.circular(5.0)),
                      child: Image(
                        height: 54.0,
                        width: 54.0,
                        image: AssetImage(imagePath),
                      )),
                ),
                Text(title,
                    style: TextStyle(color: Colors.white, fontSize: 10)),
              ],
            ),
          );
        },
        itemCount: dataList.length,
      );
    });
  }

  Widget _makeupSliderView() {
    return Consumer<FUMakeupModelManager>(builder: (context, manager, child) {
      double value = manager.getSliderValue();
      int percent = (value * 100).toInt();
      String valueStr = "$percent";
      return Container(
          color: Colors.black,
          height: 45.0,
          width: _screenWidth,
          child: Visibility(
            visible: manager.showSlider,
            child: SliderTheme(
              data: SliderThemeData(
                trackHeight: 5,
                activeTrackColor: Color(0xFF5EC7FE),
                inactiveTrackColor: Colors.white,
                thumbShape: RoundSliderThumbShape(
                    //  滑块形状，可以自定义
                    enabledThumbRadius: 10 // 滑块大小
                    ),
              ),
              child: Slider(
                  label: valueStr,
                  divisions: 100,
                  value: value,
                  onChanged: (double newValue) =>
                      manager.sliderValueChange(newValue)),
            ),
          ));
    });
  }
}
