package com.faceunity.core.controller.littleMakeup


/**
 *
 * DESC：轻美妆
 * Created on 2020/11/26
 *
 */

object LightMakeupParam {

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

    /**
     * 项目
     */
    /*口红颜色*/
    const val MAKEUP_LIP_COLOR = "makeup_lip_color"
    /*嘴唇优化效果开关 */
    const val MAKEUP_LIP_MASK = "makeup_lip_mask"//0表示关闭，1表示开启

    const val TEX_BROW = "tex_brow"//眉毛
    const val TEX_EYE_SHADOW = "tex_eye"//眼影
    const val TEX_PUPIL = "tex_pupil"//美瞳
    const val TEX_EYE_LASH = "tex_eyeLash"//睫毛
    const val TEX_EYE_LINER = "tex_eyeLiner"//眼线
    const val TEX_BLUSHER = "tex_blusher"//腮红
    const val TEX_HIGH_LIGHT = "tex_highlight"//高光

    /**
     * 其他
     */
    /*alpha 值逆向*/
    const val REVERSE_ALPHA = "reverse_alpha"//0表示关闭，1表示开启

    /*是否使用修改过得landmark点 */
    const val IS_USER_FIX = "is_use_fix"//1为使用，0为不使用

    /*修改过得landmark点 */
    const val FIX_MAKEUP_DATA = "fix_makeup_data"//数组的长度为 150*人脸数，也就是将所有的点位信息存储的数组中传递进来。


}