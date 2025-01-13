package com.faceunity.fuliveplugin.fulive_plugin.render

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.View
import com.faceunity.core.entity.FURenderFrameData
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.entity.FURenderOutputData
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.core.listener.OnGlRendererListener
import com.faceunity.core.media.photo.PhotoRecordHelper
import com.faceunity.core.media.video.OnVideoRecordingListener
import com.faceunity.core.media.video.VideoRecordHelper
import com.faceunity.core.model.facebeauty.FaceBeautyBlurTypeEnum
import com.faceunity.core.renderer.BaseFURenderer
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.GlUtil
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityConfig
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.utils.FileUtils
import com.faceunity.fuliveplugin.fulive_plugin.utils.FuDeviceUtils
import io.flutter.plugin.platform.PlatformView
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

/**
 *
 * @author benyq
 * @date 12/19/2023
 *
 */
abstract class BasePlatformView(private val context: Context): PlatformView, OnGlRendererListener {

    protected val mFUAIKit = FUAIKit.getInstance()
    protected val mFURenderKit = FURenderKit.getInstance()

    protected val glSurfaceView: GLSurfaceView = GLSurfaceView(context).apply {
        setZOrderOnTop(true)
        setZOrderMediaOverlay(true)
    }
    protected var surfaceWidth = 0
    protected var surfaceHeight = 0


    // region 录制功能

    @Volatile
    protected var isTakePhoto = false
    @Volatile
    protected var isRecordingPrepared = false
    protected var isRecording = false
    @Volatile
    var isViewRendering = false
    protected val takePhotoActions = ConcurrentLinkedQueue<(Boolean) -> Unit>()
    protected val recordVideoActions = ConcurrentLinkedQueue<(Boolean) -> Unit>()

    protected val mPhotoRecordHelper: PhotoRecordHelper = PhotoRecordHelper {
        val path: String? = FileUtils.addBitmapToAlbum(context, it)
        FULogger.d(tag(), "photo onRecordSuccess: $path")
        takePhotoActions.poll()?.invoke(path!= null)
    }

    protected val mVideoRecordHelper: VideoRecordHelper = VideoRecordHelper(context, object : OnVideoRecordingListener {
        override fun onPrepared() {
            FULogger.d(tag(), "video onPrepared")
            isRecordingPrepared = true
            onVideoRecordPrepared()
        }
        override fun onProcess(time: Long?) {
            FULogger.d(tag(), "video onProcess$time")
        }

        override fun onFinish(file: File) {
            FULogger.d(tag(), "video onFinish$file")
            isRecordingPrepared = false
            onVideoRecordFinish(file)
            val filePath = FileUtils.addVideoToAlbum(context, file)
            if (file.exists()) {
                file.delete()
            }
            recordVideoActions.poll()?.invoke(filePath!= null)
        }
    })

    fun captureImage(action: (Boolean) -> Unit) {
        takePhotoActions.add(action)
        isTakePhoto = true
    }

    open fun onVideoRecordPrepared(){}
    open fun onVideoRecordFinish(file: File){}

    private fun recordingData(outputData: FURenderOutputData?, texMatrix: FloatArray) {
        if (outputData?.texture == null || outputData.texture!!.texId <= 0 || !isViewRendering) {
            return
        }
        if (isRecordingPrepared) {
            mVideoRecordHelper.frameAvailableSoon(
                outputData.texture!!.texId,
                texMatrix,
                GlUtil.IDENTITY_MATRIX
            )
        }
        if (isTakePhoto) {
            isTakePhoto = false
            mPhotoRecordHelper.sendRecordingData(
                outputData.texture!!.texId,
                texMatrix,
                GlUtil.IDENTITY_MATRIX,
                outputData.texture!!.width,
                outputData.texture!!.height
            )
        }
    }

    //endregion

    // region 人脸检测和帧率检测

    /*Benchmark 开关*/
    private val isShowBenchmark = true

    /*检测 开关*/
    protected var isAIProcessTrack = true

    /*检测 开关 打开延迟n帧再去获取人体人脸数据 */
    protected var aIProcessTrackIgnoreFrame = 0

    /*检测标识*/
    protected var trackedFaceNumber = 1


    private var mCurrentFrameCnt = 0
    private val mMaxFrameCnt = 10
    private var mLastOneHundredFrameTimeStamp: Long = 0
    private var mOneHundredFrameFUTime: Long = 0
    private var mFuCallStartTime: Long = 0 //渲染前时间锚点（用于计算渲染市场）

    protected var calculatedFPS = 0
    protected var calculatedRenderTime = 0


