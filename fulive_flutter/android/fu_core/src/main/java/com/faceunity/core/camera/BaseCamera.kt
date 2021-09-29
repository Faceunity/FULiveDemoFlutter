package com.faceunity.core.camera

import android.graphics.SurfaceTexture
import com.faceunity.core.enumeration.CameraFacingEnum


/**
 *
 * DESC：Camera抽象类
 * Created on 2020/12/11
 *
 */
abstract class BaseCamera {

    companion object {
        const val TAG = "KIT_BaseCamera"
        const val FRONT_CAMERA_ORIENTATION = 270
        const val BACK_CAMERA_ORIENTATION = 90
        const val PREVIEW_BUFFER_SIZE = 3
    }


    /**相机参数**/
    internal var mIsHighestRate = false
    protected var mFrontCameraId = 0
    protected var mBackCameraId = 0
    protected var mIsPreviewing = false
    protected var mIsStopPreview = false
    internal var mCameraFacing = CameraFacingEnum.CAMERA_FRONT
    internal var mCameraWidth: Int = 1280
    internal var mCameraHeight: Int = 720
    protected var mCameraOrientation = FRONT_CAMERA_ORIENTATION
    protected var mBackCameraOrientation = BACK_CAMERA_ORIENTATION
    protected var mFrontCameraOrientation = FRONT_CAMERA_ORIENTATION


    /**视图 **/
    var mCameraTexId = 100
    var mSurfaceTexture: SurfaceTexture? = null

    /*初始化相机*/
    internal abstract fun initCameraInfo()

    /*打开相机*/
    abstract fun openCamera()

    /*开启预览*/
    abstract fun startPreview()//

    /*对焦*/
    internal abstract fun handleFocus(viewWidth: Int, viewHeight: Int, rawX: Float, rawY: Float, areaSize: Int)

    /*获取亮度*/
    internal abstract fun getExposureCompensation(): Float//获取亮度

    /*设置亮度*/
    internal abstract fun setExposureCompensation(value: Float)//设置亮度

    /*分辨率变更处理*/
    internal abstract fun changeResolution(cameraWidth: Int, cameraHeight: Int)

    /*关闭相机*/
    internal abstract fun closeCamera()


    /**
     * 切换相机
     */
    fun switchCamera() {
        mIsStopPreview = true
        mCameraFacing = if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) CameraFacingEnum.CAMERA_BACK else CameraFacingEnum.CAMERA_FRONT
        mCameraOrientation = if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraOrientation else mBackCameraOrientation
        closeCamera()
        openCamera()
        mIsStopPreview = false

    }


}