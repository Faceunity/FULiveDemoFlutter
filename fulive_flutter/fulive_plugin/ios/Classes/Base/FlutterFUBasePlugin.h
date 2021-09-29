//
//  FLutterFUBasePlugin.h
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import <Foundation/Foundation.h>
#import "FUModulePluginProtocol.h"

NS_ASSUME_NONNULL_BEGIN

//plugin 抽象基类
@interface FlutterFUBasePlugin : NSObject <FUModulePluginProtocol>
@property (nonatomic, weak) id<FUModulePluginProtocol>delegate;
@end

NS_ASSUME_NONNULL_END
