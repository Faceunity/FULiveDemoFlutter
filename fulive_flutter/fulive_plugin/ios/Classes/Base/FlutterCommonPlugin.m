//
//  FlutterCommonPlugin.m
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import "FlutterCommonPlugin.h"
#import "FUFlutterEventChannel.h"
#import "FUFlutterPluginModelProtocol.h"
#import <FURenderKit/FURenderKit.h>
#import <FURenderKit/FUAIKit.h>
#import "NSObject+economizer.h"
#import "SVProgressHUD.h"
#import "FlutterBaseModel.h"
@interface FlutterCommonModel : FlutterBaseModel <FUFlutterPluginModelProtocol>

@end

@implementation FlutterCommonModel
@synthesize value, method, channel;
@end

@interface FlutterCommonPlugin () <FURenderKitDelegate> {
    size_t _imageW;
    size_t _imageH;
}
//是否检测到人脸
@property (nonatomic, assign) BOOL hasFace;

@property (nonatomic, copy) NSString *debugStr;
@property (nonatomic, assign) BOOL compare;


@end

@implementation FlutterCommonPlugin

- (instancetype)init {
    self = [super init];
    if (self) {
        [SVProgressHUD setMinimumDismissTimeInterval:1.5];
        [FURenderKit shareRenderKit].delegate = self;
    }
    return self;
}


//目的获取channel
- (void)startBeautyStreamListen:(NSDictionary *)params {
    FlutterCommonModel *model = [FlutterCommonModel analysis: params];
    if (!self.eventChannel) {
        self.eventChannel = model.channel;
    }
}


//FULiveModulePlugin 销毁plugin
- (void)disposeCommon {
    if ([self.delegate respondsToSelector:@selector(disposePluginWithKey:)]) {
        [self.delegate disposePluginWithKey:NSStringFromClass([self class])];
    }
}


static int rate = 0;
static NSTimeInterval totalRenderTime = 0;
static NSTimeInterval oldTime = 0;
static NSTimeInterval startTime = 0;
// 使用内部相机时，即将处理图像时输入回调
- (void)renderKitWillRenderFromRenderInput:(FURenderInput *)renderInput {
    _imageW = CVPixelBufferGetWidth(renderInput.pixelBuffer);
    _imageH = CVPixelBufferGetHeight(renderInput.pixelBuffer);
    startTime = [[NSDate date] timeIntervalSince1970];
}

// 使用内部相机时，处理图像后的输出回调
- (void)renderKitDidRenderToOutput:(FURenderOutput *)renderOutput {
    NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970];
    totalRenderTime += endTime - startTime;
    rate ++;
    CVPixelBufferRef pixelBuffer = renderOutput.pixelBuffer;
    [self updateVideoParametersText:endTime bufferRef:pixelBuffer];
    self.hasFace = [FUAIKit shareKit].trackedFacesCount > 0;
   
    if (self.debugStr && self.eventChannel) {
        NSDictionary *par = @{@"debug":self.debugStr, @"hasFace":@(self.hasFace)};
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:par options:NSJSONWritingPrettyPrinted error:nil];
        NSString *jsonStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        [self.eventChannel sendMessageEventChannel: jsonStr];
    }
}

// 使用内部相机时，内部是否进行render处理，返回NO，将直接输出原图。
- (BOOL)renderKitShouldDoRender {
    return !self.compare;
}


//Flutter 长按事件
- (void)renderOrigin:(NSDictionary *)params {
    FlutterCommonModel *model = [FlutterCommonModel analysis: params];
    if ([model.value isKindOfClass:[NSNumber class]]) {
        self.compare = [model.value boolValue];
    } else {
        NSLog(@"对比按钮参数类型错误: %@",params);
    }
}


//Flutter 聚光
- (void)adjustSpotlight:(NSDictionary *)params {
    FlutterCommonModel *model = [FlutterCommonModel analysis: params];
    if ([model.value isKindOfClass:[NSNumber class]]) {
        double value = [model.value doubleValue];
        [[FURenderKit shareRenderKit].captureCamera setExposureValue:value];
    } else {
        NSLog(@"聚光调节参数类型错误: %@",params);
    }
   
}


