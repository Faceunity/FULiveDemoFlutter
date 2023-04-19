//
//  FUMakeUpManager.m
//  FULiveDemo
//
//  Created by Chen on 2021/3/2.
//  Copyright © 2021 FaceUnity. All rights reserved.
//

#import "FUMakeupManager.h"
#import "FULocalDataManager.h"
#import "FUMakeupModel.h"
#import "FUMakeupSupModel.h"
#import <MJExtension/NSObject+MJKeyValue.h>
#import <FURenderKit/FURenderKit.h>
#import "NSObject+AddBundle.h"

@interface FUMakeupManager ()
@property (nonatomic, strong) NSArray <FUMakeupModel *>* dataArray;

@property (nonatomic, strong) NSArray <FUMakeupSupModel *>*supArray;

@end

@implementation FUMakeupManager

- (instancetype)init {
    self = [super init];
    if (self) {
        NSDictionary *dataParms = [NSDictionary dictionaryWithDictionary:[FULocalDataManager makeupJsonData]];
        self.dataArray = [FUMakeupModel mj_objectArrayWithKeyValuesArray:dataParms[@"data"]];
        for (FUMakeupModel *model in self.dataArray) {
            for (FUSingleMakeupModel *singleM in model.sgArr) {
                //非json 数据的初始化，不修改json了，太多了
                singleM.realValue = singleM.value;
            }
        }
        if (self.dataArray.count == 0) {
            NSLog(@"%@.dataArray数据出错",self.class);
        }
        NSDictionary *supParms = [NSDictionary dictionaryWithDictionary:[FULocalDataManager makeupWholeJsonData]];
        self.supArray = [FUMakeupSupModel mj_objectArrayWithKeyValuesArray:supParms[@"data"]];
        if (self.supArray.count == 0) {
            NSLog(@"%@.supArray数据出错",self.class);
        }
    }
    return self;
}


- (void)_createOldMakeupCompletion:(void (^)(void))completion {
    NSString *path = [self loadPathWithFileName:@"face_makeup" ofType:@"bundle"];
    self.makeup = [[FUMakeup alloc] initWithPath:path name:@"makeUp"];
    self.makeup.isMakeupOn = YES;
    [self loadItemCompletion:completion];
}

//设置加载组合妆bundle
- (void)setSupModelBundleWithModel:(FUMakeupSupModel *)model
                        completion:(void(^)(void))completion {
    if ([FURenderKit shareRenderKit].makeup) {
        [FURenderKit shareRenderKit].makeup.enable = NO;
    }
    
    dispatch_async(self.loadQueue, ^{
        // @note 嗲嗲兔、冻龄、国风、混血是8.0.0新加的四个组合妆，新组合妆只需要直接加载bundle，不需要绑定到face_makeup.bundle
        if (model.newMakeupFlag) {
            NSString *path = [self loadPathWithFileName:model.makeupBundle ofType:@"bundle"];
            FUMakeup *makeup = [[FUMakeup alloc] initWithPath:path name:@"makeUp"];
            makeup.isMakeupOn = YES;
            //先卸载其他妆容在加载新版本美妆
            [self loadMakeupPackageWithPathName:nil];
            self.makeup = nil;
            [FURenderKit shareRenderKit].makeup = nil;
            // 高端机打开全脸分割
            makeup.makeupSegmentation = [FURenderKit devicePerformanceLevel] == FUDevicePerformanceLevelHigh;
            [FURenderKit shareRenderKit].makeup = makeup;
            [FURenderKit shareRenderKit].makeup.enable = YES;
            if (completion) completion();
        } else {
            //make 设置过并且当前的组合妆还是新版本组合妆，此时设置旧版组合妆需要重新初始化 face_makeup
            if (self.preSelectedIndex == -1 || !self.makeup) {
                __weak typeof (self) weak = self;
                [self _createOldMakeupCompletion:^{
                    // 高端机打开全脸分割
                    weak.makeup.makeupSegmentation = [FURenderKit devicePerformanceLevel] == FUDevicePerformanceLevelHigh;
                    [self loadMakeupPackageWithPathName:model.makeupBundle];
                    [FURenderKit shareRenderKit].makeup.enable = YES;
                    if (completion) completion();
                }];
            } else {
                [self loadMakeupPackageWithPathName:model.makeupBundle];
                [FURenderKit shareRenderKit].makeup.enable = YES;
                if (completion) completion();
            }
        }

        NSLog(@"setSupModelBundleWithModel compeletion");
    });
}

