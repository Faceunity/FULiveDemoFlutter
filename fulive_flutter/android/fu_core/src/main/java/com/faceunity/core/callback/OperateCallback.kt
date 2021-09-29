package com.faceunity.core.callback


/**
 *
 * DESC： SDK统一的回调信息返回接口
 * Created on 2020/11/16
 *
 */
interface OperateCallback {
    /**
     * 调用成功
     * @param code Int
     * @param msg String
     */
    fun onSuccess(code: Int, msg: String)

    /**
     * 调用失败
     * @param errCode Int
     * @param errMsg String
     */
    fun onFail(errCode: Int, errMsg: String)

}
