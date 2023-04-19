@file:Suppress("DEPRECATION")

package com.faceunity.fulive_plugin.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.fulive_plugin.R
import java.lang.ref.WeakReference

/**
 *
 * DESC：自定义Toast弹窗
 * Created on 2020/11/10
 *
 */
object ToastHelper {


    @JvmStatic
    fun showWhiteTextToast(context: Context, @StringRes strId: Int) {
        showWhiteTextToast(context.applicationContext, context.getString(strId))
    }

    @JvmStatic
    fun showNormalToast(context: Context, @StringRes strId: Int) {
        showNormalToast(context.applicationContext, context.getString(strId))
    }

    @JvmStatic
    fun dismissToast() {
        dismissWhiteTextToast()
        dismissNormalToast()
    }


    @JvmStatic
    fun dismissWhiteTextToast() {
        mWhiteTextToast?.let {
            it.cancel()
        }
    }

    @JvmStatic
    fun dismissNormalToast() {
        mNormalToast?.let {
            it.cancel()
        }
    }


    private var mNormalToast: Toast? = null
    private var mWhiteTextToast: Toast? = null
    private var mWeakContext: WeakReference<Context>? = null

    @JvmStatic
    fun showWhiteTextToast(context: Context, text: String?) {
        if (mWeakContext?.get() == context) {
            if (mWhiteTextToast != null) {
                val view = mWhiteTextToast!!.view as TextView
                view.text = text
                if (!view.isShown) {
                    mWhiteTextToast!!.show()
                }
                return
            }
        }
        mWeakContext = WeakReference(context)
        val resources = context.resources
        val textView = TextView(mWeakContext!!.get())
        textView.setTextColor(Color.WHITE)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(context, 64f))
        textView.text = text
        mWhiteTextToast = Toast(mWeakContext!!.get())
        mWhiteTextToast!!.view = textView
        mWhiteTextToast!!.duration = Toast.LENGTH_SHORT
        val yOffset = dp2px(context, 560f).toInt()
        mWhiteTextToast!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, yOffset)
        mWhiteTextToast!!.show()
    }


    @JvmStatic
    fun showNormalToast(context: Context, text: String?) {
        if (mWeakContext?.get() == context) {
            if (mNormalToast != null) {
                val view = mNormalToast!!.view as TextView
                view.text = text
                if (!view.isShown) {
                    mNormalToast!!.show()
                }
                return
            }
        }
        mWeakContext = WeakReference(context)
        val resources = context.resources
        val textView = TextView(mWeakContext!!.get())
        textView.setTextColor(Color.WHITE)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(context, 12f))
        textView.setBackgroundResource(R.drawable.bg_toast_more)
        val hPadding = dp2px(context, 14f).toInt()
        val vPadding = dp2px(context, 8f).toInt()
        textView.setPadding(hPadding, vPadding, hPadding, vPadding)
        textView.text = text
        mNormalToast = Toast(mWeakContext!!.get())
        mNormalToast!!.view = textView
        mNormalToast!!.duration = Toast.LENGTH_SHORT
        val yOffset = dp2px(context, 90f).toInt()
        mNormalToast!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, yOffset)
        mNormalToast!!.show()
    }

    private fun dp2px(context: Context, dp: Float): Float {
        return context.resources.displayMetrics.density * dp + 0.5f
    }

}