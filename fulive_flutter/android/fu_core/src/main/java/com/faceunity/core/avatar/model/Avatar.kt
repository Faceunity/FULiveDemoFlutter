package com.faceunity.core.avatar.model

import com.faceunity.core.avatar.avatar.*
import com.faceunity.core.avatar.base.BaseAvatarAttribute
import com.faceunity.core.avatar.control.FUAAvatarData
import com.faceunity.core.entity.FUAnimationData
import com.faceunity.core.entity.FUBundleData
import com.faceunity.core.entity.FUVisibleBundleData
import com.faceunity.core.utils.FULogger


/**
 *
 * DESC：DESC：Avatar人物数据模型
 * Created on 2021/3/30
 *
 */
class Avatar(@JvmField val components: ArrayList<FUBundleData>) : BaseAvatarAttribute() {


    /*角色位置*/
    @JvmField
    val transForm: TransForm = TransForm()

    /*动画控制*/
    @JvmField
    val animation: Animation = Animation()

    /*BlendShape混合*/
    @JvmField
    val blendShape: BlendShape = BlendShape()

    /*颜色设置*/
    @JvmField
    val color: Color = Color(this)

    /*骨骼捏形*/
    @JvmField
    val deformation: Deformation = Deformation()

    /*DynamicBone控制*/
    @JvmField
    val dynamicBone: DynamicBone = DynamicBone()

    /*眼睛注视相机*/
    @JvmField
    val eyeFocusToCamera: EyeFocusToCamera = EyeFocusToCamera()

    /*捏脸*/
    @JvmField
    val facePup: FacePup = FacePup()


    init {
        avatarId = System.nanoTime()
        transForm.avatarId = avatarId
        animation.avatarId = avatarId
        blendShape.avatarId = avatarId
        color.avatarId = avatarId
        deformation.avatarId = avatarId
        dynamicBone.avatarId = avatarId
        eyeFocusToCamera.avatarId = avatarId
        facePup.avatarId = avatarId
    }

    /**
     * 获取人脸点位 2维坐标
     * @param index Int 点位标识
     * @return FloatArray
     */
    fun getInstanceFaceVertexScreenCoordinate(index: Int): FloatArray {
        val rect = FloatArray(2)
        mAvatarController.getInstanceFaceVertexScreenCoordinate(avatarId, index, rect)
        return rect
    }


    /**
     * 添加身体组件
     * @param bundle BundleHandleData 组件句柄
     */
    fun addComponent(bundle: FUBundleData) {
        components.forEach {
            if (it.path == bundle.path) {
                FULogger.w(TAG, "animation bundle has added bundle.name=${bundle.name}")
                return
            }
        }
        components.add(bundle)
        if (hasLoaded) {
            mAvatarController.loadAvatarItemBundle(avatarId, bundle)
            mAvatarController.setInstanceBodyInvisibleList(avatarId, unionInvisibleList())
        }
    }

    /**
     * 查找组件句柄
     * @param name String 组件名称
     * @return BundleHandleData? 组件句柄
     */
    fun getComponent(name: String): FUBundleData? {
        components.forEach {
            if (it.name == name)
                return it
        }
        FULogger.w(TAG, "animation bundle has not find name=$name")
        return null
    }

    /**
     * 移除组件
     * @param bundle BundleHandleData 组件句柄
     */
    fun removeComponent(bundle: FUBundleData) {
        components.forEach {
            if (it.path == bundle.path) {
                components.remove(it)
                if (hasLoaded) {
                    mAvatarController.removeAvatarItemBundle(avatarId, bundle)
                    mAvatarController.setInstanceBodyInvisibleList(avatarId, unionInvisibleList())
                }
                return
            }
        }
        FULogger.w(TAG, "animation bundle has not find bundle.name=${bundle.name}")
    }


    /**
     * 移除组件
     * @param name String 组件名称
     */
    fun removeComponent(name: String) {
        components.forEach {
            if (it.name == name) {
                components.remove(it)
                if (hasLoaded) {
                    mAvatarController.removeAvatarItemBundle(avatarId, it)
                    mAvatarController.setInstanceBodyInvisibleList(avatarId, unionInvisibleList())
                }
                return
            }
        }
        FULogger.w(TAG, "animation bundle has not find  name=$name")
    }

    /**
     * 替换组件
     * @param name String 旧组件名称
     * @param newComponent FUBundleData 新组件
     */
    fun replaceComponent(name: String, newComponent: FUBundleData) {
        var oldComponent: FUBundleData? = null
        components.forEach {
            if (it.name == name) {
                oldComponent = it
                return@forEach
            }
        }
        if (oldComponent == null) {
            addComponent(newComponent)
        } else {
            replaceComponent(oldComponent!!, newComponent)
        }
    }

    /**
     * 替换组件
     * @param oldComponent FUBundleData 旧组件
     * @param newComponent FUBundleData 新组件
     */
    fun replaceComponent(oldComponent: FUBundleData?, newComponent: FUBundleData?) {
        if (oldComponent == null && newComponent == null) {
            FULogger.w(TAG, "oldComponent and newComponent is null")
        } else if (oldComponent == null && newComponent != null) {
            addComponent(newComponent)
        } else if (oldComponent != null && newComponent == null) {
            removeComponent(oldComponent)
        } else if (oldComponent != null && newComponent != null) {
            if (oldComponent.path == newComponent.path) {
                FULogger.w(TAG, "oldComponent and newComponent   is same")
                return
            }
            components.remove(oldComponent)
            components.add(newComponent)
            if (hasLoaded) {
                mAvatarController.replaceAvatarItemBundle(avatarId, oldComponent, newComponent)
                mAvatarController.setInstanceBodyInvisibleList(avatarId, unionInvisibleList())
            }
        }
    }

