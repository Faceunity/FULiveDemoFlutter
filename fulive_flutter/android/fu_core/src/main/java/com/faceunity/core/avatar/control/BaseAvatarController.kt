package com.faceunity.core.avatar.control

import android.os.Handler
import android.os.HandlerThread
import com.faceunity.core.bundle.BundleManager
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUGroupAnimationData
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.FULogger
import java.io.File
import java.util.concurrent.*


/**
 *
 * DESC：
 * Created on 2021/3/31
 *
 */
open class BaseAvatarController {
    protected val TAG = "KIT_AvatarController"

    protected val mBundleManager by lazy { BundleManager.getInstance() }
    private val mFURenderBridge by lazy { FURenderBridge.getInstance() }

    /*  当前控制道具id */
    protected var mControllerBundleHandle = -1

    /*已加载道具引用计数*/
    protected var handleReferenceCountMap = ConcurrentHashMap<String, Int>(16)

    /*场景映射关系*/
    protected var sceneIdMap = ConcurrentHashMap<Long, Int>(16)
    protected val sceneBackgroundSet = HashSet<Long>()

    /*Avatar映射关系*/
    protected var avatarIdMap = ConcurrentHashMap<Long, Int>(16)
    protected val avatarBackgroundSet = HashSet<Long>()

    protected val mCachedThreadPool by lazy {
        ThreadPoolExecutor(0,Integer.MAX_VALUE,30L, TimeUnit.SECONDS, SynchronousQueue<Runnable>())
    }
    //region 数据比对


    /**
     * 新增Avatar数据比对
     * @param fuaAvatarData FUAAvatarData
     */
    protected fun addAvatar(sceneId: Long, fuaAvatarData: FUAAvatarData, compareData: AvatarCompareData) {
        val bindArray = ArrayList<String>()
        val bundles = getAvatarBundles(fuaAvatarData)
        bundles.forEach {
            if (!bindArray.contains(it.path)) {
                addReferenceCount(compareData.bundleAddMap, it.path)
                bindArray.add(it.path)
            }
        }
        compareData.avatarParamsMap[fuaAvatarData.id] = fuaAvatarData.param
        compareData.avatarBindHandleMap[fuaAvatarData] = bindArray
        compareData.sceneBindAvatarMap[sceneId] = arrayListOf(fuaAvatarData.id)
    }


    /**
     * 移除Avatar数据比对
     * @param fuaAvatarData FUAAvatarData
     */
    protected fun removeAvatar(sceneId: Long, fuaAvatarData: FUAAvatarData, compareData: AvatarCompareData) {
        val unbindArray = ArrayList<String>()
        val bundles = getAvatarBundles(fuaAvatarData)
        bundles.forEach {
            if (!unbindArray.contains(it.path)) {
                addReferenceCount(compareData.bundleRemoveMap, it.path)
                unbindArray.add(it.path)
            }
        }
        compareData.avatarUnbindHandleMap[fuaAvatarData.id] = unbindArray
        compareData.sceneUnbindAvatarMap[sceneId] = arrayListOf(fuaAvatarData.id)
    }


    /**
     * 替换Avatar
     * @param oldAvatar FUAAvatarData
     * @param newAvatar FUAAvatarData
     */
    protected fun replaceAvatar(oldAvatar: FUAAvatarData, targetAvatar: FUAAvatarData, compareData: AvatarCompareData) {
        compareData.sceneReplaceAvatarMap[oldAvatar.id] = targetAvatar.id
        val unbindArray = ArrayList<String>()
        val oldBundles = getAvatarBundles(oldAvatar)
        oldBundles.forEach {
            if (!unbindArray.contains(it.path)) {
                addReferenceCount(compareData.bundleRemoveMap, it.path)
                unbindArray.add(it.path)
            }
        }
        val bindArray = ArrayList<String>()
        val targetBundles = getAvatarBundles(targetAvatar)
        targetBundles.forEach {
            if (unbindArray.contains(it.path)) {
                unbindArray.remove(it.path)
                removeReferenceCount(compareData.bundleRemoveMap, it.path)
            } else {
                bindArray.add(it.path)
                addReferenceCount(compareData.bundleAddMap, it.path)
            }
        }
        compareData.avatarParamsMap[targetAvatar.id] = targetAvatar.param
        compareData.avatarUnbindHandleMap[oldAvatar.id] = unbindArray
        compareData.avatarBindHandleMap[targetAvatar] = bindArray
    }

