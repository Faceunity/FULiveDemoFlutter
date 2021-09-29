package com.faceunity.core.controller.prop

import com.faceunity.core.controller.bgSegGreen.BgSegGreenParam
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.enumeration.FUExternalInputEnum
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger

/**
 *
 * DESC：道具控制器
 * Created on 2021/2/8
 *
 */
class PropContainerController : BasePropController() {


//region 接口调用

    /**
     * 添加道具
     * @param fuFeaturesData FUFeaturesData
     */
    fun addProp(fuFeaturesData: FUFeaturesData) {
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.ADD, fuFeaturesData)
        doBackgroundAction(queueItem)
    }

    /**
     * 替换道具
     * @param oldData FUFeaturesData
     * @param newData FUFeaturesData
     */
    fun replaceProp(oldData: FUFeaturesData, newData: FUFeaturesData) {
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.REPLACE, oldData, newData)
        doBackgroundAction(queueItem)
    }


    /**
     * 移除道具
     * @param fuFeaturesData FUFeaturesData
     */
    fun removeProp(fuFeaturesData: FUFeaturesData) {
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.REMOVE, fuFeaturesData)
        doBackgroundAction(queueItem)
    }


    /**
     * Bundle 渲染开关
     * @param enable Boolean
     */
    internal fun setBundleEnable(propId: Long, enable: Boolean) {
        val unit: () -> Unit = {
            val handle = propIdMap[propId]
            handle?.let {
                if (enable) {
                    mBundleManager.bindControllerBundle(handle)
                } else {
                    mBundleManager.unbindControllerBundle(handle)
                }
            }
        }
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.UNIT, unit = unit)
        doBackgroundAction(queueItem)
    }

    /**
     * 相机摄像头切换，重设参数
     */
    internal fun updateFlipMode() {
        propIdMap.forEach { (id, handle) ->
            val remark = propTypeMap[id]
            remark?.let {
                if (remark[PropParam.PROP_TYPE] == PropParam.PROP_TYPE_BG_SEG_CUSTOM) {
                    val rotMode = mFURenderBridge.mRotationMode
                    itemSetParam(handle, BgSegGreenParam.ROTATION_MODE, rotMode.toDouble())
                } else if (remark[PropParam.PROP_TYPE] == PropParam.PROP_TYPE_FINE_STICKER && remark.containsKey(PropParam.IS_FLIP_POINTS)) {
                    val flipPoints = if (mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE
                        || mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO || mFURenderBridge.cameraFacing == CameraFacingEnum.CAMERA_BACK
                    ) 1.0 else 0.0
                    itemSetParam(handle, PropParam.IS_FLIP_POINTS, flipPoints)
                }
            }
        }
    }

    /**
     * 相机传感器，检测相机旋转，重设参数
     */
    internal fun updateRotationMode() {
        propIdMap.forEach { (id, handle) ->
            val remark = propTypeMap[id]
            remark?.let {
                if (remark[PropParam.PROP_TYPE] == PropParam.PROP_TYPE_BG_SEG_CUSTOM) {
                    val rotMode = mFURenderBridge.mRotationMode
                    itemSetParam(handle, BgSegGreenParam.ROTATION_MODE, rotMode.toDouble())
                }
            }
        }
    }


    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    internal fun setItemParamGL(propId: Long, key: String, value: Any) {
        val unit: () -> Unit = {
            val handle = propIdMap[propId]
            handle?.let {
                doGLThreadAction {
                    itemSetParam(handle, key, value)
                }
            }
        }
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.UNIT, unit = unit)
        doBackgroundAction(queueItem)
    }

    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    internal fun setItemParam(propId: Long, key: String, value: Any) {
        val unit: () -> Unit = {
            val handle = propIdMap[propId]
            handle?.let {
                itemSetParam(handle, key, value)
            }
        }
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.UNIT, unit = unit)
        doBackgroundAction(queueItem)
    }

    /**
     * 创建纹理
     * @param propId Long
     * @param name String
     * @param rgba ByteArray
     * @param width Int
     * @param height Int
     */
    internal fun createTexForItem(propId: Long, name: String, rgba: ByteArray, width: Int, height: Int) {
        val unit: () -> Unit = {
            val handle = propIdMap[propId]
            handle?.let {
                doGLThreadAction {
                    SDKController.deleteTexForItem(handle, name)
                    SDKController.createTexForItem(handle, name, rgba, width, height)
                }
            }
        }
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.UNIT, unit = unit)
        doBackgroundAction(queueItem)
    }

    /**
     * 删除纹理
     * @param propId Long
     * @param name String
     */
    internal fun deleteTexForItem(propId: Long, name: String) {
        val unit: () -> Unit = {
            val handle = propIdMap[propId]
            handle?.let {
                doGLThreadAction {
                    SDKController.deleteTexForItem(handle, name)
                }
            }
        }
        val queueItem = ThreadQueuePool.QueueItem(ThreadQueuePool.QueueType.UNIT, unit = unit)
        doBackgroundAction(queueItem)
    }


    //endregion

    //region 业务实现

    /**
     * 任务执行
     * @param queue QueueItem
     */
    override fun applyThreadQueue(queue: ThreadQueuePool.QueueItem) {
        when (queue.type) {
            ThreadQueuePool.QueueType.ADD -> {
                applyAddProp(queue.data!!)
            }
            ThreadQueuePool.QueueType.REMOVE -> {
                applyRemoveProp(queue.data!!)

            }
            ThreadQueuePool.QueueType.REPLACE -> {
                applyReplaceProp(queue.data!!, queue.replaceData!!)
            }
            ThreadQueuePool.QueueType.UNIT -> {
                queue.unit?.invoke()
            }
        }
    }


    /**
     * 添加道具
     * @param fuFeaturesData FUFeaturesData
     */
    private fun applyAddProp(fuFeaturesData: FUFeaturesData) {
        val fuBundleData = fuFeaturesData.bundle!!
        val handle = mBundleManager.loadBundleFile(fuBundleData.name, fuBundleData.path)
        if (handle <= 0) {
            FULogger.e(TAG, "load Prop bundle failed bundle path:${fuBundleData.path}")
            return
        }
        propIdMap[fuFeaturesData.id] = handle
        propTypeMap[fuFeaturesData.id] = fuFeaturesData.remark!! as LinkedHashMap<String, Any>
        if (fuFeaturesData.enable) {
            mBundleManager.bindControllerBundle(handle)
        }
        setPropParams(handle, fuFeaturesData)
    }

    /**
     * 移除道具
     * @param fuFeaturesData FUFeaturesData
     */
    private fun applyRemoveProp(fuFeaturesData: FUFeaturesData) {
        val oldHandle = propIdMap[fuFeaturesData.id]
        oldHandle?.let {
            mBundleManager.destroyControllerBundle(it)
            propIdMap.remove(fuFeaturesData.id)
            propTypeMap.remove(fuFeaturesData.id)
        }
    }


    /**
     * 替换
     * @param oldData FUFeaturesData
     * @param newData FUFeaturesData
     */
    private fun applyReplaceProp(oldData: FUFeaturesData, newData: FUFeaturesData) {
        if (oldData.bundle!!.path == newData.bundle!!.path) {
            val oldHandle = propIdMap[oldData.id]
            oldHandle?.let {
                propIdMap.remove(oldData.id)
                propTypeMap.remove(oldData.id)
                propIdMap[newData.id] = it
                propTypeMap[newData.id] = newData.remark!! as LinkedHashMap<String, Any>
                if (newData.enable) {
                    mBundleManager.bindControllerBundle(it)
                } else {
                    mBundleManager.unbindControllerBundle(it)
                }
                setPropParams(it, newData)
            }
            return
        }
        val fuBundleData = newData.bundle
        val newHandle = mBundleManager.loadBundleFile(fuBundleData.name, fuBundleData.path)
        val oldHandle = propIdMap[oldData.id]
        oldHandle?.let {
            mBundleManager.destroyControllerBundle(it)
            propIdMap.remove(oldData.id)
            propTypeMap.remove(oldData.id)
        }
        if (newHandle <= 0) {
            FULogger.e(TAG, "load Prop bundle failed bundle path:${fuBundleData.path}")
            return
        }
        propIdMap[newData.id] = newHandle
        propTypeMap[newData.id] = newData.remark!! as LinkedHashMap<String, Any>
        if (newData.enable) {
            mBundleManager.bindControllerBundle(newHandle)
        }
        setPropParams(newHandle, newData)
    }


    /**
     * 道具参数设置
     * @param handle Int
     * @param data FUFeaturesData
     */
    private fun setPropParams(handle: Int, data: FUFeaturesData) {
        val remark = data.remark as LinkedHashMap<String, Any>
        if (remark[PropParam.PROP_TYPE] == PropParam.PROP_TYPE_ANIMOJI) {
            itemSetParam(handle, PropParam.FLIP_3DH, 1)
            itemSetParam(handle, PropParam.FLIP_TRACK, 1)
            itemSetParam(handle, PropParam.FLIP_LIGHT, 1)
            if (data.param.containsKey(PropParam.FACE_FOLLOW)) {
                // 设置 Animoji 跟随人脸 需要GL线程设置
                doGLThreadAction {
                    itemSetParam(handle, PropParam.FACE_FOLLOW, if (data.param[PropParam.FACE_FOLLOW] as Boolean) 1.0 else 0.0)
                    itemSetParam(handle, PropParam.IS_FIX_X, if (data.param[PropParam.FACE_FOLLOW] as Boolean) 0.0 else 1.0)
                    itemSetParam(handle, PropParam.IS_FIX_Y, if (data.param[PropParam.FACE_FOLLOW] as Boolean) 0.0 else 1.0)
                    itemSetParam(handle, PropParam.IS_FIX_Z, if (data.param[PropParam.FACE_FOLLOW] as Boolean) 0.0 else 1.0)
                    itemSetParam(handle, PropParam.FIX_ROTATION, if (data.param[PropParam.FACE_FOLLOW] as Boolean) 0.0 else 1.0)
                }
            }
        } else if (remark[PropParam.PROP_TYPE] == PropParam.PROP_TYPE_BG_SEG_CUSTOM) {
            val rotMode = mFURenderBridge.mRotationMode
            itemSetParam(handle, PropParam.ROTATION_MODE, rotMode.toDouble())
            itemSetParam(handle, PropParam.BG_ALIGN_TYPE, 1)
        } else if (remark[PropParam.PROP_TYPE] == PropParam.PROP_TYPE_FINE_STICKER) {
            if (remark.containsKey(PropParam.IS_FLIP_POINTS)) {
                val flipPoints = if (mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE
                    || mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO || mFURenderBridge.cameraFacing == CameraFacingEnum.CAMERA_BACK
                ) 1.0 else 0.0
                itemSetParam(handle, PropParam.IS_FLIP_POINTS, flipPoints)
            }
            if (remark.containsKey(PropParam.IS_3D_FlipH)) {
                itemSetParam(handle, PropParam.IS_3D_FlipH, 1.0)
            }
            if (remark.containsKey(PropParam.FORCE_PORTRAIT)) {
                itemSetParam(handle, PropParam.FORCE_PORTRAIT, remark[PropParam.FORCE_PORTRAIT] as Int )
            }
        } else {
            data.param.forEach { (key, value) ->
                itemSetParam(handle, key, value)
            }
        }
    }


    //endregion

}