- (void)loadMakeupPackageWithPathName:(NSString *)pathName {
    NSString *path = [self loadPathWithFileName:pathName ofType:@"bundle"];
    if (path) {
        FUItem *item = [[FUItem alloc] initWithPath:path name:pathName];
        [self.makeup updateMakeupPackage:item needCleanSubItem:YES];
    } else {
        [self.makeup updateMakeupPackage:nil needCleanSubItem:YES];
    }
    NSLog(@"@@@@@loadMakeupPackageWithPathName compeletion");

}

- (void)setNewMakeupFilterIntensity:(double)intensity {
    dispatch_async(self.loadQueue, ^{
       [FURenderKit shareRenderKit].makeup.filterIntensity = intensity;
    });
}

- (void)loadItem {
    [FURenderKit shareRenderKit].makeup = self.makeup;
    NSLog(@"@@@@@loadItem compeletion");
}

- (void)loadItemCompletion:(void (^)(void))completion {
    dispatch_async(self.loadQueue, ^{
        [FURenderKit shareRenderKit].makeup = self.makeup;
        if (completion) completion();
        NSLog(@"@@@@@loadItem compeletion");
    });
}

- (void)releaseItem {
    self.makeup = nil;
    [FURenderKit shareRenderKit].makeup = nil;
    NSLog(@"@@@@@releaseItem compeletion");
}

- (void)releaseItemCompletion:(void (^)(void))completion {
    dispatch_async(self.loadQueue, ^{
        self.makeup = nil;
        [FURenderKit shareRenderKit].makeup = nil;
        if (completion) completion();
        NSLog(@"@@@@@releaseItem compeletion");
    });
}



//修改整体妆容的数据
- (void)setMakeupWholeModel:(FUMakeupSupModel *)model {
    dispatch_async(self.loadQueue, ^{
        if (model.newMakeupFlag) {
            // 恢复美颜滤镜为原图效果
            [FURenderKit shareRenderKit].beauty.filterName = @"origin";
            [FURenderKit shareRenderKit].makeup.intensity = model.value;
        } else {
            for (FUSingleMakeupModel *singleModel in model.makeups) {
                //整体妆容设置每个子妆强度时需要乘上整体妆容程度值
                singleModel.realValue = singleModel.value * model.value;
                [self setMakeupSupModel:singleModel type:UIMAKEUITYPE_intensity];
            }
        }
    });
}

////设置子妆容数据
- (void)setMakeupSupModel:(FUSingleMakeupModel *)model type:(UIMAKEUITYPE)type {
    switch (type) {
        case UIMAKEUITYPE_intensity:
            [self setIntensity:model];
            break;
        case UIMAKEUITYPE_color:
            [self setColor:model];
            break;
        case UIMAKEUITYPE_pattern:
            [self setPatter:model];
            break;
        default:
            break;
    }
}

- (void)setIntensity:(FUSingleMakeupModel *)model {
    switch (model.makeType) {
        case MAKEUPTYPE_foundation:
            self.makeup.intensityFoundation = model.realValue;
            break;
        case MAKEUPTYPE_Lip: {
            self.makeup.intensityLip = model.realValue;
            self.makeup.lipType = model.lip_type;
            self.makeup.isTwoColor = model.is_two_color == 1?YES:NO;
            if (model.lip_type == FUMakeupLipTypeMoisturizing) {
                // 润泽Ⅱ口红时需要开启口红高光，高光暂时为固定值0.8
                [FURenderKit shareRenderKit].makeup.isLipHighlightOn = YES;
                [FURenderKit shareRenderKit].makeup.intensityLipHighlight = 0.8;
            } else {
                [FURenderKit shareRenderKit].makeup.isLipHighlightOn = NO;
                [FURenderKit shareRenderKit].makeup.intensityLipHighlight = 0;
            }
            NSLog(@"@@@@@intensityLip compeletion self.makeup.intensityLip == %f",self.makeup.intensityLip);
        }
            break;
        case MAKEUPTYPE_blusher:
            self.makeup.intensityBlusher = model.realValue;
            break;
        case MAKEUPTYPE_eyeBrow: {
            self.makeup.intensityEyebrow = model.realValue;
        }
            break;
        case MAKEUPTYPE_eyeShadow:
            self.makeup.intensityEyeshadow = model.realValue;
            break;
        case MAKEUPTYPE_eyeLiner:
            self.makeup.intensityEyeliner = model.realValue;
            break;
        case MAKEUPTYPE_eyelash:
            self.makeup.intensityEyelash = model.realValue;
            break;
        case MAKEUPTYPE_highlight:
            self.makeup.intensityHighlight = model.realValue;
            break;
        case MAKEUPTYPE_shadow:
            self.makeup.intensityShadow = model.realValue;
            break;
        case MAKEUPTYPE_pupil:
            self.makeup.intensityPupil = model.realValue;
            break;
        default:
            break;
    }
}

