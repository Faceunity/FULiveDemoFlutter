package com.faceunity.core.media.photo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;

import com.faceunity.core.program.ProgramTexture2dWithAlpha;
import com.faceunity.core.program.ProgramTextureOES;
import com.faceunity.core.utils.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

/**
 * DESC：拍照
 * Created on 2021/3/12
 */
public class PhotoRecordHelper {

    private OnPhotoRecordingListener mOnPhotoRecordingListener;

    public PhotoRecordHelper(OnPhotoRecordingListener listener) {
        mOnPhotoRecordingListener = listener;
    }


    /**
     * 保存图片
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     */
    public void sendRecordingData(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight) {
        glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, false);
    }

    /**
     * 保存图片
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     */
    public void sendRecordingData(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight, boolean isOes) {
        glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, isOes);
    }

    /**
     * 将纹理转换成Bitmap
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     * @param isOes
     */
    private void glReadBitmap(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight, boolean isOes) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
        int[] viewport = new int[4];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
        GLES20.glViewport(0, 0, texWidth, texHeight);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (isOes) {
            new ProgramTextureOES().drawFrame(texId, texMatrix, mvpMatrix);
        } else {
            new ProgramTexture2dWithAlpha().drawFrame(texId, texMatrix, mvpMatrix);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(texWidth * texHeight * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glFinish();
        GLES20.glReadPixels(0, 0, texWidth, texHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);
        GlUtil.checkGlError("glReadPixels");
        buffer.rewind();
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteTextures(1, textures, 0);
        GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
        // ref: https://www.programcreek.com/java-api-examples/?class=android.opengl.GLES20&method=glReadPixels
        new Thread(() -> {
            Bitmap bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buffer);
            Matrix matrix = new Matrix();
            matrix.preScale(1f, -1f);
            Bitmap finalBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
            bmp.recycle();
            mOnPhotoRecordingListener.onRecordSuccess(finalBmp);
        }).start();

    }


}
