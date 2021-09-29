//
//  FlutterFUMakeupPlugin.m
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import "FlutterFUMakeupPlugin.h"
#import "FlutterFUBeautyPlugin.h"
#import "FUFlutterPluginModelProtocol.h"
#import "FlutterBaseModel.h"
#import "FUMakeupManager.h"
#import "FUMakeupSupModel.h"
#import "FUBaseViewControllerManager.h"
#import "FUMakeupModel.h"

@interface FlutterMakeupModel : FlutterBaseModel <FUFlutterPluginModelProtocol>
@property (nonatomic, assign) int index;//选中的组合装
@property (nonatomic, assign) int subTitleIndex;//选中子妆标题索引
@property (nonatomic, assign) int subIndex;//选择的子妆
@property (nonatomic, assign) int colorIndex;//选中子妆对应的颜色
@end

@implementation FlutterMakeupModel
@synthesize value, method;
@end

@interface FlutterFUMakeupPlugin ()
@property (nonatomic, strong) FlutterFUBeautyPlugin *beautyPlugin;
@property (nonatomic, strong) FUMakeupManager *makeupManager;
@end

@implementation FlutterFUMakeupPlugin
- (instancetype)init {
    self = [super init];
    if (self) {
        //需要美颜插件效果
        _beautyPlugin = [[FlutterFUBeautyPlugin alloc] init];
        _makeupManager = [[FUMakeupManager alloc] init];
    }
    return self;
}


//初始化美妆
- (void)configMakeup {
    [_beautyPlugin configBeauty];
}


//选中组合装item
- (void)itemDidSelectedWithParams:(NSDictionary *)params {
    FlutterMakeupModel *model = [FlutterMakeupModel analysis:params];
    if (model.index > self.makeupManager.supArray.count) {
        NSLog(@"美妆数据数组越界makeupManager.supArray:%@,index:%d",self.makeupManager.supArray,model.index);
    } else {
        //组合装模型
        FUMakeupSupModel *supModel = [self.makeupManager.supArray objectAtIndex:model.index];
        [self.makeupManager setSupModel:supModel];
        [self.makeupManager setMakeupWholeModel:supModel];
        [self modifyFilter:supModel];
    }
}



//Flutter 滑动组合装slider 触发方法
-(void)sliderChangeValueWithValue:(NSDictionary *)params {
    FlutterMakeupModel *model = [FlutterMakeupModel analysis:params];
    if (model.index > self.makeupManager.supArray.count) {
        NSLog(@"美妆数据数组越界makeupManager.supArray:%@,index:%d",self.makeupManager.supArray,model.index);
    } else {
        //组合装模型
        FUMakeupSupModel *supModel = [self.makeupManager.supArray objectAtIndex:model.index];
        supModel.value = [model.value doubleValue]; //Flutter侧slider滑动的数据，更新native值
        [self.makeupManager setMakeupWholeModel:supModel];
        [self modifyFilter:supModel];
    }
}

//点击子妆
- (void)didSelectedSubItem:(NSDictionary *)params {
    FlutterMakeupModel *flutterModel = [FlutterMakeupModel analysis:params];
    FUSingleMakeupModel *subModel = [self findSinleMakeupModel:flutterModel];
    //value由模型的本身值决定
    subModel.realValue = subModel.value;

    //子妆bundle(样式)
    [self.makeupManager  setMakeupSupModel:subModel type:UIMAKEUITYPE_pattern];
    
    [self.makeupManager setMakeupSupModel:subModel type:UIMAKEUITYPE_intensity];
    //子妆颜色
    [self.makeupManager setMakeupSupModel:subModel type:UIMAKEUITYPE_color];
    
}

//点击子妆颜色
- (void)didSelectedColorItem:(NSDictionary *)params {
    FlutterMakeupModel *flutterModel = [FlutterMakeupModel analysis:params];
    FUSingleMakeupModel *subModel = [self findSinleMakeupModel:flutterModel];
    if (subModel) {
        //子妆颜色
        [self.makeupManager setMakeupSupModel:subModel type:UIMAKEUITYPE_color];
    }
}


//Flutter 滑动某个子妆的slider
- (void)subMakupSliderChangeValueWithValue:(NSDictionary *)params {
    FlutterMakeupModel *flutterModel = [FlutterMakeupModel analysis:params];
    FUSingleMakeupModel *subModel = [self findSinleMakeupModel:flutterModel];
    //value由滑动条值决定
    subModel.value = [flutterModel.value doubleValue];
    if (subModel) {
        //设置强度值
        subModel.realValue = subModel.value;
        [self.makeupManager setMakeupSupModel:subModel type:UIMAKEUITYPE_intensity];
    }
}

//美妆业务释放
- (void)disposeMakeup {
    if ([self.delegate respondsToSelector:@selector(disposePluginWithKey:)]) {
        [self.delegate disposePluginWithKey:NSStringFromClass([self class])];
    }
    
    //清理美颜资源
    [_beautyPlugin beautyClean];
    
    [self clean];
}

//清理美妆资源
- (void)clean {
    [self.makeupManager releaseItem];
}

- (void)modifyFilter:(FUMakeupSupModel *)model {
    /* 修改美颜的滤镜 */
    if (!model.selectedFilter || [model.selectedFilter isEqualToString:@""]) {
        FUBeautyModel *param = self.beautyPlugin.baseManager.seletedFliter;
        self.beautyPlugin.baseManager.beauty.filterName = [param.strValue lowercaseString];
        self.beautyPlugin.baseManager.beauty.filterLevel = param.mValue;
    }else {
        self.beautyPlugin.baseManager.beauty.filterName = [model.selectedFilter lowercaseString];
        self.beautyPlugin.baseManager.beauty.filterLevel = model.value;
    }
}


//抽象出点击子妆、点击子妆颜色和滑动自装的slider共性代码函数
- (FUSingleMakeupModel *)findSinleMakeupModel:(FlutterMakeupModel *)flutterModel {
    if (flutterModel.subTitleIndex < self.makeupManager.dataArray.count) {
        FUMakeupModel *model = [self.makeupManager.dataArray objectAtIndex:flutterModel.subTitleIndex];
        if (flutterModel.subIndex < model.sgArr.count) {
            FUSingleMakeupModel *subModel = model.sgArr[flutterModel.subIndex];
            return subModel;
        } else {
            NSLog(@"美妆数据数组越界model.sgArr:%@,subIndex:%d", model.sgArr,flutterModel.subIndex);
        }
    } else {
        NSLog(@"美妆数据数组越界self.makeupManager.dataArray:%@,subTitleIndex:%d",self.makeupManager.dataArray,flutterModel.subTitleIndex);
    }
    return nil;
}

@end
