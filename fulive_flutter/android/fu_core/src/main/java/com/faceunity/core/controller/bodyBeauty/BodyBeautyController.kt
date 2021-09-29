package com.faceunity.core.controller.bodyBeauty

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData


/**
 *
 * DESC：美体调用控制器
 * Created on 2020/12/28
 *
 */
class BodyBeautyController : BaseSingleController() {
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            itemSetParam(featuresData.param)
        }
    }


}