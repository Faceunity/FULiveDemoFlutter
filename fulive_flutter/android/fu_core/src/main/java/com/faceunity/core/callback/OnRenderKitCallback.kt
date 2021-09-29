package com.faceunity.core.callback

import com.faceunity.core.enumeration.FUAIProcessorEnum

/**
 *
 * DESC：FURenderKit回调
 * Created on 2020/12/7
 *
 */
interface OnRenderKitCallback {

    /**
     * 检测状态发生变化
     *
     * @param process  对应AI算法模型
     * @param status 识别到数量
     */
    fun onTrackStatusChanged(process: FUAIProcessorEnum, status: Int) {}

    /**
     *  检测数据回调
     * @param width Int  图像宽
     * @param height Int 图像高
     * @param fps Double 当前帧率
     * @param renderTime Double 渲染时长
     */
    fun onBenchmarkChanged(width: Int, height: Int, fps: Double, renderTime: Double) {}

}