    /**
     * 添加场景
     * @param fuaSceneData FUASceneData
     */
    protected fun addScene(fuaSceneData: FUASceneData, compareData: AvatarCompareData) {
        val bindArray = ArrayList<String>()
        val bundles = getSceneBundles(fuaSceneData)
        bundles.forEach {
            if (!bindArray.contains(it.path)) {
                bindArray.add(it.path)
                addReferenceCount(compareData.bundleAddMap, it.path)
            }
        }
        if (!compareData.sceneAddList.contains(fuaSceneData)) {
            compareData.sceneAddList.add(fuaSceneData)
        }
        compareData.sceneBindHandleMap[fuaSceneData] = bindArray
        fuaSceneData.avatars.forEach {
            addAvatar(fuaSceneData.id, it, compareData)
        }
    }

    /**
     * 移除场景
     * @param fuaSceneData FUASceneData
     */
    protected fun removeScene(fuaSceneData: FUASceneData, compareData: AvatarCompareData) {
        val unbindArray = ArrayList<String>()
        val bundles = getSceneBundles(fuaSceneData)
        bundles.forEach {
            if (!unbindArray.contains(it.path)) {
                unbindArray.add(it.path)
                addReferenceCount(compareData.bundleRemoveMap, it.path)
            }
        }
        if (!compareData.sceneRemoveList.contains(fuaSceneData)) {
            compareData.sceneRemoveList.add(fuaSceneData)
        }
        compareData.sceneUnbindHandleMap[fuaSceneData.id] = unbindArray
        fuaSceneData.avatars.forEach {
            removeAvatar(fuaSceneData.id, it, compareData)
        }
    }


    /**
     * 替换Scene
     * @param oldScene FUASceneData
     * @param newScene FUASceneData
     */
    protected fun replaceScene(oldScene: FUASceneData, newScene: FUASceneData, compareData: AvatarCompareData) {
        removeScene(oldScene, compareData)
        addScene(newScene, compareData)
        diffBundleMap(compareData)
    }


    /**
     * 获取FUAAvatarData 包含的所有道具列表
     * @param avatarData FUAAvatarData
     * @return ArrayList<FUBundleData>
     */
    private fun getAvatarBundles(avatarData: FUAAvatarData): ArrayList<FUBundleData> {
        val bundles = ArrayList<FUBundleData>()
        bundles.addAll(avatarData.itemBundles)
        avatarData.animationData.forEach {
            bundles.add(it.animation)
            if (it is FUGroupAnimationData) {
                bundles.addAll(it.subAnimations)
                bundles.addAll(it.subProps)
            }
        }
        return bundles
    }

    /**
     * 获取FUAAvatarData 包含的所有道具列表
     * @param sceneData FUAAvatarData
     * @return ArrayList<FUBundleData>
     */
    private fun getSceneBundles(sceneData: FUASceneData): ArrayList<FUBundleData> {
        val bundles = ArrayList<FUBundleData>()
        bundles.addAll(sceneData.itemBundles)
        sceneData.animationData.forEach {
            bundles.add(it.animation)
            if (it is FUGroupAnimationData) {
                bundles.addAll(it.subAnimations)
                bundles.addAll(it.subProps)
            }
        }
        return bundles
    }


    //endregion 数据比对


    //region 业务支持


    /**
     * 添加控制道具
     * @param sceneData FUASceneData
     */
    protected fun loadControllerBundle(sceneData: FUASceneData) {
        val controller = sceneData.controller
        val handle = mBundleManager.loadBundleFile(controller.name, controller.path)
        if (handle <= 0) {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
            mControllerBundleHandle = -1
            FULogger.e(TAG, "loadControllerBundle failed handle:$handle  path:${controller.path}")
            return
        }
        if (sceneData.enable) {
            mBundleManager.updateControllerBundle(mControllerBundleHandle, handle, false)
        } else {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
        }
        mControllerBundleHandle = handle
    }

    /**
     * 销毁道具
     * @param path String
     */
    protected fun destroyBundle(path: String) {
        if (!handleReferenceCountMap.containsKey(path)) {
            val handle = mBundleManager.getBundleHandle(path)
            if (handle > 0) {
                mBundleManager.destroyBundle(intArrayOf(handle))
            }
        }
    }

    /**
     * 创建道具
     * @param path String
     * @return Int
     */
    protected fun createBundle(path: String): Int {
        return mBundleManager.loadBundleFile(getFileName(path), path)
    }

