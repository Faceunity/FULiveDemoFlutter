//
//  FUBaseViewControllerManager.m
//  FULiveDemo
//
//  Created by Chen on 2021/2/24.
//  Copyright © 2021 FaceUnity. All rights reserved.
//

#import "FUBaseViewControllerManager.h"
#import "FUManager.h"
#import "FUBeautyDefine.h"
#import <FURenderKit/FUAIKit.h>
#import <FURenderKit/FUGLContext.h>
#import "FULocalDataManager.h"
#import "FULandmarkManager.h"

@interface FUBaseViewControllerManager () {
    BOOL _preFaceResult;
}
/* 滤镜参数 */
@property (nonatomic, strong) NSArray<FUBeautyModel *> *filters;
/* 美肤参数 */
@property (nonatomic, strong) NSArray<FUBeautyModel *> *skinParams;
/* 美型参数 */
@property (nonatomic, strong) NSArray<FUBeautyModel *> *shapeParams;
///* 风格参数 ，用父类，因为View 用的就是父类泛型，后续需要优化*/
//@property (nonatomic, strong) NSArray<FUStyleModel *> *styleParams;
@end

@implementation FUBaseViewControllerManager

- (void)updateBeautyCache {
    //更新缓存参数
    NSArray *filters = [NSArray arrayWithArray:self.filters];
    NSArray *skins = [NSArray arrayWithArray:self.skinParams];
    NSArray *shape = [NSArray arrayWithArray:self.shapeParams];
    NSArray *cache = @[skins, shape, filters];
    [FUManager shareManager].beautyParams = cache;
    [FUManager shareManager].seletedFliter = self.seletedFliter;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        //默认配置相机为前置
        [FURenderKit shareRenderKit].internalCameraSetting.position = AVCaptureDevicePositionFront;
        [self initBeauty];
        
        [self reloadBeautyParams];
        
        // 添加点位测试开关
        if ([FUManager shareManager].showsLandmarks) {
            [FULandmarkManager show];
        }
    }
    return self;
}

- (void)setFaceProcessorDetectMode:(int)mode {
    [FUAIKit shareKit].faceProcessorDetectMode = mode;
}

- (void)setOnCameraChange {
    [FUAIKit resetTrackedResult];
}

//检测是否有人脸
- (BOOL)faceTrace {
    BOOL ret = [FUAIKit shareKit].trackedFacesCount > 0;
    return ret;
}

- (void)setMaxFaces:(int)maxFaces {
    [FUAIKit shareKit].maxTrackFaces = maxFaces;
}

- (void)setMaxBodies:(int)maxBodies {
    [FUAIKit shareKit].maxTrackBodies = maxBodies;
}

- (void)setAsyncTrackFaceEnable:(BOOL)enable {
    [FUAIKit shareKit].asyncTrackFace = enable;
}


/**获取图像中人脸中心点*/
- (CGPoint)getFaceCenterInFrameSize:(CGSize)frameSize{
    CGPoint preCenter = CGPointMake(0.49, 0.5);
    // 获取人脸矩形框，坐标系原点为图像右下角，float数组为矩形框右下角及左上角两个点的x,y坐标（前两位为右下角的x,y信息，后两位为左上角的x,y信息）
    CGRect faceRect = [FUPoster cacluteRectWithIndex:0 height:frameSize.height width:frameSize.width];
    
    // 计算出中心点的坐标值
    CGFloat centerX = (faceRect.origin.x + (faceRect.origin.x + faceRect.size.width)) * 0.5;
    CGFloat centerY = (faceRect.origin.y + (faceRect.origin.y + faceRect.size.height)) * 0.5;
    
    // 将坐标系转换成以左上角为原点的坐标系
    centerX = centerX / frameSize.width;
    centerY = centerY / frameSize.height;
    
    CGPoint center = CGPointMake(centerX, centerY);
    
    preCenter = center;
    
    return center;
}

//刷新数据，比如从自定义视频或图片返回需要刷新一下
- (void)reloadBeautyParams {
    //优先获取缓存的美颜参数
    if ([FUManager shareManager].beautyParams.count == 3) {
        NSArray *list = [FUManager shareManager].beautyParams;
        self.skinParams = [list objectAtIndex:0];
        self.shapeParams =  [list objectAtIndex:1];
        self.filters =  [list objectAtIndex:2];
        self.seletedFliter = [FUManager shareManager].seletedFliter;
        if (!self.seletedFliter) {
            self.seletedFliter = self.filters[2];
        }
    } else {
        self.filters = [self getFilterData];
        self.skinParams =  [self getSkinData];
        self.shapeParams =  [self getShapData];
        self.seletedFliter = self.filters[2];
    }
    [self setBeautyParameters];
}

