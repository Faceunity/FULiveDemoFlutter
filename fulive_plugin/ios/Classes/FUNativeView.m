//
//  FUNativeView.m
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/13.
//

#import "FUNativeView.h"
#import "FURenderKitManager.h"

@implementation FUNativeViewFactory {
    NSObject<FlutterBinaryMessenger> *_messenger;
}

- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger> *)messenger {
    self = [super init];
    if (self) {
        _messenger = messenger;
    }
    return self;
}

- (NSObject<FlutterPlatformView> *)createWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id _Nullable)args {
    return [[FUNativeView alloc] initWithFrame:frame viewIdentifier:viewId arguments:args binaryMessenger:_messenger];
}

- (NSObject<FlutterMessageCodec> *)createArgsCodec {
    return [FlutterStringCodec sharedInstance];
}

@end

@implementation FUNativeView {
    FUGLDisplayView *_view;
}

- (instancetype)initWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id)args binaryMessenger:(NSObject<FlutterBinaryMessenger> *          )messenger {
    self = [super init];
    if (self) {
        _view = [[FUGLDisplayView alloc] initWithFrame:frame];
        NSString *argument = (NSString *)args;
        if ([argument isEqualToString:@"camera_render"]) {
            [FURenderKitManager sharedManager].cameraRenderView = _view;
            [FURenderKit shareRenderKit].glDisplayView = _view;
        } else if ([argument isEqualToString:@"image_render"]) {
            [FURenderKitManager sharedManager].imageRenderView = _view;
            [FURenderKitManager sharedManager].imageRenderView.contentMode = FUGLDisplayViewContentModeScaleAspectFit;
        } else if ([argument isEqualToString:@"video_render"]) {
            [FURenderKitManager sharedManager].videoRenderView = _view;
            [FURenderKitManager sharedManager].videoRenderView.contentMode = FUGLDisplayViewContentModeScaleAspectFit;
        }
    }
    return self;
}

- (UIView *)view {
    return _view;
}

@end
