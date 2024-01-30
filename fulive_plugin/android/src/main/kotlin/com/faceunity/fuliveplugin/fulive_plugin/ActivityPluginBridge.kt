package com.faceunity.fuliveplugin.fulive_plugin

import android.app.Activity
import android.content.Intent
import com.faceunity.fuliveplugin.fulive_plugin.utils.FileUtils
import java.lang.ref.WeakReference

/**
 *
 * @author benyq
 * @date 12/19/2023
 * 沟通Activity和Plugin的桥接， 用于访问图片和视频Activity
 */
object ActivityPluginBridge {
    private const val TYPE = "type"
    private const val REQUEST_CODE_PHOTO = 1000
    private const val REQUEST_CODE_VIDEO = 1001

    private var activityHolder: WeakReference<Activity>? = null
    private var currentAction: ((Boolean, String)->Unit)? = null

    fun register(activity: Activity) {
        activityHolder = WeakReference(activity)
    }

    fun unregister() {
        activityHolder?.clear()
        activityHolder = null
    }


    /**
     * 选中图片
     *
     * @param activity Activity
     */
    fun pickImageFile(action: (Boolean, String)->Unit) {
        activityHolder?.get()?.let {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            it.startActivityForResult(intent, REQUEST_CODE_PHOTO)
            currentAction = action
        } ?: action(false, "")
    }

    /**
     * 选中视频
     *
     * @param activity Activity
     * 回调可参考下方
     */
    fun pickVideoFile(action: (Boolean, String)->Unit) {
        activityHolder?.get()?.let {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("video/*")
            it.startActivityForResult(intent, REQUEST_CODE_VIDEO)
            currentAction = action
        } ?: action(false, "")
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val action = currentAction
        currentAction = null
        if (resultCode!= Activity.RESULT_OK || data == null) {
            action?.invoke(false, "")
            return
        }
        val activity = activityHolder?.get() ?: let {
            action?.invoke(false, "")
            return
        }
        val uri = data.data
        val path: String = FileUtils.getFilePathByUri(activity, uri)
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (!FileUtils.checkIsImage(path)) {
                action?.invoke(false, "")
            }else {
                action?.invoke(true, path)
            }
        } else if (requestCode == REQUEST_CODE_VIDEO) {
            if (!FileUtils.checkIsVideo(activity, path)) {
                action?.invoke(false, "")
            }else {
                action?.invoke(true, path)
            }
        }
    }

}