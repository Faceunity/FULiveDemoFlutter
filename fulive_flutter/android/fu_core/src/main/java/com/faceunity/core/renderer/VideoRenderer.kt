package com.faceunity.core.renderer

import android.graphics.SurfaceTexture
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.enumeration.FUExternalInputEnum
import com.faceunity.core.enumeration.FUInputTextureEnum
import com.faceunity.core.enumeration.FUTransformMatrixEnum
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.infe.IVideoRenderer
import com.faceunity.core.listener.OnGlRendererListener
import com.faceunity.core.listener.OnVideoPlayListener
import com.faceunity.core.program.ProgramTextureOES
import com.faceunity.core.utils.DecimalUtils
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.GlUtil
import com.faceunity.core.utils.LimitFpsUtil
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *
 * DESC：
 * Created on 2021/1/4
 *
 */
class VideoRenderer(gLSurfaceView: GLSurfaceView?, private val videoPath: String, glRendererListener: OnGlRendererListener) : BaseFURenderer(gLSurfaceView, glRendererListener), IVideoRenderer {


    //region 初始化

    init {
        currentFURenderInputData.apply {
            currentFURenderInputData.texture = FURenderInputData.FUTexture(FUInputTextureEnum.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, 0)
            renderConfig.apply {
                externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO
                cameraFacing = CameraFacingEnum.CAMERA_BACK
                inputBufferMatrix = FUTransformMatrixEnum.CCROT0
                inputTextureMatrix = FUTransformMatrixEnum.CCROT0
            }
        }
        externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO
        gLSurfaceView?.setEGLContextClientVersion(2)
        gLSurfaceView?.setRenderer(this)
        gLSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

    }


    //endregion

    //region 生命周期调用

    /**Activity onPause**/
    override fun onResume() {
        startPlayerThread()
        if (isActivityPause) {
            gLSurfaceView?.onResume()
        }
        isActivityPause = false
    }

    /**Activity release**/
    override fun onPause() {
        isActivityPause = true
        val count = CountDownLatch(1)
        mPlayerHandler?.removeCallbacksAndMessages(null)
        mPlayerHandler?.post {
            releaseMediaPlayer()
            gLSurfaceView?.queueEvent(Runnable {
                destroyGlSurface()
                count.countDown()
            })
        }

        try {
            count.await(500, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            // ignored
        }
        gLSurfaceView?.onPause()
    }

    /**Activity onDestroy**/
    override fun onDestroy() {
        stopPlayerThread()
        glRendererListener = null
        gLSurfaceView = null
    }


    //endregion 生命周期调用


    //region GLSurfaceView.Renderer 回调

    private var videoOrientation = 0
    /*系统相机录制标识*/

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mProgramTextureOes: ProgramTextureOES? = null


    override fun surfaceCreated(gl: GL10?, config: EGLConfig?) {
        originalTextId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
        currentFURenderInputData.texture?.texId = originalTextId
        mProgramTextureOes = ProgramTextureOES()
        createSurfaceTexture()
        analysisVideo()
        LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS)
    }


    override fun surfaceChanged(gl: GL10?, width: Int, height: Int) {
        originMvpMatrix = if (videoOrientation == 0 || videoOrientation == 180) {
            GlUtil.changeMvpMatrixInside(width.toFloat(), height.toFloat(), originalWidth.toFloat(), originalHeight.toFloat())
        } else {
            GlUtil.changeMvpMatrixInside(width.toFloat(), height.toFloat(), originalHeight.toFloat(), originalWidth.toFloat())
        }
        smallViewMatrix = GlUtil.changeMvpMatrixCrop(90f, 160f, originalHeight.toFloat(), originalWidth.toFloat())
        defaultFUMvpMatrix = originMvpMatrix.copyOf()
        when (videoOrientation) {
            270 -> {
                Matrix.rotateM(defaultFUMvpMatrix, 0, 90f, 0f, 0f, 1f)
            }
            180 -> {
                Matrix.rotateM(defaultFUMvpMatrix, 0, 180f, 0f, 0f, 1f)
            }
            90 -> {
                Matrix.rotateM(defaultFUMvpMatrix, 0, 270f, 0f, 0f, 1f)
            }
        }
    }


    override fun prepareRender(gl: GL10?): Boolean {
        return if (mSurfaceTexture == null || programTexture2d == null) {
            false
        } else {
            mSurfaceTexture!!.updateTexImage()
            mSurfaceTexture!!.getTransformMatrix(originTexMatrix)
            true
        }
    }

    override fun buildFURenderInputData(): FURenderInputData {
        return currentFURenderInputData
    }

