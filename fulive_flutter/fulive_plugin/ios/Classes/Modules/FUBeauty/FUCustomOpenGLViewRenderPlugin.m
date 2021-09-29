//
//  FUCustomOpenGLViewRenderPlugin.m
//  fulive_plugin
//
//  Created by Chen on 2021/8/13.
//

#import "FUCustomOpenGLViewRenderPlugin.h"
#import <FURenderKit/FURenderKit.h>
#import "FUFlutterPluginModelProtocol.h"
#import "FUVideoReader.h"
#import <FURenderKit/FUAIKit.h>

#import "FUFlutterEventChannel.h"
#import "FUImageHelper.h"
#import <SVProgressHUD/SVProgressHUD.h>
@interface FUCustomOpenGLViewRenderPluginModel : NSObject <FUFlutterPluginModelProtocol>

@end

@implementation FUCustomOpenGLViewRenderPluginModel

@synthesize method, value, channel;

@end

@interface FUCustomOpenGLViewRenderPlugin ()<FUVideoReaderDelegate>
@property (strong, nonatomic) FUGLDisplayView *view;
@property (strong, nonatomic) UIImage *image;
@property (strong, nonatomic) NSURL *videoPath;
@property (assign, nonatomic) int type; //0 图片， 1 视频
@property (nonatomic, assign) BOOL compare; //对比按钮标识

//视频音频播放
@property (nonatomic, strong) AVPlayer *avPlayer;
//视频解码
@property (nonatomic, strong) FUVideoReader *videoReader;

//视频存存储路径
@property (nonatomic, strong) NSString *desPath;

@property (nonatomic, strong) FUFlutterEventChannel *eventChannel;

@property (nonatomic, strong) FlutterMethodChannel *methodChannel;

@property (nonatomic, assign) BOOL takePic;
@end

@implementation FUCustomOpenGLViewRenderPlugin {
    // 定时器
    CADisplayLink *_displayLink;

    
    dispatch_queue_t _renderQueue;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _renderQueue = dispatch_queue_create("com.faceUMakeup", DISPATCH_QUEUE_SERIAL);
        
        [self addObserver];
        _compare = NO;
        _desPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject stringByAppendingPathComponent:@"finalVideo.mp4"];
        
    }
    return self;
}

// 使用内部相机时，内部是否进行render处理，返回NO，将直接输出原图。
- (BOOL)renderKitShouldDoRender {
    return !self.compare;
}


//Flutter 长按事件
- (void)customRenderOrigin:(NSDictionary *)params {
    FUCustomOpenGLViewRenderPluginModel *model = [FUCustomOpenGLViewRenderPlugin analysis: params];
    if ([model.value isKindOfClass:[NSNumber class]]) {
        self.compare = [model.value boolValue];
    } else {
        NSLog(@"对比按钮参数类型错误: %@",params);
    }
}

//目的获取channel
- (void)startCustomRenderStremListen:(NSDictionary *)params {
    FUCustomOpenGLViewRenderPluginModel *model = [FUCustomOpenGLViewRenderPlugin analysis: params];
    self.eventChannel = model.channel;
}



//视频或者图片选择完成
- (void)selectedImageOrVideo:(NSDictionary *)params {
    FUCustomOpenGLViewRenderPluginModel *model = [FUCustomOpenGLViewRenderPlugin  analysis: params];
    if ([model.value isKindOfClass:[NSNumber class]]) {
        _type = [model.value intValue];
        
        NSString *key = [NSString stringWithFormat:@"type_%d",_type];
        if (_type == 0) {
            //图片解码
            NSData *data = [[NSUserDefaults standardUserDefaults] objectForKey:key];
            UIImage *sourceImage =  [UIImage imageWithData:data];
            //目的压缩成RGBA8(A 32-bit-per-pixel, fixed-point pixel format in which the red, green, and blue color components precede the alpha value.)位颜色深度的格式，目前底层库只支持处理RGBA8
            NSData *imageData0 = UIImageJPEGRepresentation(sourceImage, 1.0);
            UIImage *newImage = [UIImage imageWithData:imageData0];
            _image = newImage;
        } else {
            //视频解码
            NSData *urlsData = [[NSUserDefaults standardUserDefaults] objectForKey:key];
            NSArray *list = [NSKeyedUnarchiver unarchiveObjectWithData:urlsData];
            if (list.count > 0) {
                _videoPath = list[0];
            }
        }
    } else {
        NSLog(@"自定义图片参数类型错误: %@", params);
    }

   
}

//Flutter 插件调用顺序先于 FUCustomOpenGLViewRender，通过通知来获取FUCustomOpenGLViewRenderd 的View实例
- (void)linkCustom:(NSNotification *)noti {
//    self.view =
    if ([noti.object isKindOfClass:[FUGLDisplayView class]]) {
        self.view = noti.object;
    } else {
        NSLog(@"参数不正确: %@",noti.object);
    }
    
    if (_type == 0) {
        [self processImage];
    } else {
        [self processVideo];
    }
}


