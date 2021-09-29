//
//  FULiveModulePlugin.h
//  fulive_plugin
//
//  Created by Chen on 2021/7/30.
//

#import <Flutter/Flutter.h>
@class FUFlutterEventChannel;
NS_ASSUME_NONNULL_BEGIN
/**
 * 桥梁形式存在，持有业务模型并且分发方法到具体的业务数据类里面
 */
@interface FULiveModulePlugin : NSObject
+ (instancetype)shareInstance;

@end

NS_ASSUME_NONNULL_END
