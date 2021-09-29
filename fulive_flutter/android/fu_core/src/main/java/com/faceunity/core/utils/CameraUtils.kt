/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.faceunity.core.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.hardware.Camera.CameraInfo
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.os.Build
import android.text.TextUtils.SimpleStringSplitter
import android.text.TextUtils.StringSplitter
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.Surface
import android.view.WindowManager
import java.util.*

/**
 * Camera-related utility functions.
 */
object CameraUtils {
    private val TAG = CameraUtils::class.java.simpleName
    const val FOCUS_TIME = 2000L
    const val DEBUG = false

    /**
     * 是否支持 Camera2
     *
     * @param context
     * @return
     */
    fun hasCamera2(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            false
        } else try {
            val manager = (context.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
            val idList = manager.cameraIdList
            var notNull = true
            if (idList.size == 0) {
                notNull = false
            } else {
                for (str in idList) {
                    if (str == null || str.trim { it <= ' ' }.isEmpty()) {
                        notNull = false
                        break
                    }
                    val characteristics = manager.getCameraCharacteristics(str)
                    val iSupportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                    if (iSupportLevel != null && iSupportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        notNull = false
                        break
                    }
                }
            }
            notNull
        } catch (ignored: Exception) {
            false
        }
    }

    /**
     * 获取相机方向
     *
     * @param cameraFacing
     * @return
     */
    fun getCameraOrientation(cameraFacing: Int): Int {
        val info = CameraInfo()
        var cameraId = -1
        val numCameras = Camera.getNumberOfCameras()
        for (i in 0 until numCameras) {
            Camera.getCameraInfo(i, info)
            if (info.facing == cameraFacing) {
                cameraId = i
                break
            }
        }
        return if (cameraId < 0) { // no front camera, regard it as back camera
            90
        } else {
            info.orientation
        }
    }

    /**
     * 设置相机显示方向
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    fun setCameraDisplayOrientation(context: Context, cameraId: Int, camera: Camera) {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
            else -> {
            }
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    /**
     * 设置对焦模式，优先支持自动对焦
     *
     * @param parameters
     */
    fun setFocusModes(parameters: Camera.Parameters) {
        val focusModes = parameters.supportedFocusModes
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        if (DEBUG) {
            Log.i(TAG, "setFocusModes: " + parameters.focusMode)
        }
    }


    /**
     * Attempts to find a preview size that matches the provided width and height (which
     * specify the dimensions of the encoded video).  If it fails to find a match it just
     * uses the default preview size for video.
     *
     *
     * https://github.com/commonsguy/cwac-camera/blob/master/camera/src/com/commonsware/cwac/camera/CameraUtils.java
     */
    fun choosePreviewSize(parameters: Camera.Parameters, width: Int, height: Int): IntArray {
        val supportedPreviewSizes = parameters.supportedPreviewSizes
        if (DEBUG) {
            val sb = StringBuilder("[")
            for (supportedPreviewSize in supportedPreviewSizes) {
                sb.append("[").append(supportedPreviewSize.width).append(", ")
                    .append(supportedPreviewSize.height).append("]").append(", ")
            }
            sb.append("]")
            Log.d(TAG, "choosePreviewSize: Supported preview size $sb")
        }
        for (size in supportedPreviewSizes) {
            if (size.width == width && size.height == height) {
                parameters.setPreviewSize(width, height)
                return intArrayOf(width, height)
            }
        }
        if (DEBUG) {
            Log.e(TAG, "Unable to set preview size to " + width + "x" + height)
        }
        val ppsfv = parameters.preferredPreviewSizeForVideo
        if (ppsfv != null) {
            parameters.setPreviewSize(ppsfv.width, ppsfv.height)
            return intArrayOf(ppsfv.width, ppsfv.height)
        }
        // else use whatever the default size is
        return intArrayOf(0, 0)
    }

