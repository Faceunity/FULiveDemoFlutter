package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute
import com.faceunity.core.entity.*
import com.faceunity.core.utils.FULogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 *
 * DESC：动画控制
 * Created on 2021/1/13
 *
 */
class Animation : BaseAvatarAttribute() {

    private val animations: ArrayList<FUAnimationData> = ArrayList()
    private var currentAnimation: FUAnimationData? = null

    /**
     * 获取动画列表
     * @return ArrayList<FUBaseAnimationData>
     */
    @NotNull
    fun getAnimations(): ArrayList<FUAnimationData> {
        return animations
    }

    /**
     * 获取当前动画
     * @return FUBaseAnimationData?
     */
    @Nullable
    fun getCurrentAnimation(): FUAnimationData? {
        return currentAnimation
    }


    /**
     * 查找动画句柄
     * @param name String 名称
     * @return FUBaseAnimationData? 动画道具
     */
    @Nullable
    fun getAnimation(name: String): FUAnimationData? {
        animations.forEach {
            if (it.name == name)
                return it
        }
        FULogger.w(TAG, "animation  has not find name=$name")
        return null
    }


    /**
     * 添加动画
     * @param bundle FUBaseAnimationData 动画道具
     */
    fun addAnimation(bundle: FUAnimationData) {
        animations.forEach {
            if (it.isEqual(bundle)) {
                FULogger.w(TAG, "animation  has added")
                return
            }
        }
        doAvatarAnimationLoad(bundle)
    }


    /**
     * 移除动画
     * @param bundle FUBaseAnimationData 动画道具
     */
    fun removeAnimation(bundle: FUAnimationData) {
        animations.forEach {
            if (it.isEqual(bundle)) {
                doAvatarAnimationRemove(bundle)
                return
            }
        }
        FULogger.w(TAG, "animation has not find name=${bundle.name}")
    }

    /**
     * 移除动画
     * @param name String 动画名称
     */
    fun removeAnimation(name: String) {
        animations.forEach {
            if (it.name == name) {
                doAvatarAnimationRemove(it)
                return
            }
        }
        FULogger.w(TAG, "animation bundle has not find  name=$name")
    }

    /**
     * 移除动画
     */
    fun removeAllAnimations() {
        animations.forEach {
            doAvatarAnimationRemove(it)
        }
        animations.clear()
    }

    /**
     * 替换动画
     * @param name String 被替换动画名称
     * @param targetAnimation FUAnimationData 新动画
     */
    fun replaceAnimation(name: String, targetAnimation: FUAnimationData) {
        var animation: FUAnimationData? = null
        animations.forEach {
            if (it.name == name) {
                animation = it
                return@forEach
            }
        }
        replaceAnimation(animation, targetAnimation)
    }

    /**
     * 替换动画
     * @param animation FUAnimationData 被替换动画
     * @param targetAnimation FUAnimationData 新动画
     */
    fun replaceAnimation(animation: FUAnimationData?, targetAnimation: FUAnimationData?) {
        if (animation == null && targetAnimation == null) {
            FULogger.w(TAG, "animation and targetAnimation is null")
        } else if (animation == null && targetAnimation != null) {
            addAnimation(targetAnimation)
        } else if (animation != null && targetAnimation == null) {
            removeAnimation(animation)
        } else if (animation != null && targetAnimation != null) {
            if (animation.isEqual(targetAnimation)) {
                FULogger.w(TAG, "animation and targetAnimation  is same")
                return
            }
            doAvatarAnimationReplace(animation, targetAnimation)
        }
    }


    /**
     * 从头播放 动画（循环）
     * @param name String 句柄名称
     * @param isLoop Boolean 是否循环
     */
    fun playAnimation(name: String, isLoop: Boolean) {
        val animationData = getAnimation(name)
        animationData?.let {
            doPlayAnimation(animationData, isLoop)
            return
        }
        FULogger.w(TAG, "animation bundle has not find name=$name")
    }


    /**
     * 从头播放 动画（循环）
     * @param bundle String 动画道具
     * @param isLoop Boolean 是否循环
     */
    fun playAnimation(bundle: FUAnimationData, isLoop: Boolean) {
        var hasAnimationLoaded = false
        animations.forEach {
            if (it.isEqual(bundle)) {
                hasAnimationLoaded = true
                return@forEach
            }
        }
        if (!hasAnimationLoaded) {
            doAvatarAnimationLoad(bundle, isLoop)
        } else {
            doPlayAnimation(bundle, isLoop)
        }
        currentAnimation = bundle
    }


    /**
     * 继续播放当前动画
     */
    fun startCurrentAnimation() {
        mAvatarController.startInstanceAnimation(avatarId)
    }


