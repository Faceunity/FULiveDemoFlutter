package com.faceunity.fuliveplugin.fulive_plugin.render

/**
 *
 * @author benyq
 * @date 12/18/2023
 *
 */

const val CAMERA_RENDER = "camera_render"
const val IMAGE_RENDER = "image_render"
const val VIDEO_RENDER = "video_render"

enum class RenderType {
    CAMERA, IMAGE, VIDEO;
    companion object {
        fun valueFrom(value: String?): RenderType {
            return when (value) {
                CAMERA_RENDER -> CAMERA
                IMAGE_RENDER -> IMAGE
                VIDEO_RENDER -> VIDEO
                else -> throw IllegalArgumentException("unknown render type: $value")
            }
        }
    }
}