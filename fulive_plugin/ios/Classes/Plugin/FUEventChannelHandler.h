//
//  FUEventChannelHandler.h
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/16.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN

@interface FUEventChannelHandler : NSObject<FlutterStreamHandler>

+ (instancetype)shared;

- (void)sendMessage:(NSDictionary *)message;

@end

NS_ASSUME_NONNULL_END
