package com.faceunity.fuliveplugin.fulive_plugin.render

import android.content.Context
import android.view.View
import com.faceunity.core.entity.FURenderFrameData
import com.faceunity.core.entity.FURenderOutputData
import com.faceunity.core.listener.OnVideoPlayListener
import com.faceunity.core.renderer.VideoRenderer
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 *
 * @author benyq
 * @date 11/29/2023
 *
 */
class GLVideoPlatformView(context: Context, private val videoPath: String, private val callback: ()-> Unit): BasePlatformView(context) {

    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var videoExportJob: Job? = null
    private val videoRenderer: VideoRenderer = VideoRenderer(glSurfaceView, videoPath, this, true, true)
    private var mVideoHeight = 0
    private var mVideoWidth = 0

    @Volatile
    private var mIsFinishStop = false
    @Volatile
    private var mIsRecordStop = false
    private var isVideoExited = false
    /**
     * 视频播放回调
     */
    private val mOnVideoPlayListener: OnVideoPlayListener = object : OnVideoPlayListener {
        override fun onError(error: String) {}
        override fun onPlayFinish() {
            notifyPlayFinished()
        }
    }

    /**
     * 视频播放回调
     */
    private val mRenderVideoUnDrawTextureListener: OnVideoPlayListener =
        object : OnVideoPlayListener {
            override fun onError(error: String) {
                isRecording = false
                onStopRecord(false)
                notifyExportVideo(videoExportingResult= false)
            }
            override fun onPlayFinish() {
                FULogger.d(tag(), "onPlayFinish: $isRecording")
                if (isRecording) {
                    isRecording = false
                    onStopRecord(true)
                    notifyExportVideo(videoExportingResult= true)
                }
            }
        }

    override fun provideRender() = videoRenderer

    override fun getView(): View {
        return glSurfaceView
    }

    override fun onFlutterViewAttached(flutterView: View) {
        super.onFlutterViewAttached(flutterView)
        videoRenderer.onResume()
    }

    override fun dispose() {
        super.dispose()
        coroutineScope.cancel()
        videoRenderer.onDestroy()
        callback.invoke()
    }

    fun onResume() {
        FULogger.d(tag(), "onResume")
        videoRenderer.onResume()
    }

    fun onPause() {
        FULogger.d(tag(), "onPause")
        videoRenderer.onPause()
    }

    fun startPlayingVideo() {
        FULogger.d(tag(), "startPlayingVideo")
        videoRenderer.startMediaPlayer(mOnVideoPlayListener)
    }

    fun stopPlayingVideo() {
        FULogger.d(tag(), "stopPlayingVideo")
        videoRenderer.pauseMediaPlayer()
    }
    fun disposeVideoRender() {
        FULogger.d(tag(), "disposeVideoRender: isVideoExited: $isVideoExited")
        if (!isVideoExited) {
            isVideoExited = true
            videoRenderer.onPause()
        }
    }

    fun startPreviewingVideo() {
        FULogger.d(tag(), "startPreviewingVideo: ${Thread.currentThread().name}")
    }

    fun stopPreviewingVideo() {
        FULogger.d(tag(), "stopPreviewingVideo")
    }

    fun startExportingVideo() {
        FULogger.d(tag(), "startExportingVideo")
        mVideoRecordHelper.startRecording(glSurfaceView, mVideoWidth, mVideoHeight, videoPath)
    }
    fun stopExportingVideo() {
        FULogger.d(tag(), "stopExportingVideo")
        onStopRecord(false)
    }

    private fun onStopRecord(isFinishStop: Boolean) {
        if (!mIsRecordStop) {
            FULogger.d(tag(), "onStopRecord: isFinishStop:$isFinishStop")
            this.mIsFinishStop = isFinishStop
            mVideoRecordHelper.stopRecording()
            videoRenderer.pauseMediaPlayer()
            mIsRecordStop = true
        }
    }

    override fun onVideoRecordPrepared() {
        FULogger.d(tag(), "onVideoRecordPrepared")
        videoRenderer.renderVideoUnDrawTexture(mRenderVideoUnDrawTextureListener)
        isRecording = true
        mIsRecordStop = false
        videoExportJob?.cancel()
        val startTime = System.currentTimeMillis()
        val videoDuration = videoRenderer.getDuration()
        videoExportJob = coroutineScope.launch {
            while (isActive) {
                delay(50)
                val exportTime = System.currentTimeMillis() - startTime
                notifyExportVideo(videoExportingProgress = exportTime * 1.0 / videoDuration)
            }
        }
    }

    override fun onVideoRecordFinish(file: File) {
        FULogger.d(tag(), "onVideoRecordFinish")
        isRecording = false
        if (!mIsFinishStop) {
            if (file.exists()) {
                file.delete()
            }
        }
        videoExportJob?.cancel()
        videoExportJob = null
    }

    override fun onRenderAfter(outputData: FURenderOutputData, frameData: FURenderFrameData) {
        outputData.texture?.let {
            mVideoWidth = it.width
            mVideoHeight = it.height
        }
        super.onRenderAfter(outputData, frameData)
    }

    override fun onSurfaceDestroy() {
        FaceunityKit.storeFaceUnityConfig()
        super.onSurfaceDestroy()
    }

    override fun notifyFlutterRenderInfo() {
        val data = mapOf("faceTracked" to (trackedFaceNumber > 0))
        mRenderFrameListener?.notifyFlutter(data)
    }

    private fun notifyPlayFinished() {
        FULogger.d(tag(), "notifyPlayFinished")
        val data = mapOf("videoPlayingFinished" to true)
        mRenderFrameListener?.notifyFlutter(data)
    }

    private fun notifyExportVideo(videoExportingProgress: Double? = null, videoExportingResult: Boolean? = null) {
        FULogger.d(tag(), "notifyExportVideo: videoExportingProgress: $videoExportingProgress, videoExportingResult: $videoExportingResult")
        val data = mutableMapOf<String, Any>()
        videoExportingProgress?.let {
            data["videoExportingProgress"] = it
        }
        videoExportingResult?.let {
            data["videoExportingResult"] = it
        }
        mRenderFrameListener?.notifyFlutter(data)
    }
}