    /**
     * 暂停播放当前动画
     */
    fun pauseCurrentAnimation() {
        mAvatarController.pauseInstanceAnimation(avatarId)
    }


    /**
     * 结束播放动画
     */
    fun stopCurrentAnimation() {
        mAvatarController.stopInstanceAnimation(avatarId)
    }

    /**
     * 重置动画， 相当于先调用stop_animation再调用start_animation
     */
    fun resetCurrentAnimation() {
        mAvatarController.resetInstanceAnimation(avatarId)
    }


    /**
     * 设置动画的过渡时间，单位为秒
     * @param time Float
     */
    fun setAnimationTransitionTime(time: Float) {
        mAvatarController.setInstanceAnimationTransitionTime(avatarId, time)
    }


    /**
     * 1为开启，0为关闭，开启时会把25帧的相机动画插值到实际渲染帧数（如60帧）从而使得相机动画更流畅，
     * 但是某些情况下不适合插值，如有闪现操作等不希望插值的相机动画，可以主动关闭。
     * 开启时会把25帧的动画插值到实际渲染帧数（如60帧）从而使得动画更流畅
     * 这个开关并不会对已加载的相机动画产生效果，已加载的相机动画无法实时改变帧间插值
     * 开启或关闭这个开关后再加载的相机动画，就会产生上述效果
     */
    var enableInternalLerp: Boolean? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceAnimationInternalLerp(avatarId, it)
                }
            }
            field = value
        }

    /**
     * 获取句柄为2的相机动画的总帧数
     * @return Int
     */
    fun getAnimationFrameNumber(data: FUAnimationData): Int {
        return mAvatarController.getInstanceAnimationFrameNumber(avatarId, data.animation)
    }

    /**
     * 获取句柄为2的动画的当前进度
     * 进度0~0.9999为第一次循环，1.0~1.9999为第二次循环，以此类推
     * 即使play_animation_once，进度也会突破1.0，照常运行
     * @return Int
     */
    fun getAnimationProgress(data: FUAnimationData): Float {
        return mAvatarController.getInstanceAnimationProgress(avatarId, data.animation)
    }

    /**
     * 获取当前的相机动画的当前过渡进度
     * 进度小于0时，这个相机动画没有在过渡状态
     * 进度大于等于0时，这个相机动画在过渡中，范围为0~1.0，0为开始，1.0为结束
     * @return Int
     */
    fun getAnimationTransitionProgress(data: FUAnimationData): Float {
        return mAvatarController.getInstanceAnimationTransitionProgress(avatarId, data.animation)
    }


    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     * @param bundles ArrayList<FUBaseAnimationData>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>, bundles: ArrayList<FUAnimationData>) {
        enableInternalLerp?.let {
            params["enableInternalLerp"] = { mAvatarController.enableInstanceAnimationInternalLerp(avatarId, it, false) }
        }
        bundles.addAll(animations)
        hasLoaded = true
    }


    /**
     * 数据克隆
     * @param animation Animation
     */
    internal fun clone(animation: Animation) {
        animation.getAnimations().forEach {
            animations.add(it.clone())
        }
        enableInternalLerp = animation.enableInternalLerp
    }

    //region AvatarController 调用

    /**
     * 执行道具添加
     * @param data FUBaseAnimationData
     */
    private fun doAvatarAnimationLoad(data: FUAnimationData, isLoop: Boolean? = null) {
        animations.add(data)
        if (!hasLoaded) {
            return
        }
        mAvatarController.loadAvatarAnimationData(avatarId, data, isLoop)
    }

    /**
     * 执行道具移动
     * @param data FUBaseAnimationData
     */
    private fun doAvatarAnimationRemove(data: FUAnimationData) {
        animations.remove(data)
        if (!hasLoaded) {
            return
        }
        mAvatarController.removeAvatarAnimationData(avatarId, data)
    }

    /**
     * 执行道具替换
     * @param data FUAnimationData 原始动画道具
     * @param targetData FUAnimationData 目标动画道具
     */
    private fun doAvatarAnimationReplace(data: FUAnimationData, targetData: FUAnimationData) {
        animations.remove(data)
        animations.add(targetData)
        if (!hasLoaded) {
            return
        }
        mAvatarController.replaceAvatarAnimationData(avatarId, data, targetData)
    }


    /**
     * 执行播放动画
     * @param data FUBaseAnimationData
     * @param isLooper Boolean
     */
    private fun doPlayAnimation(data: FUAnimationData, isLooper: Boolean) {
        if (!hasLoaded) {
            return
        }
        mAvatarController.playInstanceAnimation(avatarId, data, isLooper)
    }


    //endregion AvatarController


}