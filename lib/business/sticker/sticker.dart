import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/business/beauty/beauty_view_model.dart';
import 'package:fulivedemo_flutter/business/render/render_view_model.dart';
import 'package:fulivedemo_flutter/business/render/render_view.dart';
import 'package:fulivedemo_flutter/business/sticker/sticker_view_model.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';

class Sticker extends StatefulWidget {
  const Sticker({super.key});
  
  @override
  State<StatefulWidget> createState() {
    return StickerState();
  }
}

class StickerState extends State<Sticker> {

  BeautyViewModel beautyViewModel = BeautyViewModel();
  StickerViewModel viewModel = StickerViewModel();
  RenderViewModel renderViewModel = RenderViewModel(Module.sticker);

  @override
  void initState() {
    renderViewModel.captureButtonBottom = 90 + ScreenUtil.getInstance().bottomBarHeight;
    renderViewModel.supportMediaRendering = true;
    renderViewModel.supportPresetSelection = false;
    // 初始化美颜
    beautyViewModel.initialize();
    // 选择贴纸
    viewModel.setSelectedIndex(viewModel.selectedIndex);
    super.initState();
  }

  @override
  void dispose() {
    beautyViewModel.dispose();
    viewModel.dispose();
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
          alignment: Alignment.bottomCenter,
          children: [
            // 基础渲染视图
            RenderView(
              viewModel: renderViewModel,
              viewWillAppear: (){
                setState(() {
                });
              },

            ),

            // 贴纸选择器
            Positioned(
              left: 0,
              right: 0,
              bottom: 0,
              height: 84 + bottom,
              child: Container(
                alignment: AlignmentDirectional.topStart,
                color: const Color.fromARGB(255, 5, 15, 20),
                child: SizedBox(
                  height: 84,
                  width: double.infinity,
                  child: ListView.separated(
                    itemBuilder: (context, index) {
                      return _itemCell(index);
                    },
                    separatorBuilder: (context, index) {
                      return const VerticalDivider(
                        width: 10,
                        color: Color.fromARGB(255, 5, 15, 20)
                      );
                    },
                    padding: const EdgeInsets.fromLTRB(10, 0, 10, 0),
                    itemCount: viewModel.stickers.length,
                    scrollDirection: Axis.horizontal
                  )
                )
              ) 
            ),
          ],
        )
      )
    );
  }

  Widget _itemCell(int index) {
    String name =  viewModel.stickers[index];
    return Center(
      child: Container(
        width: 60,
        height: 60,
        decoration: BoxDecoration(
          border: Border.all(
            color: viewModel.selectedIndex == index ? const Color(0xFF5EC7FE) : Colors.transparent,
            width: 3.0
          ),
          borderRadius: BorderRadius.circular(30.0)
        ),
        child: GestureDetector(
          child: Image(image: CommonUtil.assetImageNamed("sticker/$name"), fit: BoxFit.fill),
          onTap: () {
            if (index != viewModel.selectedIndex) {
              setState(() {
                viewModel.setSelectedIndex(index);
              });
            }
          },
        )
      ),
    );
  }
}