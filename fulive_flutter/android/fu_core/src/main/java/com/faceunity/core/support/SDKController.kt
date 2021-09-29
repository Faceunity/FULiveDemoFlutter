package com.faceunity.core.support

import com.faceunity.core.faceunity.FURenderConfig
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.utils.FULogger
import com.faceunity.wrapper.faceunity


/**
 *
 * DESC：nama.jar 调用接口
 * Created on 2020/11/10
 *
 */
object SDKController {
    private const val TAG = "KIT_SDKController"

    /************************** 初始化部分 ******************************/

    /**
     * 获取版本信息。
     */
    internal fun getVersion(): String {
        val res = faceunity.fuGetVersion()
        FULogger.d(TAG, "fuGetVersion  res:$res  ")
        return res
    }

    /**
     * 获取证书权限码。
     */
    internal fun getModuleCode(code:Int): Int {
        val res = faceunity.fuGetModuleCode(code)
        FULogger.d(TAG, "fuGetModuleCode code $code  res:$res  ")
        return res
    }


    /**
     * 设置数据回写同步。
     */
    internal fun setReadbackSync(enable: Boolean) {
        FULogger.d(TAG, "fuSetReadbackSync  enable:$enable")
        faceunity.fuSetReadbackSync(enable)
    }


    /**
     * 检测接口是否已经初始化。
     * @return Boolean true 初始化成功  false 初始化失败
     */
    internal fun fuIsLibraryInit(): Boolean {
        val res = faceunity.fuIsLibraryInit()
        FULogger.d(TAG, "fuIsLibraryInit  res:$res  return:${res == 1}")
        return res == 1
    }

