package com.faceunity.fulive_plugin.data_factory;

import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.enumeration.FUFaceBeautyMultiModePropertyEnum;
import com.faceunity.core.enumeration.FUFaceBeautyPropertyModeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.facebeauty.FaceBeautyBlurTypeEnum;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.core.model.prop.expression.ExpressionRecognition;
import com.faceunity.fulive_plugin.common.PluginConfig;
import com.faceunity.fulive_plugin.utils.FuDeviceUtils;

/**
 * DESC：美颜业务工厂
 * Created on 2021/3/1
 */
public class FaceBeautyDataFactory {

    /*美颜缓存数据模型 用于风格切换*/
    private static final FaceBeauty defaultFaceBeauty = getDefaultFaceBeauty();
    /*当前生效美颜数据模型*/
    public static FaceBeauty faceBeauty = defaultFaceBeauty;

    public static void configBeauty() {
        FUAIKit.getInstance().loadAIProcessor(PluginConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
        FUAIKit.getInstance().faceProcessorSetFaceLandmarkQuality(PluginConfig.DEVICE_LEVEL);
        if (PluginConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID) {
            FUAIKit.getInstance().fuFaceProcessorSetDetectSmallFace(true);
            if (faceBeauty.getBlurType() != FaceBeautyBlurTypeEnum.EquallySkin) {
                faceBeauty.setBlurType(FaceBeautyBlurTypeEnum.EquallySkin);
                faceBeauty.setEnableBlurUseMask(true);
            }
        }
        FURenderKit.getInstance().setFaceBeauty(faceBeauty);
        FUAIKit.getInstance().setMaxFaces(4);
        if (PluginConfig.IS_OPEN_LAND_MARK) {
            ExpressionRecognition expressionRecognition =  new ExpressionRecognition(new FUBundleData(PluginConfig.BUNDLE_LANDMARKS));
            expressionRecognition.setLandmarksType(FUAITypeEnum.FUAITYPE_FACELANDMARKS239);
            FURenderKit.getInstance().getPropContainer().addProp(expressionRecognition);
        }
    }

    public static void dispose() {
        FUAIKit.getInstance().releaseAllAIProcessor();
        FURenderKit.getInstance().setFaceBeauty(null);
    }

    public static void setSkinBeauty(int index, double value) {
        switch (index) {
            case 0:
                if (PluginConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID) {
                    if (faceBeauty.getBlurType() != FaceBeautyBlurTypeEnum.EquallySkin) {
                        faceBeauty.setBlurType(FaceBeautyBlurTypeEnum.EquallySkin);
                        faceBeauty.setEnableBlurUseMask(true);
                    }
                }
                faceBeauty.setBlurIntensity(value);
                break;
            case 1:
                faceBeauty.setColorIntensity(value);
                break;
            case 2:
                faceBeauty.setRedIntensity(value);
                break;
            case 3:
                faceBeauty.setSharpenIntensity(value);
                break;
            case 4:
                faceBeauty.setFaceThreeIntensity(value);
                break;
            case 5:
                faceBeauty.setEyeBrightIntensity(value);
                break;
            case 6:
                faceBeauty.setToothIntensity(value);
                break;
            case 7:
                faceBeauty.setRemovePouchIntensity(value);
                break;
            case 8:
                faceBeauty.setRemoveLawPatternIntensity(value);
                break;
            default:
        }
    }

    public static void setShapeBeauty(int index, double value) {
        switch (index) {
            case 0:
                faceBeauty.setCheekThinningIntensity(value);//瘦脸程度
                break;
            case 1:
                faceBeauty.setCheekVIntensity(value);//V脸程度
                break;
            case 2:
                faceBeauty.setCheekNarrowIntensity(value);//窄脸程度
                break;
            case 3:
                faceBeauty.setCheekShortIntensity(value);//短脸程度
                break;
            case 4:
                faceBeauty.setCheekSmallIntensity(value);//小脸程度
                break;
            case 5:
                faceBeauty.setCheekBonesIntensity(value);//瘦颧骨
                break;
            case 6:
                faceBeauty.setLowerJawIntensity(value);//瘦下颌骨
                break;
            case 7:
                faceBeauty.setEyeEnlargingIntensity(value);//大眼程度
                break;
            case 8:
                faceBeauty.setEyeCircleIntensity(value);//圆眼程度
                break;
            case 9:
                faceBeauty.setChinIntensity(value);//下巴调整程度
                break;
            case 10:
                faceBeauty.setForHeadIntensity(value);//额头调整程度
                break;
            case 11:
                faceBeauty.setNoseIntensity(value);//瘦鼻程度
                break;
            case 12:
                faceBeauty.setMouthIntensity(value);//嘴巴调整程度
                break;
            case 13:
                faceBeauty.setLipThickIntensity(value);//嘴巴厚度
                break;
            case 14:
                faceBeauty.setEyeHeightIntensity(value);//眼睛高度
                break;
            case 15:
                faceBeauty.setCanthusIntensity(value);//开眼角强度
                break;
            case 16:
                faceBeauty.setEyeLidIntensity(value);//眼睑
                break;
            case 17:
                faceBeauty.setEyeSpaceIntensity(value);//眼睛间距
                break;
            case 18:
                faceBeauty.setEyeRotateIntensity(value);//眼睛角度
                break;
            case 19:
                faceBeauty.setLongNoseIntensity(value);//鼻子长度
                break;
            case 20:
                faceBeauty.setPhiltrumIntensity(value);//调节人中
                break;
            case 21:
                faceBeauty.setSmileIntensity(value);//微笑嘴角强度
                break;
            case 22:
                faceBeauty.setBrowHeightIntensity(value);//眉毛上下
                break;
            case 23:
                faceBeauty.setBrowSpaceIntensity(value);//眉毛间距
                break;
            case 24:
                faceBeauty.setBrowThickIntensity(value);//眉毛粗细
                break;
            default:
        }
    }

    public static void setFilter(String filterName, double filterIntensity) {
        faceBeauty.setFilterName(filterName);
        faceBeauty.setFilterIntensity(filterIntensity);
    }

    public static void resetSkinBeauty() {
        faceBeauty.setColorIntensity(0.3);
        faceBeauty.setBlurIntensity(4.2);
        faceBeauty.setRedIntensity(0.3);
        faceBeauty.setSharpenIntensity(0.2);
        faceBeauty.setEyeBrightIntensity(0);
        faceBeauty.setToothIntensity(0);
        faceBeauty.setRemovePouchIntensity(0);
        faceBeauty.setRemoveLawPatternIntensity(0);
        faceBeauty.setFaceThreeIntensity(0);
    }

    public static void resetShapeBeauty() {
        faceBeauty.setSharpenIntensity(1.0);
        faceBeauty.setCheekThinningIntensity(0);
        faceBeauty.setCheekLongIntensity(0);
        faceBeauty.setCheekCircleIntensity(0);
        faceBeauty.setCheekVIntensity(0.5);
        faceBeauty.setCheekNarrowIntensity(0);
        faceBeauty.setCheekShortIntensity(0);
        faceBeauty.setCheekSmallIntensity(0);
        faceBeauty.setCheekBonesIntensity(0);
        faceBeauty.setLowerJawIntensity(0);
        faceBeauty.setEyeEnlargingIntensity(0.4);
        faceBeauty.setEyeCircleIntensity(0);
        faceBeauty.setChinIntensity(0.3);
        faceBeauty.setForHeadIntensity(0.3);
        faceBeauty.setNoseIntensity(0.5);
        faceBeauty.setMouthIntensity(0.4);
        faceBeauty.setCanthusIntensity(0);
        faceBeauty.setEyeSpaceIntensity(0.5);
        faceBeauty.setEyeRotateIntensity(0.5);
        faceBeauty.setLongNoseIntensity(0.5);
        faceBeauty.setPhiltrumIntensity(0.5);
        faceBeauty.setSmileIntensity(0);
        faceBeauty.setBrowHeightIntensity(0.5);
        faceBeauty.setBrowSpaceIntensity(0.5);
        faceBeauty.setEyeLidIntensity(0);
        faceBeauty.setEyeHeightIntensity(0.5);
        faceBeauty.setBrowThickIntensity(0.5);
        faceBeauty.setLipThickIntensity(0.5);
    }

    public static void resetFilter() {
        faceBeauty.setFilterName(FaceBeautyFilterEnum.ZIRAN_2);
        faceBeauty.setFilterIntensity(0.4);
    }

    //目前没发现什么用
    public void beautyClean() {
        FURenderKit.getInstance().setFaceBeauty(null);
    }

    private static FaceBeauty getDefaultFaceBeauty() {
        faceBeauty = new FaceBeauty(new FUBundleData(PluginConfig.BUNDLE_FACE_BEAUTIFICATION));

        if (PluginConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID) {
            setFaceBeautyPropertyMode(faceBeauty);
        }

        faceBeauty.setFilterName(FaceBeautyFilterEnum.ZIRAN_2);
        faceBeauty.setFilterIntensity(0.4);
        /*美肤*/
        faceBeauty.setSharpenIntensity(0.2);
        faceBeauty.setColorIntensity(0.3);
        faceBeauty.setRedIntensity(0.3);
        faceBeauty.setBlurIntensity(4.2);
        /*美型*/
        faceBeauty.setFaceShapeIntensity(1.0);
        faceBeauty.setEyeEnlargingIntensity(0.4);
        faceBeauty.setCheekVIntensity(0.5);
        faceBeauty.setNoseIntensity(0.5);
        faceBeauty.setForHeadIntensity(0.3);
        faceBeauty.setMouthIntensity(0.4);
        faceBeauty.setChinIntensity(0.3);


//        resetFilter();
//        resetShapeBeauty();
//        resetSkinBeauty();

        return faceBeauty;
    }

    /**
     * 克隆模型
     *
     * @param faceBeauty
     * @return
     */
    public static FaceBeauty clone(FaceBeauty faceBeauty) {
        FaceBeauty cloneFaceBeauty = new FaceBeauty(new FUBundleData(faceBeauty.getControlBundle().getPath()));
        /*滤镜*/
        cloneFaceBeauty.setFilterName(faceBeauty.getFilterName());
        cloneFaceBeauty.setFilterIntensity(faceBeauty.getFilterIntensity());
        /*美肤*/
        cloneFaceBeauty.setBlurIntensity(faceBeauty.getBlurIntensity());
        cloneFaceBeauty.setEnableHeavyBlur(faceBeauty.getEnableHeavyBlur());
        cloneFaceBeauty.setEnableSkinDetect(faceBeauty.getEnableSkinDetect());
        cloneFaceBeauty.setNonSkinBlurIntensity(faceBeauty.getNonSkinBlurIntensity());
        cloneFaceBeauty.setBlurType(faceBeauty.getBlurType());
        cloneFaceBeauty.setEnableBlurUseMask(faceBeauty.getEnableBlurUseMask());
        cloneFaceBeauty.setColorIntensity(faceBeauty.getColorIntensity());
        cloneFaceBeauty.setRedIntensity(faceBeauty.getRedIntensity());
        cloneFaceBeauty.setSharpenIntensity(faceBeauty.getSharpenIntensity());
        cloneFaceBeauty.setEyeBrightIntensity(faceBeauty.getEyeBrightIntensity());
        cloneFaceBeauty.setToothIntensity(faceBeauty.getToothIntensity());
        cloneFaceBeauty.setRemovePouchIntensity(faceBeauty.getRemovePouchIntensity());
        cloneFaceBeauty.setRemoveLawPatternIntensity(faceBeauty.getRemoveLawPatternIntensity());
        cloneFaceBeauty.setFaceThreeIntensity(faceBeauty.getFaceThreeIntensity());
        /*美型*/
        cloneFaceBeauty.setFaceShape(faceBeauty.getFaceShape());
        cloneFaceBeauty.setFaceShapeIntensity(faceBeauty.getFaceShapeIntensity());
        cloneFaceBeauty.setCheekThinningIntensity(faceBeauty.getCheekThinningIntensity());
        cloneFaceBeauty.setCheekVIntensity(faceBeauty.getCheekVIntensity());
        cloneFaceBeauty.setCheekLongIntensity(faceBeauty.getCheekLongIntensity());
        cloneFaceBeauty.setCheekCircleIntensity(faceBeauty.getCheekCircleIntensity());
        cloneFaceBeauty.setCheekNarrowIntensity(faceBeauty.getCheekNarrowIntensity());
        cloneFaceBeauty.setCheekShortIntensity(faceBeauty.getCheekShortIntensity());
        cloneFaceBeauty.setCheekSmallIntensity(faceBeauty.getCheekSmallIntensity());
        cloneFaceBeauty.setCheekBonesIntensity(faceBeauty.getCheekBonesIntensity());
        cloneFaceBeauty.setLowerJawIntensity(faceBeauty.getLowerJawIntensity());
        cloneFaceBeauty.setEyeEnlargingIntensity(faceBeauty.getEyeEnlargingIntensity());
        cloneFaceBeauty.setChinIntensity(faceBeauty.getChinIntensity());
        cloneFaceBeauty.setForHeadIntensity(faceBeauty.getForHeadIntensity());
        cloneFaceBeauty.setNoseIntensity(faceBeauty.getNoseIntensity());
        cloneFaceBeauty.setMouthIntensity(faceBeauty.getMouthIntensity());
        cloneFaceBeauty.setCanthusIntensity(faceBeauty.getCanthusIntensity());
        cloneFaceBeauty.setEyeSpaceIntensity(faceBeauty.getEyeSpaceIntensity());
        cloneFaceBeauty.setEyeRotateIntensity(faceBeauty.getEyeRotateIntensity());
        cloneFaceBeauty.setLongNoseIntensity(faceBeauty.getLongNoseIntensity());
        cloneFaceBeauty.setPhiltrumIntensity(faceBeauty.getPhiltrumIntensity());
        cloneFaceBeauty.setSmileIntensity(faceBeauty.getSmileIntensity());
        cloneFaceBeauty.setEyeCircleIntensity(faceBeauty.getEyeCircleIntensity());
        cloneFaceBeauty.setBrowHeightIntensity(faceBeauty.getBrowHeightIntensity());
        cloneFaceBeauty.setBrowSpaceIntensity(faceBeauty.getBrowSpaceIntensity());
        cloneFaceBeauty.setEyeLidIntensity(faceBeauty.getEyeLidIntensity());
        cloneFaceBeauty.setEyeHeightIntensity(faceBeauty.getEyeHeightIntensity());
        cloneFaceBeauty.setBrowThickIntensity(faceBeauty.getBrowThickIntensity());
        cloneFaceBeauty.setLipThickIntensity(faceBeauty.getLipThickIntensity());
        cloneFaceBeauty.setChangeFramesIntensity(faceBeauty.getChangeFramesIntensity());
        return cloneFaceBeauty;
    }

    /**
     * 高端机的时候，开启4个相对吃性能的模式
     * 1.祛黑眼圈 MODE2
     * 2.祛法令纹 MODE2
     * 3.大眼 MODE3
     * 4.嘴型 MODE3
     */
    private static void setFaceBeautyPropertyMode(FaceBeauty faceBeauty) {
        /*
         * 多模式属性
         * 属性名称|支持模式|默认模式|最早支持版本
         * 美白 colorIntensity|MODE1 MODE2|MODE2|MODE2 8.2.0;
         * 祛黑眼圈 removePouchIntensity|MODE1 MODE2|MODE2|MODE2 8.2.0;
         * 祛法令纹 removeLawPatternIntensity|MODE1 MODE1|MODE2|MODE2 8.2.0;
         * 窄脸程度 cheekNarrowIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 小脸程度 cheekSmallIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 大眼程度 eyeEnlargingIntensity|MODE1 MODE2 MODE3|MODE3|MODE2 8.0.0;MODE3 8.2.0;
         * 额头调整程度 forHeadIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 瘦鼻程度 noseIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 嘴巴调整程度 mouthIntensity|MODE1 MODE2 MODE3|MODE3|MODE2 8.0.0;MODE3 8.2.0;
         */
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.REMOVE_POUCH_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE2);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.REMOVE_NASOLABIAL_FOLDS_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE2);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.EYE_ENLARGING_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE3);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.MOUTH_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE3);
    }
}
