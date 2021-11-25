package com.faceunity.core.avatar.control

import com.faceunity.core.avatar.listener.OnSceneListener
import com.faceunity.core.entity.*
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import java.util.concurrent.CountDownLatch


/**
 *
 * DESC：
 * Created on 2021/3/30
 *
 */
class AvatarController : BaseAvatarController() {

    //region Scene Avatar 业务场景线程切换
    private fun doAvatarActionBackground(avatarId: Long, unit: (() -> Unit)) {
        doBackgroundAction {
            if (avatarBackgroundSet.contains(avatarId)) {
                unit()
                return@doBackgroundAction
            }
            FULogger.w(
                    TAG,
                    "doAvatarActionBackground failed  avatarBackgroundSet not contains avatarId=$avatarId"
            )
        }
    }


    private fun doAvatarActionBackgroundGL(
            avatarId: Long,
            needBackgroundThread: Boolean = true,
            unit: ((id: Int) -> Unit)
    ) {
        if (needBackgroundThread) {
            doBackgroundAction {
                if (avatarBackgroundSet.contains(avatarId)) {
                    doAvatarActionGL(avatarId, unit)
                    return@doBackgroundAction
                }
                FULogger.w(
                        TAG,
                        "doAvatarActionBackgroundGL failed  avatarBackgroundSet not contains avatarId=$avatarId"
                )
            }
        } else {
            doAvatarActionGL(avatarId, unit)
        }
    }


    private fun doAvatarActionGL(avatarId: Long, unit: ((id: Int) -> Unit)) {
        doGLThreadAction {
            val id = avatarIdMap[avatarId]
            id?.let {
                unit(it)
                return@doGLThreadAction
            }
            FULogger.w(TAG, "doAvatarActionGL failed  avatarId=$avatarId    id=$id")
        }
    }

    private fun doSceneActionBackground(sceneId: Long, unit: (() -> Unit)) {
        doBackgroundAction {
            if (sceneBackgroundSet.contains(sceneId)) {
                unit()
                return@doBackgroundAction
            }
            FULogger.w(
                    TAG,
                    "doSceneActionBackground failed  sceneBackgroundSet not contains sceneId=$sceneId"
            )
        }
    }


    private fun doSceneActionBackgroundGL(
            sceneId: Long,
            needBackgroundThread: Boolean = true,
            unit: ((id: Int) -> Unit)
    ) {
        if (needBackgroundThread) {
            doBackgroundAction {
                if (sceneBackgroundSet.contains(sceneId)) {
                    doSceneActionGL(sceneId, unit)
                    return@doBackgroundAction
                }
                FULogger.w(
                        TAG,
                        "doSceneActionBackgroundGL failed  sceneBackgroundSet not contains sceneId=$sceneId"
                )
            }
        } else {
            doSceneActionGL(sceneId, unit)
        }
    }

