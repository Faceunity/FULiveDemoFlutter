package com.faceunity.core.controller.hairBeauty

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData

/**
 *
 * DESC：美发调用控制器
 * Created on 2020/12/21
 *
 */
class HairBeautyController : BaseSingleController() {
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            itemSetParam(featuresData.param)
        }
    }
}