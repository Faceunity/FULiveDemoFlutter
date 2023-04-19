//
//  NSObject+throttle.m
//  fulive_plugin
//
//  Created by lsh726 on 2023/3/29.
//

#import "NSObject+throttle.h"
#import <objc/runtime.h>
static const char preActionKey;

@interface NSObject ()
//函数上次执行的绝对时间
@property (nonatomic, strong) NSNumber *preActionTime;
@end

@implementation NSObject (throttle)

- (void)throttleWithInterval:(NSTimeInterval)interval
                  completion:(void(^)(void))completion {
    
    CFAbsoluteTime curTime = CFAbsoluteTimeGetCurrent();
    if (curTime - self.preActionTime.doubleValue > interval) {
        if (completion) {
            completion();
        }
        self.preActionTime = @(curTime);
    } else {
        NSLog(@"throttle");
    }
}


- (void)setPreActionTime:(NSNumber *)preActionTime {
    objc_setAssociatedObject(self, &preActionKey, preActionTime, OBJC_ASSOCIATION_RETAIN);
}


- (NSNumber *)preActionTime {
    return objc_getAssociatedObject(self, &preActionKey);
}
@end
