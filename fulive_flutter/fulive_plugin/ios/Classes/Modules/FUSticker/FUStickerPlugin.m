//
//  FUStickerPlugin.m
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/11/19.
//

#import "FUStickerPlugin.h"
#import "FUFlutterPluginModelProtocol.h"
#import "FlutterBaseModel.h"
#import "FUStickerManager.h"
#import "FlutterFUBeautyPlugin.h"
#import "FUBaseViewControllerManager.h"
#import "FULocalDataManager.h"

@interface FlutterStickerModel : FlutterBaseModel <FUFlutterPluginModelProtocol>
@property (nonatomic, assign) int index;//选中的贴纸索引

@property (nonatomic, assign) NSString *bundlePath;//缓存当前index 对应的path, 只限于该plugin用，和Flutter 传参无关
@end

@implementation FlutterStickerModel
@synthesize value, method;
@end


@interface FUStickerPlugin ()
@property (nonatomic, strong) FUStickerManager *manager;
@property (nonatomic, strong) FlutterFUBeautyPlugin *beautyPlugin;
@property (nonatomic, strong) NSArray *stickerList;

//当前设置的贴纸效果
@property (nonatomic, strong) FlutterStickerModel *curModel;
@end

@implementation FUStickerPlugin
- (instancetype)init {
    self = [super init];
    if (self) {
        //需要美颜插件效果
        _beautyPlugin = [[FlutterFUBeautyPlugin alloc] init];
        _manager = [[FUStickerManager alloc] init];
        _stickerList = [FULocalDataManager stickerBundleJsonData];
    }
    return self;
}


- (void)configBiz {
    [self.beautyPlugin configBeauty];
    self.manager.type = FUStickerPropType;
}


- (void)dispose {
    if ([self.delegate respondsToSelector:@selector(disposePluginWithKey:)]) {
        [self.delegate disposePluginWithKey:NSStringFromClass([self class])];
    }
    [self stopCature];
    [self.beautyPlugin beautyClean];
    [self.manager releaseItem];
    
}


- (void)flutterWillAppear {
    [self startCapture];
}


- (void)flutterWillDisappear {
    [self stopCature];
    [self setOnCameraChange];
}


//点击贴纸
- (NSNumber *)clickItem:(NSDictionary *)params {
    __block BOOL ret = NO;
    FlutterStickerModel *flutterModel = [FlutterStickerModel analysis:params];

    NSString *bundleName = @"";
    if (flutterModel.index < _stickerList.count && flutterModel.index >= 0) {
        bundleName = [_stickerList objectAtIndex:flutterModel.index];
        flutterModel.bundlePath = bundleName;
        self.curModel = flutterModel;
    } else {
        NSLog(@"贴纸数组索引越界");
        return @(0);
    }

    dispatch_semaphore_t sem = dispatch_semaphore_create(0);
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        [self.manager loadItem:bundleName completion:^(BOOL finished) {
            ret = finished;
            dispatch_semaphore_signal(sem);
        }];
    });
    
    dispatch_semaphore_wait(sem, DISPATCH_TIME_FOREVER);
    
    return @(ret);
}
@end
