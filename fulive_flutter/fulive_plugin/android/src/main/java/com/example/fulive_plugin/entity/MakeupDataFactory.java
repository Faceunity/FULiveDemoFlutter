package com.example.fulive_plugin.entity;


import com.example.fulive_plugin.FUCommon.DemoConfig;
import com.example.fulive_plugin.entity.bean.MakeupCombinationBean;
import com.example.fulive_plugin.entity.bean.SubMakeupBean;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.makeup.Makeup;
import com.faceunity.core.model.makeup.MakeupBrowWarpEnum;
import com.faceunity.core.model.makeup.MakeupLipEnum;
import com.faceunity.core.utils.DecimalUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_BLUSHER;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_EYE_BROW;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_EYE_LASH;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_EYE_LINER;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_EYE_PUPIL;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_EYE_SHADOW;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_FOUNDATION;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_HIGH_LIGHT;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_LIP_STICK;
import static com.example.fulive_plugin.entity.MakeupSource.FACE_MAKEUP_TYPE_SHADOW;
import static com.example.fulive_plugin.entity.MakeupSource.buildFUColorRGBData;

/**
 * @author benyq
 * @date 2021/11/24
 * @email 1520063035@qq.com
 */
public class MakeupDataFactory {

    private static FURenderKit sFURenderKit = FURenderKit.getInstance();
    private static Makeup currentMakeup = null;

    /*当前滤镜*/
    private static String currentFilterName;
    /*当前滤镜*/
    private static Double currentFilterIntensity;
    /*组合妆容列表*/
    private static ArrayList<MakeupCombinationBean> makeupCombinations = MakeupSource.buildCombinations();

    //美妆颜色表
    private static HashMap<String, ArrayList<double[]>> sMakeUpColorMap = MakeupSource.buildMakeUpColorMap();
    //key:美妆类别  value:当前美妆子项选中下标  默认0
    private static HashMap<String, Integer> sCustomIndexMap = new HashMap<>();
    //key:美妆类别_子项下标    value:当前美妆选中子项的妆容强度  默认1.0
    private static HashMap<String, Double> sCustomIntensityMap = new HashMap<>();
    //获取子妆类别列表
    private static HashMap<String, Integer> sCustomColorIndexMap = new HashMap<>();

    private static List<String> sMakeupCustomClass = MakeupSource.buildCustomClasses();
    private static boolean isCustomMakeup = false;
    private static Makeup oldMakeup = null;

    public static void onMakeupCombinationSelected(int index) {
        MakeupCombinationBean bean = makeupCombinations.get(index);
        currentFilterName = bean.getFilterName();
        currentFilterIntensity = bean.getFilterIntensity();
        if (sFURenderKit.getFaceBeauty() != null) {
            sFURenderKit.getFaceBeauty().setFilterName(currentFilterName);
            sFURenderKit.getFaceBeauty().setFilterIntensity(currentFilterIntensity);
        }
        currentMakeup = MakeupSource.getMakeupModel(bean);
        sFURenderKit.setMakeup(currentMakeup);
    }

    public static void sliderChangeValueWithValue(int index, double intensity) {
        currentMakeup.setMakeupIntensity(intensity);
        currentFilterIntensity = intensity;
        if (sFURenderKit.getFaceBeauty() != null) {
            sFURenderKit.getFaceBeauty().setFilterIntensity(currentFilterIntensity);
        }
    }

    public static void configureMakeup() {
        sFURenderKit.setFaceBeauty(MakeupSource.clone(FaceBeautyDataFactory.faceBeauty));
        if (currentFilterName != null) {
            sFURenderKit.getFaceBeauty().setFilterName(currentFilterName);
            sFURenderKit.getFaceBeauty().setFilterIntensity(currentFilterIntensity);
        }
        sFURenderKit.setMakeup(currentMakeup);
        FUAIKit.getInstance().setMaxFaces(4);
        if (currentMakeup != null) {
            //特殊有一些需要设置图层混合模式的 04双色眼影3（第2层眼影的混合模式 == 1） 06三色眼影2（第3层眼影的混合模式 == 1）
            if (currentMakeup.getEyeShadowBundle() != null && ("mu_style_eyeshadow_0" + 4).equals(currentMakeup.getEyeShadowBundle().getName()))
                currentMakeup.setEyeShadowTexBlend2(1);
            else if (currentMakeup.getEyeShadowBundle() != null && ("mu_style_eyeshadow_0" + 6).equals(currentMakeup.getEyeShadowBundle().getName()))
                currentMakeup.setEyeShadowTexBlend3(1);
        }
    }


