package com.faceunity.core.model.facebeauty

import com.faceunity.core.controller.facebeauty.FaceBeautyParam
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge


/**
 *
 * DESC：
 * Created on 2021/1/29
 *
 */
class FaceBeauty(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {
    override fun getModelController() = mFaceBeautyController

    private val mFaceBeautyController by lazy { FURenderBridge.getInstance().mFaceBeautyController }

    //region 滤镜
    /* 滤镜名称 */

    var filterName = FaceBeautyFilterEnum.ORIGIN
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.FILTER_NAME, value)
            updateAttributes(FaceBeautyParam.FILTER_INTENSITY, filterIntensity)
        }

    /* 滤镜程度 */
    var filterIntensity = 0.0   //范围0~1 0表示不显示滤镜
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.FILTER_INTENSITY, value)
        }

    //endregion
    //region 美肤
    /* 朦胧磨皮开关 */
    var enableHeavyBlur = false  //0表示关闭，1表示开启
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.HEAVY_BLUR, if (value) 1.0 else 0.0)
        }

    /* 肤色检测开关 */
    var enableSkinDetect = false  //0表示关闭，1表示开启
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.SKIN_DETECT, if (value) 1.0 else 0.0)
        }

    /* 融合程度 */
    var nonSkinBlurIntensity = 0.0  // 肤色检测之后非肤色区域的融合程度，取值范围0.0-1.0，默认0.0
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.NON_SKIN_BLUR_SCALE, value)
        }

    /* 磨皮类型 */
    var blurType = FaceBeautyBlurTypeEnum.FineSkin // 清晰磨皮  朦胧磨皮   精细磨皮  均匀磨皮。 此参数优先级比 heavy_blur 低，在使用时要将 heavy_blur 设为 0
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.BLUR_TYPE, value)
        }

    /* 基于人脸的磨皮mask */
    var enableBlurUseMask = false //false表示关闭 true表示开启，使用正常磨皮。只在 blur_type 为 FineSkin 时生效
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.BLUR_USE_MASK, if (value) 1.0 else 0.0)
        }

    /* 磨皮程度 */
    var blurIntensity = 0.0 //范围[0-6]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.BLUR_INTENSITY, value)
        }


    /* 美白程度 */
    var colorIntensity = 0.0 //范围 [0-2]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.COLOR_INTENSITY, value)
        }


    /* 红润程度 */
    var redIntensity = 0.0 //范围 [0-2]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.RED_INTENSITY, value)
        }

    /* 锐化程度 */
    var sharpenIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.SHARPEN_INTENSITY, value)
        }

    /* 亮眼程度 */
    var eyeBrightIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.EYE_BRIGHT_INTENSITY, value)
        }

    /* 美牙程度 */
    var toothIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.TOOTH_WHITEN_INTENSITY, value)
        }

    /* 去黑眼圈强度 */
    var removePouchIntensity = 0.0  //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.REMOVE_POUCH_INTENSITY, value)
        }

    /* 去法令纹强度 */
    var removeLawPatternIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, value)
        }
//endregion

    //region 美型
    /* 变形选择 */
    var faceShape = FaceBeautyShapeEnum.FineDeformation //0 女神，1 网红，2 自然，3 默认，4 精细变形
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.FACE_SHAPE, value)
        }

    /* 变形程度 */
    var faceShapeIntensity = 1.0 ////范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.FACE_SHAPE_INTENSITY, value)
        }

    /* 瘦脸程度 */
    var cheekThinningIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_THINNING_INTENSITY, value)
        }

    /* V脸程度 */
    var cheekVIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_V_INTENSITY, value)
        }

    /* 长脸程度 */
    var cheekLongIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_LONG_INTENSITY, value)
        }

    /* 圆脸程度 */
    var cheekCircleIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_CIRCLE_INTENSITY, value)
        }

    /* 窄脸程度 */
    var cheekNarrowIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_NARROW_INTENSITY, value)
        }

    /* 窄脸程度 */
    var cheekNarrowIntensityV2 = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_NARROW_INTENSITY_V2, value)
        }

    /* 短脸程度 */
    var cheekShortIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_SHORT_INTENSITY, value)
        }

    /* 小脸程度 */
    var cheekSmallIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_SMALL_INTENSITY, value)
        }

    /* 小脸程度 */
    var cheekSmallIntensityV2 = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHEEK_SMALL_INTENSITY_V2, value)
        }

    /* 瘦颧骨 */
    var cheekBonesIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, value)
        }

    /* 瘦下颌骨 */
    var lowerJawIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, value)
        }

    /* 大眼程度 */
    var eyeEnlargingIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.EYE_ENLARGING_INTENSITY, value)
        }

    /* 大眼程度 */
    var eyeEnlargingIntensityV2 = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.EYE_ENLARGING_INTENSITY_V2, value)
        }

    /* 下巴调整程度 */
    var chinIntensity = 0.5 //范围 [0-1]，0-0.5是变小，0.5-1是变大
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CHIN_INTENSITY, value)
        }

    /* 额头调整程度 */
    var forHeadIntensity = 0.5 //范围[0-1]，0-0.5 是变小，0.5-1 是变大
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.FOREHEAD_INTENSITY, value)
        }

    /* 额头调整程度 */
    var forHeadIntensityV2 = 0.5 //范围[0-1]，0-0.5 是变小，0.5-1 是变大
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.FOREHEAD_INTENSITY_V2, value)
        }

    /* 瘦鼻程度 */
    var noseIntensity = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.NOSE_INTENSITY, value)
        }

    /* 瘦鼻程度 */
    var noseIntensityV2 = 0.0 //范围 [0-1]
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.NOSE_INTENSITY_V2, value)
        }

    /* 嘴巴调整程度 */
    var mouthIntensity = 0.5 //[0-1]，0-0.5是变小，0.5-1是变大
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.MOUTH_INTENSITY, value)
        }

    /* 嘴巴调整程度 */
    var mouthIntensityV2 = 0.5 //[0-1]，0-0.5是变小，0.5-1是变大
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.MOUTH_INTENSITY_V2, value)
        }

    /* 开眼角强度 */
    var canthusIntensity = 0.0 //范围 [0-1]，0.0 到 1.0 变强
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.CANTHUS_INTENSITY, value)
        }

    /* 眼睛间距 */
    var eyeSpaceIntensity = 0.5 //范围 [0-1]，[0-1]，0.5-0.0 变长，0.5-1.0 变短 
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.EYE_SPACE_INTENSITY, value)
        }

    /* 眼睛角度 */
    var eyeRotateIntensity = 0.5 //范围 [0-1]，0.5-0.0 逆时针旋转，0.5-1.0 顺时针旋转 
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.EYE_ROTATE_INTENSITY, value)
        }

    /* 鼻子长度 */
    var longNoseIntensity = 0.5 //范围 [0-1]，0.5-0.0 变短，0.5-1.0 变长 
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.LONG_NOSE_INTENSITY, value)
        }

    /* 调节人中 */
    var philtrumIntensity = 0.5 //范围[0-1]，0.5-1.0 变长，0.5-0.0 变短  
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.PHILTRUM_INTENSITY, value)
        }

    /* 微笑嘴角强度 */
    var smileIntensity = 0.0 //范围 [0-1]，0.0 到 1.0 变强
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.SMILE_INTENSITY, value)
        }

    /* 圆眼程度 */
    var eyeCircleIntensity = 0.0 //范围 [0-1]，0.0 到 1.0 变强
        set(value) {
            field = value
            updateAttributes(FaceBeautyParam.EYE_CIRCLE_INTENSITY, value)
        }
