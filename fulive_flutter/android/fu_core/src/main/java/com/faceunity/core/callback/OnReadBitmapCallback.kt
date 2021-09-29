package com.faceunity.core.callback

import android.graphics.Bitmap

/**
 *
 * DESC： 纹理转换成图片回调
 * Created on 2020/11/16
 *
 */
interface OnReadBitmapCallback {

    /**
     * 读取图片完成
     *
     * @param bitmap
     */
    fun onReadBitmap(bitmap: Bitmap)
}