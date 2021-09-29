//
//  FUNativeViewPlugin.h
//  fulive_plugin
//
//  Created by Chen on 2021/7/23.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN
/**
 * 作为openGLView 和 Flutter 通讯桥梁
 */
@interface FUNativeViewPlugin : NSObject <FlutterPlatformView>
- (instancetype)initWithFrame:(CGRect)frame
               viewIdentifier:(int64_t)viewId
                    arguments:(id _Nullable)args
              binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger;

- (UIView *)view;
@end

NS_ASSUME_NONNULL_END
