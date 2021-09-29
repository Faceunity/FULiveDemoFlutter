package com.faceunity.core.callback


/**
 *
 * DESC：控制句柄异步加载回调
 * Created on 2021/2/4
 *
 */
interface OnControllerBundleLoadCallback {

    /**
     * 初始化异步加载Bundle完成
     * @param sign Long
     */
    fun onLoadSuccess(sign: Long)
}