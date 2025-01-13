package com.faceunity.fuliveplugin.fulive_plugin.modules

import android.content.Context
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum
import com.faceunity.core.model.makeup.Makeup
import com.faceunity.core.model.makeup.MakeupLipEnum
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityConfig
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.model.FUCombinationMakeupModel
import com.faceunity.fuliveplugin.fulive_plugin.model.FUMakeupModel
import com.faceunity.fuliveplugin.fulive_plugin.model.FUSubMakeupModel
import com.faceunity.fuliveplugin.fulive_plugin.model.FUSubMakeupType
import com.faceunity.fuliveplugin.fulive_plugin.utils.FuDeviceUtils
import io.flutter.plugin.common.MethodChannel
import org.json.JSONArray

/**
 *
 * @author benyq
 * @date 11/13/2023
 *
 */
class FUMakeupPlugin(private val context: Context) : BaseModulePlugin {

    private val methods = mapOf(
        "loadCombinationMakeup" to ::loadCombinationMakeup,
        "setCombinationMakeupIntensity" to ::setCombinationMakeupIntensity,
        "setSubMakeupBundle" to ::setSubMakeupBundle,
        "setSubMakeupIntensity" to ::setSubMakeupIntensity,
        "setSubMakeupColor" to ::setSubMakeupColor,
        "unloadSubMakeup" to ::unloadSubMakeup,
        "unloadCombinationMakeup" to ::unloadCombinationMakeup,
    )

    private fun loadCombinationMakeup(params: Map<String, Any>, result: MethodChannel.Result) {
        val makeupParams = params["makeup"] as Map<String, *>
        val makeupModel = FUMakeupModel.mapToMakeupModel(makeupParams)
        renderKit.addMakeupLoadListener {}
        // @note 嗲嗲兔、冻龄、国风、混血是8.0.0新加的四个组合妆，新组合妆只需要直接加载bundle，不需要绑定到face_makeup.bundle
        val makeup = if (makeupModel.isCombined) {
            val makeup =
                Makeup(FUBundleData(FaceunityConfig.makeupCombinationBundlePath(makeupModel.bundleName)))
            makeup.machineLevel = FaceunityKit.devicePerformanceLevel >= FuDeviceUtils.DEVICE_LEVEL_TWO
            makeup
        } else {
            val makeup = Makeup(FUBundleData(FaceunityConfig.BUNDLE_FACE_MAKEUP))
            makeup.setCombinedConfig(FUBundleData(FaceunityConfig.makeupCombinationBundlePath(makeupModel.bundleName)))
            makeup.machineLevel = FaceunityKit.devicePerformanceLevel >= FuDeviceUtils.DEVICE_LEVEL_TWO
            makeup
        }
        loadMakeupParams(makeup, makeupModel)
        updateFilterOfCombinationMakeup(makeupModel, makeup)
        renderKit.makeup = makeup
    }

    private fun setCombinationMakeupIntensity(
        params: Map<String, Any>,
        result: MethodChannel.Result,
    ) {
        val makeupParams = params["makeup"] as Map<String, *>
        val makeupModel = FUMakeupModel.mapToMakeupModel(makeupParams)
        updateFilterOfCombinationMakeup(makeupModel, renderKit.makeup as? Makeup)
        renderKit.makeup?.makeupIntensity = makeupModel.value
    }

