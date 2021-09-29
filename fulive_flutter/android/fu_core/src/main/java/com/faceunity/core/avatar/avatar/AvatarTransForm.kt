package com.faceunity.core.avatar.avatar

import com.faceunity.core.avatar.base.BaseAvatarAttribute
import com.faceunity.core.entity.FUAvatarAnimFilterParams
import com.faceunity.core.entity.FUAvatarOffset
import com.faceunity.core.entity.FUCoordinate3DData
import com.faceunity.core.support.SDKController

/**
 *设置角色位置 X轴水平向右，Y轴竖直向上，Z轴垂直屏幕向外
 * @constructor
 */
class AvatarTransForm : BaseAvatarAttribute() {

    /**
     * 角色位置
     * @param positionX Double  X轴坐标 [-200, 200]
     * @param positionY Double Y轴坐标 范围[-600, 800]
     * @param positionZ Double Z轴坐标 范围[-3000, 600]
     **/
    var position = FUCoordinate3DData(0.0, 0.0, 0.0)
        set(value) {
            field = value
            mAvatarController.setInstanceTargetPosition(avatarId, position.positionX, position.positionY, position.positionZ)
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
    internal fun loadInitParams(params: LinkedHashMap<String, () -> Unit>) {
        params["setInstanceTargetPosition"] = { mAvatarController.setInstanceTargetPosition(avatarId, position.positionX, position.positionY, position.positionZ) }
    }

    /**
     * 加载执行操作
     * @param params LinkedHashMap<String, Function0<Unit>>
     */
    internal fun loadParams(params: LinkedHashMap<String, () -> Unit>) {
        params["humanProcessorSetAvatarScale"] = { if (humanProcessorSetAvatarScale > 0) (mAvatarController.humanProcessorSetAvatarScale(humanProcessorSetAvatarScale)) }
        params["humanProcessorSetAvatarGlobalOffset"] =
            { mAvatarController.humanProcessorSetAvatarGlobalOffset(humanProcessorSetAvatarGlobalOffset.offsetX, humanProcessorSetAvatarGlobalOffset.offsetY, humanProcessorSetAvatarGlobalOffset.offsetZ) }
        params["humanProcessorSetAvatarAnimFilterParams"] =
            { mAvatarController.humanProcessorSetAvatarAnimFilterParams(humanProcessorSetAvatarAnimFilterParams.nBufferFrames, humanProcessorSetAvatarAnimFilterParams.pos, humanProcessorSetAvatarAnimFilterParams.angle) }
    }


//    /**
//     * 更新角色位置
//     * @param positionX Double  X轴坐标 [-200, 200]
//     * @param positionY Double Y轴坐标 范围[-600, 800]
//     * @param positionZ Double Z轴坐标 范围[-3000, 600]
//     * @param rotate Double 旋转角度 范围[0.0, 1.0]，0.0代表0度，1.0代表360度
//     * @param delay Int 过渡帧数 范围[1.0, 60.0]
//     */
//    @JvmOverloads
//    fun updateTransForm(
//        positionX: Double = position.positionX, positionY: Double = position.positionY,
//        positionZ: Double = position.positionZ, rotate: Double = this.rotate, delay: Int = 1
//    ) {
//        doSetParam { params ->
//            if (positionX != position.positionX || positionY != position.positionY || positionZ != position.positionZ) {
//                position.positionX = positionX
//                position.positionY = positionY
//                position.positionZ = positionZ
//                params["target_position"] = position.toDataArray()
//            }
//            if (rotate != this.rotate) {
//                this.rotate = rotate
//                params["target_angle"] = rotate
//            }
//            if (params.size == 0) {
//                return@doSetParam
//            }
//            params["reset_all"] = delay
//        }
//    }

//    /**
//     * 增量更改角色位置
//     * @param pYDelta Double  上下移动角色 取值范围[-1.0, 1.0]    100
//     * @param pZDelta Double 缩放角色 取值范围[-1.0, 1.0]         300
//     * @param rotateDelta Double 旋转角色 取值范围[-1.0, 1.0]     360度
//     * @param delay Int 过渡帧数 范围[1.0, 60.0]
//     */
//    fun updateTransDelta(pYDelta: Double = 0.0, pZDelta: Double = 0.0, rotateDelta: Double = 0.0) {
//        doSetParam { params ->
//            if (pYDelta != 0.0) {
//                params["translate_delta"] = pYDelta
//            }
//            if (pZDelta != 0.0) {
//                params["scale_delta"] = pZDelta
//            }
//            if (rotateDelta != 0.0) {
//                params["rot_delta"] = rotateDelta
//            }
//            if (params.size == 0) {
//                return@doSetParam
//            }
//        }
//    }
//
//    /**
//     * 获取角色在三维空间的位置
//     * @return DoubleArray?
//     */
//    fun getCurrentPosition(): DoubleArray? = getItemParam("current_position", DoubleArray::class) as? DoubleArray
//
//    /**
//     * 输入脸部mesh顶点序号获取其在屏幕空间的坐标
//     * @param number Int
//     * @return DoubleArray  array[0] 坐标x   array[1] 坐标y
//     */
//    fun queryVert(number: Int): DoubleArray? {
//        val parms = LinkedHashMap<String, Any>()
//        parms["query_vert"] = number
//        val x = getItemParam("query_vert_x", Double::class, parms) as? Double
//        val y = getItemParam("query_vert_y", Double::class, parms) as? Double
//        return if (x != null && y != null) {
//            doubleArrayOf(x, y)
//        } else {
//            null
//        }
//    }
//
//
//    /**
//     * 返回当前角色在模型空间的包围盒的左下角和右上角的坐标
//     * @return DoubleArray   返回数组[x0, y0, z0, x1, y1, z1]，[x0, y0, x0]表示左下角坐标，[x1, y1, z1]表示右上角坐标
//     */
//    fun getBoundingBox(): DoubleArray? {
//        val res = getItemParam("boundingbox", DoubleArray::class)
//        return res as? DoubleArray
//    }
//
//    /**
//     * 返回当前角色的中心在屏幕空间的二维坐标
//     * @return DoubleArray
//     */
//    fun getCurrentPositionInScreen(): DoubleArray? {
//        val res = getItemParam("target_position_in_screen_space", DoubleArray::class)
//        return res as? DoubleArray
//    }
//
//


}