//选择分别率
- (NSNumber *)chooseSessionPreset:(NSDictionary *)params {
    /**切换摄像头要调用此函数*/
    [FUAIKit resetTrackedResult];
    
    FlutterCommonModel *model = [FlutterCommonModel analysis: params];
    if ([model.value isKindOfClass:[NSNumber class]]) {
        FUCaptureCamera *camera = [FURenderKit shareRenderKit].captureCamera;
        BOOL ret = NO;
        NSInteger index = [model.value integerValue];
        switch (index) {
            case 0:
                ret = [camera changeSessionPreset:AVCaptureSessionPreset640x480];
                break;
            case 1:
                ret = [camera changeSessionPreset:AVCaptureSessionPreset1280x720];
                break;
            case 2:
                ret = [camera changeSessionPreset:AVCaptureSessionPreset1920x1080];
                break;
            default:
                break;
        }
        return ret ==YES?@1:@0;
    }
    NSLog(@"设置分辨率参数不正确: %@",params);
    return @-1;
}

#pragma  mark -  刷新bugly text
// 更新视频参数栏
-(void)updateVideoParametersText:(NSTimeInterval)startTime bufferRef:(CVPixelBufferRef)pixelBuffer{
    if (startTime - oldTime >= 1) {//一秒钟计算平均值
        oldTime = startTime;
        int diaplayRate = rate;
        NSTimeInterval diaplayRenderTime = totalRenderTime;
        
        float w = CVPixelBufferGetWidth(pixelBuffer);
        float h = CVPixelBufferGetHeight(pixelBuffer);
        NSString *ratioStr = [NSString stringWithFormat:@"%.0fX%.0f", w, h];
        dispatch_async(dispatch_get_main_queue(), ^{
            //传给Flutter 显示
            NSString *buglyStr = [NSString stringWithFormat:@" resolution:\n  %@\n fps: %d \n render time:\n  %.0fms",ratioStr,diaplayRate,diaplayRenderTime * 1000.0 / diaplayRate];
            self.debugStr = buglyStr;
            
        });
        totalRenderTime = 0;
        rate = 0;
    }
}

//Flutter 手动曝光点
- (void)manualExpose:(NSDictionary *)params {
    FlutterCommonModel *model = [FlutterCommonModel analysis: params];
    FUGLDisplayView *renderView = [FURenderKit shareRenderKit].glDisplayView;
    if ([model.value isKindOfClass:[NSArray class]]) {

        NSArray *points = (NSArray *)model.value;
        double x = 0,y = 0;
        if (points.count > 1) {
            x = [points[0] doubleValue];
            y = [points[1] doubleValue];
        }
        CGPoint center = CGPointMake(x, y);
        

        if (renderView.contentMode == FUGLDisplayViewContentModeScaleAspectFill) {
                float scal2 = _imageH/_imageW;
                float apaceLead = (renderView.bounds.size.height / scal2 - renderView.bounds.size.width )/2;
                float imagecW = renderView.bounds.size.width + 2 * apaceLead;
                center.x = center.x + apaceLead;
            
            if (center.y > 0) {
                CGPoint point = CGPointMake(center.y/renderView.bounds.size.height, [FURenderKit shareRenderKit].captureCamera.isFrontCamera ? center.x/imagecW : 1 - center.x/imagecW);
                [[FURenderKit shareRenderKit].captureCamera focusWithMode:AVCaptureFocusModeContinuousAutoFocus exposeWithMode:AVCaptureExposureModeContinuousAutoExposure atDevicePoint:point monitorSubjectAreaChange:YES];
                    NSLog(@"手动曝光点-----%@",NSStringFromCGPoint(point));
            }
        }else if(renderView.contentMode == FUGLDisplayViewContentModeScaleAspectFit){
            float scal2 = _imageH/_imageW;
            float apaceTOP = (renderView.bounds.size.height - renderView.bounds.size.width * scal2)/2;
            float imagecH = renderView.bounds.size.height - 2 * apaceTOP;
            center.y = center.y - apaceTOP;
        
            if (center.y > 0) {
                CGPoint point = CGPointMake(center.y/imagecH, [FURenderKit shareRenderKit].captureCamera.isFrontCamera ? center.x/renderView.bounds.size.width : 1 - center.x/renderView.bounds.size.width);
                [[FURenderKit shareRenderKit].captureCamera focusWithMode:AVCaptureFocusModeContinuousAutoFocus exposeWithMode:AVCaptureExposureModeContinuousAutoExposure atDevicePoint:point monitorSubjectAreaChange:YES];
                NSLog(@"手动曝光点-----%@",NSStringFromCGPoint(point));
            }
        }else{
            CGPoint point = CGPointMake(center.y/renderView.bounds.size.height, [FURenderKit shareRenderKit].captureCamera.isFrontCamera ? center.x/renderView.bounds.size.width : 1 - center.x/renderView.bounds.size.width);
            [[FURenderKit shareRenderKit].captureCamera focusWithMode:AVCaptureFocusModeContinuousAutoFocus exposeWithMode:AVCaptureExposureModeContinuousAutoExposure atDevicePoint:point monitorSubjectAreaChange:YES];
            NSLog(@"手动曝光点-----%@",NSStringFromCGPoint(point));
        }
    } else {
        NSLog(@"手动曝光点参数错误:%@",params);
    }
    
}

