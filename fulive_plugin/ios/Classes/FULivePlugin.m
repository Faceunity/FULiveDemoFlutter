#import "FULivePlugin.h"
#import "FUNativeView.h"
#import "FURenderPlugin.h"
#import "FUBeautyPlugin.h"
#import "FUMakeupPlugin.h"
#import "FUStickerPlugin.h"
#import "FURenderKitManager.h"
#import "FUEventChannelHandler.h"
#import "FULiveDefine.h"
#import <FURenderKit/FURenderKit.h>

@interface FULivePlugin ()

// 基础相机渲染
@property (nonatomic, strong) FURenderPlugin *renderPlugin;
@property (nonatomic, strong) FUBeautyPlugin *beautyPlugin;
@property (nonatomic, strong) FUMakeupPlugin *makeupPlugin;
@property (nonatomic, strong) FUStickerPlugin *stickerPlugin;

@property (nonatomic, strong) FlutterMethodChannel *channel;

@end

@implementation FULivePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    // 创建方法通道
    FlutterMethodChannel* channel = [FlutterMethodChannel methodChannelWithName:@"fulive_plugin" binaryMessenger:[registrar messenger]];
    FULivePlugin* instance = [[FULivePlugin alloc] init];
    instance.channel = channel;
    // 注册方法通道回调
    [registrar addMethodCallDelegate:instance channel:channel];
    
    // FUGLDisplayView
    FUNativeViewFactory* factory = [[FUNativeViewFactory alloc] initWithMessenger:registrar.messenger];
    [registrar registerViewFactory:factory withId:@"faceunity_display_view"];
    
    // EventChannel
    FlutterEventChannel *eventChannel = [FlutterEventChannel eventChannelWithName:@"render_event_channel" binaryMessenger:registrar.messenger];
    [eventChannel setStreamHandler:[FUEventChannelHandler shared]];
}

/// 回调方法
- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSObject *target;
    NSArray *arguments;
    SEL selector;
    if ([call.arguments isKindOfClass:[NSDictionary class]]) {
            // 渲染模式
        if (call.arguments[@"module"]) {
            // 特效模块接口
            target = [self targetForModule:[call.arguments[@"module"] integerValue]];
        } else {
            // 其他接口
            target = self.renderPlugin;
        }
        if (call.arguments[@"arguments"]) {
           // 需要设置传参
           NSArray *argumentArray = call.arguments[@"arguments"];
           NSString *selectorString = [NSString stringWithFormat:@"%@:", call.method];
           if (argumentArray.count > 1) {
               // 超过一个参数
               for (NSInteger i = 1; i < argumentArray.count; i++) {
                   NSDictionary *argument = argumentArray[i];
                   selectorString = [selectorString stringByAppendingString:[NSString stringWithFormat:@"%@:", argument.allKeys.firstObject]];
               }
           }
           selector = NSSelectorFromString(selectorString);
       } else {
           selector = NSSelectorFromString(call.method);
       }
       arguments = call.arguments[@"arguments"] ? call.arguments[@"arguments"] : nil;
    } else {
       target = self.renderPlugin;
       selector = NSSelectorFromString(call.method);
    }
    [self invokeWithSelector:selector target:target arguments:arguments result:result];
    
}

- (void)invokeWithSelector:(SEL)selector target:(NSObject *)target arguments:(nullable NSArray *)arguments result:(FlutterResult)result  {
    if (![target respondsToSelector:selector]) {
        NSLog(@"%@ can not respondsToSelector:%@", target, NSStringFromSelector(selector));
        result(FlutterMethodNotImplemented);
    }
    NSMethodSignature *signature = [target methodSignatureForSelector:selector];
    NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
    invocation.target = target;
    invocation.selector = selector;
    // 设置 argument
    if (arguments.count > 0) {
        for (NSInteger i = 0; i < arguments.count; i++) {
            NSDictionary *argument = arguments[i];
            id key = argument.allValues.firstObject;
            [invocation setArgument:&key atIndex:2+i];
        }
    }
    [invocation invoke];
    
    //返回值一定要是对象类型，基本类型 int, float ,double 等需要封装成NSNumber，id是对象，在堆里面，基本数据类型在字符常量区，需要用一个指针指向。
    id __unsafe_unretained returnValue = nil;
    if (signature.methodReturnLength != 0) {
        [invocation getReturnValue:&returnValue];
        result(returnValue);
    }
}

- (NSObject *)targetForModule:(FUModule)moduleType {
    NSObject *target;
    switch (moduleType) {
       case FUModuleBeauty:{
           target = self.beautyPlugin;
       }
           break;
        case FUModuleMakeup:{
            target = self.makeupPlugin;
        }
            break;
        case FUModuleSticker:{
            target = self.stickerPlugin;
        }
            break;
       default:
           break;
    }
    return target;
}

- (FURenderPlugin *)renderPlugin {
    if (!_renderPlugin) {
        _renderPlugin = [[FURenderPlugin alloc] initWithMethodChannel:self.channel];
    }
    return _renderPlugin;
}

- (FUBeautyPlugin *)beautyPlugin {
    if (!_beautyPlugin) {
        _beautyPlugin = [[FUBeautyPlugin alloc] init];
    }
    return _beautyPlugin;
}

- (FUMakeupPlugin *)makeupPlugin {
    if (!_makeupPlugin) {
        _makeupPlugin = [[FUMakeupPlugin alloc] init];
    }
    return _makeupPlugin;
}

- (FUStickerPlugin *)stickerPlugin {
    if (!_stickerPlugin) {
        _stickerPlugin = [[FUStickerPlugin alloc] init];
    }
    return _stickerPlugin;
}

@end
