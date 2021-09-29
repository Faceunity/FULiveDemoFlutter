package com.faceunity.core.entity

import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：X.Y.Z 抖动
 * Created on 2021/1/20
 *
 */
data class FUAvatarAnimFilterParams(var nBufferFrames: Int, var pos: Float, var angle: Float) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUAvatarAnimFilterParams

        return DecimalUtils.floatEquals(other.nBufferFrames.toFloat(), nBufferFrames.toFloat())
                && DecimalUtils.floatEquals(other.pos, pos)
                && DecimalUtils.floatEquals(other.angle, angle)
    }

    override fun hashCode(): Int {
        var result = nBufferFrames.hashCode()
        result = 31 * result + pos.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

}