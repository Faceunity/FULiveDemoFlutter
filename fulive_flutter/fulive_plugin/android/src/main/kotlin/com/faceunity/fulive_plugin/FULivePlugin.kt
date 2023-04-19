package com.faceunity.fulive_plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.faceunity.core.camera.FUCamera
import com.faceunity.core.enumeration.FUFaceProcessorDetectModeEnum
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.fulive_plugin.adapter.FUBeautyAdapter
import com.faceunity.fulive_plugin.adapter.FUMakeupAdapter
import com.faceunity.fulive_plugin.adapter.FUStickerAdapter
import com.faceunity.fulive_plugin.common.CustomGLDisplayViewFactory
import com.faceunity.fulive_plugin.common.GLDisplayViewFactory
import com.faceunity.fulive_plugin.common.PluginConfig
import com.faceunity.fulive_plugin.data_factory.FaceBeautyDataFactory
import com.faceunity.fulive_plugin.entity.FuEvent
import com.faceunity.fulive_plugin.utils.FileUtils
import com.faceunity.fulive_plugin.utils.FuDeviceUtils
import com.faceunity.fulive_plugin.view.BaseGLView
import com.faceunity.fulive_plugin.view.BaseGLView.InfoCallback
import com.faceunity.fulive_plugin.view.VideoGlView
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.*
import io.flutter.plugin.common.EventChannel.EventSink
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 *
 * @author benyq
 * @date 2023/2/22
 * @email 1520063035@qq.com
 *
 */
class FULivePlugin : FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener {

    companion object {
        private const val TAG = "FULivePlugin"

        @JvmStatic
        lateinit var appContext: Context

        private const val REQUEST_CODE_PHOTO = 1000
        private const val REQUEST_CODE_VIDEO = 1001
        private const val IMAGE_FORMAT_JPG = ".jpg"
        private const val IMAGE_FORMAT_JPEG = ".jpeg"
        private const val IMAGE_FORMAT_PNG = ".png"
        private const val TAG_FLUTTER_FRAGMENT = "flutter"

        const val STATE_DISPLAY = 0
        const val STATE_CUSTOM = 1

    }

    private lateinit var channel: MethodChannel
    private val fuBeautyAdapter: FUBeautyAdapter by lazy { FUBeautyAdapter() }
    private val fuStickerAdapter: FUStickerAdapter by lazy { FUStickerAdapter() }
    private val fuMakeupAdapter: FUMakeupAdapter by lazy { FUMakeupAdapter() }

    private lateinit var activity: Activity
    private lateinit var activityLifecycle: Lifecycle

    private lateinit var mGLDisplayViewFactory: GLDisplayViewFactory
    private lateinit var mCustomGLDisplayViewFactory: CustomGLDisplayViewFactory