    override fun drawRenderFrame(gl: GL10? ) {
        if (faceUnity2DTexId > 0 && renderSwitch) {
            programTexture2d!!.drawFrame(faceUnity2DTexId, currentFUTexMatrix, currentFUMvpMatrix)
        } else if (originalTextId > 0) {
            mProgramTextureOes!!.drawFrame(originalTextId, originTexMatrix, originMvpMatrix)
        }
        if (drawSmallViewport) {
            GLES20.glViewport(smallViewportX, smallViewportY, smallViewportWidth, smallViewportHeight)
            mProgramTextureOes!!.drawFrame(originalTextId, originTexMatrix, smallViewMatrix)
            GLES20.glViewport(0, 0, surfaceViewWidth, surfaceViewHeight)
        }
    }

    private fun createSurfaceTexture() {
        mSurfaceTexture = SurfaceTexture(originalTextId)
        mSurfaceTexture!!.setOnFrameAvailableListener {
            gLSurfaceView?.requestRender()
        }
        mSurface = Surface(mSurfaceTexture)
        mPlayerHandler?.post {
            mSimpleExoPlayer?.setVideoSurface(mSurface)
        }
    }

    private fun analysisVideo() {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(videoPath)
            originalWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
            originalHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
            videoOrientation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).toInt()
            currentFURenderInputData.apply {
                width = originalWidth
                height = originalHeight
                renderConfig.inputOrientation = videoOrientation
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    /**
     * 相关资源回收
     */
    override fun destroyGlSurface() {
        mSurfaceTexture?.let {
            it.release()
            mSurfaceTexture = null
        }
        mSurface?.let {
            it.release()
            mSurface = null
        }
        super.destroyGlSurface()
    }

    //endregion


    //region 视频处理
    /*视频播放器*/
    private var mSimpleExoPlayer: SimpleExoPlayer? = null
    private var mOnVideoPlayListener: OnVideoPlayListener? = null

    /**
     * 初始化播放器
     */
    private fun createMediaPlayer() {
        mSimpleExoPlayer = SimpleExoPlayer.Builder(FURenderManager.mContext).build()
        mSimpleExoPlayer!!.addListener(mMediaEventListener)
        mSimpleExoPlayer!!.playWhenReady = false
        val userAgent: String = Util.getUserAgent(FURenderManager.mContext, FURenderManager.mContext.packageName)
        val dataSourceFactory = DefaultDataSourceFactory(FURenderManager.mContext, userAgent)
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val uri = Uri.fromFile(File(videoPath))
        val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(uri)
        mSimpleExoPlayer!!.prepare(mediaSource)
    }

    /**
     * 释放播放器
     */
    private fun releaseMediaPlayer() {
        mOnVideoPlayListener = null
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer!!.stop(true)
            mSimpleExoPlayer!!.release()
            mSimpleExoPlayer = null
        }
    }

    /**
     * 开始播放
     * @param listener OnVideoPlayListener?
     */
    override fun startMediaPlayer(listener: OnVideoPlayListener?) {
        mOnVideoPlayListener = listener
        mPlayerHandler?.post {
            mSimpleExoPlayer?.seekTo(0)
            mSimpleExoPlayer?.playWhenReady = true
        }
    }

    /**
     * 暂停播放
     */
    private fun pauseMediaPlayer() {
        mSimpleExoPlayer?.playWhenReady = false
        mSimpleExoPlayer?.seekTo(0)
    }


    private val mMediaEventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> if (playWhenReady) {
                    gLSurfaceView?.requestRender()
                }
                Player.STATE_ENDED -> {
                    mOnVideoPlayListener?.onPlayFinish()
                }
                else -> {
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            FULogger.e(TAG, "onPlayerError:${error.message} ")
            val message: String = when (error.type) {
                ExoPlaybackException.TYPE_SOURCE -> "数据源异常"
                ExoPlaybackException.TYPE_RENDERER -> "解码异常"
                ExoPlaybackException.TYPE_UNEXPECTED -> "其他异常"
                else -> "其他异常"
            }
            mOnVideoPlayListener?.onError(message)
        }

    }

    //endregion


    //region 后台线程
    private var mPlayerHandler: Handler? = null

    private fun startPlayerThread() {
        if (mPlayerHandler == null) {
            val playerThread = HandlerThread("exo_player")
            playerThread.start()
            mPlayerHandler = Handler(playerThread.looper)
        }
        mPlayerHandler?.post {
            createMediaPlayer()
        }
    }

    private fun stopPlayerThread() {
        mPlayerHandler?.removeCallbacksAndMessages(null)
        mPlayerHandler?.post {
            releaseMediaPlayer()
        }
        mPlayerHandler?.looper?.quitSafely()
        mPlayerHandler = null
    }


}