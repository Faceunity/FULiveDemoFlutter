package com.faceunity.core.bundle

/**
 * Bundle数据模型
 * @property name String 名称标识
 * @property path String 路径
 * @property handle Int 句柄id
 * @property isSupportARMode Boolean 是否支持AR模式
 * @property isSupportFollowBodyMode Boolean 是否支持跟随模式
 * @constructor
 */
data class BundleData @JvmOverloads constructor(
    var name: String,
    val path: String,
    var handle: Int,
    val isSupportARMode: Boolean = true,
    val isSupportFollowBodyMode: Boolean = true
)