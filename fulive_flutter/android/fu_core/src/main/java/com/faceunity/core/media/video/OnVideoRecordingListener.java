package com.faceunity.core.media.video;

import java.io.File;

public interface OnVideoRecordingListener {

    /**
     * 开启准备
     */
    void onPrepared();

    /**
     * 录制时长
     *
     * @param time
     */
    void onProcess(Long time);

    /**
     * 录制完成回调
     */
    void onFinish(File file);
}