//初始化美颜数据
- (void)initBeauty {
    NSString *path = [[NSBundle mainBundle] pathForResource:@"face_beautification.bundle" ofType:nil];
    self.beauty = [[FUBeauty alloc] initWithPath:path name:@"FUBeauty"];
    /* 默认精细磨皮 */
    self.beauty.heavyBlur = 0;
    self.beauty.blurType = 3;
    /* 默认自定义脸型 */
    self.beauty.faceShape = 4;
    // 高性能设备设置去黑眼圈、去法令纹、大眼、嘴型最新效果
    if ([FURenderKit devicePerformanceLevel] == FUDevicePerformanceLevelHigh) {
        [self.beauty addPropertyMode:FUBeautyPropertyMode2 forKey:FUModeKeyRemovePouchStrength];
        [self.beauty addPropertyMode:FUBeautyPropertyMode2 forKey:FUModeKeyRemoveNasolabialFoldsStrength];
        [self.beauty addPropertyMode:FUBeautyPropertyMode3 forKey:FUModeKeyEyeEnlarging];
        [self.beauty addPropertyMode:FUBeautyPropertyMode3 forKey:FUModeKeyIntensityMouth];
    }

//    [self setBeautyParameters];
    
}

/**加载美颜道具到FURenderKit*/
- (void)loadItem {
    dispatch_async(self.loadQueue, ^{
        CFAbsoluteTime startTime = CFAbsoluteTimeGetCurrent();
        [FURenderKit shareRenderKit].beauty = self.beauty;
        CFAbsoluteTime endTime = (CFAbsoluteTimeGetCurrent() - startTime);
        NSLog(@"加载美颜道具耗时: %f ms", endTime * 1000.0);
    });
}

- (void)setBeautyParameters {
    for (FUBeautyModel *model in self.skinParams){
        [self setSkin:model.mValue forKey:model.mParam];
        NSLog(@"title===%@,value ==%f",model.mTitle, model.mValue);
    }
    
    for (FUBeautyModel *model in self.shapeParams){
        [self setShap:model.mValue forKey:model.mParam];
        NSLog(@"title===%@,value ==%f",model.mTitle, model.mValue);
    }
    
    /* 设置默认状态 */
    if (self.filters) {
        [self setFilterkey:[self.seletedFliter.strValue lowercaseString]];
        self.beauty.filterLevel = self.seletedFliter.mValue;
    }

}

- (void)setSkin:(double)value forKey:(FUBeautifySkin)key {
    switch (key) {
        case FUBeautifySkinBlurLevel: {
            self.beauty.blurLevel = value;
        }
            break;
        case FUBeautifySkinColorLevel: {
            self.beauty.colorLevel = value;
        }
            break;
        case FUBeautifySkinRedLevel: {
            self.beauty.redLevel = value;
        }
            break;
        case FUBeautifySkinSharpen: {
            self.beauty.sharpen = value;
        }
            break;
        case FUBeautifySkinFace: {
            self.beauty.faceThreed = value;
        }
            break;
        case FUBeautifySkinEyeBright: {
            self.beauty.eyeBright = value;
        }
            break;
        case FUBeautifySkinToothWhiten: {
            self.beauty.toothWhiten = value;
        }
            break;
        case FUBeautifySkinRemovePouchStrength: {
            self.beauty.removePouchStrength = value;
        }
            break;
        case FUBeautifySkinRemoveNasolabialFoldsStrength: {
            self.beauty.removeNasolabialFoldsStrength = value;
        }
            break;
        default:
            break;
    }
}

