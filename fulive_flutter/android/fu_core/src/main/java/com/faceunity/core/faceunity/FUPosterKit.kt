package com.faceunity.core.faceunity

import android.graphics.Bitmap
import android.opengl.GLES20
import com.faceunity.core.callback.OnPosterRenderCallback
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.enumeration.*
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.*


/**
 *
 * DESC：
 * Created on 2021/2/17
 *
 */
class FUPosterKit private constructor() {

    companion object {
        const val TAG = "KIT_FUPosterKit"

        @Volatile
        private var INSTANCE: FUPosterKit? = null

        @JvmStatic
        fun getInstance(handleData: FUBundleData, callback: OnPosterRenderCallback): FUPosterKit {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = FUPosterKit()
                    }
                }
            }
            INSTANCE!!.handleData = handleData
            INSTANCE!!.posterRenderCallback = callback
            INSTANCE!!.bindController(handleData)
            return INSTANCE!!
        }
    }

    private var posterRenderCallback: OnPosterRenderCallback? = null
    private lateinit var handleData: FUBundleData

    private val mPosterController by lazy { FURenderBridge.getInstance().mPosterController }
    private val mFURenderKit by lazy { FURenderKit.getInstance() }
    private val mFUAIKit by lazy { FUAIKit.getInstance() }

    private fun bindController(handleData: FUBundleData) {
        mPosterController.loadControllerBundle(FUFeaturesData(handleData))
    }

    //region 初始化

    /**Suface尺寸**/
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0


    /**模版相关参数**/
    private var templatePath: String? = null
    var templateWidth = 720
    var templateHeight = 1280
    private var templateBytes: ByteArray? = null
    private var warpIntensity: Double = 1.0

    /**照片相关参数**/
    var photoWidth = 720
    var photoHeight = 1280
    private var photoBytes: ByteArray? = null
    private var photoRGBABytes: ByteArray? = null

    /**纹理**/
    private var photoTextureId = 0
    private var mergeTexId = 0


    /**
     * 绑定窗口视图尺寸
     * @param width Int
     * @param height Int
     */
    fun bindSurfaceSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }
    //endregion初始化


    //region 接口调用
    fun renderPoster(photoBitmap: Bitmap, photoTexId: Int, templatePath: String, intensity: Double) {
        this.templatePath = templatePath
        warpIntensity = intensity
        loadPhotoData(photoBitmap, photoTexId)
        if (!hasPhotoDraw) {
            return
        }
        loadTemplateData(templatePath, intensity)
        if (hasTemplateDraw) {
            doMerge()
        }
    }


    fun renderPoster(photoPath: String, templatePath: String, intensity: Double) {
        this.templatePath = templatePath
        warpIntensity = intensity
        loadPhotoData(photoPath)
        if (!hasPhotoDraw) {
            return
        }
        loadTemplateData(templatePath, intensity)
        if (hasTemplateDraw) {
            doMerge()
        }
    }


    /**
     * 绑定照片脸位
     * @param index Int
     */
    fun bindPhotoData(index: Int) {
        if (!isNeedPhotoDraw) {
            return
        }
        val mPhotoLandmarks = FloatArray(75 * 2)
        mPosterController.getLandmarksData(index, mPhotoLandmarks)
        mPosterController.loadPosterPhoto(photoWidth, photoHeight, photoRGBABytes!!, mPhotoLandmarks)
        hasPhotoDraw = true
        if (templatePath == null) {
            return
        }
        loadTemplateData(templatePath!!, warpIntensity)
        if (hasTemplateDraw) {
            doMerge()
        }
    }


    fun updateTemplate(templatePath: String, intensity: Double) {
        if (!hasPhotoDraw) {
            FULogger.e(TAG, "please renderPoster first")
            return
        }
        hasTemplateDraw = false
        loadTemplateData(templatePath, intensity)
        if (hasTemplateDraw) {
            doMerge()
        }
    }


    //endregion接口调用

    //region 加载图片资源

    /**坐标换算**/
    private var mViewPortX: Int = 0
    private var mViewPortY = 0
    private var mViewPortScale = 1f


    /**业务标识**/
    private var hasPhotoDraw = false
    private var isNeedPhotoDraw = false
    private var hasTemplateDraw = false

    /**
     * 初始化照片数据
     */
    private fun loadPhotoData(photoPath: String) {
        val mPhotoBitmap = FileUtils.loadBitmapFromExternal(photoPath, 720)
        destroyPhotoTexture()
        val photoTextureId = GlUtil.createImageTexture(mPhotoBitmap)
        loadPhotoData(mPhotoBitmap, photoTextureId)
    }

    /**
     * 初始化照片数据
     */
    private fun loadPhotoData(photoBitmap: Bitmap, photoTexId: Int) {
        photoTextureId = photoTexId;
        photoWidth = photoBitmap.width
        photoHeight = photoBitmap.height
        scale(photoWidth, photoHeight)
        photoRGBABytes = FileUtils.loadRgbaByteFromBitmap(photoBitmap)
        photoBytes = BitmapUtils.getNV21(photoWidth, photoHeight, photoBitmap, false)
        var photoFace = 0
        for (i in 0 until 50) {
            mFUAIKit.clearCameraCache()
            photoFace = mFUAIKit.trackFace(photoBytes!!, FUInputBufferEnum.FU_FORMAT_NV21_BUFFER, photoWidth, photoHeight, 0)
            if (photoFace > 0) {
                break
            }
        }
        when (photoFace) {
            1 -> {
                if (mPosterController.checkRotation()) {
                    posterRenderCallback?.onPhotoLoaded(-1)
                    isNeedPhotoDraw = false
                } else {
                    val mPhotoLandmarks = FloatArray(75 * 2)
                    mPosterController.getLandmarksData(0, mPhotoLandmarks)
                    mPosterController.loadPosterPhoto(photoWidth, photoHeight, photoRGBABytes!!, mPhotoLandmarks)
                    hasPhotoDraw = true
                }
            }
            0 -> {
                posterRenderCallback?.onPhotoLoaded(0)
                isNeedPhotoDraw = false
            }
            else -> {
                isNeedPhotoDraw = true
                posterRenderCallback?.onPhotoLoaded(photoFace, getPhotoMaskData(photoFace))
            }
        }
    }


    /**
     * 初始化模版数据
     */
    private fun loadTemplateData(templatePath: String, warpIntensity: Double) {
        val mTemplateBitmap = FileUtils.loadBitmapFromLocal(FURenderManager.mContext, templatePath)
        if (mTemplateBitmap == null) {
            FULogger.e(TAG, "loadTemplateData failed TemplateData path:$templatePath")
            return
        }

        templateWidth = mTemplateBitmap.width
        templateHeight = mTemplateBitmap.height
        val mTemplateRGBABytes = FileUtils.loadRgbaByteFromBitmap(mTemplateBitmap)
        templateBytes = BitmapUtils.getNV21(templateWidth, templateHeight, mTemplateBitmap)
        var mTemplateFace = 0
        for (i in 0 until 50) {
            mFUAIKit.clearCameraCache()
            mTemplateFace = mFUAIKit.trackFace(templateBytes!!, FUInputBufferEnum.FU_FORMAT_NV21_BUFFER, templateWidth, templateHeight, 0)
            if (mTemplateFace > 0) {
                break
            }
        }
        hasTemplateDraw = if (mTemplateFace > 0) {
            val mTemplateLandmarks = FloatArray(75 * 2)
            mPosterController.getLandmarksData(0, mTemplateLandmarks)
            mPosterController.fixPosterFaceParam(warpIntensity)
            mPosterController.loadPosterTemplate(templateWidth, templateHeight, mTemplateRGBABytes, mTemplateLandmarks)
            true
        } else {
            posterRenderCallback?.onTemplateLoaded(mTemplateFace)
            false
        }
    }


    private fun scale(photoWidth: Int, photoHeight: Int) {
        val scale = viewWidth.toFloat() * photoHeight / viewHeight / photoWidth
        when {
            scale > 1 -> {
                mViewPortY = 0
                mViewPortScale = viewHeight.toFloat() / photoHeight
                mViewPortX = ((viewWidth - mViewPortScale * photoWidth) / 2).toInt()
            }
            scale < 1 -> {
                mViewPortX = 0
                mViewPortScale = viewWidth.toFloat() / photoWidth
                mViewPortY = ((viewHeight - mViewPortScale * photoHeight) / 2).toInt()
            }
            else -> {
                mViewPortX = 0
                mViewPortY = 0
                mViewPortScale = viewWidth.toFloat() / photoWidth
            }
        }
    }

    /**
     * 获取多人脸点位坐标
     * @param trackFace Int
     * @return ArrayList<FloatArray>
     */
    private fun getPhotoMaskData(trackFace: Int): ArrayList<FloatArray> {
        val array = ArrayList<FloatArray>()
        for (i in 0 until trackFace) {
            val faceRectData: FloatArray = mPosterController.getFaceRectData(i, 0)
            val data = DecimalUtils.copyArray(faceRectData)
            array.add(convertFaceRect(data))
        }
        return array
    }

    /**
     * 点位转换，获取人脸坐标
     * @param faceRect FloatArray
     * @return FloatArray
     */
    private fun convertFaceRect(faceRect: FloatArray): FloatArray {
        val newFaceRect = FloatArray(4)
        // 以输入图像的左上角为顶点
        newFaceRect[0] = faceRect[0] * mViewPortScale + mViewPortX
        newFaceRect[1] = faceRect[1] * mViewPortScale + mViewPortY
        newFaceRect[2] = faceRect[2] * mViewPortScale + mViewPortX
        newFaceRect[3] = faceRect[3] * mViewPortScale + mViewPortY
        return newFaceRect
    }

    //endregion 加载图片资源

    //region 合成
    private fun doMerge() {
        if (!hasPhotoDraw || !hasTemplateDraw) {
            return
        }
        var mergedStatus = false

        val renderData = FURenderInputData(templateWidth, templateHeight)
        renderData.texture = FURenderInputData.FUTexture(FUInputTextureEnum.FU_ADM_FLAG_COMMON_TEXTURE, photoTextureId)
        renderData.imageBuffer = FURenderInputData.FUImageBuffer(FUInputBufferEnum.FU_FORMAT_NV21_BUFFER, templateBytes)
        renderData.renderConfig = FURenderInputData.FURenderConfig(
            FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE, 0, 90,
            CameraFacingEnum.CAMERA_FRONT, FUTransformMatrixEnum.CCROT0_FLIPVERTICAL,
            FUTransformMatrixEnum.CCROT0_FLIPVERTICAL
        )
        /*降低错误率，最多循环调用50次*/
        for (i in 0..50) {
            mergeTexId = FURenderBridge.getInstance().renderWithInput(renderData, 1).texture?.texId ?: 0
            val faceCount = mFUAIKit.isTracking()
            if (faceCount > 0) {
                mergedStatus = true
                break
            }
        }
        posterRenderCallback?.onMergeResult(mergedStatus, mergeTexId)
    }

    //endregion

    fun onDestroy() {
        photoBytes = null
        photoRGBABytes = null
        templateBytes = null
        mViewPortScale = 1f
        mViewPortX = 0
        mViewPortY = 0
        hasPhotoDraw = false
        isNeedPhotoDraw = false
        hasTemplateDraw = false
        posterRenderCallback = null
        if (photoTextureId != 0) {
            GlUtil.deleteTextures(intArrayOf(photoTextureId))
            photoTextureId = 0
        }
        if (mergeTexId != 0) {
            GlUtil.deleteTextures(intArrayOf(mergeTexId))
            mergeTexId = 0
        }
        mFUAIKit.faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.VIDEO)
        FURenderBridge.getInstance().mPosterController.release()

    }


    /**
     * 回收相片纹理
     */
    private fun destroyPhotoTexture() {
        if (photoTextureId != 0) {
            val textures = intArrayOf(photoTextureId)
            GLES20.glDeleteTextures(1, textures, 0)
            photoTextureId = 0
        }

    }


}