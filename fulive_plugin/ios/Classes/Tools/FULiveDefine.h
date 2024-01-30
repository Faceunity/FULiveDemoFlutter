//
//  FULiveDefine.h
//  FULive
//
//  Created by L on 2018/8/1.
//  Copyright © 2018年 L. All rights reserved.
//

#import <UIKit/UIKit.h>

#pragma mark - 枚举

/// 特效模块
typedef NS_ENUM(NSUInteger, FUModule) {
    FUModuleBeauty = 0,
    FUModuleMakeup,
    FUModuleSticker,
};

/// 子妆容类型
typedef NS_ENUM(NSUInteger, FUSingleMakeupType) {
    FUSingleMakeupTypeFoundation,   // 粉底
    FUSingleMakeupTypeLip,          // 口红
    FUSingleMakeupTypeBlusher,      // 腮红
    FUSingleMakeupTypeEyebrow,      // 眉毛
    FUSingleMakeupTypeEyeshadow,    // 眼影
    FUSingleMakeupTypeEyeliner,     // 眼线
    FUSingleMakeupTypeEyelash,      // 睫毛
    FUSingleMakeupTypeHighlight,    // 高光
    FUSingleMakeupTypeShadow,       // 阴影
    FUSingleMakeupTypePupil         // 美瞳
};

#define FUDocumentPath NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject

static NSString * const FUPersistentBeautySkinKey = @"FUPersistentBeautySkin";

static NSString * const FUPersistentBeautyShapeKey = @"FUPersistentBeautyShape";

static NSString * const FUPersistentBeautyFilterKey = @"FUPersistentBeautyFilter";

static inline NSString * FUCurrentDateString(void) {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"YYYYMMddhhmmssSS";
    NSDate *date = [NSDate date];
    NSString *dateString = [formatter stringFromDate:date];
    return dateString;
}