- (void)setColor:(FUSingleMakeupModel *)model {
    if (model.defaultColorIndex >= model.colors.count) {
        return ;
    }
    NSArray *values = model.colors[model.defaultColorIndex];
    FUColor color = [self FUColorTransformWithValues:values];
    switch (model.makeType) {
        case MAKEUPTYPE_foundation:
            self.makeup.foundationColor = color;
            break;
        case MAKEUPTYPE_Lip:
            self.makeup.lipColor = color;
            break;
        case MAKEUPTYPE_blusher:
            self.makeup.blusherColor = color;
            break;
        case MAKEUPTYPE_eyeBrow:
            self.makeup.eyebrowColor = color;
            break;
        case MAKEUPTYPE_eyeShadow: {
            NSArray *values0 = [model.colors[model.defaultColorIndex] subarrayWithRange:NSMakeRange(0, 4)];
            NSArray *values2 = [model.colors[model.defaultColorIndex] subarrayWithRange:NSMakeRange(4, 4)];
            NSArray *values3 = [model.colors[model.defaultColorIndex] subarrayWithRange:NSMakeRange(8, 4)];
            [self.makeup setEyeColor:[self FUColorTransformWithValues:values0]
                              color1:FUColorMake(0, 0, 0, 0)
                              color2:[self FUColorTransformWithValues:values2]
                              color3:[self FUColorTransformWithValues:values3]];
        }
            break;
        case MAKEUPTYPE_eyeLiner:
            self.makeup.eyelinerColor = color;
            break;
        case MAKEUPTYPE_eyelash:
            self.makeup.eyelashColor = color;
            break;
        case MAKEUPTYPE_highlight:
            self.makeup.highlightColor = color;
            break;
        case MAKEUPTYPE_shadow:
            self.makeup.shadowColor = color;
            break;
        case MAKEUPTYPE_pupil:
            self.makeup.pupilColor = color;
            break;
        default:
            break;
    }
}


- (FUColor)FUColorTransformWithValues:(NSArray *)values {
    return FUColorMake([values[0] doubleValue], [values[1] doubleValue], [values[2] doubleValue], [values[3] doubleValue]);;
}

- (void)setPatter:(FUSingleMakeupModel *)model {
    if (!model.namaBundle) {
        return;
    }
    if (!self.makeup) {
        [self _createOldMakeupCompletion:nil];
        // 高端机打开全脸分割
        self.makeup.makeupSegmentation = [FURenderKit devicePerformanceLevel] == FUDevicePerformanceLevelHigh;
    }
    NSString *path = [self loadPathWithFileName:model.namaBundle ofType:@"bundle"];
    FUItem *item = [[FUItem alloc] initWithPath:path name:model.namaBundle];
    switch (model.namaBundleType) {
        case SUBMAKEUPTYPE_foundation:
            NSLog(@"#### 自定义粉底bundle %@",item.path);
            self.makeup.subFoundation = item;
            break;
        case SUBMAKEUPTYPE_blusher:
            self.makeup.subBlusher = item;
            break;
        case SUBMAKEUPTYPE_eyeBrow:
            self.makeup.subEyebrow = item;
            break;
        case SUBMAKEUPTYPE_eyeShadow:
            self.makeup.subEyeshadow = item;
            break;
        case SUBMAKEUPTYPE_eyeLiner:
            self.makeup.subEyeliner = item;
            break;
        case SUBMAKEUPTYPE_eyeLash:
            self.makeup.subEyelash = item;
            break;
        case SUBMAKEUPTYPE_highlight:
            self.makeup.subHighlight = item;
            break;
        case SUBMAKEUPTYPE_shadow:
            self.makeup.subShadow = item;
            break;
        case SUBMAKEUPTYPE_pupil:
            self.makeup.subPupil = item;
            self.makeup.blendTypePupil = 1;
            break;
        case SUBMAKEUPTYPE_lip:
            self.makeup.subLip = item;
            break;
        default:
            break;
    }
    
}


