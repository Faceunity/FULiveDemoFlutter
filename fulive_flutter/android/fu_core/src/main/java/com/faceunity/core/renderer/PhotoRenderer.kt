package com.faceunity.core.renderer

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.enumeration.*
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.infe.IPhotoRenderer
import com.faceunity.core.listener.OnGlRendererListener
import com.faceunity.core.utils.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *
 * DESC：渲染照片
 * Created on 2021/1/4
 *
 */

class PhotoRenderer(gLSurfaceView: GLSurfaceView?, private val photoPath: String, glRendererListener: OnGlRendererListener?) : BaseFURenderer(gLSurfaceView, glRendererListener), IPhotoRenderer {

    /**  加载本地文件保存初始大小 */
    private val requestPhotoWidth = 1080
    private val requestPhotoHeight = 1920


    /**渲染配置**/

    //region 初始化

    init {
        currentFURenderInputData.apply {
            currentFURenderInputData.texture = FURenderInputData.FUTexture(FUInputTextureEnum.FU_ADM_FLAG_COMMON_TEXTURE, 0)
            currentFURenderInputData.imageBuffer = FURenderInputData.FUImageBuffer(FUInputBufferEnum.FU_FORMAT_NV21_BUFFER)
            renderConfig.apply {
                externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE
                cameraFacing = CameraFacingEnum.CAMERA_BACK
                inputTextureMatrix = FUTransformMatrixEnum.CCROT0
                inputBufferMatrix = FUTransformMatrixEnum.CCROT0
            }
        }
        externalInputType = FUExternalInputEnum.EXTERNAL_INPUT_TYPE_IMAGE
        gLSurfaceView?.setEGLContextClientVersion(GlUtil.getSupportGlVersion(FURenderManager.mContext))
        gLSurfaceView?.setRenderer(this)
        gLSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }


    //endregion

    //region 生命周期调用
    /**Activity onResume**/
    override fun onResume() {
        if (isActivityPause) {
            gLSurfaceView?.onResume()
        }
        isActivityPause = false
    }


    /**Activity release**/
    override fun onPause() {
        isActivityPause = true
        val count = CountDownLatch(1)
        gLSurfaceView?.queueEvent(Runnable {
            destroyGlSurface()
            count.countDown()
        })
        try {
            count.await(500, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            // ignored
        }
        gLSurfaceView?.onPause()
    }

    /**Activity onDestroy**/
    override fun onDestroy() {
        glRendererListener = null
        gLSurfaceView = null
    }


    //endregion 生命周期调用

    //region  GLSurfaceView.Renderer相关

    override fun surfaceCreated(gl: GL10?, config: EGLConfig?) {
        val bitmap: Bitmap? = FileUtils.loadBitmapFromExternal(photoPath, requestPhotoWidth, requestPhotoHeight)
        bitmap?.let {
            originalTextId = GlUtil.createImageTexture(bitmap)
            currentFURenderInputData.texture?.texId = originalTextId
            originalWidth = bitmap.width
            originalHeight = bitmap.height
            currentFURenderInputData.apply {
                width = originalWidth
                height = originalHeight
                imageBuffer?.buffer = BitmapUtils.getNV21(originalWidth, originalHeight, bitmap)
            }
            LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS)
        }
    }

    /**
     * 根据视图宽高，初始化配置
     * @param gl GL10
     * @param width Int
     * @param height Int
     */
    override fun surfaceChanged(gl: GL10?, width: Int, height: Int) {
        defaultFUMvpMatrix = GlUtil.changeMvpMatrixInside(width.toFloat(), height.toFloat(), originalWidth.toFloat(), originalHeight.toFloat())
        smallViewMatrix = GlUtil.changeMvpMatrixCrop(90f, 160f, originalHeight.toFloat(), originalWidth.toFloat())
        originMvpMatrix = defaultFUMvpMatrix.copyOf()
        Matrix.scaleM(originMvpMatrix, 0, 1f, -1f, 1f)
    }

    override fun prepareRender(gl: GL10?) = (programTexture2d != null)

    override fun buildFURenderInputData(): FURenderInputData {
        return currentFURenderInputData
    }


    override fun drawRenderFrame(gl: GL10? ) {
        if (faceUnity2DTexId > 0 && renderSwitch) {
            programTexture2d!!.drawFrame(faceUnity2DTexId, currentFUTexMatrix, currentFUMvpMatrix)
        } else if (originalTextId > 0) {
            programTexture2d!!.drawFrame(originalTextId, originTexMatrix, originMvpMatrix)
        }
        if (drawSmallViewport) {
            GLES20.glViewport(smallViewportX, smallViewportY, smallViewportWidth, smallViewportHeight)
            programTexture2d!!.drawFrame(originalTextId, originTexMatrix, smallViewMatrix)
            GLES20.glViewport(0, 0, surfaceViewWidth, surfaceViewHeight)
        }
    }


    //endregion  GLSurfaceView.Renderer相关

}