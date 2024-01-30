import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:fulivedemo_flutter/classes/makeup/custom/customized_makeup_view_model.dart';
import 'package:fulivedemo_flutter/common/slider_view.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';
import 'package:provider/provider.dart';

class CustomizedMakeupView extends StatefulWidget {
  final CustomizedMakeupViewModel viewModel;

  // 点击返回回调
  final VoidCallback? clickBackCallBack;

  final VoidCallback? onChanged;

  const CustomizedMakeupView({super.key, required this.viewModel, this.clickBackCallBack, this.onChanged});

  @override
  State<StatefulWidget> createState() {
    return CustomizedMakeupState();
  }

}

class CustomizedMakeupState extends State<CustomizedMakeupView> {
  @override
  Widget build(BuildContext context) {
    // 底部安全区域高度
    final bottom = MediaQuery.of(context).padding.bottom;
    return ChangeNotifierProvider(
      create: (context) {
        return widget.viewModel;
      },
      child: Consumer<CustomizedMakeupViewModel>(
        builder: (context, value, child) {
          return Stack(
            alignment: Alignment.bottomRight,
            children: [
              // 底部功能栏
              Positioned(
                left: 0,
                right: 0,
                bottom: 0,
                height: 49 + bottom,
                child: Container(
                  alignment: AlignmentDirectional.topStart,
                  color: const Color.fromARGB(255, 5, 15, 20),
                  child: SizedBox(
                    height: 49,
                    width: double.infinity,
                    child: _categorySegment()
                  )
                )
              ),

              Positioned(
                bottom: 49 + bottom,
                left: 0,
                right: 0,
                child: _subMakeupList()
              )
            ],
          );
        },
      )
    );
  }

  Widget _subMakeupList() {
    double width = ScreenUtil.getInstance().screenWidth;
    return Container(
      color: const Color.fromARGB(200, 0, 0, 0),
      child: SingleChildScrollView(
        child: Column(
          children: [
            widget.viewModel.selectedSubMakeupIndex > 0 ? 
            SizedBox(
              height: 40,
              width: width - 112,
              child: SliderView(
                  value: widget.viewModel.selectedSubMakeupValue,
                  onChanged: (value) {
                    widget.viewModel.setSelectedSubMakeupValue(value);
                  },
                  onChangeEnd: () {
                    setState(() {
                    });
                  },
              ),
            ) : const SizedBox(
              height: 50,
            ),
            SizedBox(
              height: 81,
              child: Row(
                children: [
                  SizedBox(
                    width: 68,
                    child: TextButton(
                      style: const ButtonStyle(
                        backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                        overlayColor: MaterialStatePropertyAll(Colors.transparent)
                      ),
                      onPressed: () {
                        if (widget.clickBackCallBack != null) {
                          widget.clickBackCallBack!();
                        }
                      }, 
                      child: Container(
                        padding: const EdgeInsets.only(bottom: 8),
                        height: 54,
                        width: 54,
                        decoration: BoxDecoration(
                          image: DecorationImage(image: CommonUtil.assetImageNamed("makeup/makeup_back")),
                          color: Colors.transparent,
                          borderRadius: BorderRadius.circular(3.0)
                        ),
                      ),
                    )
                  ),
                  const VerticalDivider(
                    width: 1,
                    endIndent: 22,
                    indent: 18,
                    color: Color.fromARGB(51, 229, 229, 229),
                  ),
                  SizedBox(
                    width: width - 69,
                    child: ListView.builder(
                      padding: const EdgeInsets.only(left: 4),
                      itemBuilder: (context, index) {
                        return _itemCell(index);
                      },
                      itemCount: widget.viewModel.selectedSubMakeups.length,
                      scrollDirection: Axis.horizontal
                    ),
                  )
                ],
              )
            ),

            
          ],
        )
      )
    );
  }

  Widget _itemCell(int index) {
    AssetImage? icon = widget.viewModel.subMakeupImageAtIndex(index);
    return TextButton(
      style: const ButtonStyle(
        backgroundColor: MaterialStatePropertyAll(Colors.transparent),
        overlayColor: MaterialStatePropertyAll(Colors.transparent)
      ),
      onPressed: () {
        if (widget.viewModel.selectedSubMakeupIndex != index) {
          widget.viewModel.setSelectedSubMakeupIndex(index);
          if (widget.viewModel.selectedSubMakeupTitle != null) {
            showCommonToast(context: context, content: widget.viewModel.selectedSubMakeupTitle!, contentFontSize: 32, gravity: ToastGravity.CENTER, backgroundColor: Colors.transparent, duration: const Duration(milliseconds: 1000));
          }
          if (widget.onChanged != null) {
            widget.onChanged!();
          }
        }
      }, 
      child: Container(
        height: 54,
        width: 54,
        decoration: BoxDecoration(
          color: icon == null ? widget.viewModel.subMakeupBackgroundColorAtIndex(index) : Colors.transparent,
          image: icon == null ? null : DecorationImage(image: icon),
          border: Border.all(
            color: widget.viewModel.selectedSubMakeupIndex == index ? const Color(0xFF5EC7FE) : Colors.transparent,
            width: 2
          ),
          borderRadius: BorderRadius.circular(3.0)
        ),
      ),
    );
  }

  Widget _categorySegment() {
    return ListView.builder(
      itemBuilder: (context, index) {
        return _categoryItemCell(index);
      },
      itemCount: widget.viewModel.customizedMakeups.length,
      scrollDirection: Axis.horizontal
    );
  }

  Widget _categoryItemCell(int index) {
    return SizedBox(
      width: 70,
      height: double.infinity,
      child: Stack(
        alignment: Alignment.topRight,
        children: [
          Positioned(
            right: 8,
            top: 8,
            child: Visibility(
              visible: widget.viewModel.hasValidValueAtCategoryIndex(index),
              child: Container(
                height: 4,
                width: 4,
                decoration: BoxDecoration(
                  color: const Color.fromARGB(255, 94, 199, 254),
                  borderRadius: BorderRadius.circular(2)
                ),
              )
            ),
             
          ),
          Center(
            child: TextButton(
              style: const ButtonStyle(
                backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                overlayColor: MaterialStatePropertyAll(Colors.transparent)
              ),
              onPressed: () {
                if (widget.viewModel.selectedCategoryIndex != index) {
                  setState(() {
                    widget.viewModel.selectedCategoryIndex = index;
                    if (widget.onChanged != null) {
                      widget.onChanged!();
                    }
                  });
                }
              }, 
              child: Center(
                child: Text(
                  widget.viewModel.customizedMakeups[index].name,
                  style: TextStyle(
                    color: index == widget.viewModel.selectedCategoryIndex ? const Color.fromARGB(255, 94, 199, 254) : Colors.white,
                    fontSize: 13,
                  ),
                ),
              )
            ),
          )
        ],
      ),
       
    );
  }
}