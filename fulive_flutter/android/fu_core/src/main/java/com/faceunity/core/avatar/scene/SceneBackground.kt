package com.faceunity.core.avatar.scene

import com.faceunity.core.avatar.base.BaseSceneAttribute
import com.faceunity.core.entity.FUBundleData


/**
 *
 * DESC：设置背景颜色
 * Created on 2021/1/13
 *
 */
class SceneBackground : BaseSceneAttribute() {


    /**
     * 更新背景贴图参数
     */
    enum class BackgroundKeyEnum {
        size_x_tex_live, size_y_tex_live, offset_x_tex_live, offset_y_tex_live, is_foreground
    }


    /**
     * 背景Bundle
     */
    var backgroundBundle: FUBundleData? = null
        set(value) {
            if (field == null && value != null) {
                mAvatarController.loadSceneItemBundle(sceneId, value)
            } else if (field != null && value != null && field!!.path != value.path) {
                mAvatarController.replaceSceneItemBundle(sceneId, field!!, value)
            } else if (field != null && value == null) {
                mAvatarController.removeSceneItemBundle(sceneId, field!!)
            }
            field = value
        }


    internal fun loadParams(attributes: ArrayList<FUBundleData>) {
        if (backgroundBundle != null) {
            attributes.add(backgroundBundle!!)
        }
    }


//        set(value) {
//            if (value == null) {
//                field?.let {
//                    removeItemBundle(it)
//                }
//            } else {
//                loadItemBundle(value)
//            }
//            field = value


//    /**
//     * 开启enable_background_color后背景道具失效，所以如果要使用背景道具，注意关闭enable_background_color
//     */
//    var enableBackgroundColor: Boolean = false
//        set(value) {
//            doSetParam { params ->
//                params["enable_background_color"] = if (value) 1.0 else 0.0
//            }
//            field = value
//        }
//
//    /**
//     * 设置背景颜色,开启enable_background_color，只有开启后，才能通过
//     */
//    var backgroundColor: RGBColorData? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["set_background_color"] = value.toColorArray()
//                }
//            }
//            field = value
//        }
//
//    /**
//     * 更新背景贴图
//     * @param data ByteArray
//     * @param width Int
//     * @param height Int
//     */
//    fun createBackgroundTex(data: ByteArray, width: Int, height: Int) {
//        createTexForItem(backgroundBundle, "background_bg_tex", data, width, height)
//    }
//
//    /**
//     * 更新画中画贴图
//     * @param data ByteArray
//     * @param width Int
//     * @param height Int
//     */
//    fun createLiveBackgroundTex(data: ByteArray, width: Int, height: Int) {
//        createTexForItem(backgroundBundle, "background_live_tex", data, width, height)
//    }
//
//    /**
//     * 更新贴图（从RGBA Buffer创建贴图）
//     * @param data ByteArray
//     * @param width Int
//     * @param height Int
//     */
//    fun updateBackgroundTex(data: ByteArray, width: Int, height: Int) {
//        updateTexForItem(backgroundBundle, data, width, height)
//    }
//
//    fun updateBackgroundTex(key: BackgroundKeyEnum, value: Double) {
//        if (backgroundBundle == null) {
//            FULogger.w(TAG, "updateBackgroundTex failed  bundle==null")
//            return
//        }
//        doSetParam(backgroundBundle!!) { handle, params ->
//            params["{\"name\":\"global\", \"type\":\"background\", \"param\":${key.name}, \"UUID\":$handle}"] = value
//        }
//    }


}