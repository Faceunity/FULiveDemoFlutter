package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute
import com.faceunity.core.entity.FUAvatarAnimFilterParams
import com.faceunity.core.entity.FUAvatarOffset
import com.faceunity.core.entity.FUCoordinate3DData


/**
 *
 * DESC：角色位置
 * Created on 2021/5/13
 *
 */
class TransForm : BaseAvatarAttribute() {



    /**
     * 角色位置
     * positionX Double  X轴坐标 [-200, 200]
     * positionY Double Y轴坐标 范围[-600, 800]
     * positionZ Double Z轴坐标 范围[-3000, 600]
     **/


    var position: FUCoordinate3DData? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setInstanceTargetPosition(avatarId, it)
                }
            }
        }


    fun setPositionGL(data: FUCoordinate3DData?) {
        data?.let {
            position?.positionX = it.positionX
            position?.positionY = it.positionY
            position?.positionZ = it.positionZ
            if (hasLoaded) {
                mAvatarController.setInstanceTargetPosition(avatarId, it, false)
            }
        }
    }

    /**
     * 设置avatar相对人体的大小
     */
    var humanProcessorSetAvatarScale: Float = -1f
        set(value) {
            field = value
            if (value > 0)
                mAvatarController.humanProcessorSetAvatarScale(value)
        }

    /**
     * 偏移量
     */
    var humanProcessorSetAvatarGlobalOffset = FUAvatarOffset(0.0f, 0.0f, 0.0f)
        set(value) {
            field = value
            mAvatarController.humanProcessorSetAvatarGlobalOffset(humanProcessorSetAvatarGlobalOffset.offsetX, humanProcessorSetAvatarGlobalOffset.offsetY, humanProcessorSetAvatarGlobalOffset.offsetZ)
        }

    /**
     * 角色旋转角度取值范围[-1.0, 1.0]     360度
     */
    var rotate: Float? = null
        set(value) {
            field = value
            value?.let {
                if (hasLoaded) {
                    mAvatarController.setInstanceTargetAngle(avatarId, it)
                }
            }

        }

    /**
     * 旋转角色 取值范围[-1.0, 1.0]     360度
     * @param delta Float
     */
    fun setRotDelta(delta: Float) {
        mAvatarController.setInstanceRotDelta(avatarId, delta)
    }

    /**
     * 缩放角色 取值范围[-1.0, 1.0]
     * @param delta Float
     */
    fun setScaleDelta(delta: Float) {
        mAvatarController.setInstanceScaleDelta(avatarId, delta)
    }

    /**
     * 上下移动角色 取值范围[-1.0, 1.0]
     * @param delta Float
     */
    fun setTranslateDelta(delta: Float) {
        mAvatarController.setInstanceTranslateDelta(avatarId, delta)
    }
    /**
     * 抖动
     */
    var humanProcessorSetAvatarAnimFilterParams = FUAvatarAnimFilterParams(0, 0.0f, 0.0f)
        set(value) {
            field = value
            mAvatarController.humanProcessorSetAvatarAnimFilterParams(humanProcessorSetAvatarAnimFilterParams.nBufferFrames, humanProcessorSetAvatarAnimFilterParams.pos, humanProcessorSetAvatarAnimFilterParams.angle)
        }

    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        position?.let {
            params["setInstanceTargetPosition"] = { mAvatarController.setInstanceTargetPosition(avatarId, it, false) }
        }
        rotate?.let {
            params["setInstanceTargetAngle"] = { mAvatarController.setInstanceTargetAngle(avatarId, it, false) }
        }
        params["humanProcessorSetAvatarScale"] = { if (humanProcessorSetAvatarScale > 0) (mAvatarController.humanProcessorSetAvatarScale(humanProcessorSetAvatarScale)) }
        params["humanProcessorSetAvatarGlobalOffset"] =
                { mAvatarController.humanProcessorSetAvatarGlobalOffset(humanProcessorSetAvatarGlobalOffset.offsetX, humanProcessorSetAvatarGlobalOffset.offsetY, humanProcessorSetAvatarGlobalOffset.offsetZ) }
        params["humanProcessorSetAvatarAnimFilterParams"] =
                { mAvatarController.humanProcessorSetAvatarAnimFilterParams(humanProcessorSetAvatarAnimFilterParams.nBufferFrames, humanProcessorSetAvatarAnimFilterParams.pos, humanProcessorSetAvatarAnimFilterParams.angle) }

        hasLoaded = true
    }


    /**
     * 数据克隆
     * @param transForm TransForm
     */
    fun clone(transForm: TransForm) {
        position = transForm.position
        rotate = transForm.rotate
    }


}