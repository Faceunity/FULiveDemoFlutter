package com.faceunity.core.enumeration

import com.faceunity.wrapper.faceunity


/**
 *
 * DESC：输入纹理类型
 * Created on 2021/1/25
 *
 */
enum class FUInputTextureEnum(val type: Int) {
    FU_ADM_FLAG_COMMON_TEXTURE(0),//	普通纹理
    FU_ADM_FLAG_EXTERNAL_OES_TEXTURE(faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE),//	传入的纹理为OpenGL external OES 纹理
    FU_ADM_FLAG_NV21_TEXTURE(faceunity.FU_ADM_FLAG_NV21_TEXTURE),//  传入的纹理为 NV21 数据格式
    FU_ADM_FLAG_I420_TEXTURE(faceunity.FU_ADM_FLAG_I420_TEXTURE) //   传入的纹理为 I420 数据格式
}