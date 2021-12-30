package com.faceunity.core.avatar.base

import com.faceunity.core.support.FURenderBridge

/**
 *
 * DESC：Avatar 属性基类
 * Created on 2021/1/12
 *
 */
abstract class BaseAvatarAttribute {
    var TAG = "KIT_PTA_${this.javaClass.name}"

    internal val mAvatarController by lazy { FURenderBridge.getInstance().mAvatarController }

    /**
     * 绑定的avatarId
     */
    internal var avatarId: Long = -1
//
//    /**
//     * 获取参数值
//     * @param name String
//     * @param clazz Any
//     * @param params LinkedHashMap<String, Any>
//     * @return Any?
//     */
//    protected fun getItemParam(name: String, clazz: Any, params: LinkedHashMap<String, Any> = LinkedHashMap()): Any? {
//        return mAvatarController.getAvatarItemParam(avatarId, name, clazz, params)
//    }
//

//    /**
//     * 执行函数
//     * @param unit  Unit
//     */
//    protected fun doSetParam(unit: () -> Unit) {
//        val params = LinkedHashMap<String, Any>()
//        unit(params)
//        if (params.isNotEmpty()) {
//           mAvatarController.setAvatarParam(avatarId, params)
//        }
//    }

//    /**
//     * 赋值处理
//     * @param unit Function1<[@kotlin.ParameterName] LinkedHashMap<String, Any>, Unit>
//     */
//    protected fun doSetParam(unit: (params: LinkedHashMap<String, Any>) -> Unit) {
//        val params = LinkedHashMap<String, Any>()
//        unit(params)
//        if (params.isNotEmpty()) {
////            mAvatarController.setAvatarParam(avatarId, params)
//        }
//    }
//
//    /**
//     * 涉及到句柄对象的，异步赋值处理
//     * @param bundle BundleHandleData
//     * @param unit Function2<[@kotlin.ParameterName] Int, [@kotlin.ParameterName] LinkedHashMap<String, Any>, Unit>
//     */
//    protected fun doSetParam(bundle: FUBundleData, unit: (handle: Int, params: LinkedHashMap<String, Any>) -> Unit) {
//        mAvatarController.runBackground {
//            val handle = getBundleHandle(bundle.path)
//            if (handle <= 0) {
//                FULogger.w(TAG, "doSetParam failed bundle=${bundle.name}  handle=$handle")
//                return@runBackground
//            }
//            val params = LinkedHashMap<String, Any>()
//            unit(handle, params)
//            if (params.size > 0) {
//                mAvatarController.setAvatarParam(avatarId, params)
//            }
//        }
//    }
//
//
//    /**
//     * 加载句柄
//     * @param bundle BundleHandleData
//     */
//    protected fun loadItemBundle(bundle: FUBundleData) {
//        mAvatarController.loadSceneItemBundle(bundle, avatarId)
//    }
//
//    /**
//     * 移除句柄
//     * @param bundle BundleHandleData
//     */
//    protected fun removeItemBundle(bundle: FUBundleData) {
//        mAvatarController.removeSceneItemBundle(bundle, avatarId)
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