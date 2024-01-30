package com.faceunity.fuliveplugin.fulive_plugin.utils

/**
 *
 * @author benyq
 * @date 1/8/2024
 *
 */

fun Boolean?.ifTrue(block: ()->Unit) {
    if (this == true) {
        block()
    }
}