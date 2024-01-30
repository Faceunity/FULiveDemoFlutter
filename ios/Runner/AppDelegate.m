#import "AppDelegate.h"
#import "GeneratedPluginRegistrant.h"
#import <fulive_plugin-umbrella.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [[FURenderKitManager sharedManager] setupRenderKit];
    [GeneratedPluginRegistrant registerWithRegistry:self];
    return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
