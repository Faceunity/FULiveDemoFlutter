import 'package:fulive_flutter/Main/MainCellModel.dart';

//公共路由传参模
class FUBaseWidgetArguments {
  MainCellModel? model;
  String? selectedImagePath;

  FUBaseWidgetArguments(this.model, {this.selectedImagePath});
}