    /**
     * Given `choices` of `Size`s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     * class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal `Size`, or an arbitrary one if none were big enough
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun chooseOptimalSize(
        choices: Array<Size>, textureViewWidth: Int,
        textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size
    ): Size { // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough: MutableList<Size> = ArrayList()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough: MutableList<Size> = ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w
            ) {
                if (option.width >= textureViewWidth &&
                    option.height >= textureViewHeight
                ) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }
        // Pick the smallest of those big enough. If there is no one big enough, pick the
// largest of those not big enough.
        val comparator = Comparator<Size> { lhs, rhs ->
            // We cast here to ensure the multiplications won't overflow
            java.lang.Long.signum(
                lhs.width.toLong() * lhs.height -
                        rhs.width.toLong() * rhs.height
            )
        }
        return if (bigEnough.size > 0) {
            Collections.min(bigEnough, comparator)
        } else if (notBigEnough.size > 0) {
            Collections.max(notBigEnough, comparator)
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            choices[0]
        }
    }

    /**
     * 设置相机视频防抖动
     *
     * @param parameters
     */
    fun setVideoStabilization(parameters: Camera.Parameters) {
        if (parameters.isVideoStabilizationSupported) {
            if (!parameters.videoStabilization) {
                parameters.videoStabilization = true
                if (DEBUG) {
                    Log.i(TAG, "Enabling video stabilization...")
                }
            }
        } else {
            if (DEBUG) {
                Log.i(TAG, "This device does not support video stabilization")
            }
        }
    }

    /**
     * 获取曝光补偿
     *
     * @param camera
     * @return
     */
    fun getExposureCompensation(camera: Camera?): Float {
        if (camera == null) {
            return 0f
        }
        try {
            val parameters = camera.parameters
            val value = parameters.exposureCompensation.toFloat()
            val min = parameters.minExposureCompensation.toFloat()
            val max = parameters.maxExposureCompensation.toFloat()
            return (value - min) / (max - min)
        } catch (e: Exception) {
            if (DEBUG) {
                Log.w(TAG, "getExposureCompensation: ", e)
            }
        }
        return 0f
    }

    /**
     * 设置曝光补偿
     *
     * @param camera
     * @param value
     */
    fun setExposureCompensation(camera: Camera?, value: Float) {
        if (camera == null) {
            return
        }
        try {
            val parameters = camera.parameters
            val min = parameters.minExposureCompensation.toFloat()
            val max = parameters.maxExposureCompensation.toFloat()
            val compensation = (value * (max - min) + min).toInt()
            parameters.exposureCompensation = compensation
            if (DEBUG) {
                Log.d(TAG, "setExposureCompensation: $compensation")
            }
            camera.parameters = parameters
        } catch (e: Exception) {
            if (DEBUG) {
                Log.w(TAG, "setExposureCompensation: ", e)
            }
        }
    }

    /**
     * 设置相机参数
     *
     * @param camera
     * @param parameters
     */
    fun setParameters(camera: Camera?, parameters: Camera.Parameters?) {
        if (camera != null && parameters != null) {
            try {
                camera.parameters = parameters
            } catch (ex: Exception) {
                if (DEBUG) {
                    Log.w(TAG, "setParameters: ", ex)
                }
            }
        }
    }