///Flutter 视频重新播放
- (void)customVideoRePlay {
    [self startAudio];
    [self startVideo];
}


//下载视频或者图片事件  0 图片，1视频
- (void)downLoadCustomRender:(NSDictionary *)params {
    FUCustomOpenGLViewRenderPluginModel *model = [FUCustomOpenGLViewRenderPlugin  analysis: params];
    if ([model.value isKindOfClass:[NSNumber class]]) {
        if ([model.value intValue] == 0) {
            self.takePic = YES;
        } else {
            UISaveVideoAtPathToSavedPhotosAlbum(self.desPath, self, @selector(video:didFinishSavingWithError:contextInfo:), NULL);
            dispatch_async(dispatch_get_main_queue(), ^{
                
            });
        }
    }
}

- (void)addObserver{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(linkCustom:) name:@"FUCustomOpenGLViewRender" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(willResignActive) name:UIApplicationWillResignActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
}

- (void)willResignActive {
    if (self.image) {
        _displayLink.paused = YES ;
    }else {
        [self.videoReader stopReading];
        self.videoReader = nil;
        [_avPlayer pause];
        _avPlayer = nil;
    }
}


- (void)didBecomeActive {
    if (self.image) {
        [self processImage];
    } else {
        //前后台Flutter UI 自己处理监听处理，native 不发消息了
        [self startAudio];
        [self startVideo];
    }
}



- (void)processImage {
    
    if (!_displayLink) {
        _displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(displayLinkAction)];
        [_displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
        [_displayLink setFrameInterval:10];
        _displayLink.paused = NO;
    }
    if (_displayLink.paused) {
        _displayLink.paused = NO ;
    }
}

- (void)displayLinkAction {

    dispatch_async(_renderQueue, ^{
        
        @autoreleasepool {
            UIImage *newImage = nil;
            FUImageBuffer imageBuffer = [self.image getImageBuffer];
            FURenderInput *input = [[FURenderInput alloc] init];
            if (!self.compare) { //对比按钮
                input.renderConfig.imageOrientation = 0;
                switch (self.image.imageOrientation) {
                    case UIImageOrientationUp:
                        input.renderConfig.imageOrientation = FUImageOrientationUP;
                        break;
                    case UIImageOrientationLeft:
                        input.renderConfig.imageOrientation = FUImageOrientationRight;
                        break;
                    case UIImageOrientationDown:
                        input.renderConfig.imageOrientation = FUImageOrientationDown;
                        break;
                    case UIImageOrientationRight:
                        input.renderConfig.imageOrientation = FUImageOrientationLeft;
                        break;
                    default:
                        input.renderConfig.imageOrientation = FUImageOrientationUP;
                        break;
                }
                input.imageBuffer = imageBuffer;
                FURenderOutput *outPut =  [[FURenderKit shareRenderKit] renderWithInput:input];
                if (self.takePic) {
                    self.takePic = NO;
                    imageBuffer = outPut.imageBuffer;
                    newImage = [UIImage imageWithRGBAImageBuffer:&imageBuffer autoFreeBuffer:NO];
                    dispatch_async(dispatch_get_global_queue(0, 0), ^{
                        UIImageWriteToSavedPhotosAlbum(newImage, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
                    });
                }
            }

            [(FUGLDisplayView *)self.view displayImageData:imageBuffer.buffer0 withSize:imageBuffer.size];
            
            [UIImage freeImageBuffer:&imageBuffer];
            
            if (self.eventChannel) {
                BOOL hasFace = [FUAIKit shareKit].trackedFacesCount > 0;
                [self.eventChannel sendMessageEventChannel:hasFace==YES?@"1":@"0"];
            }
        }
        
    });
}


- (void)processVideo {
    if (self.videoPath) {
        if (self.videoReader) {
            [self.videoReader stopReading];
            self.videoReader = nil;
        }
        self.videoReader = [[FUVideoReader alloc] initWithVideoURL:self.videoPath];
        self.videoReader.delegate = self;
        self.view.origintation = (int)self.videoReader.videoOrientation;
        
        [self startAudio];
        [self startVideo];
    }
}

- (void)startAudio {
    /* 音频的播放 */
    if (_avPlayer) {
        [_avPlayer pause];
        _avPlayer = nil ;
    }
    _avPlayer = [[AVPlayer alloc] init];
    AVPlayerItem *item = [AVPlayerItem playerItemWithURL:self.videoPath];
    [_avPlayer replaceCurrentItemWithPlayerItem:item];
    _avPlayer.actionAtItemEnd = AVPlayerActionAtItemEndNone;
    [_avPlayer play];
    
    //通知Flutter 显示 重播，下载等UI
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.methodChannel) {
            [self.methodChannel invokeMethod:@"videoPlay" arguments:@{@"isPlay": @YES}];
        } else {
            NSLog(@"无methodChannel通道");
        }
    });
}

- (void)startVideo {
    if (self.videoReader) {
        [self.videoReader setVideoURL:self.videoPath];
    }else {
        self.videoReader = [[FUVideoReader alloc] initWithVideoURL:self.videoPath];
        self.videoReader.delegate = self ;
    }
    [self.videoReader startReadWithDestinationPath:self.desPath];
    
    self.view.origintation = (int)self.videoReader.videoOrientation;
}



