package com.faceunity.core.callback

import android.net.Uri


/**
 *
 * DESC：录像功能
 * Created on 2020/12/7
 *
 */
interface OnRecordingVideoCallback {
    /**
     * 录制时长
     *
     * @param time
     */
    fun onProcess(time: Long)

    /**
     * 录制出错
     * @param error String 错误信息
     */
    fun onError(error: String)


    /**
     * 录制完成回调
     * @param uri Uri 视频地址
     */
    fun onRecordFinish(uri: Uri?)

}