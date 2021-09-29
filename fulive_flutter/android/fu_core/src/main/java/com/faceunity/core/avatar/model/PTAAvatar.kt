package com.faceunity.core.avatar.model

import com.faceunity.core.avatar.avatar.AvatarAnimation
import com.faceunity.core.avatar.avatar.AvatarTransForm
import com.faceunity.core.avatar.control.FUAAvatarData
import com.faceunity.core.entity.FUBundleData


/**
 *
 * DESC：DESC：Avatar人物数据模型
 * Created on 2021/3/30
 *
 */
class PTAAvatar(
    private val components: ArrayList<FUBundleData>,
    private val animations: ArrayList<FUBundleData>
) {
    private val avatarId: Long = System.nanoTime()

    val mAvatarTransForm = AvatarTransForm()//位置信息
    val mAvatarAnimation = AvatarAnimation(animations) //动画控制

    init {
        mAvatarTransForm.avatarId = avatarId
        mAvatarAnimation.avatarId = avatarId
    }


    internal fun buildFUAAvatarData(): FUAAvatarData {
        val initParams = LinkedHashMap<String, () -> Unit>()
        val params = LinkedHashMap<String, () -> Unit>()
        val itemBundles = ArrayList<FUBundleData>()
        itemBundles.addAll(components)
        mAvatarTransForm.loadInitParams(initParams)
        mAvatarTransForm.loadParams(params)
        mAvatarAnimation.loadParams(itemBundles)
        return FUAAvatarData(avatarId, itemBundles, initParams, params)
    }


}