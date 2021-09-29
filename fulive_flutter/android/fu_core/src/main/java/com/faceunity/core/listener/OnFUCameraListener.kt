package com.faceunity.core.listener

import com.faceunity.core.camera.FUCameraPreviewData

/**
 *
 * DESC：相机数据回调
 * Created on 2020/12/7
 *
 */
interface OnFUCameraListener {

    /**
     *
     * @param previewData FUCameraPreviewData 当前相机数据流
     */
    fun onPreviewFrame(previewData: FUCameraPreviewData)

}