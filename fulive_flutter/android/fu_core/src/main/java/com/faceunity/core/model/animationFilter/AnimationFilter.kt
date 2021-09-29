package com.faceunity.core.model.animationFilter

import com.faceunity.core.controller.animationFilter.AnimationFilterParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge

/**
 *
 * DESC：动漫滤镜
 * Created on 2021/1/29
 *
 */
class AnimationFilter(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = FURenderBridge.getInstance().mAnimationFilterController


    /* 滤镜样式* */
    var style = AnimationFilterTypeEnum.Origin  //范围[-1~7] 参考AnimationFilterParam中值
        set(value) {
            field = value
            updateAttributes(AnimationFilterParam.STYLE, value)
        }


    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[AnimationFilterParam.STYLE] = style
        return params
    }
}