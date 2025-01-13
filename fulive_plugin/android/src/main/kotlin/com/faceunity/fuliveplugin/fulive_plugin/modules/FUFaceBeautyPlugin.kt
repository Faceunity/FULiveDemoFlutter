package com.faceunity.fuliveplugin.fulive_plugin.modules

import com.faceunity.core.controller.facebeauty.FaceBeautyParam
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.utils.DecimalUtils
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.utils.FULocalStorage
import io.flutter.plugin.common.MethodChannel

/**
 *
 * @author benyq
 * @date 11/10/2023
 *
 */
class FUFaceBeautyPlugin : BaseModulePlugin {

    private val methods = mapOf(
        "setSkinIntensity" to ::setSkinIntensity,
        "setShapeIntensity" to ::setShapeIntensity,
        "selectFilter" to ::selectFilter,
        "setFilterLevel" to ::setFilterLevel,
        "loadBeauty" to ::loadBeauty,
        "unloadBeauty" to ::unloadBeauty,
        "setMaximumFacesNumber" to ::setMaximumFacesNumber,
        "saveSkinToLocal" to ::saveSkinToLocal,
        "getLocalSkin" to ::getLocalSkin,
        "saveShapeToLocal" to ::saveShapeToLocal,
        "getLocalShape" to ::getLocalShape,
        "saveFilterToLocal" to ::saveFilterToLocal,
        "getLocalFilter" to ::getLocalFilter,
        "setBeautyParam" to ::setBeautyParam,
    )


    private fun setSkinIntensity(params: Map<String, Any>, result: MethodChannel.Result) {
        val value = params.getDouble("intensity") ?: return
        val type = params.getInt("type") ?: return

        when (SkinEnum.values().getOrNull(type)) {
            SkinEnum.FUBeautySkinBlurLevel -> renderKit.faceBeauty?.blurIntensity = value
            SkinEnum.FUBeautySkinColorLevel -> renderKit.faceBeauty?.colorIntensity = value
            SkinEnum.FUBeautySkinRedLevel -> renderKit.faceBeauty?.redIntensity = value
            SkinEnum.FUBeautySkinSharpen -> renderKit.faceBeauty?.sharpenIntensity = value
            SkinEnum.FUBeautySkinFaceThreed -> renderKit.faceBeauty?.faceThreeIntensity = value
            SkinEnum.FUBeautySkinEyeBright -> renderKit.faceBeauty?.eyeBrightIntensity = value
            SkinEnum.FUBeautySkinToothWhiten -> renderKit.faceBeauty?.toothIntensity = value
            SkinEnum.FUBeautySkinRemovePouchStrength -> renderKit.faceBeauty?.removePouchIntensity =
                value

            SkinEnum.FUBeautySkinRemoveNasolabialFoldsStrength -> renderKit.faceBeauty?.removeLawPatternIntensity =
                value

            SkinEnum.FUBeautySkinAntiAcneSpot -> renderKit.faceBeauty?.delspotIntensity = value

            SkinEnum.FUBeautySkinClarity -> renderKit.faceBeauty?.clarityIntensity = value
            else -> {}
        }
    }

    private fun setShapeIntensity(params: Map<String, Any>, result: MethodChannel.Result) {
        val value = params.getDouble("intensity") ?: return
        val type = params.getInt("type") ?: return
        when (ShapeEnum.values().getOrNull(type)) {
            ShapeEnum.FUBeautyShapeCheekThinning -> renderKit.faceBeauty?.cheekThinningIntensity = value
            ShapeEnum.FUBeautyShapeCheekV -> renderKit.faceBeauty?.cheekVIntensity = value
            ShapeEnum.FUBeautyShapeCheekNarrow -> renderKit.faceBeauty?.cheekNarrowIntensity = value
            ShapeEnum.FUBeautyShapeCheekShort -> renderKit.faceBeauty?.cheekShortIntensity = value
            ShapeEnum.FUBeautyShapeCheekSmall -> renderKit.faceBeauty?.cheekSmallIntensity = value
            ShapeEnum.FUBeautyShapeCheekbones -> renderKit.faceBeauty?.cheekBonesIntensity = value
            ShapeEnum.FUBeautyShapeLowerJaw -> renderKit.faceBeauty?.lowerJawIntensity = value
            ShapeEnum.FUBeautyShapeEyeEnlarging -> renderKit.faceBeauty?.eyeEnlargingIntensity = value
            ShapeEnum.FUBeautyShapeEyeCircle -> renderKit.faceBeauty?.eyeCircleIntensity = value
            ShapeEnum.FUBeautyShapeChin -> renderKit.faceBeauty?.chinIntensity = value
            ShapeEnum.FUBeautyShapeForehead -> renderKit.faceBeauty?.forHeadIntensity = value
            ShapeEnum.FUBeautyShapeNose -> renderKit.faceBeauty?.noseIntensity = value
            ShapeEnum.FUBeautyShapeMouth -> renderKit.faceBeauty?.mouthIntensity = value
            ShapeEnum.FUBeautyShapeLipThick -> renderKit.faceBeauty?.lipThickIntensity = value
            ShapeEnum.FUBeautyShapeEyeHeight -> renderKit.faceBeauty?.eyeHeightIntensity = value
            ShapeEnum.FUBeautyShapeCanthus -> renderKit.faceBeauty?.canthusIntensity = value
            ShapeEnum.FUBeautyShapeEyeLid -> renderKit.faceBeauty?.eyeLidIntensity = value
            ShapeEnum.FUBeautyShapeEyeSpace -> renderKit.faceBeauty?.eyeSpaceIntensity = value
            ShapeEnum.FUBeautyShapeEyeRotate -> renderKit.faceBeauty?.eyeRotateIntensity = value
            ShapeEnum.FUBeautyShapeLongNose -> renderKit.faceBeauty?.longNoseIntensity = value
            ShapeEnum.FUBeautyShapePhiltrum -> renderKit.faceBeauty?.philtrumIntensity = value
            ShapeEnum.FUBeautyShapeSmile -> renderKit.faceBeauty?.smileIntensity = value
            ShapeEnum.FUBeautyShapeBrowHeight -> renderKit.faceBeauty?.browHeightIntensity = value
            ShapeEnum.FUBeautyShapeBrowSpace -> renderKit.faceBeauty?.browSpaceIntensity = value
            ShapeEnum.FUBeautyShapeBrowThick -> renderKit.faceBeauty?.browThickIntensity = value
            else -> {}
        }
    }

