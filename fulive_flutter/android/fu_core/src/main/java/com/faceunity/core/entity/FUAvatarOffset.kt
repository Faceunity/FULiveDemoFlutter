package com.faceunity.core.entity

import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：X.Y.Z 偏移量
 * Created on 2021/1/20
 *
 */
data class FUAvatarOffset(var offsetX: Float, var offsetY: Float, var offsetZ: Float) {

    /**
     * 转换成数组
     * @return DoubleArray
     */
    fun toDataArray(): FloatArray = floatArrayOf(offsetX, offsetY, offsetZ)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUAvatarOffset

        return DecimalUtils.floatEquals(other.offsetX, offsetX)
                && DecimalUtils.floatEquals(other.offsetY, offsetY)
                && DecimalUtils.floatEquals(other.offsetZ, offsetZ)
    }

    override fun hashCode(): Int {
        var result = offsetX.hashCode()
        result = 31 * result + offsetY.hashCode()
        result = 31 * result + offsetZ.hashCode()
        return result
    }

}