    private fun doSceneActionGL(sceneId: Long, unit: ((id: Int) -> Unit)) {
        doGLThreadAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                unit(it)
                return@doGLThreadAction
            }
            FULogger.w(TAG, "doSceneActionGL failed  sceneId=$sceneId    id=$id")
        }
    }


    //endregion region Scene Avatar 业务场景线程切换

    //region 业务接口实现
    /**
     * 加载Scene场景
     * @param sceneData FUASceneData
     */
    internal fun doAddAvatarScene(sceneData: FUASceneData, listener: OnSceneListener?) {
        doBackgroundAction {
            loadControllerBundle(sceneData)
            if (mControllerBundleHandle <= 0) {
                return@doBackgroundAction
            }
            val compareData = AvatarCompareData()
            addScene(sceneData, compareData)
            applyCompData(compareData, sceneData.id, listener)
        }
    }

    /**
     * 加载Scene场景
     * @param sceneData FUASceneData
     */
    internal fun doAddAvatarSceneGL(sceneData: FUASceneData, listener: OnSceneListener?) {
        doGLThreadAction {
            loadControllerBundle(sceneData)
            if (mControllerBundleHandle <= 0) {
                return@doGLThreadAction
            }
            val compareData = AvatarCompareData()
            addScene(sceneData, compareData)
            applyCompData(compareData, sceneData.id, listener)
        }
    }

    /**
     * 移除Scene场景
     * @param sceneData FUASceneData
     */
    internal fun doRemoveAvatarScene(sceneData: FUASceneData) {
        doBackgroundAction {
            val compareData = AvatarCompareData()
            removeScene(sceneData, compareData)
            applyCompData(compareData)
        }
    }

    /**
     * 替换场景
     * @param oldAvatar FUASceneData
     * @param newAvatar FUASceneData
     */
    internal fun doReplaceAvatarScene(oldAvatar: FUASceneData, newAvatar: FUASceneData) {
        doBackgroundAction {
            val compareData = AvatarCompareData()
            replaceScene(oldAvatar, newAvatar, compareData)
            applyCompData(compareData)
        }
    }


    /**
     * 新增 Avatar
     * @param sceneId Long
     * @param avatar FUAAvatarData
     */
    internal fun doAddAvatar(sceneId: Long, avatar: FUAAvatarData) {
        doSceneActionBackground(sceneId) {
            val compareData = AvatarCompareData()
            addAvatar(sceneId, avatar, compareData)
            applyCompData(compareData)

        }
    }

    /**
     * 新增 Avatar
     * @param sceneId Long
     * @param avatar FUAAvatarData
     */
    internal fun doAddAvatarGL(sceneId: Long, avatar: FUAAvatarData) {
        doSceneActionGL(sceneId) {
            val compareData = AvatarCompareData()
            addAvatar(sceneId, avatar, compareData)
            applyCompData(compareData)
        }
    }


    /**
     * 移除 Avatar
     * @param sceneId Long
     * @param avatar FUAAvatarData
     */
    internal fun doRemoveAvatar(sceneId: Long, avatar: FUAAvatarData) {
        doSceneActionBackground(sceneId) {
            val compareData = AvatarCompareData()
            removeAvatar(sceneId, avatar, compareData)
            applyCompData(compareData)
        }
    }


    /**
     * 移除 Avatar
     * @param sceneId Long
     * @param avatar FUAAvatarData
     */
    internal fun doRemoveAvatarGL(sceneId: Long, avatar: FUAAvatarData) {
        doSceneActionGL(sceneId) {
            val compareData = AvatarCompareData()
            removeAvatar(sceneId, avatar, compareData)
            applyCompData(compareData)
        }
    }

    /**
     * 替换 Avatar
     * @param sceneId Long
     * @param oldAvatar FUAAvatarData
     * @param newAvatar FUAAvatarData
     */
    internal fun doReplaceAvatar(
            sceneId: Long,
            oldAvatar: FUAAvatarData,
            newAvatar: FUAAvatarData
    ) {
        doSceneActionBackground(sceneId) {
            val compareData = AvatarCompareData()
            replaceAvatar(oldAvatar, newAvatar, compareData)
            applyCompData(compareData)
        }
    }


    /**
     * 替换 Avatar
     * @param sceneId Long
     * @param oldAvatar FUAAvatarData
     * @param newAvatar FUAAvatarData
     */
    internal fun doReplaceAvatarGL(
            sceneId: Long,
            oldAvatar: FUAAvatarData,
            newAvatar: FUAAvatarData
    ) {
        doSceneActionGL(sceneId) {
            val compareData = AvatarCompareData()
            replaceAvatar(oldAvatar, newAvatar, compareData)
            applyCompData(compareData)
        }
    }

    override fun release(unit: (() -> Unit)?) {
        super.release {
            releaseAll()
        }
    }

    /**
     * 释放所有资源
     */
    private fun releaseAll() {
        avatarIdMap.forEach { (_, id) ->
            SDKController.destroyInstance(id)
        }
        avatarIdMap.clear()
        sceneIdMap.forEach { (_, id) ->
            SDKController.destroyScene(id)
        }
        sceneIdMap.clear()
        handleReferenceCountMap.forEach { (path, _) ->
            destroyBundle(path)
        }
        handleReferenceCountMap.clear()
    }


    //endregion 业务接口实现

    //region 通用比对功能实现


    /**
     * 比对数据生效
     */
    private fun applyCompData(
            compareData: AvatarCompareData,
            sceneId: Long = 0,
            listener: OnSceneListener? = null
    ) {
        //0.更新avatar scene id缓存
        updateBackgroundSet(compareData)
        //1.初始化加载新道具
        applyCreateBundle(compareData)
        doGLThreadAction {
            //2.解绑Avatar道具
            applyRemoveAvatarBundle(compareData)
            //3.解绑Avatar
            applyRemoveAvatar(compareData)
            //4.解绑Scene道具
            applyRemoveSceneBundle(compareData)
            //5.移除场景
            applyRemoveScene(compareData)
            //6.增加场景并设置参数
            applyAddScene(compareData)
            //7.绑定Scene对象道具
            applyAddSceneBundle(compareData)
            //8.增加Avatar对象并绑定到Scene
            applyAddAvatar(compareData)
            //9.Scene设置参数
            applySceneParams(compareData)
            //10.绑定Avatar道具并设置参数
            applyAddAvatarBundle(compareData)
            //9.Scene设置参数
            applyAvatarParams(compareData)
            //11.销毁道具
            applyDestroyBundle(compareData)
            //12.完成回调
            listener?.onSceneLoaded(sceneId)
        }
    }


    /**
     * background 更新缓存信息
     * @param compareData AvatarCompareData
     */
    private fun updateBackgroundSet(compareData: AvatarCompareData) {
        compareData.sceneRemoveList.forEach { scene ->
            sceneBackgroundSet.remove(scene.id)
        }
        compareData.sceneAddList.forEach { scene ->
            sceneBackgroundSet.add(scene.id)
        }
        compareData.sceneUnbindAvatarMap.forEach { (_, avatars) ->
            avatars.forEach {
                avatarBackgroundSet.remove(it)
            }
        }
        compareData.sceneBindAvatarMap.forEach { (_, avatars) ->
            avatars.forEach {
                avatarBackgroundSet.add(it)
            }
        }
        compareData.sceneReplaceAvatarMap.forEach { (target, release) ->
            avatarBackgroundSet.remove(target)
            avatarBackgroundSet.add(release)

        }
        compareData.bundleAddMap.forEach { (path, count) ->
            addReferenceCount(handleReferenceCountMap, path, count)
        }
    }


    /**
     * 加载新道具
     */
    private fun applyCreateBundle(compareData: AvatarCompareData) {
        val countDownLatch = CountDownLatch(compareData.bundleAddMap.size)
        compareData.bundleAddMap.forEach { (path, _) ->
            mCachedThreadPool.execute {
                createBundle(path)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
    }


    /**
     * 移除Avatar道具
     */
    private fun applyRemoveAvatarBundle(compareData: AvatarCompareData) {
        compareData.avatarUnbindHandleMap.forEach { (id, bundles) ->
            if (avatarIdMap.containsKey(id)) {
                val avatarId = avatarIdMap[id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    val handle = mBundleManager.getBundleHandle(it)
                    removeReferenceCount(handleReferenceCountMap, it)
                    if (handle > 0) {
                        items.add(handle)
                    }
                }
                SDKController.unbindItemsFromInstance(avatarId, items.toIntArray())
            }
        }

    }

    /**
     * 解绑Avatar
     */
    private fun applyRemoveAvatar(compareData: AvatarCompareData) {
        compareData.sceneUnbindAvatarMap.forEach { (id, avatarList) ->
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
        compareData.sceneReplaceAvatarMap.forEach { (oldId, replaceId) ->
            avatarIdMap[oldId]?.let {
                avatarIdMap[replaceId] = it
                avatarIdMap.remove(oldId)
            }
        }
    }

    /**
     * 解绑场景道具
     */
    private fun applyRemoveSceneBundle(compareData: AvatarCompareData) {
        compareData.sceneUnbindHandleMap.forEach { (id, bundles) ->
            if (sceneIdMap.containsKey(id)) {
                val sceneId = sceneIdMap[id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    val handle = mBundleManager.getBundleHandle(it)
                    removeReferenceCount(handleReferenceCountMap, it)
                    if (handle > 0) {
                        items.add(handle)
                    }
                }
                SDKController.unbindItemsFromScene(sceneId, items.toIntArray())
            }
        }
    }

    /**
     * 移除Scene
     */
    private fun applyRemoveScene(compareData: AvatarCompareData) {
        compareData.sceneRemoveList.forEach {
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
    private fun applyAddScene(compareData: AvatarCompareData) {
        compareData.sceneAddList.forEach {
            val sceneId = SDKController.createScene()
            if (sceneId > 0) {
                sceneIdMap[it.id] = sceneId
            }

        }
    }

    /**
     *Scene绑定道具
     */
    private fun applyAddSceneBundle(compareData: AvatarCompareData) {
        compareData.sceneBindHandleMap.forEach { (scene, bundles) ->
            if (sceneIdMap.containsKey(scene.id)) {
                val sceneId = sceneIdMap[scene.id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    val handle = mBundleManager.getBundleHandle(it)
                    if (handle > 0) {
                        items.add(handle)
                    }
                }
                SDKController.bindItemsToScene(sceneId, items.toIntArray())
            }

        }
    }

    /**
     * 增加Avatar对象
     */
    private fun applyAddAvatar(compareData: AvatarCompareData) {
        compareData.sceneBindAvatarMap.forEach { (id, avatarList) ->
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
     * 增加Avatar对象
     */
    private fun applySceneParams(compareData: AvatarCompareData) {
        compareData.sceneAddList.forEach { scene ->
            scene.params.forEach { (_, unit) ->
                unit.invoke()
            }
        }
    }

    /**
     * 绑定Avatar道具并设置参数
     */
    private fun applyAddAvatarBundle(compareData: AvatarCompareData) {
        compareData.avatarBindHandleMap.forEach { (avatar, bundles) ->
            if (avatarIdMap.containsKey(avatar.id)) {
                val avatarId = avatarIdMap[avatar.id]!!
                val items = ArrayList<Int>()
                bundles.forEach {
                    val handle = mBundleManager.getBundleHandle(it)
                    if (handle > 0) {
                        items.add(handle)
                    }
                }
                if (items.isNotEmpty()) {
                    SDKController.bindItemsToInstance(avatarId, items.toIntArray())
                }
            }
        }
    }


    /**
     * Avatar属性绑定
     */
    private fun applyAvatarParams(compareData: AvatarCompareData) {
        compareData.avatarParamsMap.forEach { (id, params) ->
            avatarIdMap[id]?.let {
                params.forEach { (_, unit) ->
                    unit.invoke()
                }
            }
        }
    }


    /**
     * 更新引用计数
     */
    private fun applyDestroyBundle(compareData: AvatarCompareData) {
        compareData.bundleRemoveMap.forEach { (path, _) ->
            destroyBundle(path)
        }
    }


//endregion 通用比对功能实现
//region 同步实现道具处理

    /**
     * 同步加载道具实现
     * @param bundle FUBundleData
     */
    fun preloadBundleUnThread(bundle: FUBundleData) {
        createBundle(bundle.path)
    }

    /**
     * 同步移除道具实现
     * @param path String
     */
    fun removePreLoadedBundle(path: String) {
        doBackgroundAction {
            doGLThreadAction {
                destroyBundle(path)
            }
        }
    }


//endregion 道具预加载实现

//region 其他业务接口
    /**
     * 加载句柄
     * @param bundle BundleHandleData
     */
    fun loadSceneItemBundle(sceneId: Long, bundle: FUBundleData) {
        doSceneActionBackground(sceneId) {
            val handle = createBundle(bundle.path)
            if (handle == 0) {
                return@doSceneActionBackground
            }
            addReferenceCount(handleReferenceCountMap, bundle.path)
            doSceneActionGL(sceneId) {
                SDKController.bindItemsToScene(it, intArrayOf(handle))
            }
        }

    }

    /**
     * 加载句柄
     * @param bundle BundleHandleData
     */
    fun loadSceneItemBundleGL(sceneId: Long, bundle: FUBundleData) {
        addReferenceCount(handleReferenceCountMap, bundle.path)
        doSceneActionGL(sceneId) {
            val handle = createBundle(bundle.path)
            if (handle == 0) {
                return@doSceneActionGL
            }
            SDKController.bindItemsToScene(it, intArrayOf(handle))
        }
    }


    /**
     * 移除句柄
     * @param bundle BundleHandleData
     */
    fun removeSceneItemBundle(sceneId: Long, bundle: FUBundleData) {
        doSceneActionBackgroundGL(sceneId) { id ->
            val handle = mBundleManager.getBundleHandle(bundle.path)
            if (handle > 0) {
                removeReferenceCount(handleReferenceCountMap, bundle.path)
                SDKController.unbindItemsFromScene(id, intArrayOf(handle))
            }
            destroyBundle(bundle.path)
        }
    }


    /**
     * 移除句柄
     * @param bundle BundleHandleData
     */
    fun removeSceneItemBundleGL(sceneId: Long, bundle: FUBundleData) {
        doSceneActionGL(sceneId) { id ->
            val handle = mBundleManager.getBundleHandle(bundle.path)
            if (handle > 0) {
                removeReferenceCount(handleReferenceCountMap, bundle.path)
                SDKController.unbindItemsFromScene(id, intArrayOf(handle))
            }
            destroyBundle(bundle.path)
        }
    }


    /**
     * 替换句柄
     * @param sceneId Long
     * @param oldBundle FUBundleData
     * @param newBundle FUBundleData
     */
    fun replaceSceneItemBundle(sceneId: Long, oldBundle: FUBundleData, newBundle: FUBundleData) {
        doSceneActionBackground(sceneId) {
            addReferenceCount(handleReferenceCountMap, newBundle.path)
            createBundle(newBundle.path)
            doSceneActionGL(sceneId) { id ->
                var handle = mBundleManager.getBundleHandle(oldBundle.path)
                if (handle > 0) {
                    removeReferenceCount(handleReferenceCountMap, oldBundle.path)
                    SDKController.unbindItemsFromScene(id, intArrayOf(handle))
                }
                handle = mBundleManager.getBundleHandle(newBundle.path)
                if (handle > 0) {
                    SDKController.bindItemsToScene(id, intArrayOf(handle))
                }
                destroyBundle(oldBundle.path)
            }
        }
    }


    /**
     * 替换句柄
     * @param sceneId Long
     * @param oldBundle FUBundleData
     * @param newBundle FUBundleData
     */
    fun replaceSceneItemBundleGL(sceneId: Long, oldBundle: FUBundleData, newBundle: FUBundleData) {

        addReferenceCount(handleReferenceCountMap, newBundle.path)
        doSceneActionGL(sceneId) { id ->
            createBundle(newBundle.path)
            var handle = mBundleManager.getBundleHandle(oldBundle.path)
            if (handle > 0) {
                removeReferenceCount(handleReferenceCountMap, oldBundle.path)
                SDKController.unbindItemsFromScene(id, intArrayOf(handle))
            }
            handle = mBundleManager.getBundleHandle(newBundle.path)
            if (handle > 0) {
                SDKController.bindItemsToScene(id, intArrayOf(handle))
            }
            destroyBundle(oldBundle.path)
        }
    }


    /**
     * 加载句柄
     * @param bundle BundleHandleData
     */
    fun loadAvatarItemBundle(avatarId: Long, bundle: FUBundleData) {
        doAvatarActionBackground(avatarId) {
            addReferenceCount(handleReferenceCountMap, bundle.path)
            createBundle(bundle.path)
            doAvatarActionGL(avatarId) {
                val handle = mBundleManager.getBundleHandle(bundle.path)
                if (handle > 0) {
                    SDKController.bindItemsToInstance(it, intArrayOf(handle))
                }

            }
        }
    }


    /**
     * 加载句柄
     * @param bundle BundleHandleData
     */
    fun loadAvatarItemBundleGL(avatarId: Long, bundle: FUBundleData) {
        addReferenceCount(handleReferenceCountMap, bundle.path)
        doAvatarActionGL(avatarId) {
            createBundle(bundle.path)
            val handle = mBundleManager.getBundleHandle(bundle.path)
            if (handle > 0) {
                SDKController.bindItemsToInstance(it, intArrayOf(handle))
            }
        }
    }


    /**
     * 移除句柄
     * @param bundle BundleHandleData
     */
    fun removeAvatarItemBundle(avatarId: Long, bundle: FUBundleData) {
        doAvatarActionBackgroundGL(avatarId) { id ->
            val handle = mBundleManager.getBundleHandle(bundle.path)
            if (handle > 0) {
                removeReferenceCount(handleReferenceCountMap, bundle.path)
                SDKController.unbindItemsFromInstance(id, intArrayOf(handle))
            }
            destroyBundle(bundle.path)
        }
    }

    /**
     * 移除句柄
     * @param bundles BundleHandleData 多道具列表
     */
    fun removeAvatarItemBundle(avatarId: Long, bundles: ArrayList<FUBundleData>) {
        doAvatarActionBackgroundGL(avatarId) { id ->
            val items = ArrayList<Int>()
            bundles.forEach {
                val handle = mBundleManager.getBundleHandle(it.path)
                if (handle > 0) {
                    removeReferenceCount(handleReferenceCountMap, it.path)
                    items.add(handle)

                }
            }
            if (items.isNotEmpty()) {
                SDKController.unbindItemsFromInstance(id, items.toIntArray())
            }
            bundles.forEach {
                destroyBundle(it.path)
            }
        }
    }


    /**
     * 替换句柄
     * @param avatarId Long
     * @param oldBundle FUBundleData
     * @param newBundle FUBundleData
     */
    fun replaceAvatarItemBundle(avatarId: Long, oldBundle: FUBundleData, newBundle: FUBundleData) {
        doAvatarActionBackground(avatarId) {
            addReferenceCount(handleReferenceCountMap, newBundle.path)
            createBundle(newBundle.path)
            doAvatarActionGL(avatarId) { id ->
                var handle = mBundleManager.getBundleHandle(oldBundle.path)
                if (handle > 0) {
                    removeReferenceCount(handleReferenceCountMap, oldBundle.path)
                    SDKController.unbindItemsFromInstance(id, intArrayOf(handle))
                }
                handle = mBundleManager.getBundleHandle(newBundle.path)
                if (handle > 0) {
                    SDKController.bindItemsToInstance(id, intArrayOf(handle))
                }
                destroyBundle(oldBundle.path)
            }
        }
    }

    /**
     * 替换句柄
     * @param avatarId Long
     * @param oldBundles ArrayList<FUBundleData>
     * @param newBundles ArrayList<FUBundleData>
     */
    fun replaceAvatarItemBundle(
            avatarId: Long,
            oldBundles: ArrayList<FUBundleData>,
            newBundles: ArrayList<FUBundleData>
    ) {
        doAvatarActionBackground(avatarId) {
            newBundles.forEach {
                addReferenceCount(handleReferenceCountMap, it.path)
            }
            newBundles.forEach {
                createBundle(it.path)
            }
            doAvatarActionGL(avatarId) { id ->
                val unbindHandles = ArrayList<Int>()
                oldBundles.forEach {
                    val handle = mBundleManager.getBundleHandle(it.path)
                    if (handle > 0) {
                        removeReferenceCount(handleReferenceCountMap, it.path)
                        unbindHandles.add(handle)
                    }
                }
                if (oldBundles.size > 0) {
                    SDKController.unbindItemsFromInstance(id, unbindHandles.toIntArray())
                }

                val bindHandles = ArrayList<Int>()
                newBundles.forEach {
                    val handle = mBundleManager.getBundleHandle(it.path)
                    if (handle > 0) {
                        bindHandles.add(handle)
                    }
                }
                if (bindHandles.isNotEmpty()) {
                    SDKController.bindItemsToInstance(id, bindHandles.toIntArray())
                }
                oldBundles.forEach {
                    destroyBundle(it.path)
                }
            }
        }
    }

    /**
     * 替换句柄
     * @param avatarId Long
     * @param oldBundles ArrayList<FUBundleData>
     * @param newBundles ArrayList<FUBundleData>
     */
    fun replaceAvatarItemBundleGL(
            avatarId: Long,
            oldBundles: ArrayList<FUBundleData>,
            newBundles: ArrayList<FUBundleData>
    ) {
        newBundles.forEach {
            addReferenceCount(handleReferenceCountMap, it.path)
        }
        doAvatarActionGL(avatarId) { id ->
            newBundles.forEach {
                createBundle(it.path)
            }
            val unbindHandles = ArrayList<Int>()
            oldBundles.forEach {
                val handle = mBundleManager.getBundleHandle(it.path)
                if (handle > 0) {
                    removeReferenceCount(handleReferenceCountMap, it.path)
                    unbindHandles.add(handle)
                }
            }
            if (oldBundles.size > 0) {
                SDKController.unbindItemsFromInstance(id, unbindHandles.toIntArray())
            }

            val bindHandles = ArrayList<Int>()
            newBundles.forEach {
                val handle = mBundleManager.getBundleHandle(it.path)
                if (handle > 0) {
                    bindHandles.add(handle)
                }
            }
            if (bindHandles.isNotEmpty()) {
                SDKController.bindItemsToInstance(id, bindHandles.toIntArray())
            }
            oldBundles.forEach {
                destroyBundle(it.path)
            }
        }
    }


    //endregion 业务接口
    fun setCurrentScene(sceneId: Long, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setCurrentScene(it)
        }
    }

    //region 背景
    fun enableBackgroundColor(
            sceneId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableBackgroundColor(it, enable)
        }
    }

    fun setBackgroundColor(
            sceneId: Long,
            color: FUColorRGBData,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setBackgroundColor(
                    it,
                    color.red.toInt(),
                    color.green.toInt(),
                    color.blue.toInt(),
                    color.alpha.toInt()
            )
        }
    }

    //endregion 背景


//region SceneCamera

    fun enableRenderCamera(sceneId: Long, enable: Boolean, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableRenderCamera(it, enable)
        }
    }


    fun setProjectionMatrixFov(sceneId: Long, fov: Float, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setProjectionMatrixFov(it, fov)
        }
    }

    fun setProjectionMatrixOrthoSize(
            sceneId: Long,
            fov: Float,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setProjectionMatrixOrthoSize(it, fov)
        }
    }

    fun setProjectionMatrixZnear(sceneId: Long, near: Float, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setProjectionMatrixZnear(it, near)
        }
    }

    fun setProjectionMatrixZfar(sceneId: Long, far: Float, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setProjectionMatrixZfar(it, far)
        }
    }

