//
//  FUUtility.m
//  FULiveDemo
//
//  Created by 项林平 on 2022/8/16.
//

#import "FUUtility.h"
#import <Photos/Photos.h>

@implementation FUUtility

+ (NSString *)pluginBundlePathWithName:(NSString *)name {
    NSBundle *bundle = [NSBundle bundleForClass:self];
    NSURL *resourceURL = [bundle URLForResource:@"fulive_plugin" withExtension:@"bundle"];
    return [NSBundle pathForResource:name ofType:@"bundle" inDirectory:resourceURL.path];
}

+ (void)requestPhotoLibraryAuthorization:(void (^)(PHAuthorizationStatus))handler {
    if (@available(iOS 14, *)) {
        [PHPhotoLibrary requestAuthorizationForAccessLevel:PHAccessLevelReadWrite handler:^(PHAuthorizationStatus status) {
            !handler ?: handler(status);
        }];
    } else {
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
            !handler ?: handler(status);
        }];
    }
}

+ (void)requestVideoURLFromInfo:(NSDictionary<NSString *,id> *)info resultHandler:(void (^)(NSURL * _Nonnull))handler {
    if (info[UIImagePickerControllerReferenceURL]) {
        NSURL *refrenceURL = info[UIImagePickerControllerReferenceURL];
        PHFetchResult<PHAsset *> *assets = [PHAsset fetchAssetsWithALAssetURLs:@[refrenceURL] options:nil];
        [[PHImageManager defaultManager] requestAVAssetForVideo:assets.firstObject options:nil resultHandler:^(AVAsset * _Nullable asset, AVAudioMix * _Nullable audioMix, NSDictionary * _Nullable info) {
            AVURLAsset *urlAsset = (AVURLAsset *)asset;
            !handler ?: handler(urlAsset.URL);
        }];
    } else if (info[UIImagePickerControllerMediaURL]) {
        !handler ?: handler(info[UIImagePickerControllerMediaURL]);
    } else {
        if (@available(iOS 11.0, *)) {
            PHAsset *asset = info[UIImagePickerControllerPHAsset];
            [[PHImageManager defaultManager] requestAVAssetForVideo:asset options:nil resultHandler:^(AVAsset * _Nullable asset, AVAudioMix * _Nullable audioMix, NSDictionary * _Nullable info) {
                AVURLAsset *urlAsset = (AVURLAsset *)asset;
                !handler ?: handler(urlAsset.URL);
            }];
        }
    }
}

+ (UIImage *)previewImageFromVideoURL:(NSURL *)videoURL preferredTrackTransform:(BOOL)preferred {
    if (!videoURL) {
        return nil;
    }
    AVURLAsset *asset = [AVURLAsset assetWithURL:videoURL];
    AVAssetImageGenerator *imageGenerator = [[AVAssetImageGenerator alloc] initWithAsset:asset];
    imageGenerator.appliesPreferredTrackTransform = preferred;
    CMTime time = CMTimeMakeWithSeconds(0.0, 600);
    NSError *error = nil;
    CMTime actualTime;
    CGImageRef image = [imageGenerator copyCGImageAtTime:time actualTime:&actualTime error:&error];
    UIImage *videoImage = [[UIImage alloc] initWithCGImage:image];
    CGImageRelease(image);
    return videoImage;
}

+ (UIImage *)lastFrameImageFromVideoURL:(NSURL *)videoURL preferredTrackTransform:(BOOL)preferred {
    if (!videoURL) {
        return nil;
    }
    AVURLAsset *asset = [AVURLAsset assetWithURL:videoURL];
    AVAssetImageGenerator *imageGenerator = [[AVAssetImageGenerator alloc] initWithAsset:asset];
    imageGenerator.appliesPreferredTrackTransform = preferred;
    Float64 lastFrameTime = CMTimeGetSeconds(asset.duration);
    CMTime time = CMTimeMakeWithSeconds(lastFrameTime, 600);
    NSError *error = nil;
    CMTime actualTime;
    CGImageRef image = [imageGenerator copyCGImageAtTime:time actualTime:&actualTime error:&error];
    UIImage *videoImage = [[UIImage alloc] initWithCGImage:image];
    CGImageRelease(image);
    return videoImage;
}

+ (NSUInteger)videoOrientationFromVideoURL:(NSURL *)videoURL {
    AVURLAsset *asset = [[AVURLAsset alloc] initWithURL:videoURL options:@{AVURLAssetPreferPreciseDurationAndTimingKey : @YES}];
    NSArray *videoTracks = [asset tracksWithMediaType:AVMediaTypeVideo];
    if (videoTracks.count == 0) {
        return 0;
    }
    AVAssetTrack *videoTrack = videoTracks[0];
    NSUInteger orientation = 0;
    CGAffineTransform transform = videoTrack.preferredTransform;
    if(transform.a == 0 && transform.b == 1.0 && transform.c == -1.0 && transform.d == 0) {
        orientation = 1;
    }else if(transform.a == 0 && transform.b == -1.0 && transform.c == 1.0 && transform.d == 0){
        orientation = 3;
    }else if(transform.a == 1.0 && transform.b == 0 && transform.c == 0 && transform.d == 1.0){
        orientation = 0;
    }else if(transform.a == -1.0 && transform.b == 0 && transform.c == 0 && transform.d == -1.0){
        orientation = 2;
    }
    return orientation;
}

+ (UIViewController *)topViewController {
    UIViewController *root = [UIApplication sharedApplication].delegate.window.rootViewController;
    if (!root) {
        root = [UIApplication sharedApplication].windows.firstObject.rootViewController;
    }
    return [self currentViewControllerWithRootViewController:root];
}

+ (UIViewController *)currentViewControllerWithRootViewController:(UIViewController *)viewController {
    if (viewController.presentedViewController) {
        return [self currentViewControllerWithRootViewController:viewController.presentedViewController];
    } else if ([viewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navigation = (UINavigationController *)viewController;
        return [self currentViewControllerWithRootViewController:navigation.visibleViewController];
    } else if ([viewController isKindOfClass:[UITabBarController class]]) {
        UITabBarController *tabBar = (UITabBarController *)viewController;
        return [self currentViewControllerWithRootViewController:tabBar.selectedViewController];
    } else {
        return viewController;
    }
}

@end
