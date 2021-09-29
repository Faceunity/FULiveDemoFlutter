package com.faceunity.core.model.littleMakeup

import com.faceunity.core.controller.littleMakeup.LightMakeupParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.DecimalUtils

/**
 *
 * DESC：轻美妆
 * Created on 2021/1/30
 *
 */
class LightMakeup(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = FURenderBridge.getInstance().mLightMakeupController


    /* 是否使用修改过得landmark点 */
    var enableUserFixLandmark = false  //true表示开启  false表示关闭
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.IS_USER_FIX, if (value) 1.0 else 0.0)
        }


    /* 修改过得landmark点 */
    var fixLandmarkArray: FloatArray = floatArrayOf()    //数组的长度为 150*人脸数，也就是将所有的点位信息存储的数组中传递进来。
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.FIX_MAKEUP_DATA, value)
        }


    //region 强度

    /* 轻美妆整体强度 */
    var makeupIntensity = 1.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.MAKEUP_INTENSITY, value)
        }


    /* 口红强度 */
    var lipIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.LIP_INTENSITY, value)
        }


    /* 眼线强度 */
    var eyeLineIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.EYE_LINER_INTENSITY, value)
        }

    /* 腮红强度 */
    var blusherIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.BLUSHER_INTENSITY, value)
        }

    /* 美瞳强度 */
    var pupilIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.PUPIL_INTENSITY, value)
        }

    /* 眉毛强度 */
    var eyeBrowIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.EYE_BROW_INTENSITY, value)
        }

    /* 眼影强度 */
    var eyeShadowIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.EYE_SHADOW_INTENSITY, value)
        }

    /* 睫毛强度 */
    var eyeLashIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.EYELASH_INTENSITY, value)
        }

    //endregion
    //region 项目
    /* 口红颜色 */
    var lipColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.MAKEUP_LIP_COLOR, value.toScaleColorArray())
        }


    /* 嘴唇优化效果开关 */
    var enableLibMask = true  //true表示开启  false表示关闭
        set(value) {
            field = value
            updateAttributes(LightMakeupParam.MAKEUP_LIP_MASK, if (value) 1.0 else 0.0)
        }

    /* 眉毛贴图 */
    var eyeBrowTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_BROW, value)

        }

    /* 眼影贴图 */
    var eyeShadowTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_EYE_SHADOW, value)
        }

    /* 美瞳贴图 */
    var pupilTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_PUPIL, value)
        }


    /* 睫毛贴图 */
    var eyeLashTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_EYE_LASH, value)
        }

    /* 眼线贴图 */
    var eyeLinerTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_EYE_LINER, value)
        }

    /* 腮红贴图 */
    var blusherTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_BLUSHER, value)
        }

    /*  高光贴图 */
    var highLightTex: String? = null
        set(value) {
            field = value
            updateItemTex(LightMakeupParam.TEX_HIGH_LIGHT, value)
        }


    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        /*业务*/
        params[LightMakeupParam.REVERSE_ALPHA] = 1.0
        params[LightMakeupParam.IS_USER_FIX] = if (enableUserFixLandmark) 1.0 else 0.0
        if (fixLandmarkArray.isNotEmpty()) {
            params[LightMakeupParam.FIX_MAKEUP_DATA] = fixLandmarkArray
        }
        /*强度*/
        params[LightMakeupParam.MAKEUP_INTENSITY] = makeupIntensity
        params[LightMakeupParam.LIP_INTENSITY] = lipIntensity
        params[LightMakeupParam.EYE_LINER_INTENSITY] = eyeLineIntensity
        params[LightMakeupParam.BLUSHER_INTENSITY] = blusherIntensity
        params[LightMakeupParam.PUPIL_INTENSITY] = pupilIntensity
        params[LightMakeupParam.EYE_BROW_INTENSITY] = eyeBrowIntensity
        params[LightMakeupParam.EYE_SHADOW_INTENSITY] = eyeShadowIntensity
        params[LightMakeupParam.EYELASH_INTENSITY] = eyeLashIntensity
        /*单项*/
        eyeBrowTex?.let { params[LightMakeupParam.TEX_BROW] = it }
        eyeShadowTex?.let { params[LightMakeupParam.TEX_EYE_SHADOW] = it }
        pupilTex?.let { params[LightMakeupParam.TEX_PUPIL] = it }
        eyeLashTex?.let { params[LightMakeupParam.TEX_EYE_LASH] = it }
        eyeLinerTex?.let { params[LightMakeupParam.TEX_EYE_LINER] = it }
        blusherTex?.let { params[LightMakeupParam.TEX_BLUSHER] = it }
        highLightTex?.let { params[LightMakeupParam.TEX_HIGH_LIGHT] = it }
        params[LightMakeupParam.MAKEUP_LIP_COLOR] = lipColor.toScaleColorArray()
        params[LightMakeupParam.MAKEUP_LIP_MASK] = if (enableLibMask) 1.0 else 0.0
        return params
    }


    //endregion


}