package com.faceunity.core.controller.prop

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.faceunity.core.bundle.BundleManager
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import java.util.concurrent.CountDownLatch

/**
 *
 * DESC：
 * Created on 2021/4/2
 *
 */
open class BasePropController {

    protected val TAG = "KIT_${this.javaClass.simpleName}"

    protected val mFURenderBridge by lazy { FURenderBridge.getInstance() }
    protected val mBundleManager by lazy { BundleManager.getInstance() }

    /*任务池*/
    private val threadQueuePool = ThreadQueuePool()

    /*道具映射关系*/
    protected var propIdMap = HashMap<Long, Int>(16)

    /*道具映射关系*/
    protected var propTypeMap = HashMap<Long, LinkedHashMap<String,Any>>(16)

    /**
     * 任务执行
     * @param queue QueueItem
     */
    open fun applyThreadQueue(queue: ThreadQueuePool.QueueItem) {

    }

    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    protected fun itemSetParam(handle: Int, key: String, value: Any) {
        FULogger.i(TAG, "setItemParam  key:$key   value:$value")
        if (handle <= 0) {
            FULogger.e(TAG, "setItemParam failed handle:$handle  ")
            return
        }
        when (value) {
            is Double -> SDKController.itemSetParam(handle, key, value)
            is String -> SDKController.itemSetParam(handle, key, value)
            is DoubleArray -> SDKController.itemSetParam(handle, key, value)
            is Int -> SDKController.itemSetParam(handle, key, value.toDouble())
            is Float -> SDKController.itemSetParam(handle, key, value.toDouble())
        }
    }

    /**
     * 资源释放
     * @param unit Function0<Unit>?
     */
    internal open fun release(unit: (() -> Unit)? = null) {
        controllerHandler?.let {
            val countDownLatch = CountDownLatch(1)
            it.post {
                propIdMap.forEach { (_, handle) ->
                    mBundleManager.destroyControllerBundle(handle)
                }
                propIdMap.clear()
                propTypeMap.clear()
                countDownLatch.countDown()
            }
            countDownLatch.await()
        }
        releaseThread()
    }


    //region 异步线程
    /**
     * 执行后台任务
     * @param queue ThreadQueuePool.QueueItem
     */
    protected fun doBackgroundAction(queue: ThreadQueuePool.QueueItem) {
        if (controllerHandler == null) {
            startBackgroundThread()
        }
        threadQueuePool.push(queue)
        val msg = Message()
        msg.what = 1
        controllerHandler?.removeMessages(1)
        controllerHandler?.sendMessage(msg)
    }

    /**
     * GL线程执行任务
     * @param unit Function0<Unit>
     */
    protected fun doGLThreadAction(unit: () -> Unit) {
        mFURenderBridge.doGLThreadAction(unit)
    }

    private var controllerThreadId = -1L
    private var controllerHandler: ControllerHandler? = null

    /* 创建线程  */
    private fun startBackgroundThread() {
        val backgroundThread = HandlerThread("KIT_${this.javaClass.simpleName}")
        backgroundThread.start()
        controllerHandler = ControllerHandler(backgroundThread.looper, this)
        controllerThreadId = controllerHandler!!.looper.thread.id
    }


    /* 释放线程  */
    private fun releaseThread() {
        controllerHandler?.removeCallbacksAndMessages(null)
        controllerHandler?.looper?.quitSafely()
        controllerHandler = null
    }


    private class ControllerHandler(looper: Looper, val propController: BasePropController) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            do {
                val queue = propController.threadQueuePool.pull()
                if (queue == null) {
                    break
                } else {
                    propController.applyThreadQueue(queue)
                }
            } while (true)
        }
    }


//endregion 异步线程
}