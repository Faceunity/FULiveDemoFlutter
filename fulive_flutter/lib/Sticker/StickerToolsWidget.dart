import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fulive_flutter/Sticker/FUStickerManager.dart';
import 'package:fulive_flutter/Sticker/Model/FUStickerModel.dart';
import 'package:provider/provider.dart';

class StickerToolsWidget extends StatefulWidget {
  StickerToolsWidget(this.dataList, {Key? key}) : super(key: key);
  final List<FUStickerModel> dataList;

  @override
  _StickerToolsState createState() => _StickerToolsState();
}

class _StickerToolsState extends State<StickerToolsWidget> {
  late List<FUStickerModel> _dataList;

  @override
  void initState() {
    super.initState();
    _dataList = widget.dataList;
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(mainAxisAlignment: MainAxisAlignment.end, children: [
      Container(
          color: Colors.black87,
          height: 92,
          width: double.infinity,
          child: _stickerList(_dataList))
    ]);
  }

  Widget _stickerList(List dataList) {
    return ListView.separated(
        itemCount: dataList.length,
        padding: const EdgeInsets.fromLTRB(12, 16, 12, 16),
        scrollDirection: Axis.horizontal,
        separatorBuilder: (BuildContext context, int index) {
          return VerticalDivider(
            width: 15,
            color: Color(0x00000000),
          );
        },
        itemBuilder: (BuildContext context, int index) {
          FUStickerModel model = dataList[index];
          return Consumer<FUStickerManager>(builder: (context, manager, child) {
            return Stack(children: [
              Container(
                decoration: BoxDecoration(
                    border: Border.all(
                        color: manager.selectedIndex == index
                            ? Color.fromARGB(255, 94, 199, 254)
                            : Colors.transparent,
                        width: 3.0),
                    borderRadius: BorderRadius.circular(30)),
                child: InkWell(
                    onTap: () => manager.selectedItemWithIndex(index),
                    child: Image(image: AssetImage(model.imageName))),
              ),
              ValueListenableBuilder(
                valueListenable: manager.loadingNotifier,
                builder: (BuildContext context, bool value, Widget? child) {
                  return Visibility(
                      visible: manager.canLoading(index) && value,
                      child: IgnorePointer(
                        ignoring: true,
                        child: Container(
                          width: 60,
                          height: 60,
                          child: Align(
                            alignment: Alignment.center,
                            child: CupertinoActivityIndicator(),
                          ),
                        ),
                      ));
                },
              )
            ]);
          });
        });
  }
}