    /**
     * 初始化接口，App 启动后只需要 setup 一次即可
     * @param auth ByteArray 鉴权数据字节数组
     * isStep返回0代表失败,非0为成功
     */
    internal fun setup(auth: ByteArray): Boolean {
        val isStep = faceunity.fuSetup(ByteArray(0), auth)
        FULogger.d(TAG, "fuSetup isStep:$isStep    auth:$auth")
        if (isStep == 0) {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_AUTH, "setup failed")
            callBackSystemError()
        } else {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_AUTH, "setup success")
            getVersion()
        }
        return (isStep != 0)
    }

    /**
     * 初始化接口，App 启动后只需要 setupLocal 一次即可
     * @param auth ByteArray 鉴权数据字节数组
     * @param offlineBundle ByteArray 鉴权使用的bundle数组
     * isStep返回0代表失败,非0为成功
     */
    internal fun setupLocal(auth: ByteArray,offlineBundle:ByteArray): Boolean {
        val isStep = faceunity.fuSetupLocal(ByteArray(0), auth,offlineBundle)
        FULogger.d(TAG, "fuSetupLocal isStep:${if (isStep == null) "success" else "failed"}    auth:$auth")
        if (isStep == null) {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_AUTH, "setupLocal failed")
            callBackSystemError()
        } else {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_AUTH, "setupLocal success")
            getVersion()
        }
        return (isStep != null)
    }

    /**
     * 设置日志级别
     * @param level Int
     */
    internal fun setLogLevel(level: Int) {
        faceunity.fuSetLogLevel(level)
        FULogger.d(TAG, "fuSetLogLevel    level:$level")
    }


    /************************** 算法模块 ******************************/
    /**
     * 是否加载 AI 模型接口
     * @param type Int bundle对应的AI能力类型
     * @return Boolean 是否已加载
     */
    internal fun isAIModelLoaded(type: Int): Boolean {
        val res = faceunity.fuIsAIModelLoaded(type)
        FULogger.d(TAG, "fuIsAIModelLoaded  type:$type   isLoaded:${res == 1}")
        return res == 1
    }


    /**
     * 加载 AI 模型资源 SDK返回1代表成功，返回0代表失败。
     * @param buffer ByteArray AI能力模型
     * @param type Int bundle对应的AI能力类型
     */
    internal fun loadAIModelFromPackage(buffer: ByteArray, type: Int, path: String = ""): Boolean {
        val isLoaded = faceunity.fuLoadAIModelFromPackage(buffer, type)
        FULogger.d(TAG, "fuLoadAIModelFromPackage  type:$type  isLoaded:${isLoaded == 1}  path:$path")
        if (isLoaded == 1) {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_LOAD_AI_MODEL, "loadAIModelFromPackage type=$type ")
        } else {
            FURenderManager.mOperateCallback?.onFail(
                FURenderConfig.OPERATE_FAILED_LOAD_AI_MODEL,
                "loadAIModelFromPackage failed  type=$type isLoaded=$isLoaded"
            )
        }
        return isLoaded == 1
    }


    internal fun loadTongueModel(buffer: ByteArray, path: String = ""): Boolean {
        val isLoaded = faceunity.fuLoadTongueModel(buffer)
        FULogger.d(TAG, "fuLoadTongueModel isLoaded:${isLoaded == 1}  path:$path")
        if (isLoaded == 1) {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_LOAD_AI_MODEL, "loadTongueModel  ")
        } else {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_LOAD_AI_MODEL, "LoadTongueModel failed isLoaded=$isLoaded  ")
        }
        return isLoaded == 1
    }


    /**
     * 释放 AI 模型接口
     * @param type Int bundle对应的AI能力类型
     * @return Boolean 是否已释放
     */
    internal fun releaseAIModel(type: Int): Boolean {
        val res = faceunity.fuReleaseAIModel(type)
        FULogger.d(TAG, "fuReleaseAIModel  type:$type   res:${res == 1}")
        return res == 1
    }

    /**
     * 销毁单个道具接口
     * @param handle Int 道具句柄
     */
    internal fun destroyItem(handle: Int) {
        faceunity.fuDestroyItem(handle)
        FULogger.d(TAG, "fuDestroyItem   handle:$handle")
    }

    /**
     * 销毁全部道具句柄所对应的资源
     */
    internal fun destroyAllItems() {
        faceunity.fuDestroyAllItems()
        FULogger.d(TAG, "fuDestroyAllItems")
    }

    internal fun done() {
        faceunity.fuDone()
        FULogger.d(TAG, "fuDone")
    }

    /**
     * 特殊函数，当程序退出或OpenGL context准备销毁时，调用该函数，会进行资源清理和回收，所有系统占用的内存资源会被释放，包括GL的GPU资源以及内存。
     */
    internal fun onDeviceLost() {
        faceunity.fuOnDeviceLost()
        FULogger.d(TAG, "fuOnDeviceLost")
    }

    /**
     * 特殊函数，当程序退出或OpenGL context准备销毁时，调用该函数，会进行资源清理和回收，所有系统占用的内存资源会被释放，包括GL的GPU资源以及内存。
     */
    internal fun onDeviceLostSafe() {
        faceunity.fuOnDeviceLostSafe()
        FULogger.d(TAG, "fuOnDeviceLostSafe")
    }

    /**
     * 设置人脸检测距离的接口
     * @param ratio Float 数值范围0.0至1.0，最小人脸的大小和输入图形宽高短边的比值。默认值 0.2
     */
    internal fun faceProcessorSetMinFaceRatio(ratio: Float) {
        faceunity.fuFaceProcessorSetMinFaceRatio(ratio)
        FULogger.d(TAG, "fuFaceProcessorSetMinFaceRatio   ratio:$ratio")
    }

    /**
    \brief Set AI type for fuTrackFace and fuTrackFaceWithTongue interface
    \param ai_type, is a bit combination of FUAITYPE;
     */
    internal fun setTrackFaceAIType(type: Int) {
        faceunity.fuSetTrackFaceAIType(type)
        FULogger.d(TAG, "setTrackFaceAIType type: $type")
    }

    /**
    \brief Set tracking fov for ai model FaceProcessor.
     */
    internal fun setFaceProcessorFov(fov: Float) {
        faceunity.fuSetFaceProcessorFov(fov)
        FULogger.d(TAG, "fuSetFaceProcessorFov fov: $fov")
    }

    /**
     * 重置 HumanProcessor 人体算法模块状态
     */
    internal fun humanProcessorReset() {
        faceunity.fuHumanProcessorReset()
        FULogger.d(TAG, "fuHumanProcessorReset")
    }

    /**
     * 设置HumanProcessor人体算法模块跟踪人体数
     * @param maxHumans Int 默认值是 1，最大值无上限；性能随人数增加线性下降。
     */
    internal fun humanProcessorSetMaxHumans(maxHumans: Int) {
        faceunity.fuHumanProcessorSetMaxHumans(maxHumans)
        FULogger.d(TAG, "fuHumanProcessorSetMaxHumans  maxHumans:$maxHumans")
    }

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体数。
     * @return Int 当前跟踪到人体数
     */
    internal fun humanProcessorGetNumResults(): Int {
        val res = faceunity.fuHumanProcessorGetNumResults()
        FULogger.t(TAG, "fuHumanProcessorGetNumResults  res:$res")
        return res
    }

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体数。
     * @return Int 当前跟踪到人体数
     */
    internal fun humanProcessorGetResultTrackId(index: Int): Int {
        val res = faceunity.fuHumanProcessorGetResultTrackId(index)
        FULogger.d(TAG, "fuHumanProcessorGetResultTrackId  index:$index    res:$res")
        return res
    }

    /**
    \brief get ai model HumanProcessor's tracking rect with index.
    \param index, index of fuHumanProcessorGetNumResults
    \return rect array
     */
    internal fun humanProcessorGetResultRect(index: Int, rect: FloatArray): Int {
        val res = faceunity.fuHumanProcessorGetResultRect(index, rect)
        FULogger.d(TAG, "fuHumanProcessorGetResultRect  index:$index   rect:${rect.contentToString()}   res:$res")
        return res
    }

    /**
    \brief get ai model HumanProcessor's tracking 2d joint with index.
    \param index, index of fuHumanProcessorGetNumResults
    \param size,  size of return data.
     */
    internal fun humanProcessorGetResultJoint2ds(index: Int, joint2ds: FloatArray): Int {
        val res = faceunity.fuHumanProcessorGetResultJoint2ds(index, joint2ds)
        FULogger.d(TAG, "fuHumanProcessorGetResultJoint2ds  index:$index   joint2ds:${joint2ds.contentToString()}   res:$res")
        return res
    }

    /**
    \brief get ai model HumanProcessor's tracking 3d joint with index.
    \param index, index of fuHumanProcessorGetNumResults
    \param size,  size of return data.
     */
    internal fun humanProcessorGetResultJoint3ds(index: Int, joint3ds: FloatArray): Int {
        val res = faceunity.fuHumanProcessorGetResultJoint3ds(index, joint3ds)
        FULogger.d(TAG, "fuHumanProcessorGetResultJoint3ds   index:$index   joint3ds:${joint3ds.contentToString()}   res:$res")
        return res
    }

    /**
    \brief get ai model HumanProcessor's tracking fov, use to 3d joint projection.
    \return fov
     */
    internal fun humanProcessorGetFov(): Float {
        val res = faceunity.fuHumanProcessorGetFov()
        FULogger.d(TAG, "fuHumanProcessorGetFov      res:$res")
        return res
    }

    /**
    \brief get ai model HumanProcessor's tracking fov, use to 3d joint projection.
    \return fov
     */
    internal fun humanProcessorSetFov(fov: Float) {
        faceunity.fuHumanProcessorSetFov(fov)
        FULogger.d(TAG, "fuHumanProcessorSetFov      fov:$fov")
    }

    /**
    \brief set ai model HumanProcessor's 3d skeleton hierarchy.
    \param data, json file description of skeleton hierarchy. ref to boneMap.json.
     */
    internal fun humanProcessorSetBoneMap(data: ByteArray) {
        faceunity.fuHumanProcessorSetBonemap(data)
        FULogger.d(TAG, "fuHumanProcessorSetBonemap      data:${data.contentToString()}")
    }

    /**
    \brief get ai model HumanProcessor's 3d joint transform, rotation only.
    \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultTransformArray(index: Int, data: FloatArray) {
        faceunity.fuHumanProcessorGetResultTransformArray(index, data)
        FULogger.d(TAG, "fuHumanProcessorGetResultTransformArray    index:$index  data:${data.contentToString()}")
    }


    /**
    \brief get ai model HumanProcessor's 3d root joint's transform.
    \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultModelMatrix(index: Int, matrix: FloatArray) {
        faceunity.fuHumanProcessorGetResultModelMatrix(index, matrix)
        FULogger.d(TAG, "fuHumanProcessorGetResultModelMatrix    index:$index  matrix:${matrix.contentToString()}")
    }

    /**
    \brief get ai model HumanProcessor's tracking full body mask with index.
    \param index, index of fuHumanProcessorGetNumResults.
    \param mask_width,  width of return.
    \param mask_height,  height of return.
    \return mask data.
     */
    internal fun humanProcessorGetResultHumanMask(index: Int, mask: FloatArray): Int {
        val res = faceunity.fuHumanProcessorGetResultHumanMask(index, mask)
        FULogger.d(TAG, "fuHumanProcessorGetResultHumanMask   res:$res   index:$index  mask:${mask.contentToString()}")
        return res;
    }

    /**
    \brief get ai model HumanProcessor's tracking hair mask with index.
    \param index, index of fuHumanProcessorGetNumResults.
    \param mask_width,  width of return.
    \param mask_height,  height of return.
    \return mask data.
     */
    internal fun faceProcessorGetResultHairMask(index: Int, mask: FloatArray): Int {
        val res = faceunity.fuFaceProcessorGetResultHairMask(index, mask)
        FULogger.d(TAG, "fuFaceProcessorGetResultHairMask   res:$res   index:$index  mask:${mask.contentToString()}")
        return res
    }

    /**
    \brief get ai model HumanProcessor's tracking head mask with index.
    \param index, index of fuHumanProcessorGetNumResults.
    \param mask_width,  width of return.
    \param mask_height,  height of return.
    \return mask data.
     */
    internal fun faceProcessorGetResultHeadMask(index: Int, mask: FloatArray): Int {
        val res = faceunity.fuFaceProcessorGetResultHeadMask(index, mask)
        FULogger.d(TAG, "fuFaceProcessorGetResultHeadMask   res:$res   index:$index  mask:${mask.contentToString()}")
        return res
    }


    /**
    \brief get ai model HumanProcessor's action type with index.
    \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultActionType(index: Int): Int {
        val res = faceunity.fuHumanProcessorGetResultActionType(index)
        FULogger.d(TAG, "fuHumanProcessorGetResultActionType   res:$res   index:$index  ")
        return res
    }

    /**
    \brief get ai model HumanProcessor's action score with index.
    \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultActionScore(index: Int): Float {
        val res = faceunity.fuHumanProcessorGetResultActionScore(index)
        FULogger.d(TAG, "fuHumanProcessorGetResultActionScore   res:$res   index:$index  ")
        return res
    }

    /**
     * 获取 HandGesture 手势算法模块跟踪手势数量。需加载 ai_hand_processor.bundle
     * @return Int 算法模块跟踪手势数量。
     */
    internal fun handDetectorGetResultNumHands(): Int {
        val res = faceunity.fuHandDetectorGetResultNumHands()
        FULogger.t(TAG, "fuHandDetectorGetResultNumHands  res:$res")
        return res
    }

    /**
    \brief get hand detector's tracking rect with index.
    \param index ,index of fuHandDetectorGetResultNumHands.
    \return rect data, float array with size 4.
     */
    internal fun handDetectorGetResultHandRect(index: Int, rect: FloatArray): Int {
        val res = faceunity.fuHandDetectorGetResultHandRect(index, rect)
        FULogger.d(TAG, "fuHandDetectorGetResultHandRect   res:$res   index:$index  rect:${rect.contentToString()}")
        return res
    }

    /**
    \brief get hand detector's tracking hand gesture type with index.
    \param index ,index of fuHandDetectorGetResultNumHands.
    \return gesture type, ref to FUAIGESTURETYPE.
     */
    internal fun handDetectorGetResultGestureType(index: Int): Int {
        val res = faceunity.fuHandDetectorGetResultGestureType(index)
        FULogger.d(TAG, "fuHandDetectorGetResultGestureType   res:$res   index:$index  ")
        return res
    }

    /**
    \brief get hand detector's tracking hand gesture type with index.
    \param index ,index of fuHandDetectorGetResultNumHands.
    \return gesture type, ref to FUAIGESTURETYPE.
     */
    internal fun handDetectorGetResultHandScore(index: Int): Float {
        val res = faceunity.fuHandDetectorGetResultHandScore(index)
        FULogger.d(TAG, "fuHandDetectorGetResultHandScore   res:$res   index:$index  ")
        return res
    }

    /**************************  通用功能 ******************************/


    /**
     * 创建OpenGL环境 适用于没OpenGL环境时调用
     */
    internal fun createEGLContext() {
        faceunity.fuCreateEGLContext()
        FULogger.d(TAG, "fuCreateEGLContext()")
    }

    /**
     * 调用过fuCreateEGLContext，在销毁时需要调用fuReleaseEGLContext
     */
    internal fun releaseEGLContext() {
        faceunity.fuReleaseEGLContext()
        FULogger.d(TAG, "fuReleaseEGLContext()")
    }

    /**
     * 设置最大人脸识别数目
     * @param maxFaces Int 最多支持 8 个
     */
    internal fun setMaxFaces(maxFaces: Int) {
        faceunity.fuSetMaxFaces(maxFaces)
        FULogger.d(TAG, "fuSetMaxFaces  maxFaces:$maxFaces")
    }

    /**
     *  设置默认的人脸朝向
     * @param rotMode Int 范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度
     */
    internal fun setDefaultRotationMode(rotMode: Int) {
        faceunity.fuSetDefaultRotationMode(rotMode)
        FULogger.d(TAG, "fuSetDefaultRotationMode  rotationMode:$rotMode   remark:${rotMode * 90}度")
    }


    /**
     * 设置输出图形分辨率
     * @param width Int width
     * @param height Int height
     */
    internal fun setOutputResolution(width: Int, height: Int) {
        faceunity.fuSetOutputResolution(width, height)
        FULogger.t(TAG, "fuSetOutputResolution  width:$width  height:$height")
    }


    /**
     * 获取当前人脸朝向
     * @return Int 范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度
     */
    fun getCurrentRotationMode(): Int {
        val rotMode = faceunity.fuGetCurrentRotationMode()
        FULogger.d(TAG, "fuGetCurrentRotationMode :$rotMode  remark:${rotMode * 90}度")
        return rotMode
    }


    /**
     * 在相机数据来源发生切换时调用（例如手机前/后置摄像头切换），用于重置人脸跟踪状态
     */
    internal fun onCameraChange() {
        faceunity.fuOnCameraChange()
        FULogger.d(TAG, "fuOnCameraChange")
    }

    /**
     * 获取人脸信息
     *
     */
    fun getFaceInfo(face_id: Int, name: String, value: FloatArray) {
        faceunity.fuGetFaceInfoRotated(face_id, name, value)
        FULogger.d(TAG, "fuGetFaceInfo   face_id:$face_id    name:$name   value:${value.contentToString()}")
    }

    /**
     * 获取人脸信息
     *
     */
    fun getFaceInfo(face_id: Int, name: String, value: IntArray) {
        faceunity.fuGetFaceInfoRotated(face_id, name, value)
        FULogger.d(TAG, "fuGetFaceInfo   face_id:$face_id    name:$name   value:${value.contentToString()}")
    }

    /**
     * 获取当前人脸跟踪状态，返回正在跟踪的人脸数量。
     * @return Int 检测到的人脸个数，返回 0 代表没有检测到人脸
     */
    internal fun isTracking(): Int {
        val res = faceunity.fuIsTracking()
        FULogger.t(TAG, "fuIsTracking  res:$res")
        return res
    }

    /**
     * 人脸信息跟踪接口
     *
     */
    fun trackFace(img: ByteArray, format: Int, w: Int, h: Int): Int {
        val res = faceunity.fuTrackFace(img, format, w, h)
        FULogger.t(TAG, "fuTrackFace  format:$format   w:$w   h:$h   res:$res")
        return res
    }

    /**
     * 获取人脸置信度
     */
    fun getFaceProcessorGetConfidenceScore(index:Int) :Float{
        val res = faceunity.fuFaceProcessorGetConfidenceScore(index)
        FULogger.t(TAG, "fuFaceProcessorGetConfidenceScore  index:$index   res:$res")
        return res
    }

    /**
     * 默认的视频模式下，不保证每帧都检测出人脸；对于图片场景，要设置图片模式
     *
     * @param mode 0 图片模式, 1 视频模式, 默认 1
     */
    fun setFaceProcessorDetectMode(mode: Int) {
        faceunity.fuSetFaceProcessorDetectMode(mode)
        FULogger.d(TAG, "fuSetFaceProcessorDetectMode   mode:$mode")
    }


    /**
     * 设置输入源数据朝向
     * @param matrix Int
     */
    internal fun setInputCameraBufferMatrix(matrix: Int) {
        faceunity.fuSetInputBufferMatrix(matrix)
        FULogger.d(TAG, "setInputCameraBufferMatrix    matrix:$matrix")
    }

    /**
     * 设置输入源纹理朝向
     * @param matrix Int
     */
    internal fun setInputCameraTextureMatrix(matrix: Int) {
        faceunity.fuSetInputTextureMatrix(matrix)
        FULogger.d(TAG, "setInputCameraTextureMatrix    matrix:$matrix")
    }

    /**
     * buffer朝向开关
     * @param enable Int
     */
    internal fun setInputCameraBufferMatrixState(enable: Boolean) {
        faceunity.fuSetInputCameraBufferMatrixState(if (enable) 1 else 0)
        FULogger.d(TAG, "setInputCameraBufferMatrixState    enable:$enable")
    }

    /**
     * 纹理朝向开关
     * @param enable Int
     */
    internal fun setInputCameraTextureMatrixState(enable: Boolean) {
        faceunity.fuSetInputCameraTextureMatrixState(if (enable) 1 else 0)
        FULogger.d(TAG, "fuSetInputCameraTextureMatrixState    enable:$enable")
    }


    /**
     * 设置输出纹理朝向
     * @param matrix Int
     */
    internal fun setOutputMatrix(matrix: Int) {
        faceunity.fuSetOutputMatrix(matrix)
        FULogger.d(TAG, "fuSetOutputMatrix    matrix:$matrix")
    }

    /**
     * 输出旋转朝向开关
     * @param enable Int
     */
    internal fun setOutputMatrixState(enable: Boolean) {
        faceunity.fuSetOutputMatrixState(if (enable) 1 else 0)
        FULogger.d(TAG, "fuSetOutputMatrixState    enable:$enable")
    }

    /**
     * 清空单纹理缓存
     */
    internal fun clearCacheResource() {
        faceunity.fuClearCacheResource()
        FULogger.d(TAG, "fuClearCacheResource ")
    }


