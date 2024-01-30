import 'package:fulivedemo_flutter/classes/sticker/sticker_data_provider.dart';
import 'package:fulive_plugin/sticker_plugin.dart';

class StickerViewModel {

  void setSelectedIndex(int index) {
    StickerDataProvider.getInstance().selectedIndex = index;
    if (index == 0) {
      StickerPlugin.removeSticker();
    } else {
      StickerPlugin.selectSticker(StickerDataProvider.getInstance().stickers[index]);
    }
  }

  List<String> get stickers {
    return StickerDataProvider.getInstance().stickers;
  }

  int get selectedIndex {
    return StickerDataProvider.getInstance().selectedIndex;
  }

  void dispose() {
    StickerPlugin.removeSticker();
    StickerDataProvider.dispose();
  }

}