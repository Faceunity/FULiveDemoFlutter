//
//  FUCustomOpenGLViewRenderPlugin.h
//  fulive_plugin
//
//  Created by Chen on 2021/8/13.
//

#import "FlutterFUBasePlugin.h"

NS_ASSUME_NONNULL_BEGIN
/**
 * 为自定义视频和图片提供Flutter 插件接口,
 * 由于模块分割无需向native 那样 一个页面集成很多业务功能，所以可以和相机页面公用一个FUBaseViewControllerManager 来对接FURenderKit。后续看情况是否要独立
 * 该页面处理的对应的UI 都在Flutter 里面做掉了，native 无需关心。
 */
@interface FUCustomOpenGLViewRenderPlugin : FlutterFUBasePlugin

@end

NS_ASSUME_NONNULL_END
