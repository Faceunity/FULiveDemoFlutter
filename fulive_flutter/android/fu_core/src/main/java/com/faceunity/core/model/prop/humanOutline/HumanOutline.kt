package com.faceunity.core.model.prop.humanOutline

import com.faceunity.core.controller.prop.PropParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData
import com.faceunity.core.model.prop.Prop
import com.faceunity.core.utils.DecimalUtils

/**
 *
 * DESC：人物描边
 * Created on 2021/1/29
 *
 */
class HumanOutline(controlBundle: FUBundleData) : Prop(controlBundle) {

    var lineGap = 3.0    //描边与人的距离
        set(value) {
            field = value
            updateAttributes(PropParam.LINE_GAP, value)
        }

    var lineSize = 1.0    //描边宽度
        set(value) {
            field = value
            updateAttributes(PropParam.LINE_SIZE, value)
        }

    var lineColor = FUColorRGBData(0.0, 0.0, 255.0)    //描边颜色
        set(value) {
            field = value
            updateAttributes(PropParam.LINE_COLOR, value.toScaleColorArray())
        }

    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[PropParam.LINE_GAP] = lineGap
        params[PropParam.LINE_SIZE] = lineSize
        params[PropParam.LINE_COLOR] = lineColor.toScaleColorArray()
        return params
    }
}