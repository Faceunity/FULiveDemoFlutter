import 'dart:async';
import 'dart:io';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:fulive_flutter/Beauty/FUBeautyDefine.dart';
import 'package:fulive_flutter/Tools/FUImagePixelRatio.dart';
import 'package:provider/provider.dart';

import 'FUBeautyDataManager.dart';
import 'FUBeautyModel.dart';
import 'FUBeautySubModel.dart';

// ignore: must_be_immutable
class BeautyToolsWidget extends StatefulWidget {
  BeautyToolsWidget(
      {required this.bizType,
      required this.dataList,
      this.showFilterTips,
      this.compareCallback,
      this.clickItemCallback,
      this.prohibitCompare});

  //是否展示滤镜提示语
  late bool? showFilterTips = true;
  late FUBeautyDefine bizType;
  late List<FUBeautyModel> dataList;

  //点击底部item 美肤、美型这些标题回调外面，
  final Function? clickItemCallback;

  //订阅外部是否禁止对比按钮事件
  final StreamController? prohibitCompare;

  //对比按钮回调，上层处理
  final Function? compareCallback;

  @override
  _BeautyToolsWidgetState createState() => _BeautyToolsWidgetState();
}

class _BeautyToolsWidgetState extends State<BeautyToolsWidget> {
  late List<FUBeautyModel> _dataList;

  bool _hiddenDialog = true;

  //订阅是否禁止录屏按钮工作
  // ignore: cancel_subscriptions, unused_field
  late StreamSubscription? _prohibitCompareSubscription;

  bool _prohibitCompareEnable = false;

  //对比按钮禁止工作
  void _prohibitCompare(bool state) {
    setState(() {
      _prohibitCompareEnable = state;
    });
  }

  //是否隐藏dialog
  void hiddenDialog(bool hidden) {
    setState(() {
      _hiddenDialog = hidden;
    });
  }

