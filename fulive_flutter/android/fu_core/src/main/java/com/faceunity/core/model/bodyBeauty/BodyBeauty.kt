package com.faceunity.core.model.bodyBeauty

import com.faceunity.core.controller.bodyBeauty.BodyBeautyParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.DecimalUtils

/**
 *
 * DESC：美体
 * Created on 2021/1/29
 *
 */
class BodyBeauty(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = FURenderBridge.getInstance().mBodyBeautyController

    /* 点位绘制 */
    var enableDebug = false //false表示关闭，true表示开启 默认关闭
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.IS_DEBUG, if (value) 1 else 0)
        }

    /* 瘦身幅度 */
    var bodySlimIntensity = 0.0   //范围[0-1] 值越大，瘦身幅度越大，0.0为不变形
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.BODY_SLIM_INTENSITY, value)
        }

    /* 腿拉伸幅度 */
    var legStretchIntensity = 0.0 //范围[0-1] 值越大，腿拉伸幅度越大，0.0为不变形
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.LEG_STRETCH_INTENSITY, value)
        }

    /* 瘦腰幅度 */
    var waistSlimIntensity = 0.0 //范围[0-1] 值越大，瘦腰幅度越大，0.0为不变形
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.WAIST_SLIM_INTENSITY, value)
        }

    /* 肩膀变窄幅度 */
    var shoulderSlimIntensity = 0.5 //范围[0-1] 小于0.5肩膀变窄，大于0.5肩膀变宽，0.5为不变形
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.SHOULDER_SLIM_INTENSITY, value)
        }

    /* 臀部变宽幅度 */
    var hipSlimIntensity = 0.0 //范围[0-1] 值越大，臀部变宽上提越大，0.0为不变形
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.HIP_SLIM_INTENSITY, value)
        }


    /* 小头幅度 */
    var headSlimIntensity = 0.0 //范围[0-1] 程度渐强，默认 0.0
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.HEAD_SLIM_INTENSITY, value)
        }


    /* 瘦腿幅度 */
    var legSlimIntensity = 0.0 ///范围[0-1] 程度渐强，默认 0.0
        set(value) {
            field = value
            updateAttributes(BodyBeautyParam.LEG_SLIM_INTENSITY, value)
        }


    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[BodyBeautyParam.CLEAR_SLIM] = 1
        params[BodyBeautyParam.IS_DEBUG] = if (enableDebug) 1 else 0
        params[BodyBeautyParam.BODY_SLIM_INTENSITY] = bodySlimIntensity
        params[BodyBeautyParam.LEG_STRETCH_INTENSITY] = legStretchIntensity
        params[BodyBeautyParam.WAIST_SLIM_INTENSITY] = waistSlimIntensity
        params[BodyBeautyParam.SHOULDER_SLIM_INTENSITY] = shoulderSlimIntensity
        params[BodyBeautyParam.HIP_SLIM_INTENSITY] = hipSlimIntensity
        params[BodyBeautyParam.HEAD_SLIM_INTENSITY] = headSlimIntensity
        params[BodyBeautyParam.LEG_SLIM_INTENSITY] = legSlimIntensity
        return params
    }


}