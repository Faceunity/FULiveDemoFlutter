package com.faceunity.core.model.hairBeauty

import com.faceunity.core.controller.hairBeauty.HairBeautyParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorLABData
import com.faceunity.core.utils.DecimalUtils

/**
 *
 * DESC：
 * Created on 2021/1/29
 *
 */
class HairBeautyGradient(controlBundle: FUBundleData) : HairBeautyNormal(controlBundle) {


    /* 头发光泽度 */
    var hairShine2: Double = 0.0
        //取值范围：0.0~3.0， 0.0为无光泽，3.0为最大光泽度
        set(value) {
            field = value
            updateAttributes(HairBeautyParam.SHINE_1, value)

        }


    var hairColorLABData2: FUColorLABData? = null
        set(value) {
            if (value == null) {
                return
            }
            field = value
            val param = LinkedHashMap<String, Any>()
            value.coverLABParam("Col1", param)
            updateAttributes("Col1", param)

        }

    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[HairBeautyParam.INDEX] = hairIndex
        params[HairBeautyParam.INTENSITY] = hairIntensity
        params[HairBeautyParam.SHINE_0] = hairShine
        params[HairBeautyParam.SHINE_1] = hairShine2
        hairColorLABData?.coverLABParam("Col0", params)
        hairColorLABData2?.coverLABParam("Col1", params)
        return params
    }


}