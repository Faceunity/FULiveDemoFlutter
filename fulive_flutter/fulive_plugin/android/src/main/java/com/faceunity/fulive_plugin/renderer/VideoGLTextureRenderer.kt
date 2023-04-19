package com.faceunity.fulive_plugin.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.opengl.*
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.enumeration.FUExternalInputEnum
import com.faceunity.core.enumeration.FUInputTextureEnum
import com.faceunity.core.enumeration.FUTransformMatrixEnum
import com.faceunity.core.glview.GLTextureView
import com.faceunity.core.infe.IVideoRenderer
import com.faceunity.core.listener.OnGlRendererListener
import com.faceunity.core.listener.OnVideoPlayListener
import com.faceunity.core.media.photo.OnPhotoRecordingListener
import com.faceunity.core.media.photo.PhotoRecordHelper
import com.faceunity.core.program.ProgramTexture2d
import com.faceunity.core.program.ProgramTextureOES
import com.faceunity.core.renderer.texture.BaseFUTextureRenderer
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.GlUtil
import com.faceunity.core.utils.LimitFpsUtil
import com.faceunity.fulive_plugin.view.VideoGlView
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


/**
 *
 * DESC：
 * Created on 2021/1/4
 * openUnDrawMode 为true的时候，renderVideoUnDrawTexture的时候不画面不显示到页面上
 * theSameDrawTextureMode 是否左下角为坐标原点(和oes纹理一样的方式) 绘制纹理
 */
