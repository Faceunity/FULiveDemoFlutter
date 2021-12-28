//
//  FLutterFUBasePlugin.m
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import "FlutterFUBasePlugin.h"
#import <FURenderKit/FURenderKit.h>
@interface FlutterFUBasePlugin ()

@end


@interface FlutterFUBasePlugin () {
    
    //记录当前被渲染的view
    FUGLDisplayView *_curRenderView;
}

@end

@implementation FlutterFUBasePlugin

/**
 * 抽象接口开始
 */
- (void)configBiz {NSAssert(1, @"子类实现");}
- (void)dispose {NSAssert(1, @"子类实现");}
- (void)flutterWillAppear {NSAssert(1, @"子类实现");}
- (void)flutterWillDisappear {NSAssert(1, @"子类实现");}


//Plugin销毁协议, 存储时候用的是类名字符串
- (void)disposePluginWithKey:(NSString *)key {NSAssert(1, @"子类实现");}


/**
 * 非抽象接口
 */

- (void)startCapture {
    [[FURenderKit shareRenderKit] startInternalCamera];
    if (_curRenderView) {
        [FURenderKit shareRenderKit].glDisplayView = _curRenderView;
    }
    [FURenderKit shareRenderKit].pause = NO;
}


- (void)stopCature {
    [[FURenderKit shareRenderKit].captureCamera resetFocusAndExposureModes];
    //处理进入自定义视频/图片模块的问题，必须停止
    NSLog(@"viewWillDisappear : %@",self);
    [[FURenderKit shareRenderKit] stopInternalCamera];
    //记录当前页面的view，下次再进入时候赋值给FURenderKit
    _curRenderView = [FURenderKit shareRenderKit].glDisplayView;
    [FURenderKit shareRenderKit].glDisplayView = nil;
}


- (void)disposeRenderKit {
    [FURenderKit shareRenderKit].delegate = nil;
//    [FURenderKit shareRenderKit].glDisplayView = nil;
//    [FURenderKit shareRenderKit].pause = YES;
    [[FURenderKit shareRenderKit] stopInternalCamera];
    //分别率还原成720 * 1080
    [FURenderKit shareRenderKit].internalCameraSetting.sessionPreset = AVCaptureSessionPreset1280x720;
    //退出页面还原成前置摄像头
    [[FURenderKit shareRenderKit].captureCamera changeCameraInputDeviceisFront:YES];
}

- (void)setOnCameraChange {
    [FUAIKit resetTrackedResult];
}
@end
