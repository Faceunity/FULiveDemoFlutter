package com.faceunity.core.faceunity

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.faceunity.core.support.SDKController
import com.google.android.exoplayer2.Renderer

/**
 * 自定义GLContext
 */
class OffLineRenderHandler private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: OffLineRenderHandler? = null

        @JvmStatic
        fun getInstance(): OffLineRenderHandler {
            if (INSTANCE == null) {
                synchronized(OffLineRenderHandler::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = OffLineRenderHandler()
                    }
                }
            }
            return INSTANCE!!
        }

        private const val RENDER_WHAT = 999
    }


    /**
     * 同步执行
     *
     * @param runnable
     */
    fun queueEvent(runnable: Runnable) {
        val msg = Message()
        msg.obj = runnable
        mCustomGLHandler?.sendMessage(msg)
    }

    /**
     * 触发Render 刷新 回调onDrawFrame
     */
    fun requestRender() {
        val msg = Message()
        msg.what = RENDER_WHAT
        mCustomGLHandler?.removeMessages(RENDER_WHAT)
        mCustomGLHandler?.sendMessage(msg)
    }

    /**
     * 重新启动线程
     */
    fun onResume() {
        startGLThread()
    }

    /**
     * 释放异步线程
     */
    fun onPause() {
        releaseGLThread()
    }

    /**
     * 绑定render
     * @param renderer Renderer
     */
    fun setRenderer(renderer: Renderer) {
        mCustomGLHandler?.renderer = renderer
    }


    //region 线程管理
    private var mBackgroundThread: HandlerThread? = null
    private var mCustomGLHandler: CustomGLHandler? = null

    /* 创建线程  */
    private fun startGLThread() {
        if (mBackgroundThread != null) return
        mBackgroundThread = HandlerThread("OffLineRenderHandler")
        mBackgroundThread!!.start()
        mCustomGLHandler = CustomGLHandler(mBackgroundThread!!.looper)
        mCustomGLHandler!!.post { SDKController.createEGLContext() }
    }

    /* 释放线程  */
    private fun releaseGLThread() {
        if (mBackgroundThread == null) return
        mCustomGLHandler?.removeCallbacksAndMessages(0)
        mCustomGLHandler?.post { SDKController.releaseEGLContext() }
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mCustomGLHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    //endregion 线程管理


    private class CustomGLHandler(looper: Looper) : Handler(looper) {
        /**
         * 绑定Renderer
         */
        var renderer: Renderer? = null


        override fun handleMessage(msg: Message?) {
            msg?.let {
                when {
                    it.what == RENDER_WHAT -> {
                        renderer?.onDrawFrame()
                    }
                    it.obj is Runnable -> {
                        (msg.obj as Runnable).run()
                    }
                    else -> {

                    }
                }
            }
        }

    }


    interface Renderer {
        /**
         * GL线程触发刷新
         */
        fun onDrawFrame()
    }


}