package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute
import com.faceunity.core.entity.FUBundleData

/**
 *
 * DESC：动画控制
 * Created on 2021/1/13
 *
 */
class AvatarAnimation(private val animations: ArrayList<FUBundleData> = ArrayList()) : BaseAvatarAttribute() {

    internal fun loadParams(attributes: ArrayList<FUBundleData>) {
        attributes.addAll(animations)
    }


//
//    /**
//     * 个别设备支持的骨骼数量有限，无法在默认情况下运行骨骼动画，这时候开启这个选项
//     */
//    var enableVTF: Boolean? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["enable_vtf"] = if (it) 1.0 else 0.0
//                }
//            }
//            field = value
//        }
//
//
//    /**
//     * 开启时会把25帧的动画插值到实际渲染帧数（如60帧）从而使得动画更流畅
//     */
//    var enableInternalLerp: Boolean = true
//        set(value) {
//            doSetParam { params ->
//                params["animation_internal_lerp"] = if (value) 1.0 else 0.0
//            }
//            field = value
//        }
//
//
//    /**
//     * 添加动画
//     * @param bundle FUBundleData 动画句柄
//     */
//    fun addAnimation(bundle: FUBundleData) {
//        animations.forEach {
//            if (it.path == bundle.path) {
//                FULogger.w(TAG, "animation bundle has added bundle.name=${bundle.name}")
//                return
//            }
//        }
//        loadItemBundle(bundle)
//        animations.add(bundle)
//    }
//
//    /**
//     * 查找动画句柄
//     * @param name String 名称
//     * @return FUBundleData? 句柄
//     */
//    fun getAnimation(name: String): FUBundleData? {
//        animations.forEach {
//            if (it.name == name)
//                return it
//        }
//        FULogger.w(TAG, "animation bundle has not find bundle.name=$name")
//        return null
//    }
//
//    /**
//     * 移除动画
//     * @param bundle FUBundleData 动画句柄
//     */
//    fun removeAnimation(bundle: FUBundleData) {
//        animations.forEach {
//            if (it.path == bundle.path) {
//                removeItemBundle(it)
//                animations.remove(it)
//                return
//            }
//        }
//        FULogger.w(TAG, "animation bundle has not find bundle.name=${bundle.name}")
//    }
//
//
//    /**
//     * 移除动画
//     * @param name String 动画名称
//     */
//    fun removeAnimation(name: String) {
//        animations.forEach {
//            if (it.name == name) {
//                removeItemBundle(it)
//                animations.remove(it)
//                return
//            }
//        }
//        FULogger.w(TAG, "animation bundle has not find  bundle.name=$name")
//    }
//
//
//    /**
//     * 从头播放 动画（循环）
//     * @param name String 句柄名称
//     * @param isLoop Boolean 是否循环
//     * @param duration Double 动画的过渡时间
//     */
//    fun playAnimation(name: String, isLoop: Boolean, duration: Double) {
//        animations.forEach {
//            if (it.name == name) {
//                playAnimation(it, isLoop, duration)
//                return
//            }
//        }
//    }
//
//    /**
//     * 从头播放 动画（循环）
//     * @param bundle String 句柄
//     * @param isLoop Boolean 是否循环
//     * @param duration Double 动画的过渡时间 单位为秒
//     */
//    fun playAnimation(bundle: FUBundleData, isLoop: Boolean, duration: Double) {
//        doSetParam(bundle) { handle, params ->
//            if (isLoop) {
//                params["play_animation"] = handle
//            } else {
//                params["play_animation_once"] = handle
//            }
//            params["animation_transition_time"] = duration
//        }
//    }
//
//
//    /**
//     * 继续播放当前动画
//     */
//    fun resumeCurrentAnimation() {
//        doSetParam { params ->
//            params["start_animation"] = 1.0
//        }
//    }
//
//    /**
//     * 暂停播放当前动画
//     */
//    fun pauseCurrentAnimation() {
//        doSetParam { params ->
//            params["pause_animation"] = 1.0
//        }
//    }
//
//    /**
//     * 结束播放动画
//     */
//    fun stopCurrentAnimation() {
//        doSetParam { params ->
//            params["stop_animation"] = 1.0
//        }
//    }
//
//    /**
//     * 重置动画， 相当于先调用stop_animation再调用start_animation
//     */
//    fun resetCurrentAnimation() {
//        doSetParam { params ->
//            params["reset_animation"] = 1.0
//        }
//    }
//
//
//    /**
//     * 获取句柄为2的动画的当前进度
//     * 进度0~0.9999为第一次循环，1.0~1.9999为第二次循环，以此类推
//     * 即使play_animation_once，进度也会突破1.0，照常运行
//     * @return DoubleArray?
//     */
//    fun getAnimationProgress(bundle: FUBundleData): Double? {
//        val handle = getBundleHandle(bundle.path)
//        if (handle <= 0) {
//            FULogger.w(TAG, "getAnimationProgress failed bundle has not loaded bundle=${bundle.name} handle=$handle")
//            return null
//        }
//        val res = getItemParam("{\"name\":\"get_animation_progress\", \"anim_id\":$handle}", Double::class)
//        return res as? Double
//    }
//
//    /**
//     * 获取当前的相机动画的当前过渡进度
//     * 进度小于0时，这个相机动画没有在过渡状态
//     * 进度大于等于0时，这个相机动画在过渡中，范围为0~1.0，0为开始，1.0为结束
//     * @return Double?
//     */
//    fun getAnimationTransitionProgress(): Double? {
//        val res = getItemParam("{\"name\":\"get_animation_transition_progress\"}", Double::class)
//        return res as? Double
//    }
//
//    /**
//     * 获取句柄为2的相机动画的总帧数
//     * @return Double?
//     */
//    fun getAnimationFrameNumber(bundle: FUBundleData): Double? {
//        val handle = getBundleHandle(bundle.path)
//        if (handle <= 0) {
//            FULogger.w(TAG, "getAnimationFrameNumber failed bundle has not loaded bundle=${bundle.name} handle=$handle")
//            return null
//        }
//        val res = getItemParam("{\"name\":\"get_animation_frame_num\", \"anim_id\":$handle}", Double::class)
//        return res as? Double
//    }
//
//
//    /**
//     * 获取句柄为2的动画的LayerID
//     * @return Double?
//     */
//    fun getAnimationLayerId(bundle: FUBundleData): Double? {
//        val handle = getBundleHandle(bundle.path)
//        if (handle <= 0) {
//            FULogger.e(TAG, "getAnimationLayerId failed bundle has not loaded bundle=${bundle.name} handle=$handle")
//            return null
//        }
//        val res = getItemParam("{\"name\":\"get_animation_layerid\", \"anim_id\":$handle}", Double::class)
//        return res as? Double
//    }


}