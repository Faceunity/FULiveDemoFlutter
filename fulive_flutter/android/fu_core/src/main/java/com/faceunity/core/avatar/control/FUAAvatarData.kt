package com.faceunity.core.avatar.control

import com.faceunity.core.entity.FUAnimationData
import com.faceunity.core.entity.FUBundleData

data class FUAAvatarData(
        val id: Long,
        val itemBundles: ArrayList<FUBundleData> = ArrayList(),
        val animationData: ArrayList<FUAnimationData> = ArrayList(),
        val param: LinkedHashMap<String, () -> Unit> = LinkedHashMap()
)