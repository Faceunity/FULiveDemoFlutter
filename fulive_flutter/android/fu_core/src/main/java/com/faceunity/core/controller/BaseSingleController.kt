package com.faceunity.core.controller

import android.os.*
import android.util.Log
import com.faceunity.core.bundle.BundleManager
import com.faceunity.core.callback.OnControllerBundleLoadCallback
import com.faceunity.core.controller.facebeauty.FaceBeautyController
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.entity.TextureImage
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.FileUtils
import java.util.concurrent.CountDownLatch

/**
 *
 * DESC：同时只生效一个ControllerBundle控制器
 * Created on 2021/2/8
 *
 */
abstract class BaseSingleController {
    protected val TAG = "KIT_${this.javaClass.simpleName}"

    protected var modelSign = -99L
    internal var mControllerBundleHandle = -1
    protected val mBundleManager by lazy { BundleManager.getInstance() }
    protected val mFURenderKit by lazy { FURenderKit.getInstance() }
    protected val mFURenderBridge by lazy { FURenderBridge.getInstance() }

    /*  纹理缓存  */
    private val mTextureImageMap = LinkedHashMap<String, TextureImage>(16)


    protected abstract fun applyControllerBundle(featuresData: FUFeaturesData)

    //region 业务接口
    internal fun loadControllerBundle(featuresData: FUFeaturesData, callback: OnControllerBundleLoadCallback? = null) {
        removeBackgroundAction(999)
        doBackgroundAction(999) {
            val sign = System.nanoTime()
            modelSign = sign
            applyControllerBundle(featuresData)
            callback?.onLoadSuccess(sign)
        }
    }


    /**
     * Bundle 渲染开关
     * @param sign Long
     * @param enable Boolean
     */
    internal open fun setBundleEnable(sign: Long, enable: Boolean) {
        if (sign != modelSign) return
        FULogger.i(TAG, "setItemParam  enable:$enable  ")
        if (enable) {
            mBundleManager.bindControllerBundle(mControllerBundleHandle, this is FaceBeautyController)
        } else {
            mBundleManager.unbindControllerBundle(mControllerBundleHandle)
        }
    }


    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    open internal fun setItemParam(sign: Long, key: String, value: Any) {
        if (sign != modelSign) return
        FULogger.i(TAG, "setItemParam   key:$key  value:$value")
        itemSetParam(key, value)
    }


    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    internal fun setItemParamGL(sign: Long, key: String, value: Any) {
        if (sign != modelSign) return
        doGLThreadAction {
            FULogger.i(TAG, "setItemParam   key:$key  value:$value")
            itemSetParam(key, value)
        }
    }

    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    internal fun setItemParamBackground(sign: Long, key: String, value: Any) {
        if (sign != modelSign) return
        doBackgroundAction {
            FULogger.i(TAG, "setItemParamBackground  key:$key  value:$value")
            itemSetParam(key, value)
        }
    }

    /**
     * 参数设置
     * @param sign Long
     * @param params LinkedHashMap<String, Any>
     */
    internal fun setItemParamBackground(sign: Long, params: LinkedHashMap<String, Any>) {
        if (sign != modelSign) return
        doBackgroundAction {
            FULogger.i(TAG, "setItemParamBackground    params.size:${params.size}")
            itemSetParam(params)
        }
    }


    /**
     * 参数设置
     * @param sign Long
     * @param params LinkedHashMap<String, Any>
     */
    internal fun setItemParam(sign: Long, params: LinkedHashMap<String, Any>) {
        if (sign != modelSign) return
        FULogger.i(TAG, "setItemParam    params.size:${params.size}")
        itemSetParam(params)
    }


    /**
     * 创建纹理
     * @param sign Long
     * @param name String
     * @param path Any
     */
    internal fun createItemTex(sign: Long, name: String, path: String) {
        if (sign != modelSign) return
        FULogger.i(TAG, "createItemTex   name:$name  path:$path")
        createItemTex(name, path)
    }

    /**
     * 删除纹理
     * @param sign Long
     * @param name String
     */
    internal fun deleteItemTex(sign: Long, name: String) {
        if (sign != modelSign) return
        FULogger.i(TAG, "deleteItemTex    name:$name  ")
        deleteItemTex(name)
    }


    //endregion 业务接口

    //region 内部实现

    /**
     * 加载控制道具
     * @param bundle FUBundleData?
     * @param enable Boolean
     * @param unit Function0<Unit>?
     */
    protected fun applyControllerBundleAction(bundle: FUBundleData?, enable: Boolean, unit: (() -> Unit)? = null) {
        var handle = 0
        bundle?.let {
            handle = mBundleManager.loadBundleFile(it.name, it.path)
        }
        if (handle <= 0) {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
            mControllerBundleHandle = -1
            FULogger.e(TAG, "loadControllerBundle failed handle:$handle  path:${bundle?.path}")
            return
        }
        if (enable) {
            mBundleManager.updateControllerBundle(mControllerBundleHandle, handle, this is FaceBeautyController)
        } else {
            mBundleManager.destroyControllerBundle(mControllerBundleHandle)
        }
        mControllerBundleHandle = handle
        unit?.invoke()
    }

