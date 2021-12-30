package com.faceunity.core.avatar.control

import com.faceunity.core.entity.FUBundleData

data class FUAAvatarData(
    val id: Long,
    val itemBundles: ArrayList<FUBundleData> = ArrayList(),
    val initParam: LinkedHashMap<String, () -> Unit> = LinkedHashMap(),
    val param: LinkedHashMap<String, () -> Unit> = LinkedHashMap()
)