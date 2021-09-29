package com.faceunity.core.bundle

import com.faceunity.core.faceunity.FURenderConfig
import com.faceunity.core.faceunity.FURenderManager
import com.faceunity.core.support.SDKController
import com.faceunity.core.utils.FULogger
import com.faceunity.core.utils.FileUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 *
 * DESC：
 * Created on 2021/2/8
 *
 */
class BundleManager private constructor() {

    companion object {
        const val TAG = "KIT_BundleManager"

        @Volatile
        private var INSTANCE: BundleManager? = null

        @JvmStatic
        internal fun getInstance(): BundleManager {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = BundleManager()
                    }
                }
            }
            return INSTANCE!!
        }
    }


    //region 渲染道具对应句柄管理
    /*  绑定句柄变更标识  */
    private var renderBundleUpdateFlag = false

    /*当前渲染绑定道具数组*/
    internal val renderBindBundles: IntArray
        get() = if (!renderBundleUpdateFlag) {
            _renderBindBundles
        } else {
            synchronized(listLock) {
                renderBundleUpdateFlag = false
                _renderBindBundles = renderBundlesList.toIntArray()
            }
            _renderBindBundles
        }

    private var _renderBindBundles = IntArray(0)


    private var renderBundlesList = LinkedList<Int>()

    private val listLock = Any()


    private fun renderBundlesAdd(handle: Int) {
        synchronized(listLock) {
            if (!renderBundlesList.contains(handle)) {
                renderBundlesList.add(handle)
                renderBundleUpdateFlag = true
            }
        }
    }

    private fun renderBundlesAdd(index: Int, handle: Int) {
        synchronized(listLock) {
            if (!renderBundlesList.contains(handle)) {
                renderBundlesList.add(index, handle)
                renderBundleUpdateFlag = true
            }
        }
    }

    private fun renderBundlesRemove(handle: Int) {
        synchronized(listLock) {
            if (renderBundlesList.contains(handle)) {
                renderBundlesList.remove(handle)
                renderBundleUpdateFlag = true
            }
        }
    }

    private fun renderBundlesClear() {
        synchronized(listLock) {
            renderBundlesList.clear()
            renderBundleUpdateFlag = true
        }
    }


    //endregion渲染道具对应句柄管理

    //region 控制句柄控制
    /**
     * 绑定当前渲染特效Bundle
     * @param handle Int
     */
    fun bindControllerBundle(handle: Int, isFaceBeauty: Boolean = false) {
        FULogger.d(TAG, "bindControllerBundle  handle:$handle  ")
        if (isFaceBeauty) {
            renderBundlesAdd(0, handle)
        } else {
            renderBundlesAdd(handle)
        }
    }

    /**
     * 移除当前渲染特效Bundle
     * @param handle Int
     */
    fun unbindControllerBundle(handle: Int) {
        FULogger.d(TAG, "unbindControllerBundle  handle:$handle  ")
        renderBundlesRemove(handle)
    }


    /**
     * 更新当前渲染特效Bundle
     * @param oldHandle Int
     * @param newHandle Int
     */
    fun updateControllerBundle(oldHandle: Int, newHandle: Int, isFaceBeauty: Boolean = false) {
        FULogger.d(TAG, "bindControllerBundle  oldHandle:$oldHandle  newHandle:$newHandle")
        if (oldHandle != newHandle) {
            if (oldHandle > 0) {
                destroyBundle(oldHandle)
                renderBundlesRemove(oldHandle)
            }
            if (newHandle > 0) {
                if (isFaceBeauty) {
                    renderBundlesAdd(0, newHandle)
                } else {
                    renderBundlesAdd(newHandle)
                }
            }
        }
    }

    /**
     * 销毁当前渲染特效Bundle
     * @param handle Int
     */
    fun destroyControllerBundle(handle: Int) {
        FULogger.d(TAG, "destroyControllerBundle  handle:$handle  ")
        if (handle > 0) {
            destroyBundle(handle)
            renderBundlesRemove(handle)
        }
    }


    //endregion 控制句柄控制


    //region 资源句柄控制
    /**
     * 将资源道具绑定到controller道具上
     * @param controlHandle Int
     * @param items IntArray
     */
    fun bindControllerItem(controlHandle: Int, items: IntArray) {
        FULogger.d(TAG, "bindControllerItem  controlHandle:$controlHandle  items:${items.contentToString()}")
        if (controlHandle > 0 && items.isNotEmpty()) {
            SDKController.bindItems(controlHandle, items)
        }
    }

    /**
     * 将资源道具绑定到controller道具上
     * @param controlHandle Int
     * @param item Int
     */
    fun bindControllerItem(controlHandle: Int, item: Int) {
        bindControllerItem(controlHandle, intArrayOf(item))
    }


    /**
     * 将资源道具从controller道具上解绑
     * @param controlHandle Int
     * @param items IntArray
     */
    fun unbindControllerItem(controlHandle: Int, items: IntArray) {
        FULogger.d(TAG, "unbindControllerItem  controlHandle:$controlHandle  items:${items.contentToString()}")
        SDKController.unBindItems(controlHandle, items)
    }

    /**
     * 将资源道具从controller道具上解绑
     * @param controlHandle Int
     * @param item IntArray
     */
    fun unbindControllerItem(controlHandle: Int, item: Int) {
        unbindControllerItem(controlHandle, intArrayOf(item))
    }


    //endregion 资源句柄控制

    //region Bundle管理
    /*路径-Bundle 存储*/
    private val mBundleItemMap = HashMap<String, BundleData>()

    /*句柄id-路径 存储*/
    private val mBundleItemPathMap = HashMap<Int, String>()


    /**
     * 加载本地Bundle，如果已加载则默认使用原来
     * @param name String 名称标识
     * @param path String Assets路径
     * @return Int
     */
    fun loadBundleFile(name: String, path: String): Int {
        FULogger.d(TAG, "createItemFromPackage  name:$name  path:$path")
        var handle = mBundleItemMap[path]?.handle ?: 0
        if (handle <= 0) {
            handle = createItemFromPackage(path)
            if (handle > 0) {
                mBundleItemMap[path] = BundleData(name, path, handle)
                mBundleItemPathMap[handle] = path
            } else {
                FULogger.e(TAG, "createItemFromPackage failed  name:$name  path:$path")
            }
        }
        return handle
    }


    /**
     * 销毁道具
     * @param handles IntArray
     */
    fun destroyBundle(handles: IntArray) {
        handles.forEach {
            if (it > 0) {
                destroyBundle(it)
            }
        }
    }


    /**
     * 销毁道具
     * @param handle Int
     */
    fun destroyBundle(handle: Int) {
        val path = mBundleItemPathMap[handle]
        FULogger.d(TAG, "destroyBundle  path:$path    handle:$handle")
        path?.let {
            mBundleItemMap.remove(path)
            mBundleItemPathMap.remove(handle)
        }
        SDKController.destroyItem(handle)
    }

    /**
     * 根据句柄获取当前Bundle路径
     * @param handle Int
     * @return String?
     */
    fun getBundlePath(handle: Int): String? {
        return mBundleItemPathMap[handle]

    }

    /**
     * 根据当前Bundle路径获取句柄
     * @param path String 路径
     * @return Int  句柄
     */
    fun getBundleHandle(path: String): Int {
        return mBundleItemMap[path]?.handle ?: 0
    }


    /**
     * 加载 bundle 道具
     * @param path String 放在 assets中的道具文件
     * @return Int 创建的道具句柄
     */
    private fun createItemFromPackage(path: String): Int {
        FULogger.i(TAG, "createItemFromPackage   path=$path")
        if (path.isEmpty()) return 0
        val buffer = FileUtils.loadBundleFromLocal(FURenderManager.mContext, path)
        var handle = 0
        if (buffer != null) {
            handle = SDKController.createItemFromPackage(buffer, path)
        } else {
            FURenderManager.mOperateCallback?.onFail(FURenderConfig.OPERATE_FAILED_FILE_NOT_FOUND, "file not found: $path")
            FULogger.d(TAG, "createItemFromPackage failed   file not found: $path")
        }
        return handle
    }


    //endregion道具


    /**
     * 销毁所有道具
     */
    internal fun release() {
        FULogger.d(TAG, "release")
        renderBundlesClear()
        mBundleItemMap.clear()
        mBundleItemPathMap.clear()
        SDKController.destroyAllItems()
    }
}