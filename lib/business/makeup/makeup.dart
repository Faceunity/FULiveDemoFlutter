import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/business/beauty/beauty_view_model.dart';
import 'package:fulivedemo_flutter/business/makeup/combination/combination_makeup_view.dart';
import 'package:fulivedemo_flutter/business/makeup/custom/customized_makeup_view.dart';
import 'package:fulivedemo_flutter/business/makeup/makeup_view_model.dart';
import 'package:fulivedemo_flutter/business/render/render_view_model.dart';
import 'package:fulivedemo_flutter/business/render/render_view.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class Makeup extends StatefulWidget {
  const Makeup({super.key});

  @override
  State<StatefulWidget> createState() {
    return MakeupState();
  }
}

class MakeupState extends State<Makeup> {
  late BeautyViewModel beautyViewModel;
  late MakeupViewModel makeupViewModel;
  RenderViewModel renderViewModel = RenderViewModel(Module.makeup);

  late ScrollController scrollController;
  
  // 滑动时色卡列表选中索引（其他色卡需要按比例缩小）
  late int scrollIndex;

  @override
  void initState() {
    renderViewModel.captureButtonBottom = 144 + ScreenUtil.getInstance().bottomBarHeight;
    // 美妆模块需要美颜支持
    beautyViewModel = BeautyViewModel();
    beautyViewModel.initialize();
    // 初始化美妆 ViewModel
    makeupViewModel = MakeupViewModel();
    makeupViewModel.initialize();
    super.initState();

    scrollIndex = makeupViewModel.customizedMakeupViewModel.selectedColorIndex;

    // 初始化色卡 ScrollController
    scrollController = ScrollController(
      initialScrollOffset: makeupViewModel.customizedMakeupViewModel.selectedColorIndex * 40 + 20
    );

    scrollController.addListener(() {
      // 监听色卡列表滚动
      double offset = scrollController.offset;
      List<List<dynamic>> colors = makeupViewModel.customizedMakeupViewModel.currentColors ?? [];
      int maxIndex = colors.length - 1;
      double tempIndex = (offset < 0 ? 0 : (offset > maxIndex * 40.0 ? maxIndex * 40.0 : offset)) / 40;
      int index = tempIndex.toInt();
      if (index != scrollIndex) {
        scrollIndex = index;
        setState(() {
        });
      }
    });
  }

