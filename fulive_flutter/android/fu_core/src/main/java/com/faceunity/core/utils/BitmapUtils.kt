package com.faceunity.core.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES20
import android.os.AsyncTask
import com.faceunity.core.callback.OnReadBitmapCallback
import com.faceunity.core.program.ProgramTexture2dWithAlpha
import com.faceunity.core.program.ProgramTextureOES
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10
import kotlin.math.ceil


/**
 *
 * DESC：
 * Created on 2020/12/8
 *
 */
object BitmapUtils {
    /**
     * 读取图片（glReadPixels）
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     * @param callback
     * @param isOes     是否是OES纹理
     */
    fun glReadBitmap(
        texId: Int,
        texMatrix: FloatArray,
        mvpMatrix: FloatArray,
        texWidth: Int,
        texHeight: Int,
        callback: OnReadBitmapCallback,
        isOes: Boolean
    ) {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
        val frameBuffers = IntArray(1)
        GLES20.glGenFramebuffers(1, frameBuffers, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0])
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0)
        val viewport = IntArray(4)
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0)
        GLES20.glViewport(0, 0, texWidth, texHeight)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        if (isOes) {
            ProgramTextureOES().drawFrame(texId, texMatrix, mvpMatrix)
        } else {
            ProgramTexture2dWithAlpha().drawFrame(texId, texMatrix, mvpMatrix)
        }
        val buffer = ByteBuffer.allocateDirect(texWidth * texHeight * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        GLES20.glFinish()
        GLES20.glReadPixels(0, 0, texWidth, texHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer)
        GlUtil.checkGlError("glReadPixels")
        buffer.rewind()
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glDeleteTextures(1, textures, 0)
        GLES20.glDeleteFramebuffers(1, frameBuffers, 0)
        // ref: https://www.programcreek.com/java-api-examples/?class=android.opengl.GLES20&method=glReadPixels
        AsyncTask.execute {
            val bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(buffer)
            val matrix = Matrix()
            matrix.preScale(1f, -1f)
            val finalBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, false)
            bmp.recycle()
            callback.onReadBitmap(finalBmp)
        }
    }

    /**
     * 旋转 Bitmap
     *
     * @param bitmap
     * @param orientation
     * @return
     */
    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        var bitmap = bitmap
        if (orientation == 90 || orientation == 180 || orientation == 270) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        return bitmap
    }

    /**
     * bitmap 转 NV21 数据
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    fun getNV21(inputWidth: Int, inputHeight: Int, scaled: Bitmap, needRecycle: Boolean = true): ByteArray? {
        val argb = IntArray(inputWidth * inputHeight)
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)
        val yuv = ByteArray(
            inputHeight * inputWidth + 2 * ceil(inputHeight.toFloat() / 2.toDouble()).toInt() * ceil(inputWidth.toFloat() / 2.toDouble()).toInt()
        )
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight)
        if (needRecycle) {
            scaled.recycle()
        }
        return yuv
    }

    /**
     * ARGB 转 NV21 数据
     *
     * @param yuv420sp
     * @param argb
     * @param width
     * @param height
     */
    fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var Y: Int
        var U: Int
        var V: Int
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                A = argb[index] and -0x1000000 shr 24 // a is not used obviously
                R = argb[index] and 0xff0000 shr 16
                G = argb[index] and 0xff00 shr 8
                B = argb[index] and 0xff shr 0
                // well known RGB to YUV algorithm
                Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128
                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
//    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
//    pixel AND every other scanline.
                yuv420sp[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                    yuv420sp[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                }
                index++
            }
        }
    }

    fun decodeSampledBitmapFromResource(
        resource: Resources?,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? { // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resource, resId, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resource, resId, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int { // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
// height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}