package com.faceunity.fuliveplugin.fulive_plugin

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.faceunity.core.enumeration.FUFaceProcessorDetectModeEnum
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.modules.BaseModulePlugin
import com.faceunity.fuliveplugin.fulive_plugin.modules.FUFaceBeautyPlugin
import com.faceunity.fuliveplugin.fulive_plugin.modules.FUMakeupPlugin
import com.faceunity.fuliveplugin.fulive_plugin.modules.FUStickerPlugin
import com.faceunity.fuliveplugin.fulive_plugin.modules.RenderPlugin
import com.faceunity.fuliveplugin.fulive_plugin.render.GLSurfaceViewPlatformViewFactory
import com.faceunity.fuliveplugin.fulive_plugin.render.NotifyFlutterListener
import com.faceunity.fuliveplugin.fulive_plugin.utils.RestrictedSkinTool
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/** FulivePlugin */
class FulivePlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, BaseModulePlugin, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity

    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private var eventSink: EventChannel.EventSink? = null

    private val glSurfaceViewPlatformViewFactory = GLSurfaceViewPlatformViewFactory()
    private val faceBeautyPlugin by lazy { FUFaceBeautyPlugin() }
    private val stickerPlugin by lazy { FUStickerPlugin() }
    private val makeupPlugin by lazy { FUMakeupPlugin(context) }
    private val renderPlugin by lazy { RenderPlugin(methodChannel) }

    private lateinit var context: Context
    private val mainScope = MainScope()
    private lateinit var lifecycle: Lifecycle

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "fulive_plugin")
        methodChannel.setMethodCallHandler(this)

        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "render_event_channel")
        eventChannel.setStreamHandler(this)

        context = flutterPluginBinding.applicationContext

        glSurfaceViewPlatformViewFactory.setRenderFrameListener(object : NotifyFlutterListener {
            override fun notifyFlutter(data: Map<String, Any>) {
                mainScope.launch {
                    eventSink?.success(data)
                }
            }
        })
        flutterPluginBinding.platformViewRegistry.registerViewFactory("faceunity_display_view", glSurfaceViewPlatformViewFactory)
        renderPlugin.init(glSurfaceViewPlatformViewFactory)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when {
            faceBeautyPlugin.containsMethod(call.method) -> faceBeautyPlugin.handleMethod(call, result)
            makeupPlugin.containsMethod(call.method) -> makeupPlugin.handleMethod(call, result)
            stickerPlugin.containsMethod(call.method) -> stickerPlugin.handleMethod(call, result)
            renderPlugin.containsMethod(call.method) -> renderPlugin.handleMethod(call, result)
            else -> handleMethod(call, result)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
        eventSink = null
        mainScope.cancel()
        renderPlugin.dispose()
    }


    private val methods =
        mapOf(
            "getPlatformVersion" to ::getPlatformVersion,
            "devicePerformanceLevel" to ::devicePerformanceLevel,
            "getModuleCode" to ::getModuleCode,
            "setFaceProcessorDetectMode" to ::setFaceProcessorDetectMode,
            "requestAlbumForType" to ::requestAlbumForType,
            "setMaxFaceNumber" to ::setMaxFaceNumber,
            "restrictedSkinParams" to ::restrictedSkinParams
        )
    override fun methods(): Map<String, (Map<String, Any>, MethodChannel.Result) -> Any> = methods

    override fun tag() = "FulivePlugin"

    private fun getPlatformVersion(params: Map<String, Any>, result: MethodChannel.Result) {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }
    private fun devicePerformanceLevel(params: Map<String, Any>, result: MethodChannel.Result) {
        result.success(FaceunityKit.devicePerformanceLevel)
    }
    private fun getModuleCode(params: Map<String, Any>, result: MethodChannel.Result) {
        val code = params.getInt("code") ?: return
        result.success(renderKit.getModuleCode(code))
    }

    private fun setFaceProcessorDetectMode(params: Map<String, Any>, result: MethodChannel.Result) {
        val mode = params.getInt("mode")?: return
        if (mode == 0) {
            FUAIKit.getInstance().faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.IMAGE)
        } else {
            FUAIKit.getInstance().faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.VIDEO)
        }
    }

    private fun requestAlbumForType(params: Map<String, Any>, result: MethodChannel.Result) {
        val type = params.getInt("type")?: return
        mainScope.launch {
            glSurfaceViewPlatformViewFactory.startSelectMedia()
            val pair = suspendCancellableCoroutine { cancellableContinuation->
                if (type == 0) {
                    ActivityPluginBridge.pickImageFile { isSuccess, path ->
                        cancellableContinuation.resume(Pair(isSuccess, path))
                    }
                }else{
                    ActivityPluginBridge.pickVideoFile { isSuccess, path ->
                        cancellableContinuation.resume(Pair(isSuccess, path))
                    }
                }
            }
            glSurfaceViewPlatformViewFactory.stopSelectMedia()
            val mediaPath = pair.second
            glSurfaceViewPlatformViewFactory.setMediaPath(mediaPath)
            methodChannel.invokeMethod(if (type == 0) "photoSelected" else "videoSelected", pair.first)
        }
    }

    private fun setMaxFaceNumber(params: Map<String, Any>, result: MethodChannel.Result) {
        val number = params.getInt("number")?: return
        FUAIKit.getInstance().maxFaces = number.coerceIn(1, 4)
    }

    private fun restrictedSkinParams(params: Map<String, Any>, result: MethodChannel.Result) {
        mainScope.launch(Dispatchers.IO) {
            result.success(RestrictedSkinTool.restrictedSkinParams)
        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        this.eventSink = events
    }

    override fun onCancel(arguments: Any?) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        lifecycle = getActivityLifecycle(binding)
        lifecycle.addObserver(glSurfaceViewPlatformViewFactory)
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
        lifecycle.removeObserver(glSurfaceViewPlatformViewFactory)
    }


    private fun getActivityLifecycle(
        activityPluginBinding: ActivityPluginBinding,
    ): Lifecycle {
        val reference = activityPluginBinding.lifecycle as HiddenLifecycleReference
        return reference.lifecycle
    }
}
