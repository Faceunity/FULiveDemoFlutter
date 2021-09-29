package com.faceunity.core.entity


/**
 * DESC：功能数据单元
 * @property bundle BundleHandleData?  控制句柄 为空默认移除该功能
 * @property param LinkedHashMap<String, Any>   参数配置
 * @property remark Any  预留字段
 * @constructor
 */
data class FUFeaturesData @JvmOverloads constructor(
    val bundle: FUBundleData?,
    val param: LinkedHashMap<String, Any> = LinkedHashMap(),
    val enable: Boolean = true,
    val remark: Any? = null,
    val id: Long = 0L
)