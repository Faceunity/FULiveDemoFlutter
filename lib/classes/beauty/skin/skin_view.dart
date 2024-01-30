
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/classes/beauty/skin/skin_model.dart';
import 'package:fulivedemo_flutter/classes/beauty/skin/skin_view_model.dart';
import 'package:fulivedemo_flutter/common/slider_view.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';
import 'package:provider/provider.dart';

class SkinView extends StatefulWidget {
  final SkinViewModel viewModel;
  const SkinView({super.key, required this.viewModel});

  @override
  State<StatefulWidget> createState() {
    return SkinViewState();
  }

}

class SkinViewState extends State<SkinView> {
  
  @override
  Widget build(BuildContext context) {
    double width = ScreenUtil.getScreenW(context);
    
    return ChangeNotifierProvider(
      create: (context) {
        return widget.viewModel;
      },
      child: Consumer<SkinViewModel>(
        builder: (context, value, child) {
          double sliderValue = 0;
          bool defaulInMiddle = false;
          if (widget.viewModel.skins.isNotEmpty && widget.viewModel.selectedIndex >= 0) {
            sliderValue = widget.viewModel.skins[widget.viewModel.selectedIndex].currentValue / widget.viewModel.skins[widget.viewModel.selectedIndex].ratio;
            defaulInMiddle = widget.viewModel.skins[widget.viewModel.selectedIndex].defaultValueInMiddle;
          }
          return Container(
            color: const Color.fromARGB(200, 0, 0, 0),
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.max,
                verticalDirection: VerticalDirection.up,
                children: [
                  _providerList(width),
                  widget.viewModel.selectedIndex >= 0 ? 
                  SizedBox(
                    height: 50,
                    width: width - 112,
                    child: SliderView(
                      value: sliderValue,
                      defaulInMiddle: defaulInMiddle,
                      onChanged: (value) {
                        // 程度值变化
                        widget.viewModel.setSkinIntensity(value);
                      },
                      onChangeEnd: () {
                        // 滑动结束，需要刷新列表
                        setState(() {
                        });
                      },
                    ),
                  ) : const SizedBox(
                    height: 50,
                  )
                ],
              )
            )
          );
        },
      ),
    );
  }

  Widget _providerList(double screenWidth) {
    // 是否默认值
    bool isDefault = widget.viewModel.isDefaultValue;
    return SizedBox(
      height: 90,
      child: Row(
        children: [
          SizedBox(
            width: 68,
            child: Opacity(
              opacity: isDefault ? 0.6 : 1,
              child: TextButton(
                style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                  overlayColor: MaterialStatePropertyAll(Colors.transparent)
                ),
                onPressed: () {
                  if (!isDefault) {
                    showAlertDialog(
                      context: context, 
                      content: "是否将所有参数恢复到默认值",
                      comformPressed: () {
                        widget.viewModel.recoverAllSkinValuesToDefault();
                      },
                    );
                  }
                }, 
                child: Column(
                  children: [
                    SizedBox(
                      height: 44,
                      width: 44,
                      child: Image(
                        image: CommonUtil.assetImageNamed("common/recover"),
                        fit: BoxFit.fill,
                      ),
                    ),
                    const SizedBox(
                      height: 24,
                      width: 44,
                      child: Align(
                        alignment: Alignment.center,
                        child: Text("恢复", style: TextStyle(fontSize: 10, color: Colors.white), textAlign: TextAlign.center,),
                      )
                    )
                  ],
                )
              ),
            ),
          ),
          const VerticalDivider(
            width: 1,
            endIndent: 45,
            indent: 20,
            color: Color.fromARGB(51, 229, 229, 229),
          ),
          SizedBox(
            width: screenWidth - 69,
            child: ListView.builder(
              padding: const EdgeInsets.only(left:4),
              itemBuilder: (context, index) {
                return _itemCell(index);
              },
              itemCount: widget.viewModel.skins.length,
              scrollDirection: Axis.horizontal
            ),
          )
        ],
      )
    );
  }

  Widget _itemCell(int index) {
    SkinModel skin = widget.viewModel.skins[index];
    String name = skin.name;
    bool needsNPU = skin.needsNPUSupport;
    bool needsHighPerformance = skin.differentiateDevicePerformance;

    bool disabled = false;
    if (needsNPU) {
      disabled = !widget.viewModel.supportsNPU;
    } else {
      if (needsHighPerformance) {
        disabled = !widget.viewModel.highPerformanceDevice;
      }
    }

    String imageName;
    Color textColor = Colors.white;
    if (disabled) {
      imageName = "$name-0";
    } else {
      if (widget.viewModel.selectedIndex == index) {
        imageName = skin.currentValue > 0.01 ? "$name-3" : "$name-2";
        textColor = const Color.fromARGB(255, 94, 199, 254);
      } else {
        imageName = skin.currentValue > 0.01 ? "$name-1" : "$name-0";
        textColor = Colors.white;
      }
    }
    
    return Opacity(
      opacity: disabled ? 0.6 : 1.0,
      child: TextButton(
        style: const ButtonStyle(
          backgroundColor: MaterialStatePropertyAll(Colors.transparent),
          overlayColor: MaterialStatePropertyAll(Colors.transparent)
        ),
        onPressed: () {
          if (!disabled) {
            setState(() {
              widget.viewModel.setSelectedIndex(index);
            });
          } else {
            if (needsNPU) {
              // 提示仅支持 NPU 机型
              showCommonToast(context: context, content: defaultTargetPlatform == TargetPlatform.iOS ? "该功能仅支持iPhoneXR及以上机型使用" : "该功能即将上线");
            }
          }
        }, 
        child: Column(
          children: [
            SizedBox(
              height: 44,
              width: 44,
              child: Image(
                image: CommonUtil.assetImageNamed("beauty/skin/$imageName"),
                fit: BoxFit.fill,
              ),
            ),
            SizedBox(
              height: 24,
              width: 44,
              child: Align(
                alignment: Alignment.center,
                child: Text(name, style: TextStyle(fontSize: 10, color: textColor), textAlign: TextAlign.center,),
              )
            )
          ],
        )
      ),
    );
  }
}