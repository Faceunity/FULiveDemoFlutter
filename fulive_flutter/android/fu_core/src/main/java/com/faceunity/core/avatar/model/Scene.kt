package com.faceunity.core.avatar.model

import com.faceunity.core.avatar.base.BaseSceneAttribute
import com.faceunity.core.avatar.control.FUAAvatarData
import com.faceunity.core.avatar.control.FUASceneData
import com.faceunity.core.avatar.scene.Camera
import com.faceunity.core.avatar.scene.CameraAnimation
import com.faceunity.core.avatar.scene.ProcessorConfig
import com.faceunity.core.entity.FUAnimationData
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：
 * Created on 2021/1/15
 *
 */
class Scene(private val controlBundle: FUBundleData, private val avatarConfig: FUBundleData) : BaseSceneAttribute() {

    private val avatars: ArrayList<Avatar> = ArrayList()

    /**
     * Camera 相关能力配置
     */
    @JvmField
    val camera: Camera = Camera()

    /**
     * 相机动画CameraAnimation
     */
    @JvmField
    val cameraAnimation: CameraAnimation = CameraAnimation()

    /**
     * 驱动配置
     */
    @JvmField
    val processorConfig: ProcessorConfig = ProcessorConfig()


    init {
        sceneId = System.nanoTime()
        camera.sceneId = sceneId
        cameraAnimation.sceneId = sceneId
        processorConfig.sceneId = sceneId
    }


    //region 背景
    /**
     * 背景道具
     */
    var backgroundBundle: FUBundleData? = null
        set(value) {
            if (hasLoaded) {
                if (field == null && value != null) {
                    mAvatarController.loadSceneItemBundle(sceneId, value)
                } else if (field != null && value != null && field!!.path != value.path) {
                    mAvatarController.replaceSceneItemBundle(sceneId, field!!, value)
                } else if (field != null && value == null) {
                    mAvatarController.removeSceneItemBundle(sceneId, field!!)
                }
            }
            field = value
        }

    /**
     * 背景颜色，如果设置了背景颜色，背景道具自动失效
     */
    var backgroundColor: FUColorRGBData? = null
        set(value) {
            field = value
            if (hasLoaded) {
                if (value != null) {
                    mAvatarController.enableBackgroundColor(sceneId, true)
                    mAvatarController.setBackgroundColor(sceneId, value)
                } else {
                    mAvatarController.enableBackgroundColor(sceneId, false)
                }
            }
        }
    //endregion 背景
    //region shadow

    /**
     * 开启阴影，value = 1.0 代表开启，value = 0.0 代表关闭
     */
    var enableShadow: Boolean? = null
        set(value) {
            field = value
            if (hasLoaded) {
                value?.let {
                    mAvatarController.enableShadow(sceneId, it)
                }
            }


        }

    /**
     * 设置阴影等级
     */
    var shadowPCFLevel: Int? = null
        set(value) {
            field = value
            if (hasLoaded) {
                value?.let {
                    mAvatarController.setInstanceShadowPCFLevel(sceneId, it)
                }
            }
        }
    //endregion shaow
    //region Lighting

    /**
     * 设置是否开启低质量灯光的渲染
     */
    var enableLowQualityLighting: Boolean? = null
        set(value) {
            field = value
            if (hasLoaded) {
                value?.let {
                    mAvatarController.enableLowQualityLighting(sceneId, it)
                }
            }

        }

    /**
     * 背景道具
     */
    var lightingBundle: FUBundleData? = null
        set(value) {
            if (hasLoaded) {
                if (field == null && value != null) {
                    mAvatarController.loadSceneItemBundle(sceneId, value)
                } else if (field != null && value != null && field!!.path != value.path) {
                    mAvatarController.replaceSceneItemBundle(sceneId, field!!, value)
                } else if (field != null && value == null) {
                    mAvatarController.removeSceneItemBundle(sceneId, field!!)
                }
            }
            field = value
        }
    //endregion Lighting

    //region avatar
    /**
     * 获取Avatar队列
     * @return ArrayList<Avatar>
     */
    fun getAvatars(): ArrayList<Avatar> {
        return avatars
    }

    /**
     * 添加 Avatar
     * @param avatar FaceUnityAvatarModel
     */
    fun addAvatar(avatar: Avatar) {
        if (avatars.contains(avatar)) {
            FULogger.e(TAG, "has loaded this FaceUnityAvatarModel")
            return
        }
        avatars.add(avatar)
        if (hasLoaded) {
            mAvatarController.doAddAvatar(sceneId, avatar.buildFUAAvatarData())
        }
    }

    /**
     * 添加 Avatar
     * @param avatar FaceUnityAvatarModel
     */
    fun addAvatarGL(avatar: Avatar) {
        if (avatars.contains(avatar)) {
            FULogger.e(TAG, "has loaded this FaceUnityAvatarModel")
            return
        }
        avatars.add(avatar)
        if (hasLoaded) {
            mAvatarController.doAddAvatarGL(sceneId, avatar.buildFUAAvatarData())
        }
    }


    /**
     * 移除 Avatar
     * @param avatar FaceUnityAvatarModel
     */
    fun removeAvatar(avatar: Avatar) {
        if (!avatars.contains(avatar)) {
            FULogger.e(TAG, "has not loaded this FaceUnityAvatarModel")
            return
        }
        avatars.remove(avatar)
        if (hasLoaded) {
            mAvatarController.doRemoveAvatar(sceneId, avatar.buildFUAAvatarData())
        }
    }

