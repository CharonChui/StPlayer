package com.st.stplayer.mediacontroller

import android.content.Context
import android.view.View
import android.widget.TextView
import com.st.stplayer.R
import com.st.stplayer_ui.StMediaController

open class FullScreenMediaController(context: Context) : StMediaController(context) {
    private var mBackBtn: View? = null
    private var mTitle: TextView? = null

    override fun getLayoutId(): Int {
        return R.layout.full_screen_media_controller
    }

    override fun initControllerView(view: View) {
        super.initControllerView(view)
        mBackBtn = view.findViewById(R.id.full_screen_back)
        mTitle = view.findViewById(R.id.full_screen_title)
        mBackBtn?.setOnClickListener {
            mOnBackBtnListener?.onBack()
        }
    }

    fun setTitle(title: String?) {
        mTitle?.text = title
    }

    private var mOnBackBtnListener: OnBackBtnListener? = null
    fun setOnBackBtnListener(listener: OnBackBtnListener?) {
        mOnBackBtnListener = listener
    }

    interface OnBackBtnListener {
        fun onBack()
    }
}