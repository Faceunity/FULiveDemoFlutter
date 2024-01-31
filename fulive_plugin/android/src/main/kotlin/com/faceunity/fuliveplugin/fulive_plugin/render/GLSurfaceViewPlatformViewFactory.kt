package com.faceunity.fuliveplugin.fulive_plugin.render

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.utils.ifTrue
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

/**
 *
 * @author benyq
 * @date 11/29/2023
 *
 */
class GLSurfaceViewPlatformViewFactory: PlatformViewFactory(StandardMessageCodec.INSTANCE), DefaultLifecycleObserver {

    companion object {
        private const val TAG = "GLSurfaceViewPlatformViewFactory"
    }

    private var platformViews = mutableMapOf<String, BasePlatformView>()
    private var sortedPlatformViews = mutableListOf<BasePlatformView>()
    private var renderFrameListener: NotifyFlutterListener? = null
    // 缓存图片/视频路径
    private var cacheMediaPath: String? = null
    //是否是因为离开 cameraView页面 而关闭 相机
    private var leaveCameraPage = false

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val identifier = args as? String ?: throw IllegalArgumentException("identifier is null")
        FULogger.d(TAG, "create view: $identifier, viewId: $viewId")
        val platformView = when(RenderType.valueFrom(identifier)) {
            RenderType.CAMERA -> GLCameraPlatformView(context) {
                removePlatformView(identifier)
            }
            RenderType.IMAGE -> {
                val path = cacheMediaPath ?: ""
                cacheMediaPath = null
                GLImagePlatformView(context, path) {
                    removePlatformView(identifier)
                }
            }
            RenderType.VIDEO -> {
                val path = cacheMediaPath ?: ""
                cacheMediaPath = null
                GLVideoPlatformView(context, path) {
                    removePlatformView(identifier)
                }
            }
        }
        platformView.setRenderFrameListener(renderFrameListener)
        platformViews[identifier] = platformView
        sortedPlatformViews.add(platformView)
        return platformView
    }

    private fun removePlatformView(identifier: String) {
        val platformView = platformViews.remove(identifier)
        sortedPlatformViews.remove(platformView)
    }

    fun setRenderFrameListener(listener: NotifyFlutterListener) {
        renderFrameListener = listener
    }

    fun release() {
        platformViews.clear()
    }

    fun startCamera() {
        leaveCameraPage = false
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.startCamera()
    }

    fun stopCamera() {
        leaveCameraPage = true
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.stopCamera()
    }

    fun switchCamera(isFront: Boolean ) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.switchCamera(isFront)
    }

    fun switchRenderInputType(inputType: Int) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.switchRenderInputType(inputType)
    }

    fun switchCapturePreset(preset: Int) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.switchCapturePreset(preset)
    }

    fun setCameraExposure(exposure: Double) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.setCameraExposure(exposure)
    }

    fun manualFocus(dx: Double, dy: Double, focusRectSize: Int) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.manualFocus(dx, dy, focusRectSize)
    }

    fun takePhoto(takePhotoAction: (Boolean) -> Unit) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.takePhoto(takePhotoAction)
    }

    fun startRecord() {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.startRecord()
    }

    fun stopRecord(action: (Boolean) -> Unit) {
        (platformViews[CAMERA_RENDER] as? GLCameraPlatformView)?.stopRecord(action)
    }

    fun setRenderState(isRendering: Boolean) {
        platformViews.forEach {
            it.value.setRenderState(isRendering)
        }
    }
    fun setMediaPath(path: String) {
        cacheMediaPath = path
    }

    fun startImageRender() {
        (platformViews[IMAGE_RENDER] as? GLImagePlatformView)?.onResume()
    }

    fun stopImageRender() {
        (platformViews[IMAGE_RENDER] as? GLImagePlatformView)?.onPause()
    }

    fun captureImage(action: (Boolean) -> Unit) {
        (platformViews[IMAGE_RENDER] as? GLImagePlatformView)?.captureImage(action)
    }

    fun disposeImageRender() {
        (platformViews[IMAGE_RENDER] as? GLImagePlatformView)?.onDestroy()
    }

    fun startPlayingVideo() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.startPlayingVideo()
    }

    fun stopPlayingVideo() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.stopPlayingVideo()
    }

    fun disposeVideoRender() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.disposeVideoRender()
    }

    fun startPreviewingVideo() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.startPreviewingVideo()
    }

    fun stopPreviewingVideo() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.stopPreviewingVideo()
    }

    fun startExportingVideo() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.startExportingVideo()
    }

    fun stopExportingVideo() {
        (platformViews[VIDEO_RENDER] as? GLVideoPlatformView)?.stopExportingVideo()
    }


    // 开始选择图片和照片
    private var isSelectingMedia: Boolean = false
    fun startSelectMedia() {
        isSelectingMedia = true
    }

    fun stopSelectMedia(){
        isSelectingMedia = false
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val currentPlatformView = sortedPlatformViews.lastOrNull()
        FULogger.d(TAG, "Lifecycle onResume: $currentPlatformView")
        when(currentPlatformView) {
            is GLCameraPlatformView -> {
                if (!currentPlatformView.isViewRendering && !isSelectingMedia && !leaveCameraPage) {
                    currentPlatformView.startCamera()
                }
            }
            is GLImagePlatformView -> {
                if (!currentPlatformView.isViewRendering) {
                    currentPlatformView.onResume()
                }
            }
            is GLVideoPlatformView -> {
                if (!currentPlatformView.isViewRendering) {
                    currentPlatformView.onResume()
                }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        val currentPlatformView = sortedPlatformViews.lastOrNull()
        FULogger.d(TAG, "Lifecycle onPause: $currentPlatformView")
        when(currentPlatformView) {
            is GLCameraPlatformView -> {
                if (currentPlatformView.isViewRendering && !isSelectingMedia) {
                    currentPlatformView.stopCamera()
                }
            }
            is GLImagePlatformView -> {
                if (currentPlatformView.isViewRendering) {
                    currentPlatformView.onPause()
                }
            }
            is GLVideoPlatformView -> {
                if (currentPlatformView.isViewRendering) {
                    currentPlatformView.onPause()
                }
            }
        }
    }


}

interface NotifyFlutterListener {
    fun notifyFlutter(data: Map<String, Any>)
}