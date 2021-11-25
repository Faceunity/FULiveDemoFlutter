package com.faceunity.core.model.makeup

import com.faceunity.core.controller.makeup.MakeupParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData


/**
 *
 * DESC：美妆
 * Created on 2021/1/31
 *
 */
class Makeup(controlBundle: FUBundleData) : SimpleMakeup(controlBundle) {


    /**
     * 设置组合妆容
     * @param controlBundle FUBundleData?
     * @param isReset Boolean
     */
    @JvmOverloads
    fun setCombinedConfig(controlBundle: FUBundleData?, isReset: Boolean = true) {
        combined = controlBundle
        if (isReset) {
            resetMakeup()
        } else {
            val params = buildParams()
            params.remove(MakeupParam.COMBINATION)
            updateAttributesBackground("reset", params)
        }
    }


    //endregion 组合妆容切换


    /**
     * 强度
     */
    //region 强度


    /* 口红强度 */
    var lipIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.LIP_INTENSITY, value)
        }

    /* 美瞳强度 */
    var pupilIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.PUPIL_INTENSITY, value)
        }

    /* 眼影强度 */
    var eyeShadowIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.EYE_SHADOW_INTENSITY, value)
        }

    /* 眼线强度 */
    var eyeLineIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.EYE_LINER_INTENSITY, value)
        }

    /* 睫毛强度 */
    var eyeLashIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.EYELASH_INTENSITY, value)
        }


    /* 眉毛强度 */
    var eyeBrowIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.EYE_BROW_INTENSITY, value)
        }


    /* 腮红强度 */
    var blusherIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLUSHER_INTENSITY, value)
        }

    /* 粉底强度 */
    var foundationIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.FOUNDATION_INTENSITY, value)
        }

    /* 高光强度 */
    var heightLightIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.HIGHLIGHT_INTENSITY, value)
        }

    /* 阴影强度 */
    var shadowIntensity = 0.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.SHADOW_INTENSITY, value)
        }

    /* 新的组合妆容 -> 滤镜程度 */
    var filterIntensity = 0.0   //范围0~1 0表示不显示滤镜
        set(value) {
            field = value * currentFilterScale
            updateAttributes(MakeupParam.FILTER_INTENSITY, field)
        }

    /* 当前滤镜的比率 例子 某个值为 X * scale = 真实值 */
    var currentFilterScale = 1.0
    //endregion


