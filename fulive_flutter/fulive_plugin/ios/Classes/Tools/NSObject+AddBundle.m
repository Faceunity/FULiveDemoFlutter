//
//  NSObject+AddBundle.m
//  fulive_plugin
//
//  Created by Chen on 2021/8/25.
//

#import "NSObject+AddBundle.h"

@implementation NSObject (AddBundle)
- (NSString *)loadPathWithBundleName:(NSString *)bundleName fileName:(NSString *)fileName ofType:(NSString *)ofType {
    NSString *path = nil;
    path = [[NSBundle mainBundle] pathForResource:fileName ofType:ofType];
    if (!path) {
        NSBundle *bundle = [NSBundle bundleForClass:self.class];
        //bundle 内部的资源bundle
        NSURL *resource  = [bundle URLForResource:bundleName withExtension:@"bundle"];
        //资源路径
        path = [NSBundle pathForResource:fileName ofType:ofType inDirectory:resource.path];
    }
  
    return path;
}

/**
 * 默认以fulive_plugin为bundleName
 * fileName 文件名称
 * reuturn NSString
 */
- (NSString *)loadPathWithFileName:(NSString *)fileName ofType:(NSString *)ofType {
    return [self loadPathWithBundleName:@"fulive_plugin" fileName:fileName ofType:ofType];
}
@end
