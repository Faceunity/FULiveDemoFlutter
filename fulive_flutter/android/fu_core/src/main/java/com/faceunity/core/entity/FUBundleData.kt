package com.faceunity.core.entity

import java.io.File

/**
 * DESC：Bundle数据模型
 * @property path String 文件绝对路径（支持本地文件以及Assets文件）
 * @property supportScenes List<String>? 支持的场景白名单，默认全支持（暂留）
 * @property name String 文件名称标识（根据路径自动截取）
 * @constructor
 */
data class FUBundleData @JvmOverloads constructor(val path: String, val supportScenes: Any? = null, val name: String = getFileName(path)) {
    companion object {
        private fun getFileName(path: String): String {
            val fName = path.trim()
            var fileName = fName.substring(fName.lastIndexOf(File.separator) + 1)
            if (fileName.contains(".bundle")) {
                fileName = fileName.substring(0, fileName.indexOf(".bundle"))
            }
            return fileName
        }
    }
}