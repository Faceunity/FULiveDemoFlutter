package com.faceunity.core.controller.prop


/**
 *
 * DESC：
 * Created on 2020/12/10
 *
 */
object PropParam {

    internal const val PROP_TYPE = "propType"

    internal const val PROP_TYPE_STICKER = 0
    internal const val PROP_TYPE_ANIMOJI = 1
    internal const val PROP_TYPE_AR_MASK = 2
    internal const val PROP_TYPE_PORTRAIT_SEGMENT = 3
    internal const val PROP_TYPE_HUMAN_OUTLINE = 4
    internal const val PROP_TYPE_BG_SEG_CUSTOM = 5
    internal const val PROP_TYPE_BIG_HEAD = 6
    internal const val PROP_TYPE_EXPRESSION = 7
    internal const val PROP_TYPE_FACE_WARP = 8
    internal const val PROP_TYPE_GESTURE= 9
    internal const val PROP_TYPE_FINE_STICKER= 10
    internal const val PROP_TYPE_MAKEUP_STICKER= 11
    /**
     * Animoji
     */
    /*翻转3D模型*/
    const val FLIP_3DH = "is3DFlipH" //  0表示关闭 1表示开启

    /*翻转表情*/
    const val FLIP_EXPR = "isFlipExpr" // 0表示关闭 1表示开启

    /*翻转光照*/
    const val FLIP_LIGHT = "isFlipLight" // 0表示关闭 1表示开启

    /*翻转位置和旋转*/
    const val FLIP_TRACK = "isFlipTrack" // 0表示关闭 1表示开启

    /*人脸跟随*/
    const val FACE_FOLLOW = "{\"thing\":\"<global>\",\"param\":\"follow\"}" // 0表示关闭 1表示开启

    /*人脸位置_X_是否开启*/
    const val IS_FIX_X = "{\"thing\":\"<global>\",\"param\":\"is_fix_x\"}" // X轴位置
    /*人脸跟随_Y_是否开启*/
    const val IS_FIX_Y = "{\"thing\":\"<global>\",\"param\":\"is_fix_y\"}" // Y轴位置
    /*人脸跟随_Z_是否开启*/
    const val IS_FIX_Z = "{\"thing\":\"<global>\",\"param\":\"is_fix_z\"}" // Z轴位置
    /* 老道具 -> animoji 人脸不跟随 -> 设置人脸移除时候锁定方向*/
    const val FIX_ROTATION = "fix_rotation"

    /**
     * ExpressionRecognition
     */
    const val KEY_AI_TYPE: String = "aitype"
    const val KEY_LANDMARKS_TYPE = "landmarks_type"


    /**
     * HumanOutline
     */
    /* 描边与人的距离 默认值为3.0 */
    internal const val LINE_GAP = "lineGap"

    /* 描边宽度 默认值为1.0 */
    internal const val LINE_SIZE = "lineSize"

    /* 描边颜色RGB 默认值为[0.0,0.0,1.0]  */
    internal const val LINE_COLOR = "lineColor"

    /**
     * BgSegCustom
     */
//    /*默认值为0，用于背景图片的旋转*/
    const val ROTATION_MODE = "rotation_mode"

    /*背景场景纹理*/
    const val TAX_BG = "tex_bg_seg"

    /*背景场景纹理*/
    const val BG_ALIGN_TYPE = "bg_align_type"

    /**
     * PROP_TYPE_FINE_STICKER
     */
    /*点位镜像开关*/
    const val IS_FLIP_POINTS = "is_flip_points"//0表示关闭，1表示开启

    /*点位镜像开关*/
    const val IS_3D_FlipH = "is3DFlipH"//0表示关闭，1表示开启

    /*固定绘制方向*/
    const val FORCE_PORTRAIT = "force_portrait"

    /*点击触发*/
    const val MOUSE_DOWN = "mouse_down"
}