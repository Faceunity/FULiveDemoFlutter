package com.faceunity.core.entity

/**
 * 图片对象模型
 * @property width Int
 * @property height Int
 * @property bytes ByteArray
 * @constructor
 */
data class TextureImage(val width: Int, val height: Int, val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextureImage

        if (width != other.width) return false
        if (height != other.height) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}