//endregion SceneCamera

//region CameraAnimation

    /**
     * 相机动画开关
     * @param sceneId Long
     * @param enable Boolean 是否开启
     * @param needBackgroundThread Boolean
     */
    fun enableCameraAnimation(
            sceneId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableCameraAnimation(it, enable)
        }
    }

    /**
     * 重新开始播放相机动画
     * @param sceneId Long
     * @param needBackgroundThread Boolean
     */
    fun startCameraAnimation(sceneId: Long, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.startCameraAnimation(it)
        }
    }

    /**
     * 暂停播放相机动画
     * @param sceneId Long
     * @param needBackgroundThread Boolean
     */
    fun pauseCameraAnimation(sceneId: Long, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.pauseCameraAnimation(it)
        }
    }

    /**
     * 重置相机动画
     * @param sceneId Long
     * @param needBackgroundThread Boolean
     */
    fun resetCameraAnimation(sceneId: Long, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.resetCameraAnimation(it)
        }
    }


    /**
     * 设置相机动画过渡时间
     * @param sceneId Long
     * @param time Float
     * @param needBackgroundThread Boolean
     */
    fun setCameraAnimationTransitionTime(
            sceneId: Long,
            time: Float,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setCameraAnimationTransitionTime(it, time)
        }
    }

    /**
     * 是否开启帧间插值
     * @param sceneId Long
     * @param enable Boolean
     * @param needBackgroundThread Boolean
     */
    fun enableCameraAnimationInternalLerp(
            sceneId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableCameraAnimationInternalLerp(it, enable)
        }
    }

    /**
     * 获取动画桢数
     * @param sceneId Long
     * @param bundle FUBundleData
     * @return Int
     */
    fun getCameraAnimationFrameNumber(sceneId: Long, bundle: FUBundleData): Int {
        val id = sceneIdMap[sceneId]
        val handle = mBundleManager.getBundleHandle(bundle.path)
        if (id != null && handle > 0) {
            return SDKController.getCameraAnimationFrameNumber(id, handle)
        }
        return -1
    }

    /**
     * 获取动画进度
     * @param sceneId Long
     * @param bundle FUBundleData
     * @return Float
     */
    fun getCameraAnimationProgress(sceneId: Long, bundle: FUBundleData): Float {
        val id = sceneIdMap[sceneId]
        val handle = mBundleManager.getBundleHandle(bundle.path)
        if (id != null && handle > 0) {
            return SDKController.getCameraAnimationProgress(id, handle)
        }
        return -1f
    }

    /**
     * 获取当前动画过渡进度
     * @param sceneId Long
     * @return Float
     */
    fun getCameraAnimationTransitionProgress(sceneId: Long): Float {
        val id = sceneIdMap[sceneId]
        if (id != null) {
            return SDKController.getCameraAnimationTransitionProgress(id)
        }
        return -1f
    }


    /**
     * 加载动画道具，并是否开启播放
     * @param sceneId Long
     * @param animationData FUAnimationData 动画模型
     * @param isLoop Boolean?  是否播放  null：不播放 true：循环播放 false：不循环播放
     * @param needBackgroundThread Boolean 是否需要background线程队列
     */
    fun loadCameraAnimationData(sceneId: Long, animationData: FUAnimationData, isLoop: Boolean?, needBackgroundThread: Boolean) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            val propList = ArrayList<FUBundleData>()
            val animationList = ArrayList<FUBundleData>()
            analyzeAnimationData(animationData, propList, animationList)
            /*创建*/
            doCreateAnimationBundle(propList, animationList)
            doSceneActionGL(sceneId) { id ->
                doAddCameraAnimation(id, propList, animationList, isLoop)
            }
        }
    }


    /**
     * 移除动画道具
     * @param animationData FUAnimationData 动画模型
     * @param needBackgroundThread Boolean 是否需要background线程队列
     */
    fun removeCameraAnimationData(sceneId: Long, animationData: FUAnimationData, needBackgroundThread: Boolean) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) { id ->
            doRemoveCameraAnimation(id, animationData)
        }
    }

    /**
     * 替换动画道具
     * @param sceneId Long Long
     * @param animationData FUAnimationData 原始动画模型
     * @param targetAnimationData FUGroupAnimationData 目标动画模型
     * @param needBackgroundThread Boolean 是否需要background线程队列
     */
    fun replaceCameraAnimationData(sceneId: Long, animationData: FUAnimationData, targetAnimationData: FUAnimationData, needBackgroundThread: Boolean) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            val propList = ArrayList<FUBundleData>()
            val animationList = ArrayList<FUBundleData>()
            analyzeAnimationData(targetAnimationData, propList, animationList)
            /*创建*/
            doCreateAnimationBundle(propList, animationList)
            doSceneActionGL(sceneId) { id ->
                /*移除*/
                doRemoveCameraAnimation(id, animationData)
                /*添加*/
                doAddCameraAnimation(id, propList, animationList)
            }
        }
    }

    /**
     * 播放相机动画
     * @param sceneId Long  模型Id
     * @param animationData FUAnimationData 动画数据模型
     * @param isLoop Boolean 是否循环
     * @param needBackgroundThread Boolean 是否需要background线程队列
     */
    fun playCameraAnimation(
            sceneId: Long,
            animationData: FUAnimationData,
            isLoop: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(sceneId, needBackgroundThread) { id ->
            val propList = ArrayList<FUBundleData>()
            val animationList = ArrayList<FUBundleData>()
            analyzeAnimationData(animationData, propList, animationList)
            animationList.forEach {
                val handle = mBundleManager.getBundleHandle(it.path)
                if (handle > 0) {
                    if (!isLoop) {
                        SDKController.playCameraAnimationOnce(id, handle)
                    } else {
                        SDKController.playCameraAnimation(id, handle)
                    }
                }
            }
        }
    }



    /**
     * 移除动画
     * @param sceneId Int
     * @param animationData FUAnimationData 动画数据模型
     */
    private fun doRemoveCameraAnimation(sceneId: Int, animationData: FUAnimationData) {
        val removeBundleList = ArrayList<FUBundleData>()
        analyzeAnimationData(animationData, removeBundleList, removeBundleList)
        val removeHandles = ArrayList<Int>()
        removeBundleList.forEach {
            val handle = mBundleManager.getBundleHandle(it.path)
            if (handle > 0) {
                removeReferenceCount(handleReferenceCountMap, it.path)
                removeHandles.add(handle)
            }
        }
        if (removeHandles.isNotEmpty()) {
            SDKController.unbindItemsFromScene(sceneId, removeHandles.toIntArray())
        }
        removeBundleList.forEach {
            destroyBundle(it.path)
        }
    }


    /**
     * 配置动画
     * @param instanceId Int
     * @param propList ArrayList<FUBundleData> 道具列表
     * @param animationList ArrayList<FUBundleData> 动画子列表
     * @param isLoop Boolean？ 是否循环播放
     */
    private fun doAddCameraAnimation(instanceId: Int, propList: ArrayList<FUBundleData>, animationList: ArrayList<FUBundleData>, isLoop: Boolean? = null) {
        val handles = ArrayList<Int>()
        val animationHandles = ArrayList<Int>()
        animationList.forEach {
            val handle = mBundleManager.getBundleHandle(it.path)
            if (handle > 0) {
                animationHandles.add(handle)
                handles.add(handle)
            }
        }
        propList.forEach {
            val handle = mBundleManager.getBundleHandle(it.path)
            if (handle > 0) {
                handles.add(handle)
            }
        }
        if (handles.isNotEmpty()) {
            SDKController.bindItemsToScene(instanceId, handles.toIntArray())
        }
        isLoop?.let {
            animationHandles.forEach { anim ->
                if (!isLoop) {
                    SDKController.playCameraAnimationOnce(instanceId, anim)
                } else {
                    SDKController.playCameraAnimation(instanceId, anim)
                }
            }
        }
    }


