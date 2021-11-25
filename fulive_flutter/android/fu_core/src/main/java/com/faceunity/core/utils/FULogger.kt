package com.faceunity.core.utils

import android.util.Log


/**
 *
 * DESC：
 * Created on 2021/1/7
 *FULogger
 */
object FULogger {
    enum class LogLevel(ordinal: Int) {
        TRACE(0), //调试日志，每帧多次
        DEBUG(1), //调试日志，每帧一次或多次信息
        INFO(2),  //正常信息日志，程序运行过程中出现一次的信息，系统信息等
        WARN(3),  //警告级日志
        ERROR(4), //错误级日志
        CRITICAL(5), //错误且影响程序正常运行日志
        OFF(6); //关闭日志输出
    }


    private var _logLevel: LogLevel = LogLevel.OFF

    /**
     * 设置日志等级
     * @param level LogLevel
     */
    internal fun setLogLevel(level: LogLevel) {
        _logLevel = level
    }

    /**
     * 打印任务堆栈信息
     */
    @JvmStatic
    fun printCallStack() {
        val ex = Throwable()
        val stackElements = ex.stackTrace
        for (i in stackElements.indices) {
            print(stackElements[i].className + "/t")
            print(stackElements[i].fileName + "/t")
            print(stackElements[i].lineNumber.toString() + "/t")
            println(stackElements[i].methodName)
            println("-----------------------------------")
        }
    }


    /**
     * 调式日志
     * @param tag String
     * @param msg String
     */
    @JvmStatic
    fun t(tag: String, msg: String) {
        if (_logLevel.ordinal <= LogLevel.TRACE.ordinal) {
            Log.v(tag, msg)
        }
    }

    /**
     * 调式日志
     * @param tag String
     * @param msg String
     */
    @JvmStatic
    fun d(tag: String, msg: String) {
        if (_logLevel.ordinal <= LogLevel.DEBUG.ordinal) {
            Log.d(tag, msg)
        }
    }

    /**
     * 运行日志
     * @param tag String
     * @param msg String
     */
    @JvmStatic
    fun i(tag: String, msg: String) {
        if (_logLevel.ordinal <= LogLevel.INFO.ordinal) {
            Log.i(tag, msg)
        }
    }

    /**
     * 警告级日志
     * @param tag String
     * @param msg String
     */
    @JvmStatic
    fun w(tag: String, msg: String) {
        if (_logLevel.ordinal <= LogLevel.WARN.ordinal) {
            Log.w(tag, msg)
        }
    }

    /**
     * 错误级日志提醒
     * @param tag String
     * @param msg String
     */
    @JvmStatic
    fun e(tag: String, msg: String) {
        if (_logLevel.ordinal <= LogLevel.ERROR.ordinal) {
            Log.e(tag, msg)
        }
    }

    /**
     * 错误且影响程序正常运行日志日志提醒
     * @param tag String
     * @param msg String
     */
    @JvmStatic
    fun c(tag: String, msg: String) {
        if (_logLevel.ordinal <= LogLevel.CRITICAL.ordinal) {
            Log.e(tag, msg)
        }
    }

}