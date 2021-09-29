package com.faceunity.core.model.prop.bgSegCustom

import com.faceunity.core.controller.prop.PropParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.prop.Prop


/**
 *
 * DESC：
 * Created on 2021/1/30
 *
 */
class BgSegCustom(controlBundle: FUBundleData) : Prop(controlBundle) {


    /* 自定义背景 */
    fun createBgSegment(rgba: ByteArray, width: Int, height: Int) {
        createTexForItem(PropParam.TAX_BG, rgba, width, height)
    }

    /*移除自定义背景*/
    fun removeBgSegment() {
        deleteTexForItem(PropParam.TAX_BG)
    }
}