//**************************视频处理******************************/

    internal fun getRotatedImage(): faceunity.RotatedImage {
        FULogger.d(TAG, "RotatedImage ")
        return faceunity.RotatedImage()
    }


    internal fun getFaceTransferTexID(): Int {
        val res = faceunity.fuGetFaceTransferTexID()
        FULogger.d(TAG, "fuGetFaceTransferTexID res:$res")
        return res
    }


    /**
     *
     * @param w Int  图像数据的宽
     * @param h Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items IntArray 包含多个道具句柄的int数组
     * @param tex_in Int 图像数据纹理ID
     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param img ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
     * @param imgType Int 图像数据类型
     * @param readback_w Int 数据宽
     * @param readback_h Int 数据高
     * @param readback_img ByteArray? 回写数组
     * @return Int
     */
    internal fun fuRenderDualInput(
        w: Int,
        h: Int,
        frame_id: Int,
        items: IntArray,
        tex_in: Int,
        flags: Int,
        img: ByteArray,
        imgType: Int,
        readback_w: Int = 0,
        readback_h: Int = 0,
        readback_img: ByteArray? = null
    ): Int {
        val id = faceunity.fuRenderDualInput(w, h, frame_id, items, tex_in, flags, img, imgType, readback_w, readback_h, readback_img)
//        val id = faceunity.fuDualInputToTexture( img,tex_in,flags,   w, h, frame_id, items, readback_w, readback_h, readback_img)
        FULogger.t(
            TAG,
            "fuRenderDualInput  id:$id  tex_in:$tex_in  w:$w  h:$h  flags:$flags  items:${items.contentToString()}  imgType:$imgType  frame_id:$frame_id  readback:${readback_img?.size ?: 0 > 0}"
        )
        return id
    }

    /**
     *
     * @param w Int  图像数据的宽
     * @param h Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items IntArray 包含多个道具句柄的int数组
     * @param tex_in Int 图像数据纹理ID
     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @return Int
     */
    internal fun fuRenderTexture(
        w: Int,
        h: Int,
        frame_id: Int,
        items: IntArray,
        tex_in: Int,
        flags: Int
    ): Int {
        val id = faceunity.fuRenderTexture(w, h, frame_id, items, tex_in, flags)
        //     val id = faceunity.fuRenderToTexture(tex_in, w, h, frame_id, items, flags)
        FULogger.t(TAG, "fuRenderTexture  id:$id  tex_in:$tex_in  w:$w  h:$h  flags:$flags  items:${items.contentToString()}   frame_id:$frame_id")
        return id
    }


    /**
     *
     * @param w Int  图像数据的宽
     * @param h Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items IntArray 包含多个道具句柄的int数组
     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param img ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
     * @param imgType Int 图像数据类型
     * @param readback_w Int 数据宽
     * @param readback_h Int 数据高
     * @param readback_img ByteArray? 回写数组
     * @return Int
     */
    internal fun fuRenderImg(
        w: Int,
        h: Int,
        frame_id: Int,
        items: IntArray,
        flags: Int,
        img: ByteArray,
        imgType: Int,
        readback_w: Int = 0,
        readback_h: Int = 0,
        readback_img: ByteArray? = null
    ): Int {
        val id = faceunity.fuRenderImg(w, h, frame_id, items, flags, img, imgType, readback_w, readback_h, readback_img)
        //       val id = faceunity.fuRenderToNV21Image(img, w, h, frame_id, items, flags)
        FULogger.t(TAG, "fuRenderImg  id:$id    w:$w  h:$h  flags:$flags  items:${items.contentToString()}  imgType:$imgType  frame_id:$frame_id   readback:${readback_img?.size ?: 0 > 0}")
        return id
    }

    /**
     *
     * @param y_buffer ByteArray
     * @param u_buffer ByteArray
     * @param v_buffer ByteArray
     * @param y_stride Int
     * @param u_stride Int
     * @param v_stride Int
     * @param w Int 图像数据的宽
     * @param h Int 图像数据的高
     * @param frame_id Int Int 当前处理的视频帧序数
     * @param items IntArray IntArray 包含多个道具句柄的int数组
     * @param flags Int  Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
     * @return Int 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
     */
    internal fun fuRenderYUV(
        w: Int,
        h: Int,
        frame_id: Int,
        items: IntArray,
        flags: Int,
        y_buffer: ByteArray,
        u_buffer: ByteArray,
        v_buffer: ByteArray,
        y_stride: Int,
        u_stride: Int,
        v_stride: Int,
        read_back: Boolean = false
    ): Int {
        val id = faceunity.fuRenderYUV(w, h, frame_id, items, flags, y_buffer, u_buffer, v_buffer, y_stride, u_stride, v_stride, read_back)
        FULogger.t(TAG, "fuRenderYUV  id:$id    w:$w  h:$h  flags:$flags  items:${items.contentToString()}  read_back:$read_back")
        return id
    }

    /**
     * 单独渲染美颜功能
     * @param tex_in Int 图像数据纹理ID
     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param w Int 图像数据的宽
     * @param h Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items IntArray 包含多个道具句柄的int数组
     * @return Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
     * 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
     */
    internal fun fuRenderBeautifyOnly(tex_in: Int, flags: Int, w: Int, h: Int, frame_id: Int, items: IntArray): Int {
        val id = faceunity.fuBeautifyImage(tex_in, flags, w, h, frame_id, items)
        FULogger.t(TAG, "fuBeautifyImage  id:$id  tex_in:$tex_in  w:$w  h:$h  flags:$flags  items:${items.contentToString()}")
        return id
    }


