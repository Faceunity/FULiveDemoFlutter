package com.faceunity.core.controller.poster

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import com.faceunity.wrapper.faceunity
import kotlin.math.asin
import kotlin.math.atan2


/**
 *
 * DESC：
 * Created on 2020/12/28
 *
 */
class PosterController : BaseSingleController() {

    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        var handle = 0
        featuresData.bundle?.let {
            handle = mBundleManager.loadBundleFile(it.name, it.path)
        }
        if (handle <= 0) {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
            mControllerBundleHandle = -1
            FULogger.e(TAG, "loadControllerBundle failed handle:$handle  path:${featuresData.bundle?.path}")
            return
        }
        if (mControllerBundleHandle != handle) {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
        }
        mControllerBundleHandle = handle
    }

    /**
     * 海报换脸，输入图片
     * @param inputWidth Int
     * @param inputHeight Int
     * @param input ByteArray
     * @param landmark FloatArray
     */
    internal fun loadPosterPhoto(inputWidth: Int, inputHeight: Int, input: ByteArray, landmark: FloatArray) {
        val landmarks = floatArrayToDoubleArray(landmark)
        // 输入图片的宽
        itemSetParam("input_width", inputWidth)
        itemSetParam("input_height", inputHeight)
        itemSetParam("input_face_points", landmarks)
        SDKController.createTexForItem(mControllerBundleHandle, "tex_input", input, inputWidth, inputHeight)
    }

    /**
     * 海报换脸 ，输入模版
     * @param inputWidth Int
     * @param inputHeight Int
     * @param input ByteArray
     * @param landmark FloatArray
     */
    internal fun loadPosterTemplate(inputWidth: Int, inputHeight: Int, input: ByteArray, landmark: FloatArray) {
        val landmarks: DoubleArray = floatArrayToDoubleArray(landmark)
        // 模板图片的宽
        itemSetParam("template_width", inputWidth)
        // 模板图片的高
        itemSetParam("template_height", inputHeight)
        // 图片的特征点，75个点
        itemSetParam("template_face_points", landmarks)
        // 模板图片的 RGBA byte数组
        SDKController.createTexForItem(mControllerBundleHandle, "tex_template", input, inputWidth, inputHeight)
    }


    /**
     * 海报换脸，输入人脸五官，自动变形调整
     *
     * @param value 范围 [0-1]，0 为关闭
     */
    internal fun fixPosterFaceParam(value: Double) {
        FULogger.i(TAG, "fixPosterFaceParam value:$value")
        itemSetParam("warp_intensity", value)
    }


    /**
     * 校验脸部是否端正
     */
    internal fun checkRotation(): Boolean {
        val rotations: FloatArray = getRotationData()
        val x = rotations[0].toDouble()
        val y = rotations[1].toDouble()
        val z = rotations[2].toDouble()
        val w = rotations[3].toDouble()
        val yaw = atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y)) / Math.PI * 180
        val pitch = asin(2 * (w * y - z * x)) / Math.PI * 180
        val roll = atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z)) / Math.PI * 180
        // 左右 pitch, 俯仰 yaw，摇摆 roll
        return yaw > 30 || yaw < -30 || pitch > 15 || pitch < -15
    }


    /**
     * 获取人脸信息跟踪接口
     *
     * @return
     */
    private fun getRotationData(): FloatArray {
        val rotationData = FloatArray(4)
        SDKController.getFaceInfo(0, "rotation", rotationData)
        return rotationData
    }


    /**
     * 获取 landmarks 点位
     *
     * @param faceId    0,1...
     * @param landmarks float array
     */
    internal fun getLandmarksData(faceId: Int, landmarks: FloatArray) {
        val isTracking = faceunity.fuIsTracking()
        if (isTracking > 0) {
            SDKController.getFaceInfo(faceId, "landmarks_origin", landmarks)
        }
    }

    /**
     * 获取 人脸信息跟踪接口
     *
     * @return
     */
    internal fun getFaceRectData(i: Int, rotMode: Int): FloatArray {
        val faceRectData = FloatArray(4)
        SDKController.getFaceInfo(i, "face_rect_origin", faceRectData)
        return faceRectData
    }

    /**
     * 释放资源
     */
    override fun release(unit: (() -> Unit)?) {
        super.release {
            deleteItemTex("tex_template")
            deleteItemTex("tex_input")
        }
    }


    /**
     * 数据转换
     * @param input FloatArray
     * @return DoubleArray
     */
    private fun floatArrayToDoubleArray(input: FloatArray): DoubleArray {
        val output = DoubleArray(input.size)
        for (i in input.indices) {
            output[i] = input[i].toDouble()
        }
        return output
    }


}