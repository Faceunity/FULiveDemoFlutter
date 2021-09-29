//
//  FlutterFUBeautyPlugin.h
//  fulive_plugin
//
//  Created by Chen on 2021/7/30.
//

#import "FlutterFUBasePlugin.h"

@class FUBaseViewControllerManager;
NS_ASSUME_NONNULL_BEGIN

@interface FlutterFUBeautyPlugin : FlutterFUBasePlugin
//初始化资源
- (void)configBeauty;
//清理资源
- (void)beautyClean;

@property (nonatomic, strong, readonly) FUBaseViewControllerManager *baseManager;
@end

NS_ASSUME_NONNULL_END
