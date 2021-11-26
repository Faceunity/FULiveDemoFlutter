class FUMakeupColorModel extends Object {
  FUMakeupColorModel(this.offset, this.maxScrollExtent, this.ratio, this.index);
  late double offset; //当前的颜色组件在sliver控件里面的偏移量
  late double maxScrollExtent; //sliver可滚动的最大距离
  late double ratio; //当前颜色组件放大缩放比例
  late int index; //当前颜色组件在列表中的索引

}
