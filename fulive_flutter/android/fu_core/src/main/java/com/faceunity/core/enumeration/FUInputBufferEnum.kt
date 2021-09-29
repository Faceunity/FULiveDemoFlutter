package com.faceunity.core.enumeration

import com.faceunity.wrapper.faceunity


/**
 *
 * DESC：输入buffer类型
 * Created on 2021/1/25
 *
 */
enum class FUInputBufferEnum(val type: Int) {
    FU_FORMAT_NV21_BUFFER(faceunity.FU_FORMAT_NV21_BUFFER),//	传入的数据为 NV21 数据格式
    FU_FORMAT_RGBA_BUFFER(faceunity.FU_FORMAT_RGBA_BUFFER),//	传入的数据为 RGBA 数据格式
    FU_FORMAT_I420_BUFFER(faceunity.FU_FORMAT_I420_BUFFER),//  传入的数据为 I420 数据格式
    FU_FORMAT_YUV_BUFFER(0)//  传入的数据为 YUV 数据格式
}