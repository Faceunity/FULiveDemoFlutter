import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'package:fulive_flutter/Makeup/Models/FUMakeupSubModel.dart';
import 'package:fulive_flutter/Makeup/Models/FUMakeupSubTitleModel.dart';
import 'package:provider/provider.dart';
import 'package:fulive_flutter/Makeup/FUMakeupSubManager.dart';

//change 表示切换一个title，
typedef SelectedSubItemCallback = Function(
    List<List<Color>> colors, bool showColors, int index);

//子妆UI
class FUMakeupSubUI extends StatefulWidget {
  //切换组合妆回调
  final Function? switchMakeupCallback;

  final SelectedSubItemCallback? colorSelectedCallback;

  FUMakeupSubUI(Key key, this.switchMakeupCallback, this.colorSelectedCallback)
      : super(key: key);
  @override
  FUMakeupSubUIState createState() => FUMakeupSubUIState();
}

class FUMakeupSubUIState extends State<FUMakeupSubUI> {
  final _screenWidth = window.physicalSize.width / window.devicePixelRatio;
  late final FUMakeupSubManager _manager;

  //颜色组件选中具体颜色值
  void selectedColorIndex(int colorIndex) {
    _manager.didSelectedColorItem(colorIndex);
  }

  //设置是否隐藏子妆上半部分,外部调用
  void setHiddenSubUI(bool isHidden) {
    _manager.hiddenSubMakeup(true);
  }

  @override
  void initState() {
    super.initState();
    _manager = FUMakeupSubManager();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<FUMakeupSubTitleModel>>(
      builder: (BuildContext context, AsyncSnapshot snapshot) {
        if (snapshot.connectionState == ConnectionState.done) {
          if (snapshot.hasError) {
            // 请求失败，显示错误
            return Text("Error: ${snapshot.error}");
          } else {
            // // 请求成功，显示数据
            List<FUMakeupSubTitleModel> dataList = snapshot.data;
            return _customSubMakeup(dataList);
          }
        } else {
          // 请求未结束，显示loading
          // return CircularProgressIndicator();
          return Container();
        }
      },
      future: _manager.getSubMakeupModels(),
    );
  }

