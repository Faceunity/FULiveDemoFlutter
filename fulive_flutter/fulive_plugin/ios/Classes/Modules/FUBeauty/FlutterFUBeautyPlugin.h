//
//  FlutterFUBeautyPlugin.h
//  fulive_plugin
//
//  Created by Chen on 2021/7/30.
//

#import "FlutterFUBasePlugin.h"

@class FUBaseViewControllerManager;
NS_ASSUME_NONNULL_BEGIN
/**
 * 美颜几乎所有情况下都需要被其他模块使用
 *  所以需要开放 configBeauty 和 beautyClean 接口
 */
@interface FlutterFUBeautyPlugin : FlutterFUBasePlugin
//初始化资源
- (void)configBeauty;
//清理资源
- (void)beautyClean;

@property (nonatomic, strong, readonly) FUBaseViewControllerManager *baseManager;
@end

NS_ASSUME_NONNULL_END
