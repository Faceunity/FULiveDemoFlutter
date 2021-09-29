package com.faceunity.core.controller.bodyBeauty

/**
 *
 * DESC：美体
 * Created on 2020/12/28
 *
 */
object BodyBeautyParam {


    /*瘦身*/
    const val BODY_SLIM_INTENSITY = "BodySlimStrength"//范围[0-1] 值越大，瘦身幅度越大，0.0为不变形 默认为0.0

    /*腿拉伸*/
    const val LEG_STRETCH_INTENSITY = "LegSlimStrength"//范围[0-1] 值越大，腿拉伸幅度越大，0.0为不变形 默认为0.0

    /*瘦腰程度*/
    const val WAIST_SLIM_INTENSITY = "WaistSlimStrength"//范围[0-1] 值越大，瘦腰幅度越大，0.0为不变形 默认为0.0

    /*肩膀*/
    const val SHOULDER_SLIM_INTENSITY = "ShoulderSlimStrength"//范围[0-1] 小于0.5肩膀变窄，大于0.5肩膀变宽，0.5为不变形 默认为0.5

    /*臀部*/
    const val HIP_SLIM_INTENSITY = "HipSlimStrength"//范围[0-1] 值越大，臀部变宽上提越大，0.0为不变形 默认为0.0

    /*小头*/
    const val HEAD_SLIM_INTENSITY = "HeadSlim"//范围[0-1] ，值越大，小头效果越明显，默认为0.0

    /*瘦腿*/
    const val LEG_SLIM_INTENSITY = "LegSlim"//范围[0-1] 0.0表示强度为0，值越大，瘦腿效果越明显，默认为0.0

    /*重置*/
    const val CLEAR_SLIM = "clearSlim"//0表示关闭，1表示开启 清空所有的美体效果，恢复为默认值

    /*设置相机方向*/
    const val ORIENTATION = "Orientation"//设置相机方向 0, 1, 2, 3

    /*点位绘制*/
    const val IS_DEBUG = "Debug"//0表示关闭，1表示开启 默认关闭


}