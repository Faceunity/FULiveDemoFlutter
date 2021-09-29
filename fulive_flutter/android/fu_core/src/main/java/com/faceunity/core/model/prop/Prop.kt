package com.faceunity.core.model.prop

import com.faceunity.core.controller.prop.PropParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.model.prop.animoji.Animoji
import com.faceunity.core.model.prop.arMask.ARMask
import com.faceunity.core.model.prop.bgSegCustom.BgSegCustom
import com.faceunity.core.model.prop.bigHead.BigHead
import com.faceunity.core.model.prop.expression.ExpressionRecognition
import com.faceunity.core.model.prop.faceWarp.FaceWarp
import com.faceunity.core.model.prop.gesture.GestureRecognition
import com.faceunity.core.model.prop.humanOutline.HumanOutline
import com.faceunity.core.model.prop.portraitSegment.PortraitSegment
import com.faceunity.core.model.prop.sticker.FineSticker
import com.faceunity.core.model.prop.sticker.Sticker
import com.faceunity.core.support.FURenderBridge
import java.util.LinkedHashMap


/**
 *
 * DESC：
 * Created on 2021/1/30
 *
 */
abstract class Prop(val controlBundle: FUBundleData) {

    /*道具控制器*/
    private val mPropController by lazy { FURenderBridge.getInstance().mPropContainerController }

    /*当前模型标识*/
    val propId = System.nanoTime()

    /**
     * 道具是否显示开关
     */
    var enable = true
        set(value) {
            if (value == field) return
            field = value
            mPropController.setBundleEnable(propId, field)
        }

    /**
     * 获取道具参数
     * @return LinkedHashMap<String, Any>
     */
    internal open fun buildParams(): LinkedHashMap<String, Any> = LinkedHashMap()


    /**
     * 构造FUFeaturesData
     * @return FUFeaturesData
     */
    internal fun buildFUFeaturesData(): FUFeaturesData {
        return FUFeaturesData(controlBundle, buildParams(), enable, buildRemark(), propId)
    }

    /**
     * 构造道具参数
     * @return LinkedHashMap<String, Any>
     */
    internal open fun buildRemark(): LinkedHashMap<String, Any> {
        val remark = LinkedHashMap<String, Any>()
        val typeProp = when (this) {
            is Sticker -> PropParam.PROP_TYPE_STICKER
            is Animoji -> PropParam.PROP_TYPE_ANIMOJI
            is ARMask -> PropParam.PROP_TYPE_AR_MASK
            is HumanOutline -> PropParam.PROP_TYPE_HUMAN_OUTLINE
            is PortraitSegment -> PropParam.PROP_TYPE_PORTRAIT_SEGMENT
            is BgSegCustom -> PropParam.PROP_TYPE_BG_SEG_CUSTOM
            is BigHead -> PropParam.PROP_TYPE_BIG_HEAD
            is ExpressionRecognition -> PropParam.PROP_TYPE_EXPRESSION
            is FaceWarp -> PropParam.PROP_TYPE_FACE_WARP
            is GestureRecognition -> PropParam.PROP_TYPE_GESTURE
            is FineSticker -> PropParam.PROP_TYPE_FINE_STICKER
            else -> PropParam.PROP_TYPE_STICKER
        }
        remark[PropParam.PROP_TYPE] = typeProp
        return remark
    }


    /**
     * 更新属性参数
     * @param key String
     * @param value Any
     */
    protected fun updateAttributesGL(key: String, value: Any) {
        mPropController.setItemParamGL(propId, key, value)
    }

    /**
     * 更新属性参数
     * @param key String
     * @param value Any
     */
    protected fun updateAttributes(key: String, value: Any) {
        mPropController.setItemParam(propId, key, value)
    }

    /**
     * 创建属性纹理
     * @param name String
     * @param rgba ByteArray
     * @param width Int
     * @param height Int
     */
    protected fun createTexForItem(name: String, rgba: ByteArray, width: Int, height: Int) {
        mPropController.createTexForItem(propId, name, rgba, width, height)
    }

    /**
     * 移除属性纹理
     * @param key String
     * @param name String
     */
    protected fun deleteTexForItem(name: String) {
        mPropController.deleteTexForItem(propId, name)
    }


}