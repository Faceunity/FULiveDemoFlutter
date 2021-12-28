//
//  FlutterFUBeautyPlugin.m
//  fulive_plugin
//
//  Created by Chen on 2021/7/30.
//

#import "FlutterFUBeautyPlugin.h"
#import "FUBeautyDefine.h"
#import "FUFlutterPluginModelProtocol.h"
#import "FlutterBaseModel.h"
#import "FUBaseViewControllerManager.h"

@interface FlutterBeautyModel : FlutterBaseModel <FUFlutterPluginModelProtocol>
@property (nonatomic, copy) NSNumber *bizType;

@property (nonatomic, copy) NSNumber *subBizType;
@property (nonatomic, copy) NSString *stringValue;
@end

@implementation FlutterBeautyModel

@synthesize method, value;
@end

@interface FlutterFUBeautyPlugin () {

}

@property (nonatomic, strong) FUBaseViewControllerManager *baseManager;

@end

@implementation FlutterFUBeautyPlugin

- (instancetype)init {
    self = [super init];
    if (self) {
        _baseManager = [[FUBaseViewControllerManager alloc] init];
        [self startCapture];
    }
    return self;
}

- (void)configBeauty {
    //加载loaditem
    [self.baseManager loadItem];
    /* 视频模式 */
    [self.baseManager setFaceProcessorDetectMode:1];
    [self.baseManager setMaxFaces:4];
}


//从上个页面回退到当前页面，类比viewWillAppear,不完全一样，初始化widget不走这个方法
- (void)FlutterWillAppear {
    [self.baseManager loadItem];
    [self startCapture];
}


//离开当前页面, 销毁FlutterWidget 不走此方法，可以走disposeFUBeauty来释放资源
- (void)FlutterWillDisappear {
    [self stopCature];
    [self setOnCameraChange];
}

//FULiveModulePlugin 销毁plugin,
- (void)disposeFUBeauty {
    if ([self.delegate respondsToSelector:@selector(disposePluginWithKey:)]) {
        [self.delegate disposePluginWithKey:NSStringFromClass([self class])];
    }
}

//清理资源
- (void)beautyClean {
    [self.baseManager releaseItem];
    [self.baseManager updateBeautyCache];
}

- (void)resetDefault:(NSDictionary *)params {
    FlutterBeautyModel *model = [FlutterBeautyModel analysis: params];
    int bizType = [(NSNumber *)model.bizType doubleValue];
    [self.baseManager resetDefaultParams:bizType];
}

- (void)setFilterParams:(NSDictionary *)params {
    FlutterBeautyModel *model = [FlutterBeautyModel analysis: params];
    if (model.subBizType.intValue > self.baseManager.filters.count) {
        NSLog(@"滤镜参数数组越界params：%@,",params);
    } else {
        double value = [(NSNumber *)model.value doubleValue];
        [self.baseManager setFilterkey:[model.stringValue lowercaseString]];
        self.baseManager.beauty.filterLevel = value;
        FUBeautyModel *curModel = [self.baseManager.filters objectAtIndex:model.subBizType.intValue];
        self.baseManager.seletedFliter = curModel;
        
        //同步更新native 滤镜值
        curModel.mValue = value;
        curModel.strValue = model.stringValue;
    }
}

- (void)setFUBeautyParams:(NSDictionary *)params {
    FlutterBeautyModel *model = [FlutterBeautyModel analysis: params];
    int subBizType = [(NSNumber *)model.subBizType doubleValue];
    switch (model.bizType.intValue) {
        case FUBeautyDefineSkin: {
            double value = [(NSNumber *)model.value doubleValue];
            [self.baseManager setSkin:value forKey:subBizType];
            //同步native值
            FUBeautyModel *native = [self.baseManager.skinParams objectAtIndex:subBizType];
            native.mValue = value;
        }
            
            break;
        case FUBeautyDefineShape: {
            double value = [(NSNumber *)model.value doubleValue];
            [self.baseManager setShap:[(NSNumber *)model.value doubleValue] forKey:[(NSNumber *)model.subBizType doubleValue]];
            //同步native值
            FUBeautyModel *native = [self.baseManager.shapeParams objectAtIndex:subBizType];
            native.mValue = value;
        }
            break;
        case FUBeautyDefineStyle: {
            FUStyleModel *model = [self.baseManager.styleParams objectAtIndex:subBizType];
            [self.baseManager setStyleBeautyParams:model];
        }
            break;
            
        default:
            break;
    }
}




@end
