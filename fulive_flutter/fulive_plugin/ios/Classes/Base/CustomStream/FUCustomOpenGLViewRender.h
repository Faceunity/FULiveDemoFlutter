//
//  FUCustomOpenGLViewRender.h
//  fulive_plugin
//
//  Created by Chen on 2021/8/13.
//

#import <Foundation/Foundation.h>

#import <Flutter/Flutter.h>
NS_ASSUME_NONNULL_BEGIN


@interface FUCustomOpenGLViewRender : NSObject <FlutterPlatformView>
- (instancetype)initWithFrame:(CGRect)frame
               viewIdentifier:(int64_t)viewId
                    arguments:(id _Nullable)args
              binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger;

- (UIView *)view;
@end

NS_ASSUME_NONNULL_END
