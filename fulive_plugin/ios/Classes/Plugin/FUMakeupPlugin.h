//
//  FUMakeupPlugin.h
//  fulive_plugin
//
//  Created by 项林平 on 2023/11/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface FUMakeupPlugin : NSObject

- (void)loadCombinationMakeup:(NSDictionary *)makeup;

- (void)setCombinationMakeupIntensity:(NSDictionary *)makeup;

- (void)setSubMakeupBundle:(NSDictionary *)subMakeup;

- (void)setSubMakeupIntensity:(NSDictionary *)subMakeup;

- (void)setSubMakeupColor:(NSDictionary *)subMakeup;

- (void)unloadSubMakeup:(NSNumber *)type;

- (void)unloadCombinationMakeup;

@end

NS_ASSUME_NONNULL_END
