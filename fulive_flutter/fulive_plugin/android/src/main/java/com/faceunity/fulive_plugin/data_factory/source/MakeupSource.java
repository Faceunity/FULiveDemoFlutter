package com.faceunity.fulive_plugin.data_factory.source;

import com.faceunity.core.controller.makeup.MakeupParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.core.model.makeup.Makeup;
import com.faceunity.core.utils.FileUtils;
import com.faceunity.fulive_plugin.common.PluginConfig;
import com.faceunity.fulive_plugin.FULivePlugin;
import com.faceunity.fulive_plugin.entity.bean.MakeupCombinationBean;
import com.faceunity.fulive_plugin.entity.bean.MakeupCombinationBean.TypeEnum;
import com.faceunity.fulive_plugin.utils.FuDeviceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DESC：美妆数据构造
 * Created on 2021/3/28
 */
public class MakeupSource {


    //region 组合妆容

    /**
     * 构造美妆组合妆容配置
     *
     * @return ArrayList<MakeupCombinationBean>
     */
    public static ArrayList<MakeupCombinationBean> buildCombinations() {
        ArrayList<MakeupCombinationBean> combinations = new ArrayList<MakeupCombinationBean>();
        String jsonDir = PluginConfig.MAKEUP_RESOURCE_JSON_DIR;
        String bundleDir = PluginConfig.MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR;
        combinations.add(new MakeupCombinationBean("origin", TypeEnum.TYPE_NONE, null, "", FaceBeautyFilterEnum.ZIRAN_2, 1.0, 0.0));
        combinations.add(new MakeupCombinationBean("diadiatu", TypeEnum.TYPE_THEME_MAIN, bundleDir + "diadiatu.bundle", jsonDir + "diadiatu.json", FaceBeautyFilterEnum.ORIGIN, 0.68));
        combinations.add(new MakeupCombinationBean("dongling", TypeEnum.TYPE_THEME_MAIN, bundleDir + "dongling.bundle", jsonDir + "dongling.json", FaceBeautyFilterEnum.ORIGIN, 0.68));
        combinations.add(new MakeupCombinationBean("guofeng", TypeEnum.TYPE_THEME_MAIN, bundleDir + "guofeng.bundle", jsonDir + "guofeng.json", FaceBeautyFilterEnum.ORIGIN, 0.6));
        combinations.add(new MakeupCombinationBean("hunxie", TypeEnum.TYPE_THEME_MAIN, bundleDir + "hunxie.bundle", jsonDir + "hunxie.json", FaceBeautyFilterEnum.ORIGIN, 0.6));
        combinations.add(new MakeupCombinationBean("jianling", TypeEnum.TYPE_THEME_SUB, bundleDir + "jianling.bundle", jsonDir + "jianling.json", FaceBeautyFilterEnum.ZHIGANHUI_1));
        combinations.add(new MakeupCombinationBean("nuandong", TypeEnum.TYPE_THEME_SUB, bundleDir + "nuandong.bundle", jsonDir + "nuandong.json", FaceBeautyFilterEnum.ZHIGANHUI_2));
        combinations.add(new MakeupCombinationBean("hongfeng", TypeEnum.TYPE_THEME_SUB, bundleDir + "hongfeng.bundle", jsonDir + "hongfeng.json", FaceBeautyFilterEnum.ZHIGANHUI_3));
        combinations.add(new MakeupCombinationBean("rose", TypeEnum.TYPE_THEME_SUB, bundleDir + "rose.bundle", jsonDir + "rose.json", FaceBeautyFilterEnum.ZHIGANHUI_2));
        combinations.add(new MakeupCombinationBean("shaonv", TypeEnum.TYPE_THEME_SUB, bundleDir + "shaonv.bundle", jsonDir + "shaonv.json", FaceBeautyFilterEnum.ZHIGANHUI_4));
        combinations.add(new MakeupCombinationBean("ziyun", TypeEnum.TYPE_THEME_SUB, bundleDir + "ziyun.bundle", jsonDir + "ziyun.json", FaceBeautyFilterEnum.ZHIGANHUI_1));
        combinations.add(new MakeupCombinationBean("yanshimao", TypeEnum.TYPE_THEME_SUB, bundleDir + "yanshimao.bundle", jsonDir + "yanshimao.json", FaceBeautyFilterEnum.ZHIGANHUI_5));
        combinations.add(new MakeupCombinationBean("renyu", TypeEnum.TYPE_THEME_SUB, bundleDir + "renyu.bundle", jsonDir + "renyu.json", FaceBeautyFilterEnum.ZHIGANHUI_1));
        combinations.add(new MakeupCombinationBean("chuqiu", TypeEnum.TYPE_THEME_SUB, bundleDir + "chuqiu.bundle", jsonDir + "chuqiu.json", FaceBeautyFilterEnum.ZHIGANHUI_6));
        combinations.add(new MakeupCombinationBean("qianzhihe", TypeEnum.TYPE_THEME_SUB, bundleDir + "qianzhihe.bundle", jsonDir + "qianzhihe.json", FaceBeautyFilterEnum.ZHIGANHUI_2));
        combinations.add(new MakeupCombinationBean("chaomo", TypeEnum.TYPE_THEME_SUB, bundleDir + "chaomo.bundle", jsonDir + "chaomo.json", FaceBeautyFilterEnum.ZHIGANHUI_7));
        combinations.add(new MakeupCombinationBean("chuju", TypeEnum.TYPE_THEME_SUB, bundleDir + "chuju.bundle", jsonDir + "chuju.json", FaceBeautyFilterEnum.ZHIGANHUI_8));
        combinations.add(new MakeupCombinationBean("gangfeng", TypeEnum.TYPE_THEME_SUB, bundleDir + "gangfeng.bundle", jsonDir + "gangfeng.json", FaceBeautyFilterEnum.ZIRAN_8));
        combinations.add(new MakeupCombinationBean("xinggan", TypeEnum.TYPE_DAILY, bundleDir + "xinggan.bundle", jsonDir + "xinggan.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("tianmei", TypeEnum.TYPE_DAILY, bundleDir + "tianmei.bundle", jsonDir + "tianmei.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("linjia", TypeEnum.TYPE_DAILY, bundleDir + "linjia.bundle", jsonDir + "linjia.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("oumei", TypeEnum.TYPE_DAILY, bundleDir + "oumei.bundle", jsonDir + "oumei.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("wumei", TypeEnum.TYPE_DAILY, bundleDir + "wumei.bundle", jsonDir + "wumei.json", FaceBeautyFilterEnum.ZIRAN_4));
        return combinations;
    }


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

    /**
     * 构造美妆模型
     *
     * @return
     */
    public static Makeup getMakeupModel(MakeupCombinationBean bean) {
        Makeup makeupModel;
        if (TypeEnum.TYPE_THEME_MAIN == bean.getType() && bean.getBundlePath() != null && bean.getBundlePath().trim().length() > 0) {
            makeupModel = new Makeup(new FUBundleData(bean.getBundlePath()));
            //新的组合妆容设置滤镜scale
            makeupModel.setCurrentFilterScale(bean.getFilterScale());
        } else {
            makeupModel = new Makeup(new FUBundleData(PluginConfig.BUNDLE_FACE_MAKEUP));
        }

        if (bean.getKey().equals("origin")) {
            return makeupModel;
        }

        if ((TypeEnum.TYPE_THEME_SUB == bean.getType() || TypeEnum.TYPE_DAILY == bean.getType()) && bean.getBundlePath() != null && bean.getBundlePath().trim().length() > 0)
            makeupModel.setCombinedConfig(new FUBundleData(bean.getBundlePath()));

        makeupModel.setMakeupIntensity(bean.getIntensity());
        makeupModel.setMachineLevel(PluginConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID);//更新设备等级去设置是否开启人脸遮挡

        if (bean.getJsonPathParams() == null) {
            bean.setJsonPathParams(getLocalParams(bean.getJsonPath()));
        }
        LinkedHashMap<String, Object> params = bean.getJsonPathParams();

        //支持自定义，所以需要知道选中了妆容的哪一些项
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


    /**
     * 读取本地参数配置
     *
     * @param jsonPath String json文件路径
     * @return LinkedHashMap<String, Any>
     */
    private static LinkedHashMap<String, Object> getLocalParams(String jsonPath) {
        LinkedHashMap<String, Object> map = new LinkedHashMap(32);
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
        LinkedHashMap<String, Object> jsonParam = FileUtils.INSTANCE.loadParamsFromLocal(FULivePlugin.appContext, jsonPath);
        for (Map.Entry<String, Object> entry : jsonParam.entrySet()) {
            if (entry.getKey().startsWith("tex_")) {
                if (entry.getValue() instanceof String && ((String) entry.getValue()).contains(".bundle")) {
                    map.put(entry.getKey(), PluginConfig.MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + entry.getValue());
                }
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    interface MakeupSetParam {
        /**
         * 模型属性赋值
         *
         * @param makeup
         * @param value
         */
        void setValue(Makeup makeup, Object value);

    }

    /*美妆映射模型*/
    public static HashMap<String, MakeupSetParam> makeupSetMapping = new HashMap<String, MakeupSetParam>() {
        {
            put(MakeupParam.LIP_TYPE, (makeup, value) -> makeup.setLipType((int) value));
            put(MakeupParam.IS_TWO_COLOR, (makeup, value) -> makeup.setEnableTwoLipColor((int) value == 1));
            put(MakeupParam.MAKEUP_LIP_HIGH_LIGHT_ENABLE, (makeup, value) -> makeup.setLipHighLightEnable((int) value == 1));
            put(MakeupParam.MAKEUP_LIP_HIGH_LIGHT_STRENGTH, (makeup, value) -> makeup.setLipHighLightStrength((double) value));
            put(MakeupParam.BROW_WARP, (makeup, value) -> makeup.setEnableBrowWarp((double) value == 1.0));
            put(MakeupParam.MAKEUP_MACHINE_LEVEL, (makeup, value) -> makeup.setMachineLevel((double) value == 1.0));
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
            put(MakeupParam.TEX_LIP, (makeup, value) -> makeup.setLipBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
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
            put(MakeupParam.MAKEUP_LIP_COLOR_V2, (makeup, value) -> makeup.setLipColorV2(buildFUColorRGBData(value)));
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


    /**
     * 构造颜色模型
     *
     * @param object
     * @return
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
    //endregion 组合妆容

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


    //endregion 子妆容


    //region 其他

    /**
     * 获取颜色值配置
     *
     * @return LinkedHashMap<String, ArrayList < DoubleArray>>
     */
    public static LinkedHashMap<String, ArrayList<double[]>> buildMakeUpColorMap() {
        LinkedHashMap<String, ArrayList<double[]>> makeupColorMap = new LinkedHashMap<>(32);
        String colorJson = FileUtils.INSTANCE.loadStringFromLocal(FULivePlugin.appContext, PluginConfig.MAKEUP_RESOURCE_COLOR_SETUP_JSON);
        if (colorJson != null && colorJson.trim().length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(colorJson);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ArrayList<double[]> colorList = new ArrayList(12);
                    // add additional transparent to fit ui
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
                    makeupColorMap.put(key, colorList);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return makeupColorMap;
    }


    //endregion

    /**
     * 获取日常妆的选中项
     *
     * @param key
     * @return
     */
    public static HashMap<String, Integer> getDailyCombinationSelectItem(String key) {
        HashMap<String, Integer> mCustomIndexMap = new HashMap<>();
        if ("xinggan".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 1);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 2);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 1);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 2);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 1);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 4);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 2);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 1);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("tianmei".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 2);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 4);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 4);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 1);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 2);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 2);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 1);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 1);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("linjia".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 3);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 1);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 2);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 1);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 6);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 1);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 0);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 0);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("oumei".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 2);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 2);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 1);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 4);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 5);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 5);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 2);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 1);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("wumei".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 4);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 3);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 1);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 2);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 3);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 3);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 1);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 0);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        }
        return mCustomIndexMap;
    }

    /**
     * 获取日常妆选中项的强度
     *
     * @param key
     * @return
     */
    public static HashMap<String, Double> getDailyCombinationSelectItemValue(String key) {
        HashMap<String, Double> mCustomIntensityMap = new HashMap<>();
        if ("xinggan".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_1", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.800000011920929);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0.4000000059604645);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 0.8999999761581421);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_1", 0.6000000238418579);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_4", 0.699999988079071);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 1.0);//高光
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 1.0);//阴影
        } else if ("tianmei".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_2", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.5);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_4", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_4", 0.5);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 0.699999988079071);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_2", 0.5);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_2", 0.5);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 1.0);//高光
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 1.0);//阴影
        } else if ("linjia".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_3", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.6000000238418579);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_1", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_2", 0.4);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 0.8999999761581421);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_6", 0.699999988079071);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_1", 0.699999988079071);//睫毛
        } else if ("oumei".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_2", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.8600000143051148);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0.5);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_4", 0.800000011920929);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_5", 0.4000000059604645);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_5", 0.6000000238418579);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 1.0);//高光
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 1.0);//阴影
        } else if ("wumei".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_4", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.699999988079071);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_3", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0.6000000238418579);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 0.699999988079071);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_3", 0.6000000238418579);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_3", 0.6000000238418579);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 1.0);//高光
        }
        return mCustomIntensityMap;
    }

    /**
     * 获取日常妆选中项的颜色
     *
     * @param key
     * @return
     */
    public static HashMap<String, Integer> getDailyCombinationSelectItemColor(String key) {
        HashMap<String, Integer> mCustomColorIndexMap = new HashMap<>();
        if ("xinggan".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 0);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 0);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_1", 0);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_4", 0);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 0);//高光
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 0);//阴影
        } else if ("tianmei".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 1);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_4", 1);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_4", 0);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 0);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_2", 1);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_2", 0);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 1);//高光
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 0);//阴影
        } else if ("linjia".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 2);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_1", 2);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_2", 0);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 0);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_6", 2);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_1", 0);//睫毛
        } else if ("oumei".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 3);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 3);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_4", 0);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_5", 3);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_5", 0);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 3);//高光
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 3);//阴影
        } else if ("wumei".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 4);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_3", 4);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 1);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_3", 2);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_3", 0);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 4);//高光
        }
        return mCustomColorIndexMap;
    }
}
