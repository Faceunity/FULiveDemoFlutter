package com.faceunity.core.avatar.control


/**
 *
 * DESC：Avatar比对数据存储模型
 * Created on 2021/6/30
 *
 */
class AvatarCompareData {
    /*bundle计数管理*/
    val bundleRemoveMap = LinkedHashMap<String, Int>()//比对需要减少的Bundle，key:路径  value 引用计数
    val bundleAddMap = LinkedHashMap<String, Int>()//比对新增的Bundle，key:路径  value 引用计数

    /*scene管理*/
    val sceneRemoveList = ArrayList<FUASceneData>()//需要移除的场景
    val sceneAddList = ArrayList<FUASceneData>()//需要绑定创建的场景

    /*scene-avatar管理*/
    val sceneUnbindAvatarMap = LinkedHashMap<Long, ArrayList<Long>>()//Scene比对结束需要解绑Avatar
    val sceneBindAvatarMap = LinkedHashMap<Long, ArrayList<Long>>()//Scene比对结束需要绑定的Avatar
    val sceneReplaceAvatarMap = LinkedHashMap<Long, Long>()//Avatar替换使用ModelId缓存

    /*scene-bundle管理*/
    val sceneBindHandleMap = LinkedHashMap<FUASceneData, ArrayList<String>>()//Scene比对结束需要绑定的句柄
    val sceneUnbindHandleMap = LinkedHashMap<Long, ArrayList<String>>()//Scene比对结束需要解绑的句柄

    /*avatar-bundle管理*/
    val avatarBindHandleMap = LinkedHashMap<FUAAvatarData, ArrayList<String>>()//Avatar比对结束需要绑定的句柄
    val avatarUnbindHandleMap = LinkedHashMap<Long, ArrayList<String>>()//Avatar比对结束需要解绑的句柄

    /*avatar-param管理*/
    val avatarParamsMap = LinkedHashMap<Long, LinkedHashMap<String, () -> Unit>>()//avatar属性配置
}