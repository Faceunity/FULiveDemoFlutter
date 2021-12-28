package com.example.fulive_plugin.entity;

import com.example.fulive_plugin.FUCommon.DemoConfig;
import com.example.fulive_plugin.FULivePlugin;
import com.example.fulive_plugin.entity.bean.MakeupCombinationBean;
import com.example.fulive_plugin.entity.bean.MakeupCombinationBean.TypeEnum;
import com.faceunity.core.controller.makeup.MakeupParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.core.model.makeup.Makeup;
import com.faceunity.core.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benyq
 * @date 2021/11/24
 * @email 1520063035@qq.com
 */
public class MakeupSource {
    
    /**
     * 构造美妆组合妆容配置
     *
     * @return ArrayList<MakeupCombinationBean>
     */
    public static ArrayList<MakeupCombinationBean> buildCombinations() {
        ArrayList<MakeupCombinationBean> combinations = new ArrayList<MakeupCombinationBean>();
        String jsonDir = DemoConfig.MAKEUP_RESOURCE_JSON_DIR;
        String bundleDir = DemoConfig.MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR;
        combinations.add(new MakeupCombinationBean("origin", TypeEnum.TYPE_NONE, null, "", FaceBeautyFilterEnum.ZIRAN_2,0.4));
        combinations.add(
                new MakeupCombinationBean(
                        "jianling", TypeEnum.TYPE_THEME,
                        bundleDir + "jianling.bundle", jsonDir + "jianling.json", FaceBeautyFilterEnum.ZHIGANHUI_1
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "nuandong", TypeEnum.TYPE_THEME,
                        bundleDir + "nuandong.bundle", jsonDir + "nuandong.json", FaceBeautyFilterEnum.ZHIGANHUI_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "hongfeng", TypeEnum.TYPE_THEME,
                        bundleDir + "hongfeng.bundle", jsonDir + "hongfeng.json", FaceBeautyFilterEnum.ZHIGANHUI_3)
        );
        combinations.add(
                new MakeupCombinationBean(
                        "rose", TypeEnum.TYPE_THEME,
                        bundleDir + "rose.bundle", jsonDir + "rose.json", FaceBeautyFilterEnum.ZHIGANHUI_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "shaonv", TypeEnum.TYPE_THEME,
                        bundleDir + "shaonv.bundle", jsonDir + "shaonv.json", FaceBeautyFilterEnum.ZHIGANHUI_4
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "ziyun", TypeEnum.TYPE_THEME,
                        bundleDir + "ziyun.bundle", jsonDir + "ziyun.json", FaceBeautyFilterEnum.ZHIGANHUI_1
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "yanshimao", TypeEnum.TYPE_THEME,
                        bundleDir + "yanshimao.bundle", jsonDir + "yanshimao.json", FaceBeautyFilterEnum.ZHIGANHUI_5
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "renyu", TypeEnum.TYPE_THEME,
                        bundleDir + "renyu.bundle", jsonDir + "renyu.json", FaceBeautyFilterEnum.ZHIGANHUI_1
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "chuqiu", TypeEnum.TYPE_THEME,
                        bundleDir + "chuqiu.bundle", jsonDir + "chuqiu.json", FaceBeautyFilterEnum.ZHIGANHUI_6
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "qianzhihe", TypeEnum.TYPE_THEME,
                        bundleDir + "qianzhihe.bundle", jsonDir + "qianzhihe.json", FaceBeautyFilterEnum.ZHIGANHUI_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "chaomo", TypeEnum.TYPE_THEME,
                        bundleDir + "chaomo.bundle", jsonDir + "chaomo.json", FaceBeautyFilterEnum.ZHIGANHUI_7
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "chuju", TypeEnum.TYPE_THEME,
                        bundleDir + "chuju.bundle", jsonDir + "chuju.json", FaceBeautyFilterEnum.ZHIGANHUI_8
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "gangfeng", TypeEnum.TYPE_THEME,
                        bundleDir + "gangfeng.bundle", jsonDir + "gangfeng.json", FaceBeautyFilterEnum.ZIRAN_8
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "xinggan", TypeEnum.TYPE_DAILY,
                        bundleDir + "xinggan.bundle", jsonDir + "xinggan.json", FaceBeautyFilterEnum.ZIRAN_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "tianmei", TypeEnum.TYPE_DAILY,
                        bundleDir + "tianmei.bundle", jsonDir + "tianmei.json", FaceBeautyFilterEnum.ZIRAN_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "linjia", TypeEnum.TYPE_DAILY,
                        bundleDir + "linjia.bundle", jsonDir + "linjia.json", FaceBeautyFilterEnum.ZIRAN_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "oumei", TypeEnum.TYPE_DAILY,
                        bundleDir + "oumei.bundle", jsonDir + "oumei.json", FaceBeautyFilterEnum.ZIRAN_2
                )
        );
        combinations.add(
                new MakeupCombinationBean(
                        "wumei", TypeEnum.TYPE_DAILY,
                        bundleDir + "wumei.bundle", jsonDir + "wumei.json", FaceBeautyFilterEnum.ZIRAN_2
                )
        );
        return combinations;
    }