class VideoGLTextureRenderer(
    gLTextureView: GLTextureView?,
    private val videoPath: String,
    glRendererListener: OnGlRendererListener,
    private val openUnDrawMode: Boolean = true,
    private val identicalDrawTextureMode: Boolean = true
) : BaseFUTextureRenderer(gLTextureView, glRendererListener), IVideoRenderer {

    constructor(
        glTextureView: GLTextureView?,
        videoPath: String,
        glRendererListener: OnGlRendererListener
    ) : this(glTextureView, videoPath, glRendererListener, true, true)

    constructor(
        glTextureView: GLTextureView?,
        videoPath: String,
        glRendererListener: OnGlRendererListener,
        openUnDrawMode: Boolean
    ) : this(glTextureView, videoPath, glRendererListener, openUnDrawMode, true)

    private lateinit var applicationContext: Context

    private val TEXTURE_MATRIX_CCRO_FLIPV_0_LLQ = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f
    )

    var videoDuration = 0L

    //region 初始化
    init {
        currentFURenderInputData.apply {
            currentFURenderInputData.texture =
                FURenderInputData.FUTexture(FUInputTextureEnum.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, 0)
            renderConfig.apply {
                externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO
                cameraFacing = CameraFacingEnum.CAMERA_BACK
                inputBufferMatrix = FUTransformMatrixEnum.CCROT0
                inputTextureMatrix = FUTransformMatrixEnum.CCROT0
            }
        }
        externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_VIDEO
        gLTextureView?.let {
            applicationContext = it.context.applicationContext
            it.setEGLContextClientVersion(GlUtil.getSupportGlVersion(it.context))
            it.setRenderer(this)
            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }

    }
    //endregion

    //region 生命周期调用

    /**Activity onPause**/
    override fun onResume() {
        startPlayerThread()
        if (isActivityPause) {
            gLTextureView?.onResume()
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
            gLTextureView?.queueEvent(Runnable {
                destroyGlSurface()
                count.countDown()
            })
        }

        try {
            count.await(500, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            // ignored
        }
        gLTextureView?.onPause()
    }

    /**Activity onDestroy**/
    override fun onDestroy() {
        stopPlayerThread()
        glRendererListener = null
        gLTextureView = null
    }


    //endregion 生命周期调用


    //region GLSurfaceView.Renderer 回调

    private var videoOrientation = 0
    /*系统相机录制标识*/

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mProgramTextureOes: ProgramTextureOES? = null

    //1.绘制普通视频图片
    val drawNormal = 0

    //2.绘制缓存图片 不需要缓存图片
    val drawCacheBitmapUnCacheBitmap = 99

    //3.绘制缓存图片 需要缓存图片
    val drawCacheBitmapCacheBitmap = 100

    //4.过滤几帧缓存图片
    val filterCacheBitmap = 5

    /**
     * defaultValue 不绘制缓存图片，需要缓存图片
     * drawCacheBitmapUnCacheBitmap 绘制缓存图片 不需要缓存图片
     * drawCacheBitmapCacheBitmap 绘制缓存图片 需要缓存图片
     */
    @Volatile
    var isShowVideoCacheFrame = drawNormal


    override fun surfaceCreated(config: EGLConfig?) {
        if (identicalDrawTextureMode)
            programTexture2d = ProgramTexture2d(identicalDrawTextureMode)
        originalTextId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
        currentFURenderInputData.texture?.texId = originalTextId
        mProgramTextureOes = ProgramTextureOES()
        createSurfaceTexture()
        analysisVideo()
        LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS)
        isShowVideoCacheFrame = drawNormal
    }

    override fun surfaceChanged(width: Int, height: Int) {
        originMvpMatrix = if (videoOrientation == 0 || videoOrientation == 180) {
            GlUtil.changeMvpMatrixInside(
                width.toFloat(),
                height.toFloat(),
                originalWidth.toFloat(),
                originalHeight.toFloat()
            )
        } else {
            GlUtil.changeMvpMatrixInside(
                width.toFloat(),
                height.toFloat(),
                originalHeight.toFloat(),
                originalWidth.toFloat()
            )
        }
        smallViewMatrix =
            GlUtil.changeMvpMatrixCrop(90f, 160f, originalHeight.toFloat(), originalWidth.toFloat())
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

    override fun prepareRender(): Boolean {
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

    override fun drawRenderFrame() {
        if (showCacheBitmapLogic()) return
        if (faceUnity2DTexId > 0 && renderSwitch) {
            if (identicalDrawTextureMode) {
                val matrix = originTexMatrix.copyOf()
                Matrix.multiplyMM(matrix, 0, TEXTURE_MATRIX_CCRO_FLIPV_0_LLQ, 0, matrix, 0)
                programTexture2d!!.drawFrame(faceUnity2DTexId, matrix, originMvpMatrix)
            } else {
                programTexture2d!!.drawFrame(
                    faceUnity2DTexId,
                    currentFUTexMatrix,
                    currentFUMvpMatrix
                )
            }
        } else if (originalTextId > 0) {
            mProgramTextureOes!!.drawFrame(originalTextId, originTexMatrix, originMvpMatrix)
        }
        if (drawSmallViewport) {
            GLES20.glViewport(
                smallViewportX,
                smallViewportY,
                smallViewportWidth,
                smallViewportHeight
            )
            mProgramTextureOes!!.drawFrame(originalTextId, originTexMatrix, smallViewMatrix)
            GLES20.glViewport(0, 0, surfaceViewWidth, surfaceViewHeight)
        }
    }

    /**
     * 展示缓存图片的逻辑
     */
    private fun showCacheBitmapLogic(): Boolean {
        if (openUnDrawMode) {
            //需要缓存图片 需要显示缓存图片
            if (isShowVideoCacheFrame >= drawCacheBitmapUnCacheBitmap) {
                if (isShowVideoCacheFrame == drawCacheBitmapCacheBitmap) {
                    cacheBitmap()
                    isShowVideoCacheFrame = drawCacheBitmapUnCacheBitmap
                }
                drawCacheBitmap()
                return true
            }

            //如果是在过滤帧中则几帧之后显示原始视频流
            if (isShowVideoCacheFrame in drawNormal + 1..filterCacheBitmap) {
                isShowVideoCacheFrame--
                drawCacheBitmap()
                return true
            }
        }
        return false
    }

    private fun createSurfaceTexture() {
        mSurfaceTexture = SurfaceTexture(originalTextId)
        mSurfaceTexture!!.setOnFrameAvailableListener {
            gLTextureView?.requestRender()
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
            originalWidth =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    ?.toInt() ?: 0
            originalHeight =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    ?.toInt() ?: 0
            videoOrientation =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                    ?.toInt() ?: 0
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

    //渲染视频不渲染画面监听
    private var mRenderVideoUnDrawTextureListener: OnVideoPlayListener? = null

    /**
     * 初始化播放器
     */
    private fun createMediaPlayer() {
        mSimpleExoPlayer = SimpleExoPlayer.Builder(applicationContext).build()
        mSimpleExoPlayer!!.addListener(mMediaEventListener)
        mSimpleExoPlayer!!.playWhenReady = false
        val userAgent: String =
            Util.getUserAgent(applicationContext, applicationContext.packageName)
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext, userAgent)
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
        mRenderVideoUnDrawTextureListener = null
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
            if (openUnDrawMode) isShowVideoCacheFrame = drawNormal
            mSimpleExoPlayer?.volume = 1f
            mSimpleExoPlayer?.seekTo(0)
            mSimpleExoPlayer?.playWhenReady = true
        }
    }

    /**
     * 停止播放
     */
    fun pauseMediaPlayer() {
        mPlayerHandler?.post {
            mSimpleExoPlayer?.playWhenReady = false
            mSimpleExoPlayer?.seekTo(0)
            if (openUnDrawMode) isShowVideoCacheFrame = filterCacheBitmap
        }
    }


    private val mMediaEventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {

                    if (playWhenReady) {
                        gLTextureView?.requestRender()
                    }
                    mPlayerHandler?.post {
                        videoDuration = getDuration()
                    }
                }
                Player.STATE_ENDED -> {
                    mOnVideoPlayListener?.onPlayFinish()
                    mRenderVideoUnDrawTextureListener?.onPlayFinish()
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
            mRenderVideoUnDrawTextureListener?.onError(message)
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

    /**
     * 该方法用于禁止onDraw 并且从头开始渲染视频
     */
    fun renderVideoUnDrawTexture(renderVideoUnDrawTextureListener: OnVideoPlayListener) {
        if (!openUnDrawMode) {
            return
        }
        if (isShowVideoCacheFrame != drawCacheBitmapUnCacheBitmap) isShowVideoCacheFrame =
            drawCacheBitmapCacheBitmap
        mRenderVideoUnDrawTextureListener = renderVideoUnDrawTextureListener
        mPlayerHandler?.post {
            mSimpleExoPlayer?.seekTo(0)
            mSimpleExoPlayer?.playWhenReady = true
            mSimpleExoPlayer?.volume = 0f
        }
    }

    private var mCacheBitmap: Bitmap? = null //图片资源
    private var mCacheBitmapTexId = 0

    private val mOnPhotoRecordingListener by lazy {
        OnPhotoRecordingListener {
            mCacheBitmap = it
        }
    }

    private val mPhotoRecordHelper = PhotoRecordHelper(mOnPhotoRecordingListener)

    private fun cacheBitmap() {
        if (currentFURenderOutputData != null && currentFURenderOutputData!!.texture != null)
            mPhotoRecordHelper.sendRecordingData(
                faceUnity2DTexId,
                TEXTURE_MATRIX,
                TEXTURE_MATRIX,
                currentFURenderOutputData!!.texture!!.width,
                currentFURenderOutputData!!.texture!!.height,
                false,
                false
            )
    }

    private fun drawCacheBitmap() {
        mCacheBitmap?.let {
            deleteCacheBitmapTexId()
            mCacheBitmapTexId = GlUtil.createImageTexture(it)
            if (mCacheBitmapTexId > 0) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                if (identicalDrawTextureMode) {
                    programTexture2d!!.drawFrame(
                        mCacheBitmapTexId,
                        originTexMatrix,
                        originMvpMatrix
                    )
                } else {
                    val cacheBitmapMvpMatrix = currentFUMvpMatrix.copyOf()
                    Matrix.scaleM(cacheBitmapMvpMatrix, 0, 1f, -1f, 1f)
                    programTexture2d!!.drawFrame(
                        mCacheBitmapTexId,
                        currentFUTexMatrix,
                        cacheBitmapMvpMatrix
                    )
                }
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

    /**
     * 获取时长
     */
    private fun getDuration(): Long {
        var duration = 0L
        mSimpleExoPlayer?.duration?.let {
            duration = it
        }
        return duration
    }
}