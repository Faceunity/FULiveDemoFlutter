package com.faceunity.core.avatar.control

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.faceunity.core.bundle.BundleManager
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.FULogger
import java.io.File
import java.util.concurrent.CountDownLatch


/**
 *
 * DESC：
 * Created on 2021/3/31
 *
 */
open class BaseAvatarController {
    protected val TAG = "KIT_AvatarController"

    protected val mBundleManager by lazy { BundleManager.getInstance() }
    protected val mFURenderBridge by lazy { FURenderBridge.getInstance() }

    /*  当前控制道具id */
    protected var mControllerBundleHandle = -1

    /*已加载道具引用计数*/
    protected var handleReferenceCountMap = LinkedHashMap<String, Int>(16)

    /*已加载道具句柄映射关系*/
    protected var handleBundleIdMap = HashMap<String, Int>(16)

    /*场景映射关系*/
    protected var sceneIdMap = HashMap<Long, Int>(16)

    /*Avatar映射关系*/
    protected var avatarIdMap = HashMap<Long, Int>(16)


    //region 数据比对

    protected val bundleCreateList = ArrayList<String>()//需要新创建的Bundle
    protected val bundleRemoveMap = LinkedHashMap<String, Int>()//比对需要减少的Bundle，key:路径  value 引用计数
    protected val bundleAddMap = LinkedHashMap<String, Int>()//比对新增的Bundle，key:路径  value 引用计数
    protected val sceneRemoveList = ArrayList<FUASceneData>()//需要移除的场景
    protected val sceneAddList = ArrayList<FUASceneData>()//需要绑定创建的场景

    protected val sceneBindHandleMap = LinkedHashMap<FUASceneData, ArrayList<String>>()//Scene比对结束需要绑定的句柄
    protected val sceneUnbindHandleMap = LinkedHashMap<Long, ArrayList<String>>()//Scene比对结束需要解绑的句柄
    protected val sceneUnbindAvatarMap = LinkedHashMap<Long, ArrayList<Long>>()//Scene比对结束需要解绑Avatar
    protected val sceneBindAvatarMap = LinkedHashMap<Long, ArrayList<Long>>()//Scene比对结束需要绑定的Avatar

    protected val avatarBindHandleMap = LinkedHashMap<FUAAvatarData, ArrayList<String>>()//Avatar比对结束需要绑定的句柄
    protected val avatarUnbindHandleMap = LinkedHashMap<Long, ArrayList<String>>()//Avatar比对结束需要解绑的句柄


    /**
     * 清空比对数据缓存
     */
    protected fun clearCompData() {
        bundleCreateList.clear()
        bundleRemoveMap.clear()
        bundleAddMap.clear()
        sceneRemoveList.clear()
        sceneAddList.clear()
        sceneBindHandleMap.clear()
        sceneUnbindHandleMap.clear()
        sceneUnbindAvatarMap.clear()
        sceneBindAvatarMap.clear()
        avatarBindHandleMap.clear()
        avatarUnbindHandleMap.clear()
    }

    /**
     * 新增Avatar数据比对
     * @param fuaAvatarData FUAAvatarData
     */
    protected fun addAvatar(sceneId: Long, fuaAvatarData: FUAAvatarData) {
        val bindArray = ArrayList<String>()
        fuaAvatarData.itemBundles.forEach {
            if (!handleBundleIdMap.containsKey(it.path) && !bundleCreateList.contains(it.path)) {
                bundleCreateList.add(it.path)
            }
            if (!bindArray.contains(it.path)) {
                addReferenceCount(bundleAddMap, it.path)
                bindArray.add(it.path)
            }
        }
        avatarBindHandleMap[fuaAvatarData] = bindArray
        sceneBindAvatarMap[sceneId] = arrayListOf(fuaAvatarData.id)
    }

    /**
     * 移除Avatar数据比对
     * @param fuaAvatarData FUAAvatarData
     */
    protected fun removeAvatar(sceneId: Long, fuaAvatarData: FUAAvatarData) {
        if (!avatarIdMap.containsKey(fuaAvatarData.id)){
            FULogger.e(TAG,"removeAvatar failed has not contains this fuaAvatarData:${fuaAvatarData.id}")
            return
        }
        val unbindArray = ArrayList<String>()
        fuaAvatarData.itemBundles.forEach {
            if (!unbindArray.contains(it.path)) {
                addReferenceCount(bundleRemoveMap, it.path)
                unbindArray.add(it.path)
            }
        }
        avatarUnbindHandleMap[fuaAvatarData.id] = unbindArray
        sceneUnbindAvatarMap[sceneId] = arrayListOf(fuaAvatarData.id)
    }


