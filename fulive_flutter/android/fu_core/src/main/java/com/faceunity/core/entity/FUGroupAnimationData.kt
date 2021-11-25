package com.faceunity.core.entity


/**
 * 组合动画模型
 * @property subAnimations ArrayList<FUBundleData> 子动画列表
 * @property subProps ArrayList<FUBundleData> 子道具列表
 * @constructor
 */
class FUGroupAnimationData @JvmOverloads constructor(animation: FUBundleData, name: String = animation.name,
                                                     val subAnimations: ArrayList<FUBundleData> = ArrayList(),
                                                     val subProps: ArrayList<FUBundleData> = ArrayList()
) : FUAnimationData(animation, name) {


    /**
     * 克隆
     * @return FUBundleData
     */
    override fun clone(): FUGroupAnimationData {
        val cloneData = FUGroupAnimationData(animation.clone(), name)
        subAnimations.forEach {
            cloneData.subAnimations.add(it.clone())
        }
        subProps.forEach {
            cloneData.subProps.add(it.clone())
        }
        return cloneData
    }

    /**
     * 数据比对
     * @param data FUBaseAnimationData
     * @return Boolean
     */
    override fun isEqual(data: FUAnimationData): Boolean {
        if (data !is FUGroupAnimationData) {
            return false
        }
        if (animation.path != data.animation.path || animation.name != data.animation.name) {
            return false
        }
        val subAnimationsSet = data.subAnimations.map { it.path }.toSet()
        val subPropsSet = data.subProps.map { it.path }.toSet()
        subAnimations.forEach {
            if (!subAnimationsSet.contains(it.path)) {
                return false
            }
        }
        subProps.forEach {
            if (!subPropsSet.contains(it.path)) {
                return false
            }
        }
        return true
    }


}