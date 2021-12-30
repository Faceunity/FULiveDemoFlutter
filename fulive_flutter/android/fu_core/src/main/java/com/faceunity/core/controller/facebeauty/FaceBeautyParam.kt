package com.faceunity.core.controller.facebeauty


/**
 *
 * DESC：美颜参数
 * Created on 2020/12/21
 *
 */
object FaceBeautyParam {


    //region 滤镜
    /* 滤镜名称 */
    const val FILTER_NAME = "filter_name"

    /* 滤镜程度 */
    const val FILTER_INTENSITY = "filter_level"//范围0~1 0表示不显示滤镜
    //endregion

    //region 美肤
    /* 磨皮程度 */
    const val BLUR_INTENSITY = "blur_level"//范围[0-6]

    /*  朦胧磨皮开关 */
    const val HEAVY_BLUR = "heavy_blur"//0表示关闭，1表示开启

    /* 磨皮类型*/
    const val BLUR_TYPE = "blur_type"//0:清晰磨皮  1:朦胧磨皮  2:精细磨皮。 此参数优先级比 heavy_blur 低，在使用时要将 heavy_blur 设为 0

    /* 肤色检测*/
    const val SKIN_DETECT = "skin_detect"//肤色检测开关，0为关，1为开 默认0

    /* 融合程度*/
    const val NON_SKIN_BLUR_SCALE = "nonskin_blur_scale"// 肤色检测之后非肤色区域的融合程度，取值范围0.0-1.0，默认0.0


    /*  开启基于人脸的磨皮mask*/
    const val BLUR_USE_MASK = "blur_use_mask"//0表示关闭 1表示开启，使用正常磨皮。只在 blur_type 为 2 时生效。默认为 0

    /* 美白程度 */
    const val COLOR_INTENSITY = "color_level"//范围 [0-2]，推荐 0.2

    /* 红润程度 */
    const val RED_INTENSITY = "red_level"//范围 [0-2]，推荐  0.5

    /* 锐化程度 */
    const val SHARPEN_INTENSITY = "sharpen"//范围 [0-1]，推荐 0.2

    /* 亮眼程度 */
    const val EYE_BRIGHT_INTENSITY = "eye_bright"//范围 [0-1]，推荐 1

    /* 美牙程度 */
    const val TOOTH_WHITEN_INTENSITY = "tooth_whiten"//范围 [0-1]，推荐 1

    /* 去黑眼圈强度 */
    const val REMOVE_POUCH_INTENSITY = "remove_pouch_strength"//范围 [0-1]，，推荐 0

    /* 去法令纹强度 */
    const val REMOVE_NASOLABIAL_FOLDS_INTENSITY = "remove_nasolabial_folds_strength"//范围 [0-1]，，推荐 0
    //endregion
    //region 美型

    /* 变形选择 */
    const val FACE_SHAPE = "face_shape"//0 女神，1 网红，2 自然，3 默认，4 精细变形，推荐 3

    /* 变形程度 */
    const val FACE_SHAPE_INTENSITY = "face_shape_level"//范围 [0-1]，默认0

    /* 瘦脸程度 */
    const val CHEEK_THINNING_INTENSITY = "cheek_thinning"//范围 [0-1]，默认0

    /* V脸程度 */
    const val CHEEK_V_INTENSITY = "cheek_v"//范围 [0-1]，默认0

    /* 窄脸程度 */
    const val CHEEK_NARROW_INTENSITY = "cheek_narrow"//范围 [0-1]，默认0

    /* 小脸程度 */
    const val CHEEK_SMALL_INTENSITY = "cheek_small"//范围 [0-1]，默认0

    /* 瘦颧骨 */
    const val INTENSITY_CHEEKBONES_INTENSITY = "intensity_cheekbones"//范围 [0-1]，默认0

    /* 瘦下颌骨 */
    const val INTENSITY_LOW_JAW_INTENSITY = "intensity_lower_jaw"//范围 [0-1]，默认0

    /* 大眼程度 */
    const val EYE_ENLARGING_INTENSITY = "eye_enlarging"//范围 [0-1]，默认 0.5

    /* 下巴调整程度 */
    const val CHIN_INTENSITY = "intensity_chin"//范围 [0-1]，0-0.5是变小，0.5-1是变大，默认 0.5

    /* 额头调整程度 */
    const val FOREHEAD_INTENSITY = "intensity_forehead"//范围 [0-1]，0-0.5 是变小，0.5-1 是变大，默认 0.5

    /* 瘦鼻程度 */
    const val NOSE_INTENSITY = "intensity_nose"//范围 [0-1]，默认0.0

    /* 嘴巴调整程度 */
    const val MOUTH_INTENSITY = "intensity_mouth"//范围 [0-1]，0-0.5是变小，0.5-1是变大，默认  0.5

    /* 开眼角强度 */
    const val CANTHUS_INTENSITY = "intensity_canthus"//范围 [0-1]，0.0 到 1.0 变强 默认0.0

    /* 眼睛间距 */
    const val EYE_SPACE_INTENSITY = "intensity_eye_space"//范围 [0-1]，0.5-0.0 变长，0.5-1.0 变短  默认0.5

    /* 眼睛角度 */
    const val EYE_ROTATE_INTENSITY = "intensity_eye_rotate"//范围 [0-1]，0.5-0.0 逆时针旋转，0.5-1.0 顺时针旋转 默认0.5

    /* 鼻子长度 */
    const val LONG_NOSE_INTENSITY = "intensity_long_nose"//范围 [0-1]，0.5-0.0 变短，0.5-1.0 变长 默认0.5

    /* 调节人中 */
    const val PHILTRUM_INTENSITY = "intensity_philtrum"//范围 [0-1]，0.5-1.0 变长，0.5-0.0 变短 默认0.5

    /* 微笑嘴角强度 */
    const val SMILE_INTENSITY = "intensity_smile"//范围 [0-1]，0.0 到 1.0 变强 默认0.0
    //endregion

    /* 圆眼程度 */
    const val EYE_CIRCLE_INTENSITY = "intensity_eye_circle"//范围 [0-1]，0.0 到 1.0 变强 默认0.0
    //endregion


}