package com.faceunity.core.camera

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import com.faceunity.core.entity.FUCameraConfig
import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.enumeration.CameraTypeEnum
import com.faceunity.core.infe.IFaceUnityCamera
import com.faceunity.core.listener.OnFUCameraListener
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：相机封装类
 * Created on 2020/12/14
 *
 */
class FUCamera private constructor() : IFaceUnityCamera {
    companion object {
        const val TAG = "KIT_FaceUnityCamera"

        @Volatile
        private var INSTANCE: FUCamera? = null

        @JvmStatic
        fun getInstance(): FUCamera {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = FUCamera()
                    }
                }
            }
            return INSTANCE!!
        }

    }

    /*相机类型、前后置、帧率、FURenderer配置*/
    private var mFUCameraConfig: FUCameraConfig? = null

    /*相机配置*/
    private var mOnCameraListener: OnFUCameraListener? = null


    /* 相机对象 */
    private var mFaceUnityCamera: BaseCamera? = null

    /*  当前桢数据 */
    private var currentPreviewData: FUCameraPreviewData? = null

    @Volatile
    private var isCameraOpen = false

    @Volatile
    private var isSwitchCamera = false

    /**
     * 线程相关
     */
    /*帧数*/
    private var mFPSNumber = 0


    /**
     * 开启相机
     * @param config FUCameraConfig
     * @param onCameraListener OnFUCameraListener?
     */
    override fun openCamera(config: FUCameraConfig, texId: Int, onCameraListener: OnFUCameraListener?) {
        startBackgroundThread()
        mBackgroundHandler?.post {
            FULogger.i(TAG, "openCamera")
            isNeedFPSLoop = true
            mFUCameraConfig = config
            mOnCameraListener = onCameraListener
            if (isCameraOpen) {
                mFaceUnityCamera?.closeCamera()
            }
            mFaceUnityCamera = initFUCamera(config, texId)
            mFaceUnityCamera?.openCamera()
            isCameraOpen = true
        }

    }

    /**
     * 关闭相机
     */
    override fun closeCamera() {
        mBackgroundHandler?.post {
            FULogger.i(TAG, "closeCamera")
            stopFPSLooper()
            mFUCameraConfig = null
            mOnCameraListener = null
            currentPreviewData = null
            if (isCameraOpen) {
                mFaceUnityCamera?.closeCamera()
                mFaceUnityCamera = null
                isCameraOpen = false

            }
        }

    }


    /**
     * 前后摄像头切换
     */
    override fun switchCamera() {
        if (isSwitchCamera) {
            FULogger.e(TAG, "switchCamera so frequently")
            return
        }
        isSwitchCamera = true
        mBackgroundHandler?.post {
            FULogger.i(TAG, "switchCamera")
            mFaceUnityCamera?.switchCamera()
            isCameraOpen = true
            isSwitchCamera = false
        }

    }


    /**
     * 释放相机资源
     */
    override fun releaseCamera() {
        FULogger.i(TAG, "releaseCamera")
        stopBackgroundThread()
    }


    /**
     * 初始化相机参数
     * @param config FUCameraConfig
     * @return BaseCamera
     */
    private fun initFUCamera(config: FUCameraConfig, texId: Int): BaseCamera {
        val camera = if (config.cameraType == CameraTypeEnum.CAMERA1) {
            FUCamera1(mCameraListener)
        } else {
            FUCamera2(mCameraListener)
        }
        mFPSNumber = config.cameraFPS
        camera.mCameraTexId = texId
        camera.mCameraFacing = config.cameraFacing
        camera.mCameraHeight = config.cameraHeight
        camera.mCameraWidth = config.cameraWidth
        camera.mIsHighestRate = config.isHighestRate
        camera.initCameraInfo()
        return camera
    }


    // region 后台线程模块

    /*后台线程*/
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundHandlerThread: HandlerThread? = null

    /**
     * 开启后台任务线程
     */
    private fun startBackgroundThread() {
        if (mBackgroundHandler == null) {
            mBackgroundHandlerThread = HandlerThread("$TAG-CAMERA", Process.THREAD_PRIORITY_BACKGROUND)
            mBackgroundHandlerThread!!.start()
            mBackgroundHandler = Handler(mBackgroundHandlerThread!!.looper)
        }
    }

    /**
     * 关闭后台任务线程
     */
    private fun stopBackgroundThread() {
        mBackgroundHandlerThread?.quitSafely()
        mBackgroundHandlerThread = null
        mBackgroundHandler = null
    }

    //endregion 后台线程模块


    //region 对外接口

    /**
     *  获取相机朝向
     * @return Int
     */
    override fun getCameraFacing() = currentPreviewData?.cameraFacing

    /**
     *  获取相机宽度
     * @return Int
     */
    override fun getCameraWidth() = currentPreviewData?.width ?: 0

    /**
     * 获取相机高度
     * @return Int
     */
    override fun getCameraHeight() = currentPreviewData?.height ?: 0


    /**
     * 获取相机最新帧数据
     * @return ByteArray?
     */
    override fun getCameraByte() = currentPreviewData

    /**
     * 获取相机绑定的SurfaceTexture
     * @return SurfaceTexture?
     */
    override fun getSurfaceTexture(): SurfaceTexture? {
        return mFaceUnityCamera?.mSurfaceTexture
    }


    /**
     * 设置分辨率
     * @param width Int
     * @param height Int
     */
    override fun changeResolution(width: Int, height: Int) {
        FULogger.i(TAG, "changeResolution  width:$width   height:$height")
        mBackgroundHandler?.post {
            mFaceUnityCamera?.mCameraWidth = width
            mFaceUnityCamera?.mCameraHeight = height
            mFaceUnityCamera?.changeResolution(width, height)
        }
    }


    /**
     * 对焦
     * @param rawX Float
     * @param rawY Float
     * @param areaSize Int
     */
    override fun handleFocus(viewWidth: Int, viewHeight: Int, rawX: Float, rawY: Float, areaSize: Int) {
        FULogger.i(TAG, "handleFocus   viewWidth:$viewWidth   viewHeight:$viewHeight   rawX:$rawX  rawY:$rawY  areaSize:$areaSize")
        mBackgroundHandler?.post {
            mFaceUnityCamera?.handleFocus(viewWidth, viewHeight, rawX, rawY, areaSize)
        }
    }


    /**
     * 获取当前光照补偿值
     */
    override fun getExposureCompensation(): Float {
        FULogger.i(TAG, "getExposureCompensation")
        return mFaceUnityCamera?.getExposureCompensation() ?: 0f
    }

    /**
     * 设置光照补偿
     * @param value Float
     */
    override fun setExposureCompensation(value: Float) {
        FULogger.i(TAG, "setExposureCompensation  value:$value")
        mBackgroundHandler?.post {
            mFaceUnityCamera?.setExposureCompensation(value)
        }
    }

    //endregion

    //region 循环looper

    private val mFPSThreadLock = Any()

    private var mFPSThread: Thread? = null
    private var isFPSLoop = false //循环标识
    private var isNeedFPSLoop = false

    /**
     * 开启循环
     */
    private fun startFPSLooper() {
        FULogger.i(TAG, "startFPSLooper")
        synchronized(mFPSThreadLock) {
            isFPSLoop = true
            if (mFPSThread == null) {
                mFPSThread = Thread { doSendPreviewFrame(mFPSNumber) }
                mFPSThread!!.start()
            }
        }
    }

    /**
     * 关闭循环
     */
    private fun stopFPSLooper() {
        FULogger.i(TAG, "stopFPSLooper")
        synchronized(mFPSThreadLock) {
            isFPSLoop = false
            mFPSThread?.interrupt()
            mFPSThread = null
        }

    }

    private fun doSendPreviewFrame(fps: Int) {
        var first = true
        var startWhen: Long = 0
        val timeStamp = 1000 / 10.coerceAtLeast(100.coerceAtMost(fps)).toLong()
        while (true) {
            if (!isFPSLoop) {
                break
            }
            if (first) {
                first = false
            } else {
                try {
                    val sleepTime: Long = timeStamp - (System.currentTimeMillis() - startWhen)
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime)
                    }
                } catch (e: InterruptedException) {
                    // ignored
                }
            }
            startWhen = System.currentTimeMillis()
            if (currentPreviewData != null && isFPSLoop) {
                FULogger.t(TAG, "onPreviewFrame")
                mOnCameraListener?.onPreviewFrame(currentPreviewData!!)
            }
        }
    }


    //endregion

//region 数据回调

    /**
     * Camera 数据回传处理
     * 确认是否需要FURenderer处理
     * 确认是否按固定帧率推流
     */
    private val mCameraListener = object : OnFUCameraListener {
        override fun onPreviewFrame(previewData: FUCameraPreviewData) {

            if (!isCameraOpen) {
                isCameraOpen = true
            }
            currentPreviewData = previewData
            if (mFPSNumber <= 0) {
                FULogger.t(TAG, "onPreviewFrame")
                mOnCameraListener?.onPreviewFrame(previewData)
            } else if (!isFPSLoop && isNeedFPSLoop) {
                startFPSLooper()
            }
        }
    }
    //endregion


}