//endregion CameraAnimation

    //region SceneShadow
    fun enableShadow(sceneId: Long, enable: Boolean, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableShadow(it, enable)
        }
    }

    fun setInstanceShadowPCFLevel(sceneId: Long, level: Int, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.setInstanceShadowPCFLevel(it, level)
        }
    }
//endregion SceneShadow
//region Lighting

    fun enableLowQualityLighting(
            sceneId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableLowQualityLighting(it, enable)
        }
    }

    //endregion Lighting
//region AIProcess
    fun enableARMode(sceneId: Long, enable: Boolean, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableARMode(it, enable)
        }
    }
//endregion AIProcess
    /**
     * 人体驱动开关
     * @param sceneId Int
     * @param enable Boolean
     */
    fun enableHumanProcessor(sceneId: Long, enable: Boolean, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableHumanProcessor(it, enable)
        }
    }

    /**
     * 人体驱动开关
     * @param sceneId Int
     * @param enable Boolean
     */
    fun enableFaceProcessor(sceneId: Long, enable: Boolean, needBackgroundThread: Boolean = true) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.enableFaceProcessor(it, enable)
        }
    }


    /**
     * 人体驱动类型
     * @param sceneId Int
     * @param isFull Boolean 是否是全身
     */
    fun humanProcessorSet3DScene(
            sceneId: Long,
            isFull: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doSceneActionBackgroundGL(sceneId, needBackgroundThread) {
            SDKController.humanProcessorSet3DScene(it, isFull)
        }
    }

