package com.faceunity.core.renderer

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import com.faceunity.core.camera.FUCamera
import com.faceunity.core.camera.FUCameraPreviewData
import com.faceunity.core.entity.FUCameraConfig
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.enumeration.*
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.infe.ICameraRenderer
import com.faceunity.core.listener.OnFUCameraListener
import com.faceunity.core.listener.OnGlRendererListener
import com.faceunity.core.media.photo.OnPhotoRecordingListener
import com.faceunity.core.media.photo.PhotoRecordHelper
import com.faceunity.core.program.ProgramTextureOES
import com.faceunity.core.utils.DecimalUtils
import com.faceunity.core.utils.GlUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs


/**
 *
 * DESC：默认配置GlSurfaceRenderer 实现相机跟特效功能
 * Created on 2020/12/14
 *
 */

class CameraRenderer(gLSurfaceView: GLSurfaceView?, private val cameraConfig: FUCameraConfig, glRendererListener: OnGlRendererListener?) :
    BaseFURenderer(gLSurfaceView, glRendererListener), ICameraRenderer {

    /**相机**/
    var fUCamera: FUCamera = FUCamera.getInstance()
    var isCameraPreviewFrame = false

    /**传感器**/
    private val mSensorManager by lazy { FURenderManager.mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val mSensor by lazy { mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    /**渲染配置**/
    private val mFURenderInputDataLock = Any()
    private var mProgramTextureOES: ProgramTextureOES? = null

    //region 初始化

    init {
        externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA
        inputTextureType = FUInputTextureEnum.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE
        inputBufferType = FUInputBufferEnum.FU_FORMAT_NV21_BUFFER
        gLSurfaceView?.setEGLContextClientVersion(2)
        gLSurfaceView?.setRenderer(this)
        gLSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    /**
     * 绑定相机回调
     * 更新矩阵
     * 更新相机配置
     * 更新渲染参数
     * @return <no name provided>
     */
    private fun getFUCameraListener() = object : OnFUCameraListener {
        override fun onPreviewFrame(previewData: FUCameraPreviewData) {
            synchronized(mFURenderInputDataLock) {
                if (originalWidth != previewData.width || originalHeight != previewData.height) {
                    originalWidth = previewData.width
                    originalHeight = previewData.height
                    defaultFUMvpMatrix =
                        GlUtil.changeMvpMatrixCrop(surfaceViewWidth.toFloat(), surfaceViewHeight.toFloat(), originalHeight.toFloat(), originalWidth.toFloat())
                    smallViewMatrix = GlUtil.changeMvpMatrixCrop(90f, 160f, originalHeight.toFloat(), originalWidth.toFloat())
                }
                cameraConfig.cameraFacing = previewData.cameraFacing
                cameraConfig.cameraHeight = previewData.height
                cameraConfig.cameraWidth = previewData.width
                currentFURenderInputData = FURenderInputData(originalWidth, originalHeight)
                    .apply {
                        imageBuffer = FURenderInputData.FUImageBuffer(inputBufferType, previewData.buffer)
                        texture = FURenderInputData.FUTexture(inputTextureType, originalTextId)
                        renderConfig.apply {
                            externalInputType = this@CameraRenderer.externalInputType
                            inputOrientation = previewData.cameraOrientation
                            deviceOrientation = this@CameraRenderer.deviceOrientation
                            cameraFacing = previewData.cameraFacing
                            if (cameraFacing == CameraFacingEnum.CAMERA_FRONT) {
                                originTexMatrix = DecimalUtils.copyArray(CAMERA_TEXTURE_MATRIX)
                                inputTextureMatrix = FUTransformMatrixEnum.CCROT90_FLIPHORIZONTAL
                                inputBufferMatrix = FUTransformMatrixEnum.CCROT90_FLIPHORIZONTAL
                            } else {
                                originTexMatrix = DecimalUtils.copyArray(CAMERA_TEXTURE_MATRIX_BACK)
                                inputTextureMatrix = FUTransformMatrixEnum.CCROT270
                                inputBufferMatrix = FUTransformMatrixEnum.CCROT270
                            }
                        }
                    }
                isCameraPreviewFrame = true
            }
            gLSurfaceView?.requestRender()
        }
    }

    //endregion

    //region 生命周期调用


    /**Activity onResume**/
    override fun onResume() {
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        if (isActivityPause) {
            gLSurfaceView?.onResume()
        }
        isActivityPause = false
    }


    /**Activity onPause**/
    override fun onPause() {
        isActivityPause = true
        mSensorManager.unregisterListener(mSensorEventListener)
        fUCamera.closeCamera()
        val countDownLatch = CountDownLatch(1)
        gLSurfaceView?.queueEvent {
            cacheLastBitmap()
            destroyGlSurface()
            countDownLatch.countDown()
        }
        try {
            countDownLatch.await(500, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            // ignored
        }
        gLSurfaceView?.onPause()
    }

    /**Activity onDestroy**/
    override fun onDestroy() {
        mCacheBitmap = null
        glRendererListener = null
        gLSurfaceView = null
    }


    //endregion 生命周期调用


    //region  GLSurfaceView.Renderer相关


    override fun surfaceCreated(gl: GL10?, config: EGLConfig?) {
        originalTextId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
        mProgramTextureOES = ProgramTextureOES()
        isCameraPreviewFrame = false
        fUCamera.openCamera(cameraConfig, originalTextId, getFUCameraListener())
    }

    override fun surfaceChanged(gl: GL10?, width: Int, height: Int) {
        defaultFUMvpMatrix = GlUtil.changeMvpMatrixCrop(width.toFloat(), height.toFloat(), originalHeight.toFloat(), originalWidth.toFloat())
    }


    override fun prepareRender(gl: GL10?): Boolean {
        if (!isCameraPreviewFrame) {
            drawCacheBitmap()
            return false
        }
        if (mProgramTextureOES == null || programTexture2d == null) {
            return false
        }
        val surfaceTexture = fUCamera.getSurfaceTexture() ?: return false
        try {
            surfaceTexture.updateTexImage()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun buildFURenderInputData(): FURenderInputData {
        synchronized(mFURenderInputDataLock) {
            return currentFURenderInputData.clone()
        }
    }


    override fun drawRenderFrame(gl: GL10?) {
        if (faceUnity2DTexId > 0 && renderSwitch) {
            programTexture2d!!.drawFrame(faceUnity2DTexId, currentFUTexMatrix, currentFUMvpMatrix)
        } else if (originalTextId > 0) {
            mProgramTextureOES!!.drawFrame(originalTextId, originTexMatrix, defaultFUMvpMatrix)
        }
        if (drawSmallViewport) {
            GLES20.glViewport(smallViewportX, smallViewportY, smallViewportWidth, smallViewportHeight)
            mProgramTextureOES!!.drawFrame(originalTextId, originTexMatrix, smallViewMatrix)
            GLES20.glViewport(0, 0, surfaceViewWidth, surfaceViewHeight)
        }
    }


    override fun destroyGlSurface() {
        mProgramTextureOES?.let {
            it.release()
            mProgramTextureOES = null
        }
        deleteCacheBitmapTexId()
        super.destroyGlSurface()
    }

//endregion


//region 其他业务


    /**
     * 窗口渲染固定图片
     * @param bitmap Bitmap
     */
    override fun showImageTexture(bitmap: Bitmap) {
        drawImageTexture(bitmap)
    }

    /**
     * 移除图片渲染
     */
    override fun hideImageTexture() {
        dismissImageTexture()
    }

    /**
     * 全身Avatar小窗口显示
     * @param isShow Boolean
     */
    override fun drawSmallViewport(isShow: Boolean) {
        drawSmallViewport = isShow
    }


    /**
     * avatar 小窗拖拽
     * @param x Int
     * @param y Int
     * @param action Int
     */
    override fun onTouchEvent(x: Int, y: Int, action: Int) {
        if (!drawSmallViewport) {
            return
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (x < smallViewportHorizontalPadding || x > surfaceViewWidth - smallViewportHorizontalPadding || y < smallViewportTopPadding || y > surfaceViewHeight - smallViewportBottomPadding
            ) {
                return
            }
            val touchX = touchX
            val touchY = touchY
            this.touchX = x
            this.touchY = y
            val distanceX = x - touchX
            val distanceY = y - touchY
            var viewportX = smallViewportX
            var viewportY = smallViewportY
            viewportX += distanceX
            viewportY -= distanceY
            if (viewportX < smallViewportHorizontalPadding || viewportX + smallViewportWidth > surfaceViewWidth - smallViewportHorizontalPadding || surfaceViewHeight - viewportY - smallViewportHeight < smallViewportTopPadding || viewportY < smallViewportBottomPadding
            ) {
                return
            }
            smallViewportX = viewportX
            smallViewportY = viewportY
        } else if (action == MotionEvent.ACTION_DOWN) {
            touchX = x
            touchY = y
        } else if (action == MotionEvent.ACTION_UP) {
            val alignLeft = smallViewportX < surfaceViewWidth / 2
            smallViewportX = if (alignLeft) smallViewportHorizontalPadding else surfaceViewWidth - smallViewportHorizontalPadding - smallViewportWidth
            touchX = 0
            touchY = 0
        }
    }

    /**
     * 重新开启相机
     */
    override fun reopenCamera() {
        fUCamera.openCamera(cameraConfig, originalTextId, getFUCameraListener())
    }

    /**
     * 关闭相机
     */
    override fun closeCamera() {
        fUCamera.closeCamera()
    }

    /**
     * 切换相机
     */
    override fun switchCamera() {
        fUCamera.switchCamera()
    }

    /**
     * 内置陀螺仪
     */
    private val mSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                if (abs(x) > 3 || abs(y) > 3) {
                    deviceOrientation = if (abs(x) > abs(y)) {
                        if (x > 0) 0 else 180
                    } else {
                        if (y > 0) 90 else 270
                    }

                }
            }
        }
    }

//endregion

//region 图像缓存

    private var mCacheBitmap: Bitmap? = null //图片资源
    private var mCacheBitmapTexId = 0
    private var mCacheBitmapMvpMatrix = TEXTURE_MATRIX.copyOf()

    private val mOnPhotoRecordingListener by lazy {
        OnPhotoRecordingListener {
            mCacheBitmap = it
        }
    }

    private val mPhotoRecordHelper = PhotoRecordHelper(mOnPhotoRecordingListener)


    private fun cacheLastBitmap() {
        if (currentFURenderOutputData != null && currentFURenderOutputData!!.texture != null) {
            mPhotoRecordHelper.sendRecordingData(faceUnity2DTexId, currentFUTexMatrix, TEXTURE_MATRIX, currentFURenderOutputData!!.texture!!.width, currentFURenderOutputData!!.texture!!.height)
        }
    }

    private fun drawCacheBitmap() {
        mCacheBitmap?.let {
            deleteCacheBitmapTexId()
            mCacheBitmapTexId = GlUtil.createImageTexture(it)
            mCacheBitmapMvpMatrix =
                GlUtil.changeMvpMatrixCrop(surfaceViewWidth.toFloat(), surfaceViewHeight.toFloat(), it.width.toFloat(), it.height.toFloat())
            Matrix.scaleM(mCacheBitmapMvpMatrix, 0, 1f, -1f, 1f)
            if (mCacheBitmapTexId > 0) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                programTexture2d?.drawFrame(mCacheBitmapTexId, TEXTURE_MATRIX, mCacheBitmapMvpMatrix)
            }
        }
    }

    /**
     * 移除图片纹理
     */
    private fun deleteCacheBitmapTexId() {
        if (mCacheBitmapTexId > 0) {
            GlUtil.deleteTextures(intArrayOf(mCacheBitmapTexId))
            mCacheBitmapTexId = 0
        }
    }

//endregion
}