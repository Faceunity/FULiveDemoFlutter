package com.faceunity.core.model.prop.sticker

import com.faceunity.core.controller.prop.PropParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.prop.Prop


/**
 *
 * DESC：精品贴图
 * Created on 2021/1/29
 *
 */
open class FineSticker @JvmOverloads constructor(
    controlBundle: FUBundleData, val isFlipPoints: Boolean = false,
    val is3DFlipH: Boolean = false, val isClick: Boolean = false
) : Prop(controlBundle) {

    /**
     * 触发点击事件
     */
    fun onClick() {//点击
        if (isClick) {
            updateAttributes(PropParam.MOUSE_DOWN, 1.0)
        }
    }
    /**
     * 固定绘制方向
     */
    var forcePortrait = 0
        set(value) {
            field = value
            updateAttributes(PropParam.FORCE_PORTRAIT, value)
        }


    override fun buildRemark(): java.util.LinkedHashMap<String, Any> {
        val remark = super.buildRemark()
        if (isFlipPoints) {
            remark[PropParam.IS_FLIP_POINTS] = 1
        }
        if (is3DFlipH) {
            remark[PropParam.IS_3D_FlipH] = 1
        }
        remark[PropParam.FORCE_PORTRAIT] = forcePortrait
        return remark
    }
}