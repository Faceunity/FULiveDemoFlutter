//
//  FUModulePluginProtocol.h
//  FlutterPluginRegistrant
//
//  Created by Chen on 2021/8/19.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol FUModulePluginProtocol <NSObject>

@required
//Plugin销毁协议, 存储时候用的是类名字符串
- (void)disposePluginWithKey:(NSString *)key;
@end

NS_ASSUME_NONNULL_END
