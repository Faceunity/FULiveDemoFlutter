//
//  NSObject+AddBundle.h
//  fulive_plugin
//
//  Created by Chen on 2021/8/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSObject (AddBundle)
/**
 * bundleName 指的是存放资源的bundle名称，具体可在插件：fulive_plugin/ios/fulive_plugin.podspec 里面的s.resource_bundles查看key名称
 * fileName 文件名称
 * ofType文件类型 json,   bundle
 * reuturn NSString
 */
- (NSString *)loadPathWithBundleName:(NSString *)bundleName fileName:(NSString *)fileName ofType:(NSString *)ofType;

/**
 * 默认以fulive_plugin为bundleName
 * fileName 文件名称
 * ofType文件类型 json,   bundle
 * reuturn NSString
 */
- (NSString *)loadPathWithFileName:(NSString *)fileName ofType:(NSString *)ofType;
@end

NS_ASSUME_NONNULL_END
