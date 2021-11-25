package com.faceunity.core.entity

/**
 * 单动画模型
 * @property animation FUBundleData 动画道具路径(文件绝对路径)
 * @constructor
 */
open class FUAnimationData @JvmOverloads constructor(val animation: FUBundleData, val name: String = animation.name) {

    /**
     * 克隆
     * @return FUBundleData
     */
    open fun clone(): FUAnimationData {
        return FUAnimationData(animation.clone())
    }

    /**
     * 数据比对
     * @param data FUBaseAnimationData
     * @return Boolean
     */
    open fun isEqual(data: FUAnimationData): Boolean {
        return data.animation.path == animation.path && name == data.name
    }

}