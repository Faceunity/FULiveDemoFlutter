package com.faceunity.core.avatar.control

import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：
 * Created on 2021/3/30
 *
 */
class AvatarController : BaseAvatarController() {


    //region 业务接口

    /**
     * 加载Scene场景
     * @param sceneData FUASceneData
     */
    internal fun doAddAvatarScene(sceneData: FUASceneData) {
        doBackgroundAction {
            loadControllerBundle(sceneData)
            if (mControllerBundleHandle <= 0) {
                return@doBackgroundAction
            }
            clearCompData()
            addScene(sceneData)
            applyCompData()
        }
    }

    /**
     * 移除Scene场景
     * @param sceneData FUASceneData
     */
    internal fun doRemoveAvatarScene(sceneData: FUASceneData) {
        doBackgroundAction {
            clearCompData()
            removeScene(sceneData)
            applyCompData()
        }
    }

    /**
     * 替换场景
     * @param oldAvatar FUASceneData
     * @param newAvatar FUASceneData
     */
    internal fun doReplaceAvatarScene(oldAvatar: FUASceneData, newAvatar: FUASceneData) {
        doBackgroundAction {
            clearCompData()
            replaceScene(oldAvatar, newAvatar)
            applyCompData()
        }
    }


    /**
     * 替换 Avatar
     * @param sceneId Long
     * @param oldAvatar FUAAvatarData
     * @param newAvatar FUAAvatarData
     */
    internal fun doReplaceAvatar(sceneId: Long, oldAvatar: FUAAvatarData, newAvatar: FUAAvatarData) {
        doBackgroundAction {
            clearCompData()
            replaceAvatar(sceneId, oldAvatar, newAvatar)
            applyCompData()
        }
    }


    /**
     * 新增 Avatar
     * @param sceneId Long
     * @param avatar FUAAvatarData
     */
    internal fun doAddAvatar(sceneId: Long, avatar: FUAAvatarData) {
        doBackgroundAction {
            clearCompData()
            addAvatar(sceneId, avatar)
            applyCompData()
        }
    }


    /**
     * 移除 Avatar
     * @param sceneId Long
     * @param avatar FUAAvatarData
     */
    internal fun doRemoveAvatar(sceneId: Long, avatar: FUAAvatarData) {
        doBackgroundAction {
            clearCompData()
            removeAvatar(sceneId, avatar)
            applyCompData()
        }
    }


    override fun release(unit: (() -> Unit)?) {
        super.release {
            releaseAll()
        }
    }

    //endregion 业务接口

    //region 功能实现


    /**
     * 比对数据生效
     */
    private fun applyCompData() {
        //1.初始化加载新道具
        applyCreateBundle()
        //2.解绑Avatar道具
        applyRemoveAvatarBundle()
        //3.解绑Avatar
        applyRemoveAvatar()
        //4.解绑Scene道具
        applyRemoveSceneBundle()
        //5.移除场景
        applyRemoveScene()
        //6.增加场景并设置参数
        applyAddScene()
        //7.绑定Scene对象道具并设置参数
        applyAddSceneBundle()
        //8.增加Avatar对象并绑定到Scene
        applyAddAvatar()
        //9.绑定Avatar对象道具并设置参数
        applyAddAvatarBundle()
        //10.更新引用计数
        updateReferenceCount()
    }


    /**
     * 加载新道具
     */
    private fun applyCreateBundle() {
        bundleCreateList.forEach {
            val handle = mBundleManager.loadBundleFile(getFileName(it), it)
            if (handle > 0) {
                handleBundleIdMap[it] = handle
            } else {
                FULogger.e(TAG, "loadBundle failed handle:$handle  path:$it")
            }
        }
    }

    /**
     * 移除Avatar道具
     */
    private fun applyRemoveAvatarBundle() {
        avatarUnbindHandleMap.forEach { (id, bundles) ->
            if (avatarIdMap.containsKey(id)) {
                val avatarId = avatarIdMap[id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    if (handleBundleIdMap.containsKey(it) && handleBundleIdMap[it]!! > 0) {
                        items.add(handleBundleIdMap[it]!!)
                    }
                }
                SDKController.unbindItemsFromInstance(avatarId, items.toIntArray())
            }
        }

    }

    /**
     * 解绑Avatar
     */
    private fun applyRemoveAvatar() {
        sceneUnbindAvatarMap.forEach { (id, avatarList) ->
            if (sceneIdMap.containsKey(id)) {
                avatarList.forEach {
                    if (avatarIdMap.containsKey(it)) {
                        val instanceId = avatarIdMap[it]!!
                        SDKController.destroyInstance(instanceId)
                        avatarIdMap.remove(it)
                    }
                }
            }
        }
    }

