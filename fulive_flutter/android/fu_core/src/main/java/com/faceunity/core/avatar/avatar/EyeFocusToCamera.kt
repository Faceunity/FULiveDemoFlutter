package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute


/**
 *
 * DESC：眼睛注视相机
 * Created on 2021/5/14
 *
 */
class EyeFocusToCamera : BaseAvatarAttribute() {


    /**
     * 开启眼镜注释功能
     */
    var enableEyeFocusToCamera: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceFocusEyeToCamera(avatarId, it)
                }
            }

        }

    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        enableEyeFocusToCamera?.let {
            params["enableInstanceFocusEyeToCamera"] = { mAvatarController.enableInstanceFocusEyeToCamera(avatarId, it, false) }
        }
        hasLoaded = true
    }

    /**
     * 数据克隆
     * @param eyeFocusToCamera EyeFocusToCamera
     */
    fun clone(eyeFocusToCamera: EyeFocusToCamera) {
        enableEyeFocusToCamera = eyeFocusToCamera.enableEyeFocusToCamera
    }
}