//endregion Scene接口
//region Avatar接口


    fun setInstanceBodyVisibleList(avatarId: Long, visibleList: IntArray, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceBodyVisibleList(it, visibleList)
        }
    }

    fun setInstanceBodyInvisibleList(avatarId: Long, visibleList: IntArray, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.fuSetInstanceBodyInvisibleList(it, visibleList)
        }
    }

    fun getInstanceFaceVertexScreenCoordinate(avatarId: Long, index: Int, rect: FloatArray) {
        val id = avatarIdMap[avatarId]
        if (id != null) {
            SDKController.getInstanceFaceVertexScreenCoordinate(id, index, rect)
        }
    }

//region TransForm

    /**
     * 设置角色点位
     * @param avatarId Long
     * @param x Float
     * @param y Float
     * @param z Float
     */
    fun setInstanceTargetPosition(
            avatarId: Long,
            position: FUCoordinate3DData,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceTargetPosition(
                    it,
                    position.positionX.toFloat(),
                    position.positionY.toFloat(),
                    position.positionZ.toFloat()
            )
        }
    }


    fun setInstanceTargetAngle(avatarId: Long, value: Float, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceTargetAngle(it, value)
        }
    }

    fun setInstanceRotDelta(avatarId: Long, value: Float, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceRotDelta(it, value)
        }
    }

    fun setInstanceScaleDelta(avatarId: Long, value: Float, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceScaleDelta(it, value)
        }
    }

    fun setInstanceTranslateDelta(
            avatarId: Long,
            value: Float,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceTranslateDelta(it, value)
        }
    }

