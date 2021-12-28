//
//  FlutterCustomSubMakeupModel.h
//  fulive_plugin
//
//  Created by Chen on 2021/11/30.
//

#import "FlutterBaseModel.h"
#import "FlutterSubMakeupModel.h"

NS_ASSUME_NONNULL_BEGIN
/**
 * Flutter 侧用来确定 支持自定义子妆的组合妆时 对应的子妆选中状态模型
 */
@interface FlutterCustomSubMakeupModel : FlutterBaseModel
@property (nonatomic, strong) NSArray<FlutterSubMakeupModel *> *sub;
@end

NS_ASSUME_NONNULL_END
