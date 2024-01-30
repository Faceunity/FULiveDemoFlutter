import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/classes/makeup/combination/combination_makeup_view_model.dart';
import 'package:fulivedemo_flutter/common/slider_view.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';
import 'package:provider/provider.dart';

class CombinationMakeupView extends StatefulWidget {

  final CombinationMakeupViewModel viewModel;
    // 点击自定义回调
  final VoidCallback? clickCustomizeCallBack;

  const CombinationMakeupView({super.key, required this.viewModel, this.clickCustomizeCallBack});

  @override
  State<StatefulWidget> createState() {
    return CombinationMakeupState();
  }

}

class CombinationMakeupState extends State<CombinationMakeupView> {
  @override
  Widget build(BuildContext context) {
    double width = ScreenUtil.getScreenW(context);
    return ChangeNotifierProvider(
      create: (context) {
        return widget.viewModel;
      },
      child: Consumer<CombinationMakeupViewModel>(builder: (context, value, child) {
        return Container(
          color: const Color.fromARGB(200, 0, 0, 0),
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.max,
              verticalDirection: VerticalDirection.up,
              children: [
                _providerList(width),
                widget.viewModel.selectedIndex > 0 ? 
                SizedBox(
                  height: 50,
                  width: width - 112,
                  child: SliderView(
                    value: widget.viewModel.makeups.isNotEmpty ? widget.viewModel.makeups[widget.viewModel.selectedIndex].value : 0,
                    onChanged: (value) {
                      widget.viewModel.setSelectedMakeupValue(value);
                    },
                  ),
                ) : const SizedBox(
                  height: 50,
                )
              ],
            )
          )
        );
      },),
    );
  }

  Widget _providerList(double screenWidth) {
    bool customized = widget.viewModel.isSelectedMakeupAllowedEdit;
    return SizedBox(
      height: 98,
      child: Row(
        children: [
          SizedBox(
            width: 68,
            child: Opacity(
              opacity: customized ? 1 : 0.6,
              child: TextButton(
                style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.transparent),
                  overlayColor: MaterialStatePropertyAll(Colors.transparent)
                ),
                onPressed: () {
                  if (customized && widget.clickCustomizeCallBack != null) {
                    widget.clickCustomizeCallBack!();
                  }
                }, 
                child: Column(
                  children: [
                    Container(
                      height: 54,
                      width: 54,
                      decoration: BoxDecoration(
                        image: DecorationImage(image: CommonUtil.assetImageNamed("makeup/makeup_custom")),
                        border: Border.all(
                          color: Colors.transparent,
                          width: 2
                        ),
                        borderRadius: BorderRadius.circular(3.0)
                      ),
                    ),
                    const SizedBox(
                      height: 16,
                      width: 54,
                      child: Center(
                        child: Text("自定义", style: TextStyle(fontSize: 10, color: Colors.white), textAlign: TextAlign.center,),
                      )
                    )
                  ],
                )
              ),
            ),
          ),
          const VerticalDivider(
            width: 1,
            endIndent: 54,
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
              itemCount: widget.viewModel.makeups.length,
              scrollDirection: Axis.horizontal
            ),
          )
        ],
      )
    );
  }

  Widget _itemCell(int index) {
    String makeupName = widget.viewModel.makeups[index].name;
    String makeupIcon = widget.viewModel.makeups[index].icon;
    return TextButton(
      style: const ButtonStyle(
        backgroundColor: MaterialStatePropertyAll(Colors.transparent),
        overlayColor: MaterialStatePropertyAll(Colors.transparent)
      ),
      onPressed: () {
        setState(() {
          widget.viewModel.setSelectedIndex(index);
        });
      }, 
      
      child: Column(
        children: [
          Container(
            height: 54,
            width: 54,
            decoration: BoxDecoration(
              image: DecorationImage(image: CommonUtil.assetImageNamed("makeup/combination/$makeupIcon")),
              border: Border.all(
                color: widget.viewModel.selectedIndex == index ? const Color(0xFF5EC7FE) : Colors.transparent,
                width: 2
              ),
              borderRadius: BorderRadius.circular(3.0)
            ),
          ),
          SizedBox(
            height: 16,
            width: 54,
            child: Center(
              child: Text(makeupName, style: const TextStyle(fontSize: 10, color: Colors.white), textAlign: TextAlign.center,),
            )
          )
        ],
      )
    );
  }

} 