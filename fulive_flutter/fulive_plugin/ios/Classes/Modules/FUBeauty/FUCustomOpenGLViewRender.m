//
//  FUCustomOpenGLViewRender.m
//  fulive_plugin
//
//  Created by Chen on 2021/8/13.
//

#import "FUCustomOpenGLViewRender.h"
#import <FURenderKit/FURenderKit.h>
@implementation FUCustomOpenGLViewRender {
    FUGLDisplayView *_view;
}

- (instancetype)initWithFrame:(CGRect)frame
               viewIdentifier:(int64_t)viewId
                    arguments:(id _Nullable)args
              binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger {
    if (self) {
        _view = [[FUGLDisplayView alloc] initWithFrame:frame];
        _view.contentMode = FUGLDisplayViewContentModeScaleAspectFit;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"FUCustomOpenGLViewRender" object:_view];
    }
    return self;
}

- (UIView *)view {
    return  _view;
}

@end
