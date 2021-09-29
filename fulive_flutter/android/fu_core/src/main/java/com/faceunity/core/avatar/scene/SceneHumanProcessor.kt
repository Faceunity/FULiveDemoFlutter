package com.faceunity.core.avatar.scene

import com.faceunity.core.avatar.base.BaseSceneAttribute
import com.faceunity.core.entity.FUTranslationScale

/**
 *
 * DESC：人体AI驱动
 * Created on 2021/1/14
 *
 */
class SceneHumanProcessor : BaseSceneAttribute() {

    enum class TrackScene {
        SceneFull, //全身
        SceneHalf  //半身
    }

    /**
     * 开启或关闭身体追踪，true表示开启，false表示关闭
     */
    var enableHumanProcessor: Boolean = false
        set(value) {
            field = value
            mAvatarController.enableHumanProcessor(sceneId, value)
        }

    /**
     * 设置是全身驱动，还是半身驱动， SceneFull为全身驱动，SceneHalf为半身驱动
     */
    var trackScene: TrackScene? = null
        set(value) {
            field = value
            mAvatarController.humanProcessorSet3DScene(sceneId, field == TrackScene.SceneFull)
        }

    /**
     * 开关或关闭avatar跟随人体，true开启，false关闭
     */
    var enableHumanFollowMode: Boolean = false
        set(value) {
            field = value
            mAvatarController.enableHumanFollowMode(sceneId, value)
        }

    /**
     * 限制avatar活动范围
     */
    var humanProcessorTranslationScale: FUTranslationScale = FUTranslationScale(0.0f, 0.0f, 0.0f)
        set(value) {
            value?.let {
                field = value
                mAvatarController.setHumanProcessorTranslationScale(sceneId, humanProcessorTranslationScale.x, humanProcessorTranslationScale.y, humanProcessorTranslationScale.z)
            }
        }

    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        params["enableHumanProcessor"] = { mAvatarController.enableHumanProcessor(sceneId, enableHumanProcessor) }
        params["humanProcessorSet3DScene"] = { mAvatarController.humanProcessorSet3DScene(sceneId, trackScene == TrackScene.SceneFull) }
        params["enableHumanFollowMode"] = { mAvatarController.enableHumanFollowMode(sceneId, enableHumanFollowMode) }
        params["humanProcessorTranslationScale"] = { mAvatarController.setHumanProcessorTranslationScale(sceneId, humanProcessorTranslationScale.x, humanProcessorTranslationScale.y, humanProcessorTranslationScale.z) }
    }


    enum class HumanTrackStatus(val vaule: Double) {
        HUMAN_NO_BODY(0.0),
        HUMAN_HALF_LESS_BODY(1.0),
        HUMAN_HALF_BODY(2.0),
        HUMAN_HALF_MORE_BODY(3.0),
        HUMAN_FULL_BODY(4.0)
    }


//    /**
//     * 设置是否开启跟随模式：true表示跟随，false表示不跟随
//     */
//    var enableTrackFollow: Boolean? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["human_3d_track_is_follow"] = if (value) 1.0 else 0.0
//                }
//                field = value
//            }
//        }
//
//
//    /**
//     * 如果使用跟随模式，可以通过参数human_3d_track_render_fov设置渲染的fov大小，单位是度
//     */
//    var trackRenderFov: Double? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["human_3d_track_render_fov"] = it
//                }
//            }
//            field = value
//        }
//

