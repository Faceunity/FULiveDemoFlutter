//
//  FUStickerPlugin.h
//  fulive_plugin
//
//  Created by 项林平 on 2023/12/11.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface FUStickerPlugin : NSObject

- (void)selectSticker:(NSString *)name;

- (void)removeSticker;

@end

NS_ASSUME_NONNULL_END