    /**
     * 移除 Avatar
     * @param avatar FaceUnityAvatarModel
     */
    fun removeAvatarGL(avatar: Avatar) {
        if (!avatars.contains(avatar)) {
            FULogger.e(TAG, "has not loaded this FaceUnityAvatarModel")
            return
        }
        avatars.remove(avatar)
        if (hasLoaded) {
            mAvatarController.doRemoveAvatarGL(sceneId, avatar.buildFUAAvatarData())
        }
    }

    /**
     * 替换 Avatar
     * @param oldAvatar FaceUnityAvatarModel 旧
     * @param newAvatar FaceUnityAvatarModel 新
     */
    fun replaceAvatar(oldAvatar: Avatar?, newAvatar: Avatar?) {
        if (oldAvatar == null && newAvatar == null) {
            FULogger.w(TAG, "oldAvatar and newAvatar is null")
        } else if (oldAvatar == null && newAvatar != null) {
            addAvatar(newAvatar)
        } else if (oldAvatar != null && newAvatar == null) {
            removeAvatar(oldAvatar)
        } else if (oldAvatar != null && newAvatar != null) {
            if (!avatars.contains(oldAvatar)) {
                FULogger.e(TAG, "has not loaded this FaceUnityAvatarModel")
                addAvatar(newAvatar)
                return
            }
            if (avatars.contains(newAvatar)) {
                if (oldAvatar == newAvatar) {
                    FULogger.w(TAG, "oldAvatar and newAvatar  is same")
                    return
                } else {
                    FULogger.e(TAG, "same newAvatar  already exists")
                    removeAvatar(oldAvatar)
                    return
                }
            }
            avatars.remove(oldAvatar)
            avatars.add(newAvatar)
            if (hasLoaded) {
                mAvatarController.doReplaceAvatar(sceneId, oldAvatar.buildFUAAvatarData(), newAvatar.buildFUAAvatarData())
            }
        }
    }

    /**
     * 替换 Avatar
     * @param oldAvatar FaceUnityAvatarModel 旧
     * @param newAvatar FaceUnityAvatarModel 新
     */
    fun replaceAvatarGL(oldAvatar: Avatar?, newAvatar: Avatar?) {
        if (oldAvatar == null && newAvatar == null) {
            FULogger.w(TAG, "oldAvatar and newAvatar is null")
        } else if (oldAvatar == null && newAvatar != null) {
            addAvatarGL(newAvatar)
        } else if (oldAvatar != null && newAvatar == null) {
            removeAvatarGL(oldAvatar)
        } else if (oldAvatar != null && newAvatar != null) {
            if (!avatars.contains(oldAvatar)) {
                FULogger.e(TAG, "has not loaded this FaceUnityAvatarModel")
                addAvatarGL(newAvatar)
                return
            }
            if (avatars.contains(newAvatar)) {
                if (oldAvatar == newAvatar) {
                    FULogger.w(TAG, "oldAvatar and newAvatar  is same")
                    return
                } else {
                    FULogger.e(TAG, "same newAvatar  already exists")
                    removeAvatarGL(oldAvatar)
                    return
                }
            }
            avatars.remove(oldAvatar)
            avatars.add(newAvatar)
            if (hasLoaded) {
                mAvatarController.doReplaceAvatarGL(sceneId, oldAvatar.buildFUAAvatarData(), newAvatar.buildFUAAvatarData())
            }
        }
    }
    //endregion avatar


    /**
     * 构造AvatarScene数据
     * @return FUASceneData
     */
    internal fun buildFUASceneData(): FUASceneData {
        val params = LinkedHashMap<String, () -> Unit>()
        val bundles = ArrayList<FUBundleData>()
        val animationData = ArrayList<FUAnimationData>()
        val avatarData = ArrayList<FUAAvatarData>()
        bundles.add(avatarConfig)
        /*背景*/
        backgroundBundle?.let {
            bundles.add(it)
        }
        backgroundColor?.let {
            params["enableBackgroundColor"] = { mAvatarController.enableBackgroundColor(sceneId, enable = true, needBackgroundThread = false) }
            params["setBackgroundColor"] = { mAvatarController.setBackgroundColor(sceneId, it, false) }
        }
        /*相机*/
        camera.loadParams(params)
        /*相机动画*/
        cameraAnimation.loadParams(params, animationData)
        /*阴影*/
        enableShadow?.let {
            params["enableShadow"] = { mAvatarController.enableShadow(sceneId, it, false) }
        }
        shadowPCFLevel?.let {
            params["setInstanceShadowPCFLevel"] = { mAvatarController.setInstanceShadowPCFLevel(sceneId, it, false) }
        }
        /*灯光*/
        enableLowQualityLighting?.let {
            params["enableLowQualityLighting"] = { mAvatarController.enableLowQualityLighting(sceneId, it) }
        }
        lightingBundle?.let {
            bundles.add(it)
        }
        /*AI驱动相关*/
        processorConfig.loadParams(params)
        /*avatars*/
        avatars.forEach {
            avatarData.add(it.buildFUAAvatarData())
        }
        hasLoaded = true
        return FUASceneData(sceneId, controlBundle, bundles, animationData, avatarData, params)
    }


}