    private fun setSubMakeupBundle(params: Map<String, Any>, result: MethodChannel.Result) {
        val subMakeupParams = params["subMakeup"] as Map<String, *>
        val makeupModel = FUSubMakeupModel.mapToSubMakeupModel(subMakeupParams) ?: return
        if (renderKit.makeup == null) {
            val makeup = Makeup(FUBundleData(FaceunityConfig.BUNDLE_FACE_MAKEUP))
            makeup.machineLevel = FaceunityKit.devicePerformanceLevel >= FuDeviceUtils.DEVICE_LEVEL_TWO
            renderKit.makeup = makeup
        }
        val makeup = renderKit.makeup as? Makeup ?: return
        val bundleName = makeupModel.bundleName ?: return
        val item = FUBundleData(FaceunityConfig.makeupItemBundlePath(bundleName))
        when (makeupModel.type) {
            FUSubMakeupType.FUSubMakeupTypeFoundation -> makeup.foundationBundle = item
            FUSubMakeupType.FUSubMakeupTypeLip -> makeup.lipBundle = item
            FUSubMakeupType.FUSubMakeupTypeBlusher -> makeup.blusherBundle = item
            FUSubMakeupType.FUSubMakeupTypeEyebrow -> makeup.eyeBrowBundle = item
            FUSubMakeupType.FUSubMakeupTypeEyeShadow -> makeup.eyeShadowBundle = item
            FUSubMakeupType.FUSubMakeupTypeEyeliner -> makeup.eyeLinerBundle = item
            FUSubMakeupType.FUSubMakeupTypeEyelash -> makeup.eyeLashBundle = item
            FUSubMakeupType.FUSubMakeupTypeHighlight -> makeup.highLightBundle = item
            FUSubMakeupType.FUSubMakeupTypeShadow -> makeup.shadowBundle = item
            FUSubMakeupType.FUSubMakeupTypePupil -> makeup.pupilBundle = item
        }
    }

    private fun setSubMakeupIntensity(params: Map<String, Any>, result: MethodChannel.Result) {
        val subMakeupParams = params["subMakeup"] as Map<String, *>
        val makeupModel = FUSubMakeupModel.mapToSubMakeupModel(subMakeupParams) ?: return
        val makeup = renderKit.makeup as? Makeup ?: return
        when (makeupModel.type) {
            FUSubMakeupType.FUSubMakeupTypeFoundation -> makeup.foundationIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeLip -> {
                makeupModel.lipstickType?.let {
                    makeup.lipType = makeupModel.lipstickType
                }
                makeupModel.isTwoColorLipstick?.let {
                    makeup.enableTwoLipColor = makeupModel.isTwoColorLipstick
                }
                makeup.lipIntensity = makeupModel.value
                if (makeup.lipType == MakeupLipEnum.WATER) {
                    // 润泽Ⅱ口红时需要开启口红高光，高光暂时为固定值
                    makeup.lipHighLightEnable = true
                    makeup.lipHighLightStrength = 0.8
                } else {
                    makeup.lipHighLightEnable = false
                    makeup.lipHighLightStrength = 0.0
                }
            }
            FUSubMakeupType.FUSubMakeupTypeBlusher -> makeup.blusherIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeEyebrow -> makeup.eyeBrowIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeEyeShadow -> makeup.eyeShadowIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeEyeliner -> makeup.eyeLineIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeEyelash -> makeup.eyeLashIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeHighlight -> makeup.heightLightIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypeShadow -> makeup.shadowIntensity = makeupModel.value
            FUSubMakeupType.FUSubMakeupTypePupil -> makeup.pupilIntensity = makeupModel.value
        }
    }

    private fun setSubMakeupColor(params: Map<String, Any>, result: MethodChannel.Result) {
        val subMakeupParams = params["subMakeup"] as Map<String, *>
        val makeupModel = FUSubMakeupModel.mapToSubMakeupModel(subMakeupParams) ?: return
        val color = if (makeupModel.colors.isNullOrEmpty() || makeupModel.defaultColorIndex == null || makeupModel.defaultColorIndex >= makeupModel.colors.size) {
            buildFUColorRGBData(null)
        }else{
            buildFUColorRGBData(makeupModel.colors[makeupModel.defaultColorIndex])
        }
        val makeup = renderKit.makeup as? Makeup ?: return
        when (makeupModel.type) {
            FUSubMakeupType.FUSubMakeupTypeFoundation -> makeup.foundationColor = color
            FUSubMakeupType.FUSubMakeupTypeLip -> {
                if (makeup.lipType == MakeupLipEnum.MOIST) {
                    makeup.lipColorV2 = color
                }else {
                    makeup.lipColor = color
                }
            }
            FUSubMakeupType.FUSubMakeupTypeBlusher -> makeup.blusherColor = color
            FUSubMakeupType.FUSubMakeupTypeEyebrow -> makeup.eyeBrowColor = color
            FUSubMakeupType.FUSubMakeupTypeEyeShadow -> {
                val shadowColor = makeupModel.colors!![makeupModel.defaultColorIndex!!]
                makeup.eyeShadowColor = buildFUColorRGBData(shadowColor.subList(0, 4))
                makeup.eyeShadowColor2 = buildFUColorRGBData(shadowColor.subList(4, 8))
                makeup.eyeShadowColor3 = buildFUColorRGBData(shadowColor.subList(8, 12))
            }

            FUSubMakeupType.FUSubMakeupTypeEyeliner -> makeup.eyeLinerColor = color
            FUSubMakeupType.FUSubMakeupTypeEyelash -> makeup.eyeLashColor = color
            FUSubMakeupType.FUSubMakeupTypeHighlight -> makeup.highLightColor = color
            FUSubMakeupType.FUSubMakeupTypeShadow -> makeup.shadowColor = color
            FUSubMakeupType.FUSubMakeupTypePupil -> makeup.pupilColor = color
        }
    }

