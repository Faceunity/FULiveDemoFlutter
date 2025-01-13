package com.faceunity.fuliveplugin.fulive_plugin.config

import io.flutter.FlutterInjector
import java.io.File

/**
 *
 * @author benyq
 * @date 11/10/2023
 *
 */
object FaceunityConfig {

    @JvmField
    var BLACK_LIST: String = "config" + File.separator + "blackList.json"

    val BUNDLE_AI_FACE = "model" + File.separator + "ai_face_processor.bundle"
    val BUNDLE_AI_HUMAN = "model" + File.separator + "ai_human_processor.bundle"

    val BUNDLE_FACE_BEAUTIFICATION = "graphics" + File.separator + "face_beautification.bundle"
    val BUNDLE_FACE_MAKEUP = "graphics" + File.separator + "face_makeup.bundle"
    val BUNDLE_BODY_BEAUTY = "graphics" + File.separator + "body_slim.bundle"

    //人脸置信度 标准
    const val FACE_CONFIDENCE_SCORE = 0.95f

    fun makeupCombinationBundlePath(bundleName: String): String {
        return "makeup/combination_bundle/${bundleName}.bundle"
    }

    @JvmStatic
    fun makeupItemBundlePath(bundleName: String): String {
        return "makeup/item_bundle/${bundleName}.bundle"
    }

    fun flutterAssetsPath(fileName: String): String {
        return FlutterInjector.instance().flutterLoader()
            .getLookupKeyForAsset("lib/resource/jsons/makeup/combination/${fileName}")
    }
}