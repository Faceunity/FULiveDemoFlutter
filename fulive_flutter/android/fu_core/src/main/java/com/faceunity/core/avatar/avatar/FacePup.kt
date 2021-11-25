package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute


/**
 *
 * DESC：捏脸
 * Created on 2021/5/14
 *
 */
class FacePup : BaseAvatarAttribute() {


    /**
     * 捏脸设置缓存
     */
    val facePupCache = HashMap<String, Float>()

    internal var facePupMode: Boolean? = null


    /**
     * 进入捏脸模式
     */
    fun enterFacePupMode() {
        facePupMode = true
        mAvatarController.enableInstanceFaceUpMode(avatarId, true)
    }

    /**
     * 退出捏脸模式
     */
    fun quitFacePupMode() {
        facePupMode = false
        mAvatarController.enableInstanceFaceUpMode(avatarId, false)
    }

    /**
     * 细分部位调整
     * @param name String
     * @param value Double
     */
    fun setFacePupParam(name: String, value: Float) {
        facePupCache[name] = value
        mAvatarController.setInstanceFaceUp(avatarId, name, value)
    }

    /**
     * 细分部位调整
     * @param name String
     * @param value Double
     */
    fun setFacePupParamGL(name: String, value: Float) {
        facePupCache[name] = value
        mAvatarController.setInstanceFaceUp(avatarId, name, value, false)
    }

    /**
     * 获取保存在bundle中的捏脸参数
     * @param name String
     */
    fun getFacePupParam(name: String): Float {
        return mAvatarController.getInstanceFaceUpOriginalValue(avatarId, name)
    }

    /**
     * 获取捏脸系数
     * @return FloatArray
     */
    fun getInstanceFaceUpArray(): FloatArray {
        val rect = FloatArray(100)
        mAvatarController.getInstanceFaceUpArray(avatarId, rect)
        return rect
    }


    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        facePupMode?.let {
            params["enableInstanceFaceUpMode"] = {
                mAvatarController.enableInstanceFaceUpMode(avatarId, it, false)
            }
        }
        if (facePupCache.isNotEmpty()) {
            params["setInstanceFaceUp"] = {
                facePupCache.forEach { (name, value) ->
                    mAvatarController.setInstanceFaceUp(avatarId, name, value, false)
                }
            }
        }
        hasLoaded = true
    }

    /**
     * 数据克隆
     * @param facePup FacePup
     */
    fun clone(facePup: FacePup) {
        facePupMode = facePup.facePupMode
        facePup.facePupCache.forEach { (name, value) ->
            facePupCache[name] = value
        }
    }

}