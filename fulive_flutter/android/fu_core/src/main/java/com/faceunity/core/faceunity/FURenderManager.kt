package com.faceunity.core.faceunity

import android.content.Context
import com.faceunity.core.callback.OperateCallback
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import kotlin.properties.Delegates


/**
 *
 * DESC：
 * Created on 2021/2/8
 *
 */
object FURenderManager {

    internal var mContext: Context by Delegates.notNull()
    internal var mOperateCallback: OperateCallback? = null


    /**
     * 初始化FaceUnity环境
     * @param context Context   上下文
     * @param auth ByteArray 鉴权数据字节数组 authpack.A()
     * @param operateCallback OperateCallback 执行回调
     * @return FaceUnityHelper
     */
    @JvmStatic
    fun registerFURender(context: Context, auth: ByteArray, operateCallback: OperateCallback) {
        mContext = context.applicationContext
        mOperateCallback = operateCallback
        if (!SDKController.fuIsLibraryInit()) {
            SDKController.setup(auth)
        } else {
            operateCallback.onSuccess(FURenderConfig.OPERATE_SUCCESS_AUTH, "setup")
        }
    }

    /**
     * 设置FURenderKit 日志等级
     * @param logLevel LogLevel
     * @return FURenderManager
     */
    @JvmStatic
    fun setKitDebug(logLevel: FULogger.LogLevel) {
        FULogger.setLogLevel(logLevel)
    }

    /**
     * 设置 faceunity 日志等级
     * @param logLevel LogLevel
     * @return FURenderManager
     */
    @JvmStatic
    fun setCoreDebug(logLevel: FULogger.LogLevel) {
        SDKController.setLogLevel(logLevel.ordinal)
    }


}