//endregion TransForm

//region 动画

    /**
     * 播放Avatar动画
     * @param avatarId Long  模型Id
     * @param animationData FUAnimationData 动画数据模型
     * @param isLoop Boolean 是否循环
     * @param needBackgroundThread Boolean 是否需要过子线程
     */
    fun playInstanceAnimation(
            avatarId: Long,
            animationData: FUAnimationData,
            isLoop: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) { id ->
            val animationList = ArrayList<FUBundleData>()
            if (animationData is FUGroupAnimationData) {
                animationList.add(animationData.animation)
                animationList.addAll(animationData.subAnimations)
            } else {
                animationList.add(animationData.animation)
            }
            animationList.forEach {
                val handle = mBundleManager.getBundleHandle(it.path)
                if (handle > 0) {
                    if (!isLoop) {
                        SDKController.playInstanceAnimationOnce(id, handle)
                    } else {
                        SDKController.playInstanceAnimation(id, handle)
                    }
                }
            }
        }
    }

    /**
     * 开始当前动画
     * @param avatarId Long
     * @param needBackgroundThread Boolean
     */
    fun startInstanceAnimation(avatarId: Long, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.startInstanceAnimation(it)
        }
    }

    /**
     * 暂停当前动画
     * @param avatarId Long
     * @param needBackgroundThread Boolean
     */
    fun pauseInstanceAnimation(avatarId: Long, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.pauseInstanceAnimation(it)
        }
    }

    /**
     * 停止当前动画
     * @param avatarId Long
     * @param needBackgroundThread Boolean
     */
    fun stopInstanceAnimation(avatarId: Long, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.stopInstanceAnimation(it)
        }
    }

    /**
     * 重置当前动画
     * @param avatarId Long
     * @param needBackgroundThread Boolean
     */
    fun resetInstanceAnimation(avatarId: Long, needBackgroundThread: Boolean = true) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.resetInstanceAnimation(it)
        }
    }

    /**
     * 设置动画过渡时间
     * @param avatarId Long
     * @param time Float
     * @param needBackgroundThread Boolean
     */
    fun setInstanceAnimationTransitionTime(
            avatarId: Long,
            time: Float,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceAnimationTransitionTime(it, time)
        }
    }

    /**
     * 是否开启动画差值
     * @param avatarId Long
     * @param enable Boolean
     * @param needBackgroundThread Boolean
     */
    fun enableInstanceAnimationInternalLerp(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceAnimationInternalLerp(it, enable)
        }
    }

    /**
     * 获取动画帧数
     * @param avatarId Long
     * @param bundle FUBundleData
     * @return Int
     */
    fun getInstanceAnimationFrameNumber(avatarId: Long, bundle: FUBundleData): Int {
        val id = avatarIdMap[avatarId]
        val handle = mBundleManager.getBundleHandle(bundle.path)
        if (id != null && handle > 0) {
            return SDKController.getInstanceAnimationFrameNumber(id, handle)
        }
        return -1
    }

    /**
     * 获取动画进度
     * @param avatarId Long
     * @param bundle FUBundleData
     * @return Float
     */
    fun getInstanceAnimationProgress(avatarId: Long, bundle: FUBundleData): Float {
        val id = avatarIdMap[avatarId]
        val handle = mBundleManager.getBundleHandle(bundle.path)
        if (id != null && handle > 0) {
            return SDKController.getInstanceAnimationProgress(id, handle)
        }
        return -1f
    }

    /**
     * 获取动画过渡进度
     * @param avatarId Long
     * @param bundle FUBundleData
     * @return Float
     */
    fun getInstanceAnimationTransitionProgress(avatarId: Long, bundle: FUBundleData): Float {
        val id = avatarIdMap[avatarId]
        val handle = mBundleManager.getBundleHandle(bundle.path)
        if (id != null && handle > 0) {
            return SDKController.getInstanceAnimationTransitionProgress(id, handle)
        }
        return -1f
    }


    /**
     * 加载动画道具，并是否开启播放
     * @param avatarId Long
     * @param animationData FUAnimationData 动画模型
     * @param isLoop Boolean?  是否播放  null：不播放 true：循环播放 false：不循环播放
     */
    fun loadAvatarAnimationData(avatarId: Long, animationData: FUAnimationData, isLoop: Boolean?) {
        doAvatarActionBackground(avatarId) {
            val propList = ArrayList<FUBundleData>()
            val animationList = ArrayList<FUBundleData>()
            analyzeAnimationData(animationData, propList, animationList)
            /*创建*/
            doCreateAnimationBundle(propList, animationList)
            doAvatarActionGL(avatarId) { id ->
                doAddAvatarAnimation(id, propList, animationList, isLoop)
            }
        }
    }


    /**
     * 移除动画道具
     * @param animationData FUAnimationData 动画模型
     */
    fun removeAvatarAnimationData(avatarId: Long, animationData: FUAnimationData) {
        doAvatarActionBackgroundGL(avatarId) { id ->
            doRemoveAvatarAnimation(id, animationData)
        }
    }

    /**
     * 替换动画道具
     * @param avatarId Long
     * @param animationData FUAnimationData 原始动画模型
     * @param targetAnimationData FUGroupAnimationData 目标动画模型
     */
    fun replaceAvatarAnimationData(avatarId: Long, animationData: FUAnimationData, targetAnimationData: FUAnimationData) {
        doAvatarActionBackground(avatarId) {
            val propList = ArrayList<FUBundleData>()
            val animationList = ArrayList<FUBundleData>()
            analyzeAnimationData(targetAnimationData, propList, animationList)
            /*创建*/
            doCreateAnimationBundle(propList, animationList)
            doAvatarActionGL(avatarId) { id ->
                /*移除*/
                doRemoveAvatarAnimation(id, animationData)
                /*添加*/
                doAddAvatarAnimation(id, propList, animationList)
            }
        }
    }


    /**
     * 解析FUAnimationData数据
     * @param animationData FUAnimationData 动画数据模型
     * @param propList ArrayList<FUBundleData> 道具列表
     * @param animationList ArrayList<FUBundleData> 动画列表
     */
    private fun analyzeAnimationData(animationData: FUAnimationData, propList: ArrayList<FUBundleData>, animationList: ArrayList<FUBundleData>) {
        if (animationData is FUGroupAnimationData) {
            animationList.add(animationData.animation)
            animationList.addAll(animationData.subAnimations)
            propList.addAll(animationData.subProps)
        } else {
            animationList.add(animationData.animation)
        }
    }


    /**
     * 移除动画
     * @param instanceId Int
     * @param animationData FUAnimationData 动画数据模型
     */
    private fun doRemoveAvatarAnimation(instanceId: Int, animationData: FUAnimationData) {
        val removeBundleList = ArrayList<FUBundleData>()
        analyzeAnimationData(animationData, removeBundleList, removeBundleList)
        val removeHandles = ArrayList<Int>()
        removeBundleList.forEach {
            val handle = mBundleManager.getBundleHandle(it.path)
            if (handle > 0) {
                removeReferenceCount(handleReferenceCountMap, it.path)
                removeHandles.add(handle)
            }
        }
        if (removeHandles.isNotEmpty()) {
            SDKController.unbindItemsFromInstance(instanceId, removeHandles.toIntArray())
        }
        removeBundleList.forEach {
            destroyBundle(it.path)
        }
    }

    /**
     * 创建动画道具
     * @param propList ArrayList<FUBundleData>
     * @param animationList ArrayList<FUBundleData>
     */
    private fun doCreateAnimationBundle(propList: ArrayList<FUBundleData>, animationList: ArrayList<FUBundleData>) {
        animationList.forEach {
            addReferenceCount(handleReferenceCountMap, it.path)
            createBundle(it.path)
        }
        propList.forEach {
            addReferenceCount(handleReferenceCountMap, it.path)
            createBundle(it.path)
        }
    }


    /**
     * 配置动画
     * @param instanceId Int
     * @param propList ArrayList<FUBundleData> 道具列表
     * @param animationList ArrayList<FUBundleData> 动画子列表
     * @param isLoop Boolean？ 是否循环播放
     */
    private fun doAddAvatarAnimation(instanceId: Int, propList: ArrayList<FUBundleData>, animationList: ArrayList<FUBundleData>, isLoop: Boolean? = null) {
        val handles = ArrayList<Int>()
        val animationHandles = ArrayList<Int>()
        animationList.forEach {
            val handle = mBundleManager.getBundleHandle(it.path)
            if (handle > 0) {
                animationHandles.add(handle)
                handles.add(handle)
            }
        }
        propList.forEach {
            val handle = mBundleManager.getBundleHandle(it.path)
            if (handle > 0) {
                handles.add(handle)
            }
        }
        if (handles.isNotEmpty()) {
            SDKController.bindItemsToInstance(instanceId, handles.toIntArray())
        }
        isLoop?.let {
            animationHandles.forEach { anim ->
                if (!isLoop) {
                    SDKController.playInstanceAnimationOnce(instanceId, anim)
                } else {
                    SDKController.playInstanceAnimation(instanceId, anim)
                }
            }
        }
    }


