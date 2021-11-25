package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute


/**
 *
 * DESC：DynamicBone控制
 * Created on 2021/5/14
 *
 */
class DynamicBone : BaseAvatarAttribute() {

    /**
     * 开启的时候已加载的物理会生效，同时加载新的带物理的bundle也会生效，
     * 关闭的时候已加载的物理会停止生效，但不会清除缓存（这时候再次开启物理会在此生效），
     * 这时加载带物理的bundle不会生效，且不会产生缓存，
     * 即关闭后加载的带物理的bundle，即时再次开启，物理也不会生效，需要重新加载
     */
    var enableDynamicBone: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceDynamicBone(avatarId, it)
                }
            }
        }

    /**
     * 开启的时候移动角色的值会被设进骨骼系统，这时候带DynamicBone的模型会有相关效果
     * 默认关闭
     * 每个角色的这个值都是独立的
     */
    var enableModelMatToBone: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceModelMatToBone(avatarId, it)
                }
            }
        }

    /**
     * 开启的时候人物移动或者动画都不会使被DynamicBone控制的骨骼产生位移，关闭的时候再开始计算DynamicBone的效果。
     * 人物需要快速移动/旋转的时候强烈建议开启这个，移动/旋转结束后再关闭，可以防止穿模和闪烁。
     */
    var enableTeleportMode: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceDynamicBoneTeleportMode(avatarId, it)
                }
            }

        }

    /**
     * 1为开启，0为关闭，根骨骼（美术编辑DynamicBone时指定的根骨骼）限速开关，开启时根骨骼移动速度超过一定值就会自动复位刚体
     *  默认值和限速值由美术编辑并保存在相应bundle中
     */
    var enableRootTranslateSpeedLimitMode: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceDynamicBoneRootTranslationSpeedLimitMode(avatarId, it)
                }
            }

        }

    /**
     * 1为开启，0为关闭，根骨骼（美术编辑DynamicBone时指定的根骨骼）限速开关，开启时根骨骼旋转速度超过一定值就会自动复位刚体
     * 默认值和限速值由美术编辑并保存在相应bundle中
     */
    var enableRootRotateSpeedLimitMode: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceDynamicBoneRootRotationSpeedLimitMode(avatarId, it)
                }
            }
        }

    /**
     * 有些时候快速移动/旋转或者大幅度的动画都会导致某些刚体被卡住，这时候可以设置以下两个参数来恢复刚体默认位置，这两个接口的参数没有意义，只是占位
     * Refresh是会重建整个DynamixBone，消耗巨大，除非不得已否则慎用
     */
    @JvmOverloads
    fun refresh(isImmediate: Boolean = true) {
        mAvatarController.refreshInstanceDynamicBone(avatarId, isImmediate)
    }

    /**
     * Reset会重置刚体位置，消耗较小，推荐用这个，这个解决不了再用Refresh
     */
    @JvmOverloads
    fun reset(isImmediate: Boolean = true) {
        mAvatarController.resetInstanceDynamicBone(avatarId, isImmediate)
    }


    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        enableDynamicBone?.let {
            params["enableInstanceDynamicBone"] = { mAvatarController.enableInstanceDynamicBone(avatarId, it, false) }
        }
        enableModelMatToBone?.let {
            params["enableInstanceModelMatToBone"] = { mAvatarController.enableInstanceModelMatToBone(avatarId, it, false) }
        }
        enableTeleportMode?.let {
            params["enableInstanceDynamicBoneTeleportMode"] = { mAvatarController.enableInstanceDynamicBoneTeleportMode(avatarId, it, false) }
        }

        enableRootTranslateSpeedLimitMode?.let {
            params["enableInstanceDynamicBoneRootTranslationSpeedLimitMode"] = { mAvatarController.enableInstanceDynamicBoneRootTranslationSpeedLimitMode(avatarId, it, false) }
        }
        enableRootRotateSpeedLimitMode?.let {
            params["enableInstanceDynamicBoneRootRotationSpeedLimitMode"] = { mAvatarController.enableInstanceDynamicBoneRootRotationSpeedLimitMode(avatarId, it, false) }
        }
        hasLoaded = true

    }

    /**
     * 数据克隆
     * @param dynamicBone DynamicBone
     */
    fun clone(dynamicBone: DynamicBone) {
        enableDynamicBone = dynamicBone.enableDynamicBone
        enableModelMatToBone = dynamicBone.enableModelMatToBone
        enableTeleportMode = dynamicBone.enableTeleportMode
        enableRootTranslateSpeedLimitMode = dynamicBone.enableRootTranslateSpeedLimitMode
        enableRootRotateSpeedLimitMode = dynamicBone.enableRootRotateSpeedLimitMode
    }

}