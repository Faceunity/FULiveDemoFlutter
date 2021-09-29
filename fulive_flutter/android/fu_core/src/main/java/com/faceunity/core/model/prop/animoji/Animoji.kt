package com.faceunity.core.model.prop.animoji

import com.faceunity.core.controller.prop.PropParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.prop.Prop


/**
 *
 * DESC：动漫贴图
 * Created on 2021/1/29
 *
 */
class Animoji(controlBundle: FUBundleData) : Prop(controlBundle) {
    /* 设定是否人脸跟随 */
    var enableFaceFollow = true //true为开启 false为关闭
        set(value) {
            field = value
            updateAttributesGL(PropParam.FACE_FOLLOW, if (value) 1.0 else 0.0)
            updateAttributesGL(PropParam.IS_FIX_X, if (value) 0.0 else 1.0)
            updateAttributesGL(PropParam.IS_FIX_Y, if (value) 0.0 else 1.0)
            updateAttributesGL(PropParam.IS_FIX_Z, if (value) 0.0 else 1.0)
            updateAttributesGL(PropParam.FIX_ROTATION, if (value) 0.0 else 1.0)
        }

    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[PropParam.FACE_FOLLOW] = enableFaceFollow
        return params
    }
}