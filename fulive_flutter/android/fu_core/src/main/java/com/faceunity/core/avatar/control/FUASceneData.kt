package com.faceunity.core.avatar.control

import com.faceunity.core.entity.FUBundleData


/**
 *
 * DESC：
 * Created on 2021/1/15
 *
 */
data class FUASceneData(
    val id: Long,
    val controller: FUBundleData,
    val bundles: ArrayList<FUBundleData> = ArrayList(),
    val avatars: ArrayList<FUAAvatarData> = ArrayList(),
    var params: LinkedHashMap<String, ()->Unit> = LinkedHashMap(),
    var enable: Boolean = true
)