- (void)setShap:(double)value forKey:(FUBeautifyShape)key {
    switch (key) {
        case FUBeautifyShapeCheekThinning: {
            self.beauty.cheekThinning = value;
        }
            break;
        case FUBeautifyShapeCheekV: {
            self.beauty.cheekV = value;
        }
            break;
        case FUBeautifyShapeCheekNarrow: {
            self.beauty.cheekNarrow = value;
        }
            break;
        case FUBeautifyShapeCheekShort: {
            self.beauty.cheekShort = value;
        }
            break;
        case FUBeautifyShapeCheekSmall: {
            self.beauty.cheekSmall = value;
        }
            break;
        case FUBeautifyShapeCheekbones: {
            self.beauty.intensityCheekbones = value;
        }
            break;
        case FUBeautifyShapeLowerJaw: {
            self.beauty.intensityLowerJaw = value;
        }
            break;
        case FUBeautifyShapeEyeEnlarging: {
            self.beauty.eyeEnlarging = value;
        }
            break;
        case FUBeautifyShapeEyeCircle: {
            self.beauty.intensityEyeCircle = value;
        }
            break;
        case FUBeautifyShapeChin: {
            self.beauty.intensityChin = value;
        }
            break;
        case FUBeautifyShapeForehead: {
            self.beauty.intensityForehead = value;
        }
            break;
        case FUBeautifyShapeNose: {
            self.beauty.intensityNose = value;
        }
            break;
        case FUBeautifyShapeMouth: {
            self.beauty.intensityMouth = value;
        }
            break;
        case FUBeautifyShapeLipThick: {
            self.beauty.intensityLipThick = value;
        }
            break;
        case FUBeautifyShapeEyeHeight: {
            self.beauty.intensityEyeHeight = value;
        }
            break;
        case FUBeautifyShapeCanthus: {
            self.beauty.intensityCanthus = value;
        }
            break;
        case FUBeautifyShapeEyeLid: {
            self.beauty.intensityEyeLid = value;
        }
            break;
        case FUBeautifyShapeEyeSpace: {
            self.beauty.intensityEyeSpace = value;
        }
            break;
        case FUBeautifyShapeEyeRotate: {
            self.beauty.intensityEyeRotate = value;
        }
            break;
        case FUBeautifyShapeLongNose: {
            self.beauty.intensityLongNose = value;
        }
            break;
        case FUBeautifyShapePhiltrum: {
            self.beauty.intensityPhiltrum = value;
        }
            break;
        case FUBeautifyShapeSmile: {
            self.beauty.intensitySmile = value;
        }
            break;
        case FUBeautifyShapeBrowHeight: {
            self.beauty.intensityBrowHeight = value;
        }
            break;
        case FUBeautifyShapeBrowSpace: {
            self.beauty.intensityBrowSpace = value;
        }
            break;
        case FUBeautifyShapeBrowThick: {
            self.beauty.intensityBrowThick = value;
        }
            break;
        default:
            break;
    }
}

- (void)setFilterkey:(FUFilter)filterKey {
    self.beauty.filterName = filterKey;
}


- (void)releaseItem {
    //释放item，内部会自动清除句柄
    [FURenderKit shareRenderKit].beauty = nil;
    
    if ([FUManager shareManager].showsLandmarks) {
        [FULandmarkManager dismiss];
    }
    //demo 是单例持有 beauty 所以必须主动设置nil, 如果是每个模块自己持有beauty 则随着模块的释放系统自动释放beauty，无需再设置
//    self.beauty = nil;
}

- (BOOL)isDefaultSkinValue {
    for (FUBeautyModel *model in _skinParams){
        if (fabs(model.mValue - model.defaultValue) > 0.01 ) {
            return NO;
        }
    }
    return YES;
}

- (BOOL)isDefaultShapeValue {
    for (FUBeautyModel *model in _shapeParams){
        if (fabs(model.mValue - model.defaultValue) > 0.01 ) {
            return NO;
        }
    }
    return YES;
}


- (NSArray <FUBeautyModel *> *)getFilterData {
    NSArray *list = [FULocalDataManager beautyFilterJsonData];
    if (!list) {
        NSLog(@"美颜-滤镜数据为空");
        return nil;
    }
    
    NSMutableArray *filters = [NSMutableArray arrayWithCapacity:list.count];
    for (NSDictionary *m in list) {
        FUBeautyModel *model = [[FUBeautyModel alloc] init];
        [model setValuesForKeysWithDictionary:m];
        [filters addObject:model];
    }
    return [NSArray arrayWithArray:filters];
}

- (NSArray <FUBeautyModel *> *)getSkinData {
    NSArray *list = [FULocalDataManager beautySkinJsonData];
    if (!list) {
        NSLog(@"美颜-美肤数据为空");
        return nil;
    }
    
    NSMutableArray *skinParams = [NSMutableArray arrayWithCapacity:list.count];
    for (NSDictionary *m in list) {
        FUBeautyModel *model = [[FUBeautyModel alloc] init];
        [model setValuesForKeysWithDictionary:m];
        [skinParams addObject:model];
    }

    return [NSArray arrayWithArray:skinParams];
}

- (NSArray <FUBeautyModel *> *)getShapData {
    NSArray *list = [FULocalDataManager beautyShapeJsonData];
    if (!list) {
        NSLog(@"美颜-美型数据为空");
        return nil;
    }
    NSMutableArray *shapeParams = [NSMutableArray arrayWithCapacity:list.count];
    for (NSDictionary *m in list) {
        FUBeautyModel *model = [[FUBeautyModel alloc] init];
        [model setValuesForKeysWithDictionary:m];
        [shapeParams addObject:model];
    }
    return [NSArray arrayWithArray:shapeParams];
}


// 默认美颜参数
- (void)resetDefaultParams:(FUBeautyDefine)type {
    switch (type) {
        case FUBeautyDefineSkin: {
            for (FUBeautyModel *model in _skinParams){
                model.mValue = model.defaultValue;
                [self setSkin:model.mValue forKey:model.mParam];
            }
        }
            break;
        case FUBeautyDefineShape: {
            for (FUBeautyModel *model in _shapeParams){
                model.mValue = model.defaultValue;
                [self setShap:model.mValue forKey:model.mParam];
            }
        }
            break;
            
        default:
            break;
    }
}

@end
