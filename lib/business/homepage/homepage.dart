
import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/business/beauty/beauty.dart';
import 'package:fulivedemo_flutter/business/homepage/homepage_module.dart';
import 'package:fulivedemo_flutter/business/homepage/homepage_modules_data.dart';
import 'package:fulivedemo_flutter/business/makeup/makeup.dart';
import 'package:fulivedemo_flutter/business/sticker/sticker.dart';
import 'package:fulivedemo_flutter/common/custom_page_route.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/live_define.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';
import 'package:fulive_plugin/fulive_plugin.dart';
class HomepageView extends StatefulWidget {
  const HomepageView({super.key});
  @override
  State<StatefulWidget> createState() {
    return _HomepageView();
  }
}

class _HomepageView extends State<HomepageView> {
  List<HomepageModule> _homepageModules = [];
  // 设备是否高性能机型
  late int devicePerformanceLevel = DevicePerformanceLevel.levelTwo;
  @override
  Widget build(BuildContext context) {
    return FutureBuilder(future: _getModules, builder: _buildHomepageFuture);
  }

  Future<List<HomepageModule>> get _getModules async {
    _homepageModules = await HomepageModulesData().getData;
    devicePerformanceLevel = await FaceunityPlugin.devicePerformanceLevel();
    return _homepageModules;
  }

  Widget _buildHomepageFuture(BuildContext context, AsyncSnapshot snapshot) {
    switch (snapshot.connectionState) {
      case ConnectionState.none:
        return const Text("获取数据失败");
      case ConnectionState.done:
        return CustomScrollView(
          slivers: <Widget>[
            _headerView(),
            _moduleGridView()
          ],
        ); 
      default:
        return Container();
    }
  }

  // 头部背景图
  SliverFixedExtentList _headerView() {
    return SliverFixedExtentList (
      delegate: SliverChildBuilderDelegate(
        (BuildContext context, int index) {
          return Image(image: AssetImage(CommonUtil.assetImagePath("homepage/homepage_top_background")), fit: BoxFit.fitWidth);
        }, 
        childCount: 1
      ), 
      itemExtent: ScreenUtil.getInstance().screenWidth * 456 / 750
    );
  }

  // 功能列表
  SliverPadding _moduleGridView() {
    return SliverPadding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 32),
      sliver: SliverGrid.builder(
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          mainAxisSpacing: 16,
          crossAxisSpacing: 16,
          childAspectRatio: 0.828,
        ), 
        itemBuilder: (context, index) {
          return _moduleCell(index);
        },
        itemCount: _homepageModules.length,
      )
    );
  }

  GestureDetector _moduleCell(int index) {
    HomepageModule homepageModule = _homepageModules[index];
    double cellHeight = (ScreenUtil.getInstance().screenWidth - 64)/3/0.828;
    String imageName = "homepage/homepage_cell_bottom";
    if(index == 1){
      imageName = devicePerformanceLevel == DevicePerformanceLevel.levelMinusOne ? "homepage/homepage_cell_bottom_disabled" : "homepage/homepage_cell_bottom";
    }
    return GestureDetector(
      onTap: () {
        switch (homepageModule.module) {
          case Module.beauty:
            Navigator.of(context).push(CustomPageRoute(builder: (context) {
              return const Beauty();
            }));
            break;
          case Module.makeup:
            if(devicePerformanceLevel == DevicePerformanceLevel.levelMinusOne){
              showCommonToast(context: context, content: "该功能只支持在高端机上使用");
            }else{
              Navigator.of(context).push(CustomPageRoute(builder: (context) {
                return const Makeup();
              }));
            }
            break;
          case Module.sticker:
            Navigator.of(context).push(CustomPageRoute(builder: (context) {
              return const Sticker();
            }));
            break;
          default:
        }
      },
      child: Container(
        width: double.infinity,
        clipBehavior: Clip.hardEdge,
        decoration: BoxDecoration(
          color: const Color(0xFF1F1D35),
          borderRadius: BorderRadius.circular(5)
        ),
        child: Stack(
          children: [
            FractionallySizedBox(
              widthFactor: 1,
              heightFactor: 0.75,
              child: Center(
                child: Image(image: AssetImage(CommonUtil.assetImagePath("homepage/${homepageModule.title}"))),
              )
            ),
            Positioned(
              left: 0,
              right: 0,
              bottom: 0,
              height: cellHeight*0.25,
              child: Image(
                  image: AssetImage(CommonUtil.assetImagePath(imageName)),
                  fit: BoxFit.fill),
            ),
            Positioned(
              left: 0,
              right: 0,
              bottom: 0,
              height: cellHeight*0.25,
              child: Center(
                child: Text(
                  homepageModule.title, 
                  style: const TextStyle(fontSize: 13, color: Colors.white), 
                ),
              ),
            )
          ],
        )
      )
    );
  }
}