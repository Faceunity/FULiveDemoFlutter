//
//  FUNativeViewPlugin.m
//  fulive_plugin
//
//  Created by Chen on 2021/7/23.
//

#import "FUNativeViewPlugin.h"
#import <FURenderKit/FURenderKit.h>

@implementation FUNativeViewPlugin {
    FUGLDisplayView *_view;
}
 
- (instancetype)initWithFrame:(CGRect)frame
               viewIdentifier:(int64_t)viewId
                    arguments:(id _Nullable)args
              binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger {
    self = [super init];
    if (self) {
        _view = [[FUGLDisplayView alloc] initWithFrame:frame];
        [FURenderKit shareRenderKit].glDisplayView = _view;
//        [[FURenderKit shareRenderKit] startInternalCamera];
    }
    return self;
}

- (UIView *)view {
    return _view;
}


- (void)dealloc {
    [FURenderKit shareRenderKit].glDisplayView = nil;
//    [[FURenderKit shareRenderKit] stopInternalCamera];
}
@end
