//
//  FUMakeupPlugin.m
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/20.
//

#import "FUMakeupPlugin.h"
#import "FUCombinationMakeupModel.h"
#import "FUSubMakeupModel.h"
#import "FUUtility.h"
#import <FURenderKit/FURenderKit.h>
#import <YYModel/YYModel.h>

@implementation FUMakeupPlugin

- (void)loadCombinationMakeup:(NSDictionary *)makeup {
    if (!makeup) {
        return;
    }
    if ([FURenderKit shareRenderKit].makeup) {
        [FURenderKit shareRenderKit].makeup.enable = NO;
    }
    [FURenderQueue async:^{
        [FURenderKit shareRenderKit].makeup = nil;
        FUCombinationMakeupModel *model = [FUCombinationMakeupModel yy_modelWithJSON:makeup];
        // @note 嗲嗲兔、冻龄、国风、混血是8.0.0新加的四个组合妆，新组合妆只需要直接加载bundle，不需要绑定到face_makeup.bundle
        if (model.isCombined) {
            // 新组合妆，每次加载必须重新初始化
            NSString *path = [FUUtility pluginBundlePathWithName:model.bundleName];
            FUMakeup *makeup = [[FUMakeup alloc] initWithPath:path name:@"makeup"];
            // 高端机打开全脸分割
            makeup.makeupSegmentation = [FURenderKit devicePerformanceLevel] >= FUDevicePerformanceLevelHigh;
            [FURenderKit shareRenderKit].makeup = makeup;
        } else {
            FUMakeup *makeup = [FUMakeup itemWithPath:[[NSBundle mainBundle] pathForResource:@"face_makeup" ofType:@"bundle"] name:@"face_makeup"];
            makeup.makeupSegmentation = [FURenderKit devicePerformanceLevel] >= FUDevicePerformanceLevelHigh;
            [FURenderKit shareRenderKit].makeup = makeup;
            [self bindCombinationMakeupWithBundleName:model.bundleName];
        }
        [self updateFilterOfCombinationMakeup:model];
        [self updateIntensityOfCombinationMakeup:model];
        [FURenderKit shareRenderKit].makeup.enable = YES;
    }];
}

- (void)setCombinationMakeupIntensity:(NSDictionary *)makeup {
    if (!makeup) {
        return;
    }
    FUCombinationMakeupModel *model = [FUCombinationMakeupModel yy_modelWithJSON:makeup];
    [self updateIntensityOfCombinationMakeup:model];
    [self updateFilterOfCombinationMakeup:model];
}

- (void)setSubMakeupBundle:(NSDictionary *)subMakeup {
    if (!subMakeup) {
        return;
    }
    FUSubMakeupModel *model = [FUSubMakeupModel yy_modelWithJSON:subMakeup];
    if (![FURenderKit shareRenderKit].makeup) {
        FUMakeup *makeup = [FUMakeup itemWithPath:[[NSBundle mainBundle] pathForResource:@"face_makeup" ofType:@"bundle"] name:@"face_makeup"];
        makeup.makeupSegmentation = [FURenderKit devicePerformanceLevel] >= FUDevicePerformanceLevelHigh;
        [FURenderKit shareRenderKit].makeup = makeup;
    }
    NSString *subPath = [FUUtility pluginBundlePathWithName:model.bundleName];
    FUItem *item = [[FUItem alloc] initWithPath:subPath name:model.bundleName];
    switch (model.type) {
        case FUSubMakeupTypeFoundation:{
            [FURenderKit shareRenderKit].makeup.subFoundation = item;
        }
            break;
        case FUSubMakeupTypeLip:{
            [FURenderKit shareRenderKit].makeup.subLip = item;
        }
            break;
        case FUSubMakeupTypeBlusher:{
            [FURenderKit shareRenderKit].makeup.subBlusher = item;
        }
            break;
        case FUSubMakeupTypeEyebrow:{
            [FURenderKit shareRenderKit].makeup.subEyebrow = item;
        }
            break;
        case FUSubMakeupTypeEyeShadow:{
            [FURenderKit shareRenderKit].makeup.subEyeshadow = item;
        }
            break;
        case FUSubMakeupTypeEyeliner:{
            [FURenderKit shareRenderKit].makeup.subEyeliner = item;
        }
            break;
        case FUSubMakeupTypeEyelash:{
            [FURenderKit shareRenderKit].makeup.subEyelash = item;
        }
            break;
        case FUSubMakeupTypeHighlight:{
            [FURenderKit shareRenderKit].makeup.subHighlight = item;
        }
            break;
        case FUSubMakeupTypeShadow:{
            [FURenderKit shareRenderKit].makeup.subShadow = item;
        }
            break;
        case FUSubMakeupTypePupil:{
            [FURenderKit shareRenderKit].makeup.subPupil = item;
        }
            break;
    }
}

