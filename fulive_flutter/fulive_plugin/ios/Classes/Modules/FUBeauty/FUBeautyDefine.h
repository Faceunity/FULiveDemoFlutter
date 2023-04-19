//
//  FUBeautyDefine.h
//  FUSDKDemo
//
//  Created by Chen on 2020/11/23.
//  Copyright © 2020 liu. All rights reserved.
//

#ifndef FUBeautyDefine_h
#define FUBeautyDefine_h

typedef NS_ENUM(NSUInteger, FUBeautyDefine) {
    FUBeautyDefineSkin = 0, //美肤
    FUBeautyDefineShape = 1, //美型
    FUBeautyDefineFilter = 2, //滤镜
};

typedef NS_ENUM(NSUInteger, FUBeautifyShape) {
    FUBeautifyShapeCheekThinning, //"cheek_thinning",
    FUBeautifyShapeCheekV, //"cheek_v",
    FUBeautifyShapeCheekNarrow, //"cheek_narrow",
    FUBeautifyShapeCheekShort, //“cheekShort”
    FUBeautifyShapeCheekSmall, //"cheek_small",
    FUBeautifyShapeCheekbones, //"intensity_cheekbones",
    FUBeautifyShapeLowerJaw, //"intensity_lower_jaw",
    FUBeautifyShapeEyeEnlarging, //"eye_enlarging",
    FUBeautifyShapeEyeCircle, //eye_circle
    FUBeautifyShapeChin, //"intensity_chin",
    FUBeautifyShapeForehead, //"intensity_forehead",
    FUBeautifyShapeNose, //"intensity_nose",
    FUBeautifyShapeMouth, //"intensity_mouth",
    FUBeautifyShapeLipThick,
    FUBeautifyShapeEyeHeight,
    FUBeautifyShapeCanthus, //"intensity_canthus",
    FUBeautifyShapeEyeLid,
    FUBeautifyShapeEyeSpace, //"intensity_eye_space",
    FUBeautifyShapeEyeRotate, //"intensity_eye_rotate",
    FUBeautifyShapeLongNose, //"intensity_long_nose",
    FUBeautifyShapePhiltrum, //"intensity_philtrum",
    FUBeautifyShapeSmile, //"intensity_smile",
    FUBeautifyShapeBrowHeight,
    FUBeautifyShapeBrowSpace,
    FUBeautifyShapeBrowThick,
    FUBeautifyShapeMax
};


typedef NS_ENUM(NSUInteger, FUBeautifySkin) {
    FUBeautifySkinBlurLevel, //"blur_level",
    FUBeautifySkinColorLevel, //"color_level",
    FUBeautifySkinRedLevel, //"red_level",
    FUBeautifySkinSharpen, //"sharpen",
    FUBeautifySkinFace, // (五官立体)
    FUBeautifySkinEyeBright, //"eye_bright",
    FUBeautifySkinToothWhiten, //"tooth_whiten",
    FUBeautifySkinRemovePouchStrength, //"remove_pouch_strength",
    FUBeautifySkinRemoveNasolabialFoldsStrength, //"remove_nasolabial_folds_strength"
    FUBeautifySkinMax
};

#endif /* FUBeautyDefine_h */
