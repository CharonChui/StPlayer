package com.st.stplayer_floatwindow.util

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout

class ViewUtil private constructor() {
    companion object {
        /**
         * Measure the height of the view will be when showing.
         *
         * @param view View to measure.
         */
        fun measureView(view: View) {
            var lp = view.layoutParams
            if (lp == null) {
                lp = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
            val childMeasureHeight: Int
            childMeasureHeight = if (lp.height > 0) {
                MeasureSpec.makeMeasureSpec(
                    lp.height,
                    MeasureSpec.EXACTLY
                )
            } else {
                // Measure specification mode: The parent has not imposed any
                // constraint on the child. It can be whatever size it wants.
                MeasureSpec.makeMeasureSpec(
                    0,
                    MeasureSpec.UNSPECIFIED
                )
            }
            view.measure(childMeasureWidth, childMeasureHeight)
        }


        fun removeViewFormParent(v: View?) {
            if (v == null) {
                return
            }
            val parent = v.parent
            if (parent is FrameLayout) {
                parent.removeView(v)
            } else if (parent is LinearLayout) {
                parent.removeView(v)
            } else if (parent is RelativeLayout) {
                parent.removeView(v)
            } else if (parent is ConstraintLayout) {
                parent.removeView(v)
            }
        }
    }
}