package com.faceunity.fuliveplugin.fulive_plugin.model

/**
 *
 * @author benyq
 * @date 12/20/2023
 *
 */

object FUSubMakeupType {
    const val FUSubMakeupTypeFoundation = 0   // 粉底
    const val FUSubMakeupTypeLip = 1         // 口红
    const val FUSubMakeupTypeBlusher = 2      // 腮红
    const val FUSubMakeupTypeEyebrow = 3     // 眉毛
    const val FUSubMakeupTypeEyeShadow = 4    // 眼影
    const val FUSubMakeupTypeEyeliner = 5     // 眼线
    const val FUSubMakeupTypeEyelash = 6      // 睫毛
    const val FUSubMakeupTypeHighlight = 7    // 高光
    const val FUSubMakeupTypeShadow = 8       // 阴影
    const val FUSubMakeupTypePupil = 9         // 美瞳
}


data class FUMakeupModel(
    val name: String,
    val bundleName: String,
    val value: Double,
    val isCombined: Boolean,
    val selectedFilter: String?,
    val selectedFilterLevel: Double?,
    // 粉底
    val foundationModel: FUSubMakeupModel? = null,
    // 口红
    val lipstickModel: FUSubMakeupModel? = null,
    // 腮红
    val blusherModel: FUSubMakeupModel? = null,
    // 眉毛
    val eyebrowModel: FUSubMakeupModel? = null,
    // 眼影
    val eyeShadowModel: FUSubMakeupModel? = null,
    // 眼线
    val eyelinerModel: FUSubMakeupModel? = null,
    // 睫毛
    val eyelashModel: FUSubMakeupModel? = null,
    // 高光
    val highlightModel: FUSubMakeupModel? = null,
    // 阴影
    val shadowModel: FUSubMakeupModel? = null,
    // 美瞳
    val pupilModel: FUSubMakeupModel? = null,
) {
    companion object {
        fun mapToMakeupModel(map: Map<String, *>): FUMakeupModel {
            val name = map["name"] as String
            val bundleName = map["bundleName"] as String
            val value = map["value"] as Double
            val selectedFilter = map["selectedFilter"] as? String
            val selectedFilterLevel = map["selectedFilterLevel"] as? Double
            val isCombined = map["isCombined"] as? Boolean ?: false
            val foundationModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["foundationModel"] as? Map<String, *>)
            val lipstickModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["lipstickModel"] as? Map<String, *>)
            val blusherModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["blusherModel"] as? Map<String, *>)
            val eyebrowModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["eyebrowModel"] as? Map<String, *>)
            val eyeShadowModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["eyeShadowModel"] as? Map<String, *>)
            val eyelinerModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["eyelinerModel"] as? Map<String, *>)
            val eyelashModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["eyelashModel"] as? Map<String, *>)
            val highlightModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["highlightModel"] as? Map<String, *>)
            val shadowModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["shadowModel"] as? Map<String, *>)
            val pupilModel =
                FUSubMakeupModel.mapToSubMakeupModel(map["pupilModel"] as? Map<String, *>)
            return FUMakeupModel(
                name,
                bundleName,
                value,
                isCombined,
                selectedFilter,
                selectedFilterLevel,
                foundationModel,
                lipstickModel,
                blusherModel,
                eyebrowModel,
                eyeShadowModel,
                eyelinerModel,
                eyelashModel,
                highlightModel,
                shadowModel,
                pupilModel
            )
        }
    }
}


data class FUSubMakeupModel(
    val type: Int,
    val value: Double,
    val color: List<Double>?,
    val index: Int? = null,
    val colors: List<List<Double>>? = null,
    val bundleName: String? = null,
    val defaultColorIndex: Int? = null,

    //口红专用
    // 是否双色口红
    val isTwoColorLipstick: Boolean? = null,
    // 口红类型
    val lipstickType: Int? = null,
    //眉毛专用
    // 是否使用眉毛变形
    val browWarpType: Int? = null,
    // 眉毛类型
    val isBrowWarp: Boolean? = null,
) {
    companion object {
        fun mapToSubMakeupModel(map: Map<String, *>?): FUSubMakeupModel? {
            if (map == null) return null
            val type = map["type"] as Int
            val value = map["value"] as Double
            val color = map["color"] as? List<Double>
            val index = map["index"] as? Int
            val colors = map["colors"] as? List<List<Double>>
            val bundleName = map["bundleName"] as? String
            val defaultColorIndex = map["defaultColorIndex"] as? Int
            val isTwoColorLipstick = map["isTwoColorLipstick"] as? Boolean
            val lipstickType = map["lipstickType"] as? Int
            val browWarpType = map["browWarpType"] as? Int
            val isBrowWarp = map["isBrowWarp"] as? Boolean
            return FUSubMakeupModel(
                type,
                value,
                color,
                index,
                colors,
                bundleName,
                defaultColorIndex,
                isTwoColorLipstick,
                lipstickType,
                browWarpType,
                isBrowWarp
            )
        }
    }
}

