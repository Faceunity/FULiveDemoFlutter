package com.faceunity.core.callback

/**
 *
 * DESC：海报换脸业务回调
 * Created on 2020/12/7
 *
 */
interface OnPosterRenderCallback {

    /**
     * 图片加载完成回调
     * @param trackFace Int -1：人脸偏转角度过大 0未检测到人脸 >1 多人脸
     * @param array ArrayList<FloatArray>? 多人脸点位信息
     */
    fun onPhotoLoaded(trackFace: Int, array: ArrayList<FloatArray>? = null)

    /**
     * 蒙版加载完成
     *
     * @param trackFace Int -1：未检测到人脸
     */
    fun onTemplateLoaded(trackFace: Int)


    /**
     * 照片合成结果状态返回
     * @param isSuccess Boolean
     * @param texId 合成对应的纹理id
     */
    fun onMergeResult(isSuccess: Boolean, texId: Int)

}