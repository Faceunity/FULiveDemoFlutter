package com.faceunity.core.model.antialiasing

import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import java.util.LinkedHashMap


/**
 *
 * DESC：3D抗锯齿
 * Created on 2021/1/30
 *
 */
class Antialiasing(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = FURenderBridge.getInstance().mAntialiasingController

    override fun buildParams() = LinkedHashMap<String, Any>()


}