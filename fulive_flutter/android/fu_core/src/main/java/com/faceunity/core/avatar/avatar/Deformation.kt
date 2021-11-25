package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute


/**
 *
 * DESC：骨骼捏形
 * Created on 2021/5/14
 *
 */
class Deformation : BaseAvatarAttribute() {


    /**
     * 骨骼设置缓存
     */
    val deformationCache = HashMap<String, Float>()

    /**
     * 设置某个捏形维度（channel）的系数，原则上范围是0~1，但是如果效果能接受，小于0或者大于1都是可以运行的
     * @param key String
     * @param intensity Float
     */
    fun setDeformation(key: String, intensity: Float) {
        deformationCache[key] = intensity
        mAvatarController.setInstanceDeformation(avatarId, key, intensity)
    }

    /**
     * 设置某个捏形维度（channel）的系数，原则上范围是0~1，但是如果效果能接受，小于0或者大于1都是可以运行的
     * @param key String
     * @param intensity Float
     */
    fun setDeformationGL(key: String, intensity: Float) {
        deformationCache[key] = intensity
        mAvatarController.setInstanceDeformation(avatarId, key, intensity, false)
    }

    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        if (deformationCache.isNotEmpty()) {
            params["setInstanceDeformation"] = {
                deformationCache.forEach { (key, deformation) ->
                    mAvatarController.setInstanceDeformation(avatarId, key, deformation, false)
                }
            }
        }
        hasLoaded = true
    }

    /**
     * 数据克隆
     * @param deformation Deformation
     */
    fun clone(deformation: Deformation) {
        deformation.deformationCache.forEach { (name, value) ->
            deformationCache[name] = value
        }
    }


}