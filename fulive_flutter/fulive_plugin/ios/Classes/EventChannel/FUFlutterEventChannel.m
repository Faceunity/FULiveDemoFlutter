//
//  FUFlutterEventChannel.m
//  fulive_plugin
//
//  Created by Chen on 2021/8/4.
//

#import "FUFlutterEventChannel.h"

static NSString *eventChannel = @"FUEventChannel";

@interface FUFlutterEventChannel ()<FlutterStreamHandler>
@property (nonatomic, strong) FlutterEventChannel *channel;
@property (nonatomic, copy) FlutterEventSink eventSink;
@end

@implementation FUFlutterEventChannel

- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger>*)messenger {
    if (self) {
        _channel = [FlutterEventChannel eventChannelWithName:eventChannel binaryMessenger:messenger codec:[FlutterStandardMethodCodec sharedInstance]];
        [_channel setStreamHandler:self];
    }
    return self;
}


- (FlutterError * _Nullable)onCancelWithArguments:(id _Nullable)arguments {
    self.eventSink = nil;
    return nil;
}

- (FlutterError * _Nullable)onListenWithArguments:(id _Nullable)arguments eventSink:(nonnull FlutterEventSink)events {
    self.eventSink = events;
    return nil;
}

- (void)sendMessageEventChannel:(NSString *)message {
    if (self.eventSink) {
        self.eventSink(message);
    }
}

@end
