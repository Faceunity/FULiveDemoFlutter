//
//  FLutterFUBasePlugin.h
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import <Foundation/Foundation.h>
#import "FUModulePluginProtocol.h"

NS_ASSUME_NONNULL_BEGIN

//plugin 抽象基类
@interface FlutterFUBasePlugin : NSObject <FUModulePluginProtocol>
@property (nonatomic, weak) id<FUModulePluginProtocol>delegate;

/**
 * 抽象接口开始
 */
- (void)configBiz;
- (void)dispose;
- (void)flutterWillAppear;
- (void)flutterWillDisappear;


/**
 * 非抽象接口开始
 */
- (void)startCapture;

- (void)stopCature;

//离开页面清理资源
- (void)disposeRenderKit;
/* 清一下信息，防止快速切换有人脸信息缓存 */
- (void)setOnCameraChange;
@end

NS_ASSUME_NONNULL_END