    /**
     * 点击屏幕时，设置测光和对焦
     *
     * @param camera
     * @param rawX
     * @param rawY
     * @param viewWidth
     * @param viewHeight
     * @param cameraWidth
     * @param cameraHeight
     * @param areaSize
     * @param cameraFacing
     */
    fun handleFocusMetering(
        camera: Camera?, rawX: Float, rawY: Float, viewWidth: Int, viewHeight: Int,
        cameraWidth: Int, cameraHeight: Int, areaSize: Int, cameraFacing: Int
    ) {
        if (camera == null) {
            return
        }
        try {
            val parameters = camera.parameters
            val focusRect = calculateTapArea(
                rawX / viewWidth * cameraHeight, rawY / viewHeight * cameraWidth,
                cameraHeight, cameraWidth, areaSize, cameraFacing
            )
            val focusMode = parameters.focusMode
            val focusAreas: MutableList<Camera.Area> =
                ArrayList()
            focusAreas.add(Camera.Area(focusRect, 1000))
            val meteringAreas: MutableList<Camera.Area> =
                ArrayList()
            meteringAreas.add(Camera.Area(Rect(focusRect), 1000))
            if (parameters.maxNumFocusAreas > 0 &&
                (focusMode == Camera.Parameters.FOCUS_MODE_AUTO || focusMode == Camera.Parameters.FOCUS_MODE_MACRO || focusMode == Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE || focusMode == Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
            ) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                parameters.focusAreas = focusAreas
                if (parameters.maxNumMeteringAreas > 0) {
                    parameters.meteringAreas = meteringAreas
                    if (DEBUG) {
                        Log.d(TAG, "handleFocusMetering: setMeteringAreas 1 $focusRect")
                    }
                }
                camera.cancelAutoFocus()
                setParameters(camera, parameters)
                camera.autoFocus(AutoFocusCallback { success, camera ->
                    if (DEBUG) {
                        Log.d(TAG, "onAutoFocus success:$success")
                    }
                    resetFocus(camera, focusMode)
                })
            } else if (parameters.maxNumMeteringAreas > 0) {
                if (!parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    Log.w(TAG, "handleFocusMetering: not support focus")
                    //                    return; //cannot autoFocus
                }
                parameters.meteringAreas = meteringAreas
                if (DEBUG) {
                    Log.d(TAG, "handleFocusMetering: setMeteringAreas 2 $focusRect")
                }
                camera.cancelAutoFocus()
                setParameters(camera, parameters)
                camera.autoFocus(AutoFocusCallback { success, camera ->
                    if (DEBUG) {
                        Log.d(TAG, "onAutoFocus success:$success")
                    }
                    resetFocus(camera, focusMode)
                })
            } else {
                camera.autoFocus(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleFocusMetering: ", e)
        }
    }

    private fun resetFocus(camera: Camera, focusMode: String) {
        ThreadHelper.getInstance().removeUiAllTasks()
        ThreadHelper.getInstance().runOnUiPostDelayed({
            try {
                camera.cancelAutoFocus()
                val parameter = camera.parameters
                parameter.focusMode = focusMode
                if (DEBUG) {
                    Log.d(TAG, "resetFocus focusMode:$focusMode")
                }
                parameter.focusAreas = null
                parameter.meteringAreas = null
                setParameters(camera, parameter)
            } catch (e: Exception) {
                if (DEBUG) {
                    Log.w(TAG, "resetFocus: ", e)
                }
            }
        }, FOCUS_TIME.toLong())
    }

    private fun calculateTapArea(
        x: Float,
        y: Float,
        width: Int,
        height: Int,
        areaSize: Int,
        cameraFacing: Int
    ): Rect {
        val centerX = (x / width * 2000 - 1000).toInt()
        val centerY = (y / height * 2000 - 1000).toInt()
        val top = clamp(centerX - areaSize / 2)
        val bottom = clamp(top + areaSize)
        val left = clamp(centerY - areaSize / 2)
        val right = clamp(left + areaSize)
        val rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val matrix = Matrix()
        val flipX = if (cameraFacing == CameraInfo.CAMERA_FACING_FRONT) -1 else 1
        matrix.setScale(flipX.toFloat(), -1f)
        matrix.mapRect(rectF)
        return Rect(
            Math.round(rectF.left),
            Math.round(rectF.top),
            Math.round(rectF.right),
            Math.round(rectF.bottom)
        )
    }

    private fun clamp(x: Int): Int {
        return if (x > 1000) 1000 else if (x < -1000) -1000 else x
    }

    /**
     * 查询所有相机参数
     *
     * @param camera
     * @return
     */
    fun getFullCameraParameters(camera: Camera): Map<String, String> {
        val result: MutableMap<String, String> = HashMap(64)
        try {
            val camClass: Class<*> = camera.javaClass
            // Internally, Android goes into native code to retrieve this String
// of values
            val getNativeParams = camClass.getDeclaredMethod("native_getParameters")
            getNativeParams.isAccessible = true
            // Boom. Here's the raw String from the hardware
            val rawParamsStr = getNativeParams.invoke(camera) as String
            // But let's do better. Here's what Android uses to parse the
// String into a usable Map -- a simple ';' StringSplitter, followed
// by splitting on '='
//
// Taken from Camera.Parameters unflatten() method
            val splitter: StringSplitter = SimpleStringSplitter(';')
            splitter.setString(rawParamsStr)
            for (kv in splitter) {
                val pos = kv.indexOf('=')
                if (pos == -1) {
                    continue
                }
                val k = kv.substring(0, pos)
                val v = kv.substring(pos + 1)
                result[k] = v
            }
            // And voila, you have a map of ALL supported parameters
            return result
        } catch (ex: Exception) {
            Log.e(TAG, "ex:", ex)
        }
        // If there was any error, just return an empty Map
        Log.e(TAG, "Unable to retrieve parameters from Camera.")
        return result
    }

    fun YUV420ToNV21(image: Image, yuvDataBuffer: ByteArray) {
        val crop = image.cropRect
        val width = crop.width()
        val height = crop.height()
        val planes = image.planes
        val yDataBuffer = ByteArray(planes[0].rowStride)
        var channelOffset = 0
        var outputStride = 1
        for (i in planes.indices) {
            when (i) {
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }
                1 -> {
                    channelOffset = width * height + 1
                    outputStride = 2
                }
                2 -> {
                    channelOffset = width * height
                    outputStride = 2
                }
                else -> {
                }
            }
            val buffer = planes[i].buffer
            val rowStride = planes[i].rowStride
            val pixelStride = planes[i].pixelStride
            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                var length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    buffer[yuvDataBuffer, channelOffset, length]
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    buffer[yDataBuffer, 0, length]
                    for (col in 0 until w) {
                        yuvDataBuffer[channelOffset] = yDataBuffer[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
    }

    /**
     * 获取最佳帧率 Camera2
     */
    fun getBestRange(context: Context, cameraId: String, isHighestRate: Boolean): Range<Int>? {
        var result: Range<Int>? = null
        try {
            val manager = (context.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
            val chars = manager.getCameraCharacteristics(cameraId)
            val ranges =
                chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
            if (ranges != null) {
                for (range in ranges) { //帧率不能太低，大于10
                    if (range.lower < 10) {
                        continue
                    }
                    if (result == null) {
                        result = range
                    } else if (isHighestRate && (range.upper > result.upper || (range.upper == result.upper && range.lower > result.lower))) {
                        result = range
                    } else if ((range.upper - range.lower > result.upper - result.lower) ||
                        ((range.upper - range.lower == result.upper - result.lower) && range.upper > range.lower)
                    ) {
                        result = range
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "getBestRange: ", e)
        }
        return result
    }

    /**
     * 设置相机 FPS，选择尽可能大的范围  Camera1
     *
     * @param parameters
     */
    fun chooseFrameRate(parameters: Camera.Parameters, isHighestRate: Boolean) {
        val supportedPreviewFpsRanges = parameters.supportedPreviewFpsRange
        if (DEBUG) {
            val buffer = StringBuilder()
            buffer.append('[')
            val it: Iterator<IntArray> = supportedPreviewFpsRanges.iterator()
            while (it.hasNext()) {
                buffer.append(Arrays.toString(it.next()))
                if (it.hasNext()) {
                    buffer.append(", ")
                }
            }
            buffer.append(']')
            Log.d(TAG, "chooseFrameRate: Supported FPS ranges $buffer")
        }
        // FPS下限小于 7，弱光时能保证足够曝光时间，提高亮度。
// range 范围跨度越大越好，光源足够时FPS较高，预览更流畅，光源不够时FPS较低，亮度更好。
        var bestFrameRate = supportedPreviewFpsRanges[0]

        if (isHighestRate) {
            for (fpsRange in supportedPreviewFpsRanges) {
                val thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                if (thisMax > bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]) {
                    bestFrameRate = fpsRange
                } else if (thisMax == bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                    && thisMin > bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                ) {
                    bestFrameRate = fpsRange
                }
            }
        } else {
            for (fpsRange in supportedPreviewFpsRanges) {
                val thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                if (thisMin < 7000) {
                    continue
                }
                if (thisMin <= 15000 && thisMax - thisMin > bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                    - bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                ) {
                    bestFrameRate = fpsRange
                }
            }
        }
        if (DEBUG) {
            Log.i(
                TAG,
                "setPreviewFpsRange: [" + bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] + ", " + bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] + "]"
            )
        }
        parameters.setPreviewFpsRange(
            bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
            bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
        )
    }
}