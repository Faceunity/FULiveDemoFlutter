//
//  FUCustomRender.m
//  fulive_plugin
//
//  Created by Chen on 2021/8/12.
//

#import "FUCustomRender.h"
#import "FUCustomOpenGLViewRender.h"

@implementation FUCustomRender {
    NSObject<FlutterBinaryMessenger>* _messenger;
    FUCustomOpenGLViewRender *_platformView;
}


- (NSObject<FlutterPlatformView>*)createWithFrame:(CGRect)frame
                                   viewIdentifier:(int64_t)viewId
                                        arguments:(id _Nullable)args {
    _platformView = [[FUCustomOpenGLViewRender alloc] initWithFrame:frame viewIdentifier:viewId arguments:args binaryMessenger:_messenger];
    return _platformView;
}

- (NSObject<FlutterMessageCodec>*)createArgsCodec {
    return [FlutterStandardMessageCodec sharedInstance];
}
@end

