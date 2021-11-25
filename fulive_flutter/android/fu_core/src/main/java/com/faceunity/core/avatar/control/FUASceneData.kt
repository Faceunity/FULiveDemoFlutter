package com.faceunity.core.avatar.control

import com.faceunity.core.entity.FUAnimationData
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
        val itemBundles: ArrayList<FUBundleData> = ArrayList(),
        val animationData: ArrayList<FUAnimationData> = ArrayList(),
        val avatars: ArrayList<FUAAvatarData> = ArrayList(),
        var params: LinkedHashMap<String, ()->Unit> = LinkedHashMap(),
        var enable: Boolean = true
)