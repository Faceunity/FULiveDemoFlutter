package com.faceunity.fuliveplugin.fulive_plugin.render

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.faceunity.core.entity.FUCameraConfig
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.renderer.CameraRenderer
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.render.renderer.CameraRenderer2

/**
 *
 * @author benyq
 * @date 11/29/2023
 *
 */
class GLCameraPlatformView(context: Context, private val callback: ()-> Unit): BasePlatformView(context) {

    private val cameraRenderer: CameraRenderer2 = CameraRenderer2(context, glSurfaceView, FUCameraConfig(), this)
    private var isFrontCamera = true
    private var cameraRenderType = 0 // 0 单输入 1 双输入
    private var cameraWidth = 0
    private var cameraHeight = 0

    private var isWaitingCameraFrame = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private val reopenCameraAction = Runnable {
        cameraRenderer.reopenCamera()
        FULogger.d(tag(), "reopenCameraAction call")
    }

    override fun getView(): View {
        return glSurfaceView
    }

    override fun dispose() {
        super.dispose()
        cameraRenderer.onDestroy()
        callback.invoke()
    }

    override fun onFlutterViewAttached(flutterView: View) {
        super.onFlutterViewAttached(flutterView)
        cameraRenderer.onResume()
    }

    fun startCamera(){
        FULogger.d(tag(), "startCamera: ")
        cameraRenderer.onResume()
        FaceunityKit.restoreFaceUnityConfig()

        isWaitingCameraFrame = true
        mainHandler.postDelayed(reopenCameraAction, 2000)
    }

    fun stopCamera(){
        FULogger.d(tag(), "onPause: ")
        FaceunityKit.storeFaceUnityConfig()
        cameraRenderer.onPause()
    }

    fun switchCamera(isFront: Boolean): Boolean {
        cameraRenderer.switchCamera()
        isFrontCamera = isFront
        return true
    }

    fun switchRenderInputType(inputType: Int) {
        cameraRenderType = inputType
    }

    fun switchCapturePreset(preset: Int) {
        when(preset) {
            0 -> cameraRenderer.fUCamera.changeResolution(640, 480)
            1 -> cameraRenderer.fUCamera.changeResolution(1280, 720)
            2 -> cameraRenderer.fUCamera.changeResolution(1920, 1080)
        }
    }

    fun setCameraExposure(exposure: Double) {
        cameraRenderer.fUCamera.setExposureCompensation(exposure.toFloat())
    }
    fun manualFocus(dx: Double, dy: Double, focusRectSize: Int) {
        cameraRenderer.fUCamera.handleFocus(surfaceWidth, surfaceHeight, dx.toFloat(), dy.toFloat(), focusRectSize)
    }

    fun takePhoto(action: (Boolean) -> Unit) {
        captureImage(action)
    }

    fun startRecord(){
        if (!isRecording) {
            isRecording = true
            mVideoRecordHelper.startRecording(
                glSurfaceView,
                cameraRenderer.fUCamera.getCameraHeight(),
                cameraRenderer.fUCamera.getCameraWidth()
            )
        }
    }

    fun stopRecord(action: (Boolean) -> Unit){
        if (isRecording) {
            isRecording = false
            recordVideoActions.add(action)
            mVideoRecordHelper.stopRecording()
        }else {
            action(false)
        }
    }

    override fun provideRender() = cameraRenderer

    override fun notifyFlutterRenderInfo() {
        val debug = "resolution:\n${cameraHeight}x${cameraWidth}\nfps:${calculatedFPS}\nrender time:\n${calculatedRenderTime}ms"
        val data = mapOf("debugInfo" to debug, "faceTracked" to (trackedFaceNumber > 0))
        mRenderFrameListener?.notifyFlutter(data)
    }

    override fun onSurfaceCreated() {
        super.onSurfaceCreated()
    }

    override fun onRenderBefore(inputData: FURenderInputData?) {
        super.onRenderBefore(inputData)
        if (isWaitingCameraFrame) {
            mainHandler.removeCallbacks(reopenCameraAction)
            isWaitingCameraFrame = false
        }
        cameraWidth = inputData?.width ?: 0
        cameraHeight = inputData?.height ?: 0
        if (mFURenderKit.makeup == null) {
            if (cameraRenderType == 0) {
                inputData?.imageBuffer = null
            }
        }else {
            //美妆模块，设置为单纹理输入。
            inputData?.imageBuffer = null
            inputData?.renderConfig?.isNeedBufferReturn = false
        }
    }
}