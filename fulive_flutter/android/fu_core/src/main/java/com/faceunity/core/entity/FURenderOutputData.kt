package com.faceunity.core.entity

import java.lang.StringBuilder

/**
 *
 * DESC：
 * Created on 2021/1/22
 *
 */
class FURenderOutputData(
    var texture: FUTexture? = null,//纹理数据
    var image: FUImageBuffer? = null//图象数据
) {
    data class FUTexture(
        var texId: Int,//纹理id
        var width: Int,//宽
        var height: Int//高
    )

    data class FUImageBuffer @JvmOverloads constructor(
        var width: Int,//宽
        var height: Int,//高
        var buffer: ByteArray? = null,//数据Buffer
        var buffer1: ByteArray? = null,//YUV_BUFFER 对应U-Buffer
        var buffer2: ByteArray? = null,//YUV_BUFFER 对应V-Buffer
        var stride: Int = 0,
        var stride1: Int = 0,
        var stride2: Int = 0
    )

    fun printMsg(): String {
        val buffer = StringBuilder()
        if (texture == null) {
            buffer.append("texture is null")
        } else {
            buffer.append("texId:${texture!!.texId}  texWdith:${texture!!.width}  texHeight:${texture!!.height}")
        }
        if (image == null) {
            buffer.append("    image is null")
        } else {
            buffer.append(
                "    imgWdith:${image!!.width}  imgHeight:${image!!.height}  buffer Size:${image!!.buffer?.size} buffer1 Size:${image!!.buffer1?.size}" +
                        "   buffer2 Size:${image!!.buffer2?.size}   stride:${image?.stride}    stride1:${image?.stride1}    stride2:${image?.stride2}"
            )
        }
        return buffer.toString()
    }


}