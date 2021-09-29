package com.faceunity.core.avatar

import com.faceunity.core.avatar.model.PTAScene
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.FULogger

/**
 *
 * DESC：
 * Created on 2021/3/31
 *
 */
class AvatarContainer {
    companion object {
        const val TAG = "KIT_AvatarContainer"

        @Volatile
        private var INSTANCE: AvatarContainer? = null

        internal fun getInstance(): AvatarContainer {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = AvatarContainer()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    /*Avatar控制器*/
    private val mAvatarController by lazy { FURenderBridge.getInstance().mAvatarController }

    /*scene缓存*/
    private val sceneArray = ArrayList<PTAScene>()


    /**
     * 添加场景
     * @param PTAScene FaceUnityAvatarSceneModel 场景模型
     * @return Boolean
     */
    fun addScene(PTAScene: PTAScene): Boolean {
        if (sceneArray.contains(PTAScene)) {
            FULogger.e(TAG, "this Scene already exists ")
            return false
        }
        sceneArray.add(PTAScene)
        mAvatarController.doAddAvatarScene(PTAScene.buildFUASceneData())
        return true
    }

    /**
     * 移除场景
     * @param PTAScene FaceUnityAvatarSceneModel
     * @return Boolean
     */
    fun removeScene(PTAScene: PTAScene): Boolean {
        if (!sceneArray.contains(PTAScene)) {
            FULogger.e(TAG, "this Scene is not exists ")
            return false
        }
        sceneArray.remove(PTAScene)
        mAvatarController.doRemoveAvatarScene(PTAScene.buildFUASceneData())
        return true
    }


    /**
     * 移除全部场景
     * @return Boolean
     */
    fun removeAllScene(): Boolean {
        sceneArray.forEach {
            removeScene(it)
        }
        sceneArray.clear()
        return true
    }

    /**
     * 获取全部道具
     * @return List<BaseProp>
     */
    fun getAllSceneModel(): List<PTAScene> {
        return sceneArray
    }

    /**
     * 释放Controller
     */
    fun release() {
        mAvatarController.release()
    }


}