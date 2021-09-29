package com.faceunity.core.controller.littleMakeup

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData

/**
 *
 * DESC：轻美妆调用控制器
 * Created on 2020/12/25
 *
 */
class LightMakeupController : BaseSingleController() {
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            featuresData.param.forEach { (key, value) ->
                if (key.startsWith("tex_") && value is String) {
                    createItemTex(key, value)
                } else {
                    itemSetParam(key, value)
                }

            }
        }
    }

}