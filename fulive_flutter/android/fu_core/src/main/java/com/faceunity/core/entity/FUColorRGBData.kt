package com.faceunity.core.entity

import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：
 * Created on 2021/1/13
 *
 */
data class FUColorRGBData @JvmOverloads constructor(val red: Double, val green: Double, val blue: Double, val alpha: Double = -1.0) {


    fun toColorArray(): DoubleArray {
        return if (alpha < 0) {
            val array = DoubleArray(3)
            array[0] = red
            array[1] = green
            array[2] = blue
            array
        } else {
            val array = DoubleArray(4)
            array[0] = red
            array[1] = green
            array[2] = blue
            array[3] = alpha
            array
        }
    }

    fun toScaleColorArray(): DoubleArray {
        return if (alpha < 0) {
            val array = DoubleArray(3)
            array[0] = red / 255
            array[1] = green / 255
            array[2] = blue / 255
            array
        } else {
            val array = DoubleArray(4)
            array[0] = red / 255
            array[1] = green / 255
            array[2] = blue / 255
            array[3] = alpha / 255
            array
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUColorRGBData

        return DecimalUtils.doubleEquals(other.alpha, alpha)
                && DecimalUtils.doubleEquals(other.blue, blue)
                && DecimalUtils.doubleEquals(other.green, green)
                && DecimalUtils.doubleEquals(other.red, red)
    }

    override fun hashCode(): Int {
        var result = red.hashCode()
        result = 31 * result + green.hashCode()
        result = 31 * result + blue.hashCode()
        result = 31 * result + alpha.hashCode()
        return result
    }


}