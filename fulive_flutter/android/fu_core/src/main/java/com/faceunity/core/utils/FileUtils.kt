package com.faceunity.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.util.Log
import com.faceunity.core.entity.TextureImage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * DESC：
 * Created on 2020/12/7
 *
 */
object FileUtils {

    private const val TAG = "KIT_FileUtils"


    /**
     * 根据路径获取InputStream
     * @param context Context
     * @param path String
     * @return InputStream?
     */
    @JvmStatic
    private fun readInputByPath(context: Context, path: String): InputStream? {
        if (path.isBlank()) return null
        var `is`: InputStream? = null
        try {
            `is` = context.assets.open(path)
        } catch (e: IOException) {
            try {
                `is` = FileInputStream(path)
            } catch (e: IOException) {

            }
        }
        return `is`
    }


    /**
     * 根据路径加载Bundle文件
     * @param context Context
     * @param path String  assets或者本地存储路径
     * @return ByteArray?
     */
    @JvmStatic
    fun loadBundleFromLocal(context: Context, path: String): ByteArray? {
        val inputStream = readInputByPath(context, path)
        inputStream?.let {
            try {
                val buffer = ByteArray(it.available())
                it.read(buffer)

                return buffer
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }
        FULogger.d(TAG, "loadBundleFromLocal failed path:$path")
        return null
    }


    /**
     * 加载本地Assets
     * @param context Context
     * @param path String
     * @return String?
     */
    @JvmStatic
    fun loadStringFromLocal(context: Context, path: String): String? {
        val inputStream = readInputByPath(context, path)
        var content: String? = null
        inputStream?.let {
            try {
                val bytes = ByteArray(it.available())
                it.read(bytes)
                content = String(bytes)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }
        FULogger.d(TAG, "loadStringFromLocal failed path:$path")
        return content
    }


    /**
     * 加载Assets下图片
     * @param context Context
     * @param path String
     * @return Bitmap
     */
    @JvmStatic
    fun loadBitmapFromLocal(context: Context, path: String): Bitmap? {
        val inputStream = readInputByPath(context, path)
        var bitmap: Bitmap? = null
        inputStream?.let {
            try {
                val drawable = Drawable.createFromStream(it, null) as BitmapDrawable
                bitmap = drawable.bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                FULogger.d(TAG, "loadBitmapFromLocal failed path:$path")
            } finally {
                it.close()
            }
        }
        return bitmap
    }

    /**
     * 加载本地json文件键值对返回
     * @param context Context
     * @param jsonPath String
     * @return LinkedHashMap<String, Any>
     */
    @JvmStatic
    fun loadParamsFromLocal(context: Context, jsonPath: String): LinkedHashMap<String, Any> {
        val paramMap = LinkedHashMap<String, Any>()
        try {
            val content = loadStringFromLocal(context, jsonPath)
            content?.let {
                val jsonObject = JSONObject(content)
                val keys: Iterator<String> = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val obj = jsonObject.opt(key)
                    if (obj is String || obj is Double || obj is Int) {
                        paramMap[key] = obj
                    } else if (obj is JSONArray) {
                        val length: Int = obj.length()
                        val value = DoubleArray(length)
                        for (i in 0 until length) {
                            value[i] = obj.optDouble(i)
                        }
                        paramMap[key] = value
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return paramMap
    }

    /**
     * 读取 RGBA 颜色数据
     *
     * @param context
     * @param path  path
     * @return
     */
    @JvmStatic
    fun loadRgbaColorFromLocal(context: Context, path: String): DoubleArray? {
        val inputStream = readInputByPath(context, path)
        var colors: DoubleArray? = null
        inputStream?.let {
            try {
                val bytes = ByteArray(it.available())
                it.read(bytes)
                val jsonObject = JSONObject(String(bytes))
                val jsonArray = jsonObject.optJSONArray("rgba") as JSONArray
                colors = DoubleArray(jsonArray.length())
                var i = 0
                val length = jsonArray.length()
                while (i < length) {
                    colors!![i] = jsonArray.optDouble(i)
                    i++
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }
        FULogger.d(TAG, "loadRgbaColorFromLocal  path:$path   colors:${colors?.contentToString()}")
        return colors
    }

    /**
     * 加载贴图资源，返回图像的字节数组和宽高。
     *
     * @param context
     * @param path
     * @return TextureImage: width, height and bytes
     */
    @JvmStatic
    fun loadTextureImageFromLocal(context: Context, path: String): TextureImage? {
        val inputStream = readInputByPath(context, path)
        var bitmap: Bitmap? = null
        inputStream?.let {
            try {
                bitmap = BitmapFactory.decodeStream(it)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }
        bitmap?.let {
            val width = it.width
            val height = it.height
            val bitmapBytes = loadRgbaByteFromBitmap(it)
            return TextureImage(width, height, bitmapBytes)
        }
        FULogger.d(TAG, "loadTextureImageFromLocal failed path:$path")
        return null
    }


    /**
     * 获取 Bitmap 的 RGBA 字节数组
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun loadRgbaByteFromBitmap(bitmap: Bitmap): ByteArray {
        val bytes = ByteArray(bitmap.byteCount)
        val rgbaBuffer = ByteBuffer.wrap(bytes)
        bitmap.copyPixelsToBuffer(rgbaBuffer)
        return bytes
    }

    /**
     * 将Assets文件拷贝到应用作用域存储
     * @param context Context
     * @param assetsPath String
     * @param fileName String
     */
    @JvmStatic
    fun copyAssetsToExternalFilesDir(context: Context, assetsPath: String, fileName: String, dir: String = "assets"): String {
        val fileDir = File(getExternalFileDir(context)!!.path + "/" + dir)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file = File("$fileDir/$fileName")
        if (file.exists()) {
            return file.absolutePath
        }
        val inputStream = context.assets.open(assetsPath)
        val fos = FileOutputStream(file)
        val bis = BufferedInputStream(inputStream)
        val bos = BufferedOutputStream(fos)
        val byteArray = ByteArray(1024)
        var bytes = bis.read(byteArray)
        while (bytes > 0) {
            bos.write(byteArray, 0, bytes)
            bos.flush()
            bytes = bis.read(byteArray)
        }
        bos.close()
        fos.close()
        Log.d("FileUtils", "Copy $fileName into $fileDir succeeded.")
        return file.absolutePath
    }

    //*****************************外部文件存储************************************//

    /**
     * 获取视频缓存文件
     *
     * @param context Context
     * @return File
     */
    @JvmStatic
    fun getCacheVideoFile(context: Context): File? {
        val fileDir = File(getExternalFileDir(context).path + File.separator + "video")
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file = File(fileDir, getCurrentVideoFileName())
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    /**
     * 构造视频文件名称
     *
     * @return
     */
    private fun getCurrentVideoFileName(): String? {
        return getDateTimeString() + ".mp4"
    }

    /**
     * 获取当前时间日期
     *
     * @return
     */
    private fun getDateTimeString(): String {
        val now = GregorianCalendar()
        return SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(now.time)
    }


    /**
     * 应用外部文件目录
     * @return
     */
    private fun getExternalFileDir(context: Context): File {
        var fileDir = context.getExternalFilesDir(null)
        if (fileDir == null) {
            fileDir = context.filesDir
        }
        return fileDir
    }


    /**
     * load本地图片
     *
     * @param path
     * @param screenWidth
     * @return
     */
    @JvmStatic
    fun loadBitmapFromExternal(path: String, screenWidth: Int): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opt)
        val picWidth = opt.outWidth
        val picHeight = opt.outHeight
        opt.inSampleSize = 1
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picHeight > screenWidth) {
                opt.inSampleSize = picHeight / screenWidth
            }
        } else {
            if (picWidth > screenWidth) {
                opt.inSampleSize = picWidth / screenWidth
            }
        }
        opt.inJustDecodeBounds = false
        var bitmap = BitmapFactory.decodeFile(path, opt)
        val orientation = getPhotoOrientation(path)
        bitmap = BitmapUtils.rotateBitmap(bitmap, orientation)
        return bitmap
    }


    /**
     * load本地图片
     * @param path String
     * @param screenWidth Int
     * @param screenHeight Int
     * @return Bitmap
     */
    @JvmStatic
    fun loadBitmapFromExternal(path: String, screenWidth: Int, screenHeight: Int): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opt)
        val picWidth = opt.outWidth
        val picHeight = opt.outHeight
        var inSampleSize = 1
        // 根据屏的大小和图片大小计算出缩放比例
        if (picHeight > screenHeight || picWidth > screenWidth) {
            val halfHeight: Int = picHeight / 2
            val halfWidth: Int = picWidth / 2
            while (halfHeight / inSampleSize >= screenHeight && halfWidth / inSampleSize >= screenWidth) {
                inSampleSize *= 2
            }
        }
        opt.inSampleSize = inSampleSize
        opt.inJustDecodeBounds = false
        var bitmap = BitmapFactory.decodeFile(path, opt)
        val orientation = getPhotoOrientation(path)
        bitmap = BitmapUtils.rotateBitmap(bitmap, orientation)
        return bitmap
    }


    /**
     * 获取图片的方向
     *
     * @param path
     * @return
     */
    fun getPhotoOrientation(path: String): Int {
        var orientation = 0
        var tagOrientation = 0
        try {
            tagOrientation = ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        when (tagOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                orientation = 90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                orientation = 180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                orientation = 270
            }
        }
        return orientation
    }


    /**
     * 将Assets文件拷贝到应用作用域存储
     *
     * @param context    Context
     * @param assetsPath String
     * @param fileName   String
     */
    @JvmStatic
    fun copyAssetsToExternalFilesDir(context: Context, assetsPath: String, fileName: String): String? {
        val fileDir = File(getExternalFileDir(context).path + File.separator + "assets")
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file = File(fileDir, fileName)
        if (file.exists()) {
            return file.absolutePath
        }
        try {
            val inputStream = context.assets.open(assetsPath)
            val fos = FileOutputStream(file)
            val bis = BufferedInputStream(inputStream)
            val bos = BufferedOutputStream(fos)
            val byteArray = ByteArray(1024)
            var bytes = bis.read(byteArray)
            while (bytes > 0) {
                bos.write(byteArray, 0, bytes)
                bos.flush()
                bytes = bis.read(byteArray)
            }
            bos.close()
            fos.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}