  @override
  void dispose() {
    beautyViewModel.dispose();
    makeupViewModel.dispose();
    scrollController.dispose();
    super.dispose();
  }
  @override
  Widget build(BuildContext context) {
    // 底部安全区域高度
    final bottom = MediaQuery.of(context).padding.bottom;
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 17, 18, 38),
      body: WillPopScope(
        onWillPop: () async {
          return false;
        },
        child: Stack(
          children:[
            // 基础渲染视图
            RenderView(
              viewModel: renderViewModel,
              backAction: (){
              },
            ),

            // 组合妆
            Positioned(
              left: 0,
              right: 0,
              bottom: 0,
              child: ValueListenableBuilder(
                valueListenable: makeupViewModel.isCustomizing, 
                builder: (context, value, child) {
                  return AnimatedContainer(
                    height: value ? 0 : 144 + bottom,
                    duration: const Duration(milliseconds: 100),
                    child: CombinationMakeupView(
                      viewModel: makeupViewModel.combinationMakeupViewModel, 
                      clickCustomizeCallBack: () {
                        makeupViewModel.startCustomizing();
                        setState(() {
                          renderViewModel.captureButtonBottom = 170 + bottom;
                        });
                      },
                    ),
                  );
                },
              )
            ),

            // 自定义子妆
            Positioned(
              left: 0,
              right: 0,
              bottom: 0,
              child: ValueListenableBuilder(
                valueListenable: makeupViewModel.isCustomizing, 
                builder: (context, value, child) {
                  return AnimatedContainer(
                    height: value ? 170 + bottom : 0,
                    duration: const Duration(milliseconds: 100),
                    child: CustomizedMakeupView(
                      viewModel: makeupViewModel.customizedMakeupViewModel, 
                      clickBackCallBack: () {
                        makeupViewModel.stopCustomizing();
                        setState(() {
                          renderViewModel.captureButtonBottom = 144 + bottom;
                        });
                      },
                      onChanged: () {
                        setState(() {
                          if (makeupViewModel.customizedMakeupViewModel.needsColorPicker) {
                            scrollIndex = makeupViewModel.customizedMakeupViewModel.selectedColorIndex;
                            scrollToItem(makeupViewModel.customizedMakeupViewModel.selectedColorIndex);
                          }
                        });
                      },
                    ),
                  );
                },
              )
            ),

            Positioned(
              right: 15,
              bottom: bottom + 192,
              child: Visibility(
                visible: makeupViewModel.customizedMakeupViewModel.needsColorPicker && makeupViewModel.isCustomizing.value,
                child: SizedBox(
                  height: 250,
                  width: 60,
                  child: _colorPicker()
                )
              ),
            )
          ]
        ),
      )
    );
  }

  /// 自定义妆容色卡
  Widget _colorPicker() {
    return Stack(
      alignment: Alignment.center,
      children: [
        NotificationListener<ScrollEndNotification>(
          onNotification: (notification) {
            // 滑动信息封装
            ScrollMetrics metrics = notification.metrics;
            // 获取当前的滑动位置
            double pixels = metrics.pixels;
            // 计算滑动位置
            double scrollIndex = (pixels) / 41;
            double scrollOffset = (pixels - 20) % 40;
            // 当前选中
            int currentIndex = scrollIndex.toInt();
            if (scrollOffset != 0.0) {
              // 需要校准位置
              scrollToItem(currentIndex);
              if (makeupViewModel.customizedMakeupViewModel.selectedColorIndex != currentIndex) {
                // 切换色卡
                makeupViewModel.customizedMakeupViewModel.setSelectedColorIndex(currentIndex);
              }
            }
            return true;
          },
          child: SingleChildScrollView(
            controller: scrollController,
            padding: const EdgeInsets.fromLTRB(10, 125, 10, 125),
            scrollDirection: Axis.vertical,
            child: Column(
              children: _listView(),
            ),
          ),
        
        ),
        
        Center(
          child: IgnorePointer(
            child: Container(
              width: 44,
              height: 44,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(22),
                border: Border.all(
                  color: Colors.white,
                  width: 4
                )
              ),
            ),
          )
        )
      ],
    );
  }

  List<Widget> _listView() {
    List<List<dynamic>> colors = makeupViewModel.customizedMakeupViewModel.currentColors ?? [];
    List<Widget> list = [];
    for (int i = 0; i < colors.length; i ++) {
      list.add(_colorItemCell(i));
    }
    return list;
  }

  Widget _colorItemCell(int index) {
    List<List<dynamic>> colors = makeupViewModel.customizedMakeupViewModel.currentColors ?? [];
    List<dynamic> color = colors[index];
    
    // 根据索引计算显示大小
    double cellWidth = 40;
    if (index < scrollIndex) {
      cellWidth = (1 - 0.3 * (scrollIndex - index)) * 40;
    } else if (index > scrollIndex) {
      cellWidth = (1 - 0.3 * (index - scrollIndex)) * 40;
    }
    if (cellWidth < 10) {
      cellWidth = 10;
    }

    return GestureDetector(
      onTap: () {
        if (makeupViewModel.customizedMakeupViewModel.selectedColorIndex != index) {
          scrollToItem(index);
          // 切换色卡
          makeupViewModel.customizedMakeupViewModel.setSelectedColorIndex(index);
        }
      },
      child: SizedBox(
        width: 40,
        height: 40,
        child: Center(
          child: AnimatedContainer(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: _colorsList(color),
              ),
              borderRadius: BorderRadius.circular(20.0)
            ),
            width: cellWidth,
            height: cellWidth, 
            duration: const Duration(milliseconds: 100),
          )
        ),
      )
    );
  }

  // 获取色卡渐变背景颜色
  List<Color> _colorsList(List<dynamic> color) {
    if (color.length == 12) {
      int r = (color[0] * 255).toInt();
      int g = (color[1] * 255).toInt();
      int b = (color[2] * 255).toInt();
      int a = (color[3] * 255).toInt();

      int r1 = (color[4] * 255).toInt();
      int g1 = (color[5] * 255).toInt();
      int b1 = (color[6] * 255).toInt();
      int a1 = (color[7] * 255).toInt();

      int r2 = (color[8] * 255).toInt();
      int g2 = (color[9] * 255).toInt();
      int b2 = (color[10] * 255).toInt();
      int a2 = (color[11] * 255).toInt();
      if (color[7] == 0.0 && color[11] == 0.0) {
        // 单色眼影
        return [Color.fromARGB(a, r, g, b), Color.fromARGB(a, r, g, b)];
      } else if (color[7] != 0.0 && color[11] == 0.0) {
        // 双色眼影
        return [Color.fromARGB(a, r, g, b), Color.fromARGB(a1, r1, g1, b1)];
      } else {
        // 三色眼影
        return [Color.fromARGB(a, r, g, b), Color.fromARGB(a1, r1, g1, b1), Color.fromARGB(a2, r2, g2, b2)];
      }
    } else {
      int r = (color[0] * 255).toInt();
      int g = (color[1] * 255).toInt();
      int b = (color[2] * 255).toInt();
      int a = (color[3] * 255).toInt();
      return [Color.fromARGB(a, r, g, b), Color.fromARGB(a, r, g, b)];
    }
  }

  // 色卡列表滚动到指定位置
  void scrollToItem(int index) {
    if (scrollController.hasClients) {
      Future.delayed(Duration.zero, (){
        scrollController.animateTo(index * 40 + 20, duration: const Duration(milliseconds: 300), curve: Curves.ease);
      });
    } else {
      WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
        scrollController.animateTo(index * 40 + 20, duration: const Duration(milliseconds: 300), curve: Curves.ease);
      });
    }
  }
}