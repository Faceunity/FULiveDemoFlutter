//
//  FUBeautyPlugin.h
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface FUBeautyPlugin : NSObject

- (void)loadBeauty;

- (void)unloadBeauty;

- (void)selectFilter:(NSString *)key;

- (void)setFilterLevel:(NSNumber *)level;

- (void)setSkinIntensity:(NSNumber *)intensity type:(NSNumber *)type;

- (void)setShapeIntensity:(NSNumber *)intensity type:(NSNumber *)type;

- (void)setBeautyParam:(NSString *)key value:(NSNumber *)value;

- (void)saveSkinToLocal:(NSString *)jsonString;

- (void)saveShapeToLocal:(NSString *)jsonString;

- (void)saveFilterToLocal:(NSString *)jsonString;

- (NSString *)getLocalSkin;

- (NSString *)getLocalShape;

- (NSString *)getLocalFilter;

@end

NS_ASSUME_NONNULL_END
