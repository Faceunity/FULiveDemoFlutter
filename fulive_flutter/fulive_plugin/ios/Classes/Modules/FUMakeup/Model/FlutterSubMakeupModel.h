//
//  FlutterSubMakeupModel.h
//  fulive_plugin
//
//  Created by Chen on 2021/11/30.
//

#import "FlutterBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface FlutterSubMakeupModel : FlutterBaseModel
@property (nonatomic, strong) NSString *title; //标识具体是什么子妆，debug用，实际Flutter 不会用来处理显示
@property (nonatomic, assign) NSInteger bundleIndex; //具体子妆的索引，如口红的红润、珠光、
@property (nonatomic, assign) NSInteger colorIndex; //具体子妆颜色值索引
//整体妆容程度值 * 具体子妆的值, float， CGFloat 为0 或 1 时， json 转换会导致精度丢失, Flutter 侧接收到之后变成了整型
//所以以字符串形式处理
@property (nonatomic, strong) NSString *value;
@end

NS_ASSUME_NONNULL_END
