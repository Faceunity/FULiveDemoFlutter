package com.faceunity.core.media.rgba;

import android.opengl.GLES20;

import com.faceunity.core.callback.OnColorReadCallback;

import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * DESC：颜色取色
 * Created on 2021/3/15
 */
public class RGBAPicker {

    /**
     * 锚点取色
     * @param anchorX
     * @param anchorY
     * @param listener
     */
    public static void readRgba(int anchorX, int anchorY, OnColorReadCallback listener) {
        byte[] byteArray = new byte[4];
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        GLES20.glReadPixels(anchorX, anchorY, 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, byteBuffer);
        byteBuffer.rewind();
        byteBuffer.get(byteArray);
        int r = ((int) byteArray[0]) & 0xFF;
        int g = ((int) byteArray[1]) & 0xFF;
        int b = ((int) byteArray[2]) & 0xFF;
        int a = ((int) byteArray[3]) & 0xFF;
        listener.onReadRgba(r, g, b, a);
    }





}
