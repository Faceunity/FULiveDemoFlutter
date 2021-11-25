package com.faceunity.core.faceunity

import com.faceunity.core.avatar.listener.OnSceneListener
import com.faceunity.core.avatar.model.Scene
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.FULogger
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * DESC：
 * Created on 2021/6/30
 *
 */
class FUSceneKit private constructor() {

    companion object {
        const val TAG = "KIT_FUSceneKit"

        @Volatile
        private var INSTANCE: FUSceneKit? = null

        @JvmStatic
        fun getInstance(): FUSceneKit {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = FUSceneKit()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private val mAvatarController by lazy { FURenderBridge.getInstance().mAvatarController }
    private val sceneCacheMap = ConcurrentHashMap<Long, Scene>()

    /**
     * 离线加载目录
     */
    var programBinaryDirectory: String? = null


    /**
     * 添加Scene场景
     * @param scene Scene
     */
    @JvmOverloads
    fun addScene(scene: Scene, listener: OnSceneListener? = null) {
        if (sceneCacheMap.containsKey(scene.sceneId)) {
            FULogger.w(TAG, "addScene failed this scene has loaded")
            return
        }
        sceneCacheMap[scene.sceneId] = scene
        mAvatarController.doAddAvatarScene(scene.buildFUASceneData(), listener)
    }

    /**
     * 添加Scene场景
     * @param scene Scene
     */
    @JvmOverloads
    fun addSceneGL(scene: Scene, listener: OnSceneListener? = null) {
        if (sceneCacheMap.containsKey(scene.sceneId)) {
            FULogger.w(TAG, "addScene failed this scene has loaded")
            return
        }
        sceneCacheMap[scene.sceneId] = scene
        mAvatarController.doAddAvatarSceneGL(scene.buildFUASceneData(), listener)
    }


    /**
     * 移除Scene场景
     * @param scene Scene
     */
    fun removeScene(scene: Scene) {
        if (!sceneCacheMap.containsKey(scene.sceneId)) {
            FULogger.w(TAG, "removeScene failed this scene has not loaded")
            return
        }
        sceneCacheMap.remove(scene.sceneId)
        mAvatarController.doRemoveAvatarScene(scene.buildFUASceneData())
    }

    /**
     * 移除Scene场景
     * @param sceneId Long Scene对应id
     */
    fun removeScene(sceneId: Long) {
        if (!sceneCacheMap.containsKey(sceneId)) {
            FULogger.w(TAG, "removeScene failed this scene has not loaded")
            return
        }
        val scene = sceneCacheMap[sceneId]
        sceneCacheMap.remove(sceneId)
        scene?.let {
            mAvatarController.doRemoveAvatarScene(it.buildFUASceneData())
        }
    }

    /**
     * 替换Scene场景
     * @param currentScene Scene
     * @param targetScene Scene
     */
    fun replaceScene(currentScene: Scene, targetScene: Scene) {
        if (currentScene.sceneId == targetScene.sceneId) {
            FULogger.w(TAG, "replaceScene failed currentScene sceneId is equal targetScene sceneId")
            return
        }
        if (!sceneCacheMap.containsKey(currentScene.sceneId)) {
            FULogger.w(TAG, "replaceScene failed currentScene has not loaded do addScene")
            addScene(targetScene)
            return
        }

        if (sceneCacheMap.containsKey(targetScene.sceneId)) {
            FULogger.w(TAG, "replaceScene failed currentScene has  loaded do removeScene")
            removeScene(currentScene)
            return
        }
        mAvatarController.doReplaceAvatarScene(currentScene.buildFUASceneData(), targetScene.buildFUASceneData())
    }

    /**
     * 替换Scene场景
     * @param currentSceneId Long
     * @param targetScene Scene
     */
    fun replaceScene(currentSceneId: Long, targetScene: Scene) {
        if (sceneCacheMap.containsKey(currentSceneId)) {
            sceneCacheMap[currentSceneId]?.let {
                replaceScene(it, targetScene)
            }
        } else {
            FULogger.w(TAG, "replaceScene failed currentScene has  loaded do removeScene")
            removeScene(targetScene)
        }
    }

    /**
     * 设定当前Scene
     * @param scene Scene
     */
    fun setCurrentScene(scene: Scene) {
        if (!sceneCacheMap.containsKey(scene.sceneId)) {
            FULogger.w(TAG, "setCurrentScene failed currentScene has not loaded")
            return
        }
        mAvatarController.setCurrentScene(scene.sceneId)

    }

    /**
     * 设定当前Scene
     * @param sceneId Long
     */
    fun setCurrentScene(sceneId: Long) {
        if (!sceneCacheMap.containsKey(sceneId)) {
            FULogger.w(TAG, "setCurrentScene failed currentScene has not loaded")
            return
        }
        mAvatarController.setCurrentScene(sceneId)

    }

    /**
     * 设定当前Scene
     * @param scene Scene
     */
    fun setCurrentSceneGL(scene: Scene) {
        if (!sceneCacheMap.containsKey(scene.sceneId)) {
            FULogger.w(TAG, "setCurrentScene failed currentScene has not loaded")
            return
        }
        mAvatarController.setCurrentScene(scene.sceneId, false)

    }


    /**
     * 设定当前Scene
     * @param sceneId Long
     */
    fun setCurrentSceneGL(sceneId: Long) {
        if (!sceneCacheMap.containsKey(sceneId)) {
            FULogger.w(TAG, "setCurrentScene failed currentScene has not loaded")
            return
        }
        mAvatarController.setCurrentScene(sceneId, false)

    }


    /**
     * 移除全部场景
     * @return Boolean
     */
    fun removeAllScene() {
        sceneCacheMap.forEach { (sceneId, scene) ->
            mAvatarController.doRemoveAvatarScene(scene.buildFUASceneData())
        }
        sceneCacheMap.clear()
    }

    /**
     * 获取全部道具
     * @return List<BaseProp>
     */
    fun getAllScene(): List<Scene> {
        val list = ArrayList<Scene>()
        sceneCacheMap.forEach { (_, scene) ->
            list.add(scene)
        }
        return list
    }

    /**
     * 调用线程-预加载道具
     * @param bundle FUBundleData
     */
    fun preloadBundleUnThread(bundle: FUBundleData) {
        mAvatarController.preloadBundleUnThread(bundle)
    }

    /**
     * 调用线程-移除道具
     * @param path String
     */
    fun removePreLoadedBundle(path: String) {
        mAvatarController.removePreLoadedBundle(path)
    }

}