package com.faceunity.core.model.action

import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge

/**
 *
 * DESC：动作识别
 * Created on 2021/1/29
 *
 */
class ActionRecognition(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = FURenderBridge.getInstance().mActionRecognitionController


    override fun buildParams(): LinkedHashMap<String, Any> = LinkedHashMap()
}