- (void)setSubMakeupIntensity:(NSDictionary *)subMakeup {
    if (!subMakeup) {
        return;
    }
    FUSubMakeupModel *model = [FUSubMakeupModel yy_modelWithJSON:subMakeup];
    switch (model.type) {
        case FUSubMakeupTypeFoundation:{
            [FURenderKit shareRenderKit].makeup.intensityFoundation = model.value;
        }
            break;
        case FUSubMakeupTypeLip:{
            [FURenderKit shareRenderKit].makeup.lipType = model.lipstickType;
            [FURenderKit shareRenderKit].makeup.isTwoColor = model.isTwoColorLipstick;
            [FURenderKit shareRenderKit].makeup.intensityLip = model.value;
            if (model.lipstickType == FUMakeupLipTypeMoisturizing) {
                // 润泽Ⅱ口红时需要开启口红高光，高光暂时为固定值0.8
                [FURenderKit shareRenderKit].makeup.isLipHighlightOn = YES;
                [FURenderKit shareRenderKit].makeup.intensityLipHighlight = 0.8;
            } else {
                [FURenderKit shareRenderKit].makeup.isLipHighlightOn = NO;
                [FURenderKit shareRenderKit].makeup.intensityLipHighlight = 0;
            }
        }
            break;
        case FUSubMakeupTypeBlusher:{
            [FURenderKit shareRenderKit].makeup.intensityBlusher = model.value;
        }
            break;
        case FUSubMakeupTypeEyebrow:{
            [FURenderKit shareRenderKit].makeup.intensityEyebrow = model.value;
        }
            break;
        case FUSubMakeupTypeEyeShadow:{
            [FURenderKit shareRenderKit].makeup.intensityEyeshadow = model.value;
        }
            break;
        case FUSubMakeupTypeEyeliner:{
            [FURenderKit shareRenderKit].makeup.intensityEyeliner = model.value;
        }
            break;
        case FUSubMakeupTypeEyelash:{
            [FURenderKit shareRenderKit].makeup.intensityEyelash = model.value;
        }
            break;
        case FUSubMakeupTypeHighlight:{
            [FURenderKit shareRenderKit].makeup.intensityHighlight = model.value;
        }
            break;
        case FUSubMakeupTypeShadow:{
            [FURenderKit shareRenderKit].makeup.intensityShadow = model.value;
        }
            break;
        case FUSubMakeupTypePupil:{
            [FURenderKit shareRenderKit].makeup.intensityPupil = model.value;
        }
            break;
    }
}

