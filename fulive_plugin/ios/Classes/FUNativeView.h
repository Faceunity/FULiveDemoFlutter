//
//  FUNativeView.h
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/13.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN

@interface FUNativeViewFactory : NSObject<FlutterPlatformViewFactory>

- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger> *)messenger;

@end

@interface FUNativeView : NSObject <FlutterPlatformView>

- (instancetype)initWithFrame:(CGRect)frame
               viewIdentifier:(int64_t)viewId
                    arguments:(id _Nullable)args
            binaryMessenger:(NSObject<FlutterBinaryMessenger> *)messenger;

- (UIView *)view;

@end

NS_ASSUME_NONNULL_END