//检测组合妆是否有变化
-(BOOL)supValueHaveChangeWithIndex:(int)index {
    BOOL isValueChange = NO;
    BOOL isColorChange = NO;
    BOOL isSelChange = NO;
    if(index < 0 || index >= _supArray.count) return NO;
    FUMakeupSupModel *supModle = _supArray[index]; //可自定义组合妆
    for (FUSingleMakeupModel *modle0 in supModle.makeups) {//遍历当前可自定义的组合装子妆数据， ex:口红具体类型、眉毛具体类型
        for (FUMakeupModel *modle1 in _dataArray) { //当前配置的子妆数据模型: 如口红、眉毛、等等
            
            if(modle0.makeType != modle1.sgArr[0].makeType) {
                continue;
            }
        
            FUSingleMakeupModel *modle2 = modle1.sgArr[modle1.singleSelIndex];
            
            //非粉底对比 isValueChange || isSelChange|| isColorChange
            if(modle0.makeType !=  MAKEUPTYPE_foundation) {
                if (modle1.singleSelIndex == modle0.singleSelIndex) {
                    isColorChange  = modle0.defaultColorIndex != modle2.defaultColorIndex;
                    isValueChange = fabs(modle2.realValue - modle0.realValue) > 0.01;
                    isSelChange = modle0.singleSelIndex != modle2.singleSelIndex;
                    
                    if (isValueChange || isSelChange|| isColorChange) {
                        self.preSelectedIndex = -1;
                        return YES;
                    }
                } else {
                    self.preSelectedIndex = -1;
                    //选择的子项里面的索引对不上，说明又改动，直接返回false
                    return YES;
                }
            } else {
                //粉底只需要对比 isSelChange 和 isValueChange
                if (modle1.singleSelIndex == modle0.singleSelIndex) {
                    isValueChange = fabs(modle2.realValue - modle0.realValue) > 0.01;
                    isSelChange = modle0.singleSelIndex != modle2.singleSelIndex;
                    
                    if (isValueChange || isSelChange) {
                        self.preSelectedIndex = -1;
                        return YES;
                    }
                } else {
                    self.preSelectedIndex = -1;
                    //选择的子项里面的索引对不上，说明又改动，直接返回false
                    return YES;
                }
            }
        }
    }
    
    return NO;
}




/**
 * 可自定义组合妆对应的子妆状态处理
 * index supModel 组合妆索引
 * dataArray 当前子妆数组数据
 */
- (NSArray *)makeupTransformToSubMakeupWithIndex:(int)index {
    if(index < 0 || index >= _supArray.count) return @[];
    for (FUMakeupModel *modle in _dataArray) {
        modle.singleSelIndex = 0;
    }
    if(index == 0){//卸妆状态没有对关系
        return @[];
    }
    FUMakeupSupModel *supModle = _supArray[index];
    for (FUSingleMakeupModel *modle0 in supModle.makeups) { //modle0 自定义组合妆对象
        for (FUMakeupModel *modle1 in _dataArray) {
            if(modle0.makeType != modle1.sgArr[0].makeType){
                continue;
            }
            FUSingleMakeupModel *modle2 = modle1.sgArr[modle0.singleSelIndex];
            if(modle0.makeType !=  MAKEUPTYPE_foundation){
                modle2.singleSelIndex = modle0.singleSelIndex;
                modle1.singleSelIndex = modle0.singleSelIndex;
                /* 值 */
                modle2.defaultColorIndex = modle0.defaultColorIndex;
                
//                modle2.value = modle0.value;
                
                //UI显示的值
                modle2.realValue = modle0.value * supModle.value;
            } else {
                modle2.singleSelIndex = modle0.singleSelIndex;
                modle1.singleSelIndex = modle0.singleSelIndex;
//                modle2.value = modle0.value;
                //UI显示的值
                modle2.realValue = modle0.value * supModle.value;
            }
        }
    }
    
    return [_dataArray copy];
}


- (BOOL)array:(NSArray *)array1 isEqualTo:(NSArray *)array2 {
    int count = (int)MIN(array1.count, array2.count);
    for (int i = 0; i  < count; i ++) {
        if (fabsf([array1[i] floatValue] - [array2[i] floatValue]) > 0.01 ) {
            return NO;
        }
    }
    return YES;
}

@end
