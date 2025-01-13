//
//  FURenderPlugin.h
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/14.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>
#import "FURenderKitManager.h"

NS_ASSUME_NONNULL_BEGIN

@interface FURenderPlugin : NSObject

- (instancetype)initWithMethodChannel:(FlutterMethodChannel *)channel;

/// 获取特效模块鉴权码
- (NSNumber *)getModuleCode:(NSNumber *)code;

/// 设备是否高端机型
- (NSNumber *)devicePerformanceLevel;

/// 设备是否支持 NPU
- (NSNumber *)isNPUSupported;

/// 设置人脸检测模式
- (void)setFaceProcessorDetectMode:(FUFaceProcessorDetectMode)mode;

/// 设置最大人脸数量
- (void)setMaxFaceNumber:(NSNumber *)number;

- (void)setRenderState:(NSNumber *)isRendering;

@end

@interface FURenderPlugin (Camera)

- (void)startCamera;

- (void)stopCamera;

- (NSNumber *)switchCamera:(NSNumber *)isFront;

- (void)setCameraExposure:(NSNumber *)exposure;

- (void)manualFocus:(NSNumber *)dx dy:(NSNumber *)dy;

- (void)takePhoto;

- (void)startRecord;

- (NSNumber *)stopRecord;

- (NSNumber *)switchCapturePreset:(NSNumber *)preset;

- (void)switchCameraOutputFormat:(NSNumber *)format;

- (void)requestAlbumForType:(NSNumber *)type;

@end

@interface FURenderPlugin (ImageRender)

- (void)startImageRender;

- (void)stopImageRender;

- (void)captureImage;

- (void)disposeImageRender;

@end

@interface FURenderPlugin (VideoRender)

- (void)startPreviewingVideo;

- (void)stopPreviewingVideo;

- (void)startPlayingVideo;

- (void)stopPlayingVideo;

- (void)startExportingVideo;

- (void)stopExportingVideo;

- (void)disposeVideoRender;

@end

NS_ASSUME_NONNULL_END