  //自定义子妆UI
  Widget _customSubMakeup(List<FUMakeupSubTitleModel> dataList) {
    return ChangeNotifierProvider(
        create: (context) => _manager,
        child: Stack(
          children: [
            Container(
              child: GestureDetector(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    BackdropFilter(
                        filter: ImageFilter.blur(sigmaX: 1.0, sigmaY: 1.0),
                        child: Opacity(
                            opacity: 0.9, child: _makeupSubUI(dataList))),
                    _makeupSubTitleListView(dataList)
                  ],
                ),
              ),
            ),
          ],
        ));
  }

  //子妆标题
  Consumer _makeupSubTitleListView(List<FUMakeupSubTitleModel> dataList) {
    return Consumer<FUMakeupSubManager>(builder: (context, manager, child) {
      return Container(
        height: 54,
        width: _screenWidth,
        color: Colors.black,
        child: ListView.separated(
            padding: const EdgeInsets.fromLTRB(0, 0, 15, 0),
            scrollDirection: Axis.horizontal,
            itemCount: dataList.length,
            separatorBuilder: (BuildContext context, int index) {
              return VerticalDivider(
                width: 10,
                color: Color(0x00000000),
              );
            },
            itemBuilder: (BuildContext context, int index) {
              String title = dataList[index].name;
              //当前选中的子妆索引
              int selectedSubIndex = dataList[index].subIndex!;
              bool selected = false;
              if (manager.selectedSubTitleIndex == index) {
                selected = true;
              }
              return Container(
                  width: 75,
                  child: TextButton(
                    onPressed: () {
                      manager.didSelectedSubTitleItem(index);
                      manager.hiddenSubMakeup(false); //显示
                      //颜色值回调出去
                      if (widget.colorSelectedCallback != null) {
                        widget.colorSelectedCallback!(
                          manager.getCurSubColors(),
                          manager.isShowColorWidget(
                                  dataList[index].subIndex != null
                                      ? dataList[index].subIndex!
                                      : 0) &&
                              manager.getCurSubColors().length != 0,
                          manager.getColorIndex(),
                        );
                      }
                    },
                    child: Stack(
                      children: [
                        Align(
                          alignment: Alignment.center,
                          child: Text(title,
                              style: TextStyle(
                                  color: selected == true
                                      ? Color(0xff5ec7fe)
                                      : Colors.white,
                                  fontSize: 13)),
                        ),
                        Visibility(
                            visible: (selectedSubIndex != 0 &&
                                    manager.getSliderValue(index))
                                ? true
                                : false,
                            child: Align(
                              alignment: Alignment(1.0, -0.7),
                              child: Image(
                                  width: 4,
                                  height: 4,
                                  image: AssetImage(
                                      "resource/images/Makeup/3.0x/makeup_dot.png")),
                            ))
                      ],
                    ),
                  ));
            }),
      );
    });
  }

  //子妆
  Widget _makeupSubUI(List<FUMakeupSubTitleModel> dataList) {
    return Consumer<FUMakeupSubManager>(builder: (context, manager, child) {
      int selectedSubIndex = dataList[manager.selectedSubTitleIndex].subIndex!;
      List<FUMakeupSubModel> subModels =
          dataList[manager.selectedSubTitleIndex].subModels;
      return Visibility(
          visible: !manager.isHiddenSubMakeup,
          child: Container(
            color: Colors.black,
            height: 140.0,
            child: Column(children: [
              _makeupSubSliderView(manager),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Consumer<FUMakeupSubManager>(
                      builder: (context, manager, child) {
                    return Padding(
                      padding: const EdgeInsets.fromLTRB(15, 3, 0, 0),
                      child: GestureDetector(
                        onTap: () {
                          if (widget.switchMakeupCallback != null) {
                            widget.switchMakeupCallback!();
                          }
                        },
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Image(
                              height: 54.0,
                              width: 54.0,
                              image: AssetImage(
                                  "resource/images/Makeup/3.0x/makeup_return_nor.png"),
                            ),
                          ],
                        ),
                      ),
                    );
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
                      ],
                    ),
                  ),
                  Container(
                    height: 60.0,
                    width: _screenWidth - 95,
                    child: _makeupSubListView(
                        subModels, manager, selectedSubIndex),
                  ),
                ],
              ),
            ]),
          ));
    });
  }

  ///selectedSubIndex 当前选中的子妆索引
  Widget _makeupSubListView(List<FUMakeupSubModel> dataList,
      FUMakeupSubManager manager, int selectedSubIndex) {
    return ListView.separated(
      itemCount: dataList.length,
      padding: const EdgeInsets.fromLTRB(0, 0, 15, 0),
      scrollDirection: Axis.horizontal,
      separatorBuilder: (BuildContext context, int index) {
        return VerticalDivider(
          width: 10,
          color: Color(0x00000000),
        );
      },
      itemBuilder: (BuildContext context, int index) {
        FUMakeupSubModel subModel = dataList[index];
        String imagePath = subModel.imagePath;
        bool isFoundation = false;
        List<double> colors;
        int r = 0, g = 0, b = 0, a = 0;

        if (manager.selectedSubTitleIndex == 0 && index != 0) {
          //粉底在数组第0位并且的图片是由颜色构成而不是图片资源，所以特别判定。当前粉底不是index = 0（无粉底）
          isFoundation = true;
          if (subModel.colors != null) {
            colors = subModel.colors![index - 1];
            r = (colors[0] * 255).toInt();
            g = (colors[1] * 255).toInt();
            b = (colors[2] * 255).toInt();
            a = (colors[3] * 255).toInt();
          }
        }

        bool selected = false;
        if (selectedSubIndex == index) {
          selected = true;
        }
        return Container(
          width: 60.0,
          // color: Colors.red,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              GestureDetector(
                onTap: () {
                  if (subModel.title != null) {
                    Fluttertoast.cancel();
                    Fluttertoast.showToast(
                        backgroundColor: Color(0x01000000),
                        msg: subModel.title!,
                        gravity: ToastGravity.CENTER,
                        fontSize: 32.0);
                  }

                  manager.didSelectedSubItem(index,
                      subModel.colorIndex != null ? subModel.colorIndex! : 0);

                  //颜色值回调出去
                  if (widget.colorSelectedCallback != null) {
                    widget.colorSelectedCallback!(
                      manager.getCurSubColors(),
                      manager.isShowColorWidget(index) &&
                          manager.getCurSubColors().length != 0,
                      manager.getColorIndex(),
                    );
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
                    child: isFoundation == true
                        ? Container(
                            height: 54.0,
                            width: 54.0,
                            decoration: BoxDecoration(
                                color: Color.fromARGB(a, r, g, b),
                                borderRadius: BorderRadius.circular(3.0)))
                        : Image(
                            image: AssetImage(imagePath),
                          )),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _makeupSubSliderView(FUMakeupSubManager manager) {
    double value = manager.getCurSubModel() != null
        ? manager.getCurSubModel()!.value
        : 1.0;
    int percent = (value * 100).toInt();
    String valueStr = "$percent";
    return Container(
        height: 45.0,
        width: _screenWidth,
        color: Colors.black,
        child: Visibility(
          visible: manager.subShowSlider,
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
                    manager.subsSliderValueChange(newValue)),
          ),
        ));
  }
}
