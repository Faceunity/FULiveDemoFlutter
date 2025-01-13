//
//  FURenderPlugin.m
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/14.
//

#import "FURenderPlugin.h"
#import "FUEventChannelHandler.h"
#import "FULiveDefine.h"
#import "FUUtility.h"
#import "UIImage+FU.h"
#import "FUNativeView.h"

#import <Photos/Photos.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import <FURenderKit/FUVideoProcessor.h>

#define kFUFinalPath [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject stringByAppendingPathComponent:@"final_video.mp4"]

@interface FURenderPlugin ()<FURenderKitDelegate, FUCaptureCameraDataSource, FUVideoReaderDelegate,  UINavigationControllerDelegate, UIImagePickerControllerDelegate>

@property (nonatomic, strong) FlutterMethodChannel *methodChannel;

@property (nonatomic, assign) BOOL rendering;

@property (nonatomic, strong) UIImage *selectedImage;

@property (nonatomic, strong) NSURL *selectedVideoURL;

@property (nonatomic, strong) CADisplayLink *displayLink;

@property (nonatomic, strong) NSOperationQueue *renderOperationQueue;
/// 播放时只需要 Reader
@property (nonatomic, strong) FUVideoReader *videoReader;
/// 导出时需要 Processor 边读边写
@property (nonatomic, strong) FUVideoProcessor *videoProcessor;

@property (nonatomic, assign) FUGLDisplayViewOrientation videoOrientation;

@property (nonatomic, strong) AVPlayer *audioReader;

@property (nonatomic, copy) void (^captureImageHandler)(UIImage *image);

@property (nonatomic, assign) CGFloat inputBufferWidth;
@property (nonatomic, assign) CGFloat inputBufferHeight;

@end

@implementation FURenderPlugin {
    // 计算帧率相关变量
    CFAbsoluteTime startTime, lastCalculateTime;
    int rate;
    NSTimeInterval currentCalculateTime;

    // 缓存的视频预览帧
    CVPixelBufferRef previewBuffer;
}

- (instancetype)initWithMethodChannel:(FlutterMethodChannel *)channel {
    self = [super init];
    if (self) {
        _methodChannel = channel;
        self.rendering = YES;
        [FURenderKit shareRenderKit].delegate = self;
        [FURenderKit shareRenderKit].captureCamera.dataSource = self;
    }
    return self;
}

- (void)dealloc {
    if (previewBuffer != NULL) {
        CVPixelBufferRelease(previewBuffer);
        previewBuffer = NULL;
    }
}

- (NSNumber *)devicePerformanceLevel {
    return [NSNumber numberWithInt:(int)[FURenderKitManager sharedManager].devicePerformanceLevel];
}

- (NSNumber *)isNPUSupported {
    return [NSNumber numberWithBool:[UIDevice currentDevice].fu_deviceModelType >= FUDeviceModelTypeiPhoneXR];
}

- (NSNumber *)getModuleCode:(NSNumber *)code {
    return [NSNumber numberWithInt:[FURenderKit getModuleCode:[code intValue]]];
}

- (void)setFaceProcessorDetectMode:(FUFaceProcessorDetectMode)mode {
    [FUAIKit shareKit].faceProcessorDetectMode = mode;
}

- (void)setMaxFaceNumber:(NSNumber *)number {
    [FUAIKit shareKit].maxTrackFaces = number.intValue;
}

#pragma mark - Camera

- (void)startCamera {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self setFaceProcessorDetectMode:FUFaceProcessorDetectModeVideo];
        [FURenderKit shareRenderKit].internalCameraSetting.needsAudioTrack = YES;
        [[FURenderKit shareRenderKit] startInternalCamera];
        if ([FURenderKitManager sharedManager].cameraRenderView) {
            [FURenderKit shareRenderKit].glDisplayView = [FURenderKitManager sharedManager].cameraRenderView;
        }
    });
}

- (void)stopCamera {
    [[FURenderKit shareRenderKit] stopInternalCamera];
    [FURenderKit shareRenderKit].glDisplayView = nil;
}

- (void)setRenderState:(NSNumber *)isRendering {
    self.rendering = isRendering.boolValue;
}