    /**
     * 解绑场景道具
     */
    private fun applyRemoveSceneBundle() {
        sceneUnbindHandleMap.forEach { (id, bundles) ->
            if (sceneIdMap.containsKey(id)) {
                val sceneId = sceneIdMap[id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    if (handleBundleIdMap.containsKey(it) && handleBundleIdMap[it]!! > 0) {
                        items.add(handleBundleIdMap[it]!!)
                    }
                }
                SDKController.unbindItemsFromScene(sceneId, items.toIntArray())
            }
        }
    }


    /**
     * 移除Scene
     */
    private fun applyRemoveScene() {
        sceneRemoveList.forEach {
            if (sceneIdMap.containsKey(it.id)) {
                val sceneId = sceneIdMap[it.id]!!
                SDKController.destroyScene(sceneId)
                sceneIdMap.remove(it.id)
            }

        }
    }

    /**
     * 增加Scene
     */
    private fun applyAddScene() {
        sceneAddList.forEach {
            val sceneId = SDKController.createScene()
            if (sceneId > 0) {
                sceneIdMap[it.id] = sceneId
            }

        }
    }

    /**
     *Scene绑定道具
     */
    private fun applyAddSceneBundle() {
        sceneBindHandleMap.forEach { (scene, bundles) ->
            if (sceneIdMap.containsKey(scene.id)) {
                val sceneId = sceneIdMap[scene.id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    if (handleBundleIdMap.containsKey(it) && handleBundleIdMap[it]!! > 0) {
                        items.add(handleBundleIdMap[it]!!)
                    }
                }
                SDKController.bindItemsToScene(sceneId, items.toIntArray())
                scene.params.forEach { (_, unit) ->
                    unit.invoke()
                }
            }

        }
    }

    /**
     * 增加Avatar对象
     */
    private fun applyAddAvatar() {
        sceneBindAvatarMap.forEach { (id, avatarList) ->
            if (sceneIdMap.containsKey(id)) {
                val sceneId = sceneIdMap[id]!!
                avatarList.forEach {
                    val avatarId = SDKController.createInstance(sceneId)
                    if (avatarId > 0) {
                        avatarIdMap[it] = avatarId
                    }
                }
            }
        }
    }

    /**
     * 绑定Avatar道具并设置参数
     */
    private fun applyAddAvatarBundle() {
        avatarBindHandleMap.forEach { (avatar, bundles) ->
            if (avatarIdMap.containsKey(avatar.id)) {
                val avatarId = avatarIdMap[avatar.id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    if (handleBundleIdMap.containsKey(it) && handleBundleIdMap[it]!! > 0) {
                        items.add(handleBundleIdMap[it]!!)
                    }
                }
                avatar.initParam.forEach { (_, unit) ->
                    unit.invoke()
                }
                SDKController.bindItemsToInstance(avatarId, items.toIntArray())
                avatar.param.forEach { (_, unit) ->
                    unit.invoke()
                }
            }
        }
    }

    /**
     * 更新引用计数
     */
    private fun updateReferenceCount() {
        bundleAddMap.forEach { (path, count) ->
            addReferenceCount(handleReferenceCountMap, path, count)
        }
        val items = ArrayList<Int>()
        bundleRemoveMap.forEach { (path, count) ->
            if (handleReferenceCountMap.containsKey(path)) {
                if (handleReferenceCountMap[path]!! > count) {
                    handleReferenceCountMap[path] = handleReferenceCountMap[path]!! - count
                } else {
                    if (handleBundleIdMap.containsKey(path)) {
                        items.add(handleBundleIdMap[path]!!)
                    }
                    handleReferenceCountMap.remove(path)
                    handleBundleIdMap.remove(path)
                }
            }
        }
        mBundleManager.destroyBundle(items.toIntArray())
        clearCompData()
    }


    /**
     * 释放所有资源
     */
    private fun releaseAll() {
        val array = ArrayList<Int>()
        handleBundleIdMap.forEach { (path, hanle) ->
            array.add(hanle)
        }
        handleBundleIdMap.clear()
        handleReferenceCountMap.clear()
        mBundleManager.destroyBundle(array.toIntArray())
        avatarIdMap.forEach { (_, id) ->
            SDKController.destroyInstance(id)
        }
        avatarIdMap.clear()
        sceneIdMap.forEach { (_, id) ->
            SDKController.destroyScene(id)
        }
        sceneIdMap.clear()

    }

    //endregion 功能实现
    //region Scene接口

