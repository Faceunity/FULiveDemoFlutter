package com.faceunity.core.enumeration


/**
 *
 * DESC：旋转矩阵
 * Created on 2021/2/22
 *
 */
enum class FUTransformMatrixEnum(val index: Int) {
    CCROT0(0),
    CCROT90(1),
    CCROT180(2),
    CCROT270(3),
    CCROT0_FLIPVERTICAL(4),
    CCROT0_FLIPHORIZONTAL(5),
    CCROT90_FLIPVERTICAL(6),
    CCROT90_FLIPHORIZONTAL(7);
}