package com.faceunity.core.entity

import com.faceunity.core.enumeration.CameraFacingEnum
import com.faceunity.core.enumeration.CameraTypeEnum


/**
 *
 * DESC：Camera相关构建参数
 * Created on 2020/12/14
 *
 */

class FUCameraConfig {

    /* 相机类型*/
    @JvmField
    var cameraType = CameraTypeEnum.CAMERA1

    /* 前后置摄像头选择*/
    @JvmField
    var cameraFacing = CameraFacingEnum.CAMERA_FRONT

    /* 相机输出固定帧率  -1不做限制*/
    @JvmField
    var cameraFPS = -1

    /* 相机分辨率 宽*/
    @JvmField
    var cameraWidth: Int = 1280

    /* 相机分辨率 高*/
    @JvmField
    var cameraHeight: Int = 720

    /* 释放开启最高帧率*/
    @JvmField
    var isHighestRate: Boolean = false


    /**
     * 相机类型
     *
     * @param cameraType
     * @return
     */
    fun setCameraType(cameraType: CameraTypeEnum): FUCameraConfig {
        this.cameraType = cameraType
        return this
    }


    /**
     * 前后置摄像头选择
     *
     * @param cameraFacing
     * @return
     */
    fun setCameraFacing(cameraFacing: CameraFacingEnum): FUCameraConfig {
        this.cameraFacing = cameraFacing
        return this
    }


    /**
     * 相机帧率
     *
     * @param cameraFPS
     * @return
     */
    fun setCameraFPS(cameraFPS: Int): FUCameraConfig {
        this.cameraFPS = cameraFPS
        return this
    }

    /**
     * 相机分辨率高
     *
     * @param cameraHeight
     * @return
     */
    fun setCameraHeight(cameraHeight: Int): FUCameraConfig {
        this.cameraHeight = cameraHeight
        return this
    }


    /**
     * 相机分辨率宽
     *
     * @param cameraWidth
     * @return
     */
    fun setCameraWidth(cameraWidth: Int): FUCameraConfig {
        this.cameraWidth = cameraWidth
        return this
    }

    /**
     * 开启相机最高帧率
     *
     * @param isHighestRate
     * @return
     */
    fun setHighestRate(isHighestRate: Boolean): FUCameraConfig {
        this.isHighestRate = isHighestRate
        return this
    }
}