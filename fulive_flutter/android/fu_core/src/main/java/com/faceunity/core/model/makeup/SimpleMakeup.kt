package com.faceunity.core.model.makeup

import com.faceunity.core.controller.makeup.MakeupParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import java.util.LinkedHashMap


/**
 *
 * DESC：简易版美妆
 * Created on 2021/4/23
 *
 */
open class SimpleMakeup(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = mMakeupController


    private val mMakeupController by lazy { FURenderBridge.getInstance().mMakeupController }

    protected var combined: FUBundleData? = null
        set(value) {
            field = value
            updateMakeupBundle(MakeupParam.COMBINATION, field)
        }

    /* 美妆整体强度 */
    var makeupIntensity = 1.0
        //范围 [0-1] 0.0为不显示
        set(value) {
            field = value
            updateAttributesBackground(MakeupParam.MAKEUP_INTENSITY, value)
        }

    /**
     * 设置组合妆容
     * @param controlBundle FUBundleData?
     */
    open fun setCombinedConfig(controlBundle: FUBundleData?) {
        combined = controlBundle
    }

    /**
     * 获取组合妆容
     * @return FUBundleData?
     */
    fun getCombinedConfig(): FUBundleData? {
        return combined
    }

    /**
     * 更新道具
     * @param key String
     * @param bundle FUBundleData?
     */
    protected fun updateMakeupBundle(key: String, bundle: FUBundleData?) {
        updateCustomUnit(key) { mMakeupController.updateItemBundle(getCurrentSign(), key, bundle) }
    }


    override fun buildParams(): LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        combined?.let { params[MakeupParam.COMBINATION] = it }
        params[MakeupParam.MAKEUP_INTENSITY] = makeupIntensity
        return params
    }


}