    /**
     * 替换组件
     * @param names ArrayList<String> 旧组件名称
     * @param newComponents ArrayList<FUBundleData> 新组件
     */
    fun replaceComponent(names: ArrayList<String>, newComponents: ArrayList<FUBundleData>) {
        /*数据去重*/
        val addComponents = ArrayList<FUBundleData>()
        val removeComponents = ArrayList<FUBundleData>()
        newComponents.forEach {
            if (names.contains(it.name)) {
                val bundle = getComponent(it.name)
                if (bundle == null) {
                    names.remove(it.name)
                    addComponents.add(it)
                } else if (bundle.path == it.path) {
                    names.remove(it.name)
                } else {
                    addComponents.add(it)
                }
            } else {
                addComponents.add(it)
            }
        }
        components.forEach {
            if (names.contains(it.name)) {
                removeComponents.add(it)
            }
        }
        removeComponents.forEach {
            components.remove(it)
        }
        addComponents.forEach {
            components.add(it)
        }
        if (hasLoaded) {
            mAvatarController.replaceAvatarItemBundle(avatarId, removeComponents, addComponents)
            mAvatarController.setInstanceBodyInvisibleList(avatarId, unionInvisibleList())
        }
    }


    /**
     * 替换组件
     * @param names ArrayList<String> 旧组件名称
     * @param newComponents ArrayList<FUBundleData> 新组件
     */
    fun replaceComponentGL(names: ArrayList<String>, newComponents: ArrayList<FUBundleData>) {
        /*数据去重*/
        val addComponents = ArrayList<FUBundleData>()
        val removeComponents = ArrayList<FUBundleData>()
        newComponents.forEach {
            if (names.contains(it.name)) {
                val bundle = getComponent(it.name)
                if (bundle == null) {
                    names.remove(it.name)
                    addComponents.add(it)
                } else if (bundle.path == it.path) {
                    names.remove(it.name)
                } else {
                    addComponents.add(it)
                }
            } else {
                addComponents.add(it)
            }
        }
        components.forEach {
            if (names.contains(it.name)) {
                removeComponents.add(it)
            }
        }
        removeComponents.forEach {
            components.remove(it)
        }
        addComponents.forEach {
            components.add(it)
        }
        if (hasLoaded) {
            mAvatarController.replaceAvatarItemBundleGL(avatarId, removeComponents, addComponents)
            mAvatarController.setInstanceBodyInvisibleList(avatarId, unionInvisibleList(), false)
        }
    }

    /**
     * Hack部分代码，只更新模型components缓存，不做底层配置
     * @param name String
     * @param newComponent FUBundleData
     */
    fun replaceComponentModelOnly(name: String, newComponent: FUBundleData) {
        var oldComponent: FUBundleData? = null
        components.forEach {
            if (it.name == name) {
                oldComponent = it
                return@forEach
            }
        }
        oldComponent?.let {
            components.remove(it)
        }
        components.add(newComponent)
    }

    /**
     * 构造Avatar数据
     * @return FUASceneData
     */
    internal fun buildFUAAvatarData(): FUAAvatarData {
        val params = LinkedHashMap<String, () -> Unit>()
        val itemBundles = ArrayList<FUBundleData>()
        val animationData = ArrayList<FUAnimationData>()
        itemBundles.addAll(components)
        val array = unionInvisibleList()
        params["setInstanceBodyInvisibleList"] = { mAvatarController.setInstanceBodyInvisibleList(avatarId, array, false) }
        transForm.loadParams(params)
        animation.loadParams(params, animationData)
        blendShape.loadParams(params)
        dynamicBone.loadParams(params)
        eyeFocusToCamera.loadParams(params)
        color.loadParams(params, params)
        facePup.loadParams(params)
        deformation.loadParams(params)
        hasLoaded = true
        return FUAAvatarData(avatarId, itemBundles, animationData, params)
    }

    /**
     * 数据克隆
     * @return Avatar
     */
    fun clone(): Avatar {
        /*clone 组件*/
        val cloneComponents = ArrayList<FUBundleData>()
        components.forEach {
            cloneComponents.add(FUBundleData(it.path, it.name))
        }
        val cloneAvatar = Avatar(cloneComponents)
        /*clone transForm*/
        cloneAvatar.transForm.clone(transForm)
        /*clone animations*/
        cloneAvatar.animation.clone(animation)
        /*clone blendShape*/
        cloneAvatar.blendShape.clone(blendShape)
        /*clone dynamicBone*/
        cloneAvatar.dynamicBone.clone(dynamicBone)
        /*clone eyeFocusToCamera*/
        cloneAvatar.eyeFocusToCamera.clone(eyeFocusToCamera)
        /*clone color*/
        cloneAvatar.color.clone(color)
        /*clone facePup*/
        cloneAvatar.facePup.clone(facePup)
        /*clone deformation*/
        cloneAvatar.deformation.clone(deformation)
        return cloneAvatar

    }

    /**
     * 显示部位数组求合集
     */
    private fun unionInvisibleList(): IntArray {
        val visibleSet = HashSet<Int>()
        val unionInvisibleList: ArrayList<Int> = ArrayList()
        components.filterIsInstance<FUVisibleBundleData>().forEach { visibleBundles ->
            visibleBundles.visibleList?.let { array ->
                array.forEach { id ->
                    visibleSet.add(id)
                }
            }
        }
        visibleSet.forEach {
            unionInvisibleList.add(it)
        }
        return unionInvisibleList.toIntArray()
    }

}