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
            updateAttributesGL(PropParam.FACE_FOLLOW, value)
        }

    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[PropParam.FACE_FOLLOW] = if (enableFaceFollow) 1.0 else 0.0
        return params
    }


}