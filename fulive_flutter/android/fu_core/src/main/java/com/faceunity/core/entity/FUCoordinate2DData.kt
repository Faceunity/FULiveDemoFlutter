package com.faceunity.core.entity

import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：X.Y 位置信息
 * Created on 2021/1/20
 *
 */
data class FUCoordinate2DData(var positionX: Double, var positionY: Double) {

    /**
     * 转换成数组
     * @return DoubleArray
     */
    fun toDataArray(): DoubleArray = doubleArrayOf(positionX, positionY)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUCoordinate2DData

        return DecimalUtils.doubleEquals(other.positionX, positionX)
                && DecimalUtils.doubleEquals(other.positionY, positionY)
    }

    override fun hashCode(): Int {
        var result = positionX.hashCode()
        result = 31 * result + positionY.hashCode()
        return result
    }

}