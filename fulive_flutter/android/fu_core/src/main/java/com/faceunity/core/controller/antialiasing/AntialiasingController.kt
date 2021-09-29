package com.faceunity.core.controller.antialiasing

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData


/**
 *
 * DESC：3D抗锯齿
 * Created on 2021/1/6
 *
 */
class AntialiasingController : BaseSingleController() {

    override fun applyControllerBundle (featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle,featuresData.enable)
    }


}