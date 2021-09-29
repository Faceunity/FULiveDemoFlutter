package com.faceunity.core.model.hairBeauty

import com.faceunity.core.controller.hairBeauty.HairBeautyParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorLABData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：
 * Created on 2021/1/29
 *
 */
open class HairBeautyNormal(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {


    override fun getModelController() = FURenderBridge.getInstance().mHairBeautyController


    /**
     * 发色索引:单色Bundle对应范围0-7，渐变色Bundle对应范围0-4
     *
     */
    var hairIndex = 0
        set(value) {
            field = value
            updateAttributes(HairBeautyParam.INDEX, value)
        }


    /**
     * 发色强度:范围[0-1]，0表示关闭  1表示开启
     */
    var hairIntensity = 1.0
        set(value) {
            field = value
            updateAttributes(HairBeautyParam.INTENSITY, value)
        }


    /* 头发光泽度 */
    var hairShine: Double = 0.0 //取值范围：0.0~3.0， 0.0为无光泽，3.0为最大光泽度
        set(value) {
            field = value
            updateAttributes(HairBeautyParam.SHINE, value)
        }


    var hairColorLABData: FUColorLABData? = null
        set(value) {
            if (value == null) {
                return
            }
            field = value
            val param = LinkedHashMap<String, Any>()
            value.coverLABParam("Col", param)
            updateAttributes("Col", param)
        }

    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[HairBeautyParam.INDEX] = hairIndex
        params[HairBeautyParam.INTENSITY] = hairIntensity
        params[HairBeautyParam.SHINE] = hairShine
        hairColorLABData?.coverLABParam("Col", params)
        return params
    }


}