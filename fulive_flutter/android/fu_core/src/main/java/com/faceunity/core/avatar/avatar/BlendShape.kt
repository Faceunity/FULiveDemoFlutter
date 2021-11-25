package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute


/**
 *
 * DESC：BlendShape混合
 * Created on 2021/5/14
 *
 */
class BlendShape : BaseAvatarAttribute() {


    /**
     * 开启或关闭Blendshape混合：value = 1.0表示开启，value = 0.0表示不开启
     */
    var enableExpressionBlend: Boolean? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.enableInstanceExpressionBlend(avatarId, it)
                }
            }
            field = value
        }

    /**
     * blend_expression的权重
     */
    var inputBlendShapeWeight: FloatArray? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setInstanceExpressionWeight0(avatarId, it)
                }
            }
            field = value
        }

    /**
     * 算法检测返回的表情或者加载的动画表情系数数组的权重
     */
    var systemBlendShapeWeight: FloatArray? = null
        set(value) {
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setInstanceExpressionWeight1(avatarId, it)
                }
            }
            field = value
        }

    /**
     * blend_expression是用户输入的bs系数数组，取值为0~1，序号0-45代表基表情bs，46-56代表口腔bs，57-66代表舌头bs
     * @param expression FloatArray
     */
    fun updateInputBlendShape(expression: FloatArray) {
        mAvatarController.setInstanceBlendExpression(avatarId, expression)
    }

    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        enableExpressionBlend?.let {
            params["enableInstanceExpressionBlend"] = { mAvatarController.enableInstanceExpressionBlend(avatarId, it, false) }
        }
        inputBlendShapeWeight?.let {
            params["setInstanceExpressionWeight0"] = { mAvatarController.setInstanceExpressionWeight0(avatarId, it, false) }
        }
        systemBlendShapeWeight?.let {
            params["setInstanceExpressionWeight1"] = { mAvatarController.setInstanceExpressionWeight1(avatarId, it, false) }
        }
        hasLoaded = true
    }

    /**
     * 数据克隆
     * @param blendShape BlendShape
     */
    fun clone(blendShape: BlendShape){
      enableExpressionBlend = blendShape.enableExpressionBlend
      inputBlendShapeWeight = blendShape.inputBlendShapeWeight
      systemBlendShapeWeight = blendShape.systemBlendShapeWeight
    }


}