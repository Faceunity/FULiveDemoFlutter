package com.faceunity.core.renderer

import android.graphics.Bitmap
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.opengl.Matrix
import com.faceunity.core.entity.FURenderFrameData
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.entity.FURenderOutputData
import com.faceunity.core.enumeration.FUExternalInputEnum
import com.faceunity.core.enumeration.FUInputBufferEnum
import com.faceunity.core.enumeration.FUInputTextureEnum
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.listener.OnGlRendererListener
import com.faceunity.core.program.ProgramTexture2d
import com.faceunity.core.utils.GlUtil
import com.faceunity.core.utils.LimitFpsUtil
import com.faceunity.core.utils.ScreenUtils
import com.faceunity.core.weight.GLTextureView


/**
 *
 * DESC：
 * Created on 2021/1/27
 *
 */
abstract class BaseFURenderer(protected var gLSurfaceView: GLTextureView?, protected var glRendererListener: OnGlRendererListener?) : GLTextureView.Renderer {
    val TAG = "KIT_BaseFURenderer"

    /** FURenderKit**/
    protected val mFURenderKit by lazy { FURenderKit.getInstance() }

    /** 渲染矩阵  */
    val TEXTURE_MATRIX = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
    )

    val CAMERA_TEXTURE_MATRIX = floatArrayOf(
        0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f
    )
    val CAMERA_TEXTURE_MATRIX_BACK = floatArrayOf(
        0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
    )


    /** 纹理Program**/

    protected var programTexture2d: ProgramTexture2d? = null

    /**GLSurfaceView尺寸**/
    protected var surfaceViewWidth: Int = 1
    protected var surfaceViewHeight: Int = 1

    /**渲染参数配置**/
    @Volatile
    protected var currentFURenderInputData = FURenderInputData(0, 0)
    protected var originalTextId = 0
    protected var originalWidth = 0
    protected var originalHeight = 0
    protected var externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA//数据源类型
    protected var inputTextureType = FUInputTextureEnum.FU_ADM_FLAG_COMMON_TEXTURE//纹理类型
    protected var inputBufferType = FUInputBufferEnum.FU_FORMAT_NV21_BUFFER//数据类型
    protected var deviceOrientation = 90//手机设备朝向


    /** 渲染后纹理结果 **/
    protected var faceUnity2DTexId = 0

    @Volatile
    protected var currentFURenderOutputData: FURenderOutputData? = null


    /**换算矩阵**/
    protected var defaultFUTexMatrix: FloatArray = TEXTURE_MATRIX.copyOf()//默认FURender图形矩阵
    protected var defaultFUMvpMatrix: FloatArray = TEXTURE_MATRIX.copyOf()//默认FURender图形矩阵
    protected var currentFUTexMatrix: FloatArray = TEXTURE_MATRIX.copyOf()//最终渲染FURender图形矩阵
    protected var currentFUMvpMatrix: FloatArray = TEXTURE_MATRIX.copyOf()//最终渲染FURender图形矩阵
    protected var originTexMatrix = TEXTURE_MATRIX.copyOf() //原始图形纹理矩阵
    protected var originMvpMatrix = TEXTURE_MATRIX.copyOf()//原始图形绑定矩阵
    protected var smallViewMatrix: FloatArray = TEXTURE_MATRIX.copyOf()//小窗图形绑定矩阵

    /**特效处理开关**/
    @Volatile
    protected var renderSwitch = true
    private var frameCount = 0
    private var frameFuRenderMinCount = 0

    /**activity是否进入Pause状态**/
    protected var isActivityPause = false

    /** 全身 avatar 相关 **/
    protected var drawSmallViewport = false
    protected val smallViewportWidth = ScreenUtils.dip2px(FURenderManager.mContext, 90)
    protected val smallViewportHeight = ScreenUtils.dip2px(FURenderManager.mContext, 160)
    protected var smallViewportX = 0
    protected var smallViewportY = 0
    protected val smallViewportHorizontalPadding = ScreenUtils.dip2px(FURenderManager.mContext, 16)
    protected val smallViewportTopPadding = ScreenUtils.dip2px(FURenderManager.mContext, 88)
    protected val smallViewportBottomPadding = ScreenUtils.dip2px(FURenderManager.mContext, 100)
    protected var touchX = 0
    protected var touchY = 0


    /**
     * FURender渲染开关设置
     * @param isOpen Boolean
     */
    fun setFURenderSwitch(isOpen: Boolean) {
        if (!isOpen) {
            gLSurfaceView?.queueEvent {
                mFURenderKit.clearCacheResource()
            }
        }
        renderSwitch = isOpen
    }

    /**
     * 设置渲染过渡帧数（原始数据-》Furender渲染数据；避免黑屏）
     * @param count Int
     */
    fun setTransitionFrameCount(count: Int) {
        frameFuRenderMinCount = count
    }

    //region GLSurfaceView.Renderer实现


    /**
     * 视图创建
     * 初始化Program，构建相机绑定纹理，打开相机，初始化FaceUnityRenderer
     * @param gl GL10
     * @param config EGLConfig
     */
    override fun onSurfaceCreated(config: EGLConfig?) {
        destroyGlSurface()
        GlUtil.logVersionInfo()
        programTexture2d = ProgramTexture2d()
        frameCount = 0
        surfaceCreated(config)
        glRendererListener?.onSurfaceCreated()
    }

    protected abstract fun surfaceCreated(config: EGLConfig?)

    /**
     * 根据视图宽高，初始化配置
     * @param gl GL10
     * @param width Int
     * @param height Int
     */
    override fun onSurfaceChanged(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        if (surfaceViewWidth != width || surfaceViewHeight != height) {
            surfaceViewWidth = width
            surfaceViewHeight = height
            surfaceChanged(width, height)
        }
        smallViewportX = width - smallViewportWidth - smallViewportHorizontalPadding
        smallViewportY = smallViewportBottomPadding
        glRendererListener?.onSurfaceChanged(width, height)
    }

    protected abstract fun surfaceChanged(width: Int, height: Int)


    override fun onDrawFrame() {
        /* 应用是否在前台 */
        if (isActivityPause) {
            return
        }
        /* 确认是否只渲染图片 */
        if (mIsBitmapPreview) {
            drawBitmapFrame(mBitmap2dTexId, mBitmapTexMatrix, mBitmapMvpMatrix)
            return
        }
        /* 确认环境是否正确 */
        if (!prepareRender()) {
            return
        }
        /* 确认数据是否正确 */
        val inputData = buildFURenderInputData()
        if ((inputData.imageBuffer == null || inputData.imageBuffer!!.buffer == null) && (inputData.texture == null || inputData.texture!!.texId <= 0)) {
            return
        }
        /* 特效合成，并通过回调确认最终渲染数据，合成数据，渲染矩阵  */
        if (renderSwitch && frameCount++ >= frameFuRenderMinCount) {
            val frameData = FURenderFrameData(defaultFUTexMatrix.copyOf(), defaultFUMvpMatrix.copyOf())
            glRendererListener?.onRenderBefore(inputData)//特效合成前置处理
            currentFURenderOutputData = mFURenderKit.renderWithInput(inputData)//特效合成
            faceUnity2DTexId = currentFURenderOutputData!!.texture?.texId ?: 0
            glRendererListener?.onRenderAfter(currentFURenderOutputData!!, frameData)  //纹理合成后置处理
            currentFUTexMatrix = frameData.texMatrix
            currentFUMvpMatrix = frameData.mvpMatrix
        }
        /* 渲染 */
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        drawRenderFrame()
        /* 渲染完成回调 */
        glRendererListener?.onDrawFrameAfter()
        /* 循环调用 */
        if (externalInputType != FUExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA) {
            LimitFpsUtil.limitFrameRate()//循环调用
            gLSurfaceView?.requestRender()
        }
    }


    protected abstract fun prepareRender(): Boolean

    protected abstract fun buildFURenderInputData(): FURenderInputData

    protected abstract fun drawRenderFrame()


    //endregion


    //region 图片模式
    /** 图片模式**/
    private var mIsBitmapPreview = false //是否只渲染图片
    private var mShotBitmap: Bitmap? = null //图片资源
    private var mBitmap2dTexId = 0
    private var mBitmapMvpMatrix = TEXTURE_MATRIX.copyOf()
    private var mBitmapTexMatrix = TEXTURE_MATRIX.copyOf()

    private fun drawBitmapFrame(texId: Int, texMatrix: FloatArray, mvpMatrix: FloatArray) {
        if (mBitmap2dTexId > 0) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            programTexture2d?.drawFrame(texId, texMatrix, mvpMatrix)
        }
    }

    /**
     * 渲染单张图片
     * @param bitmap Bitmap
     */
    protected fun drawImageTexture(bitmap: Bitmap) {
        mIsBitmapPreview = true
        mShotBitmap = bitmap
        gLSurfaceView?.queueEvent {
            deleteBitmapTexId()
            mBitmap2dTexId = GlUtil.createImageTexture(bitmap)
            mBitmapMvpMatrix =
                GlUtil.changeMvpMatrixCrop(surfaceViewWidth.toFloat(), surfaceViewHeight.toFloat(), bitmap.width.toFloat(), bitmap.height.toFloat())
            Matrix.scaleM(mBitmapMvpMatrix, 0, 1f, -1f, 1f)
        }
        gLSurfaceView?.requestRender()
    }

    /**
     * 移除单张图片渲染
     */
    protected fun dismissImageTexture() {
        mShotBitmap = null
        mIsBitmapPreview = false
        gLSurfaceView?.queueEvent {
            deleteBitmapTexId()
        }
        gLSurfaceView?.requestRender()
    }

    /**
     * 移除图片纹理
     */
    private fun deleteBitmapTexId() {
        if (mBitmap2dTexId > 0) {
            GlUtil.deleteTextures(intArrayOf(mBitmap2dTexId))
            mBitmap2dTexId = 0
        }
    }


    //endregion


    protected open fun destroyGlSurface() {
        deleteBitmapTexId()
        if (originalTextId != 0) {
            GlUtil.deleteTextures(intArrayOf(originalTextId))
            originalTextId = 0
        }
        if (faceUnity2DTexId != 0) {
            GlUtil.deleteTextures(intArrayOf(faceUnity2DTexId))
            faceUnity2DTexId = 0
        }
        programTexture2d?.let {
            it.release()
            programTexture2d = null
        }
        glRendererListener?.onSurfaceDestroy()
    }


}