- (void)changeCameraFront:(NSDictionary *)params {
    FlutterCommonModel *model = [FlutterCommonModel analysis: params];
    
    [[FURenderKit shareRenderKit].captureCamera changeCameraInputDeviceisFront: [model.value boolValue]];
    /**切换摄像头要调用此函数*/
    [FUAIKit resetTrackedResult];
}


- (void)changeCameraFormat {
    int foramt = [FURenderKit shareRenderKit].internalCameraSetting.format == kCVPixelFormatType_32BGRA ? kCVPixelFormatType_420YpCbCr8BiPlanarFullRange:kCVPixelFormatType_32BGRA;
    [FURenderKit shareRenderKit].internalCameraSetting.format = foramt;
}

- (void)takePhoto {
    [self controlEventWithInterval:0.1 queue:dispatch_get_main_queue() controlEventBlock:^{
        UIImage *image = [FURenderKit captureImage];
        if (image) {
            [self takePhotoToSave:image];
        }
    }];
}

-(void)takePhotoToSave:(UIImage *)image {
    UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
}

- (void)startRecord {
    NSDate *currentDate = [NSDate date];//获取当前时间，日期
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"YYYYMMddhhmmssSS"];
    NSString *dateString = [dateFormatter stringFromDate:currentDate];
    NSString *videoPath = [NSTemporaryDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.mp4",dateString]];
    [FURenderKit startRecordVideoWithFilePath:videoPath];
}

- (void)stopRecord {
    __weak typeof(self)weakSelf  = self ;
    [FURenderKit stopRecordVideoComplention:^(NSString * _Nonnull filePath) {
        dispatch_async(dispatch_get_main_queue(), ^{
            UISaveVideoAtPathToSavedPhotosAlbum(filePath, weakSelf, @selector(video:didFinishSavingWithError:contextInfo:), NULL);
        });
    }];
}

- (void)video:(NSString *)videoPath didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo{
    if(error != NULL){
        [SVProgressHUD showErrorWithStatus:@"保存视频失败"];
        
    }else{
        [SVProgressHUD showSuccessWithStatus:@"视频已保存到相册"];
    }
}

- (void)image: (UIImage *) image didFinishSavingWithError: (NSError *) error contextInfo: (void *) contextInfo{
    if(error != NULL){
        [SVProgressHUD showErrorWithStatus:@"保存图片失败"];
    }else{
        [SVProgressHUD showSuccessWithStatus:@"图片已保存到相册"];
    }
}


@end
