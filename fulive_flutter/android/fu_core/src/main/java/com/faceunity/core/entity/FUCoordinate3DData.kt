package com.faceunity.core.entity

import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：X.Y.Z 位置信息
 * Created on 2021/1/20
 *
 */
data class FUCoordinate3DData(var positionX: Double, var positionY: Double, var positionZ: Double) {

    /**
     * 转换成数组
     * @return DoubleArray
     */
    fun toDataArray(): DoubleArray = doubleArrayOf(positionX, positionY, positionZ)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUCoordinate3DData

        return DecimalUtils.doubleEquals(other.positionX, positionX)
                && DecimalUtils.doubleEquals(other.positionY, positionY)
                && DecimalUtils.doubleEquals(other.positionZ, positionZ)
    }

    override fun hashCode(): Int {
        var result = positionX.hashCode()
        result = 31 * result + positionY.hashCode()
        result = 31 * result + positionZ.hashCode()
        return result
    }

}