- (NSNumber *)switchCamera:(NSNumber *)isFront {
    if (![FURenderKit shareRenderKit].captureCamera) {
        return @(NO);
    }
    FUCaptureCamera *camera = [FURenderKit shareRenderKit].captureCamera;
    if (![camera supportsAVCaptureSessionPreset:isFront.boolValue]) {
        // 硬件不支持分辨率
        return @(NO);
    } else {
        [FURenderKit shareRenderKit].internalCameraSetting.position = isFront.boolValue ? AVCaptureDevicePositionFront : AVCaptureDevicePositionBack;
        return @(YES);
    }
}

- (void)setCameraExposure:(NSNumber *)exposure {
    if (![FURenderKit shareRenderKit].captureCamera) {
        return;
    }
    // 0-1转换为实际值
    double value = exposure.doubleValue;
    value = value * 4 - 2;
    [[FURenderKit shareRenderKit].captureCamera setExposureValue:value];
}

- (void)manualFocus:(NSNumber *)dx dy:(NSNumber *)dy {
    if (![FURenderKit shareRenderKit].captureCamera) {
        return;
    }
    CGPoint center = CGPointMake(dx.floatValue, dy.floatValue);
    // 根据renderView的填充模式计算图像中心点
    CGPoint pictureCenter;
    CGFloat scale = self.inputBufferHeight / self.inputBufferWidth;
    FUGLDisplayView *renderView = [FURenderKit shareRenderKit].glDisplayView;
    CGFloat renderViewWidth = CGRectGetWidth(renderView.bounds);
    CGFloat renderViewHeight = CGRectGetHeight(renderView.bounds);
    AVCaptureDevicePosition cameraPosition = [FURenderKit shareRenderKit].captureCamera.isFrontCamera ? AVCaptureDevicePositionFront : AVCaptureDevicePositionBack;
    if (renderView.contentMode == FUGLDisplayViewContentModeScaleAspectFill) {
        // 短边填满(宽度按比例截取中间部分)
        CGFloat leading = (renderViewHeight / scale - renderViewWidth) / 2.0;
        CGFloat pictureWidth = renderViewWidth + leading * 2;
        center.x += leading;
        if (center.y <= 0) {
            return;
        }
        pictureCenter = CGPointMake(center.y / renderViewHeight, cameraPosition == AVCaptureDevicePositionFront ? center.x / pictureWidth : 1 - center.x / pictureWidth);
    } else if (renderView.contentMode == FUGLDisplayViewContentModeScaleAspectFit) {
        // 长边填满(高度上下会留空白)
        CGFloat top = (renderViewHeight - renderViewWidth * scale) / 2.0;
        CGFloat pictureHeight = renderViewHeight - top * 2;
        center.y -= top;
        if (center.y <= 0) {
            return;
        }
        pictureCenter = CGPointMake(center.y / pictureHeight, cameraPosition == AVCaptureDevicePositionFront ? center.x / renderViewWidth : 1 - center.x / renderViewWidth);
    } else {
        // 拉伸填满
        pictureCenter = CGPointMake(center.y / renderViewHeight, cameraPosition == AVCaptureDevicePositionFront ? center.x / renderViewWidth : 1 - center.x / renderViewWidth);
    }
    [[FURenderKit shareRenderKit].captureCamera focusWithMode:AVCaptureFocusModeAutoFocus exposeWithMode:AVCaptureExposureModeAutoExpose atDevicePoint:pictureCenter monitorSubjectAreaChange:YES];
}

- (void)takePhoto {
    UIImage *image = [FURenderKit captureImage];
    [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
        [PHAssetChangeRequest creationRequestForAssetFromImage:image];
    } completionHandler:^(BOOL success, NSError * _Nullable error) {
        [self.methodChannel invokeMethod:@"takePhotoResult" arguments:@(success)];
    }];
}