//    /**
//     *单输入接口(fuRenderToTexture)
//     * @param tex_in Int 图像数据纹理ID
//     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
//     * @param w Int 图像数据的宽
//     * @param h Int 图像数据的高
//     * @param frame_id Int 当前处理的视频帧序数
//     * @param items IntArray 包含多个道具句柄的int数组
//     * @return Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
//     * 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
//     */
//    internal fun renderToTexture(tex_in: Int, flags: Int, w: Int, h: Int, frame_id: Int, items: IntArray): Int {
//        val id = faceunity.fuRenderToTexture(tex_in, w, h, frame_id, items, flags)
//        FULogger.t(TAG, "fuRenderToTexture  id:$id  tex_in:$tex_in  w:$w  h:$h  flags:$flags items:${items.contentToString()}")
//        return id
//    }
//
//
//    /**
//     *双输入接口(fuRenderToNV21Image)
//     * @param img ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
//     * @param tex_in Int 图像数据纹理ID
//     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
//     * @param w Int 图像数据的宽
//     * @param h Int 图像数据的高
//     * @param frame_id Int 当前处理的视频帧序数
//     * @param items IntArray 包含多个道具句柄的int数组
//     * @return Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
//     * 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
//     */
//    internal fun dualInputToTexture(img: ByteArray, tex_in: Int, flags: Int, w: Int, h: Int, frame_id: Int, items: IntArray): Int {
//        val id = faceunity.fuDualInputToTexture(img, tex_in, flags, w, h, frame_id, items)
//        FULogger.t(TAG, "fuDualInputToTexture  id:$id  tex_in:$tex_in  w:$w  h:$h  flags:$flags  items:${items.contentToString()}")
//        return id
//    }
//
//
//    /**
//     *双输入接口(fuRenderToNV21Image)
//     * @param img ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
//     * @param tex_in Int 图像数据纹理ID
//     * @param flags Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
//     * @param w Int 图像数据的宽
//     * @param h Int 图像数据的高
//     * @param frame_id Int 当前处理的视频帧序数
//     * @param items IntArray 包含多个道具句柄的int数组
//     * @param back_w Int 需要回写的图像数据的宽
//     * @param back_h Int 需要回写的图像数据的高
//     * @param back_img ByteArray 需要回写的图像数据byte[]
//     * @return Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
//     * 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
//     */
//    internal fun dualInputToTexture(
//        img: ByteArray?,
//        tex_in: Int,
//        flags: Int,
//        w: Int,
//        h: Int,
//        frame_id: Int,
//        items: IntArray,
//        back_w: Int,
//        back_h: Int,
//        back_img: ByteArray
//    ): Int {
//        val id = faceunity.fuDualInputToTexture(img, tex_in, flags, w, h, frame_id, items, back_w, back_h, back_img)
//        FULogger.t(TAG, "fuDualInputToTexture  id:$id  tex_in:$tex_in  w:$w  h:$h  flags:$flags  items:${items.contentToString()}")
//        return id
//    }
//
//
//    /**
//     * 将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据，同时绘制相机原始画面。该接口会执行Controller、FXAA、CartoonFilter等道具要求、
//     * 且证书许可的功能模块，包括人脸检测与跟踪、身体追踪、Avatar绘制、FXAA抗锯齿、卡通滤镜后处理等。
//     * @param img ByteArray?
//     * @param tex_in Int
//     * @param flags Int
//     * @param w Int
//     * @param h Int
//     * @param frame_id Int
//     * @param items IntArray?
//     * @return Int
//     */
//    internal fun renderBundlesWithCamera(img: ByteArray, tex_in: Int, flags: Int, w: Int, h: Int, frame_id: Int, items: IntArray): Int {
//        val res = faceunity.fuRenderBundlesWithCamera(img, tex_in, flags, w, h, frame_id, items)
//        FULogger.t(TAG, "fuRenderBundlesWithCamera     tex_in:$tex_in  w:$w  h:$h  flags:$flags  items:${items.contentToString()}")
//        return res
//    }
//
//    /**
//     * 旋转或者翻转图像。
//     * @param outImage RotatedImage 输出图像
//     * @param inputImage ByteArray 输入图像
//     * @param imageFormat Int 输入图像的格式
//     * @param inputWidth Int 输入图像的宽
//     * @param inputHeight Int 输入图像的高
//     * @param rotateMode Int 旋转输入，0为0度，1为90度，2为180度，3为270度
//     * @param flipX Int 水平翻转输入
//     * @param flipY Int 垂直翻转输入
//     * @return Int 返回 1 代表成功，返回 0 代表失败。
//     */
//    internal fun rotateImage(
//        outImage: faceunity.RotatedImage, inputImage: ByteArray, imageFormat: Int, inputWidth: Int, inputHeight: Int,
//        rotateMode: Int, flipX: Int, flipY: Int
//    ): Int {
//        val res = faceunity.fuRotateImage(outImage, inputImage, imageFormat, inputWidth, inputHeight, rotateMode, flipX, flipY)
//        FULogger.t(TAG, "fuRotateImage   rotateMode:$rotateMode   flipX:$flipX  flipY:$flipY res:$res")
//        return res
//    }
//
//
//    /**
//     *  设置输入纹理的转正方式
//     * @param flip_x Int 水平翻转输入
//     * @param flip_y Int 垂直翻转输入
//     * @param rotate_mode Int 旋转输入，0为0度，1为90度，2为180度，3为270度。
//     */
//    internal fun setInputCameraMatrix(flip_x: Int, flip_y: Int, rotate_mode: Int) {
//        FULogger.t(TAG, "fuSetInputCameraMatrix  flip_x:$flip_x  flip_y:$flip_y  rotate_mode:$rotate_mode")
//        faceunity.fuSetInputCameraMatrix(flip_x, flip_y, rotate_mode)
//    }


    /**
     * 为特定道具创建纹理
     * @param item Int 道具 handle
     * @param name String 道具参数名
     * @param value ByteArray 图片 RGBA buffer
     * @param width Int 图片宽度
     * @param height Int 图片高度
     * @return Int 0 为失败，1 为成功。
     */
    fun createTexForItem(item: Int, name: String, value: ByteArray, width: Int, height: Int): Int {
        FULogger.t(TAG, "fuCreateTexForItem  item:$item    name:$name   width:$width   height:$height")
        return faceunity.fuCreateTexForItem(item, name, value, width, height)
    }

    /**
     *释放创建的纹理
     * @param item Int 道具 handle
     * @param name String 道具参数名
     * @return Int
     */
    fun deleteTexForItem(item: Int, name: String): Int {
        FULogger.t(TAG, "fuDeleteTexForItem   item:$item    name:$name")
        return faceunity.fuDeleteTexForItem(item, name)
    }

    /************************** 道具相关 ******************************/


    /**
     * 将资源道具从controller道具上解绑
     * @param item_src Int 目标道具的标识符，目标道具将作为controller，管理和使用资源道具，目标道具需要有OnUnbind函数。该标识符应为调用
     * 标识符应为调用 fuCreateItemFromPackage 函数的返回值，并且道具没有被销毁
     * @param items IntArray 需要解绑的资源道具列表对应的标识符数组。
     * 备注：销毁资源道具前，需要将资源道具从Controller道具上解绑。
     */
    fun unBindItems(item_src: Int, items: IntArray) {
        FULogger.d(TAG, "fuUnBindItems  item_src:$item_src   items:${items.contentToString()}")
        faceunity.fuUnBindItems(item_src, items)
    }

    /**
     * 将资源道具绑定到controller道具上
     * @param item_src Int 目标道具的标识符，目标道具将作为controller，管理和使用资源道具，目标道具需要有OnBind函数。标识符应为调用
     * 标识符应为调用 fuCreateItemFromPackage 函数的返回值，并且道具没有被销毁
     * @param items IntArray 需要绑定的资源道具列表对应的标识符数组。标识符应为调用
     */
    fun bindItems(item_src: Int, items: IntArray) {
        FULogger.d(TAG, "fuBindItems  item_src:$item_src   items:${items.contentToString()}")
        faceunity.fuBindItems(item_src, items)
    }

    /**
     * 为道具设置参数接口
     * @param item Int 道具句柄
     * @param name String 参数名
     * @param value Double  DoubleArray String 参数值
     * @return Int 执行结果：返回 0 代表设置失败，大于 0 表示设置成功
     */
    fun itemSetParam(item: Int, name: String, value: Double): Int {
        FULogger.d(TAG, "fuItemSetParam   item: $item    name:$name   value:$value")
        return faceunity.fuItemSetParam(item, name, value)
    }

    fun itemSetParam(item: Int, name: String, value: DoubleArray): Int {
        FULogger.d(TAG, "fuItemSetParam   item: $item    name:$name   value:${value.contentToString()}")
        return faceunity.fuItemSetParam(item, name, value)
    }

    fun itemSetParam(item: Int, name: String, value: String): Int {
        FULogger.d(TAG, "fuItemSetParam   item: $item    name:$name   value:$value")
        return faceunity.fuItemSetParam(item, name, value)
    }

    fun itemGetParam(item: Int, name: String, clazz: Class<*>): Any? {
        when (clazz) {
            Double::class.java -> {
                val res = faceunity.fuItemGetParam(item, name)
                FULogger.d(TAG, "fuItemGetParam   item: $item    name:$name   res:$res")
                return res
            }
            DoubleArray::class.java -> {
                val res = faceunity.fuItemGetParamdv(item, name)
                FULogger.d(TAG, "fuItemGetParamdv   item: $item    name:$name   res:${res?.contentToString()}")
                return res
            }
            String::class.java -> {
                val res = faceunity.fuItemGetParamString(item, name)
                FULogger.d(TAG, "fuItemGetParamString   item: $item    name:$name   res:$res")
                return res
            }
            FloatArray::class.java -> {
                val res = faceunity.fuItemGetParamfv(item, name)
                FULogger.d(TAG, "fuItemGetParamfv   item: $item    name:$name   res:${res?.contentToString()}")
                return res
            }
        }
        return null
    }


    /**
     * 通过道具二进制文件创建道具接口
     * @param buffer ByteArray 道具二进制文件
     * @return Int 创建的道具句柄
     */
    internal fun createItemFromPackage(buffer: ByteArray, path: String): Int {
        setInputCameraBufferMatrixState(true)
        val handle = faceunity.fuCreateItemFromPackage(buffer)
        FULogger.d(TAG, "fuCreateItemFromPackage   handle:$handle   path:$path")
        if (handle > 0) {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_LOAD_BUNDLE, "createItemFromPackage")
        } else {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_LOAD_BUNDLE, "createItemFromPackage  failed")
        }
        return handle
    }


    /**
    \brief set if use AI async
    \param use,set 1 for use or 0 for not use.
     */
    internal fun setUseAsyncAIInference(user: Int): Int {
        val res = faceunity.fuSetUseAsyncAIInference(user)
        FULogger.d(TAG, "fuSetUseMultiBuffer   user:$user")
        return res
    }

    /**
    \brief set if use multi buffer
    \param use_multi_gpu_textuer,set 1 for use or 0 for not use.
    \param use_multi_cpu_buffer,set 1 for use or 0 for not use.
     */
    internal fun setUseMultiBuffer(use_multi_gpu_texture: Int, use_multi_cpu_buffer: Int): Int {
        val res = faceunity.fuSetUseMultiBuffer(use_multi_gpu_texture, use_multi_cpu_buffer)
        FULogger.d(TAG, "fuSetUseMultiBuffer   use_multi_gpu_texture:$use_multi_gpu_texture  use_multi_cpu_buffer:$use_multi_cpu_buffer")
        return res
    }

    /**
    \brief set if use the output texture for async reading, when use spcified
    framebuffer for output. \param use,set 1 for use or 0 for not use, not use by
    default for performance.
     */
    internal fun setUseTexAsync(user: Int): Int {
        val res = faceunity.fuSetUseTexAsync(user)
        FULogger.d(TAG, "fuSetUseTexAsync   user:$user")
        return res
    }


