//
//  FlutterCommonPlugin.h
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import "FlutterFUBasePlugin.h"

#import "FUFlutterEventChannel.h"

NS_ASSUME_NONNULL_BEGIN
//通用插件，目前用于主页面传流式数据
@interface FlutterCommonPlugin : FlutterFUBasePlugin
@property (nonatomic, strong) FUFlutterEventChannel *eventChannel;
@end

NS_ASSUME_NONNULL_END
