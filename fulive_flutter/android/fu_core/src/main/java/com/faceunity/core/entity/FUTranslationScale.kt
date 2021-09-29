package com.faceunity.core.entity

import com.faceunity.core.utils.DecimalUtils


/**
 *
 * Created on 2021/1/20
 *
 */
data class FUTranslationScale(var x: Float, var y: Float, var z: Float) {

    /**
     * 转换成数组
     * @return DoubleArray
     */
    fun toDataArray(): FloatArray = floatArrayOf(x, y, z)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUTranslationScale

        return DecimalUtils.floatEquals(other.x, x)
                && DecimalUtils.floatEquals(other.y, y)
                && DecimalUtils.floatEquals(other.z, z)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

}