    public static Makeup getMakeupModel(MakeupCombinationBean bean) {

        Makeup makeupModel = new Makeup(new FUBundleData(DemoConfig.BUNDLE_FACE_MAKEUP));

        if (bean.getKey().equals("origin")) {
            return makeupModel;
        }
        if (bean.getBundlePath() != null && bean.getBundlePath().trim().length() > 0) {
            makeupModel.setCombinedConfig(new FUBundleData(bean.getBundlePath()));
        }
        makeupModel.setMakeupIntensity(bean.getIntensity());
        if (bean.getJsonPathParams() == null) {
            bean.setJsonPathParams(getLocalParams(bean.getJsonPath()));
        }
        LinkedHashMap<String, Object> params = bean.getJsonPathParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof double[] && ((double[]) value).length > 4) {
                int count = ((double[]) value).length / 4;
                for (int i = 0; i < count; i++) {
                    if (i == 0) {
                        if (makeupSetMapping.containsKey(key)) {
                            makeupSetMapping.get(key).setValue(makeupModel, value);
                        }
                    } else {
                        if (makeupSetMapping.containsKey(key + (i + 1))) {
                            makeupSetMapping.get(key + (i + 1)).setValue(makeupModel, value);
                        }
                    }
                }
            } else {
                if (makeupSetMapping.containsKey(key)) {
                    makeupSetMapping.get(key).setValue(makeupModel, value);
                }
            }
        }

        return makeupModel;
    }


    /*美妆映射模型*/
    public static HashMap<String, MakeupSetParam> makeupSetMapping = new HashMap<String, MakeupSetParam>() {
        {
            put(MakeupParam.LIP_TYPE, (makeup, value) -> makeup.setLipType((int) value));
            put(MakeupParam.IS_TWO_COLOR, (makeup, value) -> makeup.setEnableTwoLipColor((int) value == 1));
            put(MakeupParam.BROW_WARP, (makeup, value) -> makeup.setEnableBrowWarp((double) value == 1.0));
            put(MakeupParam.BROW_WARP_TYPE, (makeup, value) -> makeup.setBrowWarpType((int) value));
            /*强度*/
            put(MakeupParam.MAKEUP_INTENSITY, (makeup, value) -> makeup.setMakeupIntensity((double) value));
            put(MakeupParam.LIP_INTENSITY, (makeup, value) -> makeup.setLipIntensity((double) value));
            put(MakeupParam.EYE_LINER_INTENSITY, (makeup, value) -> makeup.setEyeLineIntensity((double) value));
            put(MakeupParam.BLUSHER_INTENSITY, (makeup, value) -> makeup.setBlusherIntensity((double) value));
            put(MakeupParam.PUPIL_INTENSITY, (makeup, value) -> makeup.setPupilIntensity((double) value));
            put(MakeupParam.EYE_BROW_INTENSITY, (makeup, value) -> makeup.setEyeBrowIntensity((double) value));
            put(MakeupParam.EYE_SHADOW_INTENSITY, (makeup, value) -> makeup.setEyeShadowIntensity((double) value));
            put(MakeupParam.EYELASH_INTENSITY, (makeup, value) -> makeup.setEyeLashIntensity((double) value));
            put(MakeupParam.FOUNDATION_INTENSITY, (makeup, value) -> makeup.setFoundationIntensity((double) value));
            put(MakeupParam.HIGHLIGHT_INTENSITY, (makeup, value) -> makeup.setHeightLightIntensity((double) value));
            put(MakeupParam.SHADOW_INTENSITY, (makeup, value) -> makeup.setShadowIntensity((double) value));
            /*子项妆容贴图*/
            put(MakeupParam.TEX_EYE_BROW, (makeup, value) -> makeup.setEyeBrowBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW, (makeup, value) -> makeup.setEyeShadowBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW2, (makeup, value) -> makeup.setEyeShadowBundle2(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW3, (makeup, value) -> makeup.setEyeShadowBundle3(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW4, (makeup, value) -> makeup.setEyeShadowBundle4(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_PUPIL, (makeup, value) -> makeup.setPupilBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_LASH, (makeup, value) -> makeup.setEyeLashBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_LINER, (makeup, value) -> makeup.setEyeLinerBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_BLUSHER, (makeup, value) -> makeup.setBlusherBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_BLUSHER2, (makeup, value) -> makeup.setBlusherBundle2(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_FOUNDATION, (makeup, value) -> makeup.setFoundationBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_HIGH_LIGHT, (makeup, value) -> makeup.setHighLightBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_SHADOW, (makeup, value) -> makeup.setShadowBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            /*子项妆容颜色*/
            put(MakeupParam.MAKEUP_LIP_COLOR, (makeup, value) -> makeup.setLipColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_LIP_COLOR2, (makeup, value) -> makeup.setLipColor2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_LINER_COLOR, (makeup, value) -> makeup.setEyeLinerColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_LASH_COLOR, (makeup, value) -> makeup.setEyeLashColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_BLUSHER_COLOR, (makeup, value) -> makeup.setBlusherColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_BLUSHER_COLOR2, (makeup, value) -> makeup.setBlusherColor2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_FOUNDATION_COLOR, (makeup, value) -> makeup.setFoundationColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_HIGH_LIGHT_COLOR, (makeup, value) -> makeup.setHighLightColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_SHADOW_COLOR, (makeup, value) -> makeup.setShadowColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_BROW_COLOR, (makeup, value) -> makeup.setEyeBrowColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_PUPIL_COLOR, (makeup, value) -> makeup.setPupilColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR, (makeup, value) -> makeup.setEyeShadowColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR2, (makeup, value) -> makeup.setEyeShadowColor2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR3, (makeup, value) -> makeup.setEyeShadowColor3(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR4, (makeup, value) -> makeup.setEyeShadowColor4(buildFUColorRGBData(value)));
            /* 图层混合模式 */
            put(MakeupParam.BLEND_TEX_EYE_SHADOW, (makeup, value) -> makeup.setEyeShadowTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_EYE_SHADOW2, (makeup, value) -> makeup.setEyeShadowTexBlend2((int) value));
            put(MakeupParam.BLEND_TEX_EYE_SHADOW3, (makeup, value) -> makeup.setEyeShadowTexBlend3((int) value));
            put(MakeupParam.BLEND_TEX_EYE_SHADOW4, (makeup, value) -> makeup.setEyeShadowTexBlend4((int) value));
            put(MakeupParam.BLEND_TEX_EYE_LASH, (makeup, value) -> makeup.setEyeLashTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_EYE_LINER, (makeup, value) -> makeup.setEyeLinerTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_BLUSHER, (makeup, value) -> makeup.setBlusherTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_BLUSHER2, (makeup, value) -> makeup.setBlusherTexBlend2((int) value));
            put(MakeupParam.BLEND_TEX_PUPIL, (makeup, value) -> makeup.setPupilTexBlend((int) value));
        }
    };

    public static ArrayList<String> buildCustomClasses() {
        ArrayList<String> classes = new ArrayList<>();
        classes.add(FACE_MAKEUP_TYPE_FOUNDATION);
        classes.add(FACE_MAKEUP_TYPE_LIP_STICK);
        classes.add(FACE_MAKEUP_TYPE_BLUSHER);
        classes.add(FACE_MAKEUP_TYPE_EYE_BROW);
        classes.add(FACE_MAKEUP_TYPE_EYE_SHADOW);
        classes.add(FACE_MAKEUP_TYPE_EYE_LINER);
        classes.add(FACE_MAKEUP_TYPE_EYE_LASH);
        classes.add(FACE_MAKEUP_TYPE_HIGH_LIGHT);
        classes.add(FACE_MAKEUP_TYPE_SHADOW);
        classes.add(FACE_MAKEUP_TYPE_EYE_PUPIL);
        return classes;
    }

    interface MakeupSetParam {
        /**
         * 模型属性赋值
         */
        void setValue(Makeup makeup, Object value);
    }

    /**
     * 构造颜色模型
     */
    public static FUColorRGBData buildFUColorRGBData(Object object) {
        if (object instanceof double[]) {
            double[] array = (double[]) object;
            if (array.length == 4) {
                return new FUColorRGBData(array[0] * 255, array[1] * 255, array[2] * 255, array[3] * 255);
            }
        }
        return new FUColorRGBData(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * 读取本地参数配置
     *
     * @param jsonPath String json文件路径
     * @return LinkedHashMap<String, Any>
     */
    private static LinkedHashMap<String, Object> getLocalParams(String jsonPath) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(32);
        map.put(MakeupParam.LIP_INTENSITY, 0.0);
        map.put(MakeupParam.EYE_LINER_INTENSITY, 0.0);
        map.put(MakeupParam.BLUSHER_INTENSITY, 0.0);
        map.put(MakeupParam.PUPIL_INTENSITY, 0.0);
        map.put(MakeupParam.EYE_BROW_INTENSITY, 0.0);
        map.put(MakeupParam.EYE_SHADOW_INTENSITY, 0.0);
        map.put(MakeupParam.EYELASH_INTENSITY, 0.0);
        map.put(MakeupParam.FOUNDATION_INTENSITY, 0.0);
        map.put(MakeupParam.HIGHLIGHT_INTENSITY, 0.0);
        map.put(MakeupParam.SHADOW_INTENSITY, 0.0);
        LinkedHashMap<String, Object> jsonParam = FileUtils.loadParamsFromLocal(FULivePlugin.getAppContext(), jsonPath);
        for (Map.Entry<String, Object> entry : jsonParam.entrySet()) {
            if (entry.getKey().startsWith("tex_")) {
                if (entry.getValue() instanceof String && ((String) entry.getValue()).contains(".bundle")) {
                    map.put(entry.getKey(), DemoConfig.MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + entry.getValue());
                }
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }


    // region 子妆容

    /* 粉底 */
    public static String FACE_MAKEUP_TYPE_FOUNDATION = "FOUNDATION";
    /* 口红 */
    public static String FACE_MAKEUP_TYPE_LIP_STICK = "STICK";
    /* 腮红 */
    public static String FACE_MAKEUP_TYPE_BLUSHER = "BLUSHER";
    /* 眉毛 */
    public static String FACE_MAKEUP_TYPE_EYE_BROW = "EYE_BROW";
    /* 眼影 */
    public static String FACE_MAKEUP_TYPE_EYE_SHADOW = "EYE_SHADOW";
    /* 眼线 */
    public static String FACE_MAKEUP_TYPE_EYE_LINER = "EYE_LINER";
    /* 睫毛 */
    public static String FACE_MAKEUP_TYPE_EYE_LASH = "EYE_LASH";
    /* 高光 */
    public static String FACE_MAKEUP_TYPE_HIGH_LIGHT = "HIGHLIGHT";
    /* 阴影 */
    public static String FACE_MAKEUP_TYPE_SHADOW = "SHADOW";
    /* 美瞳 */
    public static String FACE_MAKEUP_TYPE_EYE_PUPIL = "EYE_PUPIL";



    /**
     * 获取颜色值配置
     *
     * @return LinkedHashMap<String, ArrayList < DoubleArray>>
     */
    public static LinkedHashMap<String, ArrayList<double[]>> buildMakeUpColorMap() {
        LinkedHashMap<String, ArrayList<double[]>> makeupColorMap = new LinkedHashMap<>(32);
        String colorJson = FileUtils.loadStringFromLocal(FULivePlugin.getAppContext(), DemoConfig.MAKEUP_RESOURCE_COLOR_SETUP_JSON);
        if (colorJson != null && colorJson.trim().length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(colorJson);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ArrayList<double[]> colorList = new ArrayList<>(12);
                    // add additional transparent to fit ui
                    //增加透明色，兼容ColorRecycleView展示
//                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
//                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
//                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    JSONObject colorObject = jsonObject.optJSONObject(key);
                    for (int i = 1; i < 6; i++) {
                        JSONArray jsonArray = colorObject.optJSONArray("color" + i);
                        int length = jsonArray.length();
                        double[] colors = new double[length];
                        for (int j = 0; j < length; j++) {
                            colors[j] = jsonArray.optDouble(j, 0.0);
                        }
                        colorList.add(colors);
                    }
//                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
//                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
//                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    makeupColorMap.put(key, colorList);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return makeupColorMap;
    }

    /**
     * 克隆模型
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
        /*美型*/
        cloneFaceBeauty.setFaceShape(faceBeauty.getFaceShape());
        cloneFaceBeauty.setFaceShapeIntensity(faceBeauty.getFaceShapeIntensity());
        cloneFaceBeauty.setCheekThinningIntensity(faceBeauty.getCheekThinningIntensity());
        cloneFaceBeauty.setCheekVIntensity(faceBeauty.getCheekVIntensity());
        cloneFaceBeauty.setCheekNarrowIntensity(faceBeauty.getCheekNarrowIntensity());
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
        return cloneFaceBeauty;
    }
}
