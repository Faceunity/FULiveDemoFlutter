package com.faceunity.core.faceunity

import com.faceunity.core.enumeration.FUAITypeEnum
import com.faceunity.core.enumeration.FUFaceProcessorDetectModeEnum
import com.faceunity.core.enumeration.FUInputBufferEnum
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.FileUtils
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * DESC：AI驱动
 * Created on 2021/2/25
 *
 */
class FUAIKit private constructor() {

    companion object {
        const val TAG = "KIT_FUAIController"

        @Volatile
        private var INSTANCE: FUAIKit? = null

        @JvmStatic
        fun getInstance(): FUAIKit {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = FUAIKit()
                    }
                }
            }
            return INSTANCE!!
        }
    }
    //region 驱动

    private val hasLoadAIProcessor = ConcurrentHashMap<Int, Int>()

    /**
     * 加载AI驱动
     * @param path String
     * @param aiType FUAITypeEnum
     */
    fun loadAIProcessor(path: String, aiType: FUAITypeEnum) {
        val hasLoaded = isAIProcessorLoaded(aiType)
        if (hasLoaded) {
            when (aiType) {
                FUAITypeEnum.FUAITYPE_FACEPROCESSOR -> faceProcessorSetMaxFaces(maxFaces)
                FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR -> humanProcessorSetMaxHumans(maxHumans)
            }
            return
        }
        if (path.isBlank()) {
            FULogger.e(TAG, "loadAIProcessor failed   type=${aiType.type}  bundle path isBlank")
            return
        }
        val buffer = FileUtils.loadBundleFromLocal(FURenderManager.mContext, path)
        if (buffer == null) {
            FULogger.e(TAG, "loadAIProcessor failed  file not found: $path")
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_FILE_NOT_FOUND, "file not found: $path")
            return
        }
        if (aiType == FUAITypeEnum.FUAITYPE_TONGUETRACKING) {
            if (SDKController.loadTongueModel(buffer, path)) {
                hasLoadAIProcessor[aiType.type] = aiType.type
            }
            return
        }
        if (SDKController.loadAIModelFromPackage(buffer, aiType.type, path)) {
            when (aiType) {
                FUAITypeEnum.FUAITYPE_FACEPROCESSOR -> faceProcessorSetMaxFaces(maxFaces)
                FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR -> humanProcessorSetMaxHumans(maxHumans)
            }
            hasLoadAIProcessor[aiType.type] = aiType.type
        }
    }

    /**
     * 判断当前类型AI驱动是否已经加载
     * @param aiType FUAITypeEnum
     * @return Boolean
     */
    fun isAIProcessorLoaded(aiType: FUAITypeEnum) = SDKController.isAIModelLoaded(aiType.type)

    /**
     * 根据类型释放AI驱动
     * @param aiType FUAITypeEnum
     */
    fun releaseAIProcessor(aiType: FUAITypeEnum) {
        SDKController.releaseAIModel(aiType.type)
        hasLoadAIProcessor.remove(aiType.type)
    }

    /**
     * 释放所有AI驱动
     */
    fun releaseAllAIProcessor() {
        val entries = hasLoadAIProcessor.entries.iterator()
        while (entries.hasNext()) {
            val entry = entries.next()
            SDKController.releaseAIModel(entry.key)
        }
        hasLoadAIProcessor.clear()
    }
    //endregion 业务调用
    //region 业务调用

    /**
     * 获取检测到的人脸数量
     * @param imgBuffer ByteArray
     * @param format Int 数据类型
     * @param width Int 宽
     * @param height Int 高
     * @return Int
     */
    fun trackFace(imgBuffer: ByteArray, format: FUInputBufferEnum, width: Int, height: Int): Int {
        return trackFace(imgBuffer, format, width, height, -1)
    }

    /**
     * 获取检测到的人脸数量
     * @param imgBuffer ByteArray
     * @param format Int 数据类型
     * @param width Int 宽
     * @param height Int 高
     * @param rotMode Int 识别角度
     * @return Int
     */
    fun trackFace(imgBuffer: ByteArray, format: FUInputBufferEnum, width: Int, height: Int, rotMode: Int): Int {
        if (width <= 0 || height <= 0) {
            return 0
        }
        val currRotMode = SDKController.getCurrentRotationMode()
        if (rotMode >= 0 && rotMode != currRotMode) {
            SDKController.setDefaultRotationMode(rotMode)
        }
        SDKController.trackFace(imgBuffer, format.type, width, height)
        val res = SDKController.isTracking()
        if (rotMode >= 0 && rotMode != currRotMode) {
            SDKController.setDefaultRotationMode(currRotMode)
        }
        return res
    }


    /**
     * 获取检测到的人脸数量
     *
     * @return
     */
    fun isTracking() = SDKController.isTracking()


    /**
     * 获取人脸信息
     *
     */
    fun getFaceInfo(faceId: Int, name: String, value: FloatArray) {
        SDKController.getFaceInfo(faceId, name, value)
    }

    /**
     * 获取人脸信息
     *
     */
    fun getFaceInfo(faceId: Int, name: String, value: IntArray) {
        SDKController.getFaceInfo(faceId, name, value)
    }

    /**
     * 重置相机缓存
     */
    fun clearCameraCache() {
        SDKController.onCameraChange()
    }



    /**
     * 设置检测驱动类型
     * @param aiType FUAITypeEnum
     */
    fun setTrackFaceAIType(aiType: FUAITypeEnum) {
        SDKController.setTrackFaceAIType(aiType.type)
    }

    //endregion 算法设置


    //region  faceProcessor
    /*最大人脸数*/
    var maxFaces = 4
        set(value) {
            if (value != field)
                field = value
            faceProcessorSetMaxFaces(value)

        }

    /**
     * 设置最大人脸识别数目
     * @param maxFaces Int 最多支持 8 个
     */
    fun faceProcessorSetMaxFaces(maxFaces: Int){
        SDKController.setMaxFaces(maxFaces)
    }


    /**
     * 设置人脸检测距离的接口
     * @param ratio Float 数值范围0.0至1.0，最小人脸的大小和输入图形宽高短边的比值。默认值 0.2
     */
    fun faceProcessorSetMinFaceRatio(ratio: Float) {
        SDKController.faceProcessorSetMinFaceRatio(ratio)
    }


    /**
     * 设置 FaceProcessor 人脸算法模块跟踪
     * @param fov Float 要设置的FaceProcessor人脸算法模块跟踪 fov。
     */
    fun faceProcessorSetFov(fov: Float) {
        SDKController.setFaceProcessorFov(fov)
    }

    /**
     * 获取 HumanProcessor 人体算法模块头发 mask
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param mask FloatArray 当前HumanProcessor人体算法模块头发mask，长度由 mask_width * mask_height 决定。
     */
    fun faceProcessorGetResultHairMask(index: Int, mask: FloatArray) {
        SDKController.faceProcessorGetResultHairMask(index, mask)
    }

    /**
     * 获取 HumanProcessor 人体算法模块头部mask
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param mask FloatArray 当前HumanProcessor人体算法模块头部 mask，长度由 mask_width * mask_height 决定。
     */
    fun faceProcessorGetResultHeadMask(index: Int, mask: FloatArray) {
        SDKController.faceProcessorGetResultHeadMask(index, mask)
    }

    /**
     * 默认的视频模式下，不保证每帧都检测出人脸；对于图片场景，要设置图片模式
     *
     * @param mode IMAGE 图片模式, VIDEO 视频模式, 默认 VIDEO
     */
    fun faceProcessorSetDetectMode(mode: FUFaceProcessorDetectModeEnum) {
        SDKController.setFaceProcessorDetectMode(mode.type)
    }


    //endregion
    //region  humanProcessor
    /*最大人体数*/
    var maxHumans = 1
        set(value) {
            if (value != field)
                field = value
            humanProcessorSetMaxHumans(value)

        }

    /**
     * 重置 HumanProcessor 人体算法模块状态
     */
    fun humanProcessorReset() {
        SDKController.humanProcessorReset()
    }

    /**
     * 设置HumanProcessor人体算法模块跟踪人体数
     * @param maxHumans Int 默认值是 1，最大值无上限；性能随人数增加线性下降。
     */
    fun humanProcessorSetMaxHumans(maxHumans: Int) {
        SDKController.humanProcessorSetMaxHumans(maxHumans)
    }

    /**
     * 获取检测到的人体数量
     *
     * @return 当前检测到人体数
     */
    fun humanProcessorGetNumResults() = SDKController.humanProcessorGetNumResults()


    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体数。
     * @return Int 当前跟踪到人体数
     */
    fun humanProcessorGetResultTrackId(index: Int) = SDKController.humanProcessorGetResultTrackId(index)


    /**
     * 人体算法模块跟踪人体框
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param rect FloatArray 当前跟踪到人体的人体框，4个 float 大小。
     */
    fun humanProcessorGetResultRect(index: Int, rect: FloatArray) {
        SDKController.humanProcessorGetResultRect(index, rect)
    }

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体2D 关键点
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param joint2ds FloatArray 当前跟踪到人体的人体2D 关键点，长度 50。返回数据格式为一维数组：[x0,y0, x1,y1, x2,y2...,x24,y24]，数值单位是：**像素。
     */
    fun humanProcessorGetResultJoint2ds(index: Int, joint2ds: FloatArray) {
        SDKController.humanProcessorGetResultJoint2ds(index, joint2ds)
    }

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体3D 关键点
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param joint2ds FloatArray 当前跟踪到人体的人体3D骨骼信息，长度固定。返回数据格式为：[x0,y0,z0, x1,y1,z1, x2,y2,z2, ..., x24,y24,z24]。数值单位
     */
    fun humanProcessorGetResultJoint3ds(index: Int, joint2ds: FloatArray) {
        SDKController.humanProcessorGetResultJoint3ds(index, joint2ds)
    }

    /**
     *获取HumanProcessor人体算法模块跟踪人体3D关键点时使用的fov
     * @return Float 人体算法模块跟踪人体3D关键点时使用的fov
     */
    fun humanProcessorGetFov() = SDKController.humanProcessorGetFov()

    /**
     * 设置HumanProcessor人体算法模块跟踪人体3D关键点时使用的fov，默认30度。
     * @param fov Float 设置HumanProcessor人体算法模块跟踪人体3D关键点时使用的fov，角度制
     */
    fun humanProcessorSetFov(fov: Float) {
        SDKController.humanProcessorSetFov(fov)
    }

    /**
     * 在决定获取骨骼动画帧数据之前，需要在初始化阶段调用fuHumanProcessorSetBonemap接口设置算法内部的bonemap。
     * @param data ByteArray  参考bonemap.json，详询我司技术支持。
     */
    fun humanProcessorSetBoneMap(data: ByteArray) {
        SDKController.humanProcessorSetBoneMap(data)
    }

    /**
     * 获取Model坐标系下的和 bonemap 中对应骨骼的local变换帧数据。
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param data FloatArray 返回的浮点数组
     * [M0, M1, M2, ..., Mn] 其中Mx表示列主序存储的bonemap中对应index骨骼的当前姿态的局部变换矩阵（4 * 4），长度为16 * n的数组，其中n为bonemap中骨骼数。
     */
    fun humanProcessorGetResultTransformArray(index: Int, data: FloatArray) {
        SDKController.humanProcessorGetResultTransformArray(index, data)
    }

    /**
     * 获取 Model 矩阵，列主序存储的 4x4矩阵，长度为16的数组。
     * @param index Int 第index个人体，从0开始，不超过fuHumanProcessorGetNumResults。
     * @param matrix FloatArray 返回的浮点数组
     */
    fun humanProcessorGetResultModelMatrix(index: Int, matrix: FloatArray) {
        SDKController.humanProcessorGetResultModelMatrix(index, matrix)
    }

    /**
     * 获取 HumanProcessor 人体算法模块全身 mask。
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @param mask FloatArray 当前HumanProcessor人体算法模块全身mask，长度由 mask_width * mask_height 决定 。
     * @return Int
     */
    fun humanProcessorGetResultHumanMask(index: Int, mask: FloatArray) = SDKController.humanProcessorGetResultHumanMask(index, mask)

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体动作类型。
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @return Int 人体算法模块跟踪人体动作类型。
     */
    fun humanProcessorGetResultActionType(index: Int) = SDKController.humanProcessorGetResultActionType(index)


    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体动作置信度
     * @param index Int 第 index 个人体，从 0 开始，不超过 fuHumanProcessorGetNumResults。
     * @return Float 人体算法模块跟踪人体动作置信度。
     */
    fun humanProcessorGetResultActionScore(index: Int) = SDKController.humanProcessorGetResultActionScore(index)


