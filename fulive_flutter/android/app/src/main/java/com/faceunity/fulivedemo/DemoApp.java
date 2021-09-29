package com.faceunity.fulivedemo;

import android.app.Application;
import android.util.Log;

import com.faceunity.core.callback.OperateCallback;
import com.faceunity.core.faceunity.FURenderManager;
import com.faceunity.core.utils.FULogger;

/**
 * @author Qinyu on 2021-09-15
 * @description
 */
public class DemoApp extends Application {
    private static String TAG = DemoApp.class.getSimpleName();
    public static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        registerFURender();
    }

    private void registerFURender() {
        FURenderManager.setKitDebug(FULogger.LogLevel.TRACE);
        FURenderManager.setCoreDebug(FULogger.LogLevel.INFO);
        FURenderManager.registerFURender(mApplication, authpack.A(), new OperateCallback() {
            @Override
            public void onSuccess(int code, String msg) {
                Log.d(TAG, "success:" + msg);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                Log.e(TAG, "errCode:" + errCode + "   errMsg:" + errMsg);
            }
        });
    }
}
