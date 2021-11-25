package com.example.fulive_plugin.entity;

/**
 * @author Qinyu on 2021-10-13
 * @description
 */
public final class FuEvent {
    public static final int choose_photo = 0x01;
    public static final int choose_video = 0x02;
    public static final int choose_result_photo = 0x03;
    public static final int choose_result_video = 0x04;
    public static final int start_video_play = 0x05;
    public static final int stop_video_play = 0x06;
    public static final int activity_resume = 0x07;
    public static final int activity_pause = 0x08;

    private int code;
    private Object data;

    public FuEvent(int code) {
        this.code = code;
    }

    public FuEvent(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
