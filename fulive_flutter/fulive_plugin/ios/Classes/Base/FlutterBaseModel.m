//
//  FlutterBaseModel.m
//  fulive_plugin
//
//  Created by Chen on 2021/8/23.
//

#import "FlutterBaseModel.h"
#import <MJExtension/MJExtension.h>

@implementation FlutterBaseModel
+ (instancetype)analysis:(NSDictionary *)params {
    FlutterBaseModel *model = [[self class] mj_objectWithKeyValues:params];
    return model;
}
@end
