import 'package:json_annotation/json_annotation.dart';

class DevicePerformanceLevel {
  static int levelMinusOne = -1;
  static int levelOne = 1;
  static int levelTwo = 2;
  static int levelThree = 3;
  static int levelFour = 4;
}

enum Module {
  @JsonValue(0)
  beauty(0),

  @JsonValue(1)
  makeup(1),

  @JsonValue(2)
  sticker(2);

  const Module(this.number);
  final int number;
}

enum CapturePreset {
  preset480x640(0, "480x640"),
  preset720x1280(1, "720x1280"),
  preset1080x1920(2, "1080x1920");

  const CapturePreset(this.number, this.description);
  final int number;
  final String description;
}

enum BeautyCategory {
  skin(0),
  shape(1),
  filter(2);

  const BeautyCategory(this.number);
  final int number;
}

enum BeautySkin {
  @JsonValue(0)
  blurLevel(0), // 磨皮

  @JsonValue(1)
  colorLevel(1),  // 美白

  @JsonValue(2)
  redLevel(2),  // 红润

  @JsonValue(3)
  sharpen(3), // 锐化

  @JsonValue(4)
  faceThreed(4),  // 五官立体

  @JsonValue(5)
  eyeBright(5), // 亮眼

  @JsonValue(6)
  toothWhiten(6), // 美牙

  @JsonValue(7)
  removePouchStrength(7), // 去黑眼圈

  @JsonValue(8)
  removeNasolabialFoldsStrength(8), // 去法令纹

  @JsonValue(9)
  antiAcneSpot(9),  // 祛斑痘

  @JsonValue(10)  
  clarity(10);  // 清晰

  const BeautySkin(this.number);
  final int number;

}

enum BeautyShape {
  @JsonValue(0)
  cheekThinning(0), // 瘦脸

  @JsonValue(1)
  cheekV(1),  // v脸

  @JsonValue(2)
  cheekNarrow(2),  // 窄脸

  @JsonValue(3)
  cheekShort(3), // 短脸

  @JsonValue(4)
  cheekSmall(4),  // 小脸

  @JsonValue(5)
  cheekbones(5), // 瘦颧骨

  @JsonValue(6)
  lowerJaw(6), // 瘦下颌骨

  @JsonValue(7)
  eyeEnlarging(7), // 大眼

  @JsonValue(8)
  eyeCircle(8), // 圆眼

  @JsonValue(9)
  chin(9),  // 下巴

  @JsonValue(10)  
  forehead(10),  // 额头

  @JsonValue(11)  
  nose(11),  // 瘦鼻

  @JsonValue(12)  
  mouth(12),  // 嘴型

  @JsonValue(13)  
  lipThick(13),  // 嘴唇厚度

  @JsonValue(14)  
  eyeHeight(14),  // 眼睛位置

  @JsonValue(15)  
  canthus(15),  // 开眼角

  @JsonValue(16)  
  eyeLid(16), // 眼睑下至

  @JsonValue(17)  
  eyeSpace(17),  // 眼距

  @JsonValue(18)  
  eyeRotate(18),  // 眼睛角度

  @JsonValue(19)  
  longNose(19),  // 长鼻

  @JsonValue(20)  
  philtrum(20),  // 缩人中

  @JsonValue(21)  
  smile(21),  // 微笑嘴角

  @JsonValue(22)  
  browHeight(22), // 眉毛上下

  @JsonValue(23)  
  browSpace(23),  // 眉间距

  @JsonValue(24)  
  browThick(24); // 眉毛粗细

  const BeautyShape(this.number);
  final int number;
}

enum SubMakeupType {
  @JsonValue(0)
  foundation(0),  // 粉底

  @JsonValue(1)
  lip(1), // 口红

  @JsonValue(2)
  blusher(2), // 腮红

  @JsonValue(3)
  eyebrow(3), // 眉毛

  @JsonValue(4)
  eyeShadow(4), // 眼影

  @JsonValue(5)
  eyeliner(5),  // 眼线

  @JsonValue(6)
  eyelash(6), // 睫毛

  @JsonValue(7)
  highlight(7), // 高光

  @JsonValue(8)
  shadow(8), // 阴影

  @JsonValue(9)
  pupil(9);  // 美瞳

  const SubMakeupType(this.number);
  final int number;
}
