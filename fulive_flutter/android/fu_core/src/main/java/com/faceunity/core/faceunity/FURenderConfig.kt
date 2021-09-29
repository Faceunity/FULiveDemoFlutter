package com.faceunity.core.faceunity


/**
 *
 * DESC：
 * Created on 2020/12/7
 *
 */
object FURenderConfig {

    /************************** OperateCallback回调相关code ******************************/
    const val OPERATE_SUCCESS_AUTH = 200 //鉴权成功
    const val OPERATE_SUCCESS_LOAD_AI_MODEL = 201 //加载AI道具成功
    const val OPERATE_SUCCESS_LOAD_BUNDLE = 202 //加载普通道具成功
    const val OPERATE_FAILED_AUTH = 10000//鉴权失败
    const val OPERATE_FAILED_FILE_NOT_FOUND = 10001 //文件找不到
    const val OPERATE_FAILED_LOAD_AI_MODEL = 10002 //加载AI道具失败
    const val OPERATE_FAILED_LOAD_BUNDLE = 10003//加载普通道具失败




}