//region口红
    /**
     * 项目
     */
    /* 口红bundle  */
    var lipBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_LIP, value)
            field = value
        }

    /* 口红类型 */
    var lipType = MakeupLipEnum.FOG
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.LIP_TYPE, value)
        }

    /* 口红双色开关 */
    var enableTwoLipColor = false //口红双色开关，true，false为开启，如果想使用咬唇，开启双色开关，并且将makeup_lip_color2的值都设置为0
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.IS_TWO_COLOR, if (value) 1.0 else 0.0)
        }

    /*口红调色参数*/
    var lipColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_LIP_COLOR, value.toScaleColorArray())
        }

    /*口红调色参数*/
    var lipColorV2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_LIP_COLOR_V2, value.toScaleColorArray())
        }

    /*口红高光开关*/
    var lipHighLightEnable = false
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_LIP_HIGH_LIGHT_ENABLE, if (value) 1.0 else 0.0)
        }

    /*口红高光强度*/
    var lipHighLightStrength = 0.0
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_LIP_HIGH_LIGHT_STRENGTH, value)
        }

    /*口红调色参数*/
    var lipColor2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //如果is_two_color为1，会启用这个颜色为外圈颜色
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_LIP_COLOR2, value.toScaleColorArray())
        }

    /* 眉毛变形开关 */
    var enableBrowWarp = false // true表示开启，false表示关闭
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BROW_WARP, if (value) 1.0 else 0.0)
        }

    /* 眉毛变形类型 */
    var browWarpType = MakeupBrowWarpEnum.WILLOW
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BROW_WARP_TYPE, value)
        }


    //endregion 


    //region 子项妆容颜色

    /* 眼线调色参数 */
    var eyeLinerColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭眼线的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_LINER_COLOR, value.toScaleColorArray())
        }

    /* 睫毛调色参数 */
    var eyeLashColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭睫毛的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_LASH_COLOR, value.toScaleColorArray())
        }

    /* 第一层腮红调色参数 */
    var blusherColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭腮红的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_BLUSHER_COLOR, value.toScaleColorArray())
        }

    /* 第二层腮红调色参数 */
    var blusherColor2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭腮红的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_BLUSHER_COLOR2, value.toScaleColorArray())
        }

    /* 粉底调色参数 */
    var foundationColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭粉底的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_FOUNDATION_COLOR, value.toScaleColorArray())
        }

    /* 高光调色参数 */
    var highLightColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭高光的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_HIGH_LIGHT_COLOR, value.toScaleColorArray())
        }

    /* 阴影调色参数 */
    var shadowColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭阴影的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_SHADOW_COLOR, value.toScaleColorArray())
        }

    /* 眉毛调色参数 */
    var eyeBrowColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭眉毛的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_BROW_COLOR, value.toScaleColorArray())
        }

    /* 美瞳调色参数 */
    var pupilColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭美瞳的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_PUPIL_COLOR, value.toScaleColorArray())
        }

    /* 眼影调色参数 */
    var eyeShadowColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭眼影的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_SHADOW_COLOR, value.toScaleColorArray())
        }

    /* 眼影调色参数 */
    var eyeShadowColor2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭美瞳的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_SHADOW_COLOR2, value.toScaleColorArray())
        }

    /* 眼影调色参数 */
    var eyeShadowColor3 = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭美瞳的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_SHADOW_COLOR3, value.toScaleColorArray())
        }

    /* 眼影调色参数 */
    var eyeShadowColor4 = FUColorRGBData(0.0, 0.0, 0.0, 0.0) //数组的第四个值（对应alpha）为0时，会关闭美瞳的调色功能，大于0时会开启
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_EYE_SHADOW_COLOR4, value.toScaleColorArray())
        }

    //endregion

    //region 子项妆容贴图
    /* 眉毛贴图 */
    var eyeBrowBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_BROW, value)
            field = value
        }

    /* 眼影贴图1 */
    var eyeShadowBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_SHADOW, value)
            field = value
        }

    /* 眼影贴图2 */
    var eyeShadowBundle2: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_SHADOW2, value)
            field = value
        }

    /* 眼影贴图3 */
    var eyeShadowBundle3: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_SHADOW3, value)
            field = value
        }

    /* 眼影贴图4*/
    var eyeShadowBundle4: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_SHADOW4, value)
            field = value
        }

    /* 美瞳贴图 */
    var pupilBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_PUPIL, value)
            field = value
        }


    /* 睫毛 */
    var eyeLashBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_LASH, value)
            field = value
        }

    /* 眼线 */
    var eyeLinerBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_EYE_LINER, value)
            field = value
        }

    /* 腮红 */
    var blusherBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_BLUSHER, value)
            field = value
        }

    /* 腮红2 */
    var blusherBundle2: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_BLUSHER2, value)
            field = value
        }

    /*  粉底 */
    var foundationBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_FOUNDATION, value)
            field = value
        }

    /*  高光 */
    var highLightBundle: FUBundleData? = null
        set(value) {
            updateMakeupBundle(MakeupParam.TEX_HIGH_LIGHT, value)
            field = value

        }

    /*  阴影 */
    var shadowBundle: FUBundleData? = null
        set(value) {
            field = value
            updateMakeupBundle(MakeupParam.TEX_SHADOW, value)
        }

    //endregion

    //region 贴图混合模式


    /*  第1层眼影的混合模式 */
    var eyeShadowTexBlend = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_EYE_SHADOW, value)
        }

    /*  第2层眼影的混合模式 */
    var eyeShadowTexBlend2 = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_EYE_SHADOW2, value)
        }

    /*  第3层眼影的混合模式 */
    var eyeShadowTexBlend3 = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_EYE_SHADOW3, value)
        }

    /*  第4层眼影的混合模式 */
    var eyeShadowTexBlend4 = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_EYE_SHADOW4, value)
        }

    /*  睫毛的混合模式 */
    var eyeLashTexBlend = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_EYE_LASH, value)
        }

    /*  眼线的混合模式 */
    var eyeLinerTexBlend = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_EYE_LINER, value)
        }

    /*  第1层腮红的混合模式 */
    var blusherTexBlend = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_BLUSHER, value)
        }

    /*  第2层腮红的混合模式 */
    var blusherTexBlend2 = MakeupBlendEnum.MULTIPLY
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_BLUSHER2, value)
        }

    /*  美瞳的混合模式 */
    var pupilTexBlend = MakeupBlendEnum.ALPHA
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.BLEND_TEX_PUPIL, value)
        }


    //endregion



    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        combined?.let { params[MakeupParam.COMBINATION] = it }
        /*业务*/
        params[MakeupParam.LIP_TYPE] = lipType
        params[MakeupParam.IS_TWO_COLOR] = if (enableTwoLipColor) 1.0 else 0.0
        params[MakeupParam.MAKEUP_LIP_HIGH_LIGHT_ENABLE] = if (lipHighLightEnable) 1.0 else 0.0
        params[MakeupParam.MAKEUP_LIP_HIGH_LIGHT_STRENGTH] = lipHighLightStrength
        params[MakeupParam.BROW_WARP] = if (enableBrowWarp) 1.0 else 0.0
        params[MakeupParam.BROW_WARP_TYPE] = browWarpType
        /*强度*/
        params[MakeupParam.MAKEUP_INTENSITY] = makeupIntensity
        params[MakeupParam.LIP_INTENSITY] = lipIntensity
        params[MakeupParam.EYE_LINER_INTENSITY] = eyeLineIntensity
        params[MakeupParam.BLUSHER_INTENSITY] = blusherIntensity
        params[MakeupParam.PUPIL_INTENSITY] = pupilIntensity
        params[MakeupParam.EYE_BROW_INTENSITY] = eyeBrowIntensity
        params[MakeupParam.EYE_SHADOW_INTENSITY] = eyeShadowIntensity
        params[MakeupParam.EYELASH_INTENSITY] = eyeLashIntensity
        params[MakeupParam.FOUNDATION_INTENSITY] = foundationIntensity
        params[MakeupParam.HIGHLIGHT_INTENSITY] = heightLightIntensity
        params[MakeupParam.SHADOW_INTENSITY] = shadowIntensity
        /*子项妆容贴图*/
        lipBundle?.let { params[MakeupParam.TEX_LIP] = it }
        eyeBrowBundle?.let { params[MakeupParam.TEX_EYE_BROW] = it }
        eyeShadowBundle?.let { params[MakeupParam.TEX_EYE_SHADOW] = it }
        eyeShadowBundle2?.let { params[MakeupParam.TEX_EYE_SHADOW2] = it }
        eyeShadowBundle3?.let { params[MakeupParam.TEX_EYE_SHADOW3] = it }
        eyeShadowBundle4?.let { params[MakeupParam.TEX_EYE_SHADOW4] = it }
        pupilBundle?.let { params[MakeupParam.TEX_PUPIL] = it }
        eyeLashBundle?.let { params[MakeupParam.TEX_EYE_LASH] = it }
        eyeLinerBundle?.let { params[MakeupParam.TEX_EYE_LINER] = it }
        blusherBundle?.let { params[MakeupParam.TEX_BLUSHER] = it }
        blusherBundle2?.let { params[MakeupParam.TEX_BLUSHER2] = it }
        foundationBundle?.let { params[MakeupParam.TEX_FOUNDATION] = it }
        highLightBundle?.let { params[MakeupParam.TEX_HIGH_LIGHT] = it }
        shadowBundle?.let { params[MakeupParam.TEX_SHADOW] = it }
        /*子项妆容颜色*/
        params[MakeupParam.MAKEUP_LIP_COLOR] = lipColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_LIP_COLOR_V2] = lipColorV2.toScaleColorArray()
        params[MakeupParam.MAKEUP_LIP_COLOR2] = lipColor2.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_LINER_COLOR] = eyeLinerColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_LASH_COLOR] = eyeLashColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_BLUSHER_COLOR] = blusherColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_BLUSHER_COLOR2] = blusherColor2.toScaleColorArray()
        params[MakeupParam.MAKEUP_FOUNDATION_COLOR] = foundationColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_HIGH_LIGHT_COLOR] = highLightColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_SHADOW_COLOR] = shadowColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_BROW_COLOR] = eyeBrowColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_PUPIL_COLOR] = pupilColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_SHADOW_COLOR] = eyeShadowColor.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_SHADOW_COLOR2] = eyeShadowColor2.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_SHADOW_COLOR3] = eyeShadowColor3.toScaleColorArray()
        params[MakeupParam.MAKEUP_EYE_SHADOW_COLOR4] = eyeShadowColor4.toScaleColorArray()

        /* 图层混合模式 */
        params[MakeupParam.BLEND_TEX_EYE_SHADOW] = eyeShadowTexBlend
        params[MakeupParam.BLEND_TEX_EYE_SHADOW2] = eyeShadowTexBlend2
        params[MakeupParam.BLEND_TEX_EYE_SHADOW3] = eyeShadowTexBlend3
        params[MakeupParam.BLEND_TEX_EYE_SHADOW4] = eyeShadowTexBlend4
        params[MakeupParam.BLEND_TEX_EYE_LASH] = eyeLashTexBlend
        params[MakeupParam.BLEND_TEX_EYE_LINER] = eyeLinerTexBlend
        params[MakeupParam.BLEND_TEX_BLUSHER] = blusherTexBlend
        params[MakeupParam.BLEND_TEX_BLUSHER2] = blusherTexBlend2
        params[MakeupParam.BLEND_TEX_PUPIL] = pupilTexBlend
        return params
    }


    private fun resetMakeup() {
        /*业务*/
        lipType = MakeupLipEnum.FOG
        lipHighLightEnable = false
        lipHighLightStrength = 0.0
        enableTwoLipColor = false
        enableBrowWarp = false
        browWarpType = MakeupBrowWarpEnum.WILLOW

        /*强度*/
        makeupIntensity = 1.0
        eyeLineIntensity = 0.0
        lipIntensity = 0.0
        blusherIntensity = 0.0
        pupilIntensity = 0.0
        eyeBrowIntensity = 0.0
        eyeShadowIntensity = 0.0
        eyeLashIntensity = 0.0
        foundationIntensity = 0.0
        heightLightIntensity = 0.0
        shadowIntensity = 0.0
        /*子项妆容贴图*/
        lipBundle = null
        eyeBrowBundle = null
        eyeShadowBundle = null
        eyeShadowBundle2 = null
        eyeShadowBundle3 = null
        eyeShadowBundle4 = null
        pupilBundle = null
        eyeLashBundle = null
        eyeLinerBundle = null
        blusherBundle = null
        blusherBundle2 = null
        foundationBundle = null
        highLightBundle = null
        shadowBundle = null
        /*子项妆容颜色*/
        lipColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        lipColorV2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        lipColor2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeLinerColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeLashColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        blusherColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        blusherColor2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        foundationColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        highLightColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        shadowColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeBrowColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        pupilColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeShadowColor = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeShadowColor2 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeShadowColor3 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)
        eyeShadowColor4 = FUColorRGBData(0.0, 0.0, 0.0, 0.0)

        /* 图层混合模式 */
        eyeShadowTexBlend = MakeupBlendEnum.MULTIPLY
        eyeShadowTexBlend2 = MakeupBlendEnum.MULTIPLY
        eyeShadowTexBlend3 = MakeupBlendEnum.MULTIPLY
        eyeShadowTexBlend4 = MakeupBlendEnum.MULTIPLY
        eyeLashTexBlend = MakeupBlendEnum.MULTIPLY
        eyeLinerTexBlend = MakeupBlendEnum.MULTIPLY
        blusherTexBlend = MakeupBlendEnum.MULTIPLY
        blusherTexBlend2 = MakeupBlendEnum.MULTIPLY
        pupilTexBlend = MakeupBlendEnum.ALPHA

    }


}