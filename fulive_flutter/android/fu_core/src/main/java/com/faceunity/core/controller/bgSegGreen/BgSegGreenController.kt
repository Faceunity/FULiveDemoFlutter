package com.faceunity.core.controller.bgSegGreen

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.enumeration.FUExternalInputEnum
import com.faceunity.core.utils.FULogger
import kotlin.math.sqrt


/**
 *
 * DESC：绿幕抠像调用控制器
 * Created on 2020/12/29
 *
 */
class BgSegGreenController : BaseSingleController() {


    private var zoom = 1.0
    private var centerX = 0.5
    private var centerY = 0.5

    /**
     * 加载绿幕抠像
     * @param featuresData FUFeaturesData
     */
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable)
        val remark = featuresData.remark!! as BgSegGreenRemark
        zoom = remark.zoom
        centerX = remark.centerX
        centerY = remark.centerY
        setBgSegGreenParams(featuresData.param)
    }

    internal fun setScale(sign: Long, zoom: Double, centerX: Double, centerY: Double) {
        FULogger.i(TAG, "setItemParam sign:${sign == modelSign}  zoom:$zoom   centerX:$centerX   centerY:$centerY")
        if (sign != modelSign) return
        this.zoom = zoom
        this.centerX = centerX
        this.centerY = centerY
        updateScale()
    }

    /**
     * 相机传感器，检测相机旋转，重设参数
     */
    internal fun updateRotationMode() {
        if (mControllerBundleHandle <= 0) {
            return
        }
        val rotMode = mFURenderBridge.mRotationMode
        itemSetParam(BgSegGreenParam.ROTATION_MODE, rotMode.toDouble())
    }

    /**
     * 相机摄像头切换，重设参数
     */
    internal fun updateFlipMode() {
        if (mControllerBundleHandle <= 0) {
            return
        }
        val rotMode = mFURenderBridge.mRotationMode
        itemSetParam(BgSegGreenParam.ROTATION_MODE, rotMode.toDouble())
        updateScale()
    }

    /**
     * 创建背景纹理
     * @param rgba ByteArray
     * @param width Int
     * @param height Int
     */
    internal fun createBgSegment(sign: Long, rgba: ByteArray, width: Int, height: Int) {
        if (sign != modelSign) return
        FULogger.i(TAG, "createBgSegment ")
        deleteItemTex(BgSegGreenParam.TAX_BG)
        createItemTex(BgSegGreenParam.TAX_BG, rgba, width, height)
    }

    /**
     * 移除背景纹理
     * @param sign Long
     */
    internal fun removeBgSegment(sign: Long) {
        if (sign != modelSign) return
        FULogger.i(TAG, "removeBgSegment ")
        deleteItemTex(BgSegGreenParam.TAX_BG)
    }

    /**
     * 资源释放
     */
    override fun release(unit: (() -> Unit)?) {
        super.release { deleteItemTex(BgSegGreenParam.TAX_BG) }
    }

    /**
     * 坐标换算
     */
    private fun updateScale() {
        val scale = sqrt(zoom)
        var pointX = centerX
        var pointY = centerY
        if (mFURenderBridge.externalInputType == FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO) {
            when (mFURenderBridge.mRotationMode) {
                1 -> {
                    pointY = centerX
                    pointX = 1 - centerY
                }
                2 -> {
                    pointX = 1 - centerX
                    pointY = 1 - centerY
                }
                3 -> {
                    pointX = centerY
                    pointY = 1 - centerX
                }
            }
        }
        val startX = pointX - scale * 0.5
        val startY = pointY - scale * 0.5
        val endX = pointX + scale * 0.5
        val endY = pointY + scale * 0.5
        itemSetParam(BgSegGreenParam.START_X, startX)
        itemSetParam(BgSegGreenParam.START_Y, startY)
        itemSetParam(BgSegGreenParam.END_X, endX)
        itemSetParam(BgSegGreenParam.END_Y, endY)
    }


    /**
     * 设置贴图参数
     * @param params LinkedHashMap<String, Any>?
     */
    private fun setBgSegGreenParams(params: LinkedHashMap<String, Any>? = null) {
        val rotMode = mFURenderBridge.mRotationMode
        itemSetParam(BgSegGreenParam.ROTATION_MODE, rotMode.toDouble())
        updateScale()
        params?.let { itemSetParam(it) }
    }


}