//endregion 动画

//region BlendShape

    fun enableInstanceExpressionBlend(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceExpressionBlend(it, enable)
        }
    }

    fun setInstanceBlendExpression(
            avatarId: Long,
            data: FloatArray,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceBlendExpression(it, data)
        }
    }

    fun setInstanceExpressionWeight0(
            avatarId: Long,
            data: FloatArray,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceExpressionWeight0(it, data)
        }
    }

    fun setInstanceExpressionWeight1(
            avatarId: Long,
            data: FloatArray,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceExpressionWeight1(it, data)
        }
    }

//endregion BlendShape
//region Color

    fun setInstanceColor(
            avatarId: Long,
            name: String,
            color: FUColorRGBData,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceColor(
                    it,
                    name,
                    color.red.toInt(),
                    color.green.toInt(),
                    color.blue.toInt()
            )
        }
    }

    fun setInstanceColorIntensity(
            avatarId: Long,
            name: String,
            intensity: Float,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceColorIntensity(it, name, intensity)
        }
    }


    fun fuSetInstanceFaceBeautyColor(
            avatarId: Long,
            bundle: FUBundleData,
            color: FUColorRGBData,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            val handle = mBundleManager.getBundleHandle(bundle.path)
            if (handle > 0) {
                SDKController.fuSetInstanceFaceBeautyColor(
                        it,
                        handle,
                        color.red.toInt(),
                        color.green.toInt(),
                        color.blue.toInt()
                )
            } else {
                FULogger.w(
                        TAG,
                        "fuSetInstanceFaceBeautyColor failed  bundle=${bundle.name} handle=$handle"
                )
            }
        }
    }

    fun getInstanceSkinColorIndex(avatarId: Long): Int {
        val id = avatarIdMap[avatarId]
        if (id != null) {
            return SDKController.getInstanceSkinColorIndex(id)
        }
        return -1
    }

