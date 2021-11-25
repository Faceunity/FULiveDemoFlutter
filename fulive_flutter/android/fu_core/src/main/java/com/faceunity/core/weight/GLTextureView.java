package com.faceunity.core.weight;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.opengl.GLES30;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.faceunity.core.utils.FULogger;

import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/*
 * Copyright (C) 2018 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class GLTextureView extends TextureView
        implements TextureView.SurfaceTextureListener, View.OnLayoutChangeListener {

    private final static String TAG = GLTextureView.class.getSimpleName();

    private final static boolean LOG_ATTACH_DETACH = false;
    private final static boolean LOG_THREADS = false;
    private final static boolean LOG_PAUSE_RESUME = false;
    private final static boolean LOG_SURFACE = false;
    private final static boolean LOG_RENDERER = false;
    private final static boolean LOG_RENDERER_DRAW_FRAME = false;
    private final static boolean LOG_EGL = false;

    /**
     * The renderer only renders
     * when the surface is created, or when {@link #requestRender} is called.
     *
     * @see #getRenderMode()
     * @see #setRenderMode(int)
     * @see #requestRender()
     */
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    /**
     * The renderer is called
     * continuously to re-render the scene.
     *
     * @see #getRenderMode()
     * @see #setRenderMode(int)
     */
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    /**
     * Check glError() after every GL call and throw an exception if glError indicates
     * that an error has occurred. This can be used to help track down which OpenGL ES call
     * is causing an error.
     *
     * @see #getDebugFlags
     * @see #setDebugFlags
     */
    public final static int DEBUG_CHECK_GL_ERROR = 1;

    /**
     * Log GL calls to the system log at "verbose" level with tag "GLTextureView".
     *
     * @see #getDebugFlags
     * @see #setDebugFlags
     */
    public final static int DEBUG_LOG_GL_CALLS = 2;

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     */
    public GLTextureView(Context context) {
        super(context);
        init();
    }

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     */
    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (glThread != null) {
                // GLThread may still be running if this view was never
                // attached to a window.
                glThread.requestExitAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    private void init() {
        setSurfaceTextureListener(this);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        FULogger.i(TAG,"setBackgroundDrawable pre");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && background != null) {
            FULogger.i(TAG,"setBackgroundDrawable start");
            setBackgroundDrawable(background);
            FULogger.i(TAG,"setBackgroundDrawable end");
        }
    }

    /**
     * Set the debug flags to a new value. The value is
     * constructed by OR-together zero or more
     * of the DEBUG_CHECK_* constants. The debug flags take effect
     * whenever a surface is created. The default value is zero.
     *
     * @param debugFlags the new debug flags
     * @see #DEBUG_CHECK_GL_ERROR
     * @see #DEBUG_LOG_GL_CALLS
     */
    public void setDebugFlags(int debugFlags) {
        this.debugFlags = debugFlags;
    }

    /**
     * Get the current value of the debug flags.
     *
     * @return the current value of the debug flags.
     */
    public int getDebugFlags() {
        return debugFlags;
    }

    /**
     * Control whether the EGL context is preserved when the GLTextureView is paused and
     * resumed.
     * <p>
     * If set to true, then the EGL context may be preserved when the GLTextureView is paused.
     * Whether the EGL context is actually preserved or not depends upon whether the
     * Android device that the program is running on can support an arbitrary number of EGL
     * contexts or not. Devices that can only support a limited number of EGL contexts must
     * release the  EGL context in order to allow multiple applications to share the GPU.
     * <p>
     * If set to false, the EGL context will be released when the GLTextureView is paused,
     * and recreated when the GLTextureView is resumed.
     * <p>
     * <p>
     * The default is false.
     *
     * @param preserveOnPause preserve the EGL context when paused
     */
    public void setPreserveEGLContextOnPause(boolean preserveOnPause) {
        preserveEGLContextOnPause = preserveOnPause;
    }

    /**
     * @return true if the EGL context will be preserved when paused
     */
    public boolean getPreserveEGLContextOnPause() {
        return preserveEGLContextOnPause;
    }

    /**
     * Set the renderer associated with this view. Also starts the thread that
     * will call the renderer, which in turn causes the rendering to start.
     * <p>This method should be called once and only once in the life-cycle of
     * a GLTextureView.
     * <p>The following GLTextureView methods can only be called <em>before</em>
     * setRenderer is called:
     * <ul>
     * <li>{@link #setEGLConfigChooser(boolean)}
     * <li>{@link #setEGLConfigChooser(EGLConfigChooser)}
     * <li>{@link #setEGLConfigChooser(int, int, int, int, int, int)}
     * </ul>
     * <p>
     * The following GLTextureView methods can only be called <em>after</em>
     * setRenderer is called:
     * <ul>
     * <li>{@link #getRenderMode()}
     * <li>{@link #onPause()}
     * <li>{@link #onResume()}
     * <li>{@link #queueEvent(Runnable)}
     * <li>{@link #requestRender()}
     * <li>{@link #setRenderMode(int)}
     * </ul>
     *
     * @param renderer the renderer to use to perform OpenGL drawing.
     */
    public void setRenderer(Renderer renderer) {
        checkRenderThreadState();
        if (eglConfigChooser == null) {
            eglConfigChooser = new SimpleEGLConfigChooser(true);
        }
        if (EGLContextFactory == null) {
            EGLContextFactory = new DefaultContextFactory();
        }
        if (eglWindowSurfaceFactory == null) {
            eglWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.renderer = renderer;
        glThread = new GLThread(mThisWeakRef);
        glThread.start();
    }

    /**
     * Install a custom EGLContextFactory.
     * <p>If this method is
     * called, it must be called before {@link #setRenderer(Renderer)}
     * is called.
     * <p>
     * If this method is not called, then by default
     * a context will be created with no shared context and
     * with a null attribute list.
     */
    public void setEGLContextFactory(EGLContextFactory factory) {
        checkRenderThreadState();
        EGLContextFactory = factory;
    }

    /**
     * Install a custom EGLWindowSurfaceFactory.
     * <p>If this method is
     * called, it must be called before {@link #setRenderer(Renderer)}
     * is called.
     * <p>
     * If this method is not called, then by default
     * a window surface will be created with a null attribute list.
     */
    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
        checkRenderThreadState();
        eglWindowSurfaceFactory = factory;
    }

    /**
     * Install a custom EGLConfigChooser.
     * <p>If this method is
     * called, it must be called before {@link #setRenderer(Renderer)}
     * is called.
     * <p>
     * If no setEGLConfigChooser method is called, then by default the
     * view will choose an EGLConfig that is compatible with the current
     * android.view.Surface, with a depth buffer depth of
     * at least 16 bits.
     */
    public void setEGLConfigChooser(EGLConfigChooser configChooser) {
        checkRenderThreadState();
        eglConfigChooser = configChooser;
    }

    /**
     * Install a config chooser which will choose a config
     * as close to 16-bit RGB as possible, with or without an optional depth
     * buffer as close to 16-bits as possible.
     * <p>If this method is
     * called, it must be called before {@link #setRenderer(Renderer)}
     * is called.
     * <p>
     * If no setEGLConfigChooser method is called, then by default the
     * view will choose an RGB_888 surface with a depth buffer depth of
     * at least 16 bits.
     */
    public void setEGLConfigChooser(boolean needDepth) {
        setEGLConfigChooser(new SimpleEGLConfigChooser(needDepth));
    }

    /**
     * Install a config chooser which will choose a config
     * with at least the specified depthSize and stencilSize,
     * and exactly the specified redSize, greenSize, blueSize and alphaSize.
     * <p>If this method is
     * called, it must be called before {@link #setRenderer(Renderer)}
     * is called.
     * <p>
     * If no setEGLConfigChooser method is called, then by default the
     * view will choose an RGB_888 surface with a depth buffer depth of
     * at least 16 bits.
     */
    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize,
                                    int depthSize, int stencilSize) {
        setEGLConfigChooser(
                new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    /**
     * Inform the default EGLContextFactory and default EGLConfigChooser
     * which EGLContext client version to pick.
     * <p>Use this method to create an OpenGL ES 2.0-compatible context.
     * Example:
     * <pre class="prettyprint">
     * public MyView(Context context) {
     * super(context);
     * setEGLContextClientVersion(2); // Pick an OpenGL ES 2.0 context.
     * setRenderer(new MyRenderer());
     * }
     * </pre>
     * <p>Note: Activities which require OpenGL ES 2.0 should indicate this by
     * setting @lt;uses-feature android:glEsVersion="0x00020000" /> in the activity's
     * AndroidManifest.xml file.
     * <p>If this method is called, it must be called before {@link #setRenderer(Renderer)}
     * is called.
     * <p>This method only affects the behavior of the default EGLContexFactory and the
     * default EGLConfigChooser. If
     * {@link #setEGLContextFactory(EGLContextFactory)} has been called, then the supplied
     * EGLContextFactory is responsible for creating an OpenGL ES 2.0-compatible context.
     * If
     * {@link #setEGLConfigChooser(EGLConfigChooser)} has been called, then the supplied
     * EGLConfigChooser is responsible for choosing an OpenGL ES 2.0-compatible config.
     *
     * @param version The EGLContext client version to choose. Use 2 for OpenGL ES 2.0
     */
    public void setEGLContextClientVersion(int version) {
        checkRenderThreadState();
        EGLContextClientVersion = version;
    }

    /**
     * Set the rendering mode. When renderMode is
     * RENDERMODE_CONTINUOUSLY, the renderer is called
     * repeatedly to re-render the scene. When renderMode
     * is RENDERMODE_WHEN_DIRTY, the renderer only rendered when the surface
     * is created, or when {@link #requestRender} is called. Defaults to RENDERMODE_CONTINUOUSLY.
     * <p>
     * Using RENDERMODE_WHEN_DIRTY can improve battery life and overall system performance
     * by allowing the GPU and CPU to idle when the view does not need to be updated.
     * <p>
     * This method can only be called after {@link #setRenderer(Renderer)}
     *
     * @param renderMode one of the RENDERMODE_X constants
     * @see #RENDERMODE_CONTINUOUSLY
     * @see #RENDERMODE_WHEN_DIRTY
     */
    public void setRenderMode(int renderMode) {
        glThread.setRenderMode(renderMode);
    }

    /**
     * Get the current rendering mode. May be called
     * from any thread. Must not be called before a renderer has been set.
     *
     * @return the current rendering mode.
     * @see #RENDERMODE_CONTINUOUSLY
     * @see #RENDERMODE_WHEN_DIRTY
     */
    public int getRenderMode() {
        return glThread.getRenderMode();
    }

    /**
     * Request that the renderer render a frame.
     * This method is typically used when the render mode has been set to
     * {@link #RENDERMODE_WHEN_DIRTY}, so that frames are only rendered on demand.
     * May be called
     * from any thread. Must not be called before a renderer has been set.
     */
    public void requestRender() {
        glThread.requestRender();
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is
     * not normally called or subclassed by clients of GLTextureView.
     */
    public void surfaceCreated(SurfaceTexture texture) {
        glThread.surfaceCreated();
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is
     * not normally called or subclassed by clients of GLTextureView.
     */
    public void surfaceDestroyed(SurfaceTexture texture) {
        // Surface will be destroyed when we return
        glThread.surfaceDestroyed();
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is
     * not normally called or subclassed by clients of GLTextureView.
     */
    public void surfaceChanged(SurfaceTexture texture, int format, int w, int h) {
        glThread.onWindowResize(w, h);
    }

    /**
     * Inform the view that the activity is paused. The owner of this view must
     * call this method when the activity is paused. Calling this method will
     * pause the rendering thread.
     * Must not be called before a renderer has been set.
     */
    public void onPause() {
        glThread.onPause();
    }

    /**
     * Inform the view that the activity is resumed. The owner of this view must
     * call this method when the activity is resumed. Calling this method will
     * recreate the OpenGL display and resume the rendering
     * thread.
     * Must not be called before a renderer has been set.
     */
    public void onResume() {
        glThread.onResume();
    }

    /**
     * Queue a runnable to be run on the GL rendering thread. This can be used
     * to communicate with the Renderer on the rendering thread.
     * Must not be called before a renderer has been set.
     *
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void queueEvent(Runnable r) {
        glThread.queueEvent(r);
    }

    /**
     * This method is used as part of the View class and is not normally
     * called or subclassed by clients of GLTextureView.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (LOG_ATTACH_DETACH) {
            Log.d(TAG, "onAttachedToWindow reattach =" + detached);
        }
        if (detached && (renderer != null)) {
            int renderMode = RENDERMODE_CONTINUOUSLY;
            if (glThread != null) {
                renderMode = glThread.getRenderMode();
            }
            glThread = new GLThread(mThisWeakRef);
            if (renderMode != RENDERMODE_CONTINUOUSLY) {
                glThread.setRenderMode(renderMode);
            }
            glThread.start();
        }
        detached = false;
    }

    /**
     * This method is used as part of the View class and is not normally
     * called or subclassed by clients of GLTextureView.
     * Must not be called before a renderer has been set.
     */
    @Override
    protected void onDetachedFromWindow() {
        if (LOG_ATTACH_DETACH) {
            Log.d(TAG, "onDetachedFromWindow");
        }
        if (glThread != null) {
            glThread.requestExitAndWait();
        }
        detached = true;
        super.onDetachedFromWindow();
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        surfaceChanged(getSurfaceTexture(), 0, right - left, bottom - top);
    }

    public void addSurfaceTextureListener(SurfaceTextureListener listener) {
        surfaceTextureListeners.add(listener);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        surfaceCreated(surface);
        surfaceChanged(surface, 0, width, height);

        for (SurfaceTextureListener l : surfaceTextureListeners) {
            l.onSurfaceTextureAvailable(surface, width, height);
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        surfaceChanged(surface, 0, width, height);

        for (SurfaceTextureListener l : surfaceTextureListeners) {
            l.onSurfaceTextureSizeChanged(surface, width, height);
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surfaceDestroyed(surface);

        for (SurfaceTextureListener l : surfaceTextureListeners) {
            l.onSurfaceTextureDestroyed(surface);
        }

        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        requestRender();

        for (SurfaceTextureListener l : surfaceTextureListeners) {
            l.onSurfaceTextureUpdated(surface);
        }
    }

    // ----------------------------------------------------------------------

    /**
     * A generic renderer interface.
     * <p>
     * The renderer is responsible for making OpenGL calls to render a frame.
     * <p>
     * GLTextureView clients typically create their own classes that implement
     * this interface, and then call {@link GLTextureView#setRenderer} to
     * register the renderer with the GLTextureView.
     * <p>
     *
     * <div class="special reference">
     * <h3>Developer Guides</h3>
     * <p>For more information about how to use OpenGL, read the
     * <a href="{@docRoot}guide/topics/graphics/opengl.html">OpenGL</a> developer guide.</p>
     * </div>
     *
     * <h3>Threading</h3>
     * The renderer will be called on a separate thread, so that rendering
     * performance is decoupled from the UI thread. Clients typically need to
     * communicate with the renderer from the UI thread, because that's where
     * input events are received. Clients can communicate using any of the
     * standard Java techniques for cross-thread communication, or they can
     * use the {@link GLTextureView#queueEvent(Runnable)} convenience method.
     * <p>
     * <h3>EGL Context Lost</h3>
     * There are situations where the EGL rendering context will be lost. This
     * typically happens when device wakes up after going to sleep. When
     * the EGL context is lost, all OpenGL resources (such as textures) that are
     * associated with that context will be automatically deleted. In order to
     * keep rendering correctly, a renderer must recreate any lost resources
     * that it still needs.
     *
     * @see #setRenderer(Renderer)
     */
    public interface Renderer {
        /**
         * Called when the surface is created or recreated.
         * <p>
         * Called when the rendering thread
         * starts and whenever the EGL context is lost. The EGL context will typically
         * be lost when the Android device awakes after going to sleep.
         * <p>
         * Since this method is called at the beginning of rendering, as well as
         * every time the EGL context is lost, this method is a convenient place to put
         * code to create resources that need to be created when the rendering
         * starts, and that need to be recreated when the EGL context is lost.
         * Textures are an example of a resource that you might want to create
         * here.
         * <p>
         * Note that when the EGL context is lost, all OpenGL resources associated
         * with that context will be automatically deleted. You do not need to call
         * the corresponding "glDelete" methods such as glDeleteTextures to
         * manually delete these lost resources.
         * <p>
         *
         * @param config the EGLConfig of the created surface. Can be used
         *               to create matching pbuffers.
         */
        void onSurfaceCreated(EGLConfig config);

        /**
         * Called when the surface changed size.
         * <p>
         * Called after the surface is created and whenever
         * the OpenGL ES surface size changes.
         * <p>
         * Typically you will set your viewport here. If your camera
         * is fixed then you could also set your projection matrix here:
         * <pre class="prettyprint">
         * void onSurfaceChanged(GL10 gl, int width, int height) {
         * gl.glViewport(0, 0, width, height);
         * // for a fixed camera, set the projection too
         * float ratio = (float) width / height;
         * gl.glMatrixMode(GL10.GL_PROJECTION);
         * gl.glLoadIdentity();
         * gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
         * }
         * </pre>
         */
        void onSurfaceChanged(int width, int height);

        /**
         * Called to onDraw the current frame.
         * <p>
         * This method is responsible for drawing the current frame.
         * <p>
         * The implementation of this method typically looks like this:
         * <pre class="prettyprint">
         * void onDrawFrame(GL10 gl) {
         * gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
         * //... other gl calls to render the scene ...
         * }
         * </pre>
         */
        void onDrawFrame();
    }

    /**
     * An interface for customizing the eglCreateContext and eglDestroyContext calls.
     * <p>
     * This interface must be implemented by clients wishing to call
     * {@link GLTextureView#setEGLContextFactory(EGLContextFactory)}
     */
    public interface EGLContextFactory {
        EGLContext createContext(EGLDisplay display, EGLConfig eglConfig);

        void destroyContext(EGLDisplay display, EGLContext context);
    }

    private class DefaultContextFactory implements EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGLDisplay display, EGLConfig config) {
            int[] attrib_list = {
                    EGL_CONTEXT_CLIENT_VERSION, EGLContextClientVersion, EGL14.EGL_NONE
            };

            return EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT,
                    EGLContextClientVersion != 0 ? attrib_list : null, 0);
        }

        public void destroyContext(EGLDisplay display, EGLContext context) {
            if (!EGL14.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
                if (LOG_THREADS) {
                    Log.i("DefaultContextFactory", "tid=" + Thread.currentThread().getId());
                }
                EglHelper.throwEglException("eglDestroyContex", EGL14.eglGetError());
            }
        }
    }

    /**
     * An interface for customizing the eglCreateWindowSurface and eglDestroySurface calls.
     * <p>
     * This interface must be implemented by clients wishing to call
     * {@link GLTextureView#setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory)}
     */
    public interface EGLWindowSurfaceFactory {
        /**
         * @return null if the surface cannot be constructed.
         */
        EGLSurface createWindowSurface(EGLDisplay display, EGLConfig config,
                                       Object nativeWindow);

        void destroySurface(EGLDisplay display, EGLSurface surface);
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {

        public EGLSurface createWindowSurface(EGLDisplay display, EGLConfig config,
                                              Object nativeWindow) {
            EGLSurface result = null;
            try {
                int[] surfaceAttribs = {EGL14.EGL_NONE};
                result = EGL14.eglCreateWindowSurface(display, config, nativeWindow, surfaceAttribs, 0);
            } catch (IllegalArgumentException e) {
                // This exception indicates that the surface flinger surface
                // is not valid. This can happen if the surface flinger surface has
                // been torn down, but the application has not yet been
                // notified via SurfaceHolder.Callback.surfaceDestroyed.
                // In theory the application should be notified first,
                // but in practice sometimes it is not. See b/4588890
                Log.e(TAG, "eglCreateWindowSurface", e);
            }
            return result;
        }

        public void destroySurface(EGLDisplay display, EGLSurface surface) {
            EGL14.eglDestroySurface(display, surface);
        }
    }

    /**
     * An interface for choosing an EGLConfig configuration from a list of
     * potential configurations.
     * <p>
     * This interface must be implemented by clients wishing to call
     * {@link GLTextureView#setEGLConfigChooser(EGLConfigChooser)}
     */
    public interface EGLConfigChooser {
        /**
         * Choose a configuration from the list. Implementors typically
         * implement this method by calling
         * {@link EGL14#eglChooseConfig} and iterating through the results. Please consult the
         * EGL specification available from The Khronos Group to learn how to call eglChooseConfig.
         *
         * @param display the current display.
         * @return the chosen configuration.
         */
        EGLConfig chooseConfig(EGLDisplay display);
    }

    private abstract class BaseConfigChooser implements EGLConfigChooser {
        public BaseConfigChooser(int[] configSpec) {
            mConfigSpec = filterConfigSpec(configSpec);
        }

        public EGLConfig chooseConfig(EGLDisplay display) {
            int[] num_config = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (!EGL14.eglChooseConfig(display, mConfigSpec, 0, configs, 0, configs.length, num_config, 0)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            EGLConfig config = chooseConfig(display, configs);
            if (config == null) {
                throw new IllegalArgumentException("No config chosen");
            }
            return config;
        }

        abstract EGLConfig chooseConfig(EGLDisplay display, EGLConfig[] configs);

        protected int[] mConfigSpec;

        private int[] filterConfigSpec(int[] configSpec) {
            if (EGLContextClientVersion != 2) {
                return configSpec;
            }
            /* We know none of the subclasses define EGL_RENDERABLE_TYPE.
             * And we know the configSpec is well formed.
             */
            int len = configSpec.length;
            int[] newConfigSpec = new int[len + 2];
            System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1);
            newConfigSpec[len - 1] = EGL14.EGL_RENDERABLE_TYPE;
            newConfigSpec[len] = 0x0004; /* EGL_OPENGL_ES2_BIT */
            newConfigSpec[len + 1] = EGL14.EGL_NONE;
            return newConfigSpec;
        }
    }

    /**
     * Choose a configuration with exactly the specified r,g,b,a sizes,
     * and at least the specified depth and stencil sizes.
     */
    private class ComponentSizeChooser extends BaseConfigChooser {
        public ComponentSizeChooser(int redSize, int greenSize, int blueSize, int alphaSize,
                                    int depthSize, int stencilSize) {
            super(new int[]{
                    EGL14.EGL_RED_SIZE, redSize, EGL14.EGL_GREEN_SIZE, greenSize, EGL14.EGL_BLUE_SIZE,
                    blueSize, EGL14.EGL_ALPHA_SIZE, alphaSize, EGL14.EGL_DEPTH_SIZE, depthSize,
                    EGL14.EGL_STENCIL_SIZE, stencilSize, EGL14.EGL_NONE
            });
            value = new int[1];
            this.redSize = redSize;
            this.greenSize = greenSize;
            this.blueSize = blueSize;
            this.alphaSize = alphaSize;
            this.depthSize = depthSize;
            this.stencilSize = stencilSize;
        }

        @Override
        public EGLConfig chooseConfig(EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(display, config, EGL14.EGL_DEPTH_SIZE, 0);
                int s = findConfigAttrib(display, config, EGL14.EGL_STENCIL_SIZE, 0);
                if ((d >= depthSize) && (s >= stencilSize)) {
                    int r = findConfigAttrib(display, config, EGL14.EGL_RED_SIZE, 0);
                    int g = findConfigAttrib(display, config, EGL14.EGL_GREEN_SIZE, 0);
                    int b = findConfigAttrib(display, config, EGL14.EGL_BLUE_SIZE, 0);
                    int a = findConfigAttrib(display, config, EGL14.EGL_ALPHA_SIZE, 0);
                    if ((r == redSize) && (g == greenSize) && (b == blueSize) && (a == alphaSize)) {
                        return config;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGLDisplay display, EGLConfig config, int attribute,
                                     int defaultValue) {

            if (EGL14.eglGetConfigAttrib(display, config, attribute, value, 0)) {
                return value[0];
            }
            return defaultValue;
        }

        private int[] value;
        // Subclasses can adjust these values:
        protected int redSize;
        protected int greenSize;
        protected int blueSize;
        protected int alphaSize;
        protected int depthSize;
        protected int stencilSize;
    }

    /**
     * This class will choose a RGB_888 surface with
     * or without a depth buffer.
     */
    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean withDepthBuffer) {
            super(8, 8, 8, 8, withDepthBuffer ? 16 : 0, 0);
        }
    }

    /**
     * An EGL helper class.
     */

    private static class EglHelper {
        public EglHelper(WeakReference<GLTextureView> glTextureViewWeakReference) {
            this.glTextureViewWeakRef = glTextureViewWeakReference;
        }

        /**
         * Initialize EGL for a given configuration spec.
         */
        public void start() {
            if (LOG_EGL) {
                Log.w("EglHelper", "start() tid=" + Thread.currentThread().getId());
            }

            /*
             * Get to the default display.
             */
            eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

            if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }

            /*
             * We can now initialize EGL for that display
             */
            int[] version = new int[2];
            if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
                throw new RuntimeException("eglInitialize failed");
            }
            GLTextureView view = glTextureViewWeakRef.get();
            if (view == null) {
                eglConfig = null;
                EGLContext = null;
            } else {
                eglConfig = view.eglConfigChooser.chooseConfig(eglDisplay);

                /*
                 * Create an EGL context. We want to do this as rarely as we can, because an
                 * EGL context is a somewhat heavy object.
                 */
                EGLContext = view.EGLContextFactory.createContext(eglDisplay, eglConfig);
            }
            if (EGLContext == null || EGLContext == EGL14.EGL_NO_CONTEXT) {
                EGLContext = null;
                throwEglException("createContext");
            }
            if (LOG_EGL) {
                Log.w("EglHelper",
                        "createContext " + EGLContext + " tid=" + Thread.currentThread().getId());
            }

            eglSurface = null;
        }

        /**
         * Create an egl surface for the current SurfaceHolder surface. If a surface
         * already exists, destroy it before creating the new surface.
         *
         * @return true if the surface was created successfully.
         */
        public boolean createSurface() {
            if (LOG_EGL) {
                Log.w("EglHelper", "createSurface()  tid=" + Thread.currentThread().getId());
            }
            if (eglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (eglConfig == null) {
                throw new RuntimeException("eglConfig not initialized");
            }

            /*
             *  The window size has changed, so we need to create a new
             *  surface.
             */
            destroySurfaceImp();

            /*
             * Create an EGL surface we can render into.
             */
            GLTextureView view = glTextureViewWeakRef.get();
            if (view != null) {
                eglSurface = view.eglWindowSurfaceFactory.createWindowSurface(eglDisplay, eglConfig,
                        view.getSurfaceTexture());
            } else {
                eglSurface = null;
            }

            if (eglSurface == null || eglSurface == EGL14.EGL_NO_SURFACE) {
                int error = EGL14.eglGetError();
                if (error == EGL14.EGL_BAD_NATIVE_WINDOW) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                return false;
            }

            /*
             * Before we can issue GL commands, we need to make sure
             * the context is current and bound to a surface.
             */
            if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, EGLContext)) {
                /*
                 * Could not make the context current, probably because the underlying
                 * TextureView surface has been destroyed.
                 */
                logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", EGL14.eglGetError());
                return false;
            }

            return true;
        }


        /**
         * Display the current render surface.
         *
         * @return the EGL error code from eglSwapBuffers.
         */
        public int swap() {
            if (!EGL14.eglSwapBuffers(eglDisplay, eglSurface)) {
                return EGL14.eglGetError();
            }
            return EGL14.EGL_SUCCESS;
        }

        public void destroySurface() {
            if (LOG_EGL) {
                Log.w("EglHelper", "destroySurface()  tid=" + Thread.currentThread().getId());
            }
            destroySurfaceImp();
        }

        private void destroySurfaceImp() {
            if (eglSurface != null && eglSurface != EGL14.EGL_NO_SURFACE) {
                EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT);
                GLTextureView view = glTextureViewWeakRef.get();
                if (view != null) {
                    view.eglWindowSurfaceFactory.destroySurface(eglDisplay, eglSurface);
                }
                eglSurface = null;
            }
        }

        public void finish() {
            if (LOG_EGL) {
                Log.w("EglHelper", "finish() tid=" + Thread.currentThread().getId());
            }
            if (EGLContext != null) {
                GLTextureView view = glTextureViewWeakRef.get();
                if (view != null) {
                    view.EGLContextFactory.destroyContext(eglDisplay, EGLContext);
                }
                EGLContext = null;
            }
            if (eglDisplay != null) {
                EGL14.eglTerminate(eglDisplay);
                eglDisplay = null;
            }
        }

        private void throwEglException(String function) {
            throwEglException(function, EGL14.eglGetError());
        }

        public static void throwEglException(String function, int error) {
            String message = formatEglError(function, error);
            if (LOG_THREADS) {
                Log.e("EglHelper",
                        "throwEglException tid=" + Thread.currentThread().getId() + " " + message);
            }
            throw new RuntimeException(message);
        }

        public static void logEglErrorAsWarning(String tag, String function, int error) {
            Log.w(tag, formatEglError(function, error));
        }

        public static String formatEglError(String function, int error) {
            return function + " failed: " + error;
        }

        private WeakReference<GLTextureView> glTextureViewWeakRef;
        EGLDisplay eglDisplay;
        EGLSurface eglSurface;
        EGLConfig eglConfig;
        android.opengl.EGLContext EGLContext;
    }

    /**
     * A generic GL Thread. Takes care of initializing EGL and GL. Delegates
     * to a Renderer instance to do the actual drawing. Can be configured to
     * render continuously or on request.
     * <p>
     * All potentially blocking synchronization is done through the
     * glThreadManager object. This avoids multiple-lock ordering issues.
     */
    static class GLThread extends Thread {
        GLThread(WeakReference<GLTextureView> glTextureViewWeakRef) {
            super();
            width = 0;
            height = 0;
            requestRender = true;
            renderMode = RENDERMODE_CONTINUOUSLY;
            this.glTextureViewWeakRef = glTextureViewWeakRef;
        }

        @Override
        public void run() {
            setName("GLThread " + getId());
            if (LOG_THREADS) {
                Log.i("GLThread", "starting tid=" + getId());
            }

            try {
                guardedRun();
            } catch (InterruptedException e) {
                // fall thru and exit normally
            } finally {
                glThreadManager.threadExiting(this);
            }
        }

        /*
         * This private method should only be called inside a
         * synchronized(glThreadManager) block.
         */
        private void stopEglSurfaceLocked() {
            if (haveEglSurface) {
                haveEglSurface = false;
                eglHelper.destroySurface();
            }
        }

        /*
         * This private method should only be called inside a
         * synchronized(glThreadManager) block.
         */
        private void stopEGLContextLocked() {
            if (haveEGLContext) {
                eglHelper.finish();
                haveEGLContext = false;
                glThreadManager.releaseEGLContextLocked(this);
            }
        }

        private void guardedRun() throws InterruptedException {
            eglHelper = new EglHelper(glTextureViewWeakRef);
            haveEGLContext = false;
            haveEglSurface = false;
            try {
                boolean createEGLContext = false;
                boolean createEglSurface = false;
                boolean createGlInterface = false;
                boolean lostEGLContext = false;
                boolean sizeChanged = false;
                boolean wantRenderNotification = false;
                boolean doRenderNotification = false;
                boolean askedToReleaseEGLContext = false;
                int w = 0;
                int h = 0;
                Runnable event = null;

                while (true) {
                    synchronized (glThreadManager) {
                        while (true) {
                            if (shouldExit) {
                                return;
                            }

                            if (!eventQueue.isEmpty()) {
                                event = eventQueue.remove(0);
                                break;
                            }

                            // Update the pause state.
                            boolean pausing = false;
                            if (paused != requestPaused) {
                                pausing = requestPaused;
                                paused = requestPaused;
                                glThreadManager.notifyAll();
                                if (LOG_PAUSE_RESUME) {
                                    Log.i("GLThread", "paused is now " + paused + " tid=" + getId());
                                }
                            }

                            // Do we need to give up the EGL context?
                            if (shouldReleaseEGLContext) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "releasing EGL context because asked to tid=" + getId());
                                }
                                stopEglSurfaceLocked();
                                stopEGLContextLocked();
                                shouldReleaseEGLContext = false;
                                askedToReleaseEGLContext = true;
                            }

                            // Have we lost the EGL context?
                            if (lostEGLContext) {
                                stopEglSurfaceLocked();
                                stopEGLContextLocked();
                                lostEGLContext = false;
                            }

                            // When pausing, release the EGL surface:
                            if (pausing && haveEglSurface) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "releasing EGL surface because paused tid=" + getId());
                                }
                                stopEglSurfaceLocked();
                            }

                            // When pausing, optionally release the EGL Context:
                            if (pausing && haveEGLContext) {
                                GLTextureView view = glTextureViewWeakRef.get();
                                boolean preserveEGLContextOnPause =
                                        view == null ? false : view.preserveEGLContextOnPause;
                                if (!preserveEGLContextOnPause
                                        || glThreadManager.shouldReleaseEGLContextWhenPausing()) {
                                    stopEGLContextLocked();
                                    if (LOG_SURFACE) {
                                        Log.i("GLThread", "releasing EGL context because paused tid=" + getId());
                                    }
                                }
                            }

                            // When pausing, optionally terminate EGL:
                            if (pausing) {
                                if (glThreadManager.shouldTerminateEGLWhenPausing()) {
                                    eglHelper.finish();
                                    if (LOG_SURFACE) {
                                        Log.i("GLThread", "terminating EGL because paused tid=" + getId());
                                    }
                                }
                            }

                            // Have we lost the TextureView surface?
                            if ((!hasSurface) && (!waitingForSurface)) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "noticed textureView surface lost tid=" + getId());
                                }
                                if (haveEglSurface) {
                                    stopEglSurfaceLocked();
                                }
                                waitingForSurface = true;
                                surfaceIsBad = false;
                                glThreadManager.notifyAll();
                            }

                            // Have we acquired the surface view surface?
                            if (hasSurface && waitingForSurface) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "noticed textureView surface acquired tid=" + getId());
                                }
                                waitingForSurface = false;
                                glThreadManager.notifyAll();
                            }

                            if (doRenderNotification) {
                                if (LOG_SURFACE) {
                                    Log.i("GLThread", "sending render notification tid=" + getId());
                                }
                                wantRenderNotification = false;
                                doRenderNotification = false;
                                renderComplete = true;
                                glThreadManager.notifyAll();
                            }

                            // Ready to onDraw?
                            if (readyToDraw()) {

                                // If we don't have an EGL context, try to acquire one.
                                if (!haveEGLContext) {
                                    if (askedToReleaseEGLContext) {
                                        askedToReleaseEGLContext = false;
                                    } else if (glThreadManager.tryAcquireEGLContextLocked(this)) {
                                        try {
                                            eglHelper.start();
                                        } catch (RuntimeException t) {
                                            glThreadManager.releaseEGLContextLocked(this);
                                            throw t;
                                        }
                                        haveEGLContext = true;
                                        createEGLContext = true;

                                        glThreadManager.notifyAll();
                                    }
                                }

                                if (haveEGLContext && !haveEglSurface) {
                                    haveEglSurface = true;
                                    createEglSurface = true;
                                    createGlInterface = true;
                                    sizeChanged = true;
                                }

                                if (haveEglSurface) {
                                    if (this.sizeChanged) {
                                        sizeChanged = true;
                                        w = width;
                                        h = height;
                                        wantRenderNotification = true;
                                        if (LOG_SURFACE) {
                                            Log.i("GLThread", "noticing that we want render notification tid=" + getId());
                                        }

                                        // Destroy and recreate the EGL surface.
                                        createEglSurface = true;

                                        this.sizeChanged = false;
                                    }
                                    requestRender = false;
                                    glThreadManager.notifyAll();
                                    break;
                                }
                            }

                            // By design, this is the only place in a GLThread thread where we wait().
                            if (LOG_THREADS) {
                                Log.i("GLThread", "waiting tid=" + getId() + " haveEGLContext: " + haveEGLContext
                                        + " haveEglSurface: " + haveEglSurface + " paused: " + paused + " hasSurface: "
                                        + hasSurface + " surfaceIsBad: " + surfaceIsBad + " waitingForSurface: "
                                        + waitingForSurface + " width: " + width + " height: " + height
                                        + " requestRender: " + requestRender + " renderMode: " + renderMode);
                            }
                            glThreadManager.wait();
                        }
                    } // end of synchronized(glThreadManager)

                    if (event != null) {
                        event.run();
                        event = null;
                        continue;
                    }

                    if (createEglSurface) {
                        if (LOG_SURFACE) {
                            Log.w("GLThread", "egl createSurface");
                        }
                        if (!eglHelper.createSurface()) {
                            synchronized (glThreadManager) {
                                surfaceIsBad = true;
                                glThreadManager.notifyAll();
                            }
                            continue;
                        }
                        createEglSurface = false;
                    }

                    if (createGlInterface) {

                        glThreadManager.checkGLDriver();
                        createGlInterface = false;
                    }

                    if (createEGLContext) {
                        if (LOG_RENDERER) {
                            Log.w("GLThread", "onSurfaceCreated");
                        }
                        GLTextureView view = glTextureViewWeakRef.get();
                        if (view != null) {
                            view.renderer.onSurfaceCreated(eglHelper.eglConfig);
                        }
                        createEGLContext = false;
                    }

                    if (sizeChanged) {
                        if (LOG_RENDERER) {
                            Log.w("GLThread", "onSurfaceChanged(" + w + ", " + h + ")");
                        }
                        GLTextureView view = glTextureViewWeakRef.get();
                        if (view != null) {
                            view.renderer.onSurfaceChanged(w, h);
                        }
                        sizeChanged = false;
                    }

                    if (LOG_RENDERER_DRAW_FRAME) {
                        Log.w("GLThread", "onDrawFrame tid=" + getId());
                    }
                    {
                        GLTextureView view = glTextureViewWeakRef.get();
                        if (view != null) {
                            view.renderer.onDrawFrame();
                        }
                    }
                    int swapError = eglHelper.swap();
                    switch (swapError) {
                        case EGL14.EGL_SUCCESS:
                            break;
                        case EGL14.EGL_CONTEXT_LOST:
                            if (LOG_SURFACE) {
                                Log.i("GLThread", "egl context lost tid=" + getId());
                            }
                            lostEGLContext = true;
                            break;
                        default:
                            // Other errors typically mean that the current surface is bad,
                            // probably because the TextureView surface has been destroyed,
                            // but we haven't been notified yet.
                            // Log the error to help developers understand why rendering stopped.
                            EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError);

                            synchronized (glThreadManager) {
                                surfaceIsBad = true;
                                glThreadManager.notifyAll();
                            }
                            break;
                    }

                    if (wantRenderNotification) {
                        doRenderNotification = true;
                    }
                }
            } finally {
                /*
                 * clean-up everything...
                 */
                synchronized (glThreadManager) {
                    stopEglSurfaceLocked();
                    stopEGLContextLocked();
                }
            }
        }

        public boolean ableToDraw() {
            return haveEGLContext && haveEglSurface && readyToDraw();
        }

        private boolean readyToDraw() {
            return (!paused) && hasSurface && (!surfaceIsBad) && (width > 0) && (height > 0) && (
                    requestRender || (renderMode == RENDERMODE_CONTINUOUSLY));
        }

        public void setRenderMode(int renderMode) {
            if (!((RENDERMODE_WHEN_DIRTY <= renderMode) && (renderMode <= RENDERMODE_CONTINUOUSLY))) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (glThreadManager) {
                this.renderMode = renderMode;
                glThreadManager.notifyAll();
            }
        }

        public int getRenderMode() {
            synchronized (glThreadManager) {
                return renderMode;
            }
        }

        public void requestRender() {
            synchronized (glThreadManager) {
                requestRender = true;
                glThreadManager.notifyAll();
            }
        }

        public void surfaceCreated() {
            synchronized (glThreadManager) {
                if (LOG_THREADS) {
                    Log.i("GLThread", "surfaceCreated tid=" + getId());
                }
                hasSurface = true;
                glThreadManager.notifyAll();
                while ((waitingForSurface) && (!exited)) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void surfaceDestroyed() {
            synchronized (glThreadManager) {
                if (LOG_THREADS) {
                    Log.i("GLThread", "surfaceDestroyed tid=" + getId());
                }
                hasSurface = false;
                glThreadManager.notifyAll();
                while ((!waitingForSurface) && (!exited)) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (glThreadManager) {
                if (LOG_PAUSE_RESUME) {
                    Log.i("GLThread", "onPause tid=" + getId());
                }
                requestPaused = true;
                glThreadManager.notifyAll();
                while ((!exited) && (!paused)) {
                    if (LOG_PAUSE_RESUME) {
                        Log.i("Main thread", "onPause waiting for paused.");
                    }
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (glThreadManager) {
                if (LOG_PAUSE_RESUME) {
                    Log.i("GLThread", "onResume tid=" + getId());
                }
                requestPaused = false;
                requestRender = true;
                renderComplete = false;
                glThreadManager.notifyAll();
                while ((!exited) && paused && (!renderComplete)) {
                    if (LOG_PAUSE_RESUME) {
                        Log.i("Main thread", "onResume waiting for !paused.");
                    }
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onWindowResize(int w, int h) {
            synchronized (glThreadManager) {
                width = w;
                height = h;
                sizeChanged = true;
                requestRender = true;
                renderComplete = false;
                glThreadManager.notifyAll();

                // Wait for thread to react to resize and render a frame
                while (!exited && !paused && !renderComplete && ableToDraw()) {
                    if (LOG_SURFACE) {
                        Log.i("Main thread", "onWindowResize waiting for render complete from tid=" + getId());
                    }
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestExitAndWait() {
            // don't call this from GLThread thread or it is a guaranteed
            // deadlock!
            synchronized (glThreadManager) {
                shouldExit = true;
                glThreadManager.notifyAll();
                while (!exited) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestReleaseEGLContextLocked() {
            shouldReleaseEGLContext = true;
            glThreadManager.notifyAll();
        }

        /**
         * Queue an "event" to be run on the GL rendering thread.
         *
         * @param r the runnable to be run on the GL rendering thread.
         */
        public void queueEvent(Runnable r) {
            if (r == null) {
                throw new IllegalArgumentException("r must not be null");
            }
            synchronized (glThreadManager) {
                eventQueue.add(r);
                glThreadManager.notifyAll();
            }
        }

        // Once the thread is started, all accesses to the following member
        // variables are protected by the glThreadManager monitor
        private boolean shouldExit;
        private boolean exited;
        private boolean requestPaused;
        private boolean paused;
        private boolean hasSurface;
        private boolean surfaceIsBad;
        private boolean waitingForSurface;
        private boolean haveEGLContext;
        private boolean haveEglSurface;
        private boolean shouldReleaseEGLContext;
        private int width;
        private int height;
        private int renderMode;
        private boolean requestRender;
        private boolean renderComplete;
        private ArrayList<Runnable> eventQueue = new ArrayList<>();
        private boolean sizeChanged = true;

        // End of member variables protected by the glThreadManager monitor.

        private EglHelper eglHelper;

        /**
         * Set once at thread construction time, nulled out when the parent view is garbage
         * called. This weak reference allows the GLTextureView to be garbage collected while
         * the GLThread is still alive.
         */
        private WeakReference<GLTextureView> glTextureViewWeakRef;
    }

    static class LogWriter extends Writer {

        @Override
        public void close() {
            flushBuilder();
        }

        @Override
        public void flush() {
            flushBuilder();
        }

        @Override
        public void write(char[] buf, int offset, int count) {
            for (int i = 0; i < count; i++) {
                char c = buf[offset + i];
                if (c == '\n') {
                    flushBuilder();
                } else {
                    builder.append(c);
                }
            }
        }

        private void flushBuilder() {
            if (builder.length() > 0) {
                Log.v("GLTextureView", builder.toString());
                builder.delete(0, builder.length());
            }
        }

        private StringBuilder builder = new StringBuilder();
    }

    private void checkRenderThreadState() {
        if (glThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    private static class GLThreadManager {
        private static String TAG = "GLThreadManager";

        public synchronized void threadExiting(GLThread thread) {
            if (LOG_THREADS) {
                Log.i("GLThread", "exiting tid=" + thread.getId());
            }
            thread.exited = true;
            if (eglOwner == thread) {
                eglOwner = null;
            }
            notifyAll();
        }

        /*
         * Tries once to acquire the right to use an EGL
         * context. Does not block. Requires that we are already
         * in the glThreadManager monitor when this is called.
         *
         * @return true if the right to use an EGL context was acquired.
         */
        public boolean tryAcquireEGLContextLocked(GLThread thread) {
            if (eglOwner == thread || eglOwner == null) {
                eglOwner = thread;
                notifyAll();
                return true;
            }
            checkGLESVersion();
            if (multipleGLESContextsAllowed) {
                return true;
            }
            // Notify the owning thread that it should release the context.
            // TODO: implement a fairness policy. Currently
            // if the owning thread is drawing continuously it will just
            // reacquire the EGL context.
            if (eglOwner != null) {
                eglOwner.requestReleaseEGLContextLocked();
            }
            return false;
        }

        /*
         * Releases the EGL context. Requires that we are already in the
         * glThreadManager monitor when this is called.
         */
        public void releaseEGLContextLocked(GLThread thread) {
            if (eglOwner == thread) {
                eglOwner = null;
            }
            notifyAll();
        }

        public synchronized boolean shouldReleaseEGLContextWhenPausing() {
            // Release the EGL context when pausing even if
            // the hardware supports multiple EGL contexts.
            // Otherwise the device could run out of EGL contexts.
            return limitedGLESContexts;
        }

        public synchronized boolean shouldTerminateEGLWhenPausing() {
            checkGLESVersion();
            return !multipleGLESContextsAllowed;
        }

        public synchronized void checkGLDriver() {
            if (!glesDriverCheckComplete) {
                checkGLESVersion();
                String renderer = GLES30.glGetString(GLES10.GL_RENDERER);
                if (glesVersion < kGLES_20) {
                    multipleGLESContextsAllowed = !renderer.startsWith(kMSM7K_RENDERER_PREFIX);
                    notifyAll();
                }
                limitedGLESContexts = !multipleGLESContextsAllowed;
                if (LOG_SURFACE) {
                    Log.w(TAG, "checkGLDriver renderer = \"" + renderer + "\" multipleContextsAllowed = "
                            + multipleGLESContextsAllowed + " limitedGLESContexts = " + limitedGLESContexts);
                }
                glesDriverCheckComplete = true;
            }
        }

        private void checkGLESVersion() {
            if (!glesVersionCheckComplete) {
                glesVersionCheckComplete = true;
            }
        }

        /**
         * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides
         * support for hardware-accelerated views, therefore multiple EGL contexts are
         * supported on all Android 3.0+ EGL drivers.
         */
        private boolean glesVersionCheckComplete;
        private int glesVersion;
        private boolean glesDriverCheckComplete;
        private boolean multipleGLESContextsAllowed;
        private boolean limitedGLESContexts;
        private static final int kGLES_20 = 0x20000;
        private static final String kMSM7K_RENDERER_PREFIX = "Q3Dimension MSM7500 ";
        private GLThread eglOwner;
    }

    private static final GLThreadManager glThreadManager = new GLThreadManager();

    private final WeakReference<GLTextureView> mThisWeakRef = new WeakReference<>(this);
    private GLThread glThread;
    private Renderer renderer;
    private boolean detached;
    private EGLConfigChooser eglConfigChooser;
    private EGLContextFactory EGLContextFactory;
    private EGLWindowSurfaceFactory eglWindowSurfaceFactory;
    private int debugFlags;
    private int EGLContextClientVersion;
    private boolean preserveEGLContextOnPause;
    private List<SurfaceTextureListener> surfaceTextureListeners = new ArrayList<>();
}
