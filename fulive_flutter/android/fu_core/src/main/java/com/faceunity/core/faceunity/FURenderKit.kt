package com.faceunity.core.faceunity

import com.faceunity.core.entity.FURenderInputData
import com.faceunity.core.entity.FURenderOutputData
import com.faceunity.core.model.action.ActionRecognition
import com.faceunity.core.model.animationFilter.AnimationFilter
import com.faceunity.core.model.antialiasing.Antialiasing
import com.faceunity.core.model.bgSegGreen.BgSegGreen
import com.faceunity.core.model.bodyBeauty.BodyBeauty
import com.faceunity.core.model.facebeauty.FaceBeauty
import com.faceunity.core.model.hairBeauty.HairBeautyNormal
import com.faceunity.core.model.littleMakeup.LightMakeup
import com.faceunity.core.model.makeup.SimpleMakeup
import com.faceunity.core.model.musicFilter.MusicFilter
import com.faceunity.core.model.prop.PropContainer
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.support.SDKController


/**
 *
 * DESC：
 * Created on 2021/2/8
 *
 */
class FURenderKit private constructor() {

    companion object {
        const val TAG = "KIT_FURenderKit"

        @Volatile
        private var INSTANCE: FURenderKit? = null

        @JvmStatic
        fun getInstance(): FURenderKit {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = FURenderKit()
                    }
                }
            }
            return INSTANCE!!
        }
    }


    private val mFURenderBridge by lazy { FURenderBridge.getInstance() }

    /**
     * 资源释放
     */
    fun release() {
        destroy(false)
    }

    /**
     * 资源无法正常释放场景下-状态重置
     */
    fun releaseSafe() {
        destroy(true)
    }


    private fun destroy(isSafe: Boolean) {
        faceBeauty?.let {
            faceBeauty = null
        }
        makeup?.let {
            makeup = null
        }
        animationFilter?.let {
            animationFilter = null
        }
        antialiasing?.let {
            antialiasing = null
        }
        bgSegGreen?.let {
            bgSegGreen = null
        }
        bodyBeauty?.let {
            bodyBeauty = null
        }
        hairBeauty?.let {
            hairBeauty = null
        }
        lightMakeup?.let {
            lightMakeup = null
        }
        musicFilter?.let {
            musicFilter = null
        }
        actionRecognition?.let {
            actionRecognition = null
        }

        if (propContainer.getAllProp().isNotEmpty()) {
            propContainer.removeAllProp()
        }
        mFURenderBridge.mPropContainerController.release()
        if (sceneManager.getAllScene().isNotEmpty()) {
            sceneManager.removeAllScene()
            mFURenderBridge.mAvatarController.release()
        }

        mFURenderBridge.onDestroy(isSafe)
    }


    //region AI驱动

    val FUAIController by lazy { FUAIKit.getInstance() }

    //endregion AI驱动
    //region 业务模型
    /*美颜*/
    var faceBeauty: FaceBeauty? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mFaceBeautyController.release()
            }
        }

    /*美妆*/
    var makeup: SimpleMakeup? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mMakeupController.release()
            }
        }

    /*动漫滤镜*/
    var animationFilter: AnimationFilter? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mAnimationFilterController.release()
            }

        }


    /*3D抗锯齿*/
    var antialiasing: Antialiasing? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mAntialiasingController.release()
            }

        }


    /*绿幕抠像*/
    var bgSegGreen: BgSegGreen? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mBgSegGreenController.release()
            }

        }


    /*美体*/
    var bodyBeauty: BodyBeauty? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mBodyBeautyController.release()
            }
        }

    /*美发*/
    var hairBeauty: HairBeautyNormal? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mHairBeautyController.release()
            }

        }

    /*轻美妆*/
    var lightMakeup: LightMakeup? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mLightMakeupController.release()
            }

        }

    /*音乐滤镜*/
    var musicFilter: MusicFilter? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mMusicFilterController.release()
            }

        }


    /*动作识别*/
    var actionRecognition: ActionRecognition? = null
        set(value) {
            if (field == value) return
            field = value
            if (value != null) {
                value.loadToRenderKit()
            } else {
                mFURenderBridge.mActionRecognitionController.release()
            }
        }


    /*道具容器*/
    val propContainer by lazy { PropContainer.getInstance() }

    /*avatar容器*/
    val sceneManager by lazy { FUSceneKit.getInstance() }
    //endregion业务模型
    //region 渲染

    /**
     * 单桢渲染
     * @param input FURenderInputData
     * @return Int
     */
    fun renderWithInput(input: FURenderInputData): FURenderOutputData {
        return mFURenderBridge.renderWithInput(input)
    }

    //endregion 渲染
    //region 业务


    /**
     * 配置是否使用AI线程
     * @param isUse Boolean
     * @return Int
     */
    fun setUseAsyncAIInference(isUse: Boolean): Int {
        return mFURenderBridge.setUseAsyncAIInference(isUse)
    }

    /**
     * 配置使用是否使用multi buffer
     * @param isUseMultiGPUTexture Boolean
     * @param isUseMultiCPUBuffer Boolean
     * @return Int
     */
    fun setUseMultiBuffer(isUseMultiGPUTexture: Boolean, isUseMultiCPUBuffer: Boolean): Int {
        return mFURenderBridge.setUseMultiBuffer(isUseMultiGPUTexture, isUseMultiCPUBuffer)
    }

    /**
     * 异步读取输出纹理 buffer
     * @param isUse Boolean
     * @return Int
     */
    fun setUseTexAsync(isUse: Boolean): Int {
        return mFURenderBridge.setUseTexAsync(isUse)
    }


    /**
     * 清除缓存
     */
    fun clearCacheResource() {
        return mFURenderBridge.clearCacheResource()
    }

    /**
     * 获取版本信息
     */
    fun getVersion(): String {
        return SDKController.getVersion()
    }

    /**
     * 获取证书权限码
     */
    fun getModuleCode(code:Int): Int {
        return SDKController.getModuleCode(code)
    }

    /**
     * 创建OpenGL环境 适用于没OpenGL环境时调用
     */
    fun createEGLContext() {
        SDKController.createEGLContext()
    }

    /**
     * 调用过fuCreateEGLContext，在销毁时需要调用fuReleaseEGLContext
     */
    fun releaseEGLContext() {
        SDKController.releaseEGLContext()
    }

    /**
     * 设置数据回写同步
     */
    fun setReadBackSync(enable: Boolean) {
        SDKController.setReadbackSync(enable)
    }
    //endregion 业务

}