    /**
     * 引用计数增加
     * @param cacheMap ConcurrentHashMap<String, Int>
     * @param key String
     */
    protected fun addReferenceCount(cacheMap: ConcurrentHashMap<String, Int>, key: String, count: Int = 1) {
        if (cacheMap.containsKey(key)) {
            cacheMap[key] = cacheMap[key]!! + count
        } else {
            cacheMap[key] = count
        }
    }

    /**
     * 引用计数增加
     * @param cacheMap LinkedHashMap<String, Int>
     * @param key String
     */
    protected fun addReferenceCount(cacheMap: LinkedHashMap<String, Int>, key: String, count: Int = 1) {
        if (cacheMap.containsKey(key)) {
            cacheMap[key] = cacheMap[key]!! + count
        } else {
            cacheMap[key] = count
        }
    }

    /**
     * 引用计数减少
     * @param cacheMap ConcurrentHashMap<String, Int>
     * @param key String
     */
    protected fun removeReferenceCount(cacheMap: ConcurrentHashMap<String, Int>, key: String, count: Int = 1) {
        if (cacheMap.containsKey(key)) {
            if (cacheMap[key]!! > count) {
                cacheMap[key] = cacheMap[key]!! - count
            } else {
                cacheMap.remove(key)
            }
        }
    }

    /**
     * 引用计数减少
     * @param cacheMap ConcurrentHashMap<String, Int>
     * @param key String
     */
    protected fun removeReferenceCount(cacheMap: LinkedHashMap<String, Int>, key: String, count: Int = 1) {
        if (cacheMap.containsKey(key)) {
            if (cacheMap[key]!! > count) {
                cacheMap[key] = cacheMap[key]!! - count
            } else {
                cacheMap.remove(key)
            }
        }
    }

    /**
     * 比较更新道具Map
     */
    private fun diffBundleMap(compareData: AvatarCompareData) {
        val iterator = compareData.bundleAddMap.entries.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (compareData.bundleRemoveMap.containsKey(item.key)) {
                val removeCount = compareData.bundleRemoveMap[item.key]!!
                when {
                    removeCount < item.value -> {
                        compareData.bundleRemoveMap.remove(item.key)
                        item.setValue(item.value - removeCount)
                    }
                    removeCount == item.value -> {
                        compareData.bundleRemoveMap.remove(item.key)
                        iterator.remove()
                    }
                    else -> {
                        compareData.bundleRemoveMap[item.key] = removeCount - item.value
                        iterator.remove()
                    }
                }
            }

        }
    }

    /**
     * 截取道具名称
     * @param path String
     * @return String
     */
    protected fun getFileName(path: String): String {
        val fName = path.trim()
        var fileName = fName.substring(fName.lastIndexOf(File.separator) + 1)
        if (fileName.contains(".bundle")) {
            fileName = fileName.substring(0, fileName.indexOf(".bundle"))
        }
        return fileName
    }

    //endregion 业务支持

    //region 异步线程
    internal open fun release(unit: (() -> Unit)? = null) {
        controllerHandler?.let {
//            it.removeCallbacksAndMessages(null)
            val countDownLatch = CountDownLatch(1)
            doBackgroundAction {
                if (mControllerBundleHandle > 0) {
                    unit?.invoke()
                    mBundleManager.destroyControllerBundle(mControllerBundleHandle)
                    mControllerBundleHandle = -1
                }
                countDownLatch.countDown()
            }
            countDownLatch.await()
        }
        releaseThread()
    }


    /**
     * 执行后台任务
     * @param code Int
     * @param unit Function0<Unit>
     */
    protected fun doBackgroundAction(unit: () -> Unit) {
        if (controllerHandler == null) {
            startBackgroundThread()
        }
        if (Thread.currentThread().id == controllerThreadId) {
            unit.invoke()
        } else {
            controllerHandler!!.post(unit)
        }
    }

    /**
     * GL线程执行任务
     * @param unit Function0<Unit>
     */
    protected fun doGLThreadAction(unit: () -> Unit) {
        mFURenderBridge.doGLThreadAction(unit)
    }

    private var controllerThreadId = -1L
    private var controllerHandler: Handler? = null

    /* 创建线程  */
    private fun startBackgroundThread() {
        val backgroundThread = HandlerThread("KIT_${this.javaClass.simpleName}")
        backgroundThread.start()
        controllerHandler = Handler(backgroundThread.looper)
        controllerThreadId = controllerHandler!!.looper.thread.id
    }


    /* 释放线程  */
    private fun releaseThread() {
        controllerHandler?.removeCallbacksAndMessages(null)
        controllerHandler?.looper?.quitSafely()
        controllerHandler = null
    }


//endregion 异步线程


}