- (void)setSubMakeupColor:(NSDictionary *)subMakeup {
    if (!subMakeup) {
        return;
    }
    FUSubMakeupModel *model = [FUSubMakeupModel yy_modelWithJSON:subMakeup];
    if (model.colors.count == 0) {
        return;
    }
    NSArray *colorValues = model.colors[model.defaultColorIndex];
    FUColor color = FUColorMake([colorValues[0] doubleValue], [colorValues[1] doubleValue], [colorValues[2] doubleValue], [colorValues[3] doubleValue]);
    switch (model.type) {
        case FUSubMakeupTypeFoundation:{
            [FURenderKit shareRenderKit].makeup.foundationColor = color;
        }
            break;
        case FUSubMakeupTypeLip:{
            [FURenderKit shareRenderKit].makeup.lipColor = color;
        }
            break;
        case FUSubMakeupTypeBlusher:{
            [FURenderKit shareRenderKit].makeup.blusherColor = color;
        }
            break;
        case FUSubMakeupTypeEyebrow:{
            [FURenderKit shareRenderKit].makeup.eyebrowColor = color;
        }
            break;
        case FUSubMakeupTypeEyeShadow:{
            NSArray *values0 = [model.colors[model.defaultColorIndex] subarrayWithRange:NSMakeRange(0, 4)];
            NSArray *values2 = [model.colors[model.defaultColorIndex] subarrayWithRange:NSMakeRange(4, 4)];
            NSArray *values3 = [model.colors[model.defaultColorIndex] subarrayWithRange:NSMakeRange(8, 4)];
            [[FURenderKit shareRenderKit].makeup setEyeColor:FUColorMake([values0[0] doubleValue], [values0[1] doubleValue], [values0[2] doubleValue], [values0[3] doubleValue])
                              color1:FUColorMake(0, 0, 0, 0)
                              color2:FUColorMake([values2[0] doubleValue], [values2[1] doubleValue], [values2[2] doubleValue], [values2[3] doubleValue])
                              color3:FUColorMake([values3[0] doubleValue], [values3[1] doubleValue], [values3[2] doubleValue], [values3[3] doubleValue])];
        }
            break;
        case FUSubMakeupTypeEyeliner:{
            [FURenderKit shareRenderKit].makeup.eyelinerColor = color;
        }
            break;
        case FUSubMakeupTypeEyelash:{
            [FURenderKit shareRenderKit].makeup.eyelashColor = color;
        }
            break;
        case FUSubMakeupTypeHighlight:{
            [FURenderKit shareRenderKit].makeup.highlightColor = color;
        }
            break;
        case FUSubMakeupTypeShadow:{
            [FURenderKit shareRenderKit].makeup.shadowColor = color;
        }
            break;
        case FUSubMakeupTypePupil:{
            [FURenderKit shareRenderKit].makeup.pupilColor = color;
        }
            break;
    }
}

- (void)unloadSubMakeup:(NSNumber *)type {
    switch (type.integerValue) {
        case FUSubMakeupTypeFoundation:{
            [FURenderKit shareRenderKit].makeup.subFoundation = nil;
            [FURenderKit shareRenderKit].makeup.intensityFoundation = 0.0;
        }
            break;
        case FUSubMakeupTypeLip:{
            [FURenderKit shareRenderKit].makeup.subLip = nil;
            [FURenderKit shareRenderKit].makeup.intensityLip = 0.0;
        }
            break;
        case FUSubMakeupTypeBlusher:{
            [FURenderKit shareRenderKit].makeup.subBlusher = nil;
            [FURenderKit shareRenderKit].makeup.intensityBlusher = 0.0;
        }
            break;
        case FUSubMakeupTypeEyebrow:{
            [FURenderKit shareRenderKit].makeup.subEyebrow = nil;
            [FURenderKit shareRenderKit].makeup.intensityEyebrow = 0.0;
        }
            break;
        case FUSubMakeupTypeEyeShadow:{
            [FURenderKit shareRenderKit].makeup.subEyeshadow = nil;
            [FURenderKit shareRenderKit].makeup.intensityEyeshadow = 0.0;
        }
            break;
        case FUSubMakeupTypeEyeliner:{
            [FURenderKit shareRenderKit].makeup.subEyeliner = nil;
            [FURenderKit shareRenderKit].makeup.intensityEyeliner = 0.0;
        }
            break;
        case FUSubMakeupTypeEyelash:{
            [FURenderKit shareRenderKit].makeup.subEyelash = nil;
            [FURenderKit shareRenderKit].makeup.intensityEyelash = 0.0;
        }
            break;
        case FUSubMakeupTypeHighlight:{
            [FURenderKit shareRenderKit].makeup.subHighlight = nil;
            [FURenderKit shareRenderKit].makeup.intensityHighlight = 0.0;
        }
            break;
        case FUSubMakeupTypeShadow:{
            [FURenderKit shareRenderKit].makeup.subShadow = nil;
            [FURenderKit shareRenderKit].makeup.intensityShadow = 0.0;
        }
            break;
        case FUSubMakeupTypePupil:{
            [FURenderKit shareRenderKit].makeup.subPupil = nil;
            [FURenderKit shareRenderKit].makeup.intensityPupil = 0.0;
        }
            break;
    }
}