  @override
  void initState() {
    super.initState();
    _dataList = widget.dataList;

    if (widget.prohibitCompare != null) {
      _prohibitCompareSubscription =
          widget.prohibitCompare!.stream.listen((event) {
        print("禁止点击对比按钮:$event");
        _prohibitCompare(event);
      });
    } else {
      _prohibitCompareSubscription = null;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<FUBeautifyDataManager>(builder: (context, manager, child) {
      List<Widget> widgets = [];
      FUBeautyDefine bizType = manager.curBizType;
      //对比按钮
      if (bizType != FUBeautyDefine.FUBeautyMax) {
        widgets.add(Padding(
          padding: EdgeInsets.fromLTRB(15, 0, 0, 15),
          child: Align(
              alignment: Alignment.topLeft,
              child: AbsorbPointer(
                absorbing: _prohibitCompareEnable,
                child: GestureDetector(
                  onTapDown: (TapDownDetails details) {
                    if (widget.compareCallback != null) {
                      widget.compareCallback!(true);
                    }
                  },
                  onTapUp: (TapUpDetails details) {
                    if (widget.compareCallback != null) {
                      widget.compareCallback!(false);
                    }
                  },
                  onLongPress: () {
                    if (widget.compareCallback != null) {
                      widget.compareCallback!(true);
                    }
                  },
                  onLongPressEnd: (details) {
                    if (widget.compareCallback != null) {
                      widget.compareCallback!(false);
                    }
                  },
                  child: Image(
                    image:
                        AssetImage("resource/images/commonImage/compare.png"),
                  ),
                ),
              )),
        ));
      }
      if (bizType == FUBeautyDefine.FUBeautyDefineSkin ||
          bizType == FUBeautyDefine.FUBeautyDefineShape) {
        widgets.add(SizedBox(
            height: 150,
            width: double.infinity,
            child: Container(
              child: _circleCell(bizType),
              color: Color(0xBC050F14),
            )));
      } else if (bizType == FUBeautyDefine.FUBeautyDefineFilter) {
        widgets.add(SizedBox(
            height: 150,
            width: double.infinity,
            child: Container(
              child: _rectangleCell(bizType),
              color: Color(0xBC050F14),
            )));
      } else if (bizType == FUBeautyDefine.FUBeautyDefineStyle) {
        widgets.add(SizedBox(
            height: 150,
            width: double.infinity,
            child: Container(
              child: _styleCell(bizType),
              color: Color(0xBC050F14),
            )));
      }

      widgets.add(SizedBox(
          height: 45,
          width: double.infinity,
          child: Container(
            child: _beautyUI(),
            color: Color(0xBC050F14),
          )));
      return Container(
          child: Stack(
        children: [
          Column(
            mainAxisAlignment: MainAxisAlignment.end,
            children: widgets,
          ),
          Visibility(
            visible: !_hiddenDialog,
            child: ResetDialog(() {
              manager.reset();
              hiddenDialog(true);
            }, () {
              hiddenDialog(true);
            }),
          )
        ],
      ));
    });
  }

  //图片为矩形的cell， 滤镜cell
  Column _rectangleCell(FUBeautyDefine bizType) {
    return Column(children: [
      _customSlider(),
      SizedBox(
          height: 85,
          child: Consumer<FUBeautifyDataManager>(
              builder: (context, manager, child) {
            return _commonCell(bizType, manager.dataList[bizType.index]);
          }))
    ]);
  }

  //风格化cell
  Column _styleCell(FUBeautyDefine bizType) {
    return Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      SizedBox(
        height: 85,
        child:
            Consumer<FUBeautifyDataManager>(builder: (context, manager, child) {
          return _commonCell(bizType, manager.dataList[bizType.index]);
        }),
      )
    ]);
  }

  //图片为圆型的cell, 美肤、美型cell复用
  Column _circleCell(FUBeautyDefine bizType) {
    String resetImagepath = FUImagePixelRatio.getImagePathWithRelativePathPre(
        "resource/images/beauty/shape");
    resetImagepath = resetImagepath + "恢复.png";
    String title = "恢复";
    return Column(
      children: [
        //Slider,
        _customSlider(),
        Container(
            height: 85,
            child: Row(
              children: [
                Container(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Consumer<FUBeautifyDataManager>(
                          builder: (context, manager, child) {
                        return Opacity(
                          opacity: manager.isDefaultValue(bizType) == true
                              ? 0.7
                              : 1.0,
                          child: TextButton(
                            onPressed: () {
                              //不是默认值提示dialog
                              bool show = manager.isDefaultValue(bizType);
                              hiddenDialog(show);
                            },
                            child: Padding(
                              padding: const EdgeInsets.fromLTRB(0, 4, 0, 0),
                              child: Image(
                                width: 50.0,
                                height: 44.0,
                                image: AssetImage(resetImagepath),
                              ),
                            ),
                          ),
                        );
                      }),
                      Text(title,
                          style: TextStyle(color: Colors.white, fontSize: 10)),
                    ],
                  ),
                ),

                //     添加reset 和 cell之间分割线
                Expanded(
                  flex: 1,
                  child: Align(
                    alignment: Alignment(1.0, -0.2),
                    child: Container(
                      height: 25,
                      width: 1,
                      color: Colors.white24,
                    ),
                  ),
                ),

                Expanded(

                    //去掉第一个reset按钮，复用其余子元素
                    flex: 45,
                    child: Container(
                        // color: Colors.green,
                        child: Consumer<FUBeautifyDataManager>(
                            builder: (context, manager, child) {
                      return _commonCell(
                          bizType, manager.dataList[bizType.index]);
                    }))),
              ],
            ))
      ],
    );
  }

  //复用的cell,
  ListView _commonCell(FUBeautyDefine bizType, FUBeautyModel model) {
    List<FUBeautySubModelUI> uiList = model.uiList;
    List<FUBeautySubModel> bizList = model.bizList;
    return ListView.separated(
        scrollDirection: Axis.horizontal,
        itemCount: uiList.length,
        separatorBuilder: (BuildContext context, int index) {
          return VerticalDivider(
            width: 5,
            color: Color(0x00000000),
          );
        },
        itemBuilder: (BuildContext context, int index) {
          FUBeautySubModelUI uiModel = uiList[index];
          //图片路径选择
          String imagePath = '';
          //滤镜是否显示边框
          bool showBoard = false;
          if (bizType == FUBeautyDefine.FUBeautyDefineFilter) {
            showBoard = model.selected == index ? true : false;
            imagePath = uiModel.imagePath + '.png';
          } else {
            //风格 图片只有选中和未选中区别
            if (bizType == FUBeautyDefine.FUBeautyDefineStyle) {
              imagePath = model.selected == index
                  ? uiModel.imagePath + '-1.png'
                  : uiModel.imagePath + '-0.png';
            } else {
              FUBeautySubModel bizModel = bizList[index];
              bool opened = false;
              if (bizModel.midSlider) {
                opened = (bizModel.value - 0.5).abs() > 0.01 ? true : false;
              } else {
                opened = (bizModel.value.abs() - 0) > 0.01 ? true : false;
              }
              if (model.selected == index) {
                imagePath = opened == true
                    ? uiModel.imagePath + '-3.png'
                    : uiModel.imagePath + '-2.png';
              } else {
                imagePath = opened == true
                    ? uiModel.imagePath + '-1.png'
                    : uiModel.imagePath + '-0.png';
              }
            }
          }

          return Consumer<FUBeautifyDataManager>(
            builder: (context, manager, child) {
              return uiList.length > index
                  ? Container(
                      // color: Colors.red,
                      child: Column(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        TextButton(
                          child: Container(
                            decoration: BoxDecoration(
                                // color: Colors.green,
                                borderRadius: BorderRadius.circular(5.0),
                                border: Border.all(
                                    color: showBoard == true
                                        ? Color(0xFF5EC7FE)
                                        : Color(0x005EC7FE),
                                    width: 2.0)),
                            child: Image(
                              image: AssetImage(imagePath),
                              // fit: BoxFit.cover,
                              width:
                                  bizType == FUBeautyDefine.FUBeautyDefineFilter
                                      ? 50
                                      : 44,
                              height:
                                  bizType == FUBeautyDefine.FUBeautyDefineFilter
                                      ? 50
                                      : 44,
                            ),
                          ),
                          onPressed: () {
                            if (widget.showFilterTips == true &&
                                bizType ==
                                    FUBeautyDefine.FUBeautyDefineFilter) {
                              Fluttertoast.cancel();
                              Fluttertoast.showToast(
                                  backgroundColor: Color(0x01000000),
                                  msg: uiModel.title,
                                  gravity: ToastGravity.CENTER,
                                  fontSize: 32.0);
                            }
                            manager.updateModelIndex(bizType, index);
                            // });
                          },
                        ),
                        Text(uiModel.title,
                            style: model.selected == index
                                ? TextStyle(
                                    color: Color(0xFF5EC7FE), fontSize: 10)
                                : TextStyle(color: Colors.white, fontSize: 10)),
                      ],
                    ))
                  : Text("无数据");
            },
          );
        });
  }

  //Slider 控件
  Container _customSlider() {
    return Container(
        height: 50,
        child:
            Consumer<FUBeautifyDataManager>(builder: (context, manager, child) {
          //滤镜原图的时候隐藏滑动条
          bool show = manager.showSlider();
          //实际滑块值
          double value = manager.curBizModel.value / manager.curBizModel.ratio;
          int percent;
          String valueStr; //百分比字符串
          //是否以中间为起始点
          bool middle = manager.curBizModel.midSlider;
          //滑块滑过的轨迹颜色
          Color activeTrackColor;
          //滑块未滑过的轨迹颜色
          Color inactiveTrackColor = Colors.white;
          // //自定义中间滑块划过的痕迹长度
          // double midleContainerWidth = 0.0;

          if (middle) {
            activeTrackColor = Colors.white;
            percent = ((value - 0.5) * 100).toInt();
            valueStr = "$percent";
            if ((value - 0.5) > 0) {
              // midleContainerWidth = (value - 0.5) * 100;
            } else {
              // midleContainerWidth = (0.5 - value) * 100;
            }
          } else {
            percent = (value * 100).toInt();
            valueStr = "$percent";
            activeTrackColor = Color(0xFF5EC7FE);
          }
          return show == true
              ? Stack(
                  alignment: Alignment.center,
                  children: [
                    Positioned(
                        child: SliderTheme(
                      data: SliderThemeData(
                        trackHeight: 2,
                        activeTrackColor: activeTrackColor,
                        inactiveTrackColor: inactiveTrackColor,
                        thumbShape: RoundSliderThumbShape(
                            //  滑块形状，可以自定义
                            enabledThumbRadius: 8 // 滑块大小
                            ),
                      ),
                      child: Slider(
                          label: valueStr,
                          divisions: 100,
                          value: value,
                          onChanged: (double newValue) {
                            manager.updateSliderValue(newValue);
                          }),
                    )),
                    Container(
                        width: middle ? 2 : 0,
                        height: 10,
                        color: Color(0xFF5EC7FE)),
                  ],
                )
              : Container();
        }));
  }

  //美颜底部标题
  GridView _beautyUI() {
    return GridView.builder(
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 1, childAspectRatio: 0.5, mainAxisSpacing: 5.0),
        //主轴为横向的GridView
        scrollDirection: Axis.horizontal,
        itemCount: _dataList.length,
        itemBuilder: (context, index) {
          return _dataList.length > index
              ? _beautyCell(_dataList[index])
              : Text("无数据");
        });
  }

  Container _beautyCell(FUBeautyModel model) {
    return Container(
        // color: Colors.red,
        child:
            Consumer<FUBeautifyDataManager>(builder: (context, manager, child) {
      //由下面的业务决定颜色
      Color titleColor;
      //是否可以切换
      bool canSwitchItem =
          model.bizType != FUBeautyDefine.FUBeautyDefineStyle &&
              manager.checkStyle();
      if (canSwitchItem) {
        titleColor = Colors.grey;
      } else {
        titleColor = model.bizType == manager.curBizType
            ? Color(0xFF5EC7FE)
            : Colors.white;
      }
      return TextButton(
          child: Text(
            model.title == null ? '数据有问题' : model.title!,
            style: TextStyle(color: titleColor),
          ),
          onPressed: () {
            if (canSwitchItem) {
              String message;
              if (model.bizType == FUBeautyDefine.FUBeautyDefineSkin) {
                message = "使用美肤先取消\'风格推荐\'";
              } else if (model.bizType == FUBeautyDefine.FUBeautyDefineShape) {
                message = "使用美型先取消\'风格推荐\'";
              } else {
                message = "使用滤镜先取消\'风格推荐\'";
              }

              Fluttertoast.showToast(
                msg: message,
                gravity: ToastGravity.CENTER,
              );
            } else {
              if (model.bizType == manager.curBizType) {
                manager.changeBizType(FUBeautyDefine.FUBeautyMax);
              } else {
                manager.changeBizType(model.bizType);
                manager.updateModelIndex(model.bizType, model.selected);
              }
              if (widget.clickItemCallback != null) {
                widget.clickItemCallback!(
                    manager.curBizType == FUBeautyDefine.FUBeautyMax
                        ? false
                        : true);
              }
            }
          });
    }));
  }
}

