package com.faceunity.core.support

import com.faceunity.core.avatar.control.AvatarController
import com.faceunity.core.bundle.BundleManager
import com.faceunity.core.controller.action.ActionRecognitionController
import com.faceunity.core.controller.animationFilter.AnimationFilterController
import com.faceunity.core.controller.antialiasing.AntialiasingController
import com.faceunity.core.controller.bgSegGreen.BgSegGreenController
import com.faceunity.core.controller.bodyBeauty.BodyBeautyController
import com.faceunity.core.controller.facebeauty.FaceBeautyController
import com.faceunity.core.controller.hairBeauty.HairBeautyController
import com.faceunity.core.controller.littleMakeup.LightMakeupController
import com.faceunity.core.controller.makeup.MakeupController
import com.faceunity.core.controller.musicFilter.MusicFilterController
import com.faceunity.core.controller.poster.PosterController
import com.faceunity.core.controller.prop.PropContainerController
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.entity.FURenderOutputData
import com.faceunity.core.enumeration.*
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.core.utils.BitmapUtils
import com.faceunity.core.utils.DecimalUtils
import com.faceunity.core.utils.FULogger
import com.faceunity.wrapper.faceunity
import java.util.*


/**
 *
 * DESC：
 * Created on 2021/2/8
 *
 */
class FURenderBridge private constructor() {

