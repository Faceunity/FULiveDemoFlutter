package com.example.fulive_plugin.entity;

import android.util.Log;

import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.sticker.Sticker;

/**
 * @author benyq
 * @date 2021/12/1
 * @email 1520063035@qq.com
 */
public class StickerDataFactory {

    private static FURenderKit sFURenderKit = FURenderKit.getInstance();

    private static int currentPropIndex = 0;
    private static Prop currentProp = null;
    
    public static void onItemSelected(int index) {
        currentPropIndex = index;
        String path = getStickerPath(index);
        if (path == null || path.trim().length() == 0) {
            sFURenderKit.getPropContainer().removeAllProp();
            currentProp = null;
            return;
        }
        Prop prop = new Sticker(new FUBundleData(path));
        sFURenderKit.getPropContainer().replaceProp(currentProp, prop);
        currentProp = prop;
    }

    public static void configBiz() {
        FUAIKit.getInstance().setMaxFaces(4);
        sFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        onItemSelected(currentPropIndex);
    }
    
    public static void release() {
        currentPropIndex = 0;
        currentProp = null;
        sFURenderKit.getPropContainer().removeAllProp();
    }

    private static String getStickerPath(int index) {
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
