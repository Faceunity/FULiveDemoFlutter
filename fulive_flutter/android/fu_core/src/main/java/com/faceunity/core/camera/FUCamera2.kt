package com.faceunity.core.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.*
import android.hardware.camera2.params.MeteringRectangle
import android.media.ImageReader
import android.util.Size
import android.view.Surface
import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.listener.OnFUCameraListener
import com.faceunity.core.utils.CameraUtils
import com.faceunity.core.utils.FULogger

/**
 *
 * DESC：Camera2实现类
 * Created on 2020/12/11
 *
 */
@Suppress("DEPRECATION")
class FUCamera2(private val cameraListener: OnFUCameraListener) : BaseCamera() {

    private lateinit var mCameraManager: CameraManager

    var mFrontCameraCharacteristics: CameraCharacteristics? = null
    var mBackCameraCharacteristics: CameraCharacteristics? = null
    var mCaptureRequestBuilder: CaptureRequest.Builder? = null


    /**
     * 相机中间交互实体
     */
    var mCameraDevice: CameraDevice? = null
    var mCameraCaptureSession: CameraCaptureSession? = null
    private var mImageReader: ImageReader? = null
    var mYuvDataBufferArray: Array<ByteArray>? = null
    var mYuvDataBufferPosition = 0


    //region 相机基本流程
    /**
     * 初始化相机参数
     * mFrontCameraId mFrontCameraCharacteristics mFrontCameraOrientation
     * mBackCameraId  mBackCameraCharacteristics mBackCameraOrientation
     * mCameraOrientation
     */
    override fun initCameraInfo() {
        mCameraManager = FURenderManager.mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mFrontCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        mBackCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
        val ids = mCameraManager.cameraIdList
        if (ids.isEmpty()) {
            FULogger.e(TAG, "No camera")
            return
        }
        ids.forEach {
            if (it == mFrontCameraId.toString()) {
                mFrontCameraCharacteristics = mCameraManager.getCameraCharacteristics(it)
                mFrontCameraOrientation =
                    mFrontCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: FRONT_CAMERA_ORIENTATION
            } else if (it == mBackCameraId.toString()) {
                mBackCameraCharacteristics = mCameraManager.getCameraCharacteristics(it)
                mBackCameraOrientation =
                    mBackCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: BACK_CAMERA_ORIENTATION
            }
        }
        mCameraOrientation = if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraOrientation else mBackCameraOrientation
    }

    /**
     * 开启相机
     * 初始化 mCameraWidth mCameraHeight
     * 开启数据回调 mOnImageAvailableListener
     * 开启状态回调 mStateCallback
     */
    @SuppressLint("MissingPermission")
    override fun openCamera() {
        if (mCameraDevice != null) {
            return
        }
        try {
            val cameraId = if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraId else mBackCameraId
            val cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId.toString())
            val streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            streamConfigurationMap?.let {
                val outputSizes = it.getOutputSizes(SurfaceTexture::class.java)
                val size =
                    CameraUtils.chooseOptimalSize(
                        outputSizes,
                        mCameraWidth,
                        mCameraHeight,
                        1920,
                        1080,
                        Size(mCameraWidth, mCameraHeight)
                    )
                mCameraWidth = size.width
                mCameraHeight = size.height
            }
            mYuvDataBufferArray = Array(PREVIEW_BUFFER_SIZE) {
                ByteArray(mCameraWidth * mCameraHeight * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8)
            }
            mImageReader = ImageReader.newInstance(mCameraWidth, mCameraHeight, ImageFormat.YUV_420_888, PREVIEW_BUFFER_SIZE)
            mImageReader!!.setOnImageAvailableListener(mOnImageAvailableListener, null)
            mCameraManager.openCamera(cameraId.toString(), mStateCallback, null)
        } catch (e: CameraAccessException) {
            mCameraDevice = null
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    /**
     * 开启预览
     * 初始化 mSurfaceTexture
     * 配置默认fps帧率
     */
    override fun startPreview() {
        if (mCameraTexId == 0 || mCameraDevice == null || mIsPreviewing) {
            return
        }
        mSurfaceTexture = SurfaceTexture(mCameraTexId)
        mSurfaceTexture!!.setDefaultBufferSize(mCameraWidth, mCameraHeight)
        try {
            val rangeFps = CameraUtils.getBestRange(
                FURenderManager.mContext,
                if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraId.toString() else mBackCameraId.toString(), mIsHighestRate
            )
            val captureRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            rangeFps?.let { captureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, it) }
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false)
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO)
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE)
            val previewSurface = Surface(mSurfaceTexture)
            captureRequestBuilder.addTarget(previewSurface)
            val imageReaderSurface = mImageReader!!.surface
            captureRequestBuilder.addTarget(imageReaderSurface)
            mCaptureRequestBuilder = captureRequestBuilder
            mCameraDevice!!.createCaptureSession(
                arrayListOf(imageReaderSurface, previewSurface), mCameraCaptureSessionStateCallback, null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun closeCamera() {
        mIsPreviewing = false
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession!!.close()
            mCameraCaptureSession = null
        }
        if (mCameraDevice != null) {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
        if (mImageReader != null) {
            mImageReader!!.close()
            mImageReader = null
        }
        mSurfaceTexture?.release()
        mSurfaceTexture = null
    }


    /**
     * 数据Buffer回调
     */
    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        try {
            val image = reader?.acquireLatestImage() ?: return@OnImageAvailableListener
            var mYuvDataBuffer: ByteArray? = null
            if (!mIsStopPreview) {
                mYuvDataBuffer = mYuvDataBufferArray!![mYuvDataBufferPosition]
                mYuvDataBufferPosition = ++mYuvDataBufferPosition % mYuvDataBufferArray!!.size
                CameraUtils.YUV420ToNV21(image, mYuvDataBuffer)
            }
            image.close()
            mYuvDataBuffer?.let {
                cameraListener.onPreviewFrame(FUCameraPreviewData(it, mCameraFacing, mCameraOrientation, mCameraWidth, mCameraHeight))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 相机状态回调
     */
    private val mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            logCameraParameters()
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            mCameraDevice = null
        }
    }

    /**
     * 相机通信session回调
     */
    private val mCameraCaptureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            mIsPreviewing = false
        }

        override fun onConfigured(session: CameraCaptureSession) {
            mIsPreviewing = true
            mCameraCaptureSession = session
            try {
                session.setRepeatingRequest(mCaptureRequestBuilder!!.build(), mCaptureCallback, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 相机RepeatingRequest回调
     */
    val mCaptureCallback: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureSequenceCompleted(session: CameraCaptureSession, sequenceId: Int, frameNumber: Long) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, null)
        }
    }


