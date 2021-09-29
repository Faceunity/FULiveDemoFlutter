package com.faceunity.core.entity


/**
 *
 * DESC：
 * Created on 2021/1/29
 *
 */
data class FUColorLABData(val Col_L: Int, val Col_A: Int, val Col_B: Int) {

    fun coverLABParam(key: String, params: LinkedHashMap<String, Any>) {
        params["${key}_L"] = Col_L / 100.0
        params["${key}_A"] = (Col_A + 128) / 255.0
        params["${key}_B"] = (Col_B + 128) / 255.0
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUColorLABData

        if (Col_L != other.Col_L) return false
        if (Col_A != other.Col_A) return false
        if (Col_B != other.Col_B) return false
        return true
    }

    override fun hashCode(): Int {
        var result = Col_L
        result = 31 * result + Col_A
        result = 31 * result + Col_B
        return result
    }

}