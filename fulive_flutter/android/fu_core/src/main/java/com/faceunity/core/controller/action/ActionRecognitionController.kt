package com.faceunity.core.controller.action

import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.utils.ScreenUtils

/**
 *
 * DESC：动作识别调用控制器
 * Created on 2020/12/24
 *
 */
class ActionRecognitionController : BaseSingleController() {
    /**
     * 加载动作识别句柄
     */
    override fun applyControllerBundle(featuresData: FUFeaturesData) {
        applyControllerBundleAction(featuresData.bundle, featuresData.enable) {
            setParams()
        }
    }


    /**
     * 设置贴图参数
     */
    private fun setParams() {
        val screenInfo = ScreenUtils.getScreenInfo(FURenderManager.mContext)
        if (screenInfo.heightPixels.toFloat() / screenInfo.widthPixels > 16.toFloat() / 9) {
            itemSetParam(ActionRecognitionParam.EDGE_DISTANCE, 0.1)
        }
    }

}