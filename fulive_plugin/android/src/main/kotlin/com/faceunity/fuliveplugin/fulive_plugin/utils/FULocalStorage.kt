package com.faceunity.fuliveplugin.fulive_plugin.utils

import android.annotation.SuppressLint
import android.content.Context

/**
 *
 * @author benyq
 * @date 12/18/2023
 *
 */

@SuppressLint("StaticFieldLeak")
lateinit var appCtx: Context

object FULocalStorage {

    private const val preferencesName = "fulive_plugin"

    private const val KEY_FACE_BEAUTY_SKIN = "face_beauty_skin"
    private const val KEY_FACE_BEAUTY_SHAPE = "face_beauty_shape"
    private const val KEY_FACE_BEAUTY_FILTER = "face_beauty_filter"


    fun saveFaceBeautySkin(json: String) {
        saveToSP(KEY_FACE_BEAUTY_SKIN, json)
    }

    fun getFaceBeautySkin(): String? {
        return getFromSP(KEY_FACE_BEAUTY_SKIN)
    }

    fun saveFaceBeautyShape(json: String) {
        saveToSP(KEY_FACE_BEAUTY_SHAPE, json)
    }

    fun getFaceBeautyShape(): String? {
        return getFromSP(KEY_FACE_BEAUTY_SHAPE)
    }

    fun saveFaceBeautyFilter(json: String) {
        saveToSP(KEY_FACE_BEAUTY_FILTER, json)
    }

    fun getFaceBeautyFilter(): String? {
        return getFromSP(KEY_FACE_BEAUTY_FILTER)
    }

    private fun saveToSP(key: String, value: String) {
        appCtx.getSharedPreferences(preferencesName, Context.MODE_PRIVATE).edit()
            .putString(key, value).apply()
    }

    private fun getFromSP(key: String): String? {
        return appCtx.getSharedPreferences(preferencesName, Context.MODE_PRIVATE).getString(key, null)
    }
}