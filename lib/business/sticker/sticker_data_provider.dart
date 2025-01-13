

class StickerDataProvider {
  StickerDataProvider._internal();
  static StickerDataProvider? _instance;
  static StickerDataProvider getInstance() {
    _instance ??= StickerDataProvider._internal();
    return _instance!;
  }

  static dispose() {
    _instance = null;
  }

  List<String> stickers = ["reset_item", "CatSparks", "fu_zh_fenshu", "sdlr", "xlong_zh_fu", "newy1", "redribbt", "DaisyPig", "sdlu"];

  // 默认选中第一个贴纸
  int selectedIndex = 1;  
}