//************************** PTA ******************************/

    /**
     * 创建场景
     * @return Int
     */
    internal fun createScene(): Int {
        val res = faceunity.fuCreateScene()
        FULogger.d(TAG, "fuCreateScene   res:$res")
        return res
    }

    /**
     * 销毁场景
     * @param sceneId Int
     * @return Int
     */
    internal fun destroyScene(sceneId: Int): Int {
        val res = faceunity.fuDestroyScene(sceneId)
        FULogger.d(TAG, "fuDestroyScene   sceneId:$sceneId   res:$res")
        return res
    }

    /**
     * 生效当前场景
     * @param sceneId Int
     * @return Int
     */
    internal fun setCurrentScene(sceneId: Int): Int {
        val res = faceunity.fuSetCurrentScene(sceneId)
        FULogger.d(TAG, "fuSetCurrentScene   sceneId:$sceneId   res:$res")
        return res
    }

    /**
     * 场景绑定道具
     * @param sceneId Int
     * @param items IntArray
     * @return Int
     */
    internal fun bindItemsToScene(sceneId: Int, items: IntArray): Int {
        val res = faceunity.fuBindItemsToScene(sceneId, items)
        FULogger.d(TAG, "fuBindItemsToScene   sceneId:$sceneId   items:${items.contentToString()}  res:$res")
        return res
    }

    /**
     * 场景移除道具
     * @param sceneId Int
     * @param items IntArray
     * @return Int
     */
    internal fun unbindItemsFromScene(sceneId: Int, items: IntArray): Int {
        val res = faceunity.fuUnbindItemsFromScene(sceneId, items)
        FULogger.d(TAG, "fuUnbindItemsFromScene   sceneId:$sceneId   items:${items.contentToString()}  res:$res")
        return res
    }


    /**
     * 是否启用人体驱动
     * @param sceneId Int
     * @param items IntArray
     * @return Int
     */
    internal fun enableHumanProcessor(sceneId: Int, enable: Boolean): Int {
        val res = faceunity.fuEnableHumanProcessor(sceneId, enable)
        FULogger.d(TAG, "fuEnableHumanProcessor   sceneId:$sceneId   enable:$enable  res:$res")
        return res
    }

    /**
     * 是否启用人体驱动
     * @param sceneId Int
     * @param items IntArray
     * @return Int
     */
    internal fun humanProcessorSet3DScene(sceneId: Int, isFull: Boolean): Int {
        val res = faceunity.fuHumanProcessorSet3DScene(sceneId, if (isFull) 1 else 0)
        FULogger.d(TAG, "fuHumanProcessorSet3DScene   sceneId:$sceneId   isFull:$isFull  res:$res")
        return res
    }

    /**
     * 设置跟随模式
     */
    internal fun enableHumanFollowMode(sceneId: Int, mode: Int) {
        faceunity.fuEnableHumanFollowMode(sceneId, mode)
        FULogger.d(TAG, "enableHumanFollowMode   sceneId:$sceneId   mode:$mode")
    }

    /**
     * avatar活动范围
     */
    internal fun setHumanProcessorTranslationScale(sceneId: Int, x: Float ,y: Float ,z: Float) {
        faceunity.fuSetHumanProcessorTranslationScale(sceneId, x,y,z)
        FULogger.d(TAG, "setHumanProcessorTranslationScale   sceneId:$sceneId   x:$x  y:$y z:$z")
    }

    /**
     * avatar的大小
     */
    internal fun humanProcessorSetAvatarScale(scale: Float) {
        faceunity.fuHumanProcessorSetAvatarScale(scale)
        FULogger.d(TAG, "humanProcessorSetAvatarScale   scale:$scale")
    }

    /**
     * avatar距离人像的偏移量
     */
    internal fun humanProcessorSetAvatarGlobalOffset(offsetX: Float, offsetY: Float, offsetZ: Float) {
        faceunity.fuHumanProcessorSetAvatarGlobalOffset(offsetX, offsetY, offsetZ)
        FULogger.d(TAG, "humanProcessorSetAvatarGlobalOffset   offsetX:$offsetX   offsetY:$offsetY  offsetZ:$offsetZ")
    }

    /**
     * 设置动画过滤参数
     */
    internal fun humanProcessorSetAvatarAnimFilterParams(nBufferFrames: Int, pos: Float, angle: Float) {
        faceunity.fuHumanProcessorSetAvatarAnimFilterParams(nBufferFrames, pos, angle)
        FULogger.d(TAG, "humanProcessorSetAvatarAnimFilterParams   nBufferFrames:$nBufferFrames   pos:$pos  angle:$angle")
    }

    /**
     * 初始化Instance
     * @param sceneId Int
     * @return Int
     */
    internal fun createInstance(sceneId: Int): Int {
        val res = faceunity.fuCreateInstance(sceneId)
        FULogger.d(TAG, "fuCreateInstance   sceneId:$sceneId   res:$res")
        return res
    }

    /**
     * 销毁Instance
     * @param instanceId Int
     * @return Int
     */
    internal fun destroyInstance(instanceId: Int): Int {
        val res = faceunity.fuDestroyInstance(instanceId)
        FULogger.d(TAG, "fuDestroyInstance   instanceId:$instanceId   res:$res")
        return res
    }


    /**
     * instance绑定道具
     * @param sceneId Int
     * @param items IntArray
     * @return Int
     */
    internal fun bindItemsToInstance(instanceId: Int, items: IntArray): Int {
        val res = faceunity.fuBindItemsToInstance(instanceId, items)
        FULogger.d(TAG, "fuBindItemsToInstance   instanceId:$instanceId   items:${items.contentToString()}  res:$res")
        return res
    }

    /**
     * instance移除道具
     * @param sceneId Int
     * @param items IntArray
     * @return Int
     */
    internal fun unbindItemsFromInstance(instanceId: Int, items: IntArray): Int {
        val res = faceunity.fuUnbindItemsFromInstance(instanceId, items)
        FULogger.d(TAG, "fuUnbindItemsFromInstance   instanceId:$instanceId   items:${items.contentToString()}  res:$res")
        return res
    }

    /**
     * 设置位移
     * @param instanceId Int
     * @param x Float
     * @param y Float
     * @param z Float
     * @return Int
     */
    internal fun setInstanceTargetPosition(instanceId: Int, x: Double, y: Double, z: Double): Int {
        val res = faceunity.fuSetInstanceTargetPosition(instanceId, x.toFloat(), y.toFloat(), z.toFloat())
        FULogger.d(TAG, "fuSetInstanceTargetPosition   instanceId:$instanceId   x:$x    y:$y   z:$z  res:$res")
        return res
    }