    /**
     * 人体驱动开关
     * @param sceneId Int
     * @param enable Boolean
     */
    fun enableHumanProcessor(sceneId: Long, enable: Boolean) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                SDKController.enableHumanProcessor(id, enable)
            }
        }
    }

    /**
     * 人体驱动类型
     * @param sceneId Int
     * @param isFull Boolean 是否是全身
     */
    fun humanProcessorSet3DScene(sceneId: Long, isFull: Boolean) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                SDKController.humanProcessorSet3DScene(id, isFull)
            }
        }
    }

    /**
     * 设置跟随模式
     * @param sceneId Int
     * @param mode 1 为跟随人体
     */
    fun enableHumanFollowMode(sceneId: Long, mode: Boolean) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                SDKController.enableHumanFollowMode(id, if (mode) 1 else 0)
            }
        }
    }

    /**
     * 显示avatar活动范围
     */
    fun setHumanProcessorTranslationScale(sceneId: Long, x: Float, y: Float, z: Float) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                SDKController.setHumanProcessorTranslationScale(id, x, y, z)
            }
        }
    }

    /**
     * 设置avatar的大小
     * @param scale 比例
     */
    fun humanProcessorSetAvatarScale(scale: Float) {
        doBackgroundAction {
            SDKController.humanProcessorSetAvatarScale(scale)
        }
    }

    /**
     * 设置avatar的偏移量
     * @param offsetX x偏移量
     * @param offsetY x偏移量
     * @param offsetZ x偏移量
     */
    fun humanProcessorSetAvatarGlobalOffset(offsetX: Float, offsetY: Float, offsetZ: Float) {
        doBackgroundAction {
            SDKController.humanProcessorSetAvatarGlobalOffset(offsetX, offsetY, offsetZ)
        }
    }

    /**
     * 抖动相关
     */
    fun humanProcessorSetAvatarAnimFilterParams(nBufferFrames: Int, pos: Float, angle: Float) {
        doBackgroundAction {
            SDKController.humanProcessorSetAvatarAnimFilterParams(nBufferFrames, pos, angle)
        }
    }

    /**
     * 加载句柄
     * @param bundle BundleHandleData
     */
    fun loadSceneItemBundle(sceneId: Long, bundle: FUBundleData) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                var handle = handleBundleIdMap[bundle.path]
                if (handle == null || handle == 0) {
                    handle = mBundleManager.loadBundleFile(bundle.name, bundle.path)
                    if (handle > 0) {
                        handleBundleIdMap[bundle.path] = handle
                    } else {
                        FULogger.e(TAG, "loadBundle failed handle:$handle  path:$it")
                        return@doBackgroundAction
                    }
                }
                addReferenceCount(handleReferenceCountMap, bundle.path, 1)
                SDKController.bindItemsToScene(it, intArrayOf(handle))
            }
        }
    }

    /**
     * 替换句柄
     * @param sceneId Long
     * @param oldBundle FUBundleData
     * @param newBundle FUBundleData
     */
    fun replaceSceneItemBundle(sceneId: Long, oldBundle: FUBundleData, newBundle: FUBundleData) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                var newHandle = handleBundleIdMap[newBundle.path]
                var oldHandle = handleBundleIdMap[oldBundle.path]
                if (newHandle == null || newHandle == 0) {
                    newHandle = mBundleManager.loadBundleFile(newBundle.name, newBundle.path)
                    if (newHandle > 0) {
                        handleBundleIdMap[newBundle.path] = newHandle
                    }
                }
                if (oldHandle != null && oldHandle > 0) {
                    SDKController.unbindItemsFromScene(it, intArrayOf(oldHandle))
                    if (handleReferenceCountMap[oldBundle.path] == 1) {
                        handleReferenceCountMap.remove(oldBundle.path)
                        handleBundleIdMap.remove(oldBundle.path)
                        mBundleManager.destroyBundle(intArrayOf(oldHandle))
                    } else {
                        removeReferenceCount(handleReferenceCountMap, oldBundle.path, 1)
                    }
                }
                if (newHandle > 0) {
                    SDKController.bindItemsToScene(it, intArrayOf(newHandle))
                    addReferenceCount(handleReferenceCountMap, newBundle.path, 1)
                }
            }
        }
    }

    /**
     * 移除句柄
     * @param bundle BundleHandleData
     */
    fun removeSceneItemBundle(sceneId: Long, bundle: FUBundleData) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                var handle = handleBundleIdMap[bundle.path]
                if (handle != null && handle > 0) {
                    SDKController.unbindItemsFromScene(it, intArrayOf(handle))
                    if (handleReferenceCountMap[bundle.path] == 1) {
                        handleReferenceCountMap.remove(bundle.path)
                        handleBundleIdMap.remove(bundle.path)
                        mBundleManager.destroyBundle(intArrayOf(handle))
                    } else {
                        removeReferenceCount(handleReferenceCountMap, bundle.path, 1)
                    }
                }
            }
        }
    }
    //endregion Scene接口
    //region Avatar接口

    /**
     * 设置角色点位
     * @param avatarId Long
     * @param x Double
     * @param y Double
     * @param z Double
     */
    fun setInstanceTargetPosition(avatarId: Long, x: Double, y: Double, z: Double) {
        doBackgroundAction {
            val instanceId = avatarIdMap[avatarId]
            instanceId?.let {
                SDKController.setInstanceTargetPosition(it, x, y, z)
            }
        }
    }


    //endregion Avatar接口


}