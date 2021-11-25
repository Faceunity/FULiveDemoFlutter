package com.faceunity.core.entity

import java.io.File

/**
 * DESC：Bundle数据模型
 * @property path String 文件绝对路径（支持本地文件以及Assets文件）
 * @property name String 文件名称标识（根据路径自动截取）
 * @constructor
 */
open class FUBundleData @JvmOverloads constructor(
    val path: String,
    val name: String = getFileName(path)
) {
    companion object {
        fun getFileName(path: String): String {
            val fName = path.trim()
            var fileName = fName.substring(fName.lastIndexOf(File.separator) + 1)
            if (fileName.contains(".bundle")) {
                fileName = fileName.substring(0, fileName.indexOf(".bundle"))
            }
            return fileName
        }
    }

    /**
     * 克隆
     * @return FUBundleData
     */
    open fun clone(): FUBundleData {
        return FUBundleData(path, name)
    }


}