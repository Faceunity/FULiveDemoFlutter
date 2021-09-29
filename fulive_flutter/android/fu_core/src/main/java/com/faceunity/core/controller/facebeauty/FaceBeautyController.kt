package com.faceunity.core.controller.facebeauty

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData


/**
 *
 * DESC：美颜调用控制器
 * Created on 2021/2/8
 *
 */
class FaceBeautyController : BaseSingleController() {
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            itemSetParam(featuresData.param)
        }
    }
}