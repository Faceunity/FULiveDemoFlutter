//
//  FUBeautyPlugin.m
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/16.
//

#import "FUBeautyPlugin.h"
#import "FULiveDefine.h"

#import <FURenderKit/FURenderKit.h>


typedef NS_ENUM(NSUInteger, FUBeautySkin) {
    FUBeautySkinBlurLevel = 0,
    FUBeautySkinColorLevel,
    FUBeautySkinRedLevel,
    FUBeautySkinSharpen,
    FUBeautySkinFaceThreed,
    FUBeautySkinEyeBright,
    FUBeautySkinToothWhiten,
    FUBeautySkinRemovePouchStrength,
    FUBeautySkinRemoveNasolabialFoldsStrength,
    FUBeautySkinAntiAcneSpot,
    FUBeautySkinClarity
};

typedef NS_ENUM(NSUInteger, FUBeautyShape) {
    FUBeautyShapeCheekThinning = 0,
    FUBeautyShapeCheekV,
    FUBeautyShapeCheekNarrow,
    FUBeautyShapeCheekShort,
    FUBeautyShapeCheekSmall,
    FUBeautyShapeCheekbones,
    FUBeautyShapeLowerJaw,
    FUBeautyShapeEyeEnlarging,
    FUBeautyShapeEyeCircle,
    FUBeautyShapeChin,
    FUBeautyShapeForehead,
    FUBeautyShapeNose,
    FUBeautyShapeMouth,
    FUBeautyShapeLipThick,
    FUBeautyShapeEyeHeight,
    FUBeautyShapeCanthus,
    FUBeautyShapeEyeLid,
    FUBeautyShapeEyeSpace,
    FUBeautyShapeEyeRotate,
    FUBeautyShapeLongNose,
    FUBeautyShapePhiltrum,
    FUBeautyShapeSmile,
    FUBeautyShapeBrowHeight,
    FUBeautyShapeBrowSpace,
    FUBeautyShapeBrowThick
};

@implementation FUBeautyPlugin

- (void)loadBeauty {
    NSString *path = [[NSBundle mainBundle] pathForResource:@"face_beautification" ofType:@"bundle"];
    FUBeauty *beauty = [[FUBeauty alloc] initWithPath:path name:@"FUBeauty"];
    beauty.heavyBlur = 0;
    // 默认均匀磨皮
    beauty.blurType = 3;
    // 默认精细变形
    beauty.faceShape = 4;
    // 高性能设备设置去黑眼圈、去法令纹、大眼、嘴型最新效果
    if ([FURenderKit devicePerformanceLevel] >= FUDevicePerformanceLevelHigh) {
        [beauty addPropertyMode:FUBeautyPropertyMode2 forKey:FUModeKeyRemovePouchStrength];
        [beauty addPropertyMode:FUBeautyPropertyMode2 forKey:FUModeKeyRemoveNasolabialFoldsStrength];
        [beauty addPropertyMode:FUBeautyPropertyMode3 forKey:FUModeKeyEyeEnlarging];
        [beauty addPropertyMode:FUBeautyPropertyMode3 forKey:FUModeKeyIntensityMouth];
    }
    [FURenderKit shareRenderKit].beauty = beauty;
}

- (void)unloadBeauty {
    [FURenderKit shareRenderKit].beauty = nil;
}

- (void)selectFilter:(NSString *)key {
    if (![FURenderKit shareRenderKit].beauty) {
        FUBeauty *beauty = [FUBeauty itemWithPath:[[NSBundle mainBundle] pathForResource:@"face_beautification" ofType:@"bundle"] name:@"face_beautification"];
        [FURenderKit shareRenderKit].beauty = beauty;
    }
    [FURenderKit shareRenderKit].beauty.filterName = key;
}

- (void)setFilterLevel:(NSNumber *)level {
    if (![FURenderKit shareRenderKit].beauty) {
        return;
    }
    [FURenderKit shareRenderKit].beauty.filterLevel = level.doubleValue;
}

- (void)setSkinIntensity:(NSNumber *)intensity type:(NSNumber *)type {
    double value = intensity.doubleValue;
    switch (type.integerValue) {
        case FUBeautySkinBlurLevel:
            [FURenderKit shareRenderKit].beauty.blurLevel = value;
            break;
        case FUBeautySkinColorLevel:
            [FURenderKit shareRenderKit].beauty.colorLevel = value;
            break;
        case FUBeautySkinRedLevel:
            [FURenderKit shareRenderKit].beauty.redLevel = value;
            break;
        case FUBeautySkinSharpen:
            [FURenderKit shareRenderKit].beauty.sharpen = value;
            break;
        case FUBeautySkinFaceThreed:
            [FURenderKit shareRenderKit].beauty.faceThreed = value;
            break;
        case FUBeautySkinEyeBright:
            [FURenderKit shareRenderKit].beauty.eyeBright = value;
            break;
        case FUBeautySkinToothWhiten:
            [FURenderKit shareRenderKit].beauty.toothWhiten = value;
            break;
        case FUBeautySkinRemovePouchStrength:
            [FURenderKit shareRenderKit].beauty.removePouchStrength = value;
            break;
        case FUBeautySkinRemoveNasolabialFoldsStrength:
            [FURenderKit shareRenderKit].beauty.removeNasolabialFoldsStrength = value;
            break;
        case FUBeautySkinAntiAcneSpot:
            [FURenderKit shareRenderKit].beauty.antiAcneSpot = value;
            break;
        case FUBeautySkinClarity:
            [FURenderKit shareRenderKit].beauty.clarity = value;
            break;
    }
}

