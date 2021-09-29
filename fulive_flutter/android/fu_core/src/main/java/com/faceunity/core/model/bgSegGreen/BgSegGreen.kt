package com.faceunity.core.model.bgSegGreen

import com.faceunity.core.controller.bgSegGreen.BgSegGreenParam
import com.faceunity.core.controller.bgSegGreen.BgSegGreenRemark
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUColorRGBData
import com.faceunity.core.entity.FUCoordinate2DData
import com.faceunity.core.entity.FUFeaturesData
import com.faceunity.core.model.BaseSingleModel
import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.DecimalUtils


/**
 *
 * DESC：绿幕抠像
 * Created on 2021/1/30
 *
 */
class BgSegGreen(controlBundle: FUBundleData) : BaseSingleModel(controlBundle) {

    override fun getModelController() = mBgSegGreenController

    private val mBgSegGreenController = FURenderBridge.getInstance().mBgSegGreenController


    /*  背景图片的颜色是否是bgra */
    var isBGRA = false   //true表示是  false表示不是
        set(value) {
            field = value
            updateAttributes(BgSegGreenParam.IS_BGRA, if (value) 1.0 else 0.0)
        }


    /* 选取的颜色RGB */
    var colorRGB: FUColorRGBData = FUColorRGBData(0.0, 255.0, 0.0)   //默认值为[0,255,0],取值范围[0-255,0-255,0-255],选取的颜色RGB*/
        set(value) {
            field = value
            updateAttributes(BgSegGreenParam.RGB_COLOR, value.toColorArray())
        }

    /* 相似度 */
    var similarity = 0.518  //取值范围0.0-1.0，相似度：色度最大容差，色度最大容差值越大，更多幕景被抠除  默认值为0.518
        set(value) {
            field = value
            updateAttributes(BgSegGreenParam.SIMILARITY, value)
        }

    /* 平滑度 */
    var smoothness = 0.22  //取值范围0.0-1.0，平滑：色度最小限差，值越大，更多幕景被扣除   默认值为0.22
        set(value) {
            field = value
            updateAttributes(BgSegGreenParam.SMOOTHNESS, value)
        }

    /* 透明度 */
    var transparency = 0.0  //取值范围0.0-1.0，透明度：图像前后景透明度过度，值越大，两者边缘处透明过度更平滑
        set(value) {
            field = value
            updateAttributes(BgSegGreenParam.TRANSPARENCY, value)
        }

    /* 是否启用安全区域纹理 */
    var isUseTemplate = 0.0  //取值范围0.0-1.0，透明度：图像前后景透明度过度，值越大，两者边缘处透明过度更平滑
        set(value) {
            field = value
            updateAttributes(BgSegGreenParam.IS_USE_TEMPLATE, value)
        }

    /* 中心坐标 */
    var centerPoint = FUCoordinate2DData(0.5, 0.5)  //取值范围0.0-1.0, [0.5,0.5]表示中心点坐标
        set(value) {
            field = value
            updateCustomUnit("coordinate") { mBgSegGreenController.setScale(getCurrentSign(), zoom, value.positionX, value.positionY) }
        }

    /* 缩放程度 */
    var zoom = 1.0   //范围[0.25f~4.0]
        set(value) {
            field = value
            updateCustomUnit("coordinate") { mBgSegGreenController.setScale(getCurrentSign(), value, centerPoint.positionX, centerPoint.positionY) }
        }

    /* 自定义安全区域纹理 */
    fun createSafeAreaSegment(rgba: ByteArray, width: Int, height: Int) {
        isUseTemplate = 1.0
        updateCustomUnit("createSafeAreaSegment") { mBgSegGreenController.createSafeAreaSegment(getCurrentSign(), rgba, width, height) }
    }

    /*移除自定义安全区域纹理*/
    fun removeSafeAreaSegment() {
        isUseTemplate = 0.0
        updateCustomUnit("removeSafeAreaSegment") { mBgSegGreenController.removeSafeAreaSegment(getCurrentSign()) }
    }

    /* 自定义背景 */
    fun createBgSegment(rgba: ByteArray, width: Int, height: Int) {
        updateCustomUnit("createBgSegment") { mBgSegGreenController.createBgSegment(getCurrentSign(), rgba, width, height) }
    }

    /*移除自定义背景*/
    fun removeBgSegment() {
        updateCustomUnit("removeBgSegment") { mBgSegGreenController.removeBgSegment(getCurrentSign()) }
    }

    override fun buildParams(): java.util.LinkedHashMap<String, Any> {
        val params = LinkedHashMap<String, Any>()
        params[BgSegGreenParam.RGB_COLOR] = colorRGB.toColorArray()
        params[BgSegGreenParam.SIMILARITY] = similarity
        params[BgSegGreenParam.SMOOTHNESS] = smoothness
        params[BgSegGreenParam.TRANSPARENCY] = transparency
        return params
    }

    override fun buildFUFeaturesData(): FUFeaturesData = FUFeaturesData(
        controlBundle, buildParams(),
        enable, BgSegGreenRemark(zoom, centerPoint.positionX, centerPoint.positionY)
    )


}