//endregion Color
//region Deformation

    fun setInstanceDeformation(
            avatarId: Long,
            name: String,
            intensity: Float,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceDeformation(it, name, intensity)
        }
    }
//region Deformation
//region DynamicBone

    fun enableInstanceDynamicBone(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceDynamicBone(it, enable)
        }
    }

    fun resetInstanceDynamicBone(
            avatarId: Long,
            isImmediate: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.resetInstanceDynamicBone(it, if (isImmediate) 1 else 0)
        }
    }


    fun refreshInstanceDynamicBone(
            avatarId: Long,
            isImmediate: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.refreshInstanceDynamicBone(it, if (isImmediate) 1 else 0)
        }
    }

    fun enableInstanceModelMatToBone(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceModelMatToBone(it, enable)
        }
    }

    fun enableInstanceDynamicBoneTeleportMode(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceDynamicBoneTeleportMode(it, enable)
        }
    }

    fun enableInstanceDynamicBoneRootRotationSpeedLimitMode(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceDynamicBoneRootRotationSpeedLimitMode(it, enable)
        }
    }

    fun enableInstanceDynamicBoneRootTranslationSpeedLimitMode(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceDynamicBoneRootTranslationSpeedLimitMode(it, enable)
        }
    }

    //endregion DynamicBone
//region EyeFocusToCamera
    fun enableInstanceFocusEyeToCamera(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceFocusEyeToCamera(it, enable)
        }
    }

    //endregion EyeFocusToCamera
//region FacePup
    fun enableInstanceFaceUpMode(
            avatarId: Long,
            enable: Boolean,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.enableInstanceFaceUpMode(it, enable)
        }
    }

    fun setInstanceFaceUp(
            avatarId: Long,
            name: String,
            value: Float,
            needBackgroundThread: Boolean = true
    ) {
        doAvatarActionBackgroundGL(avatarId, needBackgroundThread) {
            SDKController.setInstanceFaceUp(it, name, value)
        }
    }


    fun getInstanceFaceUpOriginalValue(avatarId: Long, name: String): Float {
        val id = avatarIdMap[avatarId]
        if (id != null) {
            return SDKController.getInstanceFaceUpOriginalValue(id, name)
        }
        return 0f
    }

    fun getInstanceFaceUpArray(avatarId: Long, rect: FloatArray): Int {
        val id = avatarIdMap[avatarId]
        if (id != null) {
            return SDKController.getInstanceFaceUpArray(id, rect)
        }
        return 0
    }


//endregion FacePup
    /**
     * 设置跟随模式
     * @param sceneId Int
     * @param mode 1 为跟随人体
     */
    fun enableHumanFollowMode(sceneId: Long, mode: Boolean) {
        doBackgroundAction {
            val id = sceneIdMap[sceneId]
            id?.let {
                SDKController.enableHumanFollowMode(id,mode)
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
     * 设置动画过滤参数
     */
    fun humanProcessorSetAvatarAnimFilterParams(nBufferFrames: Int, pos: Float, angle: Float) {
        doBackgroundAction {
            SDKController.humanProcessorSetAvatarAnimFilterParams(nBufferFrames, pos, angle)
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


//endregion Avatar接口


}