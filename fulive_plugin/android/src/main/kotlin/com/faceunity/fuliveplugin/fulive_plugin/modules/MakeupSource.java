package com.faceunity.fuliveplugin.fulive_plugin.modules;

import android.content.Context;
import com.faceunity.core.controller.makeup.MakeupParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.model.makeup.Makeup;
import com.faceunity.core.utils.FileUtils;
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityConfig;
import com.faceunity.fuliveplugin.fulive_plugin.model.FUCombinationMakeupModel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DESC：美妆数据构造
 * Created on 2021/3/28
 */
public class MakeupSource {


    //region 组合妆容

    /**
     * 构造美妆模型
     *
     * @return Makeup
     */
    public static Makeup setCombinationMakeupParams(Context context, Makeup makeupModel, FUCombinationMakeupModel model) {

        if (model.getJsonPathParams() == null) {
            model.setJsonPathParams(getLocalParams(context, model.getBundlePath()));
        }
        LinkedHashMap<String, Object> params = model.getJsonPathParams();

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
    private static LinkedHashMap<String, Object> getLocalParams(Context context, String jsonPath) {
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
        LinkedHashMap<String, Object> jsonParam = FileUtils.loadParamsFromLocal(context, jsonPath);
        for (Map.Entry<String, Object> entry : jsonParam.entrySet()) {
            if (entry.getKey().startsWith("tex_")) {
                if (entry.getValue() instanceof String && ((String) entry.getValue()).contains(".bundle")) {
                    map.put(entry.getKey(), FaceunityConfig.makeupItemBundlePath((String) entry.getValue()));
                }
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    private interface MakeupSetParam {
        /**
         * 模型属性赋值
         *
         * @param makeup Makeup
         * @param value  Object
         */
        void setValue(Makeup makeup, Object value);

    }

    /*美妆映射模型*/
    private static final HashMap<String, MakeupSetParam> makeupSetMapping = new HashMap<String, MakeupSetParam>() {
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
     * @param object Object
     * @return FUColorRGBData
     */
    private static FUColorRGBData buildFUColorRGBData(Object object) {
        if (object instanceof double[]) {
            double[] array = (double[]) object;
            if (array.length == 4) {
                return new FUColorRGBData(array[0] * 255, array[1] * 255, array[2] * 255, array[3] * 255);
            }
        }
        return new FUColorRGBData(0.0, 0.0, 0.0, 0.0);
    }
    //endregion 组合妆容


}