    companion object {
        const val TAG = "KIT_FURenderBridge"

        @Volatile
        private var INSTANCE: FURenderBridge? = null

        @JvmStatic
        internal fun getInstance(): FURenderBridge {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = FURenderBridge()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private val renderLock = Any()


    fun onDestroy(isSafe: Boolean) {
        synchronized(renderLock) {
            mRotationMode = -1
            externalInputType = null
            cameraFacing = null
            inputOrientation = -1
            deviceOrientation = -1
            inputTextureType = null
            inputTextureMatrix = null
            inputBufferMatrix = null
            outputMatrix = null
            mGLThreadId = -1
            mFrameId = 0
            BundleManager.getInstance().release()
            mGLEventQueue.clear()
            SDKController.onCameraChange()//重置人脸跟踪状态
            SDKController.humanProcessorReset()//重置 HumanProcessor 人体算法模块状态
            SDKController.done()
            if (isSafe) {
                SDKController.onDeviceLostSafe()//进行资源清理和回收
            } else {
                SDKController.onDeviceLost()//进行资源清理和回收
            }

        }
    }


    private val mFURenderKit by lazy { FURenderKit.getInstance() }


    /**
     * 数据状态缓存
     */
    /* 帧数*/
    private var mFrameId = 0

    /* 人脸识别方向 取值范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度*/
    internal var mRotationMode = -1

    //传入纹理的类型（传入数据没有纹理则无需调用） camera OES纹理：1  普通2D纹理：0
    private var inputTextureType: FUInputTextureEnum? = null

    //相机朝向
    internal var cameraFacing: CameraFacingEnum? = null

    // 输入的画面数据方向
    private var inputOrientation = -1

    // 设备方向
    private var deviceOrientation = -1

    //输入源数据类型
    internal var externalInputType: FUExternalInputEnum? = null

    /*输入纹理旋转矩阵*/
    private var inputTextureMatrix: FUTransformMatrixEnum? = null

    /*数据Buffer旋转矩阵*/
    private var inputBufferMatrix: FUTransformMatrixEnum? = null


    /*输出旋转矩阵*/
    private var outputMatrix: FUTransformMatrixEnum? = null


    private var lastFrameRenderTexture = false

    //region 控制器
    internal val mFaceBeautyController by lazy { FaceBeautyController() }//美颜
    internal val mMakeupController by lazy { MakeupController() }//美妆
    internal val mActionRecognitionController by lazy { ActionRecognitionController() }//美妆
    internal val mAnimationFilterController by lazy { AnimationFilterController() }//动漫滤镜
    internal val mAntialiasingController by lazy { AntialiasingController() }//3D抗锯齿
    internal val mBgSegGreenController by lazy { BgSegGreenController() }//绿幕抠像
    internal val mBodyBeautyController by lazy { BodyBeautyController() }//美体
    internal val mHairBeautyController by lazy { HairBeautyController() }//美发
    internal val mLightMakeupController by lazy { LightMakeupController() }//轻美妆
    internal val mMusicFilterController by lazy { MusicFilterController() }//音乐滤镜
    internal val mPropContainerController by lazy { PropContainerController() }//多道具-贴纸
    internal val mPosterController by lazy { PosterController() }//海报换脸
    internal val mAvatarController by lazy { AvatarController() }//海报换脸

    //endregion

    //region 渲染业务


    /**
     * 单帧渲染接口，判断是否存在GLContext进行不同处理
     * @param input FURenderInputData
     * @param type Int 类型 默认为0  1为海报换脸
     * @return Int
     */
    fun renderWithInput(input: FURenderInputData, type: Int = 0): FURenderOutputData {
        synchronized(renderLock) {
            updateRenderEnvironment(input)
            mGLThreadId = Thread.currentThread().id
            return drawFrame(input, type)
        }
    }


    /**
     * 根据渲染数据，进行业务分发
     * @param data FURenderInputData
     * @param type Int
     * @return FURenderOutputData
     */
    private fun drawFrame(data: FURenderInputData, type: Int): FURenderOutputData {
        prepareDrawFrame()//画面处理
        val texId = data.texture?.texId ?: 0
        val inputTextureType = data.texture?.inputTextureType
        val buffer = data.imageBuffer?.buffer
        val inputBufferType = data.imageBuffer?.inputBufferType
        val needReadBack = data.renderConfig.isNeedBufferReturn
        if (data.width <= 0 || data.height <= 0) {
            FULogger.e(TAG, "renderInput data is illegal   width:${data.width}  height:${data.height}  ")
            return FURenderOutputData()
        }
        val inputBufferChange = needChangeWithAndHeight(inputBufferMatrix ?: FUTransformMatrixEnum.CCROT0)
        val inputTextureChange = needChangeWithAndHeight(inputTextureMatrix ?: FUTransformMatrixEnum.CCROT0)
        val outputChange = needChangeWithAndHeight(outputMatrix ?: FUTransformMatrixEnum.CCROT0)
        val needChangedBuffer = (inputBufferChange && !outputChange) || (!inputBufferChange && outputChange)
        val needChangedTexture = (inputTextureChange && !outputChange) || (!inputTextureChange && outputChange)
        if (data.renderConfig.isRenderFaceBeautyOnly && texId >= 0 && inputTextureType != null) {
            lastFrameRenderTexture = false
            return drawFrameBeautify(data.width, data.height, texId, inputTextureType.type, needChangedTexture)
        }
        if (inputBufferType == FUInputBufferEnum.FU_FORMAT_YUV_BUFFER) {
            lastFrameRenderTexture = false
            return drawFrameYUV(data.width, data.height, data.imageBuffer?.buffer, data.imageBuffer?.buffer1, data.imageBuffer?.buffer2, needReadBack, needChangedTexture, needChangedBuffer)
        }
        if (texId > 0 && inputTextureType != null && buffer != null && inputBufferType != null) {
            return if (type == 1) {
                lastFrameRenderTexture = false
                drawFrameForPoster(data.width, data.height, texId, inputTextureType.type, buffer, inputBufferType.type)
            } else {
                lastFrameRenderTexture = false
                drawFrameDualInput(data.width, data.height, texId, inputTextureType.type, buffer, inputBufferType.type, needReadBack, needChangedTexture, needChangedBuffer)
            }
        } else if (texId > 0 && inputTextureType != null) {
            if (!lastFrameRenderTexture) {
                lastFrameRenderTexture = true
                clearCacheResource()
            }
            return drawFrameTexture(data.width, data.height, texId, inputTextureType.type, needChangedTexture)
        } else if (buffer != null && inputBufferType != null) {
            lastFrameRenderTexture = false
            return drawFrameImg(data.width, data.height, buffer, inputBufferType.type, needReadBack, needChangedTexture, needChangedBuffer)
        }
        return FURenderOutputData()
    }


    private fun needChangeWithAndHeight(matrix: FUTransformMatrixEnum): Boolean {
        return matrix == FUTransformMatrixEnum.CCROT90 || matrix == FUTransformMatrixEnum.CCROT270
                || matrix == FUTransformMatrixEnum.CCROT90_FLIPVERTICAL || matrix == FUTransformMatrixEnum.CCROT90_FLIPHORIZONTAL
    }


    /**
     * 海报换脸渲染业务
     * @param width Int
     * @param height Int
     * @param texId Int
     * @param inputTextureType Int
     * @param buffer ByteArray
     * @param imgType Int
     * @return Int
     */
    private fun drawFrameForPoster(width: Int, height: Int, texId: Int, inputTextureType: Int, buffer: ByteArray, imgType: Int): FURenderOutputData {
        if (texId <= 0) {
            FULogger.e(TAG, "drawFrameForPoster data is illegal  texId:$texId")
            return FURenderOutputData()
        }
        val flags = getRenderFlags(texId, inputTextureType)
        val fuTex = SDKController.fuRenderDualInput(width, height, mFrameId++, intArrayOf(mPosterController.mControllerBundleHandle), texId, flags, buffer, imgType)
        if (fuTex <= 0) {
            SDKController.callBackSystemError()
        }
        return FURenderOutputData(FURenderOutputData.FUTexture(fuTex, width, height))
    }


    /**
     * 渲染业务具体实现-单美颜渲染
     * @param texId Int
     * @param width Int
     * @param height Int
     * @return Int
     */
    private fun drawFrameBeautify(width: Int, height: Int, texId: Int, inputTextureType: Int, needChangedTexture: Boolean): FURenderOutputData {
        val flags = getRenderFlags(texId, inputTextureType)
        val fuTex = SDKController.fuRenderBeautifyOnly(width ,height, mFrameId++, BundleManager.getInstance().renderBindBundles,flags,texId)
        if (fuTex <= 0) {
            SDKController.callBackSystemError()
        }
        val retTexH = if (needChangedTexture) width else height
        val retTexW = if (needChangedTexture) height else width

        return FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH))

    }

