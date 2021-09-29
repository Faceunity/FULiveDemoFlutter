package com.faceunity.core.model.prop

import com.faceunity.core.support.FURenderBridge
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：多道具容器
 * Created on 2021/2/7
 *
 */
class PropContainer {

    companion object {
        const val TAG = "KIT_PropContainer"

        @Volatile
        private var INSTANCE: PropContainer? = null

        internal fun getInstance(): PropContainer {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = PropContainer()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    /*道具控制器*/
    private val mPropController by lazy { FURenderBridge.getInstance().mPropContainerController }

    /*道具缓存*/
    private val propMap = LinkedHashMap<Long, Prop>()

    /**
     * 添加道具
     * @param prop BaseProp 道具模型
     * @return Boolean
     */
    fun addProp(prop: Prop): Boolean {
        val path = prop.controlBundle.path
        if (propMap.containsKey(prop.propId)) {
            FULogger.e(TAG, "this prop already added ")
            return false
        }
        propMap[prop.propId] = prop
        mPropController.addProp(prop.buildFUFeaturesData())
        return true
    }

    /**
     * 移除道具
     * @param prop BaseProp
     * @return Boolean
     */
    fun removeProp(prop: Prop): Boolean {
        if (!propMap.containsKey(prop.propId)) {
            FULogger.e(TAG, "The prop  does not exist ")
            return false
        }
        propMap.remove(prop.propId)
        mPropController.removeProp(prop.buildFUFeaturesData())
        return true
    }

    /**
     * 移除全部道具
     * @return Boolean
     */
    fun removeAllProp(): Boolean {
        propMap.forEach { (_, prop) ->
            mPropController.removeProp(prop.buildFUFeaturesData())
        }
        propMap.clear()
        return true
    }

    /**
     * 更换道具
     * @param oldProp BaseProp
     * @param newProp BaseProp
     * @return Boolean
     */
    fun replaceProp(oldProp: Prop?, newProp: Prop?): Boolean {
        if (oldProp == null && newProp == null) {
            FULogger.w(TAG, "oldProp and newProp is null")
        } else if (oldProp == null && newProp != null) {
            addProp(newProp)
        } else if (oldProp != null && newProp == null) {
            removeProp(oldProp)
        } else if (oldProp != null && newProp != null) {
            if (!propMap.containsKey(oldProp.propId)) {
                FULogger.e(TAG, "The oldProp  does not exist ")
                return addProp(newProp)
            }
            if (propMap.containsKey(newProp.propId)) {
                return if (oldProp.propId == newProp.propId) {
                    FULogger.w(TAG, "oldProp and newProp   is same")
                    false
                } else {
                    FULogger.e(TAG, "this newProp already added")
                    removeProp(oldProp)
                }
            }
            propMap.remove(oldProp.propId)
            propMap[newProp.propId] = newProp
            mPropController.replaceProp(oldProp.buildFUFeaturesData(), newProp.buildFUFeaturesData())
            return true
        }
        return false
    }

    /**
     * 获取全部道具
     * @return List<BaseProp>
     */
    fun getAllProp(): List<Prop> {
        val list = ArrayList<Prop>()
        propMap.forEach { (_, prop) ->
            list.add(prop)
        }
        return list
    }

}