- (void)startRecord {
    NSString *videoPath = [NSTemporaryDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.mp4", FUCurrentDateString()]];
    [FURenderKit startRecordVideoWithFilePath:videoPath];
}

- (NSNumber *)stopRecord {
    __block BOOL result = NO;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [FURenderKit stopRecordVideoComplention:^(NSString * _Nonnull filePath) {
        if (!filePath) {
            NSLog(@"Error: 视频不存在!");
            result = NO;
        }
        [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
            [PHAssetChangeRequest creationRequestForAssetFromVideoAtFileURL:[NSURL fileURLWithPath:filePath]];
        } completionHandler:^(BOOL success, NSError * _Nullable error) {
            NSLog(@"保存视频结果：%@", @(success));
            result = success;
            dispatch_semaphore_signal(semaphore);
        }];
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return [NSNumber numberWithBool:result];
}

- (NSNumber *)switchCapturePreset:(NSNumber *)preset {
    if (![FURenderKit shareRenderKit].captureCamera) {
        return [NSNumber numberWithBool:NO];
    }
    FUCaptureCamera *camera = [FURenderKit shareRenderKit].captureCamera;
    AVCaptureSessionPreset capturePreset = AVCaptureSessionPreset1280x720;
    switch (preset.integerValue) {
        case 0:
            capturePreset = AVCaptureSessionPreset640x480;
            break;
        case 1:
            capturePreset = AVCaptureSessionPreset1280x720;
            break;
        case 2:
            capturePreset = AVCaptureSessionPreset1920x1080;
            break;
        default:
            break;
    }
    if ([camera changeSessionPreset:capturePreset]) {
        [FURenderKit shareRenderKit].internalCameraSetting.sessionPreset = capturePreset;
        return [NSNumber numberWithBool:YES];
    } else {
        NSLog(@"Error: 硬件不支持该分辨率!");
        return [NSNumber numberWithBool:NO];
    }
}

- (void)switchCameraOutputFormat:(NSNumber *)format {
    [FURenderKit shareRenderKit].internalCameraSetting.format = format.intValue == 0 ? kCVPixelFormatType_32BGRA : kCVPixelFormatType_420YpCbCr8BiPlanarFullRange;
}

- (void)resetCameraFocusAndExposureMode {
    if (![FURenderKit shareRenderKit].captureCamera) {
        return;
    }
    [[FURenderKit shareRenderKit].captureCamera resetFocusAndExposureModes];
}

- (void)requestAlbumForType:(NSNumber *)type {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self selectWithMediaType:type.integerValue == 0 ? (NSString *)kUTTypeImage : (NSString *)kUTTypeMovie];
    });
}

#pragma mark - Image render

- (void)startImageRender {
    [self setFaceProcessorDetectMode:FUFaceProcessorDetectModeImage];
    [FURenderKitManager resetTrackedResult];
    if (!_displayLink) {
        self.displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(displayLinkAction)];
        [self.displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
        self.displayLink.preferredFramesPerSecond = 10;
    }
    self.displayLink.paused = NO;
}

- (void)stopImageRender {
    self.displayLink.paused = YES;
    [self.displayLink invalidate];
    self.displayLink = nil;
    [self.renderOperationQueue cancelAllOperations];
}

- (void)captureImage {
    __weak typeof(self) weakSelf = self;
    self.captureImageHandler = ^(UIImage *image) {
        __strong typeof(self) strongSelf = weakSelf;
        [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
            [PHAssetChangeRequest creationRequestForAssetFromImage:image];
        } completionHandler:^(BOOL success, NSError * _Nullable error) {
            [strongSelf.methodChannel invokeMethod:@"captureImageResult" arguments:@(success)];
        }];
    };
}

- (void)disposeImageRender {
    self.displayLink.paused = YES;
    [self.displayLink invalidate];
    _displayLink = nil;
    [self.renderOperationQueue cancelAllOperations];
    
    [FURenderKitManager sharedManager].imageRenderView = nil;
}

#pragma mark - Video render

- (void)startPreviewingVideo {
    [self setFaceProcessorDetectMode:FUFaceProcessorDetectModeVideo];
    if (previewBuffer == NULL) {
        // 获取视频首帧
        UIImage *previewImage = [FUUtility previewImageFromVideoURL:self.selectedVideoURL preferredTrackTransform:NO];
        previewBuffer = [FUImageHelper pixelBufferFromImage:previewImage];
    }
    if (!_displayLink) {
        self.displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(videoDisplayLinkAction)];
        [self.displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
        self.displayLink.preferredFramesPerSecond = 10;
    }
    self.displayLink.paused = NO;
}

- (void)stopPreviewingVideo {
    self.displayLink.paused = YES;
    [self.displayLink invalidate];
    self.displayLink = nil;
    [self.renderOperationQueue cancelAllOperations];
    if (previewBuffer != NULL) {
        CVPixelBufferRelease(previewBuffer);
        previewBuffer = NULL;
    }
}

- (void)startPlayingVideo {
    // 设置方向视频方向
    [FURenderKitManager sharedManager].videoRenderView.orientation = (FUGLDisplayViewOrientation)self.videoReader.videoOrientation;
    [self stopPreviewingVideo];
    // 视频解码
    [self.videoReader start];
    // 播放音频
    [self.audioReader play];
}

