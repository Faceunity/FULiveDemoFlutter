//
//  FUFlutterImagePickerPlugin.m
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/12.
//

#import "FUFlutterImagePickerPlugin.h"
#import <MobileCoreServices/MobileCoreServices.h>
#import "UIImage+FU.h"
#import "FUFlutterPluginModelProtocol.h"
#import <Flutter/Flutter.h>

//后续扩展用,目前没有特有属性
@interface FlutterImagePickModel : NSObject <FUFlutterPluginModelProtocol>

@end

@implementation FlutterImagePickModel

@synthesize method, value, channel;

@end

@interface FUFlutterImagePickerPlugin ()<UINavigationControllerDelegate, UIImagePickerControllerDelegate>
@property (nonatomic, strong) FlutterMethodChannel *methodChannel;
@end

@implementation FUFlutterImagePickerPlugin
//暂时不支持异步callback 回调，后续如果需求强烈在定制规则来实现
- (void)chooseImageOrVideo:(NSDictionary *)params {
    FlutterImagePickModel *model = [FUFlutterImagePickerPlugin analysis: params];
    NSString *type;
    if ([(NSNumber *)model.value intValue] == 0) {
        type = (NSString *)kUTTypeImage;
    } else {
        type = (NSString *)kUTTypeMovie;
    }
    [self showImagePickerWithMediaType:type];
    
    self.methodChannel = model.channel;
}

- (void)showImagePickerWithMediaType:(NSString *)mediaType {
    
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    
    picker.delegate = self;
    picker.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
    picker.allowsEditing = NO;
    picker.mediaTypes = @[mediaType];
  
    UIViewController *topVC = [FUFlutterImagePickerPlugin topViewController];
    
    [topVC presentViewController:picker animated:YES completion:nil];
}

//获取顶层控制器
+ (UIViewController *)topViewController {
    UIViewController *topVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    //循环之前tempVC和topVC是一样的
    UIViewController *tempVC = topVC;
    while (1) {
        if ([topVC isKindOfClass:[UITabBarController class]]) {
            topVC = ((UITabBarController*)topVC).selectedViewController;
        }
        if ([topVC isKindOfClass:[UINavigationController class]]) {
            topVC = ((UINavigationController*)topVC).visibleViewController;
        }
        if (topVC.presentedViewController) {
            topVC = topVC.presentedViewController;
        }
        //如果两者一样，说明循环结束了
        if ([tempVC isEqual:topVC]) {
            break;
        } else {
            //如果两者不一样，继续循环
            tempVC = topVC;
        }
    }
    return topVC;
}


- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info {

    
    // 关闭相册
    [picker dismissViewControllerAnimated:NO completion:^{
        NSString *mediaType = [info objectForKey:UIImagePickerControllerMediaType];
        
        if ([mediaType isEqualToString:(NSString *)kUTTypeMovie]){  //视频
            NSURL *videoURL = info[UIImagePickerControllerMediaURL];
            NSArray *arr = @[videoURL];
            NSData *data = [NSKeyedArchiver archivedDataWithRootObject:arr];
            
            [[NSUserDefaults standardUserDefaults] setObject:data forKey:@"type_1"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            //通知Flutter 创建新的页面
            if (self.methodChannel) {
                [self.methodChannel invokeMethod:@"customSelectedImage" arguments:@{@"type": @1}];
            } else {
                NSLog(@"无methodChannel通道");
            }
            
        }else if ([mediaType isEqualToString:(NSString *)kUTTypeImage]) { //照片
            
            UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
            CGFloat imagePixel = image.size.width * image.size.height;
            if (imagePixel > 24000000) {
                // 大于24000000像素需要压缩
                CGFloat ratio = 24000000 / imagePixel * 1.0;
                image = [image fu_compress:ratio];
            }
            // 图片转正
            if (image.imageOrientation != UIImageOrientationUp && image.imageOrientation != UIImageOrientationUpMirrored) {
                
                UIGraphicsBeginImageContext(CGSizeMake(image.size.width * 0.5, image.size.height * 0.5));
                
                [image drawInRect:CGRectMake(0, 0, image.size.width * 0.5, image.size.height * 0.5)];
                
                image = UIGraphicsGetImageFromCurrentImageContext();
                
                UIGraphicsEndImageContext();
            }
            [[NSUserDefaults standardUserDefaults] setObject: UIImagePNGRepresentation(image) forKey:@"type_0"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            //通知Flutter 创建新的页面
            if (self.methodChannel) {
                [self.methodChannel invokeMethod:@"customSelectedImage" arguments:@{@"type": @0}];
            } else {
                NSLog(@"无methodChannel通道");
            }
        }
    }];
    
}

+ (FlutterImagePickModel *)analysis:(NSDictionary *)params {
    FlutterImagePickModel *model = [[FlutterImagePickModel alloc] init];

    if ([params.allKeys containsObject:@"value"]) {
        model.value = params[@"value"];
    }
    
    if ([params.allKeys containsObject:@"method"]) {
        model.method = params[@"method"];
    }
    
    if ([params.allKeys containsObject:@"channel"]) {
        model.channel = params[@"channel"];
    }

    return model;
}


- (void)imagePickDispose {
    if ([self.delegate respondsToSelector:@selector(disposePluginWithKey:)]) {
        [self.delegate disposePluginWithKey:NSStringFromClass(self.class)];
    }
    
}
@end
