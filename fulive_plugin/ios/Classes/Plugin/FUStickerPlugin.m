//
//  FUStickerPlugin.m
//  fulive_plugin
//
//  Created by 项林平 on 2023/12/11.
//

#import "FUStickerPlugin.h"
#import "FURenderKitManager.h"
#import "FUUtility.h"

@interface FUStickerPlugin ()

@property (nonatomic, strong) FUSticker *currentSticker;

@end

@implementation FUStickerPlugin

- (void)selectSticker:(NSString *)name {
    NSString *path = [FUUtility pluginBundlePathWithName:name];
    FUSticker *sticker = [FUSticker itemWithPath:path name:name];
    if (self.currentSticker) {
        [[FURenderKit shareRenderKit].stickerContainer replaceSticker:self.currentSticker withSticker:sticker completion:nil];
    } else {
        [[FURenderKit shareRenderKit].stickerContainer addSticker:sticker completion:nil];
    }
    self.currentSticker = sticker;
}

- (void)removeSticker {
    if (self.currentSticker) {
        [[FURenderKit shareRenderKit].stickerContainer removeAllSticks];
        self.currentSticker = nil;
    }
}

@end
