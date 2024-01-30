package com.faceunity.fuliveplugin.fulive_plugin.modules

import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.core.utils.FULogger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.reflect.KFunction

/**
 *
 * @author benyq
 * @date 11/13/2023
 *
 */
interface BaseModulePlugin {

    private val tag get() = tag()
    val renderKit: FURenderKit
        get() = FURenderKit.getInstance()

    fun methods(): Map<String, (Map<String, Any>, MethodChannel.Result) -> Any>
    fun tag(): String

    fun handleMethod(call: MethodCall, result: MethodChannel.Result) {
        val method: String = call.method
        val realMethod = methods()[method]
        val params = parseArguments(call.arguments as? Map<*, *>?)
        FULogger.d(tag, "method: $method, params: $params")
        realMethod?.invoke(params, result) ?: let {
            FULogger.e(tag, "方法: $method 未实现")
        }
    }

    private fun parseArguments(arguments: Map<*, *>?): Map<String, Any> {
        val params = mutableMapOf<String, Any>()
        (arguments?.get("arguments") as? List<*>)?.forEach {
            if (it is Map<*, *>) {
                val map = it
                val key = map.keys.first() as String
                val value = map[key]
                params[key] = value!!
            }
        }
        return params
    }

    fun containsMethod(method: String): Boolean {
        return methods().containsKey(method)
    }

    fun Map<String, Any>.getString(key: String): String? {
        return this[key] as? String
    }

    fun Map<String, Any>.getBoolean(key: String): Boolean? {
        return this[key] as? Boolean
    }

    fun Map<String, Any>.getDouble(key: String): Double? {
        return this[key] as? Double
    }

    fun Map<String, Any>.getInt(key: String): Int? {
        return this[key] as? Int
    }


}

