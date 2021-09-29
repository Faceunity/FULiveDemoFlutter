package com.faceunity.core.listener

import com.faceunity.core.entity.FURenderFrameData
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.entity.FURenderOutputData


/**
 *
 * DESC：
 * Created on 2020/12/31
 *
 */
interface OnGlRendererListener {


    /**
     * GLSurfaceView.Renderer onSurfaceCreated完成
     */
    fun onSurfaceCreated()

    /**
     * GLSurfaceView.Renderer onSurfaceChanged 完成
     */
    fun onSurfaceChanged(width: Int, height: Int)


    /**
     * 当前渲染的数据流（GL线程回调）
     * @param inputData FURenderInputData 原始数据
     */
    fun onRenderBefore(inputData: FURenderInputData?)


    /**
     * 当前渲染的数据流（GL线程回调）
     * @param outputData FURenderOutputData 特效处理后数据
     * @param frameData FURenderFrameData 即将渲染矩阵
     */
    fun onRenderAfter(outputData: FURenderOutputData, frameData: FURenderFrameData)

    /**
     * 视图渲染完成
     */
    fun onDrawFrameAfter()

    /**
     * 视图销毁
     */
    fun onSurfaceDestroy()

}