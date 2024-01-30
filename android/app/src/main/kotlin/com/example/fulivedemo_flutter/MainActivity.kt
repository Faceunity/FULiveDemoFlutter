package com.example.fulivedemo_flutter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.faceunity.fuliveplugin.fulive_plugin.ActivityPluginBridge
import com.faceunity.fuliveplugin.fulive_plugin.config.FaceunityKit
import com.faceunity.fuliveplugin.fulive_plugin.utils.appCtx
import io.flutter.embedding.android.FlutterActivity

class MainActivity: FlutterActivity() {

    private val needPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        mutableSetOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    } else {
        mutableSetOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appCtx = applicationContext

        /*防止首次安装点击home键重新实例化*/
        if (!this.isTaskRoot) {
            val intent = intent
            if (intent != null) {
                val action = intent.action
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) {
                    finish()
                    return
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions(needPermission)) {
                requestPermissions(needPermission.toTypedArray(), 10086)
            }
        }

        FaceunityKit.setupKit(this) {

        }
        ActivityPluginBridge.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityPluginBridge.unregister()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ActivityPluginBridge.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10086 -> {
                if ((grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED } )) {
                } else {
                    Toast.makeText(this, "请进入设置页赋予需要的权限。", Toast.LENGTH_SHORT).show()
                }
                return
            }

        }
    }
    private fun checkPermissions(permissions: Collection<String>) = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}
