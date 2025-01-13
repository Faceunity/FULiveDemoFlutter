package com.faceunity.fuliveplugin.fulive_plugin.utils

import com.faceunity.core.controller.facebeauty.FaceBeautyParam
import com.faceunity.fuliveplugin.fulive_plugin.modules.FUFaceBeautyPlugin

/**
 *
 * @author benyq
 * @date 11/21/2024
 *
 */
object RestrictedSkinTool {

    private lateinit var _restrictedSkinParams: List<Int>
    val restrictedSkinParams: List<Int>
        get() {
            if (!::_restrictedSkinParams.isInitialized) {
                initRestrictedSkinParams()
            }
            return _restrictedSkinParams
        }

    private fun initRestrictedSkinParams() {
        val params = mutableSetOf<Int>()
        val restrictedMap = FuDeviceUtils.getBlackListMap()
        restrictedMap.forEach {
            when(it.key) {
                FaceBeautyParam.DELSPOT-> {
                    if (it.value.contains(FuDeviceUtils.getDeviceName())) {
                        params.add(FUFaceBeautyPlugin.SkinEnum.FUBeautySkinAntiAcneSpot.ordinal)
                        return@forEach
                    }
                }
            }
        }
        _restrictedSkinParams = params.toList()
    }
}