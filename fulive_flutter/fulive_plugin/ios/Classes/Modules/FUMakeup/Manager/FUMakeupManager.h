//
//  FUMakeUpManager.h
//  FULiveDemo
//
//  Created by Chen on 2021/3/2.
//  Copyright © 2021 FaceUnity. All rights reserved.
//

#import "FUMetaManager.h"
#import <FURenderKit/FUMakeup.h>
#import "FUMakeUpDefine.h"

@class FUMakeupModel, FUMakeupSupModel, FUSingleMakeupModel;
NS_ASSUME_NONNULL_BEGIN

@interface FUMakeupManager : FUMetaManager
@property (nonatomic, strong, nullable) FUMakeup *makeup;

@property (nonatomic, strong, readonly) NSArray <FUMakeupModel *>* dataArray;

@property (nonatomic, strong, readonly) NSArray <FUMakeupSupModel *>*supArray;

@property (nonatomic, assign) int preSelectedIndex;//记录仪上一次UI选中的组合妆索引

//设置子妆容数据
- (void)setMakeupSupModel:(FUSingleMakeupModel *)model type:(UIMAKEUITYPE)type;

//设置整体妆容数据
- (void)setMakeupWholeModel:(FUMakeupSupModel *)model;

- (void)setSupModelBundleWithModel:(FUMakeupSupModel *)model
                        completion:(void(^)(void))completion;

- (void)setNewMakeupFilterIntensity:(double)intensity;

//检测组合妆是否有变化
-(BOOL)supValueHaveChangeWithIndex:(int)index;

/**
 * 可自定义组合妆对应的子妆状态处理
 * index supModel 组合妆索引
 * dataArray 当前子妆数组数据
 */
- (NSArray *)makeupTransformToSubMakeupWithIndex:(int)index;
@end

NS_ASSUME_NONNULL_END
