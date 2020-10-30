package com.st.stplayer.logiccontroller

import android.content.Context
import android.view.View
import com.st.stplayer.R

class SimpleLogicController(context: Context) : AbstractLogicController(context) {

    override fun onInitControllerView(mRoot: View) {
        super.onInitControllerView(mRoot)
        mControllerPlayerControl?.hideMediaController()
        val mStartBtn = mRoot.findViewById<View>(R.id.mStart)
        mStartBtn?.setOnClickListener {
            if (isShowing()) {
                hide()
            } else {
                show()
            }
        }
    }

    override fun onInit(context: Context) {
        mRoot?.setOnClickListener {
            if (isShowing()) {
                hide()
            } else {
                show()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.logic_controller_simple
    }
}