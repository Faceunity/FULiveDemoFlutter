//
//  FUFlutterPluginModelProtocol.h
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
//FlutterBaseModel 数据协议
@protocol FUFlutterPluginModelProtocol <NSObject>
@property (nonatomic, assign) id value; //可以是字符串，NSNumber，数组，字典等。具体类型由Flutter 开发和native 开发制定
@property (nonatomic, copy) NSString *method;

@optional
//对应具体的事件通道实例
@property (nonatomic, strong) id channel;
@end

NS_ASSUME_NONNULL_END