class ResetDialog extends StatefulWidget {
  final Function? comfirmCallback;
  final Function? cancelCallback;
  ResetDialog(this.comfirmCallback, this.cancelCallback);
  _ResetDialogState createState() => _ResetDialogState();
}

class _ResetDialogState extends State<ResetDialog> {
  // void
  @override
  Widget build(BuildContext context) {
    return Platform.isIOS == true
        ? CupertinoAlertDialog(
            content: new SingleChildScrollView(
              child: ListBody(
                children: <Widget>[
                  Text(
                    "是否将所有参数恢复到默认值",
                    style: TextStyle(
                        color: Colors.black,
                        fontSize: 15,
                        fontWeight: FontWeight.w700),
                  ),
                ],
              ),
            ),
            actions: <Widget>[
              CupertinoDialogAction(
                child: Text("取消"),
                onPressed: () => widget.cancelCallback!(),
                textStyle: TextStyle(
                    color: Colors.black,
                    fontSize: 15,
                    fontWeight: FontWeight.w400),
              ),
              CupertinoDialogAction(
                child: Text("确定"),
                onPressed: () => widget.comfirmCallback!(),
                textStyle: TextStyle(
                    color: Colors.blue,
                    fontSize: 15,
                    fontWeight: FontWeight.w400),
              ),
            ],
          )
        : AlertDialog(
            content: new SingleChildScrollView(
              child: ListBody(
                children: <Widget>[
                  Text("是否将所有参数恢复到默认值"),
                ],
              ),
            ),
            actions: <Widget>[
              TextButton(
                child: Text("确定"),
                onPressed: () => widget.comfirmCallback!(),
              ),
              TextButton(
                child: Text("取消"),
                onPressed: () => widget.cancelCallback!(),
              )
            ],
          );
  }
}
