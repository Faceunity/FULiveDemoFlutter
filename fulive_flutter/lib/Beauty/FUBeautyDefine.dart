enum FUBeautyDefine {
  FUBeautyDefineSkin, //美肤
  FUBeautyDefineShape, //美型
  FUBeautyDefineFilter, //滤镜
  FUBeautyMax, //作为none和最大值使用
}

enum FUBeautifyShape {
  FUBeautifyShapeCheekThinning, //"cheek_thinning",
  FUBeautifyShapeCheekV, //"cheek_v",
  FUBeautifyShapeCheekNarrow, //"cheek_narrow",
  FUBeautifyShapeCheekSmall, //"cheek_small",
  FUBeautifyShapeIntensityCheekbones, //"intensity_cheekbones",
  FUBeautifyShapeIntensityLowerJaw, //"intensity_lower_jaw",
  FUBeautifyShapeEyeEnlarging, //"eye_enlarging",
  FUBeautifyShapeEyeCircle, //eye_circle
  FUBeautifyShapeIntensityChin, //"intensity_chin",
  FUBeautifyShapeIntensityForehead, //"intensity_forehead",
  FUBeautifyShapeIntensityNose, //"intensity_nose",
  FUBeautifyShapeIntensityMouth, //"intensity_mouth",
  FUBeautifyShapeIntensityCanthus, //"intensity_canthus",
  FUBeautifyShapeIntensityEyeSpace, //"intensity_eye_space",
  FUBeautifyShapeIntensityEyeRotate, //"intensity_eye_rotate",
  FUBeautifyShapeIntensityLongNose, //"intensity_long_nose",
  FUBeautifyShapeIntensityPhiltrum, //"intensity_philtrum",
  FUBeautifyShapeIntensitySmile, //"intensity_smile",

  FUBeautifyShapeMax,
}

enum FUBeautifySkin {
  FUBeautifySkinBlurLevel, //"blur_level",
  FUBeautifySkinColorLevel, //"color_level",
  FUBeautifySkinRedLevel, //"red_level",
  FUBeautifySkinSharpen, //"sharpen",
  FUBeautifySkinFace, //
  FUBeautifySkinEyeBright, //"eye_bright",
  FUBeautifySkinToothWhiten, //"tooth_whiten",
  FUBeautifySkinRemovePouchStrength, //"remove_pouch_strength",
  FUBeautifySkinRemoveNasolabialFoldsStrength, //"remove_nasolabial_folds_strength"

  FUBeautifySkinMax
}

enum FUBeautyStyleType {
  FUBeautyStyleTypeNone,
  FUBeautyStyleType1,
  FUBeautyStyleType2,
  FUBeautyStyleType3,
  FUBeautyStyleType4,
  FUBeautyStyleType5,
  FUBeautyStyleType6,
  FUBeautyStyleType7,

  FUBeautyStyleMax
}

/// 设备性能等级
enum FUDevicePerformanceLevel {
  FUDevicePerformanceLevelLow,
  FUDevicePerformanceLevelMedium,
  FUDevicePerformanceLevelHigh,

  FUDevicePerformanceLevelMax
}