    public static void releaseMakeup() {
        sFURenderKit.setMakeup(null);
    }

    //region 子美妆

    /**
     * 设置子妆强度
     */
    public static void updateCustomItemIntensity(int index, int current, double intensity) {
        String key = sMakeupCustomClass.get(index);
        if (key.equals(FACE_MAKEUP_TYPE_FOUNDATION)) {
            currentMakeup.setFoundationIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_LIP_STICK)) {
            currentMakeup.setLipIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_BLUSHER)) {
            currentMakeup.setBlusherIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_BROW)) {
            currentMakeup.setEyeBrowIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_SHADOW)) {
            currentMakeup.setEyeShadowIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LINER)) {
            currentMakeup.setEyeLineIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LASH)) {
            currentMakeup.setEyeLashIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_HIGH_LIGHT)) {
            currentMakeup.setHeightLightIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_SHADOW)) {
            currentMakeup.setShadowIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_PUPIL)) {
            currentMakeup.setPupilIntensity(intensity);
        }
        sCustomIntensityMap.put(key + "_" + current, intensity);
    }

    /**
     * 切换子妆单项
     *
     * @param subTitleIndex   子妆类别下标
     * @param index 选中下标
     */
    public static void onCustomBeanSelected(int subTitleIndex, int index) {
        String itemDir = DemoConfig.MAKEUP_RESOURCE_ITEM_BUNDLE_DIR;
        String key = sMakeupCustomClass.get(subTitleIndex);
        sCustomIndexMap.put(key, index);
        if (key.equals(FACE_MAKEUP_TYPE_FOUNDATION)) {
            if (index == 0) {
                currentMakeup.setFoundationIntensity(0.0);
            } else {
                currentMakeup.setFoundationBundle(new FUBundleData(itemDir + "mu_style_foundation_01.bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_FOUNDATION + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_FOUNDATION + "_" + index);
                }
                currentMakeup.setFoundationIntensity((intensity));
                index -= 1;
                updateCustomColor(key, index);
                double[] color = sMakeUpColorMap.get("color_mu_style_foundation_01").get(index);
                currentMakeup.setFoundationColor(buildFUColorRGBData(color));
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_LIP_STICK)) {
            if (index == 0) {
                currentMakeup.setLipIntensity(0.0);
            } else {
                switch (index) {
                    case 1:
                        currentMakeup.setLipType(MakeupLipEnum.FOG);
                        currentMakeup.setEnableTwoLipColor(false);
                        break;
                    case 2:
                        currentMakeup.setLipType(MakeupLipEnum.MOIST);
                        currentMakeup.setEnableTwoLipColor(false);
                        break;
                    case 3:
                        currentMakeup.setLipType(MakeupLipEnum.PEARL);
                        currentMakeup.setEnableTwoLipColor(false);
                        break;
                    case 4:
                        currentMakeup.setLipType(MakeupLipEnum.FOG);
                        currentMakeup.setEnableTwoLipColor(true);
                        currentMakeup.setLipColor2(new FUColorRGBData(0.0, 0.0, 0.0, 0.0));
                        break;
                }
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index);
                }
                currentMakeup.setLipIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_BLUSHER)) {
            if (index == 0) {
                currentMakeup.setBlusherIntensity(0.0);
            } else {
                currentMakeup.setBlusherBundle(new FUBundleData(itemDir + "mu_style_blush_0" + index + ".bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_BLUSHER + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_BLUSHER + "_" + index);
                }
                currentMakeup.setBlusherIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_BLUSHER + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_BLUSHER + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_BROW)) {
            if (index == 0) {
                currentMakeup.setEyeBrowIntensity(0.0);
                currentMakeup.setEnableBrowWarp(false);
            } else {
                currentMakeup.setEnableBrowWarp(true);
                currentMakeup.setEyeBrowBundle(new FUBundleData(itemDir + "mu_style_eyebrow_01.bundle"));
                switch (index) {
                    case 1:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.WILLOW);
                        break;
                    case 2:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.STANDARD);
                        break;
                    case 3:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.HILL);
                        break;
                    case 4:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.ONE_WORD);
                        break;
                    case 5:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.SHAPE);
                        break;
                    case 6:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.DAILY);
                        break;
                    case 7:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.JAPAN);
                        break;
                }
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index);
                }
                currentMakeup.setEyeBrowIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_SHADOW)) {
            if (index == 0) {
                currentMakeup.setEyeShadowIntensity(0.0);
            } else {
                currentMakeup.setEyeShadowBundle(new FUBundleData(itemDir + "mu_style_eyeshadow_0" + index + ".bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index);
                }
                currentMakeup.setEyeShadowIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LINER)) {
            if (index == 0) {
                currentMakeup.setEyeLineIntensity(0.0);
            } else {
                currentMakeup.setEyeLinerBundle(new FUBundleData(itemDir + "mu_style_eyeliner_0" + index + ".bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index);
                }
                currentMakeup.setEyeLineIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LASH)) {
            if (index == 0) {
                currentMakeup.setEyeLashIntensity(0.0);
            } else {
                currentMakeup.setEyeLashBundle(new FUBundleData(itemDir + "mu_style_eyelash_0" + index + ".bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index);
                }
                currentMakeup.setEyeLashIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_HIGH_LIGHT)) {
            if (index == 0) {
                currentMakeup.setHeightLightIntensity(0.0);
            } else {
                currentMakeup.setHighLightBundle(new FUBundleData(itemDir + "mu_style_highlight_0" + index + ".bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index);
                }
                currentMakeup.setHeightLightIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_SHADOW)) {
            if (index == 0) {
                currentMakeup.setShadowIntensity(0.0);
            } else {
                currentMakeup.setShadowBundle(new FUBundleData(itemDir + "mu_style_contour_01.bundle"));
                double intensity = 1.0;
                if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_SHADOW + "_" + index)) {
                    intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_SHADOW + "_" + index);
                }
                currentMakeup.setShadowIntensity((intensity));
                int colorIndex = 3;
                if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_SHADOW + "_" + index)) {
                    colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_SHADOW + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_PUPIL)) {
            if (index == 0) {
                currentMakeup.setPupilIntensity(0.0);
            } else {
                if (index == 1) {
                    currentMakeup.setPupilBundle(new FUBundleData(itemDir + "mu_style_eyepupil_01.bundle"));
                    double intensity = 1.0;
                    if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index)) {
                        intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index);
                    }
                    currentMakeup.setPupilIntensity((intensity));
                    int colorIndex = 3;
                    if (sCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index)) {
                        colorIndex = sCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index);
                    }
                    updateCustomColor(key, colorIndex);
                } else {
                    currentMakeup.setPupilBundle(new FUBundleData(itemDir + "mu_style_eyepupil_0" + (index + 1) + ".bundle"));
                    double intensity = 1.0;
                    if (sCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index)) {
                        intensity = sCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index);
                    }
                    currentMakeup.setPupilIntensity((intensity));
                    currentMakeup.setPupilColor(buildFUColorRGBData(new double[]{0.0, 0.0, 0.0, 0.0}));
                }

            }
        }

    }

    /**
     * 设置子妆颜色值
     *
     * @param subIndex   类别下标
     * @param index 颜色下标
     */
    public static void updateCustomColor(int subIndex, int index) {
        String key = sMakeupCustomClass.get(subIndex);
        updateCustomColor(key, index);
    }

    public static void enterCustomMakeup() {
        isCustomMakeup = true;
        sCustomIndexMap.clear();
        sCustomColorIndexMap.clear();
        sCustomIntensityMap.clear();
        double makeupIntensity = currentMakeup.getMakeupIntensity();
        /*粉底*/
        if (currentMakeup.getFoundationIntensity() != 0.0) {
            double intensity = currentMakeup.getFoundationIntensity() * makeupIntensity;
            currentMakeup.setFoundationIntensity(intensity);
            double[] array = currentMakeup.getFoundationColor().toScaleColorArray();
            ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_foundation_01");
            for (int i = 0; i < list.size(); i++) {
                if (DecimalUtils.doubleArrayEquals(array, list.get(i))) {
                    sCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, i + 1);
                    sCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_" +(i + 1), intensity);
                    break;
                }
            }

        }
        /*口红*/
        if (currentMakeup.getLipIntensity() != 0.0) {
            double intensity = currentMakeup.getLipIntensity() * makeupIntensity;
            currentMakeup.setLipIntensity(intensity);
            int current = 0;
            switch (currentMakeup.getLipType()) {
                case MakeupLipEnum.FOG:
                    if (currentMakeup.getEnableTwoLipColor()) {
                        current = 4;
                    } else {
                        current = 1;
                    }
                    break;
                case MakeupLipEnum.MOIST:
                    current = 2;
                    break;
                case MakeupLipEnum.PEARL:
                    current = 3;
                    break;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getLipColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_lip_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_" + current, intensity);
            }
        }
        /*腮红*/
        if (currentMakeup.getBlusherIntensity() != 0.0 && currentMakeup.getBlusherBundle() != null) {
            double intensity = currentMakeup.getBlusherIntensity() * makeupIntensity;
            currentMakeup.setBlusherIntensity(intensity);
            String path = currentMakeup.getBlusherBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_blush_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_blush_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_blush_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_blush_04.bundle")) {
                current = 4;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getBlusherColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_blush_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_" + current, intensity);
            }
        }
        /*眉毛*/
        if (currentMakeup.getEyeBrowIntensity() != 0.0) {
            double intensity = currentMakeup.getEyeBrowIntensity() * makeupIntensity;
            currentMakeup.setEyeBrowIntensity(intensity);
            int current = 0;
            switch (currentMakeup.getBrowWarpType()) {
                case MakeupBrowWarpEnum.WILLOW:
                    current = 1;
                    break;
                case MakeupBrowWarpEnum.STANDARD:
                    current = 2;
                    break;
                case MakeupBrowWarpEnum.HILL:
                    current = 3;
                    break;
                case MakeupBrowWarpEnum.ONE_WORD:
                    current = 4;
                    break;
                case MakeupBrowWarpEnum.SHAPE:
                    current = 5;
                    break;
                case MakeupBrowWarpEnum.DAILY:
                    current = 6;
                    break;
                case MakeupBrowWarpEnum.JAPAN:
                    current = 7;
                    break;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getEyeBrowColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_eyebrow_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_" + current, intensity);
            }
        }
        /*眼影*/
        if (currentMakeup.getEyeShadowIntensity() != 0.0 && currentMakeup.getEyeShadowBundle() != null) {
            double intensity = currentMakeup.getEyeShadowIntensity() * makeupIntensity;
            currentMakeup.setEyeShadowIntensity(intensity);
            String path = currentMakeup.getEyeShadowBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyeshadow_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyeshadow_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyeshadow_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyeshadow_04.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyeshadow_05.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyeshadow_06.bundle")) {
                current = 6;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, current);
            if (current != 0) {
                double[] array = new double[12];
                double[] color1 = currentMakeup.getEyeShadowColor().toScaleColorArray();
                double[] color2 = currentMakeup.getEyeShadowColor2().toScaleColorArray();
                double[] color3 = currentMakeup.getEyeShadowColor3().toScaleColorArray();
                System.arraycopy(color1, 0, array, 0, color1.length);
                System.arraycopy(color2, 0, array, 4, color2.length);
                System.arraycopy(color3, 0, array, 8, color3.length);
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_eyeshadow_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(array, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + current, intensity);
            }
        }
        /*眼线*/
        if (currentMakeup.getEyeLineIntensity() != 0.0 && currentMakeup.getEyeLinerBundle() != null) {
            double intensity = currentMakeup.getEyeLineIntensity() * makeupIntensity;
            currentMakeup.setEyeLineIntensity(intensity);
            String path = currentMakeup.getEyeLinerBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyeliner_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyeliner_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyeliner_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyeliner_04.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyeliner_05.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyeliner_06.bundle")) {
                current = 6;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getEyeLinerColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_eyeliner_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_" + current, intensity);
            }
        }
        /* 睫毛*/
        if (currentMakeup.getEyeLashIntensity() != 0.0 && currentMakeup.getEyeLashBundle() != null) {
            double intensity = currentMakeup.getEyeLashIntensity() * makeupIntensity;
            currentMakeup.setEyeLashIntensity(intensity);
            String path = currentMakeup.getEyeLashBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyelash_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyelash_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyelash_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyelash_04.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyelash_05.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyelash_06.bundle")) {
                current = 6;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getEyeLashColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_eyelash_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_" + current, intensity);
            }
        }
        /* 高光*/
        if (currentMakeup.getHeightLightIntensity() != 0.0 && currentMakeup.getHighLightBundle() != null) {
            double intensity = currentMakeup.getHeightLightIntensity() * makeupIntensity;
            currentMakeup.setHeightLightIntensity(intensity);
            String path = currentMakeup.getHighLightBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_highlight_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_highlight_02.bundle")) {
                current = 2;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getHighLightColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_highlight_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + current, intensity);
            }
        }
        /* 阴影*/
        if (currentMakeup.getShadowIntensity() != 0.0 && currentMakeup.getShadowBundle() != null) {
            double intensity = currentMakeup.getShadowIntensity() * makeupIntensity;
            currentMakeup.setShadowIntensity(intensity);
            String path = currentMakeup.getShadowBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_contour_01.bundle")) {
                current = 1;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getShadowColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_contour_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_" + current, intensity);
            }
        }
        /* 美瞳*/
        if (currentMakeup.getPupilIntensity() != 0.0 && currentMakeup.getPupilBundle() != null) {
            double intensity = currentMakeup.getPupilIntensity() * makeupIntensity;
            currentMakeup.setPupilIntensity(intensity);
            String path = currentMakeup.getPupilBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyepupil_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyepupil_03.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyepupil_04.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyepupil_05.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyepupil_06.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyepupil_07.bundle")) {
                current = 6;
            } else if (path.endsWith("mu_style_eyepupil_08.bundle")) {
                current = 7;
            } else if (path.endsWith("mu_style_eyepupil_09.bundle")) {
                current = 8;
            }
            sCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getPupilColor().toScaleColorArray();
                ArrayList<double[]> list = sMakeUpColorMap.get("color_mu_style_eyepupil_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + current, i);
                        break;
                    }
                }
                sCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + current, intensity);
            }
        }
        currentMakeup.setMakeupIntensity(1.0);
        oldMakeup = makeupClone();
    }

    public static void exitCustomMakeup() {
        isCustomMakeup = false;
    }
    /**
     * 设置子妆颜色值
     *
     * @param key   类别关键字
     * @param index 颜色下标
     */
    public static void updateCustomColor(String key, int index) {
        int current = sCustomIndexMap.containsKey(key) ? sCustomIndexMap.get(key) : 0;
        if (key.equals(FACE_MAKEUP_TYPE_LIP_STICK)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_lip_01").get(index);
            currentMakeup.setLipColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_BLUSHER)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_blush_0" + current).get(index);
            currentMakeup.setBlusherColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_BROW)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_eyebrow_01").get(index);
            currentMakeup.setEyeBrowColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_SHADOW)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_eyeshadow_0" + current).get(index);
            currentMakeup.setEyeShadowColor(new FUColorRGBData(color[0] * 255, color[1] * 255, color[2] * 255, color[3] * 255));
            currentMakeup.setEyeShadowColor2(new FUColorRGBData(color[4] * 255, color[5] * 255, color[6] * 255, color[7] * 255));
            currentMakeup.setEyeShadowColor3(new FUColorRGBData(color[8] * 255, color[9] * 255, color[10] * 255, color[11] * 255));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LINER)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_eyeliner_0" + current).get(index);
            currentMakeup.setEyeLinerColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LASH)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_eyelash_0" + current).get(index);
            currentMakeup.setEyeLashColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_HIGH_LIGHT)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_highlight_0" + current).get(index);
            currentMakeup.setHighLightColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_SHADOW)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_contour_01").get(index);
            currentMakeup.setShadowColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_PUPIL)) {
            sCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + current, index);
            double[] color = sMakeUpColorMap.get("color_mu_style_eyepupil_01").get(index);
            currentMakeup.setPupilColor(buildFUColorRGBData(color));
        }
    }

    public static String createMakeupParamJson(int index) throws Exception {
        JSONArray jsonArray = new JSONArray();
        JSONObject jObject = new JSONObject();

        List<SubMakeupBean> result = new ArrayList<>();
        result.add(new SubMakeupBean("粉底"));
        result.add(new SubMakeupBean("口红"));
        result.add(new SubMakeupBean("腮红"));
        result.add(new SubMakeupBean("眉毛"));
        result.add(new SubMakeupBean("眼影"));
        result.add(new SubMakeupBean("眼线"));
        result.add(new SubMakeupBean("睫毛"));
        result.add(new SubMakeupBean("高光"));
        result.add(new SubMakeupBean("阴影"));
        result.add(new SubMakeupBean("美瞳"));

        for (String key : sCustomIntensityMap.keySet()) {

            SubMakeupBean subMakeupBean = result.get(0);

            int lastIndex = key.lastIndexOf("_");
            if (lastIndex < 0) {
                continue;
            }
            String type = key.substring(0, lastIndex);

            if (FACE_MAKEUP_TYPE_FOUNDATION.equals(type)) {
                subMakeupBean = result.get(0);
            }else if (FACE_MAKEUP_TYPE_LIP_STICK.equals(type)) {
                subMakeupBean = result.get(1);
            }else if (FACE_MAKEUP_TYPE_BLUSHER.equals(type)) {
                subMakeupBean = result.get(2);
            }else if (FACE_MAKEUP_TYPE_EYE_BROW.equals(type)) {
                subMakeupBean = result.get(3);
            }else if (FACE_MAKEUP_TYPE_EYE_SHADOW.equals(type)) {
                subMakeupBean = result.get(4);
            }else if (FACE_MAKEUP_TYPE_EYE_LINER.equals(type)) {
                subMakeupBean = result.get(5);
            }else if (FACE_MAKEUP_TYPE_EYE_LASH.equals(type)) {
                subMakeupBean = result.get(6);
            }else if (FACE_MAKEUP_TYPE_HIGH_LIGHT.equals(type)) {
                subMakeupBean = result.get(7);
            }else if (FACE_MAKEUP_TYPE_SHADOW.equals(type)) {
                subMakeupBean = result.get(8);
            }else if (FACE_MAKEUP_TYPE_EYE_PUPIL.equals(type)) {
                subMakeupBean = result.get(9);
            }

            String subIndex = key.substring(lastIndex + 1, key.length());
            if (parseInteger(subIndex) < 0) {
                subMakeupBean.setBundleIndex(0);
            }else {
                subMakeupBean.setBundleIndex(parseInteger(subIndex));
            }
            subMakeupBean.setValue(String.valueOf(sCustomIntensityMap.get(key)));

            Integer colorIndex = sCustomColorIndexMap.get(key);
            subMakeupBean.setColorIndex(colorIndex != null ? colorIndex : 0);
        }

        for (SubMakeupBean makeupBean : result) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", makeupBean.getTitle());
            jsonObject.put("bundleIndex", makeupBean.getBundleIndex());
            jsonObject.put("colorIndex", makeupBean.getColorIndex());
            jsonObject.put("value", makeupBean.getValue());
            jsonArray.put(jsonObject);
        }
        jObject.put("sub", jsonArray);
        return jObject.toString();
    }

    private static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e) {
            return 0;
        }
    }

    private static Makeup makeupClone() {
        if (currentMakeup == null) {
            return null;
        }
        Makeup makeup = new Makeup(new FUBundleData(currentMakeup.getControlBundle().getPath()));
        //强度
        makeup.setMakeupIntensity(currentMakeup.getMakeupIntensity());
        makeup.setLipIntensity(currentMakeup.getLipIntensity());
        makeup.setPupilIntensity(currentMakeup.getPupilIntensity());
        makeup.setEyeShadowIntensity(currentMakeup.getEyeShadowIntensity());
        makeup.setEyeLineIntensity(currentMakeup.getEyeLineIntensity());
        makeup.setEyeLashIntensity(currentMakeup.getEyeLashIntensity());
        makeup.setEyeBrowIntensity(currentMakeup.getEyeBrowIntensity());
        makeup.setBlusherIntensity(currentMakeup.getBlusherIntensity());
        makeup.setFoundationIntensity(currentMakeup.getFoundationIntensity());
        makeup.setHeightLightIntensity(currentMakeup.getHeightLightIntensity());
        makeup.setShadowIntensity(currentMakeup.getShadowIntensity());

        makeup.setLipType(currentMakeup.getLipType());
        makeup.setEnableTwoLipColor(currentMakeup.getEnableTwoLipColor());
        makeup.setLipColor(currentMakeup.getLipColor());
        makeup.setLipColor2(currentMakeup.getLipColor2());
        makeup.setEnableBrowWarp(currentMakeup.getEnableBrowWarp());
        makeup.setBrowWarpType(currentMakeup.getBrowWarpType());

        makeup.setEyeLinerColor(currentMakeup.getEyeLinerColor());
        makeup.setEyeLashColor(currentMakeup.getEyeLashColor());
        makeup.setBlusherColor(currentMakeup.getBlusherColor());
        makeup.setBlusherColor2(currentMakeup.getBlusherColor2());
        makeup.setFoundationColor(currentMakeup.getFoundationColor());
        makeup.setHighLightColor(currentMakeup.getHighLightColor());
        makeup.setShadowColor(currentMakeup.getShadowColor());
        makeup.setEyeBrowColor(currentMakeup.getEyeBrowColor());
        makeup.setPupilColor(currentMakeup.getPupilColor());
        makeup.setEyeShadowColor(currentMakeup.getEyeShadowColor());
        makeup.setEyeShadowColor2(currentMakeup.getEyeShadowColor2());
        makeup.setEyeShadowColor3(currentMakeup.getEyeShadowColor3());
        makeup.setEyeShadowColor4(currentMakeup.getEyeShadowColor4());
        makeup.setEyeBrowBundle(currentMakeup.getEyeBrowBundle());
        makeup.setEyeShadowBundle(currentMakeup.getEyeShadowBundle());
        makeup.setEyeShadowBundle2(currentMakeup.getEyeShadowBundle2());
        makeup.setEyeShadowBundle3(currentMakeup.getEyeShadowBundle3());
        makeup.setEyeShadowBundle4(currentMakeup.getEyeShadowBundle4());

        makeup.setPupilBundle(currentMakeup.getPupilBundle());
        makeup.setEyeLashBundle(currentMakeup.getEyeLashBundle());
        makeup.setEyeLinerBundle(currentMakeup.getEyeLinerBundle());
        makeup.setBlusherBundle(currentMakeup.getBlusherBundle());
        makeup.setBlusherBundle2(currentMakeup.getBlusherBundle2());
        makeup.setFoundationBundle(currentMakeup.getFoundationBundle());
        makeup.setHighLightBundle(currentMakeup.getHighLightBundle());
        makeup.setShadowBundle(currentMakeup.getShadowBundle());

        makeup.setEyeShadowTexBlend(currentMakeup.getEyeShadowTexBlend());
        makeup.setEyeShadowTexBlend2(currentMakeup.getEyeShadowTexBlend2());
        makeup.setEyeShadowTexBlend3(currentMakeup.getEyeShadowTexBlend3());
        makeup.setEyeShadowTexBlend4(currentMakeup.getEyeShadowTexBlend4());
        makeup.setEyeLashTexBlend(currentMakeup.getEyeLashTexBlend());
        makeup.setEyeLinerTexBlend(currentMakeup.getEyeLinerTexBlend());
        makeup.setBlusherTexBlend(currentMakeup.getBlusherTexBlend());
        makeup.setBlusherTexBlend2(currentMakeup.getBlusherTexBlend2());
        makeup.setPupilTexBlend(currentMakeup.getPupilTexBlend());
        return makeup;
    }

    public static boolean checkOldParam() {
        if (oldMakeup == null || currentMakeup == null) {
            return false;
        }
        if (oldMakeup.getLipIntensity() != currentMakeup.getLipIntensity()) return false;
        if (oldMakeup.getPupilIntensity() != currentMakeup.getPupilIntensity()) return false;
        if (oldMakeup.getEyeShadowIntensity() != currentMakeup.getEyeShadowIntensity()) return false;
        if (oldMakeup.getEyeLineIntensity() != currentMakeup.getEyeLineIntensity()) return false;
        if (oldMakeup.getEyeLashIntensity() != currentMakeup.getEyeLashIntensity()) return false;
        if (oldMakeup.getEyeBrowIntensity() != currentMakeup.getEyeBrowIntensity()) return false;
        if (oldMakeup.getBlusherIntensity() != currentMakeup.getBlusherIntensity()) return false;
        if (oldMakeup.getFoundationIntensity() != currentMakeup.getFoundationIntensity()) return false;
        if (oldMakeup.getHeightLightIntensity() != currentMakeup.getHeightLightIntensity()) return false;
        if (oldMakeup.getShadowIntensity() != currentMakeup.getShadowIntensity()) return false;
        if (oldMakeup.getLipType() != currentMakeup.getLipType()) return false;
        if (oldMakeup.getEnableTwoLipColor() != currentMakeup.getEnableTwoLipColor()) return false;
        if (oldMakeup.getLipColor() != currentMakeup.getLipColor()) return false;
        if (oldMakeup.getLipColor2() != currentMakeup.getLipColor2()) return false;
        if (oldMakeup.getEnableBrowWarp() != currentMakeup.getEnableBrowWarp()) return false;
        if (oldMakeup.getBrowWarpType() != currentMakeup.getBrowWarpType()) return false;
        if (oldMakeup.getEyeLinerColor() != currentMakeup.getEyeLinerColor()) return false;
        if (oldMakeup.getEyeLashColor() != currentMakeup.getEyeLashColor()) return false;
        if (oldMakeup.getBlusherColor() != currentMakeup.getBlusherColor()) return false;
        if (oldMakeup.getBlusherColor2() != currentMakeup.getBlusherColor2()) return false;
        if (oldMakeup.getFoundationColor() != currentMakeup.getFoundationColor()) return false;
        if (oldMakeup.getHighLightColor() != currentMakeup.getHighLightColor()) return false;
        if (oldMakeup.getHighLightColor() != currentMakeup.getHighLightColor()) return false;
        if (oldMakeup.getEyeBrowColor() != currentMakeup.getEyeBrowColor()) return false;
        if (oldMakeup.getPupilColor() != currentMakeup.getPupilColor()) return false;
        if (oldMakeup.getEyeShadowColor() != currentMakeup.getEyeShadowColor()) return false;
        if (oldMakeup.getEyeShadowColor2() != currentMakeup.getEyeShadowColor2()) return false;
        if (oldMakeup.getEyeShadowColor3() != currentMakeup.getEyeShadowColor3()) return false;
        if (oldMakeup.getEyeShadowColor4() != currentMakeup.getEyeShadowColor4()) return false;
        if (oldMakeup.getEyeBrowBundle() != currentMakeup.getEyeBrowBundle()) return false;
        if (oldMakeup.getEyeShadowBundle() != currentMakeup.getEyeShadowBundle()) return false;
        if (oldMakeup.getEyeShadowBundle2() != currentMakeup.getEyeShadowBundle2()) return false;
        if (oldMakeup.getEyeShadowBundle3() != currentMakeup.getEyeShadowBundle3()) return false;
        if (oldMakeup.getEyeShadowBundle4() != currentMakeup.getEyeShadowBundle4()) return false;
        if (oldMakeup.getPupilBundle() != currentMakeup.getPupilBundle()) return false;
        if (oldMakeup.getEyeLashBundle() != currentMakeup.getEyeLashBundle()) return false;
        if (oldMakeup.getEyeLinerBundle() != currentMakeup.getEyeLinerBundle()) return false;
        if (oldMakeup.getBlusherBundle() != currentMakeup.getBlusherBundle()) return false;
        if (oldMakeup.getBlusherBundle2() != currentMakeup.getBlusherBundle2()) return false;
        if (oldMakeup.getFoundationBundle() != currentMakeup.getFoundationBundle()) return false;
        if (oldMakeup.getHighLightBundle() != currentMakeup.getHighLightBundle()) return false;
        if (oldMakeup.getShadowBundle() != currentMakeup.getShadowBundle()) return false;
        if (oldMakeup.getEyeShadowTexBlend() != currentMakeup.getEyeShadowTexBlend()) return false;
        if (oldMakeup.getEyeShadowTexBlend2() != currentMakeup.getEyeShadowTexBlend2()) return false;
        if (oldMakeup.getEyeShadowTexBlend3() != currentMakeup.getEyeShadowTexBlend3()) return false;
        if (oldMakeup.getEyeShadowTexBlend4() != currentMakeup.getEyeShadowTexBlend4()) return false;
        if (oldMakeup.getEyeLashTexBlend() != currentMakeup.getEyeLashTexBlend()) return false;
        if (oldMakeup.getEyeLinerTexBlend() != currentMakeup.getEyeLinerTexBlend()) return false;
        if (oldMakeup.getBlusherTexBlend() != currentMakeup.getBlusherTexBlend()) return false;
        if (oldMakeup.getBlusherTexBlend2() != currentMakeup.getBlusherTexBlend2()) return false;
        if (oldMakeup.getPupilTexBlend() != currentMakeup.getPupilTexBlend()) return false;
        if (oldMakeup.getMakeupIntensity() != currentMakeup.getMakeupIntensity()) return false;

        return true;
    }

    //endregion 子美妆
}
