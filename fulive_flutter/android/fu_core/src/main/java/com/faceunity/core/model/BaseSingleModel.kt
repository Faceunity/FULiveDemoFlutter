package com.faceunity.core.model

import com.faceunity.core.callback.OnControllerBundleLoadCallback
import com.faceunity.core.controller.BaseSingleController
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUFeaturesData
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * DESC：
 * Created on 2021/2/8
 *
 */
abstract class BaseSingleModel(val controlBundle: FUBundleData) {

    /*当前模型标识*/
    private var mSign = -1L

    /*是否正在异步加载Bundle文件*/
    private var isControllerBundleLoading = false

    /*操作缓存*/
    private val modelUnitCache = ConcurrentHashMap<String, () -> Unit>()//异步加载过程中的自定义函数缓存


    //region 抽象实现
    /**
     * 获取模型控制器
     * @return BaseSingleController
     */
    protected abstract fun getModelController(): BaseSingleController

    /**
     * 属性参数构造
     * @return LinkedHashMap<String, Any>
     */
    protected abstract fun buildParams(): LinkedHashMap<String, Any>

    /**
     * 构造FUFeaturesData
     * @return FUFeaturesData
     */
    internal open fun buildFUFeaturesData(): FUFeaturesData = FUFeaturesData(controlBundle, buildParams(), enable)

    //endregion 抽象实现

    //region 渲染实现
    /**
     * 加载到FURenderKit
     */
    internal fun loadToRenderKit() {
        isControllerBundleLoading = true
        getModelController().loadControllerBundle(buildFUFeaturesData(), mLoadCallback)
    }


    /**
     * 异步加载完成回调，并将在加载过程中变更过的参数重置一次
     */
    private val mLoadCallback = object : OnControllerBundleLoadCallback {
        override fun onLoadSuccess(sign: Long) {
            mSign = sign
            val iterator = modelUnitCache.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                modelUnitCache.remove(entry.key)
                entry.value.invoke()
            }
            isControllerBundleLoading = false
        }
    }


    //endregion 渲染实现
    //region 属性控制
    /**
     * 获取当前标识
     * @return Long
     */
    internal fun getCurrentSign() = mSign


    /**
     * 模型是否加载开关
     */
    var enable = true
        set(value) {
            if (value == field) return
            field = value
            if (isControllerBundleLoading) {
                modelUnitCache["enable"] = {
                    getModelController().setBundleEnable(getCurrentSign(), field)
                }
            } else {
                getModelController().setBundleEnable(getCurrentSign(), field)
            }
        }


    /**
     * 更新属性参数
     * @param key String
     * @param value Any
     */
    protected fun updateAttributes(key: String, value: Any) {
        if (isControllerBundleLoading) {
            modelUnitCache[key] = {
                getModelController().setItemParam(getCurrentSign(), key, value)
            }
        } else {
            getModelController().setItemParam(getCurrentSign(), key, value)
        }
    }

    /**
     * 更新属性参数
     * @param key String
     * @param value Any
     */
    protected fun updateAttributesGL(key: String, value: Any) {
        if (isControllerBundleLoading) {
            modelUnitCache[key] = {
                getModelController().setItemParamGL(getCurrentSign(), key, value)
            }
        } else {
            getModelController().setItemParamGL(getCurrentSign(), key, value)
        }
    }

    /**
     * 更新属性参数
     * @param key String
     * @param value Any
     */
    protected fun updateAttributesBackground(key: String, value: Any) {
        if (isControllerBundleLoading) {
            modelUnitCache[key] = {
                getModelController().setItemParamBackground(getCurrentSign(), key, value)
            }
        } else {
            getModelController().setItemParamBackground(getCurrentSign(), key, value)
        }
    }

    /**
     * 更新属性参数
     * @param param LinkedHashMap<String, Any>
     */
    protected fun updateAttributesBackground(key: String, param: LinkedHashMap<String, Any>) {
        if (isControllerBundleLoading) {
            modelUnitCache[key] = { getModelController().setItemParamBackground(getCurrentSign(), param) }
        } else {
            getModelController().setItemParamBackground(getCurrentSign(), param)
        }
    }


    /**
     * 更新属性参数
     * @param param LinkedHashMap<String, Any>
     */
    protected fun updateAttributes(key: String, param: LinkedHashMap<String, Any>) {
        if (isControllerBundleLoading) {
            modelUnitCache[key] = { getModelController().setItemParam(getCurrentSign(), param) }
        } else {
            getModelController().setItemParam(getCurrentSign(), param)
        }
    }

    /**
     * 更新图片纹理
     * @param name String
     * @param path String?
     */
    protected fun updateItemTex(name: String, path: String?) {
        if (isControllerBundleLoading) {
            if (path == null) {
                modelUnitCache[name] = { getModelController().deleteItemTex(getCurrentSign(), name) }
            } else {
                modelUnitCache[name] = { getModelController().createItemTex(getCurrentSign(), name, path) }
            }
        } else {
            if (path == null) {
                getModelController().deleteItemTex(getCurrentSign(), name)
            } else {
                getModelController().createItemTex(getCurrentSign(), name, path)
            }
        }
    }


    /**
     * 更换自定义函数
     * @param key String
     * @param unity  () -> Unit
     */
    protected fun updateCustomUnit(key: String, unity: () -> Unit) {
        if (isControllerBundleLoading) {
            modelUnitCache[key] = unity
        } else {
            unity.invoke()
        }
    }


    //endregion 属性控制


}