package com.faceunity.core.controller.hairBeauty


/**
 *
 * DESC：
 * Created on 2020/12/10
 *
 */
object HairBeautyParam {

    /*  发色索引  */
    const val INDEX = "Index"//单色Bundle对应范围0-7，渐变色Bundle对应范围0-4

    /* 发色强度   */
    const val INTENSITY = "Strength"//范围[0-1]，0表示不显示

    /**
     * 单色美发道具
     */

    /* 发光泽度   */
    const val SHINE = "Shine "//更改头发光泽度, 取值范围：0.0~3.0， 0.0为无光泽，3.0为最大光泽度。

    /* 更改发色   */
    const val Col_L = "Col_L"//Col_L = L/100.0，L为LAB颜色空间的L值。

    /* 更改发色   */
    const val Col_A = "Col_A"//Col_A = A/254.0 + 0.5，A为LAB颜色空间的A值。

    /* 更改发色   */
    const val Col_B = "Col_B "//B/254.0 + 0.5, B为LAB颜色空间的B值。

    /**
     * 渐变色美发道具
     */


    /* 发光泽度  0 */
    const val SHINE_0 = "Shine0  "//更改头发光泽度0，取值范围：0.0~4.0， 0.0为无光泽，4.0为最大光泽度。

    /* 发光泽度  1 */
    const val SHINE_1 = "Shine1  "//更改头发光泽度1，取值范围：0.0~4.0， 0.0为无光泽，4.0为最大光泽度。

    /* 更改发色 0  */
    const val Col0_L = "Col0_L "//Col_L = L/100.0，L为LAB颜色空间的L值。

    /* 更改发色 0  */
    const val Col0_A = "Col0_A"//Col_A = A/254.0 + 0.5，A为LAB颜色空间的A值。

    /* 更改发色 0  */
    const val Col0_B = "Col0_B "//B/254.0 + 0.5, B为LAB颜色空间的B值。

    /* 更改发色 1  */
    const val Col1_L = "Col1_L "//Col_L = L/100.0，L为LAB颜色空间的L值。

    /* 更改发色 1  */
    const val Col1_A = "Col1_A"//Col_A = A/254.0 + 0.5，A为LAB颜色空间的A值。

    /* 更改发色 1  */
    const val Col1_B = "Col1_B "//B/254.0 + 0.5, B为LAB颜色空间的B值。
}