    /**
     * 渲染业务具体实现-YUV渲染
     * @param y_buffer ByteArray?
     * @param u_buffer ByteArray?
     * @param v_buffer ByteArray?
     * @param width Int
     * @param height Int
     * @return Int
     */
    private fun drawFrameYUV(
        width: Int, height: Int, y_buffer: ByteArray?, u_buffer: ByteArray?, v_buffer: ByteArray?,
        needReadBack: Boolean, needChangedTexture: Boolean, needChangedBuffer: Boolean
    ): FURenderOutputData {
        if (y_buffer == null || u_buffer == null || v_buffer == null) {
            FULogger.e(TAG, "drawFrameYUV data is illegal  y_buffer:${y_buffer == null}  u_buffer:${u_buffer == null} v_buffer:${v_buffer == null} width:$width  height:$height  ")
            return FURenderOutputData()
        }
        var readBackBuffer: ByteArray? = null
        val uvStride = width shr 1
        val retTexH = if (needChangedTexture) width else height
        val retTexW = if (needChangedTexture) height else width
        val retBufH = if (needChangedBuffer) width else height
        val retBufW = if (needChangedBuffer) height else width
        val retStride = retBufW shr 1
        val flags = getRenderFlags(0, 0)
        //YUV -> NV21
        val buffer = BitmapUtils.YUVTOVN21(y_buffer,u_buffer,v_buffer)
        if (needReadBack) {
            readBackBuffer = ByteArray(buffer.size)
        }
        val fuTex = SDKController.fuRenderImg(width, height, mFrameId++, BundleManager.getInstance().renderBindBundles, flags, buffer, FUInputBufferEnum.FU_FORMAT_NV21_BUFFER.type,retBufW,retBufH ,readBackBuffer )
        if (fuTex <= 0) {
            SDKController.callBackSystemError()
        }
        return if (needReadBack) {
            val yBackBuffer = ByteArray(y_buffer.size)
            val uBackBuffer = ByteArray(u_buffer.size)
            val vBackBuffer = ByteArray(v_buffer.size)
            BitmapUtils.NV21ToYUV(readBackBuffer!!,yBackBuffer,uBackBuffer,vBackBuffer)
            FURenderOutputData(
                FURenderOutputData.FUTexture(fuTex, retTexW, retTexH),
                FURenderOutputData.FUImageBuffer(retBufW, retBufH, DecimalUtils.copyArray(yBackBuffer), DecimalUtils.copyArray(uBackBuffer), DecimalUtils.copyArray(vBackBuffer), retBufW, retStride, retStride)
            )
        } else {
            FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH))
        }
    }

    /**
     * 渲染业务具体实现-双输入
     * @param width Int
     * @param height Int
     * @param texId Int
     * @param inputTextureType Int
     * @param buffer ByteArray
     * @param imgType Int
     * @param needReadBack Boolean
     * @param needChangedTexture Boolean
     * @param needChangedBuffer Boolean
     * @return FURenderOutputData
     */
    private fun drawFrameDualInput(
        width: Int, height: Int, texId: Int, inputTextureType: Int, buffer: ByteArray, imgType: Int,
        needReadBack: Boolean, needChangedTexture: Boolean, needChangedBuffer: Boolean
    ): FURenderOutputData {
        val flags = getRenderFlags(texId, inputTextureType)
        var readBackBuffer: ByteArray? = null
        val retTexH = if (needChangedTexture) width else height
        val retTexW = if (needChangedTexture) height else width
        val retBufH = if (needChangedBuffer) width else height
        val retBufW = if (needChangedBuffer) height else width
        if (needReadBack) {
            readBackBuffer = ByteArray(buffer.size)
        }
        val fuTex = SDKController.fuRenderDualInput(width, height, mFrameId++, BundleManager.getInstance().renderBindBundles, texId, flags, buffer, imgType, retBufW, retBufH, readBackBuffer)
        if (fuTex <= 0) {
            SDKController.callBackSystemError()
        }
        return if (needReadBack) {
            FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH), FURenderOutputData.FUImageBuffer(retBufW, retBufH, readBackBuffer))
        } else {
            FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH))
        }
    }

    /**
     * 渲染业务具体实现-单纹理输入
     * @param width Int
     * @param height Int
     * @param texId Int
     * @param inputTextureType Int
     * @param needChangedTexture Boolean
     * @return FURenderOutputData
     */
    private fun drawFrameTexture(width: Int, height: Int, texId: Int, inputTextureType: Int, needChangedTexture: Boolean): FURenderOutputData {
        val retTexH = if (needChangedTexture) width else height
        val retTexW = if (needChangedTexture) height else width
        val flags = getRenderFlags(texId, inputTextureType)
        val fuTex = SDKController.fuRenderTexture(width, height, mFrameId++, BundleManager.getInstance().renderBindBundles, texId, flags)
        if (fuTex <= 0) {
            SDKController.callBackSystemError()
        }
        return FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH))
    }

    /**
     * 渲染业务具体实现-单图象
     * @param width Int
     * @param height Int
     * @param buffer ByteArray
     * @param imgType Int
     * @param needReadBack Boolean
     * @param needChangedTexture Boolean
     * @param needChangedBuffer Boolean
     * @return FURenderOutputData
     */
    private fun drawFrameImg(width: Int, height: Int, buffer: ByteArray, imgType: Int, needReadBack: Boolean = false, needChangedTexture: Boolean, needChangedBuffer: Boolean): FURenderOutputData {
        var readBackBuffer: ByteArray? = null
        val retTexH = if (needChangedTexture) width else height
        val retTexW = if (needChangedTexture) height else width
        val retBufH = if (needChangedBuffer) width else height
        val retBufW = if (needChangedBuffer) height else width
        if (needReadBack) {
            readBackBuffer = ByteArray(buffer.size)
        }
        val flags = getRenderFlags(0, 0)
        val fuTex = SDKController.fuRenderImg(width, height, mFrameId++, BundleManager.getInstance().renderBindBundles, flags, buffer, imgType, retBufW, retBufH, readBackBuffer)
        if (fuTex <= 0) {
            SDKController.callBackSystemError()
        }
        return if (needReadBack) {
            FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH), FURenderOutputData.FUImageBuffer(retBufW, retBufH, readBackBuffer))
        } else {
            FURenderOutputData(FURenderOutputData.FUTexture(fuTex, retTexW, retTexH))
        }
    }

    /**
     * 获取渲染属性
     * @param texId Int
     * @param inputTextureType Int
     * @param isFlip Boolean
     * @return Int
     */
    private fun getRenderFlags(texId: Int, inputTextureType: Int): Int {
        return if (texId > 0) inputTextureType else 0
    }


    //endregion 渲染业务


    //region 其他业务处理

    /**
     * 更新渲染环境
     * @param input FURenderInputData
     */
    private fun updateRenderEnvironment(input: FURenderInputData) {
        val renderConfig = input.renderConfig
        var needUpdateDefaultRotationMode = false
        var needUpdateFlipMode = false
        if (externalInputType != renderConfig.externalInputType
            || inputOrientation != renderConfig.inputOrientation
            || deviceOrientation != renderConfig.deviceOrientation
        ) {
            externalInputType = renderConfig.externalInputType
            inputOrientation = renderConfig.inputOrientation
            deviceOrientation = renderConfig.deviceOrientation
            needUpdateDefaultRotationMode = true
        }
        if (cameraFacing != renderConfig.cameraFacing) {
            SDKController.clearCacheResource()
            cameraFacing = renderConfig.cameraFacing
            needUpdateFlipMode = true
        }
        if (needUpdateFlipMode) {
            updateFlipMode()
        } else if (needUpdateDefaultRotationMode) {
            updateRotationMode()
        }
        if (renderConfig.inputTextureMatrix != inputTextureMatrix) {
            inputTextureMatrix = renderConfig.inputTextureMatrix
            SDKController.setInputCameraTextureMatrix(renderConfig.inputTextureMatrix.index)
        }
        if (renderConfig.inputBufferMatrix != inputBufferMatrix) {
            inputBufferMatrix = renderConfig.inputBufferMatrix
            SDKController.setInputCameraBufferMatrix(renderConfig.inputBufferMatrix.index)
        }
        if (renderConfig.outputMatrix != outputMatrix) {
            outputMatrix = renderConfig.outputMatrix
            if (renderConfig.outputMatrixEnable) SDKController.setOutputMatrix(renderConfig.outputMatrix.index)
        }
    }


    /**
     * 执行完成任务队列
     */
    private fun prepareDrawFrame() {
        //处理GL线程任务队列
        while (!mGLEventQueue.isNullOrEmpty()) {
            mGLEventQueue.removeAt(0).invoke()
        }
    }


    /**
     * 更新DefaultRotationMode
     */
    private fun updateRotationMode() {
        val rotMode = calculateRotationMode()
        if (mRotationMode == rotMode) {
            return
        }
        mRotationMode = rotMode
        SDKController.onCameraChange()
        SDKController.humanProcessorReset()
        SDKController.setDefaultRotationMode(mRotationMode)
        if (mFURenderKit.bgSegGreen != null) {
            mBgSegGreenController.updateRotationMode()
        }
        mPropContainerController.updateRotationMode()

    }

    /**
     * 相关道具点位镜像变更
     */
    private fun updateFlipMode() {
        val rotMode = calculateRotationMode()
        if (mRotationMode != rotMode) {
            mRotationMode = rotMode
            SDKController.onCameraChange()
            SDKController.humanProcessorReset()
            SDKController.setDefaultRotationMode(mRotationMode)
        }
        if (mFURenderKit.bgSegGreen != null) {
            mBgSegGreenController.updateFlipMode()
        }
        if (mFURenderKit.makeup != null) {
            mMakeupController.updateFlipMode()
        }
        mPropContainerController.updateFlipMode()
    }


    /**
     * 计算 RotationMode 人脸识别方向
     *
     * @return rotationMode  范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度
     */
    private fun calculateRotationMode(): Int {
        return when (externalInputType) {
            FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE -> { // 外部图片
                faceunity.FU_ROTATION_MODE_0
            }
            FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO -> { // 外部视频
                when (inputOrientation) {
                    90 -> faceunity.FU_ROTATION_MODE_270
                    270 -> faceunity.FU_ROTATION_MODE_90
                    180 -> faceunity.FU_ROTATION_MODE_180
                    else -> faceunity.FU_ROTATION_MODE_0
                }
            }
            else -> {// 外部相机
                if (cameraFacing == CameraFacingEnum.CAMERA_FRONT) {
                    ((inputOrientation + deviceOrientation + 90) % 360) / 90
                } else {
                    ((inputOrientation - deviceOrientation + 270) % 360) / 90
                }
            }
        }
    }


    /**
     * 计算 RotationMode 人脸识别方向
     *
     * @return rotationMode  范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度
     */
    fun calculateOrientationMode(): Int {
        return when (externalInputType) {
            FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE -> { // 外部图片
                faceunity.FU_ROTATION_MODE_0
            }
            FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO -> { // 外部视频
                when (inputOrientation) {
                    90 -> faceunity.FU_ROTATION_MODE_270
                    270 -> faceunity.FU_ROTATION_MODE_90
                    180 -> faceunity.FU_ROTATION_MODE_180
                    else -> faceunity.FU_ROTATION_MODE_0
                }
            }
            else -> { // 相机数据
                var orientation = 0
                if (cameraFacing == CameraFacingEnum.CAMERA_FRONT) {
                    orientation = when (deviceOrientation) {
                        180 -> 3
                        90 -> 0
                        0 -> 1
                        else -> 2
                    }
                } else {
                    orientation = when (deviceOrientation) {
                        180 -> 1
                        90 -> 0
                        0 -> 3
                        else -> 2
                    }
                }
                orientation
            }
        }
    }


    /**
     * 计算 手势识别道具 RotationMode 人脸识别方向
     *
     * @return rotationMode  范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度
     */
    internal fun calculateRotModeLagacy(): Int {
        return if (inputOrientation == 270) {
            if (cameraFacing == CameraFacingEnum.CAMERA_FRONT) {
                deviceOrientation / 90
            } else {
                (deviceOrientation - 180) / 90
            }
        } else {
            if (cameraFacing == CameraFacingEnum.CAMERA_FRONT) {
                (deviceOrientation + 180) / 90
            } else {
                deviceOrientation / 90
            }
        }
    }


    /**
     * 配置是否使用AI线程
     * @param isUser Boolean
     * @return Int
     */
    internal fun setUseAsyncAIInference(isUse: Boolean): Int {
        return SDKController.setUseAsyncAIInference(if (isUse) 1 else 0)
    }

    /**
     * 配置使用是否使用multi buffer
     * @param isUseMultiGPUTexture Boolean
     * @param isUseMultiCPUBuffer Boolean
     * @return Int
     */
    internal fun setUseMultiBuffer(isUseMultiGPUTexture: Boolean, isUseMultiCPUBuffer: Boolean): Int {
        return SDKController.setUseMultiBuffer(if (isUseMultiGPUTexture) 1 else 0, if (isUseMultiCPUBuffer) 1 else 0)
    }

    /**
     * 异步读取输出纹理 buffer
     * @param isUse Boolean
     * @return Int
     */
    internal fun setUseTexAsync(isUse: Boolean): Int {
        return SDKController.setUseTexAsync(if (isUse) 1 else 0)
    }


    /**
     * 清除缓存
     */
    internal fun clearCacheResource() {
        SDKController.clearCacheResource()
    }
//endregion

    //region GL渲染线程
    private var mGLEventQueue: MutableList<() -> Unit> = Collections.synchronizedList(ArrayList<() -> Unit>(16))

    private var mGLThreadId = -1L

    /* 切换到GL线程执行  */
    internal fun doGLThreadAction(unit: () -> Unit) {
        if (Thread.currentThread().id == mGLThreadId) {
            unit.invoke()
        } else {
            mGLEventQueue.add(unit)
        }
    }
//endregionGL渲染线程

}