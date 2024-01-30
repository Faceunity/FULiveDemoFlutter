package com.faceunity.fuliveplugin.fulive_plugin.render

import android.content.Context
import android.view.View
import com.faceunity.core.renderer.PhotoRenderer
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit

/**
 *
 * @author benyq
 * @date 11/29/2023
 *
 */
class GLImagePlatformView(
    context: Context,
    private val photoPath: String,
    private val callback: () -> Unit,
) : BasePlatformView(context) {

    private val renderer: PhotoRenderer = PhotoRenderer(glSurfaceView, photoPath, this)
    override fun provideRender() = renderer
    override fun notifyFlutterRenderInfo() {
        val data = mapOf("faceTracked" to (trackedFaceNumber > 0))
        mRenderFrameListener?.notifyFlutter(data)
    }

    override fun getView(): View {
        return glSurfaceView
    }


    override fun dispose() {
        super.dispose()
        renderer.onDestroy()
        callback.invoke()
    }

    fun onResume() {
        FULogger.d(tag(), "onResume: ")
        renderer.onResume()
    }

    fun onPause() {
        FULogger.d(tag(), "onPause: ")

        FaceunityKit.storeFaceUnityConfig()
        renderer.onPause()
    }

    fun onDestroy() {
        FULogger.d(tag(), "onDestroy: ")
        renderer.onDestroy()
    }
}