package com.faceunity.core.avatar.scene

import com.faceunity.core.avatar.base.BaseSceneAttribute
import com.faceunity.core.entity.FUAnimationData
import com.faceunity.core.utils.FULogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


/**
 *
 * DESC：CameraAnimation
 * Created on 2021/5/13
 *
 */
class CameraAnimation : BaseSceneAttribute() {
    private val animations: ArrayList<FUAnimationData> = ArrayList()
    private var currentAnimation: FUAnimationData? = null

    /**
     * 1为开启，0为关闭，开启或关闭相机动画
     */
    var enableAnimation: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableCameraAnimation(sceneId, it)
                }
            }
        }

    /**
     * 获取动画列表
     * @return ArrayList<FUBundleData>
     */
    @NotNull
    fun getAnimations(): ArrayList<FUAnimationData> {
        return animations
    }

    /**
     * 获取当前动画
     * @return FUBundleData?
     */
    @Nullable
    fun getCurrentAnimation(): FUAnimationData? {
        return currentAnimation
    }

    /**
     * 根据名称查找动画句柄
     * @param name String 名称
     * @return FUAnimationData? 动画模型
     */
    @Nullable
    fun getAnimation(name: String): FUAnimationData? {
        animations.forEach {
            if (it.name == name)
                return it
        }
        FULogger.w(TAG, "animation has not find name=$name")
        return null
    }

    /**
     * 添加动画
     * @param bundle BundleHandleData 动画句柄
     * @param needBackgroundThread Boolean 是否需要插入background线程任务队列
     *
     */
    fun addAnimation(bundle: FUAnimationData, needBackgroundThread: Boolean = true) {
        animations.forEach {
            if (it.isEqual(bundle)) {
                FULogger.w(TAG, "animation bundle has added")
                return
            }
        }
        doCameraAnimationLoad(bundle, null, needBackgroundThread)
    }

    /**
     * 移除动画
     * @param bundle FUAnimationData 动画道具
     * @param needBackgroundThread Boolean 是否需要插入background线程任务队列
     */
    fun removeAnimation(bundle: FUAnimationData, needBackgroundThread: Boolean = true) {
        animations.forEach {
            if (it.isEqual(bundle)) {
                doCameraAnimationRemove(bundle, needBackgroundThread)
                return
            }
        }
        FULogger.w(TAG, "animation  has not find name=${bundle.name}")
    }

    /**
     * 移除动画
     * @param name String 动画名称
     * @param needBackgroundThread Boolean 是否需要插入background线程任务队列
     */
    fun removeAnimation(name: String, needBackgroundThread: Boolean = true) {
        animations.forEach {
            if (it.name == name) {
                doCameraAnimationRemove(it, needBackgroundThread)
                return
            }
        }
        FULogger.w(TAG, "animation bundle has not find  name=$name")
    }

    /**
     * 移除全部动画
     */
    fun removeAllAnimations() {
        animations.forEach {
            doCameraAnimationRemove(it, true)
        }
        animations.clear()
    }


    /**
     * 替换动画
     * @param name String 被替换动画名称
     * @param targetAnimation FUAnimationData 新动画
     * @param needBackgroundThread Boolean 是否需要插入background线程任务队列
     */
    fun replaceAnimation(name: String, targetAnimation: FUAnimationData, needBackgroundThread: Boolean = true) {
        var animation: FUAnimationData? = null
        animations.forEach {
            if (it.name == name) {
                animation = it
                return@forEach
            }
        }
        replaceAnimation(animation, targetAnimation, needBackgroundThread)
    }

    /**
     * 替换动画
     * @param animation FUAnimationData 被替换动画
     * @param targetAnimation FUAnimationData 新动画
     * @param needBackgroundThread Boolean 是否需要插入background线程任务队列
     */
    @JvmOverloads
    fun replaceAnimation(animation: FUAnimationData?, targetAnimation: FUAnimationData?, needBackgroundThread: Boolean = true) {
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
            doCameraAnimationReplace(animation, targetAnimation, needBackgroundThread)
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
            doCameraAnimationLoad(bundle, isLoop, true)
        } else {
            doPlayAnimation(bundle, isLoop)
        }
        currentAnimation = bundle
    }

    /**
     * 继续播放当前动画
     */
    fun startCurrentAnimation() {
        mAvatarController.startCameraAnimation(sceneId)
    }


    /**
     * 暂停播放当前动画
     */
    fun pauseCurrentAnimation() {
        mAvatarController.pauseCameraAnimation(sceneId)
    }


    /**
     * 重置动画， 相当于先调用stop_animation再调用start_animation
     */
    fun resetCurrentAnimation() {
        mAvatarController.resetCameraAnimation(sceneId)
    }


    /**
     * 设置相机动画的过渡时间，单位为秒
     */
    var animationTransitionTime: Float? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setCameraAnimationTransitionTime(sceneId, it)
                }
            }
            field = value

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
                    mAvatarController.enableCameraAnimationInternalLerp(sceneId, it)
                }
            }
            field = value
        }

    /**
     * 获取句柄为2的相机动画的总帧数
     * @return Double?
     */
    fun getAnimationFrameNumber(data: FUAnimationData): Int {
        return mAvatarController.getCameraAnimationFrameNumber(sceneId, data.animation)
    }

    /**
     * 获取句柄为2的动画的当前进度
     * 进度0~0.9999为第一次循环，1.0~1.9999为第二次循环，以此类推
     * 即使play_animation_once，进度也会突破1.0，照常运行
     * @return Double?
     */
    fun getAnimationProgress(data: FUAnimationData): Float {
        return mAvatarController.getCameraAnimationProgress(sceneId, data.animation)
    }

    /**
     * 获取当前的相机动画的当前过渡进度
     * 进度小于0时，这个相机动画没有在过渡状态
     * 进度大于等于0时，这个相机动画在过渡中，范围为0~1.0，0为开始，1.0为结束
     * @return Double?
     */
    fun getCurrentAnimationTransitionProgress(): Float {
        return mAvatarController.getCameraAnimationTransitionProgress(sceneId)
    }


    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>, bundles: ArrayList<FUAnimationData>) {
        enableAnimation?.let {
            params["enableCameraAnimation"] = { mAvatarController.enableCameraAnimation(sceneId, it, false) }
        }
        enableInternalLerp?.let {
            params["enableCameraAnimationInternalLerp"] = { mAvatarController.enableCameraAnimationInternalLerp(sceneId, it, false) }
        }
        animationTransitionTime?.let {
            params["setCameraAnimationTransitionTime"] = { mAvatarController.setCameraAnimationTransitionTime(sceneId, it, false) }
        }
        bundles.addAll(animations)
        hasLoaded = true
    }


    //region AvatarController 调用

    /**
     * 执行道具添加
     * @param data FUBaseAnimationData
     */
    private fun doCameraAnimationLoad(data: FUAnimationData, isLoop: Boolean? = null, needBackgroundThread: Boolean = true) {
        animations.add(data)
        if (!hasLoaded) {
            return
        }
        mAvatarController.loadCameraAnimationData(sceneId, data, isLoop, needBackgroundThread)
    }

    /**
     * 执行道具移动
     * @param data FUBaseAnimationData
     */
    private fun doCameraAnimationRemove(data: FUAnimationData, needBackgroundThread: Boolean) {
        animations.remove(data)
        if (!hasLoaded) {
            return
        }
        mAvatarController.removeCameraAnimationData(sceneId, data,needBackgroundThread)
    }

    /**
     * 执行道具替换
     * @param data FUAnimationData 原始动画道具
     * @param targetData FUAnimationData 目标动画道具
     */
    private fun doCameraAnimationReplace(data: FUAnimationData, targetData: FUAnimationData, needBackgroundThread: Boolean) {
        animations.remove(data)
        animations.add(targetData)
        if (!hasLoaded) {
            return
        }
        mAvatarController.replaceCameraAnimationData(sceneId, data, targetData,needBackgroundThread)
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
        mAvatarController.playCameraAnimation(sceneId, data, isLooper)
    }
    //endregion AvatarController
}