//endregion

    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        /*滤镜*/
        params[FaceBeautyParam.FILTER_NAME] = filterName
        params[FaceBeautyParam.FILTER_INTENSITY] = filterIntensity
        /*美肤*/
        params[FaceBeautyParam.BLUR_INTENSITY] = blurIntensity
        params[FaceBeautyParam.HEAVY_BLUR] = if (enableHeavyBlur) 1.0 else 0.0
        params[FaceBeautyParam.SKIN_DETECT] = if (enableSkinDetect) 1.0 else 0.0
        params[FaceBeautyParam.NON_SKIN_BLUR_SCALE] = nonSkinBlurIntensity
        params[FaceBeautyParam.BLUR_TYPE] = blurType
        params[FaceBeautyParam.BLUR_USE_MASK] = if (enableBlurUseMask) 1.0 else 0.0
        params[FaceBeautyParam.COLOR_INTENSITY] = colorIntensity
        params[FaceBeautyParam.RED_INTENSITY] = redIntensity
        params[FaceBeautyParam.SHARPEN_INTENSITY] = sharpenIntensity
        params[FaceBeautyParam.EYE_BRIGHT_INTENSITY] = eyeBrightIntensity
        params[FaceBeautyParam.TOOTH_WHITEN_INTENSITY] = toothIntensity
        params[FaceBeautyParam.REMOVE_POUCH_INTENSITY] = removePouchIntensity
        params[FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY] = removeLawPatternIntensity
        /*美型*/
        params[FaceBeautyParam.FACE_SHAPE] = faceShape
        params[FaceBeautyParam.FACE_SHAPE_INTENSITY] = faceShapeIntensity
        params[FaceBeautyParam.CHEEK_THINNING_INTENSITY] = cheekThinningIntensity
        params[FaceBeautyParam.CHEEK_V_INTENSITY] = cheekVIntensity
        params[FaceBeautyParam.CHEEK_LONG_INTENSITY] = cheekLongIntensity
        params[FaceBeautyParam.CHEEK_CIRCLE_INTENSITY] = cheekCircleIntensity
        params[FaceBeautyParam.CHEEK_NARROW_INTENSITY_V2] = cheekNarrowIntensityV2
        params[FaceBeautyParam.CHEEK_SHORT_INTENSITY] = cheekShortIntensity
        params[FaceBeautyParam.CHEEK_SMALL_INTENSITY_V2] = cheekSmallIntensityV2
        params[FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY] = cheekBonesIntensity
        params[FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY] = lowerJawIntensity
        params[FaceBeautyParam.EYE_ENLARGING_INTENSITY_V2] = eyeEnlargingIntensityV2
        params[FaceBeautyParam.CHIN_INTENSITY] = chinIntensity
        params[FaceBeautyParam.FOREHEAD_INTENSITY_V2] = forHeadIntensityV2
        params[FaceBeautyParam.NOSE_INTENSITY_V2] = noseIntensityV2
        params[FaceBeautyParam.MOUTH_INTENSITY_V2] = mouthIntensityV2
        params[FaceBeautyParam.CANTHUS_INTENSITY] = canthusIntensity
        params[FaceBeautyParam.EYE_SPACE_INTENSITY] = eyeSpaceIntensity
        params[FaceBeautyParam.EYE_ROTATE_INTENSITY] = eyeRotateIntensity
        params[FaceBeautyParam.LONG_NOSE_INTENSITY] = longNoseIntensity
        params[FaceBeautyParam.PHILTRUM_INTENSITY] = philtrumIntensity
        params[FaceBeautyParam.SMILE_INTENSITY] = smileIntensity
        params[FaceBeautyParam.EYE_CIRCLE_INTENSITY] = eyeCircleIntensity

        //设置旧的参数
        params[FaceBeautyParam.EYE_ENLARGING_INTENSITY] = eyeEnlargingIntensity
        return params
    }
}