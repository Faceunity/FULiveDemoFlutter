package com.faceunity.core.controller.musicFilter

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData

/**
 *
 * DESC：音乐滤镜
 * Created on 2020/12/24
 *
 */
class MusicFilterController : BaseSingleController() {

    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            featuresData.param.forEach { (key, value) ->
                itemSetParam(key, value)
            }
        }
    }

}