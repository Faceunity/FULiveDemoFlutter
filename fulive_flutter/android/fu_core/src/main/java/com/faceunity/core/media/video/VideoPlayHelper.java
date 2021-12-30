package com.faceunity.core.media.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;

import com.faceunity.core.utils.FileUtils;
import com.faceunity.core.utils.MediaFileUtil;
import com.faceunity.core.utils.VideoDecoder;

import java.nio.ByteBuffer;

/**
 * DESC：MP4文件播放
 * Created on 2021/3/28
 */
public class VideoPlayHelper {

    /**  加载本地文件保存初始大小 */
    private int requestPhotoWidth = 1080;
    private int requestPhotoHeight = 1920;

    public interface VideoDecoderListener {
        void onReadPixel(byte[] bytes, int width, int height);
    }

    private VideoDecoder mVideoDecoder;
    private VideoDecoderListener mVideoDecoderListener;

    private Handler mPlayerHandler;


    public VideoPlayHelper(VideoDecoderListener listener, GLSurfaceView surfaceView) {
        mVideoDecoderListener = listener;
        startPlayerThread();
        mVideoDecoder = new VideoDecoder();
        mVideoDecoder.setOnReadPixelListener(mOnReadPixelListener);
        surfaceView.queueEvent(() -> mVideoDecoder.create(EGL14.eglGetCurrentContext(), true));
    }

    public VideoPlayHelper(VideoDecoderListener listener, GLSurfaceView surfaceView, boolean isFlip) {
        mVideoDecoderListener = listener;
        startPlayerThread();
        mVideoDecoder = new VideoDecoder();
        mVideoDecoder.setOnReadPixelListener(mOnReadPixelListener);
        surfaceView.queueEvent(() -> mVideoDecoder.create(EGL14.eglGetCurrentContext(), isFlip));
    }

    private VideoDecoder.OnReadPixelListener mOnReadPixelListener = new VideoDecoder.OnReadPixelListener() {

        @Override
        public void onReadPixel(int width, int height, byte[] rgba) {
            mVideoDecoderListener.onReadPixel(rgba, width, height);
        }
    };

    /**
     * 播放
     *
     * @param path
     */
    public void playVideo(String path) {
        mPlayerHandler.post(() -> {
            if (path == null && mVideoDecoder != null) {
                mVideoDecoder.stop();
                return;
            }
            if (MediaFileUtil.isImageFileType(path)) {
                Bitmap bitmap = FileUtils.loadBitmapFromExternal(path, requestPhotoWidth, requestPhotoHeight);
                int orientation = FileUtils.INSTANCE.getPhotoOrientation(path);
                bitmap = rotateBitmap(bitmap, orientation);
                byte[] rgbBytes = new byte[bitmap.getByteCount()];
                ByteBuffer rgbaBuffer = ByteBuffer.wrap(rgbBytes);
                bitmap.copyPixelsToBuffer(rgbaBuffer);
                bitmap.recycle();
                mVideoDecoder.stop();
                mVideoDecoderListener.onReadPixel(rgbBytes, bitmap.getWidth(), bitmap.getHeight());
            } else if (MediaFileUtil.isVideoFileType(path)) {
                mVideoDecoder.stop();
                mVideoDecoder.start(path);
            }
        });
    }

    /**
     * 播放
     *
     * @param path
     */
    public void playAssetsVideo(Context context, String path) {
        mPlayerHandler.post(() -> {
            if (path == null && mVideoDecoder != null) {
                mVideoDecoder.stop();
                return;
            }
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            String mFilePath = FileUtils.copyAssetsToExternalFilesDir(context, path, fileName);
            if (mFilePath == null) return;
            if (MediaFileUtil.isImageFileType(mFilePath)) {
                Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
                int orientation = FileUtils.INSTANCE.getPhotoOrientation(mFilePath);
                bitmap = rotateBitmap(bitmap, orientation);
                byte[] rgbBytes = new byte[bitmap.getByteCount()];
                ByteBuffer rgbaBuffer = ByteBuffer.wrap(rgbBytes);
                bitmap.copyPixelsToBuffer(rgbaBuffer);
                bitmap.recycle();
                mVideoDecoder.stop();
                mVideoDecoderListener.onReadPixel(rgbBytes, bitmap.getWidth(), bitmap.getHeight());
            } else if (MediaFileUtil.isVideoFileType(path)) {
                mVideoDecoder.stop();
                mVideoDecoder.start(mFilePath);
            }
        });
    }


    /**
     * 暂停播放
     */
    public void pausePlay() {
        mPlayerHandler.post(() -> {
            mVideoDecoder.stop();
        });
    }


    /**
     * 资源释放
     */
    public void release() {
        mPlayerHandler.removeCallbacksAndMessages(null);
        mVideoDecoderListener = null;
        mPlayerHandler.post(() -> {
            mVideoDecoder.release();
            mVideoDecoder = null;
        });
        stopPlayerThread();
    }

    public void setFlip(boolean isFlip) {
        mPlayerHandler.post(() -> mVideoDecoder.setFrontCam(isFlip));
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    /**
     * 启动播放线程
     */
    private void startPlayerThread() {
        HandlerThread playerThread = new HandlerThread("video_decoder");
        playerThread.start();
        mPlayerHandler = new Handler(playerThread.getLooper());
    }

    /**
     * 关闭播放线程
     */
    private void stopPlayerThread() {
        mPlayerHandler.getLooper().quitSafely();
        mPlayerHandler = null;
    }

}
