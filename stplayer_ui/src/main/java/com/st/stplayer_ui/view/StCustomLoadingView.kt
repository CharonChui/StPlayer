package com.st.stplayer_ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.st.stplayer_ui.R

class StCustomLoadingView(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int
) :
    LinearLayout(context, attrs, defStyleAttr) {
    private var mRootView: View? = null
    private var mSpeedText: TextView? = null

    constructor(context: Context?, attrs: AttributeSet?) : this(
        context,
        null,
        0
    )

    constructor(context: Context?) : this(
        context,
        null,
        0
    )

    private fun init(context: Context?) {
        mRootView = View.inflate(context, R.layout.st_view_loading, this)
        mSpeedText = mRootView?.findViewById(R.id.st_ui_view_loading_speed)
    }

    fun setSpeedText(speedText: String?) {
        if (mSpeedText != null) {
            mSpeedText!!.text = speedText
        }
    }

    init {
        init(context)
    }
}