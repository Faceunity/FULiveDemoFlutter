package com.faceunity.core.infe

import android.graphics.Bitmap
import android.hardware.SensorManager
import com.faceunity.core.callback.OnColorReadCallback
import com.faceunity.core.callback.OnRecordingVideoCallback
import com.faceunity.core.callback.OnTakePictureCallback
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 *
 * DESC：GlSurfaceRenderer相关功能接口声明
 * Created on 2020/12/31
 *
 */
interface ICameraRenderer {

    /**
     *Activity onResume 调用
     **/
    fun onResume()

    /**
     *Activity onPause 调用
     **/
    fun onPause()

    /**
     *Activity release 调用
     **/
    fun onDestroy()

    /**
     * 窗口渲染固定图片
     * @param bitmap Bitmap
     */
    fun showImageTexture(bitmap: Bitmap)

    /**
     * 移除图片渲染
     */
    fun hideImageTexture()

    /**
     * 是否需要小窗显示
     * @param isShow Boolean
     */
    fun drawSmallViewport(isShow: Boolean)

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

    /**
     * avatar 小窗拖拽
     * @param x Int
     * @param y Int
     * @param action Int
     */
    fun onTouchEvent(x: Int, y: Int, action: Int)


    /**
     * 开启相机
     */
    fun reopenCamera()

    /**
     * 关闭相机
     */
    fun closeCamera()


    /**
     * 前后摄像头切换
     */
    fun switchCamera()


}