    private var state = STATE_DISPLAY
    private var mEventSink: EventSink? = null


    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {

        channel = MethodChannel(binding.binaryMessenger, "fulive_plugin")
        channel.setMethodCallHandler(this)
        appContext = binding.applicationContext

        val messenger: BinaryMessenger = binding.binaryMessenger

        mGLDisplayViewFactory = GLDisplayViewFactory(messenger)
        binding.platformViewRegistry
            .registerViewFactory("OpenGLDisplayView", mGLDisplayViewFactory)

        mCustomGLDisplayViewFactory = CustomGLDisplayViewFactory(messenger)
        binding.platformViewRegistry
            .registerViewFactory("CustomGLDisplayView", mCustomGLDisplayViewFactory)

        EventChannel(messenger, "FUEventChannel").setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventSink?) {
                mEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                mEventSink = null
            }
        })
        EventBus.getDefault().register(this)
        PluginConfig.DEVICE_LEVEL = FuDeviceUtils.judgeDeviceLevelGPU(appContext, true)
        if (PluginConfig.DEVICE_LEVEL < 0) {
            PluginConfig.DEVICE_LEVEL = 0
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        EventBus.getDefault().unregister(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        val arguments = call.arguments as? Map<*, *>?
        val method = call.method
        Log.i(TAG, "methodCall: $method, arguments: $arguments")

        when (call.method) {
            "getPlatformVersion" -> result.success("Android " + Build.VERSION.RELEASE)
            "getModuleCode" -> result.success(intArrayOf(0x7ebf7f7f, 0xfffff))
            "Common" -> commonMethod(call, result)
            "streamChannel_Common" -> {
                val subMethod = arguments?.get("method") as String?
                if (subMethod == "startBeautyStreamListen") {
                    setInfoCallback { width, height, fps, renderTime, hasFace ->
                        val debug = "Resolution:\n${width}X$height\nFPS: ${fps.toInt()}\nRender time:${renderTime.toInt()}ms"
                        val jsonObject = JSONObject()
                        jsonObject.put("debug", debug)
                        jsonObject.put("hasFace", hasFace)
                        mEventSink?.success(jsonObject.toString())
                    }
                }
            }
            "FUCustomRender" -> fuCustomRender(call, result)
            "streamChannel_FUCustomRender" -> {
                val subMethod = arguments?.get("method") as String?
                if (subMethod == "startCustomRenderStremListen") {
                    setInfoCallback { width, height, fps, renderTime, hasFace ->
                        val jsonObject = JSONObject()
                        jsonObject.put("debug", "")
                        jsonObject.put("hasFace", hasFace)
                        mEventSink?.success(jsonObject.toString())
                    }
                }
            }
            "methodChannel_FUCustomRender" -> {
            }
            "ImagePick" -> {}
            "methodChannel_ImagePick" -> {
                val arguments = call.arguments as Map<*, *>
                val value = arguments["value"] as Int
                mGLDisplayViewFactory.view.onFlutterViewDetached()
                chooseImageOrVideo(value)
            }
            FUBeautyAdapter.method -> fuBeautyAdapter.methodCall(this, call, result)
            FUStickerAdapter.method -> fuStickerAdapter.methodCall(this, call, result)
            FUMakeupAdapter.method -> fuMakeupAdapter.methodCall(this, call, result)
            else -> result.notImplemented()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetMessage(event: FuEvent) {
        Log.d(TAG, "onGetMessage: $event")
        when (event.code) {
            FuEvent.start_video_play -> {
                val map: MutableMap<String, Boolean> = HashMap()
                map["isPlay"] = true
                channel.invokeMethod("videoPlay", map)
            }
            FuEvent.finish_video_play -> {
                val map: MutableMap<String, Boolean> = HashMap()
                map["isPlay"] = false
                channel.invokeMethod("videoPlay", map)
            }
            FuEvent.stop_video_play -> { // 回到桌面或者切换进程导致的停止
//                val map: MutableMap<String, Boolean> = HashMap()
//                map["isPlay"] = false
//                channel.invokeMethod("videoPlay", map)
            }
        }
    }

    private fun chooseImageOrVideo(type: Int) {
        if (type == 0) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            activity.startActivityForResult(intent, REQUEST_CODE_PHOTO)
        } else if (type == 1) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            activity.startActivityForResult(intent, REQUEST_CODE_VIDEO)
        }
    }

    private val pluginLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            Log.i(TAG, "pluginLifecycleObserver: onResume")

            if (mGLDisplayViewFactory.view != null && state == STATE_DISPLAY) {
                mGLDisplayViewFactory.view.onResume()
            }
            if (mCustomGLDisplayViewFactory.view != null) {
                mCustomGLDisplayViewFactory.view.onResume()
            }
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            Log.i(TAG, "pluginLifecycleObserver: onPause")

            if (mGLDisplayViewFactory.view != null && state == STATE_DISPLAY) {
                mGLDisplayViewFactory.view.onPause()
            }
            if (mCustomGLDisplayViewFactory.view != null) {
                mCustomGLDisplayViewFactory.view.onPause()
            }
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
        activityLifecycle = (binding.lifecycle as HiddenLifecycleReference).lifecycle
        activityLifecycle.addObserver(pluginLifecycleObserver)
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
        activityLifecycle.removeObserver(pluginLifecycleObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return false
        }
        val uri = data.data
        val path = FileUtils.getFilePathByUri(appContext, uri)
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (!FileUtils.checkIsImage(path)) {
                Toast.makeText(activity, "请选择正确的图片文件", Toast.LENGTH_SHORT).show()
                return false
            }
            mCustomGLDisplayViewFactory.setPhotoPath(path)
            channel.invokeMethod("customSelectedImage", mapOf("type" to 0))
        } else if (requestCode == REQUEST_CODE_VIDEO) {
            if (!FileUtils.checkIsVideo(appContext, path)) {
                Toast.makeText(activity, "请选择正确的视频文件", Toast.LENGTH_SHORT).show()
                return false
            }
            mCustomGLDisplayViewFactory.setVideoPath(path)
            channel.invokeMethod("customSelectedImage", mapOf("type" to 1))
        }
        return true
    }

    fun setInfoCallback(infoCallback: InfoCallback?) {
        if (state == STATE_DISPLAY) {
            mGLDisplayViewFactory.setInfoCallback(infoCallback)
        } else {
            mCustomGLDisplayViewFactory.setInfoCallback(infoCallback)
        }
    }

    private fun commonMethod(call: MethodCall, result: MethodChannel.Result) {
        val arguments = call.arguments as? Map<*, *>?
        when(arguments?.get("method") as String?) {
            "getPerformanceLevel" -> {
                FUAIKit.getInstance().run {
                    setFaceDelayLeaveEnable(PluginConfig.FACE_DELAY_LEAVE_ENABLE)
                }
                result.success(PluginConfig.DEVICE_LEVEL)
            }
            "chooseSessionPreset" -> {
                when(arguments?.get("value") as Int) {
                    0 -> FUCamera.getInstance().changeResolution(640, 480)
                    1 -> FUCamera.getInstance().changeResolution(1280, 720)
                    2 -> FUCamera.getInstance().changeResolution(1920, 1080)
                }
                result.success(1)
            }
            "manualExpose" -> {

            }
            "adjustSpotlight" -> {
                val value = arguments!!["value"] as Double
                val convert = (value.toFloat() + 2) / 4
                FUCamera.getInstance().setExposureCompensation(convert)
                result.success(null)
            }
            "takePhoto" -> getGlView().takePic()
            "startRecord" -> getGlView().startRecord()
            "stopRecord" -> getGlView().stopRecord()
            "renderOrigin" -> {
                val enable = arguments!!["value"] as Boolean
                getGlView().rendererSwitch(!enable)
            }
            "changeCameraFront" -> {
                FUCamera.getInstance().switchCamera()
            }
            //Android 没有这功能
            "changeCameraFormat" -> {}
            "disposeCommon" -> {
                FURenderKit.getInstance().release()
            }
        }
    }

    private fun fuCustomRender(call: MethodCall, result: MethodChannel.Result) {
        val arguments = call.arguments as? Map<*, *>?
        when(arguments?.get("method") as String?) {
            "customRenderOrigin" -> {
                val enable = arguments!!["value"] as Boolean
                getGlView().rendererSwitch(!enable)
            }
            "downLoadCustomRender" -> {
                val value = arguments!!["value"] as Int
                getGlView().takePic()
            }
            "selectedImageOrVideo" -> {
                setState(STATE_CUSTOM)
                //加入美颜
                FaceBeautyDataFactory.configBeauty()
                val value = arguments?.get("value") as Int
                Log.d(TAG, "fuCustomRender: $value")
                if (value == 0) {
                    FUAIKit.getInstance()
                        .faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.IMAGE)
                }else {
                    FUAIKit.getInstance()
                        .faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.VIDEO)
                }
            }
            "customImageDispose" -> {
                getGlView().onPause()
                FUAIKit.getInstance()
                    .faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.VIDEO)
            }
            "customVideoRePlay" -> {
                if (getGlView() is VideoGlView) {
                    (getGlView() as VideoGlView).replay()
                }
            }
        }
    }


    fun getGlView(): BaseGLView {
        return if (state == STATE_DISPLAY) {
            mGLDisplayViewFactory.view
        } else {
            mCustomGLDisplayViewFactory.view
        }
    }

    fun setState(state: Int) {
        this.state = state
    }

}