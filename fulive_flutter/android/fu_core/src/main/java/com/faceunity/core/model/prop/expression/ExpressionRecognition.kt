package com.faceunity.core.model.prop.expression

import com.faceunity.core.controller.prop.PropParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.enumeration.FUAITypeEnum
import com.faceunity.core.model.prop.Prop

/**
 *
 * DESC：表情识别
 * Created on 2021/1/29
 *
 */
class ExpressionRecognition(controlBundle: FUBundleData) : Prop(controlBundle) {

    var aiType: FUAITypeEnum? = null    //AI类型
        set(value) {
            field = value
            value?.let {
                updateAttributes(PropParam.KEY_AI_TYPE, it.type)
            }
        }

    var landmarksType: FUAITypeEnum? = null    //landmarks类型
        set(value) {
            field = value
            value?.let {
                updateAttributes(PropParam.KEY_LANDMARKS_TYPE, it.type)
            }
        }

    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        aiType?.let {
            params[PropParam.KEY_AI_TYPE] = it.type
        }
        landmarksType?.let {
            params[PropParam.KEY_LANDMARKS_TYPE] = it.type
        }
        return params
    }

}