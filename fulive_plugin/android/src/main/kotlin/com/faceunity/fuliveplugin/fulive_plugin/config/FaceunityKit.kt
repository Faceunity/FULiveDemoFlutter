package com.faceunity.fuliveplugin.fulive_plugin.config

import android.content.Context
import android.util.Log
import com.faceunity.core.callback.OperateCallback
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.enumeration.FUAITypeEnum
import com.faceunity.core.enumeration.FUFaceBeautyMultiModePropertyEnum
import com.faceunity.core.enumeration.FUFaceBeautyPropertyModeEnum
import com.faceunity.core.faceunity.FUAIKit
import com.faceunity.core.faceunity.FURenderConfig
import com.faceunity.core.faceunity.FURenderKit
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.model.facebeauty.FaceBeauty
import com.faceunity.core.model.makeup.Makeup
import com.faceunity.core.model.makeup.SimpleMakeup
import com.faceunity.core.model.prop.Prop
import com.faceunity.core.utils.FULogger
import com.faceunity.fuliveplugin.fulive_plugin.authpack
import com.faceunity.fuliveplugin.fulive_plugin.utils.FuDeviceUtils

/**
 *
 * @author benyq
 * @date 11/10/2023
 *
 */
object FaceunityKit {

    private const val TAG = "FaceunityKit"
    private val aiKit = FUAIKit.getInstance()
    private val renderKit = FURenderKit.getInstance()

    var devicePerformanceLevel = FuDeviceUtils.DEVICE_LEVEL_MID

    val highLeveDeice: Boolean get() = devicePerformanceLevel == FuDeviceUtils.DEVICE_LEVEL_HIGH

    var isEffectsOn = true
    @Volatile
    var isKitInit = false
        private set

    fun setupKit(context: Context, successAction: () -> Unit) {
        devicePerformanceLevel = FuDeviceUtils.judgeDeviceLevelGPU()
        FURenderManager.setKitDebug(FULogger.LogLevel.DEBUG)
        FURenderManager.registerFURender(context, authpack.A(), object :
            OperateCallback {
            override fun onFail(errCode: Int, errMsg: String) {
                Log.e(TAG, "onFail: errCode: $errCode, errMsg: $errMsg")
            }

            override fun onSuccess(code: Int, msg: String) {
                if (code == FURenderConfig.OPERATE_SUCCESS_AUTH) {
                    successAction()
                    isKitInit = true
                    aiKit.loadAIProcessor(FaceunityConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR)
                    aiKit.loadAIProcessor(FaceunityConfig.BUNDLE_AI_HUMAN, FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR)
                    //高端机开启小脸检测
                    FUAIKit.getInstance().faceProcessorSetFaceLandmarkQuality(devicePerformanceLevel)
                    if (devicePerformanceLevel > FuDeviceUtils.DEVICE_LEVEL_MID) {
                        FUAIKit.getInstance().fuFaceProcessorSetDetectSmallFace(true)
                    }
                }
            }
        })
    }


    fun loadFaceBeauty() {
        val faceBeauty = FaceBeauty(FUBundleData(FaceunityConfig.BUNDLE_FACE_BEAUTIFICATION))
        if (devicePerformanceLevel == FuDeviceUtils.DEVICE_LEVEL_HIGH) {
         faceBeauty.addPropertyMode(
                FUFaceBeautyMultiModePropertyEnum.REMOVE_POUCH_INTENSITY,
                FUFaceBeautyPropertyModeEnum.MODE2
            )
            faceBeauty.addPropertyMode(
                FUFaceBeautyMultiModePropertyEnum.REMOVE_NASOLABIAL_FOLDS_INTENSITY,
                FUFaceBeautyPropertyModeEnum.MODE2
            )
            faceBeauty.addPropertyMode(
                FUFaceBeautyMultiModePropertyEnum.EYE_ENLARGING_INTENSITY,
                FUFaceBeautyPropertyModeEnum.MODE3
            )
            faceBeauty.addPropertyMode(
                FUFaceBeautyMultiModePropertyEnum.MOUTH_INTENSITY,
                FUFaceBeautyPropertyModeEnum.MODE3
            )
        }
        faceBeauty.enable = isEffectsOn
        renderKit.faceBeauty = faceBeauty
    }

    private var faceBeauty: FaceBeauty? = null
    private var props: List<Prop>? = null
    private var makeup: SimpleMakeup? = null
    fun storeFaceUnityConfig() {
        FULogger.d(TAG, "storeFaceUnityConfig")
        faceBeauty = renderKit.faceBeauty
        props = renderKit.propContainer.getAllProp()
        makeup = renderKit.makeup
    }

    fun restoreFaceUnityConfig() {
        FULogger.d(TAG, "restoreFaceUnityConfig")
        if (faceBeauty != null) {
            renderKit.faceBeauty = faceBeauty
        }
        if (renderKit.propContainer.getAllProp().isEmpty()) {
            props?.forEach {
                renderKit.propContainer.addProp(it)
            }
        }
        if (makeup!= null) {
            renderKit.makeup = makeup
            (renderKit.makeup as? Makeup)?.let {
                //特殊有一些需要设置图层混合模式的 04双色眼影3（第2层眼影的混合模式 == 1） 06三色眼影2（第3层眼影的混合模式 == 1）
                if ("mu_style_eyeshadow_04" == it.eyeShadowBundle?.name)
                    it.eyeShadowTexBlend2 = 1
                else if ("mu_style_eyeshadow_06" == it.eyeShadowBundle?.name)
                    it.eyeShadowTexBlend3 = 1
            }
        }
    }

    fun dropFaceUnityConfig() {
        FULogger.d(TAG, "dropFaceUnityConfig")
        faceBeauty = null
        props = null
        makeup = null
    }
}
