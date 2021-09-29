package com.faceunity.core.media.video.encoder;
/*
 * AudioVideoRecordingSample
 * Sample project to cature audio and video from internal mic/camera and save as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: RenderHandler.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
 */

import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.faceunity.core.program.ProgramTexture2d;
import com.faceunity.core.program.core.EglCore;
import com.faceunity.core.program.core.Program;
import com.faceunity.core.program.core.WindowSurface;


/**
 * Helper class to draw texture to whole view on private thread
 */
public final class RenderHandler implements Runnable {
    private static final boolean DEBUG = false;    // TODO set false on release
    private static final String TAG = "Video_RenderHandler";

    private final Object mSync = new Object();
    private EGLContext mShard_context;
    private Surface mSurface;
    private int mTexId;
    private float[] mTexMatrix = new float[16];
    private float[] mMvpMatrix = new float[16];

    private volatile boolean mRequestSetEglContext;
    private volatile boolean mRequestRelease;
    private volatile int mRequestDraw;

    private WindowSurface mInputWindowSurface;
    private EglCore mEglCore;
    private Program mProgramTexture2d;

    public static final RenderHandler createHandler(final String name) {
        if (DEBUG) Log.v(TAG, "createHandler:");
        final RenderHandler handler = new RenderHandler();
        synchronized (handler.mSync) {
            new Thread(handler, !TextUtils.isEmpty(name) ? name : TAG).start();
            try {
                handler.mSync.wait();
            } catch (final InterruptedException e) {
            }
        }
        return handler;
    }

    public final void setEglContext(final EGLContext shared_context, final Surface surface, final int texId) {
        if (DEBUG) Log.i(TAG, "setEglContext:");
        synchronized (mSync) {
            if (mRequestRelease) return;
            mShard_context = shared_context;
            mTexId = texId;
            mSurface = surface;
            mRequestSetEglContext = true;
            Matrix.setIdentityM(mTexMatrix, 0);
            Matrix.setIdentityM(mMvpMatrix, 0);
            mSync.notifyAll();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
            }
        }
    }

    public final void draw(final int tex_id, final float[] tex_matrix, final float[] mvp_matrix) {
        synchronized (mSync) {
            if (mRequestRelease) return;
            mTexId = tex_id;
            if ((tex_matrix != null) && (tex_matrix.length >= 16)) {
                System.arraycopy(tex_matrix, 0, mTexMatrix, 0, 16);
            } else {
                Matrix.setIdentityM(mTexMatrix, 0);
            }
            if ((mvp_matrix != null) && (mvp_matrix.length >= 16)) {
                System.arraycopy(mvp_matrix, 0, mMvpMatrix, 0, 16);
            } else {
                Matrix.setIdentityM(mMvpMatrix, 0);
            }
            mRequestDraw++;
            mSync.notifyAll();
        }
    }

    public boolean isValid() {
        synchronized (mSync) {
            return !(mSurface instanceof Surface) || ((Surface) mSurface).isValid();
        }
    }

    public final void release() {
        if (DEBUG) Log.i(TAG, "release:");
        synchronized (mSync) {
            if (mRequestRelease) return;
            mRequestRelease = true;
            mSync.notifyAll();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
            }
        }
    }

    //********************************************************************************
//********************************************************************************

    @Override
    public final void run() {
        if (DEBUG) Log.i(TAG, "RenderHandler thread started:");
        synchronized (mSync) {
            mRequestSetEglContext = mRequestRelease = false;
            mRequestDraw = 0;
            mSync.notifyAll();
        }
        boolean localRequestDraw;
        for (; ; ) {
            synchronized (mSync) {
                if (mRequestRelease) break;
                if (mRequestSetEglContext) {
                    mRequestSetEglContext = false;
                    internalPrepare();
                }
                localRequestDraw = mRequestDraw > 0;
                if (localRequestDraw) {
                    mRequestDraw--;
                }
            }
            if (localRequestDraw) {
                if ((mEglCore != null) && mTexId >= 0) {
                    mInputWindowSurface.makeCurrent();
                    // clear screen with yellow color so that you can see rendering rectangle
                    GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                    mProgramTexture2d.drawFrame(mTexId, mTexMatrix, mMvpMatrix);
                    mInputWindowSurface.swapBuffers();
                }
            } else {
                synchronized (mSync) {
                    try {
                        mSync.wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }
        synchronized (mSync) {
            mRequestRelease = true;
            internalRelease();
            mSync.notifyAll();
        }
        if (DEBUG) Log.i(TAG, "RenderHandler thread finished:");
    }

    private final void internalPrepare() {
        if (DEBUG) Log.i(TAG, "internalPrepare:");
        internalRelease();
        mEglCore = new EglCore(mShard_context, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mSurface, true);
        mInputWindowSurface.makeCurrent();
        mProgramTexture2d = new ProgramTexture2d();
        mSurface = null;
        mSync.notifyAll();
    }

    private final void internalRelease() {
        if (DEBUG) Log.i(TAG, "internalRelease:");
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
    }

}
