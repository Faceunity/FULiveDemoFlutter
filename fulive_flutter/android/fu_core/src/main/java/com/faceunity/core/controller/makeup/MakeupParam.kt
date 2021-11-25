package com.faceunity.core.controller.makeup


/**
 *
 * DESC：
 * Created on 2020/12/22
 *
 */
object MakeupParam {


    /*组合妆容句柄*/
    const val COMBINATION = "Combination"//组合妆容资源
    /**
     * 业务相关
     */

    /*美妆开关*/
    const val IS_MAKEUP_ON = "is_makeup_on"//0表示关闭，1表示开启

    /*口红类型*/
    const val LIP_TYPE = "lip_type"//0雾面 1咬唇 2润泽 3珠光

    /*口红双色开关*/
    const val IS_TWO_COLOR = "is_two_color"  //0表示关闭，1表示开启，如果想使用咬唇，开启双色开关，并且将makeup_lip_color2的值设置为0

    /*口红颜色*/
    const val MAKEUP_LIP_COLOR = "makeup_lip_color"//口红1
    const val MAKEUP_LIP_COLOR_V2 = "makeup_lip_color_v2"//口红1
    const val MAKEUP_LIP_COLOR2 = "makeup_lip_color2"//口红2

    /*口红高光*/
    const val MAKEUP_LIP_HIGH_LIGHT_ENABLE = "makeup_lip_highlight_enable"//口红高光开光
    const val MAKEUP_LIP_HIGH_LIGHT_STRENGTH = "makeup_lip_highlight_strength"//口红高光强度

    /*是否使用眉毛变形*/
    const val BROW_WARP = "brow_warp"//0表示关闭，1表示开启

    /*眉毛变形类型*/
    const val BROW_WARP_TYPE = "brow_warp_type" //0柳叶眉  1一字眉  2远山眉 3标准眉 4扶形眉  5日常风 6日系风

    /*点位镜像开关*/
    const val IS_FLIP_POINTS = "is_flip_points"//0表示关闭，1表示开启

    /*解绑妆容时时是否清空除口红以外的妆容*/
    const val IS_CLEAR_MAKEUP = "is_clear_makeup"//0表示不清空，1表示清空，口红可由强度进行设置

    /**
     * 强度
     */
    /*全局妆容强度 */
    const val MAKEUP_INTENSITY = "makeup_intensity"//范围 [0-1] 0.0为不显示

    /* 各个妆容强度Key参数，范围 [0-1],  0即为关闭这种妆效 */
    const val LIP_INTENSITY = "makeup_intensity_lip"//口红强度
    const val EYE_LINER_INTENSITY = "makeup_intensity_eyeLiner"//眼线强度
    const val BLUSHER_INTENSITY = "makeup_intensity_blusher"//腮红强度
    const val PUPIL_INTENSITY = "makeup_intensity_pupil"//美瞳强度
    const val EYE_BROW_INTENSITY = "makeup_intensity_eyeBrow"//眉毛强度
    const val EYE_SHADOW_INTENSITY = "makeup_intensity_eye"//眼影强度
    const val EYELASH_INTENSITY = "makeup_intensity_eyelash"//睫毛强度
    const val FOUNDATION_INTENSITY = "makeup_intensity_foundation"//粉底强度
    const val HIGHLIGHT_INTENSITY = "makeup_intensity_highlight"//高光强度
    const val SHADOW_INTENSITY = "makeup_intensity_shadow"//阴影强度
    const val FILTER_INTENSITY = "filter_level"//阴影强度

    /**
     * 子项妆容贴图
     */
    const val TEX_EYE_BROW = "tex_brow"//眉毛
    const val TEX_EYE_SHADOW = "tex_eye"//眼影
    const val TEX_EYE_SHADOW2 = "tex_eye2"//眼影2
    const val TEX_EYE_SHADOW3 = "tex_eye3"//眼影3
    const val TEX_EYE_SHADOW4 = "tex_eye4"//眼影4
    const val TEX_PUPIL = "tex_pupil"//美瞳
    const val TEX_EYE_LASH = "tex_eyeLash"//睫毛
    const val TEX_EYE_LINER = "tex_eyeLiner"//眼线
    const val TEX_BLUSHER = "tex_blusher"//腮红
    const val TEX_BLUSHER2 = "tex_blusher2"//腮红2
    const val TEX_FOUNDATION = "tex_foundation"//粉底
    const val TEX_HIGH_LIGHT = "tex_highlight"//高光
    const val TEX_SHADOW = "tex_shadow"//阴影
    const val TEX_LIP = "tex_lip"//口红


    /**
     * 子项妆容颜色
     */
    const val MAKEUP_EYE_LINER_COLOR = "makeup_eyeLiner_color"//眼线
    const val MAKEUP_EYE_LASH_COLOR = "makeup_eyelash_color"//睫毛
    const val MAKEUP_BLUSHER_COLOR = "makeup_blusher_color"//腮红
    const val MAKEUP_BLUSHER_COLOR2 = "makeup_blusher_color2"//腮红
    const val MAKEUP_FOUNDATION_COLOR = "makeup_foundation_color"//粉底
    const val MAKEUP_HIGH_LIGHT_COLOR = "makeup_highlight_color"//高光
    const val MAKEUP_SHADOW_COLOR = "makeup_shadow_color"//阴影
    const val MAKEUP_EYE_BROW_COLOR = "makeup_eyeBrow_color"//眉毛
    const val MAKEUP_PUPIL_COLOR = "makeup_pupil_color"//美瞳
    const val MAKEUP_EYE_SHADOW_COLOR = "makeup_eye_color"//眼影
    const val MAKEUP_EYE_SHADOW_COLOR2 = "makeup_eye_color2"//眼影
    const val MAKEUP_EYE_SHADOW_COLOR3 = "makeup_eye_color3"//眼影
    const val MAKEUP_EYE_SHADOW_COLOR4 = "makeup_eye_color4"//眼影


    /**
     * 图层混合模式
     */

    const val BLEND_TEX_EYE_SHADOW = "blend_type_tex_eye"///第1层眼影的混合模式
    const val BLEND_TEX_EYE_SHADOW2 = "blend_type_tex_eye2"//第2层眼影的混合模式
    const val BLEND_TEX_EYE_SHADOW3 = "blend_type_tex_eye3"//第3层眼影的混合模式
    const val BLEND_TEX_EYE_SHADOW4 = "blend_type_tex_eye4"//第4层眼影的混合模式
    const val BLEND_TEX_EYE_LASH = "blend_type_tex_eyeLash"///睫毛的混合模式
    const val BLEND_TEX_EYE_LINER = "blend_type_tex_eyeLiner"//眼线的混合模式
    const val BLEND_TEX_BLUSHER = "blend_type_tex_blusher"//第1层腮红的混合模式
    const val BLEND_TEX_BLUSHER2 = "blend_type_tex_blusher2"//第2层腮红的混合模式
    const val BLEND_TEX_PUPIL = "blend_type_tex_pupil"//美瞳的混合模式


}