#pragma mark - 视频解码回调
// 每一帧视频数据
- (CVPixelBufferRef)videoReaderDidReadVideoBuffer:(CVPixelBufferRef)pixelBuffer {
    UIImage *newImage = nil;
    CVPixelBufferRef outPixelBuffer = NULL;
    if (!self.compare) {
        FURenderInput *input = [[FURenderInput alloc] init];
        input.pixelBuffer = pixelBuffer;
        input.renderConfig.imageOrientation = 0;
        switch (self.videoReader.videoOrientation) {
            case FUVideoReaderOrientationPortrait:
                input.renderConfig.imageOrientation = FUImageOrientationUP;
                break;
            case FUVideoReaderOrientationLandscapeRight:
                input.renderConfig.imageOrientation = FUImageOrientationLeft;
                break;
            case FUVideoReaderOrientationUpsideDown:
                input.renderConfig.imageOrientation = FUImageOrientationDown;
                break;
            case FUVideoReaderOrientationLandscapeLeft:
                input.renderConfig.imageOrientation = FUImageOrientationRight;
                break;
            default:
                input.renderConfig.imageOrientation = FUImageOrientationUP;
                break;
        }
        FURenderOutput *outPut =  [[FURenderKit shareRenderKit] renderWithInput:input];
        outPixelBuffer = outPut.pixelBuffer;
 
        if (self.takePic) {
            self.takePic = NO ;
            newImage = [FUImageHelper imageFromPixelBuffer:outPixelBuffer];
            dispatch_async(dispatch_get_global_queue(0, 0), ^{
                if (newImage) {
                    UIImageWriteToSavedPhotosAlbum(newImage, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
                }
            });
        }
    } else {
        outPixelBuffer = pixelBuffer;
    }
    
    [self.view displayPixelBuffer:outPixelBuffer];
    
//    size_t w = CVPixelBufferGetWidth(outPixelBuffer);
//    size_t h = CVPixelBufferGetHeight(outPixelBuffer);

//
//    //检测人脸,传递给Flutter 
    if (self.eventChannel) {
        BOOL hasFace = [FUAIKit shareKit].trackedFacesCount > 0;
        [self.eventChannel sendMessageEventChannel:hasFace==YES?@"1":@"0"];
    }

    return outPixelBuffer;
}


// 读写视频完成
- (void)videoReaderDidFinishReadSuccess:(BOOL)success {
    [self.videoReader startReadForLastFrame];
    //通知Flutter 显示 重播，下载等UI
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.methodChannel) {
            [self.methodChannel invokeMethod:@"videoPlay" arguments:@{@"isPlay": @NO}];
        } else {
            NSLog(@"无methodChannel通道");
        }
    });
}

//定时器需要主动清理
- (void)customImageDispose {
    if (_displayLink) {
        [_displayLink invalidate];
        _displayLink.paused = YES ;
        _displayLink = nil ;
    }
 
    
    if (self.videoReader) {
        [self.videoReader stopReading];
        [self.videoReader destory];
        self.videoReader = nil;
    }
   
    self.image = nil ;
    self.videoPath = nil ;
    if ([[NSFileManager defaultManager] fileExistsAtPath:self.desPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:self.desPath error:nil];
    }
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    if ([self.delegate respondsToSelector:@selector(disposePluginWithKey:)]) {
        [self.delegate disposePluginWithKey:NSStringFromClass([self class])];
    }
}

//Flutter 监听原生回调
- (void)requestVideoProcess:(NSDictionary *)params {
    FUCustomOpenGLViewRenderPluginModel *model = [FUCustomOpenGLViewRenderPlugin analysis: params];
    self.methodChannel = model.channel;
}


- (void)image: (UIImage *) image didFinishSavingWithError: (NSError *) error contextInfo: (void *) contextInfo  {
    if(error != NULL){
        [SVProgressHUD showErrorWithStatus:@"保存图片失败"];
    }else{
        [SVProgressHUD showSuccessWithStatus:@"图片已保存到相册"];
    }
}

- (void)video:(NSString *)videoPath didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    if(error != NULL){
        [SVProgressHUD showErrorWithStatus:@"保存视频失败"];
        
    }else{
        [SVProgressHUD showSuccessWithStatus:@"视频已保存到相册"];
    }
}


+ (FUCustomOpenGLViewRenderPluginModel *)analysis:(NSDictionary *)params {
    FUCustomOpenGLViewRenderPluginModel *model = [[FUCustomOpenGLViewRenderPluginModel alloc] init];
 
    if ([params.allKeys containsObject:@"value"]) {
        model.value = params[@"value"];
    }
    
    if ([params.allKeys containsObject:@"method"]) {
        model.method = params[@"method"];
    }
    
    
    if ([params.allKeys containsObject:@"channel"]) {
        model.channel = params[@"channel"];
    }
    
    return model;
}

@end
