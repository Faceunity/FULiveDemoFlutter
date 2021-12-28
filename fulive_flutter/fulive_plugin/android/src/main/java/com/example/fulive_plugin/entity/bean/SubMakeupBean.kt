package com.example.fulive_plugin.entity.bean

/**
 *
 * @author benyq
 * @date 2021/12/3
 * @email 1520063035@qq.com
 * 子妆
 */
data class SubMakeupBean(var title: String, var bundleIndex: Int, var colorIndex: Int, var value: String) {
    constructor(title: String = ""): this(title, 0, 0, "0.0")
}