    private fun selectFilter(params: Map<String, Any>, result: MethodChannel.Result) {
        val filterName = params.getString("key") ?: return
        if (renderKit.faceBeauty == null) {
            FaceunityKit.loadFaceBeauty()
        }
        renderKit.faceBeauty?.filterName = filterName
    }

    private fun setFilterLevel(params: Map<String, Any>, result: MethodChannel.Result) {
        val intensity = params.getDouble("level") ?: return

        renderKit.faceBeauty?.filterIntensity = intensity
    }

    private fun loadBeauty(params: Map<String, Any>, result: MethodChannel.Result) {
        FaceunityKit.loadFaceBeauty()
    }

    private fun unloadBeauty(params: Map<String, Any>, result: MethodChannel.Result) {
        renderKit.faceBeauty = null
        FaceunityKit.dropFaceUnityConfig()
    }

    private fun setMaximumFacesNumber(params: Map<String, Any>, result: MethodChannel.Result) {
        val maxFaceNumber = params.getInt("number") ?: return

        FUAIKit.getInstance().maxFaces = maxFaceNumber.coerceIn(1, 4)
    }

    private fun saveSkinToLocal(params: Map<String, Any>, result: MethodChannel.Result) {
        val json = params.getString("json")?: return
        FULocalStorage.saveFaceBeautySkin(json)
    }
    private fun getLocalSkin(params: Map<String, Any>, result: MethodChannel.Result) {
        result.success(FULocalStorage.getFaceBeautySkin())
    }

    private fun saveShapeToLocal(params: Map<String, Any>, result: MethodChannel.Result) {
        val json = params.getString("json")?: return
        FULocalStorage.saveFaceBeautyShape(json)
    }
    private fun getLocalShape(params: Map<String, Any>, result: MethodChannel.Result) {
        result.success(FULocalStorage.getFaceBeautyShape())
    }

    private fun saveFilterToLocal(params: Map<String, Any>, result: MethodChannel.Result) {
        val json = params.getString("json")?: return
        FULocalStorage.saveFaceBeautyFilter(json)
    }
    private fun getLocalFilter(params: Map<String, Any>, result: MethodChannel.Result) {
        result.success(FULocalStorage.getFaceBeautyFilter())
    }

    private fun setBeautyParam(params: Map<String, Any>, result: MethodChannel.Result) {
        val key = params.getString("key") ?: return
        val value = params.get("value") ?: return
        setBeautyParam(key, value)
    }

    private fun setBeautyParam(key: String, value: Any) {
        when(key) {
            FaceBeautyParam.ENABLE_SKIN_SEG -> renderKit.faceBeauty?.enableSkinSeg = DecimalUtils.doubleEquals(value as Double, 1.0)
        }
    }

    enum class SkinEnum {
        FUBeautySkinBlurLevel,
        FUBeautySkinColorLevel,
        FUBeautySkinRedLevel,
        FUBeautySkinSharpen,
        FUBeautySkinFaceThreed,
        FUBeautySkinEyeBright,
        FUBeautySkinToothWhiten,
        FUBeautySkinRemovePouchStrength,
        FUBeautySkinRemoveNasolabialFoldsStrength,
        FUBeautySkinAntiAcneSpot,
        FUBeautySkinClarity
    }

    enum class ShapeEnum {
        FUBeautyShapeCheekThinning,
        FUBeautyShapeCheekV,
        FUBeautyShapeCheekNarrow,
        FUBeautyShapeCheekShort,
        FUBeautyShapeCheekSmall,
        FUBeautyShapeCheekbones,
        FUBeautyShapeLowerJaw,
        FUBeautyShapeEyeEnlarging,
        FUBeautyShapeEyeCircle,
        FUBeautyShapeChin,
        FUBeautyShapeForehead,
        FUBeautyShapeNose,
        FUBeautyShapeMouth,
        FUBeautyShapeLipThick,
        FUBeautyShapeEyeHeight,
        FUBeautyShapeCanthus,
        FUBeautyShapeEyeLid,
        FUBeautyShapeEyeSpace,
        FUBeautyShapeEyeRotate,
        FUBeautyShapeLongNose,
        FUBeautyShapePhiltrum,
        FUBeautyShapeSmile,
        FUBeautyShapeBrowHeight,
        FUBeautyShapeBrowSpace,
        FUBeautyShapeBrowThick
    }

    override fun methods(): Map<String, (Map<String, Any>, MethodChannel.Result) -> Any> = methods
    override fun tag(): String = "FUFaceBeautyPlugin"
}