    /**
     * 参数设置
     * @param params LinkedHashMap<String, Any>
     */
    protected fun itemSetParam(params: LinkedHashMap<String, Any>) {
        FULogger.i(TAG, "setItemParam   params.size:${params.size}")
        if (mControllerBundleHandle <= 0) {
            FULogger.e(TAG, "setItemParam failed handle:$mControllerBundleHandle  ")
            return
        }
        params.forEach { (key, value) ->
            when (value) {
                is Double -> SDKController.itemSetParam(mControllerBundleHandle, key, value)
                is String -> SDKController.itemSetParam(mControllerBundleHandle, key, value)
                is DoubleArray -> SDKController.itemSetParam(mControllerBundleHandle, key, value)
                is Int -> SDKController.itemSetParam(mControllerBundleHandle, key, value.toDouble())
                is Float -> SDKController.itemSetParam(mControllerBundleHandle, key, value.toDouble())
            }

        }
    }

    /**
     * 参数设置
     * @param key String
     * @param value Any
     */
    protected fun itemSetParam(key: String, value: Any) {
        FULogger.i(TAG, "setItemParam  key:$key   value:$value")
        if (mControllerBundleHandle <= 0) {
            FULogger.e(TAG, "setItemParam failed handle:$mControllerBundleHandle  ")
            return
        }
        when (value) {
            is Double -> SDKController.itemSetParam(mControllerBundleHandle, key, value)
            is String -> SDKController.itemSetParam(mControllerBundleHandle, key, value)
            is DoubleArray -> SDKController.itemSetParam(mControllerBundleHandle, key, value)
            is Int -> SDKController.itemSetParam(mControllerBundleHandle, key, value.toDouble())
            is Float -> SDKController.itemSetParam(mControllerBundleHandle, key, value.toDouble())
        }
    }

    /**
     * 创建纹理
     * @param name String
     * @param path Any
     */
    protected fun createItemTex(name: String, path: String) {
        FULogger.i(TAG, "createItemTex  name:$name  path:$path")
        if (mControllerBundleHandle <= 0) {
            FULogger.e(TAG, "createItemTex failed handle:$mControllerBundleHandle  ")
            return
        }
        var textureImage = mTextureImageMap[path]
        if (textureImage == null) {
            textureImage = FileUtils.loadTextureImageFromLocal(FURenderManager.mContext, path)
        }
        textureImage?.let {
            mTextureImageMap[path] = it
            doGLThreadAction {
                SDKController.createTexForItem(mControllerBundleHandle, name, it.bytes, it.width, it.height)

            }
        }
    }

    /**
     * 创建纹理
     * @param name String
     * @param bytes ByteArray
     * @param width Int
     * @param height Int
     */
    protected fun createItemTex(name: String, bytes: ByteArray, width: Int, height: Int) {
        FULogger.t(TAG, "createItemTex   name:$name  width:$width height:$height")
        doGLThreadAction {
            SDKController.createTexForItem(mControllerBundleHandle, name, bytes, width, height)
        }
    }


    /**
     * 删除纹理
     * @param name String
     */
    protected fun deleteItemTex(name: String) {
        FULogger.t(TAG, "deleteItemTex   name:$name  ")
        if (mControllerBundleHandle <= 0) {
            FULogger.e(TAG, "deleteItemTex failed handle:$mControllerBundleHandle  ")
            return
        }
        doGLThreadAction {
            SDKController.deleteTexForItem(mControllerBundleHandle, name)
        }
    }

    //endregion 参数配置


    //region 异步线程

    private var isBackgroundRunning = false


    private class ControllerHandler(looper: Looper, val singleController: BaseSingleController) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            singleController.isBackgroundRunning = true
            val runnable = msg.obj as Runnable
            runnable.run()
            singleController.isBackgroundRunning = false
        }
    }

    /**
     * 执行后台任务
     * @param code Int
     * @param unit Function0<Unit>
     */
    protected fun doBackgroundAction(code: Int = 1, unit: () -> Unit) {
        val msg = Message()
        msg.what = code
        msg.obj = Runnable(unit)
        if (controllerHandler == null) {
            startBackgroundThread()
        }
        controllerHandler?.sendMessage(msg)
    }

    /**
     * GL线程执行任务
     * @param unit Function0<Unit>
     */
    protected fun doGLThreadAction(unit: () -> Unit) {
        mFURenderBridge.doGLThreadAction(unit)
    }


    private fun removeBackgroundAction(code: Int) {
        controllerHandler?.removeMessages(code)
    }


    private var controllerHandler: ControllerHandler? = null

    /* 创建线程  */
    private fun startBackgroundThread() {
        val backgroundThread = HandlerThread("KIT_${this.javaClass.simpleName}")
        backgroundThread.start()
        controllerHandler = ControllerHandler(backgroundThread.looper, this)
    }


    /* 释放线程  */
    internal fun releaseThread() {
        controllerHandler?.looper?.quitSafely()
        controllerHandler = null
    }


    //endregion 异步线程

    internal open fun release(unit: (() -> Unit)? = null) {
        controllerHandler?.let {
            controllerHandler?.removeCallbacksAndMessages(null)
            val countDownLatch = CountDownLatch(1)
            doBackgroundAction {
                modelSign = -99L
                if (mControllerBundleHandle > 0) {
                    unit?.invoke()
                    mBundleManager.destroyControllerBundle(mControllerBundleHandle)
                    mControllerBundleHandle = -1
                }
                countDownLatch.countDown()
            }
            countDownLatch.await()
        }
        releaseThread()
    }
}