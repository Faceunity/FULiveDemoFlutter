package com.faceunity.fuliveplugin.fulive_plugin.modules

import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.render.GLSurfaceViewPlatformViewFactory
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 *
 * @author benyq
 * @date 12/19/2023
 *
 */
class RenderPlugin(private val methodChannel: MethodChannel): BaseModulePlugin {

    private lateinit var glSurfaceViewPlatformViewFactory: GLSurfaceViewPlatformViewFactory
    private val mainScope = MainScope()

    override fun tag() = "RenderPlugin"

    private val methods =
        mapOf(
            "startCamera" to ::startCamera,
            "stopCamera" to ::stopCamera,
            "switchCamera" to ::switchCamera,
            "switchCapturePreset" to ::switchCapturePreset,
            "switchRenderInputType" to ::switchRenderInputType,
            "setCameraExposure" to ::setCameraExposure,
            "manualFocus" to ::manualFocus,
            "setRenderState" to ::setRenderState,
            "startImageRender" to ::startImageRender,
            "stopImageRender" to ::stopImageRender,
            "disposeImageRender" to ::disposeImageRender,
            "startPlayingVideo" to ::startPlayingVideo,
            "stopPlayingVideo" to ::stopPlayingVideo,
            "startPreviewingVideo" to ::startPreviewingVideo,
            "stopPreviewingVideo" to ::stopPreviewingVideo,
            "disposeVideoRender" to ::disposeVideoRender,
            //拍照和录制
            "takePhoto" to ::takePhoto,
            "startRecord" to ::startRecord,
            "stopRecord" to ::stopRecord,
            "captureImage" to ::captureImage,
            "startExportingVideo" to ::startExportingVideo,
            "stopExportingVideo" to ::stopExportingVideo,
        )
    override fun methods(): Map<String, (Map<String, Any>, MethodChannel.Result) -> Any> = methods


    fun init(viewFactory: GLSurfaceViewPlatformViewFactory) {
        this.glSurfaceViewPlatformViewFactory = viewFactory
    }

    fun dispose() {
        mainScope.cancel()
    }
    
    private fun startCamera(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.startCamera()
    }
    private fun stopCamera(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.stopCamera()
    }

    private fun switchCamera(params: Map<String, Any>, result: MethodChannel.Result) {
        val isFront = params.getBoolean("isFront") ?: return
        glSurfaceViewPlatformViewFactory.switchCamera(isFront)
    }

    private fun switchCapturePreset(params: Map<String, Any>, result: MethodChannel.Result) {
        val preset = params.getInt("preset") ?: return
        glSurfaceViewPlatformViewFactory.switchCapturePreset(preset)
        result.success(true)
    }


    private fun switchRenderInputType(params: Map<String, Any>, result: MethodChannel.Result) {
        val inputType = params.getInt("inputType") ?: return
        glSurfaceViewPlatformViewFactory.switchRenderInputType(inputType)
    }
    private fun setCameraExposure(params: Map<String, Any>, result: MethodChannel.Result) {
        val exposure = params.getDouble("exposure") ?: return
        glSurfaceViewPlatformViewFactory.setCameraExposure(exposure)
    }

    private fun manualFocus(params: Map<String, Any>, result: MethodChannel.Result) {
        val dx = params.getDouble("dx") ?: return
        val dy = params.getDouble("dy") ?: return
        val focusRectSize = params.getInt("focusRectSize") ?: return
        glSurfaceViewPlatformViewFactory.manualFocus(dx, dy, focusRectSize)
    }

    private fun setRenderState(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.setRenderState(params.getBoolean("isRendering")?: return)
    }

    private fun startImageRender(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.startImageRender()
    }

    private fun stopImageRender(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.stopImageRender()
    }

    private fun disposeImageRender(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.disposeImageRender()
    }

    private fun startPreviewingVideo(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.startPreviewingVideo()
    }

    private fun stopPreviewingVideo(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.stopPreviewingVideo()
    }

    private fun startPlayingVideo(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.startPlayingVideo()
    }

    private fun stopPlayingVideo(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.stopPlayingVideo()
    }

    private fun disposeVideoRender(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.disposeVideoRender()
    }

    private fun takePhoto(params: Map<String, Any>, result: MethodChannel.Result) {
        mainScope.launch {
            val success = suspendCancellableCoroutine { continuation ->
                glSurfaceViewPlatformViewFactory.takePhoto {
                    continuation.resume(it)
                }
            }
            methodChannel.invokeMethod("takePhotoResult", success)
        }
    }

    private fun startRecord(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.startRecord()
    }

    private fun stopRecord(params: Map<String, Any>, result: MethodChannel.Result) {
        mainScope.launch {
            val success = suspendCancellableCoroutine { continuation ->
                glSurfaceViewPlatformViewFactory.stopRecord {
                    continuation.resume(it)
                }
            }
            result.success(success)
        }
    }

    private fun captureImage(params: Map<String, Any>, result: MethodChannel.Result) {
        mainScope.launch {
            val success = suspendCancellableCoroutine { continuation ->
                glSurfaceViewPlatformViewFactory.captureImage {
                    continuation.resume(it)
                }
            }
            methodChannel.invokeMethod("captureImageResult", success)
        }
    }

    private fun startExportingVideo(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.startExportingVideo()
    }

    private fun stopExportingVideo(params: Map<String, Any>, result: MethodChannel.Result) {
        glSurfaceViewPlatformViewFactory.stopExportingVideo()
    }
}