    /**
     * 替换Avatar
     * @param oldAvatar FUAAvatarData
     * @param newAvatar FUAAvatarData
     */
    protected fun replaceAvatar(sceneId: Long, oldAvatar: FUAAvatarData, newAvatar: FUAAvatarData) {
        if (!sceneIdMap.containsKey(sceneId)){
            FULogger.e(TAG,"replaceAvatar failed has not contains this fuaSceneData:${sceneId}")
            return
        }
        if (!avatarIdMap.containsKey(oldAvatar.id)){
            FULogger.e(TAG,"replaceAvatar failed has not contains this oldAvatar:${oldAvatar.id}")
            return
        }
        avatarIdMap[newAvatar.id]=avatarIdMap[oldAvatar.id]!!
        avatarIdMap.remove(oldAvatar.id)
        val unbindArray = ArrayList<String>()
        oldAvatar.itemBundles.forEach {
            if (!unbindArray.contains(it.path)) {
                addReferenceCount(bundleRemoveMap, it.path)
                unbindArray.add(it.path)
            }
        }
        val bindArray = ArrayList<String>()
        newAvatar.itemBundles.forEach {
            if (!handleBundleIdMap.containsKey(it.path) && !bundleCreateList.contains(it.path)) {
                bundleCreateList.add(it.path)
            }
            if (!bindArray.contains(it.path)) {
                if (unbindArray.contains(it.path)){
                    unbindArray.remove(it.path)
                }
                addReferenceCount(bundleAddMap, it.path)
                bindArray.add(it.path)
            }
        }
        avatarUnbindHandleMap[newAvatar.id] = unbindArray
        avatarBindHandleMap[newAvatar] = bindArray
        diffBundleMap()
    }

    /**
     * 添加场景
     * @param fuaSceneData FUASceneData
     */
    protected fun addScene(fuaSceneData: FUASceneData) {
        val bindArray = ArrayList<String>()
        fuaSceneData.bundles.forEach {
            if (!handleBundleIdMap.containsKey(it.path) && !bundleCreateList.contains(it.path)) {
                bundleCreateList.add(it.path)
            }
            if (!bindArray.contains(it.path)) {
                bindArray.add(it.path)
                addReferenceCount(bundleAddMap, it.path)
            }
        }
        if (!sceneAddList.contains(fuaSceneData)) {
            sceneAddList.add(fuaSceneData)
        }
        sceneBindHandleMap[fuaSceneData] = bindArray
        fuaSceneData.avatars.forEach {
            addAvatar(fuaSceneData.id, it)
        }
    }

    /**
     * 移除场景
     * @param fuaSceneData FUASceneData
     */
    protected fun removeScene(fuaSceneData: FUASceneData) {
        if (!sceneIdMap.containsKey(fuaSceneData.id)){
            FULogger.e(TAG,"removeScene failed has not contains this fuaSceneData:${fuaSceneData.id}")
            return
        }
        val unbindArray = ArrayList<String>()
        fuaSceneData.bundles.forEach {
            if (!unbindArray.contains(it.path)) {
                unbindArray.add(it.path)
                addReferenceCount(bundleRemoveMap, it.path)
            }
        }
        if (!sceneRemoveList.contains(fuaSceneData)) {
            sceneRemoveList.add(fuaSceneData)
        }
        sceneUnbindHandleMap[fuaSceneData.id] = unbindArray
        fuaSceneData.avatars.forEach {
            removeAvatar(fuaSceneData.id, it)
        }
    }


    /**
     * 替换Scene
     * @param oldScene FUASceneData
     * @param newScene FUASceneData
     */
    protected fun replaceScene(oldScene: FUASceneData, newScene: FUASceneData) {
        removeScene(oldScene)
        addScene(newScene)
        diffBundleMap()
    }


    //endregion 数据比对


    //region 业务支持

    /**
     * 添加控制道具
     * @param sceneData FUASceneData
     */
    protected fun loadControllerBundle(sceneData: FUASceneData) {
        var handle = 0
        val controller = sceneData.controller
        handle = mBundleManager.loadBundleFile(controller.name, controller.path)
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
     * 引用计数增加
     * @param linkedHashMap LinkedHashMap<String, Int>
     * @param key String
     */
    protected fun addReferenceCount(linkedHashMap: LinkedHashMap<String, Int>, key: String, count: Int = 1) {
        if (linkedHashMap.containsKey(key)) {
            linkedHashMap[key] = linkedHashMap[key]!! + count
        } else {
            linkedHashMap[key] = count
        }
    }

    /**
     * 引用计数减少
     * @param linkedHashMap LinkedHashMap<String, Int>
     * @param key String
     */
    protected fun removeReferenceCount(linkedHashMap: LinkedHashMap<String, Int>, key: String, count: Int = 1) {
        if (linkedHashMap.containsKey(key)) {
            if (linkedHashMap[key]!! > count) {
                linkedHashMap[key] = linkedHashMap[key]!! - count
            } else {
                linkedHashMap.remove(key)
            }
        }
    }

    /**
     * 比较更新道具Map
     */
    private fun diffBundleMap() {
        val iterator = bundleAddMap.entries.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (bundleRemoveMap.containsKey(item.key)) {
                val removeCount = bundleRemoveMap[item.key]!!
                when {
                    removeCount < item.value -> {
                        bundleRemoveMap.remove(item.key)
                        item.setValue(item.value - removeCount)
                    }
                    removeCount == item.value -> {
                        bundleRemoveMap.remove(item.key)
                        iterator.remove()
                    }
                    else -> {
                        bundleRemoveMap[item.key] = removeCount - item.value
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