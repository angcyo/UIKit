package com.angcyo.uiview.less.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.RApplication
import com.angcyo.uiview.less.kotlin.dpi
import com.angcyo.uiview.less.kotlin.find

object TopToast {
    private var toast: Toast? = null

    fun show(tipText: CharSequence, tipImageResId: Int) {
        show(RApplication.getApp(), tipText, tipImageResId)
    }

    fun tip(tipText: CharSequence) {
        show(tipText, R.drawable.base_tip_ico)
    }

    fun ok(tipText: CharSequence) {
        show(tipText, R.drawable.base_tip_ok)
    }

    @SuppressLint("ShowToast")
    private fun show(context: Context, tipText: CharSequence, tipImageResId: Int) {
        val layout: View
        if (toast == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT).apply {
                initToast(this)
                layout = LayoutInflater.from(context).inflate(R.layout.base_top_toast_tip, FrameLayout(context), false)
                (layout.findViewById<View>(R.id.base_toast_text_view) as TextView).text = tipText
                view = layout
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, 40 * dpi)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }
        } else {
            layout = toast!!.view
        }

        val titleView = layout.find<TextView>(R.id.base_toast_text_view)
        val imageView = layout.find<ImageView>(R.id.base_toast_image_view)

        if (titleView != null) {
            titleView.text = tipText
        }

        imageView?.visibility = if (tipImageResId <= 0) View.GONE else View.VISIBLE
        imageView?.setImageResource(tipImageResId)

        toast!!.show()
    }

    private fun initToast(toast: Toast) {
        try {
            val mTN = toast.javaClass.getDeclaredField("mTN")
            mTN.isAccessible = true
            val mTNObj = mTN.get(toast)

            val mParams = mTNObj.javaClass.getDeclaredField("mParams")
            mParams.isAccessible = true
            val params = mParams.get(mTNObj) as WindowManager.LayoutParams
            params.width = Math.min(RUtils.getScreenWidth(), RUtils.getScreenHeight()) - 40 * dpi
            params.height = -2
            //params.gravity = Gravity.TOP//无法生效, 请在Toast对象里面设置
            params.windowAnimations = R.style.BaseToastAnimation
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}