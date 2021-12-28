package com.example.fulive_plugin.entity.bean


/**
 *
 * @property key String 名称标识
 * @property type TypeEnum 类型
 * @property bundlePath FUBundleData 资源句柄
 * @property jsonPath String 参数配置路径
 * @property filterName Double 滤镜
 * @property filterIntensity Double 滤镜强度
 * @property intensity Double 强度
 * @property jsonPathParams Double 参数配置缓存
 * @constructor
 */
data class MakeupCombinationBean @JvmOverloads constructor(
    val key: String,
    val type: TypeEnum,
    val bundlePath: String?,
    val jsonPath: String,
    val filterName: String,
    var filterIntensity: Double = 0.7,
    var intensity: Double = 0.7,
    var jsonPathParams: LinkedHashMap<String, Any>? = null
) {
    enum class TypeEnum {
        TYPE_NONE, //无
        TYPE_DAILY,//日常妆，支持自定义
        TYPE_THEME,//主题妆
    }
}