- (void)stopPlayingVideo {
    if (_videoReader) {
        [self.videoReader stop];
        _videoReader = nil;
    }
    if (_audioReader) {
        [self.audioReader pause];
        _audioReader = nil;
    }
}

- (void)startExportingVideo {
    self.videoProcessor = [[FUVideoProcessor alloc] initWithReadingURL:self.selectedVideoURL  writingURL:[NSURL fileURLWithPath:kFUFinalPath]];
    @FUWeakify(self)
    self.videoProcessor.processingVideoBufferHandler = ^CVPixelBufferRef _Nonnull(CVPixelBufferRef  _Nonnull videoPixelBuffer, CGFloat time) {
        @FUStrongify(self)
        videoPixelBuffer = [self processVideoPixelBuffer:videoPixelBuffer];
        if (time >= 0 && self.videoProcessor.reader.duration > 0) {
            // 计算进度
            CGFloat progress = time / self.videoProcessor.reader.duration;
            // 回调进度
            [[FUEventChannelHandler shared] sendMessage:@{@"videoExportingProgress" : [NSNumber numberWithDouble:progress]}];
        }
        return videoPixelBuffer;
    };

    self.videoProcessor.processingFinishedHandler = ^{
        // 完成解码和编码
        dispatch_async(dispatch_get_main_queue(), ^{
            // 回调进度 1.0
            [[FUEventChannelHandler shared] sendMessage:@{@"videoExportingProgress" : @(1.0)}];
            [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
                [PHAssetChangeRequest creationRequestForAssetFromVideoAtFileURL:[NSURL fileURLWithPath:kFUFinalPath]];
            } completionHandler:^(BOOL success, NSError * _Nullable error) {
                NSLog(@"导出视频结果：%@", @(success));
                // 回调导出到相册的结果
                [[FUEventChannelHandler shared] sendMessage:@{@"videoExportingResult" : [NSNumber numberWithBool:success]}];
            }];
        });
    };

    [self.videoProcessor startProcessing];
}

- (void)stopExportingVideo {
    if (_videoProcessor) {
        [self.videoProcessor cancelProcessing];
        _videoProcessor = nil;
    }
}

- (void)disposeVideoRender {
    [self stopPlayingVideo];
    [self stopPreviewingVideo];
    [self stopExportingVideo];
    if (previewBuffer != NULL) {
        CVPixelBufferRelease(previewBuffer);
        previewBuffer = NULL;
    }
    [FURenderKitManager sharedManager].videoRenderView = nil;
}

#pragma mark - Private methods

- (void)selectWithMediaType:(NSString *)type {
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
    picker.allowsEditing = NO;
    picker.mediaTypes = @[type];
    [[FUUtility topViewController] presentViewController:picker animated:YES completion:nil];
}

- (void)processOutputResult:(CVPixelBufferRef)pixelBuffer {
    if (!pixelBuffer) {
        return;
    }
    if ([FURenderKitManager sharedManager].imageRenderView) {
        [[FURenderKitManager sharedManager].imageRenderView displayPixelBuffer:pixelBuffer];
    }
    
    if (self.captureImageHandler) {
        // 保存图片
        UIImage *captureImage = [FUImageHelper imageFromPixelBuffer:pixelBuffer];
        self.captureImageHandler(captureImage);
        self.captureImageHandler = nil;
    }
}

- (void)renderVideoPixelBuffer:(CVPixelBufferRef)videoPixelBuffer {
    [FURenderKitManager updateBeautyBlurEffect];
    @autoreleasepool {
        if (self.rendering) {
            FURenderInput *input = [[FURenderInput alloc] init];
            input.pixelBuffer = videoPixelBuffer;
            input.renderConfig.imageOrientation = FUImageOrientationUP;
            switch (self.videoReader.videoOrientation) {
                case FUVideoOrientationPortrait:
                    input.renderConfig.imageOrientation = FUImageOrientationUP;
                    break;
                case FUVideoOrientationLandscapeRight:
                    input.renderConfig.imageOrientation = FUImageOrientationLeft;
                    break;
                case FUVideoOrientationUpsideDown:
                    input.renderConfig.imageOrientation = FUImageOrientationDown;
                    break;
                case FUVideoOrientationLandscapeLeft:
                    input.renderConfig.imageOrientation = FUImageOrientationRight;
                    break;
            }
            FURenderOutput *output =  [[FURenderKit shareRenderKit] renderWithInput:input];
            videoPixelBuffer = output.pixelBuffer;
        }
        if ([FURenderKitManager sharedManager].videoRenderView) {
            if ([FURenderKitManager sharedManager].videoRenderView.orientation != self.videoOrientation) {
                [FURenderKitManager sharedManager].videoRenderView.orientation = self.videoOrientation;
            }
            [[FURenderKitManager sharedManager].videoRenderView displayPixelBuffer:videoPixelBuffer];
        }
        BOOL detectingResult = [FURenderKitManager faceTracked];
        [[FUEventChannelHandler shared] sendMessage:@{@"faceTracked" : @(detectingResult)}];
    }
}

