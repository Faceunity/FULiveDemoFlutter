package com.faceunity.core.avatar.scene

import com.faceunity.core.avatar.base.BaseSceneAttribute
import com.faceunity.core.entity.FUTranslationScale


/**
 *
 * DESC：驱动AI相关配置
 * Created on 2021/5/13
 *
 */
class ProcessorConfig : BaseSceneAttribute() {

    enum class TrackScene {
        SceneFull, //全身
        SceneHalf  //半身
    }

    /**
     * Ar模式
     */
    var enableARModel: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableARMode(sceneId, it)
                }
            }

        }

    /**
     * 开启或关闭身体追踪，true表示开启，false表示关闭
     */
    var enableHumanProcessor: Boolean? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableHumanProcessor(sceneId, it)
                }
            }
        }

    /**
     * 开启或关闭面部追踪, true 表示开启，false 表示关闭
     */
    var enableFaceProcessor: Boolean? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableFaceProcessor(sceneId, it)
                }
            }
            field = value
        }


    /**
     * 设置是全身驱动，还是半身驱动， SceneFull为全身驱动，SceneHalf为半身驱动
     */
    var trackScene: TrackScene? = null
        set(value) {
            field = value
            if (hasLoaded) {
                mAvatarController.humanProcessorSet3DScene(sceneId, field == TrackScene.SceneFull)
            }
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
        enableARModel?.let {
            params["enableARMode"] = { mAvatarController.enableARMode(sceneId, it, false) }
        }
        enableHumanProcessor?.let {
            params["enableHumanProcessor"] = { mAvatarController.enableHumanProcessor(sceneId, it, false) }
        }
        enableFaceProcessor?.let {
            params["enableHumanProcessor"] = { mAvatarController.enableFaceProcessor(sceneId, it, false) }
        }
        trackScene?.let {
            params["humanProcessorSet3DScene"] = { mAvatarController.humanProcessorSet3DScene(sceneId, it == TrackScene.SceneFull, false) }
        }
        params["enableHumanFollowMode"] = { mAvatarController.enableHumanFollowMode(sceneId, enableHumanFollowMode) }
        params["humanProcessorTranslationScale"] = { mAvatarController.setHumanProcessorTranslationScale(sceneId, humanProcessorTranslationScale.x, humanProcessorTranslationScale.y, humanProcessorTranslationScale.z) }

        hasLoaded = true
    }


}