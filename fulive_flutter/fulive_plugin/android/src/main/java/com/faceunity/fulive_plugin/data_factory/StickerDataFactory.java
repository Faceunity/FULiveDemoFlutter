package com.faceunity.fulive_plugin.data_factory;

import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.sticker.Sticker;
import com.faceunity.fulive_plugin.common.PluginConfig;
import com.faceunity.fulive_plugin.utils.FuDeviceUtils;
/**
 * @author benyq
 * @date 2021/12/1
 * @email 1520063035@qq.com
 */
public class StickerDataFactory {

    private final FURenderKit mFURenderKit = FURenderKit.getInstance();
    /*当前道具*/
    private Prop currentProp;

    public StickerDataFactory(int index) {
        onItemSelected(index);
    }

    public void configBiz() {
        FUAIKit.getInstance().loadAIProcessor(PluginConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
        FUAIKit.getInstance().faceProcessorSetFaceLandmarkQuality(PluginConfig.DEVICE_LEVEL);
        if (PluginConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID) {
            FUAIKit.getInstance().fuFaceProcessorSetDetectSmallFace(true);
        }
        if (currentProp != null) {
            mFURenderKit.getPropContainer().addProp(currentProp);
        }
    }

    public void dispose() {
        FUAIKit.getInstance().releaseAllAIProcessor();
        mFURenderKit.getPropContainer().removeAllProp();
        currentProp = null;
    }

    /**
     * 道具选中
     *
     * @param index
     */
    public void onItemSelected(int index) {
        String path = getStickerPath(index);
        if (path == null || path.trim().length() == 0) {
            mFURenderKit.getPropContainer().removeAllProp();
            currentProp = null;
            return;
        }
        Prop prop = new Sticker(new FUBundleData(path));
        mFURenderKit.getPropContainer().replaceProp(currentProp, prop);
        currentProp = prop;
    }

    private String getStickerPath(int index) {
        switch (index) {
            case 1:
                return "effect/normal/cat_sparks.bundle";
            case 2:
                return "effect/normal/fu_zh_fenshu.bundle";
            case 3:
                return "effect/normal/sdlr.bundle";
            case 4:
                return "effect/normal/xlong_zh_fu.bundle";
            case 5:
                return "effect/normal/newy1.bundle";
            case 6:
                return "effect/normal/redribbt.bundle";
            case 7:
                return "effect/normal/daisypig.bundle";
            case 8:
                return "effect/normal/sdlu.bundle";
            default:
                return null;
        }
    }




}
