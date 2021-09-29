import 'package:flutter/material.dart';
import 'package:fulive_flutter/Main/MainCellModel.dart';
import 'package:fulive_flutter/Main/MainDataModel.dart';
import 'package:fulive_flutter/Makeup/FUMakeup.dart';
import 'package:fulive_plugin/FUBasicMessageManager.dart';
import 'dart:developer' as developer;
import 'dart:async';
import 'package:fulive_flutter/Main/MainRouterDefine.dart';
import 'package:fulive_flutter/Beauty/FUBeauty.dart';

import 'BaseModule/FUBaseWidgetArguments.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'FULive Demo 特效版',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or press Run > Flutter Hot Reload in a Flutter IDE). Notice that the
        // counter didn't reset back to zero; the application is not restarted.
        primaryColor: Color(0xFF030110),
      ),
      home: MyHomePage(title: 'FULive Demo 特效版'),
      // initialRoute: '/',
      routes: {
        FUBeauty.routerName: (context) => FUBeauty(),
        FUMakeup.routerName: (context) => FUMakeup(),
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key? key, required this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  List<List<MainCellModel>> _dataList = [];
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is c
    //alled, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
        toolbarHeight: 44,
        bottom: PreferredSize(
            child: SizedBox(
                child: Container(height: 1.0, color: Color(0xFF302D33))),
            preferredSize: Size(double.infinity, 1)),
      ),
      backgroundColor: Color(0xFF310),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: FutureBuilder<List<List<MainCellModel>>>(
          builder: (BuildContext context, AsyncSnapshot snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              if (snapshot.hasError) {
                // 请求失败，显示错误
                return Text("Error: ${snapshot.error}");
              } else {
                // // 请求成功，显示数据
                // List<List<MainCellModel>> dataList = snapshot.data;
                return CustomScrollView(
                  slivers: <Widget>[
                    _customHeaderBgView(),
                    _commonSliverHeader(_dataList.length >= 1 ? "人脸特效" : ""),
                    _commonSliverCell(
                        _dataList.length >= 1 ? _dataList[0] : []),
                    _commonSliverHeader(_dataList.length >= 2 ? "人体特效" : ""),
                    _commonSliverCell(
                        _dataList.length >= 2 ? _dataList[1] : []),
                    _commonSliverHeader(_dataList.length >= 3 ? "内容服务" : ""),
                    _commonSliverCell(
                        _dataList.length >= 3 ? _dataList[2] : []),
                  ],
                );
              }
            } else {
              // 请求未结束，显示loading
              // return CircularProgressIndicator();
              return Container();
            }
          },
          future: _reloadDataSource(),
        ),
      ),
    );
  }

  SliverPadding _customHeaderBgView() {
    return SliverPadding(
        padding: const EdgeInsets.fromLTRB(0, 0, 0, 5.0),
        sliver: SliverList(
          delegate: SliverChildBuilderDelegate(
            (BuildContext context, int index) {
              return Image(
                image: AssetImage(
                    "resource/images/homeView/homeview_background_top.png"),
              );
            },
            childCount: 1,
          ),
        ));
  }

  //自定义滑动组件-> 头部标签组件，没找到iOS上Collection带Section的结构。
  SliverPadding _commonSliverHeader(String title) {
    return SliverPadding(
        padding: const EdgeInsets.fromLTRB(0, 0, 0, 5),
        sliver: SliverList(
          delegate: SliverChildBuilderDelegate(
            (BuildContext context, int index) {
              return Visibility(
                  visible: title.length > 0, child: _commonHeader(title));
            },
            childCount: 1,
          ),
        ));
  }

  //自定义滑动组件-> 内容cell组件，没找到iOS上Collection带Section的结构。
  SliverPadding _commonSliverCell(List<MainCellModel> dataList) {
    return SliverPadding(
      padding: const EdgeInsets.fromLTRB(8, 0, 8, 25),
      sliver: SliverGrid(
        delegate:
            new SliverChildBuilderDelegate((BuildContext context, int index) {
          return dataList.length > 0
              ? _commonCell(dataList, index)
              : Text("无数据");
        }, childCount: dataList.length),
        gridDelegate: new SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 3,
            mainAxisSpacing: 0.0,
            crossAxisSpacing: 1.0,
            childAspectRatio: 0.8),
      ),
    );
  }

  //自定义cell
  TextButton _commonCell(List<MainCellModel> dataList, int index) {
    MainCellModel model = dataList[index];
    return TextButton(
      onPressed: model.enable
          ? () {
              var itemType = dataList[index].itemType;
              mainRouter(itemType, dataList[index]);
            }
          : null,
      child: ClipRRect(
        borderRadius: BorderRadius.circular(5.0),
        child: Container(
          alignment: Alignment.center,
          color: Color(0xFF1f1d35),
          child: Column(children: [
            Expanded(
              child: Image(
                image: AssetImage(model.imagePath),
              ),
            ),
            Container(
              alignment: Alignment.center,
              child: Stack(
                alignment: Alignment.center,
                children: <Widget>[
                  Container(
                    child: Image(
                      image: AssetImage(model.enable
                          ? "resource/images/homeView/bottomImage.png"
                          : "resource/images/homeView/bottomImage_gray.png"),
                      fit: BoxFit.contain,
                      width: double.infinity,
                    ),
                  ),
                  Text(
                    model.itemName,
                    style: _mainTextStyle(),
                  ),
                ],
              ),
            ),
          ]),
        ),
      ),
    );
  }

  //头部组件
  Row _commonHeader(String title) {
    return Row(
      children: [
        Container(
          margin: EdgeInsets.fromLTRB(16, 0, 0, 5),
        ),
        ClipRRect(
          child: SizedBox(
            child: Container(
              height: 13,
              width: 4.0,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFFF661FF), Color(0xFF7755FC)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
            ),
          ),
          borderRadius: BorderRadius.circular(2.0),
        ),
        Container(
          margin: EdgeInsets.fromLTRB(10, 0, 0, 0),
        ),
        Text(
          title,
          style: _mainTextStyle(),
        ),
      ],
    );
  }

  TextStyle _mainTextStyle() {
    return TextStyle(color: Colors.white);
  }

  Future<List<List<MainCellModel>>> _reloadDataSource() async {
    if (_dataList.length != 0) {
      return _dataList;
    }
    _dataList = await MainDataModel().getModels;
    return _dataList;
  }

  void mainRouter(MainRouters itemType, MainCellModel model) {
    developer.log("itemType: $itemType isClicked");
    switch (itemType) {
      case MainRouters.FULiveModelTypeBeautifyFace:
        {
          //美颜
          Navigator.pushNamed(context, FUBeauty.routerName,
              arguments: FUBaseWidgetArguments(model,
                  selectedImagePath:
                      "resource/images/commonImage/demoIconMore.png"));
        }
        break;
      case MainRouters.FULiveModelTypeMakeUp:
        {
          //美颜
          Navigator.pushNamed(context, FUMakeup.routerName,
              arguments: FUBaseWidgetArguments(model));
        }
        break;
      default:
        FUBasicMessageManager.fuRoutes(itemType.index);
    }
  }
}
