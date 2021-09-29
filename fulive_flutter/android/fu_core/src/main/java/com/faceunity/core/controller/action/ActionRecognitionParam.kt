package com.faceunity.core.controller.action


/**
 *
 * DESC：
 * Created on 2020/12/10
 *
 */
object ActionRecognitionParam {

    /*数据源标识*/
    internal const val ANDROID = "isAndroid" // 1相机、相机录制的视频 0表示图片、非相机录制的视频

    /*设备方向*/
    internal const val ROTATION_MODE = "rotationMode" // 范围数值 0 1 2 3

    /*设备方向*/
    internal const val ROT_MODE = "rotMode" //  范围数值 0 1 2 3


    /*动作识别设置边缘距离*/
    internal const val EDGE_DISTANCE = "edge_distance"//主要是用于适配界面顶部的 UI 元素


}