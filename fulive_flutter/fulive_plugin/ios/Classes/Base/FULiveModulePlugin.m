//
//  FULiveModulePlugin.m
//  fulive_plugin
//
//  Created by Chen on 2021/7/30.
//

#import "FULiveModulePlugin.h"
#import "FlutterFUBeautyPlugin.h"
#import "FUFlutterImagePickerPlugin.h"
#import "FUCustomOpenGLViewRenderPlugin.h"
#import "FUModulePluginProtocol.h"
#import "FlutterFUMakeupPlugin.h"
#import "FlutterFUBasePlugin.h"
#import "FlutterCommonPlugin.h"
#import "FUStickerPlugin.h"

@interface FULiveModulePlugin ()<FUModulePluginProtocol>
//缓存各个模块的实例, key为类名称
@property (nonatomic, strong) NSMutableDictionary *moduleMap;

@end

@implementation FULiveModulePlugin
+ (instancetype)shareInstance {
    static FULiveModulePlugin *_manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _manager = [[FULiveModulePlugin alloc] init];
    });
    return _manager;
}


- (instancetype)init {
    self = [super init];
    if (self) {
        _moduleMap = [NSMutableDictionary dictionary];
    }
    return self;
}


- (void)disposePluginWithKey:(NSString *)key {
    if ([self.moduleMap.allKeys containsObject:key]) {
        [self.moduleMap removeObjectForKey: key];
    } else {
        NSLog(@"%@不存在",key);
    }
}


- (void)Common:(FlutterMethodCall *)call result:(FlutterResult)result {
    [self targetWithClass:[FlutterCommonPlugin class] actionWithCall:call result:result];
}

//美颜相关接口
- (void)FUBeauty:(FlutterMethodCall *)call result:(FlutterResult)result {
    [self targetWithClass:[FlutterFUBeautyPlugin class] actionWithCall:call result:result];
}

//自定义相册接口
- (void)ImagePick:(FlutterMethodCall *)call result:(FlutterResult)result {
    [self targetWithClass:[FUFlutterImagePickerPlugin class] actionWithCall:call result:result];
}


////自定义相册渲染接口
- (void)FUCustomRender:(FlutterMethodCall *)call result:(FlutterResult)result {
    [self targetWithClass:[FUCustomOpenGLViewRenderPlugin class] actionWithCall:call result:result];
}

//美妆接口
- (void)FUMakeup:(FlutterMethodCall *)call result:(FlutterResult)result {
    [self targetWithClass:[FlutterFUMakeupPlugin class] actionWithCall:call result:result];
}

//贴纸接口
- (void)Sticker:(FlutterMethodCall *)call result:(FlutterResult)result {
    [self targetWithClass:[FUStickerPlugin class] actionWithCall:call result:result];
}

- (void)targetWithClass:(Class)cls actionWithCall:(FlutterMethodCall *)call result:(FlutterResult)result {
    if (!cls) {
        NSLog(@"cls can not be nil");
        return ;
    }
    NSObject *obj;
    NSString *key = NSStringFromClass(cls);
    if ([self.moduleMap.allKeys containsObject:key]) {
        obj = self.moduleMap[key];
    } else {
        obj = [[cls alloc] init];
        [self.moduleMap setObject:obj forKey:key];
        if ([obj isKindOfClass:[FlutterFUBasePlugin class]]) {
            FlutterFUBasePlugin *plugin = (FlutterFUBasePlugin *)obj;
            plugin.delegate = self;
        } else {
            NSLog(@"plugin:%@ 未继承基类:FlutterFUBasePlugin",obj);
        }
    }
   
    if ([call.arguments isKindOfClass:[NSDictionary class]]) {
        //提取参数作为判断
        NSDictionary *param = (NSDictionary *)call.arguments;
        SEL selector;
        if ([param.allKeys containsObject:@"method"]) {
            if (param.allKeys.count > 1) {//表明还有其他参数需要在方法名称后面加 :
                selector = NSSelectorFromString([NSString stringWithFormat:@"%@:",call.arguments[@"method"]]);
            } else {
                selector = NSSelectorFromString([NSString stringWithFormat:@"%@",call.arguments[@"method"]]);
            }
            
        } else {
            //没有定义方法默认以call.method 作为方法名称
            selector = NSSelectorFromString([NSString stringWithFormat:@"%@:",call.method]);
        }
       
        if (![obj respondsToSelector:selector]) {
            NSLog(@"beauty can not respondsToSelector:%s",selector);
            result(FlutterMethodNotImplemented);
            return ;
        }
        NSMethodSignature *signature = [obj methodSignatureForSelector:selector];
        NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
        invocation.target = obj;
        invocation.selector = selector;

        if (param.count > 1) {
            [invocation setArgument:&param atIndex:2];
        }
        [invocation invoke];
        
        //返回值一定要是对象类型，基本类型 int, float ,double 等需要封装成NSNumber，id是对象，在堆里面，基本数据类型在字符常亮区，需要用一个指针指向。
        id __unsafe_unretained returnValue = nil;
        if (signature.methodReturnLength != 0) {
            [invocation getReturnValue:&returnValue];
            result(returnValue);
        }

    } else {
        NSLog(@"参数类型未定义");
    }

    result(0);
}

@end
