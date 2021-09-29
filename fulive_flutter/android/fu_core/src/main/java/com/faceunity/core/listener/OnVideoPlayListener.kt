package com.faceunity.core.listener


/**
 *
 * DESC：录像功能
 * Created on 2020/12/7
 *
 */
interface OnVideoPlayListener {
    /**
     * 播放完成
     *
     */
    fun onPlayFinish()

    /**
     * 播放出错
     * @param error String 错误信息
     */
    fun onError(error: String)

}