    /*AI识别数目检测*/
    protected fun trackStatus() {
        if (!isAIProcessTrack) {
            return
        } else {
            //延迟5帧再走后面的逻辑
            if (aIProcessTrackIgnoreFrame > 0) {
                aIProcessTrackIgnoreFrame--
                return
            }
        }
        val trackCount: Int = mFUAIKit.isTracking()
        if (trackedFaceNumber != trackCount) {
            trackedFaceNumber = trackCount
        }
    }

    protected var mEnableFaceRender = false //是否使用sdk渲染，该变量只在一个线程使用不需要volatile


    /*渲染FPS日志*/
    protected fun benchmarkFPS() {
        if (!isShowBenchmark) {
            return
        }
        if (mEnableFaceRender) {
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime
        } else {
            mOneHundredFrameFUTime = 0
        }
        if (++mCurrentFrameCnt == mMaxFrameCnt) {
            mCurrentFrameCnt = 0
            val fps = mMaxFrameCnt * 1000000000L / (System.nanoTime() - mLastOneHundredFrameTimeStamp)
            val renderTime = mOneHundredFrameFUTime / mMaxFrameCnt / 1000000L
            mLastOneHundredFrameTimeStamp = System.nanoTime()
            mOneHundredFrameFUTime = 0
            calculatedFPS = fps.toInt()
            calculatedRenderTime = renderTime.toInt()
        }
        mEnableFaceRender = false
    }

    //endregion

    // region 系统回调
    override fun onFlutterViewAttached(flutterView: View) {
        FULogger.d(tag(), "onFlutterViewAttached: ")
    }

    override fun dispose() {
        FULogger.d(tag(), "dispose: ")
        takePhotoActions.clear()
        recordVideoActions.clear()
    }

    //endregion

    // region 渲染回调
    override fun onSurfaceCreated() {
        FULogger.d(tag(), "onSurfaceCreated: ")
        //有些手机会重复调用 onSurfaceCreated
        if (isViewRendering) {
            FURenderKit.getInstance().releaseSafe()
        }
        FaceunityKit.restoreFaceUnityConfig()
        isViewRendering = true
    }

    override fun onDrawFrameAfter() {
        trackStatus()
        benchmarkFPS()
        notifyFlutterRenderInfo()
    }

    override fun onSurfaceDestroy() {
        FULogger.d(tag(), "onSurfaceDestroy: ")
        isViewRendering = false
        mFURenderKit.release()
    }


    override fun onRenderBefore(inputData: FURenderInputData?) {
        FULogger.d(tag(), "onRenderBefore: inputData：${inputData?.printMsg()}")
        mEnableFaceRender = true
        if (FaceunityKit.devicePerformanceLevel >= FuDeviceUtils.DEVICE_LEVEL_TWO) //高性能设备 -> 才会走磨皮策略
            cheekFaceConfidenceScore()
        mFuCallStartTime = System.nanoTime()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        FULogger.d(tag(), "onSurfaceChanged: width: $width, height: $height")
        surfaceWidth = width
        surfaceHeight = height
    }

    override fun onRenderAfter(outputData: FURenderOutputData, frameData: FURenderFrameData) {
        recordingData(outputData, frameData.texMatrix)
    }

    private fun cheekFaceConfidenceScore() {
        //根据有无人脸 + 设备性能 判断开启的磨皮类型
        val faceProcessorGetConfidenceScore = mFUAIKit.getFaceProcessorGetConfidenceScore(0)
        if (faceProcessorGetConfidenceScore >= FaceunityConfig.FACE_CONFIDENCE_SCORE) {
            //高端手机并且检测到人脸开启均匀磨皮，人脸点位质
            mFURenderKit.faceBeauty?.let {
                if (it.blurType != FaceBeautyBlurTypeEnum.EquallySkin) {
                    it.blurType = FaceBeautyBlurTypeEnum.EquallySkin
                    it.enableBlurUseMask = true
                }
            }
        } else {
            mFURenderKit.faceBeauty?.let {
                if (it.blurType != FaceBeautyBlurTypeEnum.FineSkin) {
                    it.blurType = FaceBeautyBlurTypeEnum.FineSkin
                    it.enableBlurUseMask = false
                }
            }
        }
    }

    // endregion


    protected var mRenderFrameListener: NotifyFlutterListener? = null
    fun setRenderFrameListener(listener: NotifyFlutterListener?) {
        mRenderFrameListener = listener
    }

    fun setRenderState(isRendering: Boolean) {
        provideRender().setFURenderSwitch(isRendering)
    }

    protected fun tag(): String {
        return this::class.java.simpleName
    }

    protected abstract fun notifyFlutterRenderInfo()

    abstract fun provideRender(): BaseFURenderer

}