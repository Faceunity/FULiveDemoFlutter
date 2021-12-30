package com.faceunity.core.avatar.model

import com.faceunity.core.avatar.control.FUAAvatarData
import com.faceunity.core.avatar.control.FUASceneData
import com.faceunity.core.avatar.scene.SceneBackground
import com.faceunity.core.avatar.scene.SceneHumanProcessor
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：
 * Created on 2021/1/15
 *
 */
class PTAScene @JvmOverloads constructor(
    private val controlBundle: FUBundleData, private val avatarConfig: FUBundleData,
    private val avatars: ArrayList<PTAAvatar> = ArrayList()
) {
    private val TAG = "KIT_FaceUnityAvatarSceneModel"
    internal val mAvatarController by lazy { FURenderBridge.getInstance().mAvatarController }

    private val sceneId: Long = System.nanoTime()

    val mSceneBackground = SceneBackground()//背景颜色
    val mSceneHumanProcessor = SceneHumanProcessor()//人体驱动


    init {
        mSceneBackground.sceneId = sceneId
        mSceneHumanProcessor.sceneId = sceneId
    }



    /**
     * 添加 Avatar
     * @param avatar FaceUnityAvatarModel
     */
    fun addAvatar(avatar: PTAAvatar) {
        if (avatars.contains(avatar)) {
            FULogger.e(TAG, "has loaded this FaceUnityAvatarModel")
            return
        }
        avatars.add(avatar)
        mAvatarController.doAddAvatar(sceneId, avatar.buildFUAAvatarData())
    }

    /**
     * 移除 Avatar
     * @param avatar FaceUnityAvatarModel
     */
    fun removeAvatar(avatar: PTAAvatar) {
        if (!avatars.contains(avatar)) {
            FULogger.e(TAG, "has not loaded this FaceUnityAvatarModel")
            return
        }
        avatars.remove(avatar)
        mAvatarController.doRemoveAvatar(sceneId, avatar.buildFUAAvatarData())
    }

    /**
     * 替换 Avatar
     * @param oldAvatar FaceUnityAvatarModel 旧
     * @param newAvatar FaceUnityAvatarModel 新
     */
    fun replaceAvatar(oldAvatar: PTAAvatar?, newAvatar: PTAAvatar?) {
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
            mAvatarController.doReplaceAvatar(sceneId, oldAvatar.buildFUAAvatarData(), newAvatar.buildFUAAvatarData())
        }
    }


    /**
     * 构造AvatarScene数据
     * @return FUASceneData
     */
    internal fun buildFUASceneData(): FUASceneData {
        val params = LinkedHashMap<String, () -> Unit>()
        val bundles = ArrayList<FUBundleData>()
        val avatarData = ArrayList<FUAAvatarData>()
        bundles.add(avatarConfig)
        mSceneBackground.loadParams(bundles)
        mSceneHumanProcessor.loadParams(params)
        avatars.forEach {
            avatarData.add(it.buildFUAAvatarData())
        }
        return FUASceneData(sceneId, controlBundle, bundles, avatarData, params)
    }


//    //region 加载
//    /*是否正在异步加载Bundle文件*/
//
//    /*是否正在异步加载Scene*/
//    private var isSceneLoading = false
//
//    /*操作缓存*/
//    private val modelUnitCache = ConcurrentHashMap<String, () -> Unit>()//异步加载过程中的自定义函数缓存
//
//    /**
//     * 加载到FURenderKit
//     */
//    internal fun loadToRenderKit() {
//        isSceneLoading = true
//        mAvatarController.doAddAvatarScene(buildFUASceneData(), mLoadCallback)
//    }
//
//
//    /**
//     * 异步加载完成回调，并将在加载过程中变更过的参数重置一次
//     */
//    private val mLoadCallback = object : OnAvatarLoadCallback {
//        override fun onLoadSuccess() {
//            val iterator = modelUnitCache.entries.iterator()
//            while (iterator.hasNext()) {
//                val entry = iterator.next()
//                modelUnitCache.remove(entry.key)
//                entry.value.invoke()
//            }
//            isSceneLoading = false
//        }
//    }


    //endregion

//    val mSceneLighting = SceneMore()
//    val mSceneShadow = SceneShadow()
//    val mSceneHumanProcessor = AvatarHumanProcessor()
//    val mSceneCamera = SceneCamera()
//    val mSceneCameraAnimation = SceneCameraAnimation(cameraAnimations)
//    val mSceneSpecialMode = SceneSpecialMode()

    //region FaceUnityAvatarModel
//
//

//
//            if (avatars.contains(newAvatar!!) {
//                return if (oldPath == newPath) {
//                    FULogger.w(TAG, "oldPath and newPath  path is same")
//                    false
//                } else {
//                    FULogger.e(TAG, "a prop with the same bundle path already exists")
//                    removeProp(oldProp)
//                }
//            }
//            propMap.remove(oldPath)
//            propMap[newPath] = newProp
//            newProp.replayPropBundle(oldProp.controlBundle)


//
//    /**
//     * 移除 Avatar
//     * @param avatarId Int
//     */
//    fun removeAvatar(avatarId: Int) {
//        avatars.forEach {
//            if (it.avatarId == avatarId) {
//                removeAvatar(it)
//                return
//            }
//        }
//        FULogger.e(TAG, "has not loaded this FaceUnityAvatarModel  avatarId:$avatarId")
//    }


//endregion


}