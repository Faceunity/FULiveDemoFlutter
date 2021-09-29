package com.faceunity.core.controller.bgSegGreen

/**
 *
 * DESC：
 * Created on 2020/12/4
 *
 */

object BgSegGreenParam {
    /*数据源标识*/
    internal const val ANDROID = "isAndroid" // 1相机、相机录制的视频 0表示图片、非相机录制的视频

    /*默认值为[0,255,0],取值范围[0-255,0-255,0-255],选取的颜色RGB,默认绿色可根据实际颜色进行调整*/
    const val RGB_COLOR = "key_color"

    /*默认值为0.518,取值范围0.0-1.0，相似度：色度最大容差，色度最大容差值越大，更多幕景被抠除   */
    const val SIMILARITY = "chroma_thres"

    /*默认值为0.22,取值范围0.0-1.0，平滑度：色度最小限差，值越大，更多幕景被扣除*/
    const val SMOOTHNESS = "chroma_thres_T"

    /*默认值为0.0,取值范围0.0-1.0，透明度：图像前后景透明度过度，值越大，两者边缘处透明过度更平滑   */
    const val TRANSPARENCY = "alpha_L"

    /* 默认值为0，用于设置背景图片的颜色是否是bgra*/
    const val IS_BGRA = "is_bgra"

    /*默认值为0.5,取值范围0.0-1.0，前景图像像素起点x的坐标，用于调节图像在背景图像中的位置和大小*/
    const val START_X = "start_x"

    /*默认值为0.5,取值范围0.0-1.0，前景图素像素起点y的坐标，用于调节图像在背景图像中的位置和大小*/
    const val START_Y = "start_y"

    /*默认值为1.0,取值范围0.0-1.0，前景图像像素终点x的坐标，用于调节图像在背景图像中的位置和大小*/
    const val END_X = "end_x"

    /*默认值为1.0,取值范围0.0-1.0，前景图素像素终点y的坐标，用于调节图像在背景图像中的位置和大小*/
    const val END_Y = "end_y"

    /*默认值为0，用于背景图片的旋转*/
    const val ROTATION_MODE = "rotation_mode"

    /*背景场景纹理*/
    const val TAX_BG = "tex_bg"

    /*生成安全区域纹理*/
    const val TEX_TEMPLATE = "tex_template"

    /*是否启用安全区域纹理*/
    const val IS_USE_TEMPLATE = "is_use_template"
}