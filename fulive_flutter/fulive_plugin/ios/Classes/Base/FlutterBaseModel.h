//
//  FlutterBaseModel.h
//  fulive_plugin
//
//  Created by Chen on 2021/8/23.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
//Flutter 数据协议模型基类，属性用协议实现FUFlutterPluginModelProtocol实现
@interface FlutterBaseModel : NSObject
/**
 * json -> model
 */
+ (instancetype)analysis:(NSDictionary *)params;

/**
 * model - > jsonStr
 */
- (NSString *)jsonStr;
@end

NS_ASSUME_NONNULL_END
