//
//  FUNativeViewFactory.m
//  fulive_plugin
//
//  Created by Chen on 2021/7/23.
//

#import "FUNativeViewFactory.h"
#import "FUNativeViewPlugin.h"

@implementation FUNativeViewFactory {
    NSObject<FlutterBinaryMessenger>* _messenger;
}

- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger>*)messenger {
    self = [super init];
    if (self) {
        _messenger = messenger;
    }
    return self;
}

- (NSObject<FlutterPlatformView>*)createWithFrame:(CGRect)frame
                                   viewIdentifier:(int64_t)viewId
                                        arguments:(id _Nullable)args {
    return [[FUNativeViewPlugin alloc] initWithFrame:frame
                                      viewIdentifier:viewId
                                           arguments:args
                                     binaryMessenger:_messenger];
}
@end
