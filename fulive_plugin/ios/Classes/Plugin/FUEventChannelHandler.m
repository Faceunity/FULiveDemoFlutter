//
//  FUEventChannelHandler.m
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/16.
//

#import "FUEventChannelHandler.h"

@interface FUEventChannelHandler ()

@property (nonatomic, strong) FlutterEventSink eventSink;

@end

@implementation FUEventChannelHandler

static FUEventChannelHandler *instance = nil;

+ (instancetype)shared {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[FUEventChannelHandler alloc] init];
    });
    return instance;
}

- (FlutterError * _Nullable)onCancelWithArguments:(id _Nullable)arguments {
    self.eventSink = nil;
    return nil;
}

- (FlutterError * _Nullable)onListenWithArguments:(id _Nullable)arguments eventSink:(nonnull FlutterEventSink)events {
    self.eventSink = events;
    return nil;
}

- (void)sendMessage:(NSDictionary *)message {
    !self.eventSink ?: self.eventSink(message);
}

@end
