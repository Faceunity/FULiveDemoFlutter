package com.faceunity.core.controller.makeup

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.enumeration.FUExternalInputEnum
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：美妆调用控制器
 * Created on 2021/2/8
 *
 */
class MakeupController : BaseSingleController() {

    // 美妆单项组合妆子句柄集合 key :路径  value :句柄
    private var makeupItemHandleMap = LinkedHashMap<String, Int>(16)

    // 美妆单项组合妆子句柄集合 key :部位关键字  value :道具路径
    private var makeupItemKeyMap = LinkedHashMap<String, String>(16)


    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        var handle = 0
        featuresData.bundle?.let {
            handle = mBundleManager.loadBundleFile(it.name, it.path)
        }
        if (handle <= 0) {
            releaseItems()
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
            mControllerBundleHandle = -1
            return
        }
        //2.差分
        loadMakeupComp(mControllerBundleHandle, handle, featuresData.param)
//        itemSetParam(MakeupParam.IS_CLEAR_MAKEUP, 1.0)
        //3.减除额外的句柄绑定
        if (comUnbindHandle.isNotEmpty()) {
            mBundleManager.unbindControllerItem(mControllerBundleHandle, comUnbindHandle.toIntArray())
        }
        //4.销毁额外的句柄绑定
        if (comDestroyHandle.isNotEmpty()) {
            mBundleManager.destroyBundle(comDestroyHandle.toIntArray())
        }
        // 5.更换控制句柄
        if (featuresData.enable) {
            mBundleManager.updateControllerBundle(mControllerBundleHandle, handle)
        } else {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
        }
        mControllerBundleHandle = handle
        makeupItemHandleMap.clear()
        makeupItemHandleMap.putAll(comHasBindHandle)
        // 6.绑定新句柄
        val itemHandles = IntArray(comBindHandle.size)
        var index = 0
        comBindHandle.forEach { (_, handle) ->
            itemHandles[index++] = handle
        }
        makeupItemHandleMap.putAll(comBindHandle)
        mBundleManager.bindControllerItem(handle, itemHandles)
        // 7.设置其他参数
        /*点位镜像处理*/
        featuresData.param.forEach { (key, value) ->
            if (!key.startsWith("tex_")) {
                itemSetParam(key, value)
            }
        }
        val flipPoints = if (mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE
            || mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO || mFURenderBridge.cameraFacing == CameraFacingEnum.CAMERA_BACK
        ) 1.0 else 0.0
        itemSetParam(MakeupParam.IS_FLIP_POINTS, flipPoints)
//        itemSetParam(MakeupParam.IS_CLEAR_MAKEUP, 0.0)
        itemSetParam(MakeupParam.IS_MAKEUP_ON, 1.0)
    }

    /**
     * 更新句柄
     * @param key String 部分名称标识
     * @param bundle FUBundleData? 新句柄Bundle文件
     */
    internal fun updateItemBundle(sign: Long, key: String, bundle: FUBundleData?) {
        FULogger.i(TAG, "updateItemBundle sign:${sign == modelSign}  key:$key  path:${bundle?.path}")
        if (sign != modelSign) return
        doBackgroundAction {
            val oldPath = makeupItemKeyMap[key]
            if (oldPath == null && bundle != null) {
                bindItemBundle(key, bundle)
            } else if (oldPath != null && bundle == null) {
                unbindItemBundle(key, oldPath)
            } else if (oldPath != null && bundle != null && oldPath != bundle.path) {
                updateItemBundle(key, oldPath, bundle)
            }
        }

    }

    /**
     * 添加道具
     * @param fuFeaturesData FUFeaturesData
     */
    fun applyAddProp( bundle: FUBundleData) {
        val handle = mBundleManager.loadBundleFile(bundle.name, bundle.path)
        if (handle <= 0) {
            FULogger.e(TAG, "load Prop bundle failed bundle path:${bundle.path}")
            return
        }
        mBundleManager.bindControllerBundle(handle)
    }


    /**
     * 相机摄像头切换，重设参数
     */
    internal fun updateFlipMode() {
        if (mControllerBundleHandle <= 0) {
            return
        }
        val flipPoints = if (mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE
            || mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO || mFURenderBridge.cameraFacing == CameraFacingEnum.CAMERA_BACK
        ) 1.0 else 0.0
        itemSetParam(MakeupParam.IS_FLIP_POINTS, flipPoints)
    }


    //region 比对
    private var isSomeController = false
    private val comUnbindHandle = ArrayList<Int>()//比对结束需要接触绑定的句柄
    private val comDestroyHandle = ArrayList<Int>()//比对结束需要解除绑定的句柄
    private val comBindHandle = LinkedHashMap<String, Int>()//比对结束需要重新绑定的句柄
    private val comHasBindHandle = LinkedHashMap<String, Int>()//比对结束已经绑定的句柄

    /**
     * 数据差分
     * @param oldHandle Int
     * @param newHandle Int
     * @param param LinkedHashMap<String, Any>
     */
    private fun loadMakeupComp(oldHandle: Int, newHandle: Int, param: LinkedHashMap<String, Any>) {
        clearCompData()
        isSomeController = oldHandle == newHandle
        makeupItemKeyMap.clear()
        makeupItemHandleMap.forEach { (path, handle) ->
            comUnbindHandle.add(handle)
            comDestroyHandle.add(handle)
        }
        for ((key, value) in param.entries) {
            if (value is FUBundleData) {
                val item = if (makeupItemHandleMap.containsKey(value.path)) makeupItemHandleMap[value.path]!! else -1
                if (item > 0) {
                    if (isSomeController) {
                        comHasBindHandle[value.path] = item
                        comUnbindHandle.remove(item)
                    } else {
                        comBindHandle[value.path] = item
                    }
                    comDestroyHandle.remove(item)
                } else {
                    val handle: Int = mBundleManager.loadBundleFile(value.name, value.path)
                    if (handle > 0) {
                        comBindHandle[value.path] = handle
                    }
                }
                makeupItemKeyMap[key] = value.path
            }
        }
    }


    /**
     * 清空比对数据缓存
     */
    private fun clearCompData() {
        isSomeController = false
        comUnbindHandle.clear()
        comDestroyHandle.clear()
        comBindHandle.clear()
        comHasBindHandle.clear()
    }


    /**
     * 移除旧句柄
     * @param key String
     * @param path String
     */
    private fun unbindItemBundle(key: String, path: String) {
        val item = makeupItemHandleMap[path]
        item?.let {
            if (mControllerBundleHandle > 0 && item > 0) {
                mBundleManager.unbindControllerItem(mControllerBundleHandle, it)
            }
            if (it > 0) {
                mBundleManager.destroyBundle(it)
            }
        }
        makeupItemHandleMap.remove(path)
        makeupItemKeyMap.remove(key)
    }

    /**
     * 绑定新句柄
     * @param key String
     * @param bundle FUBundleData
     */
    private fun bindItemBundle(key: String, bundle: FUBundleData) {
        val item: Int = mBundleManager.loadBundleFile(bundle.name, bundle.path)
        if (mControllerBundleHandle > 0 && item > 0) {
            mBundleManager.bindControllerItem(mControllerBundleHandle, item)
            makeupItemHandleMap[bundle.path] = item
            makeupItemKeyMap[key] = bundle.path
        }
    }

    /**
     * 更新句柄-先加载新句柄，再移除旧句柄，再绑定新句柄
     * @param key String
     * @param oldPath String
     * @param bundle FUBundleData
     */
    private fun updateItemBundle(key: String, oldPath: String, bundle: FUBundleData) {
        val item: Int = mBundleManager.loadBundleFile(bundle.name, bundle.path)
        unbindItemBundle(key, oldPath)
        if (mControllerBundleHandle > 0 && item > 0) {
            mBundleManager.bindControllerItem(mControllerBundleHandle, item)
            makeupItemHandleMap[bundle.path] = item
            makeupItemKeyMap[key] = bundle.path
        }
    }


//endregion


    /**
     * 释放美妆资源
     */
    override fun release(unit: (() -> Unit)?) {
        super.release { releaseItems() }
    }


    private fun releaseItems() {
        if (makeupItemHandleMap.isNotEmpty()) {
            val releaseHandles = IntArray(makeupItemHandleMap.size)
            var index = 0
            makeupItemHandleMap.forEach { (_, u) -> releaseHandles[index++] = u }
            val handle = mControllerBundleHandle
            if (handle > 0) {
                mBundleManager.unbindControllerItem(handle, releaseHandles)
            }
            mBundleManager.destroyBundle(releaseHandles)
            makeupItemHandleMap.clear()
        }
        makeupItemKeyMap.clear()
    }


}