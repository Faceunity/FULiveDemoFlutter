package com.faceunity.fulive_plugin.adapter

import android.util.Log
import com.faceunity.core.enumeration.FUAITypeEnum
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.fulive_plugin.FULivePlugin
import com.faceunity.fulive_plugin.common.PluginConfig
import com.faceunity.fulive_plugin.data_factory.FaceBeautyDataFactory
import com.faceunity.fulive_plugin.view.BaseGLView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


/**
 *
 * @author benyq
 * @date 2023/2/22
 * @email 1520063035@qq.com
 * 美颜适配器，适配 FaceBeautyDataFactory 与 插件
 */
class FUBeautyAdapter {

    companion object {
        const val method = "FUBeauty"
    }

    private lateinit var plugin: FULivePlugin

    fun methodCall(plugin: FULivePlugin, call: MethodCall, result: MethodChannel.Result) {
        this.plugin = plugin
        val arguments = call.arguments as? Map<*,*>?
        val method = arguments?.get("method") as String?
        Log.i("FUBeauty", "FUBeautyAdapter methodCall: $method, arguments: $arguments")
        when(method) {
            "configBeauty" -> configBeauty()
            "disposeFUBeauty" -> disposeFUBeauty()
            "setFUBeautyParams" -> setFUBeautyParams(arguments)
            "setFilterParams" -> setFilterParams(arguments)
            "beautyClean" -> beautyClean()
            "resetDefault" -> resetDefault(arguments)
            "FlutterWillDisappear" -> flutterWillDisappear(plugin)
            "FlutterWillAppear" -> flutterWillAppear(plugin)
        }
    }

    private fun configBeauty() {
        plugin.setState(FULivePlugin.STATE_DISPLAY)
        BaseGLView.runOnGLThread {
            FaceBeautyDataFactory.configBeauty()
        }
    }

    private fun disposeFUBeauty() {
        FaceBeautyDataFactory.dispose()
    }

    private fun setFUBeautyParams(arguments: Map<*,*>?) {
        val type = arguments?.get("bizType") as Int
        val index = arguments["subBizType"] as Int
        when(type) {
            0 -> {
                //美肤
                val value = arguments["value"] as Double
                FaceBeautyDataFactory.setSkinBeauty(index, value)
            }
            1 -> {
                //美型
                val value = arguments["value"] as Double
                FaceBeautyDataFactory.setShapeBeauty(index, value)
            }
        }

    }

    private fun setFilterParams(arguments: Map<*,*>?) {
        val filterName = arguments?.get("stringValue") as String
        val value = arguments["value"] as Double
        FaceBeautyDataFactory.setFilter(filterName, value)
    }

    private fun beautyClean() {
    }

    private fun resetDefault(arguments: Map<*,*>?) {
        when (arguments?.get("bizType") as Int?) {
            0 -> FaceBeautyDataFactory.resetSkinBeauty()
            1 -> FaceBeautyDataFactory.resetShapeBeauty()
            2 -> FaceBeautyDataFactory.resetFilter()
        }
    }

    private fun flutterWillDisappear(plugin: FULivePlugin) {
        plugin.getGlView().onPause()
        plugin.setState(FULivePlugin.STATE_CUSTOM)
    }

    private fun flutterWillAppear(plugin: FULivePlugin) {
        plugin.setState(FULivePlugin.STATE_DISPLAY)
        plugin.getGlView().onResume()
        configBeauty()
    }
}