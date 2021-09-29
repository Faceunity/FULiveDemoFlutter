#import "FULivePlugin.h"
#import "FUNativeViewFactory.h"
#import "FULiveModulePlugin.h"
#import "FUFlutterEventChannel.h"
#import "FUCustomRender.h"

//作为streamChannel_前缀标志，表明需要从naitive 获取streamChannel_实例作为参数
static NSString *STREAMCHANNEL = @"streamChannel_";
//作为methodChannel_前缀标志，表明需要从naitive 获取methodChannel_实例作为参数
static NSString *METHODCHANNEL = @"methodChannel_";
static NSString *OpenGLDisplayViewID = @"OpenGLDisplayView";
static NSString *CustomGLDisplayViewID = @"CustomGLDisplayView";

@interface FULivePlugin ()
@property (nonatomic, strong) FUFlutterEventChannel *eventChannel;
@property (nonatomic, strong) FlutterMethodChannel *methodChannel;
@property (nonatomic, strong) FUCustomRender *customRender;
@end

@implementation FULivePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"fulive_plugin"
            binaryMessenger:[registrar messenger]];
    FULivePlugin* instance = [[FULivePlugin alloc] init];
    instance.methodChannel = channel;
    
    [registrar addMethodCallDelegate:instance channel:channel];
    
    
    FUNativeViewFactory *facetory = [[FUNativeViewFactory alloc] initWithMessenger:registrar.messenger];
    [registrar registerViewFactory:facetory withId:OpenGLDisplayViewID];
    
    FUCustomRender *renderFactory = [[FUCustomRender alloc] initWithMessenger:registrar.messenger];
    instance.customRender = renderFactory;
    [registrar registerViewFactory:renderFactory withId:CustomGLDisplayViewID];
    
    
    FUFlutterEventChannel *eventChannel = [[FUFlutterEventChannel alloc] initWithMessenger: registrar.messenger];
    instance.eventChannel = eventChannel;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else if ([@"getModuleCode" isEqualToString:call.method]) {
        int module0 = [FURenderKit getModuleCode:0];
        int module1 = [FURenderKit getModuleCode:1];
        result(@[@(module0), @(module1)]);
    } else {
        
        FlutterMethodCall *customCall;
        if ([call.method hasPrefix:STREAMCHANNEL] || [call.method hasPrefix:METHODCHANNEL]) {//stream数据
            //增加channel参数
            NSMutableDictionary *params = [NSMutableDictionary dictionary];
            if ([call.arguments isKindOfClass:[NSDictionary class]]) {
                if ([call.method hasPrefix:STREAMCHANNEL]) {
                    [params setObject:self.eventChannel forKey:@"channel"];
                } else {
                    [params setObject:self.methodChannel forKey:@"channel"];
                }
                [params addEntriesFromDictionary:(NSDictionary *)call.arguments];
                customCall = [FlutterMethodCall methodCallWithMethodName:[call.method substringFromIndex:STREAMCHANNEL.length] arguments:params];
            } else {
                customCall = [FlutterMethodCall methodCallWithMethodName:[call.method substringFromIndex:STREAMCHANNEL.length] arguments:call.arguments];
            }
            
        } else {
            customCall = call;
        }
  
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"%@:result:",customCall.method]);
        
        if (![[FULiveModulePlugin shareInstance] respondsToSelector:selector]) {
            NSLog(@"FULiveModulePlugin can not respondsToSelector:%@",customCall.method);
            result(FlutterMethodNotImplemented);
            return ;
        }
        
        NSMethodSignature *signature = [[FULiveModulePlugin shareInstance] methodSignatureForSelector:selector];
        
        NSInvocation* invocation = [NSInvocation invocationWithMethodSignature:signature];
        
        invocation.target = [FULiveModulePlugin shareInstance];
        invocation.selector = selector;
        
        [invocation setArgument:&customCall atIndex:2];
        [invocation setArgument:&result atIndex:3];
        
        [invocation invoke];
    }
    
}

@end
