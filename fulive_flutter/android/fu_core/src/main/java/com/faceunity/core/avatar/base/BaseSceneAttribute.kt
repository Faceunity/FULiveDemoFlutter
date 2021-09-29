package com.faceunity.core.avatar.base

import com.faceunity.core.support.FURenderBridge
/**
 *
 * DESC：
 * Created on 2021/1/12
 *
 */
abstract class BaseSceneAttribute {

    var TAG = "KIT_PTA_${this.javaClass.name}"

    internal val mAvatarController by lazy { FURenderBridge.getInstance().mAvatarController }

    /**
     * 绑定的sceneId
     */
    internal var sceneId: Long = -1

//
//
//
//
//    private val mBundleHelper by lazy { FaceUnityBundleHelper.getInstance() }
//
//
//    /**
//     * 赋值处理
//     * @param unit Function1<[@kotlin.ParameterName] LinkedHashMap<String, Any>, Unit>
//     */
//    protected fun doSetParam(unit: (params: LinkedHashMap<String, Any>) -> Unit) {
//        val params = LinkedHashMap<String, Any>()
//        unit(params)
//        if (params.isNotEmpty()) {
//            mAvatarController.setSceneParam(params)
//        }
//    }
//
//    /**
//     * 涉及到句柄对象的，异步赋值处理
//     * @param bundle BundleHandleData
//     * @param unit Function2<[@kotlin.ParameterName] Int, [@kotlin.ParameterName] LinkedHashMap<String, Any>, Unit>
//     */
//    protected fun doSetParam(bundle: BundleHandleData, unit: (handle: Int, params: LinkedHashMap<String, Any>) -> Unit) {
//        mAvatarController.runBackground {
//            val handle = getBundleHandle(bundle.path)
//            if (handle <= 0) {
//                FULogger.w(TAG, "doSetParam failed bundle=${bundle.name}  handle=$handle")
//                return@runBackground
//            }
//            val params = LinkedHashMap<String, Any>()
//            unit(handle, params)
//            if (params.size > 0) {
//                mAvatarController.setSceneParam(params)
//            }
//        }
//    }
//
//
//    protected fun createTexForItem(bundle: BundleHandleData?, name: String, data: ByteArray, width: Int, height: Int) {
//        if (bundle == null) {
//            FULogger.w(TAG, "createTexForItem failed  bundle==null")
//            return
//        }
//        mAvatarController.runBackground {
//            val handle = getBundleHandle(bundle.path)
//            if (handle <= 0) {
//                FULogger.w(TAG, "createTexForItem failed  bundle=${bundle.name}   handle=$handle")
//                return@runBackground
//            }
//            mAvatarController.createTexForItemAvatar(handle, name, data, width, height)
//        }
//    }
//
//    protected fun updateTexForItem(bundle: BundleHandleData?, data: ByteArray, width: Int, height: Int) {
//        if (bundle == null) {
//            FULogger.w(TAG, "updateTexForItem failed  bundle==null")
//            return
//        }
//        mAvatarController.runBackground {
//            val handle = getBundleHandle(bundle.path)
//            if (handle <= 0) {
//                FULogger.w(TAG, "updateTexForItem failed  bundle=${bundle.name}   handle=$handle")
//                return@runBackground
//            }
//            val name = "{\"name\":\"update_tex_from_data\", \"UUID\":$handle, \"dc_name\":\"eyel\"}"
//            mAvatarController.createTexForItemAvatar(handle, name, data, width, height)
//        }
//    }
//
//    /**
//     * 加载句柄
//     * @param bundle BundleHandleData
//     */
//    protected fun loadItemBundle(bundle: BundleHandleData) {
//        mAvatarController.loadSceneItemBundle(bundle)
//    }
//
//    /**
//     * 移除句柄
//     * @param bundle BundleHandleData
//     */
//    protected fun removeItemBundle(bundle: BundleHandleData) {
//        mAvatarController.removeSceneItemBundle(bundle)
//    }
//
//    protected fun getItemParam(name: String, clazz: Any): Any? {
//        return mAvatarController.getSceneItemParam(name, clazz)
//    }
//
//
//    /**
//     * 根据路径获取句柄
//     * @param path String
//     * @return Int
//     */
//    protected fun getBundleHandle(path: String) = mBundleHelper.mBundleItemMap[path]?.handle ?: -1


}