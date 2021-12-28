abstract class FUBizAbstractWidget {
  //配置 native plugin
  Future configBiz() async {}

  //即将显示当前页面，适用于页面pop回来
  Future flutterWillAppear() async {}
  //即将离开当前页面，适用于页面push
  Future flutterWillDisappear() async {}

  //widget 释放
  Future dispose() async {}
}
