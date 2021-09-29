package com.faceunity.core.callback

import android.graphics.Bitmap

/**
 *
 * DESC：拍照功能
 * Created on 2020/12/7
 *
 */
interface OnTakePictureCallback {

    /**
     * 获取到Bitmap对象
     *
     * @param bitmap
     */
    fun onGetBitmap(bitmap: Bitmap)
}