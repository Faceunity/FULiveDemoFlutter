//
//  NSObject+throttle.h
//  fulive_plugin
//
//  Created by lsh726 on 2023/3/29.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSObject (throttle)
/**
 * throotle 时间间隔，单位毫秒
 */
- (void)throttleWithInterval:(NSTimeInterval)interval
                  completion:(void(^)(void))completion;
@end

NS_ASSUME_NONNULL_END
