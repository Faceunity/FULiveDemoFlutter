package com.faceunity.fulive_plugin.adapter

import android.util.Log
import com.faceunity.core.enumeration.FUAITypeEnum
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.fulive_plugin.common.PluginConfig
import com.faceunity.fulive_plugin.FULivePlugin
import com.faceunity.fulive_plugin.data_factory.FaceBeautyDataFactory
import com.faceunity.fulive_plugin.data_factory.StickerDataFactory
import com.faceunity.fulive_plugin.view.BaseGLView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


/**
 *
 * @author benyq
 * @date 2023/2/22
 * @email 1520063035@qq.com
 * 贴纸适配器，适配 StickerDataFactory 与 插件
 */
class FUStickerAdapter {

    companion object {
        const val method = "Sticker"
    }

    private var propDataFactory: StickerDataFactory? = null

    fun methodCall(plugin: FULivePlugin, call: MethodCall, result: MethodChannel.Result){
        val arguments = call.arguments as? Map<*, *>?
        val method = arguments?.get("method") as String?
        Log.i("Sticker", "FUStickerAdapter methodCall: $method, arguments: $arguments")
        when(method) {
            "configBiz" -> config()
            "dispose" -> dispose()
            "clickItem" -> {
                val index = arguments?.get("index") as Int
                selectedItem(index)
                result.success(true)
            }
            "flutterWillAppear" -> {
                plugin.setState(FULivePlugin.STATE_DISPLAY)
                plugin.getGlView().onResume()
            }
            "flutterWillDisappear" -> {
                plugin.getGlView().onPause()
                plugin.setState(FULivePlugin.STATE_CUSTOM)
            }
        }
    }

    private fun config() {
        propDataFactory =
            StickerDataFactory(0)

        BaseGLView.runOnGLThread {
            propDataFactory?.configBiz()
            FaceBeautyDataFactory.configBeauty()
        }
    }

    private fun dispose() {
        propDataFactory?.dispose()
    }

    private fun selectedItem(index: Int) {
        propDataFactory?.onItemSelected(index)
        if (FURenderKit.getInstance().faceBeauty == null) {
            FaceBeautyDataFactory.configBeauty()
        }
    }

}