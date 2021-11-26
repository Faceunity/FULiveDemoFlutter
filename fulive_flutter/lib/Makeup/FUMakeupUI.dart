import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:fulive_flutter/Makeup/FUMakeupModelManager.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupModel.dart';
import 'package:provider/provider.dart';

//组合妆UI
class FUMakeupUI extends StatefulWidget {
  final int selectedIndex;
  //切换自定义子妆回调
  final Function? switchCustomCallback;

  FUMakeupUI(this.switchCustomCallback, {this.selectedIndex = 1});
  @override
  _FUMakeupUIState createState() => _FUMakeupUIState();
}

class _FUMakeupUIState extends State<FUMakeupUI> {
  final _screenWidth = window.physicalSize.width / window.devicePixelRatio;
  late final FUMakeupModelManager _manager;
  // // 请求成功，显示数据
  late final List<FUMakeupModel> _dataList;

  @override
  void initState() {
    super.initState();
    _manager = FUMakeupModelManager();
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
            _manager.didSelectedItem(widget.selectedIndex);
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
    return BackdropFilter(
        filter: ImageFilter.blur(sigmaX: 1.0, sigmaY: 1.0),
        child: Opacity(
            opacity: 0.9,
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
                ))));
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
                          widget.switchCustomCallback!();
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
                  onTap: () => manager.didSelectedItem(index),
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
      double value = manager.makeupModels[manager.selectedIndex].value;
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
