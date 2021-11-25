package com.faceunity.core.avatar.base

import com.faceunity.core.support.FURenderBridge

/**
 *
 * DESC：
 * Created on 2021/1/12
 *
 */
abstract class BaseSceneAttribute {

    companion object {
        val TAG = "KIT_PTA_${this::class.java.name}"
    }

    /**
     * Avatar特效控制器
     */
    internal val mAvatarController by lazy { FURenderBridge.getInstance().mAvatarController }

    /**
     * 绑定的sceneId
     */
    internal var sceneId: Long = -1

    /**
     * 是否加载到SDK过
     */
    protected var hasLoaded = false


}