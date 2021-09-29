package com.faceunity.core.camera

import com.faceunity.core.enumeration.CameraFacingEnum


/**
 *
 * DESC：
 * Created on 2021/3/16
 *
 */
data class FUCameraPreviewData(val buffer: ByteArray, val cameraFacing: CameraFacingEnum, val cameraOrientation: Int, val width: Int, val height: Int)