//************************** 错误回调 ******************************/


    /**
     * 回调返回错误code以及错误信息
     */
    internal fun callBackSystemError() {
        val error = faceunity.fuGetSystemError()
        if (error != 0) {
            val errorMessage = faceunity.fuGetSystemErrorString(error)
            FULogger.e(TAG, "$errorMessage(${systemErrorMaps[error]})")
            FURenderManager.mOperateCallback?.onFail(error, "$errorMessage(${systemErrorMaps[error]})")
        }
    }


    private var systemErrorMaps: HashMap<Int, String> =
        object : HashMap<Int, String>() {
            init {
                this[1] = "随机种子生成失败"
                this[2] = "机构证书解析失败"
                this[3] = "鉴权服务器连接失败"
                this[4] = "加密连接配置失败"
                this[5] = "客户证书解析失败"
                this[6] = "客户密钥解析失败"
                this[7] = "建立加密连接失败"
                this[8] = "设置鉴权服务器地址失败"
                this[9] = "加密连接握手失败"
                this[10] = "加密连接验证失败"
                this[11] = "请求发送失败"
                this[12] = "响应接收失败"
                this[13] = "异常鉴权响应"
                this[14] = "证书权限信息不完整"
                this[15] = "鉴权功能未初始化"
                this[16] = "创建鉴权线程失败"
                this[17] = "鉴权数据被拒绝"
                this[18] = "无鉴权数据"
                this[19] = "异常鉴权数据"
                this[20] = "证书过期"
                this[21] = "无效证书"
                this[22] = "系统数据解析失败"
                this[0x100] = "加载了非正式道具包（debug版道具）"
                this[0x200] = "运行平台被证书禁止"
            }
        }


}