//
//    /**
//     * 设置全身驱动跟随模式下模型缩放
//     */
//    var trackFullAvatarScale: Double? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["human_3d_track_set_fullbody_avatar_scale"] = it
//                }
//            }
//            field = value
//        }
//
//
//    /**
//     * 设置半身驱动跟随模式下模型缩放
//     */
//    var trackHalfAvatarScale: Double? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["human_3d_track_set_halfbody_avatar_scale"] = it
//                }
//                field = value
//            }
//        }
//
//    /**
//     * 设置半身驱动跟随模式下，X轴，Y轴方向上的偏移
//     */
//    var trackHalfAvatarOffset: FaceUnity2DCoordinate? = null
//        set(value) {
//            value?.let {
//                doSetParam { params ->
//                    params["human_3d_track_set_halfbody_global_offset"] = it.toDataArray()
//                }
//            }
//            field = value
//        }
//
//
//    /**
//     * 设置手势追踪的动画过渡时间， 默认值为0.1（秒）
//     */
//    var gestureTrackTransitionAnimTime: Double = 0.1
//        set(value) {
//            doSetParam { params ->
//                params["anim_transition_max_time_gesture_track"] = value
//            }
//            field = value
//        }
//
//    /**
//     * 设置在身体动画和身体追踪数据之间过渡的时间，默认值为0.5（秒）
//     */
//    var humanTrackTransitionAnimTime: Double = 0.5
//        set(value) {
//            doSetParam { params ->
//                params["anim_transition_max_time_human_3d_track"] = value
//            }
//            field = value
//        }
//
//
//    /**
//     * 设置在面部追踪数据和身体追踪数据内的面部数据之间过渡的时间，默认值为1.0（秒）
//     */
//    var faceTrackTransitionAnimTime: Double = 1.0
//        set(value) {
//            doSetParam { params ->
//                params["anim_transition_max_time_face_track"] = value
//            }
//            field = value
//        }
//
//
//    /**
//     * 获取身体追踪的状态
//     * @return HumanTrackStatus?
//     */
//    fun getHumanStatus(): HumanTrackStatus? {
//        return when (getItemParam("human_status", Double::class) as? Double) {
//            0.0 -> HumanTrackStatus.HUMAN_NO_BODY
//            1.0 -> HumanTrackStatus.HUMAN_HALF_LESS_BODY
//            2.0 -> HumanTrackStatus.HUMAN_HALF_BODY
//            3.0 -> HumanTrackStatus.HUMAN_HALF_MORE_BODY
//            4.0 -> HumanTrackStatus.HUMAN_FULL_BODY
//            else -> null
//        }
//    }
//
//    /**
//     * 返回[x, y], x表示左手手势，y表示右手手势
//     */
//    fun getHumanTrackGestureId(): DoubleArray? {
//        val res = getItemParam("human_track_gesture_id", DoubleArray::class)
//        return res as? DoubleArray
//    }
//
//
//    internal fun loadParams(params: LinkedHashMap<String, Any>) {
//        enableHumanProcessor?.let {
//            params["enable_human_processor"] = if (it) 1.0 else 0.0
//        }
//        enableTrackFollow?.let {
//            params["human_3d_track_is_follow"] = if (it) 1.0 else 0.0
//        }
//        trackRenderFov?.let {
//            params["human_3d_track_render_fov"] = it
//        }
//        trackScene?.let {
//            params["human_3d_track_set_scene"] = if (it == TrackScene.SceneFull) 1.0 else 0.0
//        }
//        trackFullAvatarScale?.let {
//            params["human_3d_track_set_fullbody_avatar_scale"] = it
//        }
//        trackHalfAvatarScale?.let {
//            params["human_3d_track_set_halfbody_avatar_scale"] = it
//        }
//        trackHalfAvatarOffset?.let {
//            params["human_3d_track_set_halfbody_global_offset"] = it.toDataArray()
//        }
//        if (gestureTrackTransitionAnimTime != 0.1) {
//            params["anim_transition_max_time_gesture_track"] = gestureTrackTransitionAnimTime
//        }
//        if (humanTrackTransitionAnimTime != 0.5) {
//            params["anim_transition_max_time_human_3d_track"] = humanTrackTransitionAnimTime
//        }
//        if (faceTrackTransitionAnimTime != 1.0) {
//            params["anim_transition_max_time_face_track"] = faceTrackTransitionAnimTime
//        }
//    }


}
