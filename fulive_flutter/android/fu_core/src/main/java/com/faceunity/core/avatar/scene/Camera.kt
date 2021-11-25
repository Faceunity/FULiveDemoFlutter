package com.faceunity.core.avatar.scene

import com.faceunity.core.avatar.base.BaseSceneAttribute


/**
 *
 * DESC：Camera
 * Created on 2021/5/13
 *
 */

class Camera : BaseSceneAttribute() {


    /**
     *  是否渲染输入数据
     */
    var enableRenderCamera: Boolean? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableRenderCamera(sceneId, it)
                }
            }
            field = value

        }


    /**
     * 控制相机镜头的fov，默认值为8.6，取值范围0~90，单位为度（角度）
     */
    var renderFov: Float? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setProjectionMatrixFov(sceneId, it)
                }
            }
            field = value
        }

    /**
     * 控制相机镜头的渲染大小，默认值为100，单位为厘米，和模型在一个坐标系下
     */
    var renderOrthSize: Float? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setProjectionMatrixOrthoSize(sceneId, it)
                }
            }
            field = value

        }

    /**
     * 相机近平面，默认值为30，单位为厘米，和模型在一个坐标系下
     * 离相机距离小于这个值的模型不会被显示
     */
    var znear: Float? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setProjectionMatrixZnear(sceneId, it)
                }
            }
            field = value

        }

    /**
     *相机远平面，默认值为6000，单位为厘米，和模型在一个坐标系下
     *离相机距离大于这个值的模型不会被显示
     */
    var zfar: Float? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setProjectionMatrixZfar(sceneId, it)
                }
            }
            field = value

        }


    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        enableRenderCamera?.let {
            params["enableRenderCamera"] = { mAvatarController.enableRenderCamera(sceneId, it, false) }
        }
        renderFov?.let {
            params["setProjectionMatrixFov"] = { mAvatarController.setProjectionMatrixFov(sceneId, it, false) }
        }
        renderOrthSize?.let {
            params["setProjectionMatrixFov"] = { mAvatarController.setProjectionMatrixOrthoSize(sceneId, it, false) }
        }
        znear?.let {
            params["setProjectionMatrixFov"] = { mAvatarController.setProjectionMatrixZnear(sceneId, it, false) }
        }
        zfar?.let {
            params["setProjectionMatrixFov"] = { mAvatarController.setProjectionMatrixZfar(sceneId, it, false) }
        }
        hasLoaded = true
    }


}