//endregion

    //region handProcessor

    /**
     * 获取检测到的人手数量
     *
     * @return
     */
    fun handProcessorGetNumResults() = SDKController.handDetectorGetResultNumHands()

    /**
     * 获取HandGesture手势算法模块跟踪手势框
     * @param index Int 第 index 个手势，从 0 开始，不超过 fuHandDetectorGetResultNumHands 结果
     * @param rect FloatArray 手势算法模块跟踪手势框，长度为 4
     * @return Int
     */
    fun handDetectorGetResultHandRect(index: Int, rect: FloatArray) = SDKController.handDetectorGetResultHandRect(index, rect)

    /**
     * 获取 HandGesture 手势算法模块跟踪手势类别
     * @param index Int 第 index 个手势，从 0 开始，不超过 fuHandDetectorGetResultNumHands 结果
     * @return Int 手势算法模块跟踪手势类别
     */
    fun handDetectorGetResultGestureType(index: Int) = SDKController.handDetectorGetResultGestureType(index)

    /**
     * 获取 HandGesture 手势算法模块跟踪手势置信度
     * @param index Int 第 index 个手势，从 0 开始，不超过 fuHandDetectorGetResultNumHands 结果
     * @return Float 手势算法模块跟踪手势置信度
     */
    fun handDetectorGetResultHandScore(index: Int) = SDKController.handDetectorGetResultHandScore(index)

    //endregion

}