package com.faceunity.core.infe

import com.faceunity.core.listener.OnVideoPlayListener

/**
 *
 * DESC：
 * Created on 2021/1/5
 *
 */
interface IVideoRenderer {

    /**Activity onResume**/
    fun onResume()

    /**
     *Activity onPause 调用
     **/
    fun onPause()

    /**
     *Activity onDestroy 调用
     **/
    fun onDestroy()

    /**
     * 开启播放
     */
    fun startMediaPlayer(callback: OnVideoPlayListener?)

    /**
     * FURender渲染开关设置
     * @param isOpen Boolean
     */
    fun setFURenderSwitch(isOpen: Boolean)

    /**
     * 设置渲染过渡帧数（原始数据-》Furender渲染数据；避免黑屏）
     * @param count Int
     */
    fun setTransitionFrameCount(count: Int)

}