- (void)setBeautyParam:(NSString *)key value:(NSNumber *)value{
    
    NSLog(@"key = %@, value = %d",key,value.intValue);
    
    if([key isEqualToString:@"enable_skinseg"]){
        [FURenderKit shareRenderKit].beauty.enableSkinSegmentation = value.boolValue;
    }
}

- (void)setShapeIntensity:(NSNumber *)intensity type:(NSNumber *)type {
    double value = intensity.doubleValue;
    switch (type.integerValue) {
        case FUBeautyShapeCheekThinning:
            [FURenderKit shareRenderKit].beauty.cheekThinning = value;
            break;
        case FUBeautyShapeCheekV:
            [FURenderKit shareRenderKit].beauty.cheekV = value;
            break;
        case FUBeautyShapeCheekNarrow:
            [FURenderKit shareRenderKit].beauty.cheekNarrow = value;
            break;
        case FUBeautyShapeCheekShort:
            [FURenderKit shareRenderKit].beauty.cheekShort = value;
            break;
        case FUBeautyShapeCheekSmall:
            [FURenderKit shareRenderKit].beauty.cheekSmall = value;
            break;
        case FUBeautyShapeCheekbones:
            [FURenderKit shareRenderKit].beauty.intensityCheekbones = value;
            break;
        case FUBeautyShapeLowerJaw:
            [FURenderKit shareRenderKit].beauty.intensityLowerJaw = value;
            break;
        case FUBeautyShapeEyeEnlarging:
            [FURenderKit shareRenderKit].beauty.eyeEnlarging = value;
            break;
        case FUBeautyShapeEyeCircle:
            [FURenderKit shareRenderKit].beauty.intensityEyeCircle = value;
            break;
        case FUBeautyShapeChin:
            [FURenderKit shareRenderKit].beauty.intensityChin = value;
            break;
        case FUBeautyShapeForehead:
            [FURenderKit shareRenderKit].beauty.intensityForehead = value;
            break;
        case FUBeautyShapeNose:
            [FURenderKit shareRenderKit].beauty.intensityNose = value;
            break;
        case FUBeautyShapeMouth:
            [FURenderKit shareRenderKit].beauty.intensityMouth = value;
            break;
        case FUBeautyShapeLipThick:
            [FURenderKit shareRenderKit].beauty.intensityLipThick = value;
            break;
        case FUBeautyShapeEyeHeight:
            [FURenderKit shareRenderKit].beauty.intensityEyeHeight = value;
            break;
        case FUBeautyShapeCanthus:
            [FURenderKit shareRenderKit].beauty.intensityCanthus = value;
            break;
        case FUBeautyShapeEyeLid:
            [FURenderKit shareRenderKit].beauty.intensityEyeLid = value;
            break;
        case FUBeautyShapeEyeSpace:
            [FURenderKit shareRenderKit].beauty.intensityEyeSpace = value;
            break;
        case FUBeautyShapeEyeRotate:
            [FURenderKit shareRenderKit].beauty.intensityEyeRotate = value;
            break;
        case FUBeautyShapeLongNose:
            [FURenderKit shareRenderKit].beauty.intensityLongNose = value;
            break;
        case FUBeautyShapePhiltrum:
            [FURenderKit shareRenderKit].beauty.intensityPhiltrum = value;
            break;
        case FUBeautyShapeSmile:
            [FURenderKit shareRenderKit].beauty.intensitySmile = value;
            break;
        case FUBeautyShapeBrowHeight:
            [FURenderKit shareRenderKit].beauty.intensityBrowHeight = value;
            break;
        case FUBeautyShapeBrowSpace:
            [FURenderKit shareRenderKit].beauty.intensityBrowSpace = value;
            break;
        case FUBeautyShapeBrowThick:
            [FURenderKit shareRenderKit].beauty.intensityBrowThick = value;
            break;
    }
}

- (void)saveSkinToLocal:(NSString *)jsonString {
    if (jsonString.length == 0) {
        return;
    }
    [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:FUPersistentBeautySkinKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)saveShapeToLocal:(NSString *)jsonString {
    if (jsonString.length == 0) {
        return;
    }
    [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:FUPersistentBeautyShapeKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)saveFilterToLocal:(NSString *)jsonString {
    if (jsonString.length == 0) {
        return;
    }
    [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:FUPersistentBeautyFilterKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString *)getLocalSkin {
    if ([[NSUserDefaults standardUserDefaults] objectForKey:FUPersistentBeautySkinKey]) {
        return [[NSUserDefaults standardUserDefaults] stringForKey:FUPersistentBeautySkinKey];
    }
    return nil;
}

- (NSString *)getLocalShape {
    if ([[NSUserDefaults standardUserDefaults] objectForKey:FUPersistentBeautyShapeKey]) {
        return [[NSUserDefaults standardUserDefaults] stringForKey:FUPersistentBeautyShapeKey];
    }
    return nil;
}

- (NSString *)getLocalFilter {
    if ([[NSUserDefaults standardUserDefaults] objectForKey:FUPersistentBeautyFilterKey]) {
        return [[NSUserDefaults standardUserDefaults] stringForKey:FUPersistentBeautyFilterKey];
    }
    return nil;
}

@end