    private fun unloadSubMakeup(params: Map<String, Any>, result: MethodChannel.Result) {
        val type = params.getInt("type") ?: return
        val makeup = renderKit.makeup as? Makeup ?: return
        when (type) {
            FUSubMakeupType.FUSubMakeupTypeFoundation -> makeup.foundationIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeLip -> makeup.lipIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeBlusher -> makeup.blusherIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeEyebrow -> {
                makeup.eyeBrowIntensity = 0.0
                makeup.enableBrowWarp = false
            }
            FUSubMakeupType.FUSubMakeupTypeEyeShadow -> makeup.eyeShadowIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeEyeliner -> makeup.eyeLineIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeEyelash -> makeup.eyeLashIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeHighlight -> makeup.heightLightIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypeShadow -> makeup.shadowIntensity = 0.0
            FUSubMakeupType.FUSubMakeupTypePupil -> makeup.pupilIntensity = 0.0
        }
    }

    private fun unloadCombinationMakeup(params: Map<String, Any>, result: MethodChannel.Result) {
        renderKit.makeup = null
        renderKit.faceBeauty?.filterName = FaceBeautyFilterEnum.ORIGIN
        renderKit.faceBeauty?.filterIntensity = 0.0
    }


    override fun methods(): Map<String, (Map<String, Any>, MethodChannel.Result) -> Unit> = methods
    override fun tag(): String = "FUMakeupPlugin"

    private fun updateFilterOfCombinationMakeup(makeupModel: FUMakeupModel, makeup: Makeup?) {
        if (makeupModel.isCombined) {
            // 恢复美颜滤镜为原图效果
            renderKit.faceBeauty?.filterName = FaceBeautyFilterEnum.ORIGIN
            makeup?.filterIntensity = makeupModel.value * (makeupModel.selectedFilterLevel ?: 1.0)
        } else {
            if (makeupModel.selectedFilter.isNullOrEmpty()) {
                // 没有滤镜则使用默认滤镜"origin"
                renderKit.faceBeauty?.filterName = FaceBeautyFilterEnum.ORIGIN
                renderKit.faceBeauty?.filterIntensity = makeupModel.value
            } else {
                renderKit.faceBeauty?.filterName = makeupModel.selectedFilter
                renderKit.faceBeauty?.filterIntensity = makeupModel.value
            }
        }
    }


    private val combinationMakeups = mutableMapOf<String, FUCombinationMakeupModel>()
    private fun loadMakeupParams(makeup: Makeup, makeupModel: FUMakeupModel) {
        makeup.makeupIntensity = makeupModel.value
        if (!makeupModel.isCombined) {
            if (combinationMakeups.isEmpty()) {
                val combineJsonPath = FaceunityConfig.flutterAssetsPath("combination_makeups.json")
                val json = context.assets.open(combineJsonPath).bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val bundleName = jsonObject.getString("bundleName")
                    val bundlePath = FaceunityConfig.flutterAssetsPath("$bundleName.json")
                    combinationMakeups[jsonObject.getString("name")] = FUCombinationMakeupModel(bundlePath)
                }
            }
            val fuCombinationMakeup = combinationMakeups[makeupModel.name] ?: return
            MakeupSource.setCombinationMakeupParams(context, makeup, fuCombinationMakeup)
        }

    }

    private fun buildFUColorRGBData(color: List<Double>?): FUColorRGBData {
        if (color == null || color.size != 4) return FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        return FUColorRGBData(
            color[0] * 255,
            color[1] * 255,
            color[2] * 255,
            color[3] * 255
        )
    }
}