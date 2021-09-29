//
//  FUFlutterEventChannel.h
//  fulive_plugin
//
//  Created by Chen on 2021/8/4.
//

#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN

@interface FUFlutterEventChannel : NSObject
- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger>*)messenger;

//向Flutter 发消息
- (void)sendMessageEventChannel:(NSString *)message;
@end

NS_ASSUME_NONNULL_END