- (void)unloadCombinationMakeup {
    [FURenderKit shareRenderKit].makeup = nil;
    // 恢复美颜滤镜为原图效果
    [FURenderKit shareRenderKit].beauty.filterName = @"origin";
    [FURenderKit shareRenderKit].beauty.filterLevel = 0;
}

#pragma mark - Private methods

/// 绑定组合妆到face_makeup.bundle（老组合妆方法）
- (void)bindCombinationMakeupWithBundleName:(NSString *)bundleName {
    NSString *path = [FUUtility pluginBundlePathWithName:bundleName];
    FUItem *item = [[FUItem alloc] initWithPath:path name:bundleName];
    [[FURenderKit shareRenderKit].makeup updateMakeupPackage:item needCleanSubItem:YES];
}

- (void)updateIntensityOfCombinationMakeup:(FUCombinationMakeupModel *)model {
    if (model.isCombined) {
        // 新组合妆直接设置
        [FURenderKit shareRenderKit].makeup.intensity = model.value;
    } else {
        // 老组合妆需要设置所有子妆值，子妆实际值=组合妆值*子妆默认值
        FUMakeup *makeup = [FURenderKit shareRenderKit].makeup;
        makeup.intensityFoundation = model.foundationModel.value * model.value;
        makeup.lipType = model.lipstickModel.lipstickType;
        makeup.isTwoColor = model.lipstickModel.isTwoColorLipstick;
        makeup.intensityLip = model.lipstickModel.value * model.value;
        if (makeup.lipType == FUMakeupLipTypeMoisturizing) {
            // 润泽Ⅱ口红时需要开启口红高光，高光暂时为固定值
            makeup.isLipHighlightOn = YES;
            makeup.intensityLipHighlight = 0.8;
        } else {
            makeup.isLipHighlightOn = NO;
            makeup.intensityLipHighlight = 0;
        }
        makeup.intensityBlusher = model.blusherModel.value * model.value;
        makeup.intensityEyebrow = model.eyebrowModel.value * model.value;
        makeup.intensityEyeshadow = model.eyeShadowModel.value * model.value;
        makeup.intensityEyeliner = model.eyelinerModel.value * model.value;
        makeup.intensityEyelash = model.eyelashModel.value * model.value;
        makeup.intensityHighlight = model.highlightModel.value * model.value;
        makeup.intensityShadow = model.shadowModel.value * model.value;
        makeup.intensityPupil = model.pupilModel.value * model.value;
    }
}

/// 更新组合妆的滤镜
/// @note 老组合妆滤镜设置给FUBeauty实例，新组合妆滤镜直接设置给FUMakeup实例
- (void)updateFilterOfCombinationMakeup:(FUCombinationMakeupModel *)model {
    if (model.isCombined) {
        // 恢复美颜滤镜为原图效果
        [FURenderKit shareRenderKit].beauty.filterName = @"origin";
        // 设置美妆滤镜值
        [FURenderKit shareRenderKit].makeup.filterIntensity = model.value * model.selectedFilterLevel;
    } else {
        if (![FURenderKit shareRenderKit].beauty) {
            return;
        }
        if (!model.selectedFilter || [model.selectedFilter isEqualToString:@""]) {
            // 没有滤镜则使用默认滤镜"origin"
            [FURenderKit shareRenderKit].beauty.filterName = @"origin";
            [FURenderKit shareRenderKit].beauty.filterLevel = model.value;
        } else {
            [FURenderKit shareRenderKit].beauty.filterName = [model.selectedFilter lowercaseString];
            [FURenderKit shareRenderKit].beauty.filterLevel = model.value;
        }
    }
}

@end
