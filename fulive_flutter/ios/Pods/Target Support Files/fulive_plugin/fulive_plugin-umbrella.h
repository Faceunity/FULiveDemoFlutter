#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "authpack.h"
#import "FUCustomOpenGLViewRender.h"
#import "FUCustomOpenGLViewRenderPlugin.h"
#import "FUCustomRender.h"
#import "FlutterBaseModel.h"
#import "FlutterCommonPlugin.h"
#import "FlutterFUBasePlugin.h"
#import "FUFlutterPluginModelProtocol.h"
#import "FULiveModulePlugin.h"
#import "FULivePlugin.h"
#import "FUModulePluginProtocol.h"
#import "FUNativeViewFactory.h"
#import "FUNativeViewPlugin.h"
#import "FUFlutterEventChannel.h"
#import "FlutterFUBeautyPlugin.h"
#import "FUBaseUIModelProtocol.h"
#import "FUBeautyDefine.h"
#import "FUBaseViewControllerManager.h"
#import "FUManager.h"
#import "FUMetaManager.h"
#import "FUBeautyModel.h"
#import "FUMetaModel.h"
#import "FUStyleModel.h"
#import "FlutterFUMakeupPlugin.h"
#import "FUMakeUpDefine.h"
#import "FUMakeupProtocol.h"
#import "FUMakeupManager.h"
#import "FlutterCustomSubMakeupModel.h"
#import "FlutterSubMakeupModel.h"
#import "FUMakeupModel.h"
#import "FUMakeupSupModel.h"
#import "FUSingleMakeupModel.h"
#import "FUStickerDefine.h"
#import "FUStickerPlugin.h"
#import "FUStickerManager.h"
#import "FUFlutterImagePickerPlugin.h"
#import "FUImageHelper.h"
#import "FULocalDataManager.h"
#import "FUStickerProtocol.h"
#import "FUVideoReader.h"
#import "NSObject+AddBundle.h"
#import "NSObject+economizer.h"
#import "UIImage+FU.h"

FOUNDATION_EXPORT double fulive_pluginVersionNumber;
FOUNDATION_EXPORT const unsigned char fulive_pluginVersionString[];

