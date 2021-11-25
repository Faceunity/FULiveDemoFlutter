package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute
import com.faceunity.core.avatar.model.Avatar
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：颜色配置
 * Created on 2021/5/14
 *
 */
class Color(private val avatar: Avatar) : BaseAvatarAttribute() {

    /**
     * 颜色设置缓存
     */
    val colorCache = HashMap<String, FUColorRGBData>()

    /**
     * 颜色强度缓存
     */
    val colorIntensityCache = HashMap<String, Float>()

    /**
     * 美妆道具颜色设置缓存
     */
    val componentColorCache = HashMap<FUBundleData, FUColorRGBData>()

    /**
     * 颜色name枚举
     */
    companion object {
        const val Hat = "hat_color"
        const val Skin = "skin_color"
        const val Iris = "iris_color"
        const val Glass = "glass_color"
        const val GlassFrame = "glass_frame_color"
        const val Hair = "hair_color"
        const val Eyebrow = "eyebrow_color"
        const val Beard = "beard_color"
    }

    /**
     * 设置 颜色值
     * @param name String
     * @param color RGBColorData
     */
    fun setColor(name: String, color: FUColorRGBData) {
        colorCache[name] = color
        mAvatarController.setInstanceColor(avatarId, name, color)
    }

    /**
     * 设置 颜色强度
     * @param name String
     * @param level Double
     */
    fun setColorIntensity(name: String, level: Float) {
        colorIntensityCache[name] = level
        mAvatarController.setInstanceColorIntensity(avatarId, name, level)
    }


    /**
     * 设置组件的颜色
     * @param name String
     * @param color FUColorRGBData
     */
    fun setComponentColorByName(name: String, color: FUColorRGBData) {
        var bundle: FUBundleData? = null
        avatar.components.forEach {
            if (it.name == name) {
                bundle = it
                return@forEach
            }
        }
        bundle?.let {
            componentColorCache[it] = color
            mAvatarController.fuSetInstanceFaceBeautyColor(avatarId, it, color)
            return
        }
        FULogger.e("KIT-Avatar-Color", "has not loaded component which name is $name")
    }

    /**
     * 设置组件的颜色
     * @param name String
     * @param color FUColorRGBData
     */
    fun setComponentColorByNameGL(name: String, color: FUColorRGBData) {
        var bundle: FUBundleData? = null
        avatar.components.forEach {
            if (it.name == name) {
                bundle = it
                return@forEach
            }
        }
        bundle?.let {
            componentColorCache[it] = color
            mAvatarController.fuSetInstanceFaceBeautyColor(avatarId, it, color, false)
            return
        }
        FULogger.e("KIT-Avatar-Color", "has not loaded component which name is $name")
    }


    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>, initParams: LinkedHashMap<String, () -> Unit>) {
        if (colorCache.size > 0) {
            initParams["setInstanceColor"] = {
                colorCache.forEach { (name, color) ->
                    mAvatarController.setInstanceColor(avatarId, name, color, false)
                }
            }
        }
        if (colorIntensityCache.size > 0) {
            initParams["setInstanceColorIntensity"] = {
                colorIntensityCache.forEach { (name, intensity) ->
                    mAvatarController.setInstanceColorIntensity(avatarId, name, intensity, false)
                }
            }
        }
        if (componentColorCache.size > 0) {
            params["fuSetInstanceFaceBeautyColor"] = {
                componentColorCache.forEach { (bundle, color) ->
                    mAvatarController.fuSetInstanceFaceBeautyColor(avatarId, bundle, color, false)
                }
            }
        }
        hasLoaded = true
    }

    /**
     * 数据克隆
     * @param color Color
     */
    fun clone(color: Color) {
        color.colorCache.forEach { (name, color) ->
            colorCache[name] =
                FUColorRGBData(color.red, color.green, color.blue, color.alpha)
        }
        color.colorIntensityCache.forEach { (name, intensity) ->
            colorIntensityCache[name] = intensity
        }
        color.componentColorCache.forEach { (bundle, color) ->
            componentColorCache[bundle.clone()] = color.clone()
        }
    }
}
