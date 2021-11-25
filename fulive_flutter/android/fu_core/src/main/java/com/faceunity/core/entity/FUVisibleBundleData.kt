package com.faceunity.core.entity

/**
 * DESC：Bundle数据模型
 * @property path String 文件绝对路径（支持本地文件以及Assets文件）
 * @property visibleList List<Int>? 支持的Body可见的数组
 * @property name String 文件名称标识（根据路径自动截取）
 * @constructor
 */
class FUVisibleBundleData(
    path: String,
    val visibleList: IntArray? = null,
    name: String = getFileName(path)
) : FUBundleData(path, name) {
    /**
     * 克隆
     * @return FUBundleData
     */
    override fun clone(): FUBundleData {
        return FUVisibleBundleData(path, visibleList, name)
    }
}