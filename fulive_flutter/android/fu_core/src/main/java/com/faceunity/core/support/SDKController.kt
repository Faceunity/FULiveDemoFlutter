package com.faceunity.core.support

import com.faceunity.core.faceunity.FURenderConfig
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.FULogger.d
import com.faceunity.core.utils.FULogger.t
import com.faceunity.wrapper.faceunity
import com.faceunity.wrapper.faceunity.RotatedImage
import java.util.*

/**
 * DESC：nama.jar 调用接口
 * Created on 2021/6/16
 */
object SDKController {
    private const val TAG = "KIT_SDKController"
    //************************** 初始化部分 ******************************/
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
    internal fun getModuleCode(code: Int): Int {
        val res = faceunity.fuGetModuleCode(code)
        d(TAG, "fuGetModuleCode code \$code  res:\$res  ")
        return res
    }

    /**
     * 设置数据回写同步。
     */
    internal fun setReadbackSync(enable: Boolean) {
        d(TAG, "fuSetReadbackSync  enable:\$enable")
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
     *
     * @param auth ByteArray 鉴权数据字节数组
     * isStep返回0代表失败,非0为成功
     */
    internal fun setup(auth: ByteArray): Boolean {
        getVersion()
        t(TAG, "fuSetup    auth:" + auth.size)
        val isStep = faceunity.fuSetup(ByteArray(0), auth)
        if (isStep == 0) {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_AUTH, "setup failed")
            callBackSystemError()
        } else {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_AUTH, "setup success")
        }
        return isStep != 0
    }

    /**
     * 初始化接口，App 启动后只需要 setup 一次即可
     *
     * @param auth ByteArray 鉴权数据字节数组
     * isStep返回0代表失败,非0为成功
     */
    internal fun setupLocal(auth: ByteArray, encryptInfo: ByteArray): Boolean {
        getVersion()
        t(TAG, "setupLocal  auth:" + auth.size + "    encryptInfo:" + encryptInfo.size)
        val isStep = faceunity.fuSetupLocal(ByteArray(0), auth, encryptInfo)
        d(TAG, "setupLocal    auth:" + auth.size + "    encryptInfo:" + encryptInfo.size + isStep.let { it.toString() })
        if (isStep == null) {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_AUTH, "setupLocal failed")
            callBackSystemError()
        } else {
            FURenderManager.mOperateCallback?.onSuccess(FURenderConfig.OPERATE_SUCCESS_AUTH, "setupLocal success")
        }
        return isStep != null
    }

    /**
     * 设置Landmark算法精度
     *
     * @param type Int 0 1 2
     */
    internal fun faceProcessorSetFaceLandmarkQuality(type: Int) {
        faceunity.fuFaceProcessorSetFaceLandmarkQuality(type)
        d(TAG, "fuFaceProcessorSetFaceLandmarkQuality   type:\$type")
    }

    /**
     * 获取人脸置信度
     */
    internal fun getFaceProcessorGetConfidenceScore(index: Int): Float {
        val res = faceunity.fuFaceProcessorGetConfidenceScore(index)
        t(TAG, "fuFaceProcessorGetConfidenceScore  index:\$index   res:\$res")
        return res
    }

    /**
     * 设置日志级别
     *
     * @param level 日志级别
     */
    internal fun setLogLevel(level: Int) {
        d(TAG, "fuSetLogLevel    level:$level")
        faceunity.fuSetLogLevel(level)
    }

    /**
     * 加载so库文件
     *
     * @param dir 库文件根目录
     */
    internal fun loadLibrary(dir: String) {
        d(TAG, "loadLibrary    dir:$dir")
        faceunity.LoadConfig.loadLibrary(dir)
    }

    /**
     * 设置数据回写同步。
     */
    internal fun setReadBackSync(enable: Boolean) {
        d(TAG, "fuSetReadbackSync  enable:$enable")
        faceunity.fuSetReadbackSync(enable)
    }

    /************************** 算法模块 *****************************
     * / **
     * 是否加载 AI 模型接口
     * @param type  Bundle对应的AI能力类型
     * @return 是否已加载
     */
    internal fun isAIModelLoaded(type: Int): Boolean {
        val res = faceunity.fuIsAIModelLoaded(type)
        d(TAG, "fuIsAIModelLoaded  type:" + type + "   res:" + res + "  return:" + (res == 1))
        return res == 1
    }

    /**
     * 加载 AI 模型资源
     *
     * @param buffer AI能力模型
     * @param type   bundle对应的AI能力类型
     * @param path   对应的路径
     * @return boolean
     */
    internal fun loadAIModelFromPackage(buffer: ByteArray, type: Int, path: String): Boolean {
        val res = faceunity.fuLoadAIModelFromPackage(buffer, type)
        d(TAG, "fuLoadAIModelFromPackage  type:" + type + "   path:" + path + "    buffer.size:" + buffer.size + "    res:" + res)
        return res == 1
    }

    /**
     * 加载舌头驱动
     *
     * @param buffer AI能力模型
     * @param path   对应的路径
     * @return boolean
     */
    internal fun loadTongueModel(buffer: ByteArray, path: String): Boolean {
        val res = faceunity.fuLoadTongueModel(buffer)
        d(TAG, "fuLoadTongueModel   path:" + path + "    buffer.size:" + buffer.size + "    res:" + res)
        return res == 1
    }

    /**
     * 释放 AI 模型接口
     *
     * @param type bundle对应的AI能力类型
     * @return 是否已释放
     */
    internal fun releaseAIModel(type: Int): Boolean {
        val res = faceunity.fuReleaseAIModel(type)
        d(TAG, "fuReleaseAIModel  type:" + type + "   res:" + res + "  return:" + (res == 1))
        return false
    }

    /**
     * 销毁单个道具接口
     *
     * @param handle 道具句柄
     */
    internal fun destroyItem(handle: Int) {
        d(TAG, "fuDestroyItem   handle:$handle")
        faceunity.fuDestroyItem(handle)
    }

    /**
     * 销毁全部道具句柄所对应的资源
     */
    internal fun destroyAllItems() {
        d(TAG, "fuDestroyAllItems")
        faceunity.fuDestroyAllItems()
    }

    internal fun done() {
        d(TAG, "fuDone")
        faceunity.fuDone()
    }

    /**
     * 特殊函数，当程序退出或OpenGL context准备销毁时，调用该函数，会进行资源清理和回收，所有系统占用的内存资源会被释放，包括GL的GPU资源以及内存。
     */
    internal fun onDeviceLost() {
        d(TAG, "fuOnDeviceLost")
        faceunity.fuOnDeviceLost()
    }

    /**
     * 特殊函数，当程序退出或OpenGL context准备销毁时，调用该函数，会进行资源清理和回收，所有系统占用的内存资源会被释放，包括GL的GPU资源以及内存。
     */
    internal fun onDeviceLostSafe() {
        d(TAG, "fuOnDeviceLostSafe")
        faceunity.fuOnDeviceLostSafe()
    }

    /**
     * 设置人脸检测距离的接口
     *
     * @param ratio Float 数值范围0.0至1.0，最小人脸的大小和输入图形宽高短边的比值。默认值 0.2
     */
    internal fun faceProcessorSetMinFaceRatio(ratio: Float) {
        d(TAG, "fuFaceProcessorSetMinFaceRatio   ratio:$ratio")
        faceunity.fuFaceProcessorSetMinFaceRatio(ratio)
    }

    /**
     * \brief Set AI type for fuTrackFace and fuTrackFaceWithTongue interface
     * \param ai_type, is a bit combination of FUAITYPE;
     */
    internal fun setTrackFaceAIType(type: Int) {
        d(TAG, "setTrackFaceAIType type:$type")
        faceunity.fuSetTrackFaceAIType(type)
    }

    /**
     * \brief Set tracking fov for ai model FaceProcessor.
     */
    internal fun setFaceProcessorFov(fov: Float) {
        d(TAG, "fuSetFaceProcessorFov fov:$fov")
        faceunity.fuSetFaceProcessorFov(fov)
    }

    /**
     * 重置 HumanProcessor 人体算法模块状态
     */
    internal fun humanProcessorReset() {
        d(TAG, "fuHumanProcessorReset")
        faceunity.fuHumanProcessorReset()
    }

    /**
     * 设置HumanProcessor人体算法模块跟踪人体数
     *
     * @param maxHumans Int 默认值是 1，最大值无上限；性能随人数增加线性下降。
     */
    internal fun humanProcessorSetMaxHumans(maxHumans: Int) {
        d(TAG, "fuHumanProcessorSetMaxHumans  maxHumans:$maxHumans")
        faceunity.fuHumanProcessorSetMaxHumans(maxHumans)
    }

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体数。
     *
     * @return Int 当前跟踪到人体数
     */
    internal fun humanProcessorGetNumResults(): Int {
        val res = faceunity.fuHumanProcessorGetNumResults()
        t(TAG, "fuHumanProcessorGetNumResults  res:$res")
        return res
    }

    /**
     * 获取 HumanProcessor 人体算法模块跟踪人体数。
     *
     * @return Int 当前跟踪到人体数
     */
    internal fun humanProcessorGetResultTrackId(index: Int): Int {
        val res = faceunity.fuHumanProcessorGetResultTrackId(index)
        t(TAG, "fuHumanProcessorGetResultTrackId  index:$index    res:$res")
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking rect with index.
     * \param index, index of fuHumanProcessorGetNumResults
     * \return rect array
     */
    internal fun humanProcessorGetResultRect(index: Int, rect: FloatArray?): Int {
        val res = faceunity.fuHumanProcessorGetResultRect(index, rect)
        t(TAG, "fuHumanProcessorGetResultRect  index:" + index + "   rect:" + Arrays.toString(rect) + "   res:" + res)
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking 2d joint with index.
     * \param index, index of fuHumanProcessorGetNumResults
     * \param size,  size of return data.
     */
    internal fun humanProcessorGetResultJoint2ds(index: Int, joint2ds: FloatArray?): Int {
        val res = faceunity.fuHumanProcessorGetResultJoint2ds(index, joint2ds)
        t(TAG, "fuHumanProcessorGetResultJoint2ds  index:" + index + "   joint2ds:" + Arrays.toString(joint2ds) + "res:" + res)
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking 3d joint with index.
     * \param index, index of fuHumanProcessorGetNumResults
     * \param size,  size of return data.
     */
    internal fun humanProcessorGetResultJoint3ds(index: Int, joint3ds: FloatArray?): Int {
        val res = faceunity.fuHumanProcessorGetResultJoint2ds(index, joint3ds)
        t(TAG, "fuHumanProcessorGetResultJoint2ds  index:" + index + "   joint3ds:" + Arrays.toString(joint3ds) + "res:" + res)
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking fov, use to 3d joint projection.
     * \return fov
     */
    internal fun humanProcessorGetFov(): Float {
        val res = faceunity.fuHumanProcessorGetFov()
        t(TAG, "fuHumanProcessorGetFov      res:$res")
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking fov, use to 3d joint projection.
     * \return fov
     */
    internal fun humanProcessorSetFov(fov: Float) {
        faceunity.fuHumanProcessorSetFov(fov)
        t(TAG, "fuHumanProcessorSetFov      fov:$fov")
    }

    /**
     * \brief set ai model HumanProcessor's 3d skeleton hierarchy.
     * \param data, json file description of skeleton hierarchy. ref to boneMap.json.
     */
    internal fun humanProcessorSetBoneMap(data: ByteArray?) {
        faceunity.fuHumanProcessorSetBonemap(data)
        t(TAG, "fuHumanProcessorSetBonemap      data:" + Arrays.toString(data))
    }

    /**
     * \brief get ai model HumanProcessor's 3d joint transform, rotation only.
     * \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultTransformArray(index: Int, data: FloatArray?) {
        t(TAG, "fuHumanProcessorGetResultTransformArray    index:" + index + "  data:" + Arrays.toString(data))
        faceunity.fuHumanProcessorGetResultTransformArray(index, data)
    }

    /**
     * \brief get ai model HumanProcessor's 3d root joint's transform.
     * \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultModelMatrix(index: Int, matrix: FloatArray?) {
        t(TAG, "fuHumanProcessorGetResultModelMatrix    index:" + index + "  matrix:" + Arrays.toString(matrix))
        faceunity.fuHumanProcessorGetResultModelMatrix(index, matrix)
    }

    /**
     * \brief get ai model HumanProcessor's tracking full body mask with index.
     * \param index, index of fuHumanProcessorGetNumResults.
     * \param mask_width,  width of return.
     * \param mask_height,  height of return.
     * \return mask data.
     */
    internal fun humanProcessorGetResultHumanMask(index: Int, mask: FloatArray?): Int {
        val res = faceunity.fuHumanProcessorGetResultHumanMask(index, mask)
        t(TAG, "fuHumanProcessorGetResultHumanMask   res:" + res + "   index:" + index + "  mask:" + Arrays.toString(mask))
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking hair mask with index.
     * \param index, index of fuHumanProcessorGetNumResults.
     * \param mask_width,  width of return.
     * \param mask_height,  height of return.
     * \return mask data.
     */
    internal fun faceProcessorGetResultHairMask(index: Int, mask: FloatArray?): Int {
        val res = faceunity.fuFaceProcessorGetResultHairMask(index, mask)
        t(TAG, "fuFaceProcessorGetResultHairMask   res:" + res + "   index:" + index + "  mask:" + Arrays.toString(mask))
        return res
    }

    /**
     * \brief get ai model HumanProcessor's tracking head mask with index.
     * \param index, index of fuHumanProcessorGetNumResults.
     * \param mask_width,  width of return.
     * \param mask_height,  height of return.
     * \return mask data.
     */
    internal fun faceProcessorGetResultHeadMask(index: Int, mask: FloatArray?): Int {
        val res = faceunity.fuFaceProcessorGetResultHeadMask(index, mask)
        t(TAG, "fuFaceProcessorGetResultHeadMask   res:" + res + "   index:" + index + "mask:" + Arrays.toString(mask))
        return res
    }

    /**
     * \brief get ai model HumanProcessor's action type with index.
     * \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultActionType(index: Int): Int {
        val res = faceunity.fuHumanProcessorGetResultActionType(index)
        t(TAG, "fuHumanProcessorGetResultActionType   res:$res   index:$index")
        return res
    }

    /**
     * \brief get ai model HumanProcessor's action score with index.
     * \param index, index of fuHumanProcessorGetNumResults
     */
    internal fun humanProcessorGetResultActionScore(index: Int): Float {
        val res = faceunity.fuHumanProcessorGetResultActionScore(index)
        t(TAG, "fuHumanProcessorGetResultActionScore   res:$res")
        return res
    }

    /**
     * 获取 HandGesture 手势算法模块跟踪手势数量。需加载 ai_hand_processor.bundle
     *
     * @return Int 算法模块跟踪手势数量。
     */
    internal fun handDetectorGetResultNumHands(): Int {
        val res = faceunity.fuHandDetectorGetResultNumHands()
        t(TAG, "fuHandDetectorGetResultNumHands  res:$res")
        return res
    }

    /**
     * \brief get hand detector's tracking rect with index.
     * \param index ,index of fuHandDetectorGetResultNumHands.
     * \return rect data, float array with size 4.
     */
    internal fun handDetectorGetResultHandRect(index: Int, rect: FloatArray?): Int {
        val res = faceunity.fuHandDetectorGetResultHandRect(index, rect)
        t(TAG, "fuHandDetectorGetResultHandRect   res:" + res + "   index:" + index + "  rect:" + Arrays.toString(rect))
        return res
    }

    /**
     * \brief get hand detector's tracking hand gesture type with index.
     * \param index ,index of fuHandDetectorGetResultNumHands.
     * \return gesture type, ref to FUAIGESTURETYPE.
     */
    internal fun handDetectorGetResultGestureType(index: Int): Int {
        val res = faceunity.fuHandDetectorGetResultGestureType(index)
        t(TAG, "fuHandDetectorGetResultGestureType   res:$res   index:$index")
        return res
    }

    /**
     * \brief get hand detector's tracking hand gesture type with index.
     * \param index ,index of fuHandDetectorGetResultNumHands.
     * \return gesture type, ref to FUAIGESTURETYPE.
     */
    internal fun handDetectorGetResultHandScore(index: Int): Float {
        val res = faceunity.fuHandDetectorGetResultHandScore(index)
        t(TAG, "fuHandDetectorGetResultHandScore   res:$res   index:$index")
        return res
    }
    //*************************  通用功能 ******************************/
    /**
     * 创建OpenGL环境 适用于没OpenGL环境时调用
     */
    internal fun createEGLContext() {
        d(TAG, "fuCreateEGLContext()")
        faceunity.fuCreateEGLContext()
    }

    /**
     * 调用过fuCreateEGLContext，在销毁时需要调用fuReleaseEGLContext
     */
    internal fun releaseEGLContext() {
        d(TAG, "fuReleaseEGLContext()")
        faceunity.fuReleaseEGLContext()
    }

    /**
     * 设置最大人脸识别数目
     *
     * @param maxFaces Int 最多支持 8 个
     */
    internal fun setMaxFaces(maxFaces: Int) {
        d(TAG, "fuSetMaxFaces  maxFaces:$maxFaces")
        faceunity.fuSetMaxFaces(maxFaces)
    }

    /**
     * 设置默认的人脸朝向
     *
     * @param rotMode Int 范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度
     */
    internal fun setDefaultRotationMode(rotMode: Int) {
        t(TAG, "fuSetDefaultRotationMode  rotationMode:" + rotMode + "   remark:" + rotMode * 90 + "度")
        faceunity.fuSetDefaultRotationMode(rotMode)
    }

    /**
     * 设置输出图形分辨率
     *
     * @param width  Int width
     * @param height Int height
     */
    internal fun setOutputResolution(width: Int, height: Int) {
        d(TAG, "fuSetOutputResolution  width:$width  height:$height")
        faceunity.fuSetOutputResolution(width, height)
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
        d(TAG, "fuOnCameraChange")
        faceunity.fuOnCameraChange()
    }

    /**
     * 获取人脸信息
     */
    internal fun getFaceInfo(faceId: Int, name: String, value: FloatArray?) {
        t(TAG, "fuGetFaceInfo   face_id:$faceId    name:$name")
        faceunity.fuGetFaceInfo(faceId, name, value)
    }

    /**
     * 获取人脸信息
     */
    internal fun getFaceInfo(faceId: Int, name: String, value: IntArray?) {
        t(TAG, "fuGetFaceInfo   face_id:$faceId    name:$name")
        faceunity.fuGetFaceInfoRotated(faceId, name, value)
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
     */
    internal fun trackFace(img: ByteArray?, format: Int, w: Int, h: Int): Int {
        val res = faceunity.fuTrackFace(img, format, w, h)
        t(TAG, "fuTrackFace  format:$format   w:$w   h:$h   res:$res")
        return res
    }

    /**
     * 默认的视频模式下，不保证每帧都检测出人脸；对于图片场景，要设置图片模式
     *
     * @param mode 0 图片模式, 1 视频模式, 默认 1
     */
    internal fun setFaceProcessorDetectMode(mode: Int) {
        d(TAG, "fuSetFaceProcessorDetectMode   mode:$mode")
        faceunity.fuSetFaceProcessorDetectMode(mode)
    }

    /**
     * 设置输入源数据朝向
     *
     * @param matrix Int
     */
    internal fun setInputCameraBufferMatrix(matrix: Int) {
        d(TAG, "setInputCameraBufferMatrix    matrix:$matrix")
        faceunity.fuSetInputBufferMatrix(matrix)
    }

    /**
     * 设置输入源纹理朝向
     *
     * @param matrix Int
     */
    internal fun setInputCameraTextureMatrix(matrix: Int) {
        d(TAG, "setInputCameraTextureMatrix    matrix:$matrix")
        faceunity.fuSetInputTextureMatrix(matrix)
    }

    /**
     * 纹理朝向开关
     *
     * @param enable Int
     */
    internal fun setInputCameraBufferMatrixState(enable: Boolean) {
        d(TAG, "setInputCameraBufferMatrixState    enable:$enable")
        faceunity.fuSetInputCameraBufferMatrixState(if (enable) 1 else 0)
    }

    /**
     * 设置输出纹理朝向
     *
     * @param matrix Int
     */
    internal fun setOutputMatrix(matrix: Int) {
        d(TAG, "fuSetOutputMatrix    matrix:$matrix")
        faceunity.fuSetOutputMatrix(matrix)
    }

    /**
     * 清空单纹理缓存
     */
    internal fun clearCacheResource() {
        d(TAG, "fuClearCacheResource ")
        faceunity.fuClearCacheResource()
    }

    /**
     * 抗锯齿
     *
     * @param samples Int
     */
    internal fun setMultiSamples(samples: Int): Int {
        t(TAG, "fuSetMultiSamples   samples:$samples")
        val res = faceunity.fuSetMultiSamples(samples)
        d(TAG, "fuSetMultiSamples   samples:$samples    res:$res")
        return res
    }

    //**************************视频处理******************************/
    val rotatedImage: RotatedImage
        get() {
            d(TAG, "new faceunity.RotatedImage")
            return RotatedImage()
        }

    val faceTransferTexID: Int
        get() {
            t(TAG, "fuGetFaceTransferTexID")
            val res = faceunity.fuGetFaceTransferTexID()
            d(TAG, "fuGetFaceTransferTexID res:$res")
            return res
        }
    /**
     * @param w            Int  图像数据的宽
     * @param h            Int 图像数据的高
     * @param frame_id     Int 当前处理的视频帧序数
     * @param items        IntArray 包含多个道具句柄的int数组
     * @param tex_in       Int 图像数据纹理ID
     * @param flags        Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param img          ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
     * @param imgType      Int 图像数据类型
     * @param readback_w   Int 数据宽
     * @param readback_h   Int 数据高
     * @param readback_img ByteArray? 回写数组
     * @return Int
     */
    /**
     * @param w        Int  图像数据的宽
     * @param h        Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items    IntArray 包含多个道具句柄的int数组
     * @param tex_in   Int 图像数据纹理ID
     * @param flags    Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param img      ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
     * @param imgType  Int 图像数据类型
     * @return Int
     */
    @JvmOverloads
    internal fun fuRenderDualInput(
        w: Int, h: Int, frame_id: Int, items: IntArray?, tex_in: Int, flags: Int, img: ByteArray, imgType: Int,
        readback_w: Int = 0, readback_h: Int = 0, readback_img: ByteArray? = null
    ): Int {
        t(
            TAG, "fuRenderDualInput  tex_in:" + tex_in + "  img:" + img.size + "  w:" + w + "  h:" + h + "  flags:" + flags + "  items:" + Arrays.toString(items) +
                    "  imgType:" + imgType + "frame_id:" + frame_id +
                    "  readback_w:" + readback_w + "  readback_h:" + readback_h + "  readback_img:" + (readback_img?.size ?: 0)
        )
        val res = faceunity.fuRenderDualInput(w, h, frame_id, items, tex_in, flags, img, imgType, readback_w, readback_h, readback_img)
        //val id = faceunity.fuDualInputToTexture( img,tex_in,flags,   w, h, frame_id, items, readback_w, readback_h, readback_img)
        t(TAG, "fuRenderDualInput  res:$res")
        return res
    }

    /**
     * @param w        Int  图像数据的宽
     * @param h        Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items    IntArray 包含多个道具句柄的int数组
     * @param tex_in   Int 图像数据纹理ID
     * @param flags    Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @return Int
     */
    internal fun fuRenderTexture(w: Int, h: Int, frame_id: Int, items: IntArray?, tex_in: Int, flags: Int): Int {
        t(TAG, "fuRenderTexture   tex_in:" + tex_in + "  w:" + w + "  h:" + h + "  flags:" + flags + "  items:" + Arrays.toString(items) + "frame_id:" + frame_id)
        val res = faceunity.fuRenderTexture(w, h, frame_id, items, tex_in, flags)
        //     val id = faceunity.fuRenderToTexture(tex_in, w, h, frame_id, items, flags)
        t(TAG, "fuRenderTexture  res:$res")
        return res
    }
    /**
     * @param w            Int  图像数据的宽
     * @param h            Int 图像数据的高
     * @param frame_id     Int 当前处理的视频帧序数
     * @param items        IntArray 包含多个道具句柄的int数组
     * @param flags        Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param img          ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
     * @param imgType      Int 图像数据类型
     * @param readback_w   Int 数据宽
     * @param readback_h   Int 数据高
     * @param readback_img ByteArray? 回写数组
     * @return Int
     */
    /**
     * @param w        Int  图像数据的宽
     * @param h        Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items    IntArray 包含多个道具句柄的int数组
     * @param flags    Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param img      ByteArray 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA
     * @param imgType  Int 图像数据类型
     * @return Int
     */
    @JvmOverloads
    internal fun fuRenderImg(
        w: Int, h: Int, frame_id: Int, items: IntArray?, flags: Int, img: ByteArray, imgType: Int,
        readback_w: Int = 0, readback_h: Int = 0, readback_img: ByteArray? = null
    ): Int {
        t(
            TAG, "fuRenderImg   img:" + img.size + "   w:" + w + "  h:" + h + "  flags:" + flags + "  items:" + Arrays.toString(items) + "  imgType:" + imgType +
                    "frame_id:" + frame_id + "    readback_w:" + readback_w + "      readback_h:" + readback_h + "  readback_img:" + (readback_img?.size ?: 0)
        )
        val res = faceunity.fuRenderImg(w, h, frame_id, items, flags, img, imgType, readback_w, readback_h, readback_img)
        //       val id = faceunity.fuRenderToNV21Image(img, w, h, frame_id, items, flags)
        t(TAG, "fuRenderImg  res:$res")
        return res
    }
    /**
     * @param w         Int 图像数据的宽
     * @param h         Int 图像数据的高
     * @param frame_id  Int Int 当前处理的视频帧序数
     * @param items     IntArray IntArray 包含多个道具句柄的int数组
     * @param flags     Int  Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
     * @param y_buffer  ByteArray
     * @param u_buffer  ByteArray
     * @param v_buffer  ByteArray
     * @param y_stride  Int
     * @param u_stride  Int
     * @param v_stride  Int
     * @param read_back boolean 是否需要回写
     * @return Int 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
     */
    /**
     * @param w        Int 图像数据的宽
     * @param h        Int 图像数据的高
     * @param frame_id Int Int 当前处理的视频帧序数
     * @param items    IntArray IntArray 包含多个道具句柄的int数组
     * @param flags    Int  Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
     * @param y_buffer ByteArray
     * @param u_buffer ByteArray
     * @param v_buffer ByteArray
     * @param y_stride Int
     * @param u_stride Int
     * @param v_stride Int
     * @return Int 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
     */
    @JvmOverloads
    internal fun fuRenderYUV(
        w: Int, h: Int, frame_id: Int, items: IntArray?, flags: Int,
        y_buffer: ByteArray, u_buffer: ByteArray, v_buffer: ByteArray, y_stride: Int, u_stride: Int, v_stride: Int, read_back: Boolean = false
    ): Int {
        t(
            TAG, "fuRenderYUV   y_buffer:" + y_buffer.size + "    u_buffer:" + u_buffer.size + "   v_buffer:" + v_buffer.size + "   w:" + w + "  h:" + h +
                    " flags:" + flags + "  items:" + Arrays.toString(items) + "  y_stride:" + y_stride + "    u_stride:" + u_stride + "     v_stride:" + v_stride + "   read_back:" + read_back
        )
        val res = faceunity.fuRenderYUV(w, h, frame_id, items, flags, y_buffer, u_buffer, v_buffer, y_stride, u_stride, v_stride, read_back)
        t(TAG, "fuRenderYUV  res:$res")
        return res
    }

    /**
     * 单独渲染美颜功能
     *
     * @param w        Int 图像数据的宽
     * @param h        Int 图像数据的高
     * @param frame_id Int 当前处理的视频帧序数
     * @param items    IntArray 包含多个道具句柄的int数组
     * @param flags    Int 可以指定数据img数据格式，返回纹理ID的道具镜像等，详见后文”Android 双输入“部分说明
     * @param tex_in   Int 图像数据纹理ID
     * @return Int 被处理过的的图像数据纹理ID。返回值小于等于0为异常， 纹理返回的数据为 2D
     * 备注：该绘制接口需要OpenGL环境，环境异常会导致崩溃。
     */
    internal fun fuRenderBeautifyOnly(w: Int, h: Int, frame_id: Int, items: IntArray?, flags: Int, tex_in: Int): Int {
        t(TAG, "fuRenderBeautifyOnly   tex_in:" + tex_in + "   w:" + w + "  h:" + h + "  flags:" + flags + "  items:" + Arrays.toString(items) + "  frame_id:" + frame_id)
        val res = faceunity.fuBeautifyImage(tex_in, flags, w, h, frame_id, items)
        t(TAG, "fuRenderBeautifyOnly  res:$res")
        return res
    }

    /**
     * 为特定道具创建纹理
     *
     * @param item   Int 道具 handle
     * @param name   String 道具参数名
     * @param value  ByteArray 图片 RGBA buffer
     * @param width  Int 图片宽度
     * @param height Int 图片高度
     * @return Int 0 为失败，1 为成功。
     */
    internal fun createTexForItem(item: Int, name: String, value: ByteArray?, width: Int, height: Int): Int {
        val res = faceunity.fuCreateTexForItem(item, name, value, width, height)
        t(TAG, "fuCreateTexForItem  item:$item    name:$name   width:$width   height:$height  res:$res")
        return res
    }

    /**
     * 释放创建的纹理
     *
     * @param item Int 道具 handle
     * @param name String 道具参数名
     * @return Int
     */
    internal fun deleteTexForItem(item: Int, name: String): Int {
        val res = faceunity.fuDeleteTexForItem(item, name)
        t(TAG, "fuDeleteTexForItem   item:$item    name:$name    res:$res")
        return res
    }
    //************************** 道具相关 ******************************/
    /**
     * 将资源道具从controller道具上解绑
     *
     * @param item_src Int 目标道具的标识符，目标道具将作为controller，管理和使用资源道具，目标道具需要有OnUnbind函数。该标识符应为调用
     * 标识符应为调用 fuCreateItemFromPackage 函数的返回值，并且道具没有被销毁
     * @param items    IntArray 需要解绑的资源道具列表对应的标识符数组。
     * 备注：销毁资源道具前，需要将资源道具从Controller道具上解绑。
     */
    internal fun unBindItems(item_src: Int, items: IntArray?): Int {
        t(TAG, "fuUnBindItems  item_src:" + item_src + "   items:" + Arrays.toString(items))
        val res = faceunity.fuUnBindItems(item_src, items)
        d(TAG, "fuUnBindItems  item_src:" + item_src + "   items:" + Arrays.toString(items) + "    res:" + res)
        return res
    }

    /**
     * 将资源道具绑定到controller道具上
     *
     * @param item_src Int 目标道具的标识符，目标道具将作为controller，管理和使用资源道具，目标道具需要有OnBind函数。标识符应为调用
     * 标识符应为调用 fuCreateItemFromPackage 函数的返回值，并且道具没有被销毁
     * @param items    IntArray 需要绑定的资源道具列表对应的标识符数组。标识符应为调用
     */
    internal fun bindItems(item_src: Int, items: IntArray?): Int {
        t(TAG, "fuBindItems   item_src:" + item_src + "   items:" + Arrays.toString(items))
        val res = faceunity.fuBindItems(item_src, items)
        d(TAG, "fuBindItems   item_src:" + item_src + "   items:" + Arrays.toString(items) + "    res:" + res)
        return res
    }

    /**
     * 为道具设置参数接口
     *
     * @param item  Int 道具句柄
     * @param name  String 参数名
     * @param value Double  参数值
     * @return Int 执行结果：返回 0 代表设置失败，大于 0 表示设置成功
     */
    internal fun itemSetParam(item: Int, name: String, value: Double): Int {
        t(TAG, "fuItemSetParam   item: $item    name:$name   value:$value")
        val res = faceunity.fuItemSetParam(item, name, value)
        d(TAG, "fuItemSetParam   item: $item    name:$name   value:$value    res:$res")
        return res
    }

    /**
     * 为道具设置参数接口
     *
     * @param item  Int 道具句柄
     * @param name  String 参数名
     * @param value DoubleArray 参数值
     * @return Int 执行结果：返回 0 代表设置失败，大于 0 表示设置成功
     */
    internal fun itemSetParam(item: Int, name: String, value: DoubleArray?): Int {
        t(TAG, "fuItemSetParam   item: " + item + "    name:" + name + "   value:" + Arrays.toString(value))
        val res = faceunity.fuItemSetParam(item, name, value)
        d(TAG, "fuItemSetParam   item: " + item + "    name:" + name + "   value:" + Arrays.toString(value) + "    res:" + res)
        return res
    }

    /**
     * 为道具设置参数接口
     *
     * @param item  Int 道具句柄
     * @param name  String 参数名
     * @param value String 参数值
     * @return Int 执行结果：返回 0 代表设置失败，大于 0 表示设置成功
     */
    internal fun itemSetParam(item: Int, name: String, value: String): Int {
        t(TAG, "fuItemSetParam   item:$item    name:$name   value:$value")
        val res = faceunity.fuItemSetParam(item, name, value)
        d(TAG, "fuItemSetParam   item:$item    name:$name   value:$value    res:$res")
        return res
    }

    /**
     * 获取返回值
     *
     * @param item  Int  道具句柄
     * @param name  String  参数名
     * @param clazz Class<*>  数据类型
     * @return Object
     */
    internal fun itemGetParam(item: Int, name: String, clazz: Class<*>): Any? {
        t(TAG, "fuItemGetParam   item:$item    name:$name")
        if (clazz == Double::class.java) {
            val res = faceunity.fuItemGetParam(item, name)
            d(TAG, "fuItemGetParam   item:$item    name:$name   res:$res")
            return res
        } else if (clazz == DoubleArray::class.java) {
            val res = faceunity.fuItemGetParamdv(item, name)
            d(TAG, "fuItemGetParam   item:" + item + "    name:" + name + "   res:" + Arrays.toString(res))
            return res
        } else if (clazz == String::class.java) {
            val res = faceunity.fuItemGetParamString(item, name)
            d(TAG, "fuItemGetParam   item:$item    name:$name   res:$res")
            return res
        } else if (clazz == FloatArray::class.java) {
            val res = faceunity.fuItemGetParamfv(item, name)
            d(TAG, "fuItemGetParam   item:" + item + "    name:" + name + "   res:" + Arrays.toString(res))
            return res
        }
        return null
    }

    /**
     * 通过道具二进制文件创建道具接口
     *
     * @param buffer ByteArray 道具二进制文件
     * @return Int 创建的道具句柄
     */
    internal fun createItemFromPackage(buffer: ByteArray?, path: String): Int {
        faceunity.fuSetInputCameraBufferMatrixState(1)
        t(TAG, "fuSetInputCameraBufferMatrixState   enable:1")
        t(TAG, "fuCreateItemFromPackage   path:$path")
        val handle = faceunity.fuCreateItemFromPackage(buffer)
        d(TAG, "fuCreateItemFromPackage   path:$path    handle:$handle")
        return handle
    }

    /**
     * \brief set if use AI async
     * \param use,set 1 for use or 0 for not use.
     */
    internal fun setUseAsyncAIInference(user: Int): Int {
        t(TAG, "fuSetUseMultiBuffer   user:$user")
        val res = faceunity.fuSetUseAsyncAIInference(user)
        d(TAG, "fuSetUseMultiBuffer   user:$user    res:$res")
        return res
    }

    /**
     * \brief set if use multi buffer
     * \param use_multi_gpu_textuer,set 1 for use or 0 for not use.
     * \param use_multi_cpu_buffer,set 1 for use or 0 for not use.
     */
    internal fun setUseMultiBuffer(use_multi_gpu_texture: Int, use_multi_cpu_buffer: Int): Int {
        t(TAG, "fuSetUseMultiBuffer   use_multi_gpu_texture:$use_multi_gpu_texture  use_multi_cpu_buffer:$use_multi_cpu_buffer")
        val res = faceunity.fuSetUseMultiBuffer(use_multi_gpu_texture, use_multi_cpu_buffer)
        d(TAG, "fuSetUseMultiBuffer   use_multi_gpu_texture:$use_multi_gpu_texture  use_multi_cpu_buffer:$use_multi_cpu_buffer    res:$res")
        return res
    }

    /**
     * \brief set if use the output texture for async reading, when use spcified
     * framebuffer for output. \param use,set 1 for use or 0 for not use, not use by
     * default for performance.
     */
    internal fun setUseTexAsync(user: Int): Int {
        t(TAG, "fuSetUseTexAsync   user:$user")
        val res = faceunity.fuSetUseTexAsync(user)
        d(TAG, "fuSetUseTexAsync   user:$user    res:$res")
        return res
    }
    //************************** PTA ******************************/
    /**
     * 开启离线功能
     */
    internal fun prepareGLResource(items: IntArray?) {
        d(TAG, "fuPrepareGLResource  items:" + Arrays.toString(items))
        faceunity.fuPrepareGLResource(items)
    }

    /**
     * 创建场景
     *
     * @return Int
     */
    internal fun createScene(): Int {
        t(TAG, "fuCreateScene")
        val res = faceunity.fuCreateScene()
        d(TAG, "fuCreateScene   res:$res")
        return res
    }

    /**
     * 销毁场景
     *
     * @param sceneId Int
     * @return Int
     */
    internal fun destroyScene(sceneId: Int): Int {
        t(TAG, "fuDestroyScene   sceneId:$sceneId")
        val res = faceunity.fuDestroyScene(sceneId)
        d(TAG, "fuDestroyScene   sceneId:$sceneId   res:$res")
        return res
    }

    /**
     * 生效当前场景
     *
     * @param sceneId Int
     * @return Int
     */
    internal fun setCurrentScene(sceneId: Int): Int {
        t(TAG, "fuSetCurrentScene   sceneId:$sceneId")
        val res = faceunity.fuSetCurrentScene(sceneId)
        d(TAG, "fuSetCurrentScene   sceneId:$sceneId   res:$res")
        return res
    }

    /**
     * 场景绑定道具
     *
     * @param sceneId Int
     * @param items   IntArray
     * @return Int
     */
    internal fun bindItemsToScene(sceneId: Int, items: IntArray?): Int {
        t(TAG, "fuBindItemsToScene   sceneId:" + sceneId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuBindItemsToScene(sceneId, items)
        d(TAG, "fuBindItemsToScene   sceneId:" + sceneId + "   items:" + Arrays.toString(items) + "  res:" + res)
        return res
    }

    /**
     * 场景移除道具
     *
     * @param sceneId Int
     * @param items   IntArray
     * @return Int
     */
    internal fun unbindItemsFromScene(sceneId: Int, items: IntArray?): Int {
        t(TAG, "fuUnbindItemsFromScene   sceneId:" + sceneId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuUnbindItemsFromScene(sceneId, items)
        d(TAG, "fuUnbindItemsFromScene   sceneId:" + sceneId + "   items:" + Arrays.toString(items) + "  res:" + res)
        return res
    }

    /**
     * 是否启用人体驱动
     *
     * @param sceneId Int
     * @param enable  Boolean
     * @return Int
     */
    internal fun enableHumanProcessor(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableHumanProcessor   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableHumanProcessor(sceneId, enable)
        d(TAG, "fuEnableHumanProcessor   sceneId:$sceneId   enable:$enable  res:$res")
        return res
    }

    /**
     * 是否启用人体驱动
     *
     * @param sceneId Int
     * @param isFull  Boolean
     * @return Int
     */
    internal fun humanProcessorSet3DScene(sceneId: Int, isFull: Boolean): Int {
        t(TAG, "fuHumanProcessorSet3DScene   sceneId:$sceneId   isFull:$isFull")
        val res = faceunity.fuHumanProcessorSet3DScene(sceneId, if (isFull) 1 else 0)
        d(TAG, "fuHumanProcessorSet3DScene   sceneId:" + sceneId + "   isFull:" + (if (isFull) 1 else 0) + "  res:" + res)
        return res
    }

    /**
     * 初始化Instance
     *
     * @param sceneId Int
     * @return Int
     */
    internal fun createInstance(sceneId: Int): Int {
        t(TAG, "fuCreateInstance   sceneId:$sceneId")
        val res = faceunity.fuCreateInstance(sceneId)
        d(TAG, "fuCreateInstance   sceneId:$sceneId   res:$res")
        return res
    }

    /**
     * 销毁Instance
     *
     * @param instanceId Int
     * @return Int
     */
    internal fun destroyInstance(instanceId: Int): Int {
        t(TAG, "fuDestroyInstance   instanceId:$instanceId")
        val res = faceunity.fuDestroyInstance(instanceId)
        d(TAG, "fuDestroyInstance   instanceId:$instanceId   res:$res")
        return res
    }

    /**
     * instance绑定道具
     *
     * @param instanceId Int
     * @param items      IntArray
     * @return Int
     */
    internal fun bindItemsToInstance(instanceId: Int, items: IntArray?): Int {
        t(TAG, "fuBindItemsToInstance   instanceId:" + instanceId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuBindItemsToInstance(instanceId, items)
        d(TAG, "fuBindItemsToInstance   instanceId:" + instanceId + "   items:" + Arrays.toString(items) + "  res:" + res)
        return res
    }

    /**
     * instance移除道具
     *
     * @param instanceId Int
     * @param items      IntArray
     * @return Int
     */
    internal fun unbindItemsFromInstance(instanceId: Int, items: IntArray?): Int {
        t(TAG, "fuUnbindItemsFromInstance   instanceId:" + instanceId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuUnbindItemsFromInstance(instanceId, items)
        d(TAG, "fuUnbindItemsFromInstance   instanceId:" + instanceId + "   items:" + Arrays.toString(items) + "  res:" + res)
        return res
    }

    /**
     * 设置位移
     *
     * @param instanceId Int
     * @param x          Float
     * @param y          Float
     * @param z          Float
     * @return Int
     */
    internal fun setInstanceTargetPosition(instanceId: Int, x: Float, y: Float, z: Float): Int {
        t(TAG, "fuSetInstanceTargetPosition   instanceId:$instanceId   x:$x    y:$y   z:$z")
        val res = faceunity.fuSetInstanceTargetPosition(instanceId, x, y, z)
        d(TAG, "fuSetInstanceTargetPosition   instanceId:$instanceId   x:$x   y:$y   z:$z  res:$res")
        return res
    }

    /**
     * SceneCamera开关
     *
     * @param sceneId Int
     * @param enable  Boolean
     * @return Int
     */
    internal fun enableRenderCamera(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableRenderCamera   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableRenderCamera(sceneId, enable)
        d(TAG, "fuEnableRenderCamera   sceneId:$sceneId   enable:$enable    res:$res")
        return res
    }

    /**
     * 设置背景颜色开关
     *
     * @param sceneId Int
     * @param enable  Boolean
     * @return Int
     */
    internal fun enableBackgroundColor(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableBackgroundColor   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableBackgroundColor(sceneId, enable)
        d(TAG, "fuEnableBackgroundColor   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setBackgroundColor(sceneId: Int, r: Int, g: Int, b: Int, a: Int): Int {
        t(TAG, "fuSetBackgroundColor   sceneId:$sceneId   r:$r   g:$g  b:$b  a:$a")
        val res = faceunity.fuSetBackgroundColor(sceneId, r, g, b, a)
        d(TAG, "fuSetBackgroundColor   sceneId:$sceneId   r:$r   g:$g  b:$b  a:$a  res$res")
        return res
    }

    internal fun enableShadow(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableShadow   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableShadow(sceneId, enable)
        d(TAG, "fuEnableShadow   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setInstanceShadowPCFLevel(instanceId: Int, level: Int): Int {
        t(TAG, "fuSetInstanceShadowPCFLevel   instanceId:$instanceId   level:$level")
        val res = faceunity.fuSetInstanceShadowPCFLevel(instanceId, level)
        d(TAG, "fuSetInstanceShadowPCFLevel   instanceId:$instanceId   level:$level     res:$res")
        return res
    }

    internal fun setInstanceShadowSampleOffset(instanceId: Int, offsetScale: Int): Int {
        t(TAG, "fuSetInstanceShadowSampleOffset   instanceId:$instanceId   offset_scale:$offsetScale")
        val res = faceunity.fuSetInstanceShadowSampleOffset(instanceId, offsetScale)
        d(TAG, "fuSetInstanceShadowSampleOffset   instanceId:$instanceId   offset_scale:$offsetScale     res:$res")
        return res
    }

    internal fun enableBloom(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableBloom   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableBloom(sceneId, enable)
        d(TAG, "fuEnableBloom   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun enableLowQualityLighting(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableLowQualityLighting   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableLowQualityLighting(sceneId, enable)
        d(TAG, "fuEnableLowQualityLighting   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    /**
     * ai related api
     */
    internal fun enableFaceProcessor(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableFaceProcessor   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableFaceProcessor(sceneId, enable)
        d(TAG, "fuEnableFaceProcessor   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun enableARMode(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableARMode   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableARMode(sceneId, enable)
        d(TAG, "fuEnableARMode   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setInstanceInputCameraBufferMatrix(instanceId: Int, bMat: Int): Int {
        t(TAG, "fuSetInstanceInputCameraBufferMatrix   instanceId:$instanceId   bMat:$bMat")
        val res = faceunity.fuSetInstanceInputCameraBufferMatrix(instanceId, bMat)
        d(TAG, "fuSetInstanceInputCameraBufferMatrix   instanceId:$instanceId   bMat:$bMat     res:$res")
        return res
    }

    internal fun setInstanceFaceProcessorFaceId(instanceId: Int, faceId: Int): Int {
        t(TAG, "fuSetInstanceFaceProcessorFaceId   instanceId:$instanceId   face_id:$faceId")
        val res = faceunity.fuSetInstanceFaceProcessorFaceId(instanceId, faceId)
        d(TAG, "fuSetInstanceFaceProcessorFaceId   instanceId:$instanceId   face_id:$faceId     res:$res")
        return res
    }

    internal fun setInstanceFaceProcessorFilterSize(instanceId: Int, rotation: Int, translation: Int, eyeRotation: Int): Int {
        t(
            TAG,
            "fuSetInstanceFaceProcessorFilterSize   instanceId:$instanceId   filter_size_rotaion:$rotation  filter_size_translation:$translation  filter_size_eye_rotation:$eyeRotation"
        )
        val res = faceunity.fuSetInstanceFaceProcessorFilterSize(instanceId, rotation, translation, eyeRotation)
        d(
            TAG,
            "fuSetInstanceFaceProcessorFilterSize   instanceId:$instanceId   filter_size_rotaion:$rotation  filter_size_translation:$translation  filter_size_eye_rotation:$eyeRotation  res:$res"
        )
        return res
    }

    internal fun resetInstanceFaceProcessorFilter(instanceId: Int): Int {
        t(TAG, "fuResetInstanceFaceProcessorFilter   instanceId:$instanceId")
        val res = faceunity.fuResetInstanceFaceProcessorFilter(instanceId)
        d(TAG, "fuResetInstanceFaceProcessorFilter   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun setInstanceHeadRotationDeltaX(instanceId: Int, value: Float): Int {
        t(TAG, "fuSetInstanceHeadRotationDeltaX   instanceId:$instanceId   value:$value")
        val res = faceunity.fuSetInstanceHeadRotationDeltaX(instanceId, value)
        d(TAG, "fuSetInstanceHeadRotationDeltaX   instanceId:$instanceId   value:$value     res:$res")
        return res
    }

    internal fun setInstanceEyeRotationDeltaX(instanceId: Int, value: Float): Int {
        t(TAG, "fuSetInstanceEyeRotationDeltaX   instanceId:$instanceId   value:$value")
        val res = faceunity.fuSetInstanceEyeRotationDeltaX(instanceId, value)
        d(TAG, "fuSetInstanceEyeRotationDeltaX   instanceId:$instanceId   value:$value     res:$res")
        return res
    }

    internal fun enableInstanceFaceProcessorRotateHead(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceFaceProcessorRotateHead   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceFaceProcessorRotateHead(instanceId, enable)
        d(TAG, "fuEnableInstanceFaceProcessorRotateHead   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun enableHumanFollowMode(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableHumanFollowMode   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableHumanFollowMode(sceneId, enable)
        d(TAG, "fuEnableHumanFollowMode   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun enableHandDetetor(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableHandDetetor   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableHandDetetor(sceneId, enable)
        d(TAG, "fuEnableHandDetetor   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    /**
     * camera animation
     */
    internal fun enableCameraAnimation(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableCameraAnimation   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableCameraAnimation(sceneId, enable)
        d(TAG, "fuEnableCameraAnimation   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun playCameraAnimation(sceneId: Int, item: Int): Int {
        t(TAG, "fuPlayCameraAnimation   sceneId:$sceneId   item:$item")
        val res = faceunity.fuPlayCameraAnimation(sceneId, item)
        d(TAG, "fuPlayCameraAnimation   sceneId:$sceneId   item:$item     res:$res")
        return res
    }

    internal fun playCameraAnimationOnce(sceneId: Int, item: Int): Int {
        t(TAG, "fuPlayCameraAnimationOnce   sceneId:$sceneId   item:$item")
        val res = faceunity.fuPlayCameraAnimationOnce(sceneId, item)
        d(TAG, "fuPlayCameraAnimationOnce   sceneId:$sceneId   item:$item     res:$res")
        return res
    }

    internal fun startCameraAnimation(sceneId: Int): Int {
        t(TAG, "fuStartCameraAnimation   sceneId:$sceneId")
        val res = faceunity.fuStartCameraAnimation(sceneId)
        d(TAG, "fuStartCameraAnimation   sceneId:$sceneId     res:$res")
        return res
    }

    internal fun pauseCameraAnimation(sceneId: Int): Int {
        t(TAG, "fuPauseCameraAnimation   sceneId:$sceneId")
        val res = faceunity.fuPauseCameraAnimation(sceneId)
        d(TAG, "fuPauseCameraAnimation   sceneId:$sceneId     res:$res")
        return res
    }

    internal fun resetCameraAnimation(sceneId: Int): Int {
        t(TAG, "fuResetCameraAnimation   sceneId:$sceneId")
        val res = faceunity.fuResetCameraAnimation(sceneId)
        d(TAG, "fuResetCameraAnimation   sceneId:$sceneId     res:$res")
        return res
    }

    internal fun setCameraAnimationTransitionTime(sceneId: Int, time: Float): Int {
        t(TAG, "fuSetCameraAnimationTransitionTime   sceneId:$sceneId   time:$time")
        val res = faceunity.fuSetCameraAnimationTransitionTime(sceneId, time)
        d(TAG, "fuSetCameraAnimationTransitionTime   sceneId:$sceneId   time:$time     res:$res")
        return res
    }

    internal fun enableCameraAnimationInternalLerp(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableCameraAnimationInternalLerp   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableCameraAnimationInternalLerp(sceneId, enable)
        d(TAG, "fuEnableCameraAnimationInternalLerp   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    /**
     * time updating releated api
     */
    internal fun pauseTimeUpdate(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuPauseTimeUpdate   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuPauseTimeUpdate(sceneId, enable)
        d(TAG, "fuPauseTimeUpdate   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun enableControlTimeUpdate(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableControlTimeUpdate   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableControlTimeUpdate(sceneId, enable)
        d(TAG, "fuEnableControlTimeUpdate   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setCurrentTime(sceneId: Int, value: Float): Int {
        t(TAG, "fuSetCurrentTime   sceneId:$sceneId   value:$value")
        val res = faceunity.fuSetCurrentTime(sceneId, value)
        d(TAG, "fuSetCurrentTime   sceneId:$sceneId   value:$value     res:$res")
        return res
    }

    /**
     * instance related api
     */
    internal fun enableInstanceVisible(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceVisible   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceVisible(instanceId, enable)
        d(TAG, "fuEnableInstanceVisible   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun enableInstanceSingleMeshVisible(instanceId: Int, mesh_handle: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceSingleMeshVisible   instanceId:$instanceId   mesh_handle:$mesh_handle   enable:$enable")
        val res = faceunity.fuEnableInstanceSingleMeshVisible(instanceId, mesh_handle, enable)
        d(TAG, "fuEnableInstanceSingleMeshVisible   instanceId:$instanceId   mesh_handle:$mesh_handle   enable:$enable     res:$res")
        return res
    }

    internal fun enableInstanceUseFaceBeautyOrder(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceUseFaceBeautyOrder   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceUseFaceBeautyOrder(instanceId, enable)
        d(TAG, "fuEnableInstanceUseFaceBeautyOrder   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun setInstanceFaceBeautyOrder(instanceId: Int, items: IntArray?): Int {
        t(TAG, "fuSetInstanceFaceBeautyOrder   instanceId:" + instanceId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuSetInstanceFaceBeautyOrder(instanceId, items)
        d(TAG, "fuSetInstanceFaceBeautyOrder   instanceId:" + instanceId + "   items:" + Arrays.toString(items) + "     res:" + res)
        return res
    }

    /**
     * instance更新Body可见的数组
     *
     * @param instanceId Int
     * @param items      IntArray
     * @return Int
     */
    internal fun setInstanceBodyVisibleList(instanceId: Int, items: IntArray?): Int {
        t(TAG, "fuSetInstanceBodyVisibleList   instanceId:" + instanceId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuSetInstanceBodyVisibleList(instanceId, items)
        d(TAG, "fuSetInstanceBodyVisibleList   instanceId:" + instanceId + "   items:" + Arrays.toString(items) + "     res:" + res)
        return res
    }

    /**
     * instance更新Body不可见的数组
     *
     * @param instanceId Int
     * @param items      IntArray
     * @return Int
     */
    internal fun fuSetInstanceBodyInvisibleList(instanceId: Int, items: IntArray?): Int {
        t(TAG, "fuSetInstanceBodyInvisibleList   instanceId:" + instanceId + "   items:" + Arrays.toString(items))
        val res = faceunity.fuSetInstanceBodyInvisibleList(instanceId, items)
        d(TAG, "fuSetInstanceBodyInvisibleList   instanceId:" + instanceId + "   items:" + Arrays.toString(items) + "     res:" + res)
        return res
    }

    internal fun enableInstanceHideNeck(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceHideNeck   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceHideNeck(instanceId, enable)
        d(TAG, "fuEnableInstanceHideNeck   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun resetInstanceHead(instanceId: Int): Int {
        t(TAG, "fuResetInstanceHead   instanceId:$instanceId")
        val res = faceunity.fuResetInstanceHead(instanceId)
        d(TAG, "fuResetInstanceHead   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun enableInstanceFaceUpMode(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceFacepupMode   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceFacepupMode(instanceId, enable)
        d(TAG, "fuEnableInstanceFacepupMode   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun setInstanceFaceUp(instanceId: Int, name: String, value: Float): Int {
        t(TAG, "fuSetInstanceFacepup   instanceId:$instanceId   name:$name   value:$value")
        val res = faceunity.fuSetInstanceFacepup(instanceId, name, value)
        d(TAG, "fuSetInstanceFacepup   instanceId:$instanceId   name:$name   value:$value     res:$res")
        return res
    }

    internal fun setInstanceDeformation(instanceId: Int, name: String, value: Float): Int {
        t(TAG, "fuSetInstanceDeformation   instanceId:$instanceId   name:$name   value:$value")
        val res = faceunity.fuSetInstanceDeformation(instanceId, name, value)
        d(TAG, "fuSetInstanceDeformation   instanceId:$instanceId   name:$name   value:$value     res:$res")
        return res
    }

    internal fun enableInstanceExpressionBlend(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceExpressionBlend   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceExpressionBlend(instanceId, enable)
        d(TAG, "fuEnableInstanceExpressionBlend   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun setInstanceBlendExpression(instanceId: Int, data: FloatArray?): Int {
        t(TAG, "fuSetInstanceBlendExpression   instanceId:" + instanceId + "   items:" + Arrays.toString(data))
        val res = faceunity.fuSetInstanceBlendExpression(instanceId, data)
        t(TAG, "fuSetInstanceBlendExpression   instanceId:" + instanceId + "   items:" + Arrays.toString(data) + "     res:" + res)
        return res
    }

    internal fun setInstanceExpressionWeight0(instanceId: Int, data: FloatArray?): Int {
        t(TAG, "fuSetInstanceExpressionWeight0   instanceId:" + instanceId + "  items:" + Arrays.toString(data))
        val res = faceunity.fuSetInstanceExpressionWeight0(instanceId, data)
        d(TAG, "fuSetInstanceExpressionWeight0   instanceId:" + instanceId + "    items:" + Arrays.toString(data) + "     res:" + res)
        return res
    }

    internal fun setInstanceExpressionWeight1(instanceId: Int, data: FloatArray?): Int {
        t(TAG, "fuSetInstanceExpressionWeight1   instanceId:" + instanceId + "   items:" + Arrays.toString(data))
        val res = faceunity.fuSetInstanceExpressionWeight1(instanceId, data)
        d(TAG, "fuSetInstanceExpressionWeight1   instanceId:" + instanceId + "    items:" + Arrays.toString(data) + "     res:" + res)
        return res
    }

    internal fun enableInstanceFocusEyeToCamera(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceFocusEyeToCamera   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableInstanceFocusEyeToCamera(sceneId, enable)
        d(TAG, "fuEnableInstanceFocusEyeToCamera   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setInstanceFocusEyeToCameraParams(sceneId: Int, height: Float, distance: Float, weight: Float): Int {
        t(TAG, "fuSetInstanceFocusEyeToCameraParams   sceneId:$sceneId   height_adjust:$height   distance_adjust:$distance   weight:$weight")
        val res = faceunity.fuSetInstanceFocusEyeToCameraParams(sceneId, height, distance, weight)
        d(TAG, "fuSetInstanceFocusEyeToCameraParams   sceneId:$sceneId   height_adjust:$height   distance_adjust:$distance   weight:$weight   res:$res")
        return res
    }

    internal fun setInstanceRotDelta(instanceId: Int, value: Float): Int {
        t(TAG, "fuSetInstanceRotDelta   instanceId:$instanceId   value:$value")
        val res = faceunity.fuSetInstanceRotDelta(instanceId, value)
        d(TAG, "fuSetInstanceRotDelta   instanceId:$instanceId   value:$value     res:$res")
        return res
    }

    internal fun setInstanceScaleDelta(instanceId: Int, value: Float): Int {
        t(TAG, "fuSetInstanceScaleDelta   instanceId:$instanceId   value:$value")
        val res = faceunity.fuSetInstanceScaleDelta(instanceId, value)
        d(TAG, "fuSetInstanceScaleDelta   instanceId:$instanceId   value:$value     res:$res")
        return res
    }

    internal fun setInstanceTranslateDelta(instanceId: Int, value: Float): Int {
        t(TAG, "fuSetInstanceTranslateDelta   instanceId:$instanceId   value:$value")
        val res = faceunity.fuSetInstanceTranslateDelta(instanceId, value)
        d(TAG, "fuSetInstanceTranslateDelta   instanceId:$instanceId   value:$value     res:$res")
        return res
    }

    internal fun setInstanceTargetAngle(instanceId: Int, value: Float): Int {
        t(TAG, "fuSetInstanceTargetAngle   instanceId:$instanceId   value:$value")
        val res = faceunity.fuSetInstanceTargetAngle(instanceId, value)
        d(TAG, "fuSetInstanceTargetAngle   instanceId:$instanceId   value:$value     res:$res")
        return res
    }

    internal fun setInstanceColor(instanceId: Int, name: String, r: Int, g: Int, b: Int): Int {
        t(TAG, "fuSetInstanceColor   instanceId:$instanceId   name:$name   r:$r   g:$g   b:$b")
        val res = faceunity.fuSetInstanceColor(instanceId, name, r, g, b)
        d(TAG, "fuSetInstanceColor   instanceId:$instanceId   name:$name   r:$r   g:$g   b:$b   res:$res")
        return res
    }

    internal fun setInstanceColorIntensity(instanceId: Int, name: String, intensity: Float): Int {
        t(TAG, "fuSetInstanceColorIntensity   instanceId:$instanceId   name:$name   intensity:$intensity")
        val res = faceunity.fuSetInstanceColorIntensity(instanceId, name, intensity)
        d(TAG, "fuSetInstanceColorIntensity   instanceId:$instanceId   name:$name   intensity:$intensity     res:$res")
        return res
    }

    internal fun fuSetInstanceFaceBeautyColor(instanceId: Int, item: Int, r: Int, g: Int, b: Int): Int {
        t(TAG, "fuSetInstanceFacebeautyColor   instanceId:$instanceId   item:$item   r:$r   g:$g   b:$b")
        val res = faceunity.fuSetInstanceFacebeautyColor(instanceId, item, r, g, b)
        d(TAG, "fuSetInstanceFacebeautyColor   instanceId:$instanceId   item:$item   r:$r   g:$g   b:$b     res:$res")
        return res
    }

    internal fun playInstanceAnimation(instanceId: Int, item: Int): Int {
        t(TAG, "fuPlayInstanceAnimation   instanceId:$instanceId   item:$item")
        val res = faceunity.fuPlayInstanceAnimation(instanceId, item)
        d(TAG, "fuPlayInstanceAnimation   instanceId:$instanceId   item:$item     res:$res")
        return res
    }

    internal fun playInstanceAnimationOnce(instanceId: Int, item: Int): Int {
        t(TAG, "fuPlayInstanceAnimationOnce   instanceId:$instanceId   item:$item")
        val res = faceunity.fuPlayInstanceAnimationOnce(instanceId, item)
        d(TAG, "fuPlayInstanceAnimationOnce   instanceId:$instanceId   item:$item     res:$res")
        return res
    }

    internal fun startInstanceAnimation(instanceId: Int): Int {
        t(TAG, "fuStartInstanceAnimation   instanceId:$instanceId")
        val res = faceunity.fuStartInstanceAnimation(instanceId)
        d(TAG, "fuStartInstanceAnimation   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun pauseInstanceAnimation(instanceId: Int): Int {
        t(TAG, "fuPauseInstanceAnimation   instanceId:$instanceId")
        val res = faceunity.fuPauseInstanceAnimation(instanceId)
        d(TAG, "fuPauseInstanceAnimation   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun stopInstanceAnimation(instanceId: Int): Int {
        t(TAG, "fuStopInstanceAnimation   instanceId:$instanceId")
        val res = faceunity.fuStopInstanceAnimation(instanceId)
        d(TAG, "fuStopInstanceAnimation   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun resetInstanceAnimation(instanceId: Int): Int {
        t(TAG, "fuResetInstanceAnimation   instanceId:$instanceId")
        val res = faceunity.fuResetInstanceAnimation(instanceId)
        d(TAG, "fuResetInstanceAnimation   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun setInstanceAnimationTransitionTime(instanceId: Int, time: Float): Int {
        t(TAG, "fuSetInstanceAnimationTransitionTime   instanceId:$instanceId   time:$time")
        val res = faceunity.fuSetInstanceAnimationTransitionTime(instanceId, time)
        d(TAG, "fuSetInstanceAnimationTransitionTime   instanceId:$instanceId   time:$time     res:$res")
        return res
    }

    internal fun enableInstanceAnimationInternalLerp(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceAnimationInternalLerp   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceAnimationInternalLerp(instanceId, enable)
        d(TAG, "fuEnableInstanceAnimationInternalLerp   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    /**
     * DynamicBone releated api
     */
    internal fun enableInstanceDynamicBone(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceDynamicBone   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableDynamicBone(instanceId, enable)
        d(TAG, "fuEnableInstanceDynamicBone   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun resetInstanceDynamicBone(instanceId: Int, immediate: Int): Int {
        t(TAG, "fuResetInstanceDynamicBone   instanceId:$instanceId   immediate:$immediate")
        val res = faceunity.fuResetInstanceDynamicBone(instanceId, immediate)
        d(TAG, "fuResetInstanceDynamicBone   instanceId:$instanceId   immediate:$immediate     res:$res")
        return res
    }

    internal fun refreshInstanceDynamicBone(instanceId: Int, immediate: Int): Int {
        t(TAG, "fuRefreshInstanceDynamicBone   instanceId:$instanceId   immediate:$immediate")
        val res = faceunity.fuRefreshInstanceDynamicBone(instanceId, immediate)
        d(TAG, "fuRefreshInstanceDynamicBone   instanceId:$instanceId   immediate:$immediate     res:$res")
        return res
    }

    internal fun enableInstanceModelMatToBone(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceModelMatToBone   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceModelMatToBone(instanceId, enable)
        d(TAG, "fuEnableInstanceModelMatToBone   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun fuEnableInstanceSingleDynamicBone(instanceId: Int, mesh_handle: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceSingleDynamicBone   instanceId:$instanceId   mesh_handle:$mesh_handle   enable:$enable")
        val res = faceunity.fuEnableInstanceSingleDynamicBone(instanceId, mesh_handle, enable)
        d(TAG, "fuEnableInstanceSingleDynamicBone   instanceId:$instanceId   mesh_handle:$mesh_handle   enable:$enable     res:$res")
        return res
    }

    internal fun enableInstanceDynamicBoneTeleportMode(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceDynamicBoneTeleportMode   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceDynamicBoneTeleportMode(instanceId, enable)
        d(TAG, "fuEnableInstanceDynamicBoneTeleportMode   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun enableInstanceDynamicBoneRootTranslationSpeedLimitMode(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceDynamicBoneRootTranslationSpeedLimitMode   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceDynamicBoneRootTranslationSpeedLimitMode(instanceId, enable)
        d(TAG, "fuEnableInstanceDynamicBoneRootTranslationSpeedLimitMode   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    internal fun enableInstanceDynamicBoneRootRotationSpeedLimitMode(instanceId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableInstanceDynamicBoneRootRotationSpeedLimitMode   instanceId:$instanceId   enable:$enable")
        val res = faceunity.fuEnableInstanceDynamicBoneRootRotationSpeedLimitMode(instanceId, enable)
        d(TAG, "fuEnableInstanceDynamicBoneRootRotationSpeedLimitMode   instanceId:$instanceId   enable:$enable     res:$res")
        return res
    }

    /**
     * MVP related api
     */
    internal fun enableOrthogonalProjection(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableOrthogonalProjection   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableOrthogonalProjection(sceneId, enable)
        d(TAG, "fuEnableOrthogonalProjection   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setProjectionMatrixFov(sceneId: Int, fov: Float): Int {
        t(TAG, "fuSetProjectionMatrixFov   sceneId:$sceneId   fov:$fov")
        val res = faceunity.fuSetProjectionMatrixFov(sceneId, fov)
        d(TAG, "fuSetProjectionMatrixFov   sceneId:$sceneId   fov:$fov     res:$res")
        return res
    }

    internal fun setProjectionMatrixOrthoSize(sceneId: Int, size: Float): Int {
        t(TAG, "fuSetProjectionMatrixOrthoSize   sceneId:$sceneId   size:$size")
        val res = faceunity.fuSetProjectionMatrixOrthoSize(sceneId, size)
        d(TAG, "fuSetProjectionMatrixOrthoSize   sceneId:$sceneId   size:$size     res:$res")
        return res
    }

    internal fun setProjectionMatrixZnear(sceneId: Int, near: Float): Int {
        t(TAG, "fuSetProjectionMatrixZnear   sceneId:$sceneId   z_near:$near")
        val res = faceunity.fuSetProjectionMatrixZnear(sceneId, near)
        d(TAG, "fuSetProjectionMatrixZnear   sceneId:$sceneId   z_near:$near     res:$res")
        return res
    }

    internal fun setProjectionMatrixZfar(sceneId: Int, far: Float): Int {
        t(TAG, "fuSetProjectionMatrixZnearZfar   sceneId:$sceneId   z_far:$far")
        val res = faceunity.fuSetProjectionMatrixZfar(sceneId, far)
        d(TAG, "fuSetProjectionMatrixZnearZfar   sceneId:$sceneId   z_far:$far     res:$res")
        return res
    }

    internal fun enableOuterMVPMatrix(sceneId: Int, enable: Boolean): Int {
        t(TAG, "fuEnableOuterMVPMatrix   sceneId:$sceneId   enable:$enable")
        val res = faceunity.fuEnableOuterMVPMatrix(sceneId, enable)
        d(TAG, "fuEnableOuterMVPMatrix   sceneId:$sceneId   enable:$enable     res:$res")
        return res
    }

    internal fun setOuterProjectionMatrix(sceneId: Int, mat: FloatArray?): Int {
        t(TAG, "fuSetOuterProjectionMatrix   sceneId:" + sceneId + "   mat:" + Arrays.toString(mat))
        val res = faceunity.fuSetOuterProjectionMatrix(sceneId, mat)
        d(TAG, "fuSetOuterProjectionMatrix   sceneId:" + sceneId + "   mat:" + Arrays.toString(mat) + "     res:" + res)
        return res
    }

    internal fun setOuterViewMatrix(sceneId: Int, mat: FloatArray?): Int {
        t(TAG, "fuSetOuterViewMatrix   sceneId:" + sceneId + "   mat:" + Arrays.toString(mat))
        val res = faceunity.fuSetOuterViewMatrix(sceneId, mat)
        d(TAG, "fuSetOuterViewMatrix   sceneId:" + sceneId + "   mat:" + Arrays.toString(mat) + "     res:" + res)
        return res
    }

    internal fun setBackgroundParams(sceneId: Int, item: Int, xSize: Float, ySize: Float, xOffset: Float, yOffset: Float, isForeground: Boolean, mode: Int): Int {
        t(
            TAG,
            "fuSetBackgroundParams   sceneId:$sceneId   item:$item   x_size:$xSize   y_size:$ySize   x_offset:$xOffset   y_offset:$yOffset   is_foreground:$isForeground"
        )
        val res = faceunity.fuSetBackgroundParams(sceneId, item, xSize, ySize, xOffset, yOffset, isForeground, mode)
        d(
            TAG,
            "fuSetBackgroundParams   sceneId:$sceneId   item:$item   x_size:$xSize   y_size:$ySize   x_offset:$xOffset   y_offset:$yOffset   is_foreground:$isForeground     res:$res"
        )
        return res
    }

    internal fun setOuterModelMatrix(sceneId: Int, mat: FloatArray?): Int {
        t(TAG, "fuSetOuterModelMatrix   sceneId:" + sceneId + "   mat:" + Arrays.toString(mat))
        val res = faceunity.fuSetOuterModelMatrix(sceneId, mat)
        d(TAG, "fuSetOuterModelMatrix   sceneId:" + sceneId + "   mat:" + Arrays.toString(mat) + "     res:" + res)
        return res
    }

    /**
     * get api
     */
    internal fun getCameraAnimationFrameNumber(sceneId: Int, item: Int): Int {
        t(TAG, "fuGetCameraAnimationFrameNumber   sceneId:$sceneId   item:$item")
        val res = faceunity.fuGetCameraAnimationFrameNumber(sceneId, item)
        d(TAG, "fuGetCameraAnimationFrameNumber   sceneId:$sceneId   item:$item     res:$res")
        return res
    }

    internal fun getCameraAnimationProgress(sceneId: Int, item: Int): Float {
        t(TAG, "fuGetCameraAnimationProgress   sceneId:$sceneId   item:$item")
        val res = faceunity.fuGetCameraAnimationProgress(sceneId, item)
        d(TAG, "fuGetCameraAnimationProgress   sceneId:$sceneId   item:$item     res:$res")
        return res
    }

    internal fun getCameraAnimationTransitionProgress(sceneId: Int): Float {
        t(TAG, "fuGetCameraAnimationTransitionProgress   sceneId:$sceneId")
        val res = faceunity.fuGetCameraAnimationTransitionProgress(sceneId)
        d(TAG, "fuGetCameraAnimationTransitionProgress   sceneId:$sceneId      res:$res")
        return res
    }

    internal fun getInstanceFaceUpOriginalValue(instanceId: Int, name: String): Float {
        t(TAG, "fuGetInstanceFacepupOriginalValue   instanceId:$instanceId   name:$name")
        val res = faceunity.fuGetInstanceFacepupOriginalValue(instanceId, name)
        d(TAG, "fuGetInstanceFacepupOriginalValue   instanceId:$instanceId   name:$name     res:$res")
        return res
    }

    internal fun getInstanceFaceUpArray(instanceId: Int, rect: FloatArray?): Int {
        t(TAG, "fuGetInstanceFacepupArray   instanceId:" + instanceId + "   rect:" + Arrays.toString(rect))
        val res = faceunity.fuGetInstanceFacepupArray(instanceId, rect)
        d(TAG, "fuGetInstanceFacepupArray   instanceId:" + instanceId + "   rect:" + Arrays.toString(rect) + "     res:" + res)
        return res
    }

    internal fun getInstanceSkinColorIndex(instanceId: Int): Int {
        t(TAG, "fuGetInstanceSkinColorIndex   instanceId:$instanceId")
        val res = faceunity.fuGetInstanceSkinColorIndex(instanceId)
        d(TAG, "fuGetInstanceSkinColorIndex   instanceId:$instanceId     res:$res")
        return res
    }

    internal fun getInstancePosition(instanceId: Int, rect: FloatArray?): Int {
        t(TAG, "fuGetInstancePosition   instanceId:" + instanceId + "   rect:" + Arrays.toString(rect))
        val res = faceunity.fuGetInstancePosition(instanceId, rect)
        d(TAG, "fuGetInstancePosition   instanceId:" + instanceId + "   rect:" + Arrays.toString(rect) + "     res:" + res)
        return res
    }

    internal fun getInstanceHeadCenterScreenCoordinate(instanceId: Int, rect: FloatArray?): Int {
        t(TAG, "fuGetInstanceHeadCenterScreenCoordinate   instanceId:" + instanceId + "   rect:" + Arrays.toString(rect))
        val res = faceunity.fuGetInstanceHeadCenterScreenCoordinate(instanceId, rect)
        d(TAG, "fuGetInstanceHeadCenterScreenCoordinate   instanceId:" + instanceId + "   rect:" + Arrays.toString(rect) + "     res:" + res)
        return res
    }

    internal fun getInstanceBoneScreenCoordinate(instanceId: Int, name: String, rect: FloatArray?): Int {
        t(TAG, "fuGetInstanceBoneScreenCoordinate   instanceId:" + instanceId + "   name:" + name + "   rect:" + Arrays.toString(rect))
        val res = faceunity.fuGetInstanceBoneScreenCoordinate(instanceId, name, rect)
        d(TAG, "fuGetInstanceBoneScreenCoordinate   instanceId:" + instanceId + "   name:" + name + "   rect:" + Arrays.toString(rect) + "     res:" + res)
        return res
    }

    internal fun getInstanceFaceVertexScreenCoordinate(instanceId: Int, index: Int, rect: FloatArray?): Int {
        t(TAG, "fuGetInstanceFaceVertexScreenCoordinate   instanceId:" + instanceId + "   index:" + index + "   rect:" + Arrays.toString(rect))
        val res = faceunity.fuGetInstanceFaceVertexScreenCoordinate(instanceId, index, rect)
        d(TAG, "fuGetInstanceFaceVertexScreenCoordinate   instanceId:" + instanceId + "   index:" + index + "   rect:" + Arrays.toString(rect) + "     res:" + res)
        return res
    }

    internal fun getInstanceAnimationFrameNumber(instanceId: Int, item: Int): Int {
        t(TAG, "fuGetInstanceAnimationFrameNumber   instanceId:$instanceId   item:$item")
        val res = faceunity.fuGetInstanceAnimationFrameNumber(instanceId, item)
        d(TAG, "fuGetInstanceAnimationFrameNumber   instanceId:$instanceId   item:$item      res:$res")
        return res
    }

    internal fun getInstanceAnimationProgress(instanceId: Int, item: Int): Float {
        t(TAG, "fuGetInstanceAnimationProgress   instanceId:$instanceId   item:$item")
        val res = faceunity.fuGetInstanceAnimationProgress(instanceId, item)
        d(TAG, "fuGetInstanceAnimationProgress   instanceId:$instanceId   item:$item      res:$res")
        return res
    }

    internal fun getInstanceAnimationTransitionProgress(instanceId: Int, item: Int): Float {
        t(TAG, "fuGetInstanceAnimationTransitionProgress   instanceId:$instanceId   item:$item")
        val res = faceunity.fuGetInstanceAnimationTransitionProgress(instanceId, item)
        d(TAG, "fuGetInstanceAnimationTransitionProgress   instanceId:$instanceId   item:$item        res:$res")
        return res
    }

    /**
     * Other
     */
    internal fun fuEnableBinaryShaderProgram(enable: Boolean): Int {
        t(TAG, "fuEnableBinaryShaderProgram   enable:$enable")
        val res = faceunity.fuEnableBinaryShaderProgram(enable)
        d(TAG, "fuEnableBinaryShaderProgram   enable:$enable     res:$res")
        return res
    }

    internal fun fuSetBinaryShaderProgramDirectory(path: String): Int {
        t(TAG, "fuSetBinaryShaderProgramDirectory   path:$path")
        val res = faceunity.fuSetBinaryShaderProgramDirectory(path)
        d(TAG, "fuSetBinaryShaderProgramDirectory   path:$path     res:$res")
        return res
    }
    //************************** 错误回调 ******************************/
    /**
     * 回调返回错误code以及错误信息
     */
    internal fun callBackSystemError(): String? {
        val error = faceunity.fuGetSystemError()
        if (error != 0) {
            val errorMessage = faceunity.fuGetSystemErrorString(error)
            return "error:" + systemErrorMaps[error] + "     errorMessage:" + errorMessage
        }
        return null
    }

    private val systemErrorMaps: HashMap<Int, String> = object : HashMap<Int, String>() {
        init {
            put(1, "随机种子生成失败")
            put(2, "机构证书解析失败")
            put(3, "鉴权服务器连接失败")
            put(4, "加密连接配置失败")
            put(5, "客户证书解析失败")
            put(6, "客户密钥解析失败")
            put(7, "建立加密连接失败")
            put(8, "设置鉴权服务器地址失败")
            put(9, "加密连接握手失败")
            put(10, "加密连接验证失败")
            put(11, "请求发送失败")
            put(12, "响应接收失败")
            put(13, "异常鉴权响应")
            put(14, "证书权限信息不完整")
            put(15, "鉴权功能未初始化")
            put(16, "创建鉴权线程失败")
            put(17, "鉴权数据被拒绝")
            put(18, "无鉴权数据")
            put(19, "异常鉴权数据")
            put(20, "证书过期")
            put(21, "无效证书")
            put(22, "系统数据解析失败")
            put(0x100, "加载了非正式道具包（debug版道具）")
            put(0x200, "运行平台被证书禁止")
        }
    }

    /**
     * avatar的大小
     */
    internal fun humanProcessorSetAvatarScale(scale: Float) {
        faceunity.fuHumanProcessorSetAvatarScale(scale)
        d(TAG, "humanProcessorSetAvatarScale   scale:$scale")
    }

    /**
     * avatar距离人像的偏移量
     */
    internal fun humanProcessorSetAvatarGlobalOffset(offsetX: Float, offsetY: Float, offsetZ: Float) {
        faceunity.fuHumanProcessorSetAvatarGlobalOffset(offsetX, offsetY, offsetZ)
        d(TAG, "humanProcessorSetAvatarGlobalOffset   offsetX:$offsetX   offsetY:$offsetY  offsetZ:$offsetZ")
    }

    /**
     * 设置动画过滤参数
     */
    internal fun humanProcessorSetAvatarAnimFilterParams(nBufferFrames: Int, pos: Float, angle: Float) {
        faceunity.fuHumanProcessorSetAvatarAnimFilterParams(nBufferFrames, pos, angle)
        d(TAG, "humanProcessorSetAvatarAnimFilterParams   nBufferFrames:$nBufferFrames   pos:$pos  angle:$angle")
    }

    /**
     * avatar活动范围
     */
    internal fun setHumanProcessorTranslationScale(sceneId: Int, x: Float, y: Float, z: Float) {
        faceunity.fuSetHumanProcessorTranslationScale(sceneId, x, y, z)
        d(TAG, "setHumanProcessorTranslationScale   sceneId:$sceneId   x:$x  y:$y  z:$z")
    }
}