- (CVPixelBufferRef)processVideoPixelBuffer:(CVPixelBufferRef)videoPixelBuffer {
    @autoreleasepool {
        FURenderInput *input = [[FURenderInput alloc] init];
        input.pixelBuffer = videoPixelBuffer;
        input.renderConfig.imageOrientation = FUImageOrientationUP;
        switch (self.videoProcessor.reader.videoOrientation) {
            case FUVideoOrientationPortrait:
                input.renderConfig.imageOrientation = FUImageOrientationUP;
                break;
            case FUVideoOrientationLandscapeRight:
                input.renderConfig.imageOrientation = FUImageOrientationLeft;
                break;
            case FUVideoOrientationUpsideDown:
                input.renderConfig.imageOrientation = FUImageOrientationDown;
                break;
            case FUVideoOrientationLandscapeLeft:
                input.renderConfig.imageOrientation = FUImageOrientationRight;
                break;
        }
        FURenderOutput *output = [[FURenderKit shareRenderKit] renderWithInput:input];
        videoPixelBuffer = output.pixelBuffer;
    }
    return videoPixelBuffer;
}

#pragma mark - Event response

- (void)displayLinkAction {
    [self.renderOperationQueue addOperationWithBlock:^{
        [FURenderKitManager updateBeautyBlurEffect];
        @autoreleasepool {
            CVPixelBufferRef buffer = [FUImageHelper pixelBufferFromImage:self.selectedImage];
            if (self.rendering) {
                FURenderInput *input = [[FURenderInput alloc] init];
                switch (self.selectedImage.imageOrientation) {
                    case UIImageOrientationUp:
                    case UIImageOrientationUpMirrored:
                        input.renderConfig.imageOrientation = FUImageOrientationUP;
                        break;
                    case UIImageOrientationLeft:
                    case UIImageOrientationLeftMirrored:
                        input.renderConfig.imageOrientation = FUImageOrientationRight;
                        break;
                    case UIImageOrientationDown:
                    case UIImageOrientationDownMirrored:
                        input.renderConfig.imageOrientation = FUImageOrientationDown;
                        break;
                    case UIImageOrientationRight:
                    case UIImageOrientationRightMirrored:
                        input.renderConfig.imageOrientation = FUImageOrientationLeft;
                        break;
                }
                input.pixelBuffer = buffer;
                FURenderOutput *output = [[FURenderKit shareRenderKit] renderWithInput:input];
                [self processOutputResult:output.pixelBuffer];
            } else {
                // 原图
                [self processOutputResult:buffer];
            }
            CVPixelBufferRelease(buffer);
        }
        BOOL detectingResult = [FURenderKitManager faceTracked];
        [[FUEventChannelHandler shared] sendMessage:@{@"faceTracked" : @(detectingResult)}];
    }];
}

- (void)videoDisplayLinkAction {
    [self.renderOperationQueue addOperationWithBlock:^{
        CVPixelBufferRetain(self->previewBuffer);
        [self renderVideoPixelBuffer:self->previewBuffer];
        CVPixelBufferRelease(self->previewBuffer);
    }];
}

#pragma mark - FURenderKitDelegate

- (BOOL)renderKitShouldDoRender {
    return self.rendering;
}

- (void)renderKitWillRenderFromRenderInput:(FURenderInput *)renderInput {
    if (renderInput.pixelBuffer != NULL) {
        CVPixelBufferLockBaseAddress(renderInput.pixelBuffer, 0);
        self.inputBufferWidth = CVPixelBufferGetWidth(renderInput.pixelBuffer);
        self.inputBufferHeight = CVPixelBufferGetHeight(renderInput.pixelBuffer);
        CVPixelBufferUnlockBaseAddress(renderInput.pixelBuffer, 0);
    }
    [FURenderKitManager updateBeautyBlurEffect];
    startTime = CFAbsoluteTimeGetCurrent();
}

