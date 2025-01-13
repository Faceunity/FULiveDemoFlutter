import 'package:flutter/material.dart';
import 'package:fulive_plugin/render_plugin.dart';
import 'package:fulivedemo_flutter/business/beauty/beauty_view_model.dart';
import 'package:fulivedemo_flutter/business/beauty/filter/filter_view.dart';
import 'package:fulivedemo_flutter/business/beauty/shape/shape_view.dart';
import 'package:fulivedemo_flutter/business/beauty/skin/skin_view.dart';
import 'package:fulivedemo_flutter/business/render/image_render_view.dart';
import 'package:fulivedemo_flutter/business/render/image_render_view_model.dart';
import 'package:fulivedemo_flutter/common/compare_button.dart';
import 'package:fulivedemo_flutter/common/segment_bar.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';

class BeautyImageRender extends StatefulWidget {
  const BeautyImageRender({super.key});

  @override
  State<StatefulWidget> createState() {
    return BeautyImageState();
  }
}

class BeautyImageState extends State<BeautyImageRender> {
  BeautyViewModel viewModel = BeautyViewModel();
  ImageRenderViewModel renderViewModel = ImageRenderViewModel();
  
  @override
  Widget build(BuildContext context) {
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
            ImageRenderView(viewModel: renderViewModel),
            Positioned(
              left: 15,
              bottom: 200 + bottom,
              child: Visibility(
                visible: viewModel.selectedIndex >= 0,
                child: CompareButton(
                  tapStateChanged: (tappedDown) {
                    RenderPlugin.setRenderState(!tappedDown);
                  },
                )
              )
            ),
            // 美肤
            Positioned(
              left: 0,
              right: 0,
              bottom: 49 + bottom,
              child: AnimatedContainer(
                height: viewModel.selectedIndex == BeautyCategory.skin.number ? 141 : 0,
                duration: const Duration(milliseconds: 100),
                child: SkinView(viewModel: viewModel.skinViewModel,),
              )
            ),

            // 美型
            Positioned(
              left: 0,
              right: 0,
              bottom: 49 + bottom,
              child: AnimatedContainer(
                height: viewModel.selectedIndex == BeautyCategory.shape.number ? 141 : 0,
                duration: const Duration(milliseconds: 100),
                child: ShapeView(viewModel: viewModel.shapeViewModel,),
              )
            ),

            // 滤镜
            Positioned(
              left: 0,
              right: 0,
              bottom: 49 + bottom,
              child: AnimatedContainer(
                height: viewModel.selectedIndex == BeautyCategory.filter.number ? 141 : 0,
                duration: const Duration(milliseconds: 100),
                child: FilterView(viewModel: viewModel.filterViewModel),
              )
            ),

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
                  child: SegmentBar(
                    onChange: (index) {
                      setState(() {
                        renderViewModel.captureButtonBottom = index >= 0 ? 195 + bottom : 54 + bottom; 
                        viewModel.selectedIndex = index;
                      });
                    }, 
                    items: const ["美肤", "美型", "滤镜"]
                  ),
                )
              )
            ),
          ],
        )
      )
    );
  }

}