//endregion


//region 业务功能

    /**
     * 对焦
     * @param rawX Float
     * @param rawY Float
     * @param areaSize Int
     */
    override fun handleFocus(viewWidth: Int, viewHeight: Int, rawX: Float, rawY: Float, areaSize: Int) {
        if (mCameraCaptureSession == null) {
            return
        }
        if (!isMeteringAreaAFSupported()) {
            FULogger.e(TAG, "handleFocus not supported")
            return
        }
        val cameraCharacteristics: CameraCharacteristics =
            if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraCharacteristics!! else mBackCameraCharacteristics!!


        val sensorArraySize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        //here i just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
        var y = (rawX / viewWidth * sensorArraySize!!.height().toFloat()).toInt()
        val x = (rawY / viewHeight * sensorArraySize.width().toFloat()).toInt()
        if (mCameraOrientation == BACK_CAMERA_ORIENTATION) {
            y = sensorArraySize.height() - y
        }
        val halfTouchWidth = areaSize / 2
        val halfTouchHeight = areaSize / 2
        val focusAreaTouch = MeteringRectangle(
            (x - halfTouchWidth).coerceAtLeast(0), (y - halfTouchHeight).coerceAtLeast(0),
            halfTouchWidth * 2, halfTouchHeight * 2, MeteringRectangle.METERING_WEIGHT_MAX - 1
        )
        try {
            mCameraCaptureSession!!.stopRepeating()
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF)
            val meteringRectangles = arrayOf(focusAreaTouch)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_REGIONS, meteringRectangles)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)
            mCameraCaptureSession!!.setRepeatingRequest(mCaptureRequestBuilder!!.build(), mCaptureCallback, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取亮度
     * @return Float
     */
    override fun getExposureCompensation(): Float {
        val cameraCharacteristics: CameraCharacteristics =
            if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraCharacteristics!! else mBackCameraCharacteristics!!
        val range = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)
        var min = -1
        var max = 1
        if (range != null) {
            min = range.lower
            max = range.upper
        }
        val progress = mCaptureRequestBuilder?.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION) ?: 0
        return (progress - min).toFloat() / (max - min)
    }

    /**
     * 设置亮度
     */
    override fun setExposureCompensation(value: Float) {
        if (mCameraCaptureSession == null) {
            return
        }
        val cameraCharacteristics: CameraCharacteristics =
            if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraCharacteristics!! else mBackCameraCharacteristics!!
        val range = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)
        if (range != null) {
            val min = range.lower
            val max = range.upper
            val `val` = (value * (max - min) + min).toInt()
            mCaptureRequestBuilder!!.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, `val`)
            try {
                mCameraCaptureSession!!.setRepeatingRequest(mCaptureRequestBuilder!!.build(), mCaptureCallback, null)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 调整分辨率
     * @param cameraWidth Int
     * @param cameraHeight Int
     */
    override fun changeResolution(cameraWidth: Int, cameraHeight: Int) {
        mIsStopPreview = true
        mYuvDataBufferArray = null
        closeCamera()
        openCamera()
        startPreview()
        mIsStopPreview = false
    }

    /**
     * 自动对焦(AF)例程可使用的最大计量区域数。
     * @return Boolean
     */
    private fun isMeteringAreaAFSupported(): Boolean {
        val masRegionsAF: Int? =
            (if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraCharacteristics!! else mBackCameraCharacteristics!!).get(
                CameraCharacteristics.CONTROL_MAX_REGIONS_AF
            )
        return if (masRegionsAF != null) {
            masRegionsAF >= 1
        } else {
            false
        }
    }

//endregion


    private fun logCameraParameters() {
        if (CameraUtils.DEBUG) {
            val cameraCharacteristics: CameraCharacteristics =
                if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraCharacteristics!! else mBackCameraCharacteristics!!
            val keys = cameraCharacteristics.keys
            keys.forEach {
                val res = cameraCharacteristics.get(it)
                if (res is Array<*>) {
                    FULogger.d(TAG, "FUCamera2 parameters   $it:${res.contentToString()}")
                } else {
                    FULogger.d(TAG, "FUCamera2 parameters   $it:$res")
                }
            }

        }
    }


}