package com.faceunity.fuliveplugin.fulive_plugin.model

/**
 *
 * @author benyq
 * @date 1/9/2024
 *
 */
data class FUCombinationMakeupModel(
    val bundlePath: String,
    var jsonPathParams: LinkedHashMap<String, Any>? = null
)
