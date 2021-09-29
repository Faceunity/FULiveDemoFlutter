package com.faceunity.core.controller.prop

import com.faceunity.core.entity.FUFeaturesData
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 *
 * DESC：
 * Created on 2021/4/2
 *
 */
class ThreadQueuePool {

    /**
     * 单元数据
     * @property type Int 类型  remove   add  replace  UNIT
     * @property data FUFeaturesData
     * @property replaceData FUFeaturesData?
     * @constructor
     */
    data class QueueItem(val type: QueueType, val data: FUFeaturesData? = null, val replaceData: FUFeaturesData? = null, val unit: (() -> Unit)? = null)


    enum class QueueType {
        ADD, REMOVE, REPLACE, UNIT
    }


    /**
     * 单元数据存储数组
     */
    private var dataPool: Array<QueueItem?>
    private var poolArray1: Array<QueueItem?> = arrayOfNulls(32)//扩容使用
    private var poolArray2: Array<QueueItem?> = arrayOfNulls(1)//扩容使用

    /**
     * 存取下标
     */
    private val queueArrayMap = HashMap<Long, Int>()//当前道具id对应下标
    private var currentPushNode = 0//push 存储数据下标
    private val pullNodeList = ArrayList<Int>()
    private val dataLock = Any()

    init {
        dataPool = poolArray1
    }


    /**
    弹出当前第一个数据单元
     */
    fun pull(): QueueItem? {
        synchronized(dataLock) {
            if (pullNodeList.size == 0) {
                return null
            }
            val pullNode = pullNodeList[0]
            val item = dataPool[pullNode]!!
            when (item.type) {
                QueueType.ADD -> {
                    queueArrayMap.remove(item.data!!.id)
                }
                QueueType.REPLACE -> {
                    queueArrayMap.remove(item.replaceData!!.id)
                }
            }
            dataPool[pullNode] = null
            pullNodeList.removeAt(0)
            return item
        }
    }


    /**
     * 弹入数据单元
     * @param item PoolItem
     */
    fun push(item: QueueItem) {
        synchronized(dataLock) {
            if (pullNodeList.size == dataPool.size - 1) {
                addPoolSize()
            }
            when (item.type) {
                QueueType.ADD -> {
                    applyAdd(item)
                }
                QueueType.REMOVE -> {
                    applyRemove(item)
                }
                QueueType.REPLACE -> {
                    applyReplace(item)
                }
                QueueType.UNIT -> {
                    applyAddUnit(item)
                }
            }
        }
    }

    /**
     * 清空
     */
    fun clear() {
        synchronized(dataLock) {
            dataPool = arrayOfNulls(dataPool.size)
            queueArrayMap.clear()
            pullNodeList.clear()
            currentPushNode = 0
        }
    }


    /**
     * 添加执行函数
     * @param item PoolItem
     */
    private fun applyAddUnit(item: QueueItem) {
        updatePushNode()
        dataPool[currentPushNode] = item
        pullNodeList.add(currentPushNode)
    }


    /**
     * 添加数据元素
     * @param item PoolItem
     */
    private fun applyAdd(item: QueueItem) {
        updatePushNode()
        queueArrayMap[item.data!!.id] = currentPushNode
        dataPool[currentPushNode] = item
        pullNodeList.add(currentPushNode)
    }

    /**
     * 移除数据元素
     * @param item PoolItem
     */
    private fun applyRemove(item: QueueItem) {
        val itemId = item.data!!.id
        if (queueArrayMap.containsKey(itemId)) {
            val index = queueArrayMap[itemId]!!
            queueArrayMap.remove(itemId)
            dataPool[index] = null
            pullNodeList.remove(index)
        } else {
            updatePushNode()
            dataPool[currentPushNode] = item
            pullNodeList.add(currentPushNode)
        }
    }

    /**
     * 替换数据原数
     * @param item PoolItem
     */
    private fun applyReplace(item: QueueItem) {
        val itemId = item.data!!.id
        val replaceId = item.replaceData!!.id
        if (queueArrayMap.containsKey(itemId)) {
            val index = queueArrayMap[itemId]!!
            queueArrayMap.remove(itemId)
            val oldQueue = dataPool[index]!!
            val newQueue: QueueItem
            if (oldQueue.type == QueueType.REPLACE) {
                if (oldQueue.data!!.id == item.replaceData.id) {
                    dataPool[index] = null
                    pullNodeList.remove(index)
                    return
                } else {
                    newQueue = QueueItem(QueueType.REPLACE, oldQueue.data, item.replaceData)
                }
            } else {
                newQueue = QueueItem(QueueType.ADD, item.replaceData)
            }
            dataPool[index] = null
            pullNodeList.remove(index)
            updatePushNode()
            dataPool[currentPushNode] = newQueue
        } else {
            updatePushNode()
            dataPool[currentPushNode] = item

        }
        pullNodeList.add(currentPushNode)
        queueArrayMap[replaceId] = currentPushNode
    }

    /**
     * 数组扩容
     */
    private fun addPoolSize() {
        if (dataPool == poolArray1) {
            poolArray2 = arrayOfNulls(poolArray1.size * 2)
            System.arraycopy(poolArray1, 0, poolArray2, 0, poolArray1.size)
            poolArray1 = arrayOfNulls(0)
            dataPool = poolArray2
        } else {
            poolArray1 = arrayOfNulls(poolArray2.size * 2)
            System.arraycopy(poolArray2, 0, poolArray1, 0, poolArray2.size)
            poolArray2 = arrayOfNulls(0)
            dataPool = poolArray1
        }
    }


    /**
     * 更新存储下标
     * @return Int
     */
    private fun updatePushNode() {
        while (dataPool[currentPushNode] != null) {
            currentPushNode = if (currentPushNode == dataPool.size - 1) {
                0
            } else {
                currentPushNode + 1
            }
        }
    }


}