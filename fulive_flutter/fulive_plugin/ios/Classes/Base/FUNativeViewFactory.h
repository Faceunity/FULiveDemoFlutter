//
//  FUNativeViewFactory.h
//  fulive_plugin
//
//  Created by Chen on 2021/7/23.
//

#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN
//每个页面的基础视图工厂类
@interface FUNativeViewFactory : NSObject <FlutterPlatformViewFactory>
- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger>*)messenger;
@end

NS_ASSUME_NONNULL_END
