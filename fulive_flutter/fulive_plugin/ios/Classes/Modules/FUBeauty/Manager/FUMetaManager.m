//
//  FUMetaManager.m
//  FULiveDemo
//
//  Created by Chen on 2021/2/25.
//  Copyright © 2021 FaceUnity. All rights reserved.
//

#import "FUMetaManager.h"

@implementation FUMetaManager
- (instancetype)init {
    self = [super init];
    if (self) {
        _loadQueue = dispatch_queue_create("namaLoad.com", DISPATCH_QUEUE_SERIAL);
    }
    return self;
}

- (void)releaseItem {
    NSLog(@"releaseItem 异常，子类实现");
    return ;
}

//释放item，内部会自动清除相关资源文件. async
- (void)releaseItemCompletion:(void(^)(void))completion {
    NSLog(@"releaseItemCompletion 异常，子类实现");
    return ;
}

//把当前业务模型数据加载到FURenderKit,不同子类需要根据不同模型重写
- (void)loadItem {
    NSLog(@"loadItem 异常，子类实现");
    return ;
}

//把当前业务模型数据加载到FURenderKit,不同子类需要根据不同模型重写, async
- (void)loadItemCompletion:(void(^)(void))completion {
    NSLog(@"loadItemCompletion 异常，子类实现");
    return ;
}

//清除当前业务的Item，并且重FURenderKit中删除
- (void)clearItem {
    return ;
}
@end