- (void)renderKitDidRenderToOutput:(FURenderOutput *)renderOutput {
    CFAbsoluteTime endTime = CFAbsoluteTimeGetCurrent();
    // 加一帧占用时间
    currentCalculateTime += (endTime - startTime);
    // 加帧数
    rate += 1;
    if (endTime - lastCalculateTime >= 1) {
        // 一秒钟计算一次
        int width = (int)CVPixelBufferGetWidth(renderOutput.pixelBuffer);
        int height = (int)CVPixelBufferGetHeight(renderOutput.pixelBuffer);
        NSString *debugString = [NSString stringWithFormat:@"resolution:\n%dx%d\nfps: %d\nrender time:\n%.0fms", width, height, rate, currentCalculateTime * 1000 / rate];
        [[FUEventChannelHandler shared] sendMessage:@{@"debugInfo" : debugString}];
        // 恢复计算数据
        lastCalculateTime = endTime;
        currentCalculateTime = 0;
        rate = 0;
    }
    
    BOOL detectingResult = [FURenderKitManager faceTracked];
    [[FUEventChannelHandler shared] sendMessage:@{@"faceTracked" : @(detectingResult)}];
}

#pragma mark - UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<UIImagePickerControllerInfoKey,id> *)info {
    dispatch_async(dispatch_get_main_queue(), ^{
        [picker dismissViewControllerAnimated:YES completion:nil];
        NSString *mediaType = info[UIImagePickerControllerMediaType];
        if ([mediaType isEqualToString:(NSString *)kUTTypeImage]) {
            UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
            // 图片处理
            image = [image fu_processedImage];
            self.selectedImage = image;
            [self.methodChannel invokeMethod:@"photoSelected" arguments:@YES];
        } else {
            // 获取视频地址
            [FUUtility requestVideoURLFromInfo:info resultHandler:^(NSURL * _Nonnull videoURL) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    if (videoURL) {
                        self.selectedVideoURL = videoURL;
                        self.videoOrientation = [FUUtility videoOrientationFromVideoURL:self.selectedVideoURL];
                            [self.methodChannel invokeMethod:@"videoSelected" arguments:@YES];
                    } else {
                        [self.methodChannel invokeMethod:@"videoSelected" arguments:@NO];
                    }
                });
            }];
        }
    });
}

#pragma mark - FUVideoReaderDelegate

- (void)videoReaderDidOutputVideoSampleBuffer:(CMSampleBufferRef)videoSampleBuffer {
    CVPixelBufferRef videoBuffer = CMSampleBufferGetImageBuffer(videoSampleBuffer);
    [self renderVideoPixelBuffer:videoBuffer];
    CMSampleBufferInvalidate(videoSampleBuffer);
    CFRelease(videoSampleBuffer);
}

- (void)videoReaderDidFinishReading {
    [self stopPlayingVideo];
    // 获取最后视频帧，需要循环render预览
    UIImage *lastImage = [FUUtility lastFrameImageFromVideoURL:self.selectedVideoURL preferredTrackTransform:NO];
    previewBuffer = [FUImageHelper pixelBufferFromImage:lastImage];
    // 发送播放完成信息
    [[FUEventChannelHandler shared] sendMessage:@{@"videoPlayingFinished" : @(YES)}];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self startPreviewingVideo];
    });
}

#pragma mark - Getters

- (NSOperationQueue *)renderOperationQueue {
    if (!_renderOperationQueue) {
        _renderOperationQueue = [[NSOperationQueue alloc] init];
        _renderOperationQueue.maxConcurrentOperationCount = 1;
    }
    return _renderOperationQueue;
}

- (FUVideoReader *)videoReader {
    if (!_videoReader) {
        FUVideoReaderSettings *settings = [[FUVideoReaderSettings alloc] init];
        settings.readingAutomatically = YES;
        settings.videoOutputFormat = kCVPixelFormatType_32BGRA;
        _videoReader = [[FUVideoReader alloc] initWithURL:self.selectedVideoURL settings:settings];
        _videoReader.delegate = self;
    }
    return _videoReader;
}

- (AVPlayer *)audioReader {
    if (!_audioReader) {
        _audioReader = [AVPlayer playerWithURL:self.selectedVideoURL];
        _audioReader.actionAtItemEnd = AVPlayerActionAtItemEndNone;
    }
    return _audioReader;
}

@end
