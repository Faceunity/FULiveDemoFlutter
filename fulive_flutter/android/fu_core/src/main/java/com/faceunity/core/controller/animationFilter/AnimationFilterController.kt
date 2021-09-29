package com.faceunity.core.controller.animationFilter

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.utils.GlUtil

/**
 *
 * DESC：动漫滤镜调用控制器
 * Created on 2020/12/24
 *
 */
class AnimationFilterController : BaseSingleController() {
    /**
     * 加载动漫滤镜
     */
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            itemSetParam(AnimationFilterParam.GLVER, GlUtil.getGlMajorVersion().toDouble())
            itemSetParam(featuresData.param)
        }
    }

}