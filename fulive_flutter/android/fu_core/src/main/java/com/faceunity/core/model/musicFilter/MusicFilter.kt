package com.faceunity.core.model.musicFilter

import com.faceunity.core.controller.musicFilter.MusicFilterParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.DecimalUtils
import java.util.LinkedHashMap


/**
 *
 * DESC：音乐滤镜
 * Created on 2021/1/30
 *
 */
class MusicFilter(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {
    override fun getModelController() = mMusicFilterController
    private val mMusicFilterController by lazy { FURenderBridge.getInstance().mMusicFilterController }


    /* 当前音乐时间 */
    var musicTime = 0.0
        set(value) {
            field = value
            updateAttributes(MusicFilterParam.MUSIC_TIME, value)
        }

    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[MusicFilterParam.MUSIC_TIME] = musicTime
        return params
    }

    override fun buildFUFeaturesData(): FUFeaturesData = FUFeaturesData(controlBundle, buildParams(), enable)


}