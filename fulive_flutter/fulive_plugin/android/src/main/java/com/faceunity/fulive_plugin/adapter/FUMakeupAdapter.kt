package com.faceunity.fulive_plugin.adapter

import android.util.Log
import com.faceunity.fulive_plugin.FULivePlugin
import com.faceunity.fulive_plugin.data_factory.FaceBeautyDataFactory
import com.faceunity.fulive_plugin.data_factory.MakeupDataFactory
import com.faceunity.fulive_plugin.view.BaseGLView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 *
 * @author benyq
 * @date 2023/2/22
 * @email 1520063035@qq.com
 * 美妆适配器，适配 MakeupDataFactory 与 插件
 */
class FUMakeupAdapter {

    companion object {
        const val method = "FUMakeup"
    }

    private lateinit var plugin: FULivePlugin

    fun methodCall(plugin: FULivePlugin, call: MethodCall, result: MethodChannel.Result){
        this.plugin = plugin
        val arguments = call.arguments as? Map<*, *>?
        val method = arguments?.get("method") as String?
        Log.i("FUMakeup", "FUMakeupAdapter methodCall: $method, arguments: $arguments")
        when(method) {
            "configMakeup" -> configMakeup()
            "disposeMakeup" -> {
                MakeupDataFactory.releaseMakeup()
            }
            "itemDidSelectedWithParams" -> {
                val index = arguments?.get("index") as Int
                itemDidSelectedWithParams(index)
            }
            "sliderChangeValueWithValue" -> {
                val index = arguments?.get("index") as Int
                val intensity = arguments["value"] as Double
                sliderChangeValueWithValue(index, intensity)
            }
            "makeupChange" -> {
                val value = arguments?.get("value") as Boolean
                if (!value) {
                    MakeupDataFactory.enterCustomMakeup()
                }else {
                    MakeupDataFactory.exitCustomMakeup()
                }

            }
            "requestCustomIndex" -> {
                // 根据index获取组合妆参数，参与自定义
                val index = arguments?.get("index") as Int
                val resultJson = MakeupDataFactory.createMakeupParamJson(index)
                result.success(resultJson)
            }

            /**
             * 自定义美妆
             * subTitleIndex 粉底、口红等大类
             * subIndex  粉底、口红等中的小项
             * colorIndex  颜色
             */
            "didSelectedSubItem" -> {
                val subIndex = arguments?.get("subIndex") as Int
                val subTitleIndex = arguments["subTitleIndex"] as Int
                MakeupDataFactory.onCustomBeanSelected(subTitleIndex, subIndex)
            }
            "subMakupSliderChangeValueWithValue" -> {
                val subIndex = arguments?.get("subIndex") as Int
                val subTitleIndex = arguments["subTitleIndex"] as Int
                val value = arguments["value"] as Double
                subMakupSliderChangeValueWithValue(subIndex, subTitleIndex, value)
            }
            "didSelectedColorItem" -> {
                val subIndex = arguments?.get("subIndex") as Int
                val subTitleIndex = arguments["subTitleIndex"] as Int
                val colorIndex = arguments["colorIndex"] as Int
                MakeupDataFactory.updateCustomColor(subTitleIndex, colorIndex)
            }
            "subMakeupChange" -> {
                val index = arguments!!["index"] as Int
                result.success(MakeupDataFactory.checkMakeupChange(index))
            }
        }

    }

    private fun configMakeup() {
        plugin.setState(FULivePlugin.STATE_DISPLAY)
        BaseGLView.runOnGLThread {
            MakeupDataFactory.configureMakeup()
        }
    }

    private fun itemDidSelectedWithParams(index: Int) {
        MakeupDataFactory.onMakeupCombinationSelected(index)
    }

    private fun sliderChangeValueWithValue(index: Int, intensity: Double) {
        MakeupDataFactory.sliderChangeValueWithValue(index, intensity);
    }

    private fun subMakupSliderChangeValueWithValue(subIndex: Int, subTitleIndex: Int, value: Double) {
        MakeupDataFactory.updateCustomItemIntensity(subTitleIndex, subIndex, value)
    }

}